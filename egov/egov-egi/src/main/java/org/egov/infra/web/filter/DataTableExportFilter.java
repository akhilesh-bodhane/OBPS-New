package org.egov.infra.web.filter;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.persistence.EntityManagerFactory;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.egov.infra.web.support.export.DataTableXlsxExporter;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Turns any server-side DataTable search endpoint into an Excel export without per-screen code.
 * <p>
 * The page posts the table's last request again with an extra {@code exportColumns} parameter. This filter then replays
 * that very request against the same controller, chunk by chunk (by rewriting the DataTables {@code args} paging),
 * captures each JSON response and streams the rows into an .xlsx. Because the real controller/service is invoked, all
 * filters, sorting, tenant and user restrictions are exactly those of the table. Requests without
 * {@code exportColumns} pass through untouched.
 * <p>
 * Must be mapped after the security, tenant and XSS filters.
 */
public class DataTableExportFilter extends OncePerRequestFilter {

    private static final String EXPORT_COLUMNS = "exportColumns";
    private static final String EXPORT_FILE_NAME = "exportFileName";
    private static final String ARGS = "args";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String columns = "POST".equalsIgnoreCase(request.getMethod()) ? request.getParameter(EXPORT_COLUMNS) : null;
        String args = columns == null ? null : request.getParameter(ARGS);
        if (columns == null || args == null) {
            chain.doFilter(request, response);
            return;
        }

        String fileName = request.getParameter(EXPORT_FILE_NAME);
        fileName = fileName == null || fileName.isBlank() ? "export" : fileName.replaceAll("[^\\w-]", "_");
        JsonObject template = JsonParser.parseString(args).getAsJsonObject();
        Map<String, String[]> baseParams = new HashMap<>(request.getParameterMap());
        baseParams.remove(EXPORT_COLUMNS);
        baseParams.remove(EXPORT_FILE_NAME);

        try {
            DataTableXlsxExporter.export(response, fileName, DataTableXlsxExporter.columns(columns), chunk -> {
                JsonObject paging = template.deepCopy();
                paging.addProperty("start", chunk * DataTableXlsxExporter.CHUNK_SIZE);
                paging.addProperty("length", DataTableXlsxExporter.CHUNK_SIZE);
                Map<String, String[]> params = new HashMap<>(baseParams);
                params.put(ARGS, new String[] { paging.toString() });
                CapturingResponse captured = new CapturingResponse(response);
                try {
                    chain.doFilter(new ParamRequest(request, params), captured);
                } catch (IOException | ServletException e) {
                    throw new ExportFailure(e);
                }
                if (captured.status >= 300)
                    throw new ExportFailure("the search behind this table failed (HTTP " + captured.status
                            + "). Check the server log for the underlying error");
                clearPersistenceContext(request);
                return captured.body();
            });
        } catch (RuntimeException e) {
            logger.error("Excel export failed for " + request.getRequestURI(), e);
            if (!response.isCommitted()) {
                response.reset();
                DataTableXlsxExporter.error(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Unable to export the records: " + (e instanceof ExportFailure ? e.getMessage()
                                : "the server could not read the search results. Please contact support."));
            }
        }
    }

    /** The shared OpenEntityManagerInView session would otherwise keep every exported entity until the request ends. */
    private void clearPersistenceContext(HttpServletRequest request) {
        try {
            EntityManagerFactory emf = WebApplicationContextUtils.getRequiredWebApplicationContext(
                    request.getServletContext()).getBean(EntityManagerFactory.class);
            EntityManagerHolder holder = (EntityManagerHolder) TransactionSynchronizationManager.getResource(emf);
            if (holder != null)
                holder.getEntityManager().clear();
        } catch (RuntimeException e) {
            logger.debug("Could not clear persistence context between export chunks", e);
        }
    }

    private static final class ExportFailure extends RuntimeException {
        ExportFailure(Throwable cause) {
            super(cause);
        }

        ExportFailure(String message) {
            super(message);
        }
    }

    private static final class ParamRequest extends HttpServletRequestWrapper {
        private final Map<String, String[]> params;

        ParamRequest(HttpServletRequest request, Map<String, String[]> params) {
            super(request);
            this.params = params;
        }

        @Override
        public String getParameter(String name) {
            String[] values = params.get(name);
            return values == null || values.length == 0 ? null : values[0];
        }

        @Override
        public Map<String, String[]> getParameterMap() {
            return Collections.unmodifiableMap(params);
        }

        @Override
        public Enumeration<String> getParameterNames() {
            return Collections.enumeration(params.keySet());
        }

        @Override
        public String[] getParameterValues(String name) {
            return params.get(name);
        }
    }

    /** Buffers the controller output so it can be parsed instead of being sent to the browser. */
    private static final class CapturingResponse extends HttpServletResponseWrapper {
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private PrintWriter writer;
        private int status = SC_OK;

        CapturingResponse(HttpServletResponse response) {
            super(response);
        }

        String body() {
            if (writer != null)
                writer.flush();
            return new String(buffer.toByteArray(), StandardCharsets.UTF_8);
        }

        @Override
        public ServletOutputStream getOutputStream() {
            return new ServletOutputStream() {
                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setWriteListener(WriteListener listener) {
                    // synchronous only
                }

                @Override
                public void write(int b) {
                    buffer.write(b);
                }
            };
        }

        @Override
        public PrintWriter getWriter() {
            if (writer == null)
                writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8));
            return writer;
        }

        @Override
        public void setStatus(int sc) {
            status = sc;
        }

        @Override
        public void sendError(int sc) {
            status = sc;
        }

        @Override
        public void sendError(int sc, String msg) {
            status = sc;
        }

        @Override
        public void sendRedirect(String location) {
            status = SC_FOUND;
        }

        @Override
        public void setContentType(String type) {
            // keep the real response untouched until the workbook is written
        }

        @Override
        public void setCharacterEncoding(String charset) {
            // keep the real response untouched until the workbook is written
        }

        @Override
        public void setContentLength(int len) {
            // ignored
        }

        @Override
        public void setContentLengthLong(long len) {
            // ignored
        }

        @Override
        public void setHeader(String name, String value) {
            // ignored
        }

        @Override
        public void addHeader(String name, String value) {
            // ignored
        }

        @Override
        public void flushBuffer() {
            // never commit the real response from inside a chunk
        }

        @Override
        public boolean isCommitted() {
            return false;
        }
    }
}
