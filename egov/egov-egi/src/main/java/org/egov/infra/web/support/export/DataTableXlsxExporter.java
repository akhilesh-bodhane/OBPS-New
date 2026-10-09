package org.egov.infra.web.support.export;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Semaphore;
import java.util.function.IntFunction;
import java.util.regex.Pattern;

/**
 * Streams the complete, filtered result set of a server-side DataTable screen as an .xlsx file.
 * <p>
 * Rows are fetched chunk by chunk through the same service call and JSON adapter that feed the on-screen table,
 * so filters, sorting, security and column values are identical to the table. Only a window of rows is kept in memory.
 */
public final class DataTableXlsxExporter {

    public static final int CHUNK_SIZE = 1000;
    public static final int MAX_ROWS = Integer.getInteger("datatable.export.rows.limit", 200000);
    private static final int MAX_CONCURRENT_EXPORTS = Integer.getInteger("datatable.export.concurrency", 3);
    private static final Semaphore PERMITS = new Semaphore(MAX_CONCURRENT_EXPORTS);
    private static final Pattern ISO_DATE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    private DataTableXlsxExporter() {
    }

    /** Column definition: header text shown in Excel and the key of the adapter JSON property. */
    public static final class Column {
        private final String header;
        private final String key;

        public Column(String header, String key) {
            this.header = header;
            this.key = key;
        }
    }

    public static Column column(String header, String key) {
        return new Column(header, key);
    }

    /** Parses the [{"header":..,"key":..}] list posted by datatable-export.js. */
    public static Column[] columns(String json) {
        JsonArray array = JsonParser.parseString(json).getAsJsonArray();
        Column[] columns = new Column[Math.min(array.size(), 100)];
        for (int i = 0; i < columns.length; i++) {
            JsonObject o = array.get(i).getAsJsonObject();
            columns[i] = new Column(o.get("header").getAsString().trim(), o.get("key").getAsString());
        }
        return columns;
    }

    /**
     * @param pageJson returns the DataTable JSON (as produced by the screen's JSON adapter) of the given zero-based chunk
     *                 of {@link #CHUNK_SIZE} rows. The export ends once recordsFiltered rows have been requested.
     */
    public static void export(HttpServletResponse response, String fileName, Column[] columns,
                              IntFunction<String> pageJson) throws IOException {
        if (!PERMITS.tryAcquire()) {
            error(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Too many exports are running. Please try again shortly.");
            return;
        }
        // shared strings (not inline strings) so lightweight spreadsheet viewers can read the file too
        SXSSFWorkbook workbook = new SXSSFWorkbook(null, 100, false, true);
        File spool = null;
        try {
            Sheet sheet = workbook.createSheet("Report");
            CreationHelper helper = workbook.getCreationHelper();
            CellStyle headerStyle = workbook.createCellStyle();
            Font bold = workbook.createFont();
            bold.setBold(true);
            headerStyle.setFont(bold);
            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(helper.createDataFormat().getFormat("dd/mm/yyyy"));

            sheet.createFreezePane(0, 1);
            Row header = sheet.createRow(0);
            for (int c = 0; c < columns.length; c++) {
                Cell cell = header.createCell(c);
                cell.setCellValue(columns[c].header);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            long total = Long.MAX_VALUE;
            for (int i = 0; rowNum <= MAX_ROWS && (long) i * CHUNK_SIZE < total; i++) {
                JsonObject root = JsonParser.parseString(pageJson.apply(i)).getAsJsonObject();
                total = root.has("recordsFiltered") ? root.get("recordsFiltered").getAsLong() : 0;
                JsonArray chunk = root.has("data") ? root.getAsJsonArray("data") : new JsonArray();
                for (JsonElement element : chunk) {
                    if (rowNum > MAX_ROWS)
                        break;
                    JsonObject json = element.getAsJsonObject();
                    Row row = sheet.createRow(rowNum++);
                    for (int c = 0; c < columns.length; c++)
                        writeCell(row.createCell(c), json.get(columns[c].key), dateStyle);
                }
            }

            if (rowNum == 1) {
                error(response, HttpServletResponse.SC_NOT_FOUND, "No records found for the selected criteria.");
                return;
            }
            // POI writes its zip to a non-seekable stream with "version 45" headers and a zero DOS timestamp, which
            // strict OPC readers (e.g. .NET System.IO.Packaging) reject as corrupted. Spool the workbook, then
            // repack its parts with java.util.zip so the download is a conventional zip.
            spool = File.createTempFile("export-", ".xlsx");
            try (OutputStream tmp = new FileOutputStream(spool)) {
                workbook.write(tmp);
            }
            response.setContentType(XLSX);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".xlsx\"");
            repack(spool, response.getOutputStream());
        } finally {
            workbook.dispose();
            workbook.close();
            if (spool != null)
                spool.delete();
            PERMITS.release();
        }
    }

    private static void repack(File source, OutputStream target) throws IOException {
        long now = System.currentTimeMillis();
        try (ZipFile in = new ZipFile(source); ZipOutputStream out = new ZipOutputStream(target)) {
            for (Enumeration<? extends ZipEntry> entries = in.entries(); entries.hasMoreElements();) {
                ZipEntry original = entries.nextElement();
                ZipEntry copy = new ZipEntry(original.getName());
                copy.setTime(now);
                out.putNextEntry(copy);
                try (InputStream is = in.getInputStream(original)) {
                    is.transferTo(out);
                }
                out.closeEntry();
            }
        }
    }

    /** Plain-text error body so the page can show the real reason instead of a generic message. */
    public static void error(HttpServletResponse response, int status, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(message);
    }

    private static void writeCell(Cell cell, JsonElement value, CellStyle dateStyle) {
        if (value == null || value.isJsonNull()) {
            cell.setBlank();
            return;
        }
        if (!value.isJsonPrimitive()) {
            cell.setCellValue(value.toString());
            return;
        }
        if (value.getAsJsonPrimitive().isBoolean()) {
            cell.setCellValue(value.getAsBoolean());
        } else if (value.getAsJsonPrimitive().isNumber()) {
            cell.setCellValue(value.getAsDouble());
        } else {
            String text = value.getAsString();
            if (ISO_DATE.matcher(text).matches()) {
                try {
                    cell.setCellValue(java.sql.Date.valueOf(LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE)));
                    cell.setCellStyle(dateStyle);
                    return;
                } catch (RuntimeException ignored) {
                    // not a real date, fall through to text
                }
            }
            cell.setCellValue(text);
        }
    }
}
