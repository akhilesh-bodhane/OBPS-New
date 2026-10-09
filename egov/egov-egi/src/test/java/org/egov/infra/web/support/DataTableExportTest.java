package org.egov.infra.web.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.infra.web.support.export.DataTableXlsxExporter;
import org.egov.infra.web.support.search.DataTableSearchRequest;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;

public class DataTableExportTest {

    private static DataTableSearchRequest request(int start, int length) {
        DataTableSearchRequest r = new DataTableSearchRequest();
        r.setArgs("{\"start\":" + start + ",\"length\":" + length + ",\"draw\":1}");
        return r;
    }

    @Test
    public void pageSizeAndNumber() {
        assertEquals(2, request(50, 25).pageNumber());
        assertEquals(25, request(50, 25).pageSize());
    }

    @Test
    public void allIsBoundedAndStartsAtFirstPage() {
        DataTableSearchRequest r = request(30, -1);
        assertTrue(r.allRequested());
        assertEquals(0, r.pageNumber());
        assertEquals(DataTableSearchRequest.ALL_ROWS_LIMIT, r.pageSize());
        assertEquals("id", r.orderBy());
    }

    @Test
    public void exportWalksEveryChunkAndWritesTypedCells() throws Exception {
        int total = DataTableXlsxExporter.CHUNK_SIZE * 2 + 5;
        MockHttpServletResponse response = new MockHttpServletResponse();
        DataTableXlsxExporter.export(response, "t", new DataTableXlsxExporter.Column[] {
                DataTableXlsxExporter.column("No", "no"), DataTableXlsxExporter.column("Date", "date") }, chunk -> {
                    StringBuilder rows = new StringBuilder();
                    int from = chunk * DataTableXlsxExporter.CHUNK_SIZE;
                    int to = Math.min(total, from + DataTableXlsxExporter.CHUNK_SIZE);
                    for (int i = from; i < to; i++)
                        rows.append(i > from ? "," : "").append("{\"no\":").append(i).append(",\"date\":\"2024-01-15\"}");
                    return "{\"recordsFiltered\":" + total + ",\"data\":[" + rows + "]}";
                });
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            assertEquals(total, wb.getSheetAt(0).getLastRowNum());
            assertEquals(CellType.NUMERIC, wb.getSheetAt(0).getRow(1).getCell(1).getCellType());
            assertEquals(total - 1, (int) wb.getSheetAt(0).getRow(total).getCell(0).getNumericCellValue());
        }
    }

    /** Strict OPC readers (.NET Packaging) reject zip headers with "version 45" and a zero DOS timestamp. */
    @Test
    public void exportedZipHasConventionalHeaders() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        DataTableXlsxExporter.export(response, "t", new DataTableXlsxExporter.Column[] { DataTableXlsxExporter.column("No", "no") },
                chunk -> "{\"recordsFiltered\":1,\"data\":[{\"no\":1}]}");
        byte[] zip = response.getContentAsByteArray();
        assertEquals(0x50, zip[0]);
        assertEquals("version needed to extract", 20, zip[4]);
        assertTrue("DOS time/date must not be zero", zip[10] != 0 || zip[11] != 0 || zip[12] != 0 || zip[13] != 0);
        try (java.util.zip.ZipInputStream in = new java.util.zip.ZipInputStream(new ByteArrayInputStream(zip))) {
            int entries = 0;
            while (in.getNextEntry() != null)
                entries++;
            assertTrue(entries >= 6);
        }
    }

    @Test
    public void noRecordsGives404() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        DataTableXlsxExporter.export(response, "t",
                new DataTableXlsxExporter.Column[] { DataTableXlsxExporter.column("No", "no") },
                chunk -> "{\"recordsFiltered\":0,\"data\":[]}");
        assertEquals(404, response.getStatus());
    }
}
