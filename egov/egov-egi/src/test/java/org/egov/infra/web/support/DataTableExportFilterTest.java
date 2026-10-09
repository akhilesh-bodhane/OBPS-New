package org.egov.infra.web.support;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.egov.infra.web.filter.DataTableExportFilter;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class DataTableExportFilterTest {

    private static final int TOTAL = 2345;

    /** Stands in for the screen's controller: pages TOTAL rows according to the args it receives. */
    private static MockFilterChain fakeController() {
        return new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res)
                    throws java.io.IOException {
                JsonObject args = JsonParser.parseString(req.getParameter("args")).getAsJsonObject();
                int start = args.get("start").getAsInt(), length = args.get("length").getAsInt();
                StringBuilder rows = new StringBuilder();
                for (int i = start; i < Math.min(TOTAL, start + length); i++)
                    rows.append(i > start ? "," : "").append("{\"id\":").append(i).append(",\"name\":\"n").append(i).append("\"}");
                res.getWriter().write("{\"recordsFiltered\":" + TOTAL + ",\"data\":[" + rows + "]}");
            }
        };
    }

    @Test
    public void replaysEveryPageIntoOneWorkbook() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/bpa/application/search");
        request.setParameter("args", "{\"start\":20,\"length\":10,\"draw\":3}");
        request.setParameter("statusId", "5");
        request.setParameter("exportColumns", "[{\"header\":\"Id\",\"key\":\"id\"},{\"header\":\"Name\",\"key\":\"name\"}]");
        request.setParameter("exportFileName", "my report");
        MockHttpServletResponse response = new MockHttpServletResponse();

        new DataTableExportFilter().doFilter(request, response, fakeController());

        assertEquals(200, response.getStatus());
        assertEquals("attachment; filename=\"my_report.xlsx\"", response.getHeader("Content-Disposition"));
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()))) {
            assertEquals(TOTAL, wb.getSheetAt(0).getLastRowNum());
            assertEquals(0, (int) wb.getSheetAt(0).getRow(1).getCell(0).getNumericCellValue());
            assertEquals("n" + (TOTAL - 1), wb.getSheetAt(0).getRow(TOTAL).getCell(1).getStringCellValue());
        }
    }

    @Test
    public void ordinaryRequestsPassThrough() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/bpa/application/search");
        request.setParameter("args", "{\"start\":20,\"length\":10,\"draw\":3}");
        MockHttpServletResponse response = new MockHttpServletResponse();
        new DataTableExportFilter().doFilter(request, response, fakeController());
        assertEquals(true, response.getContentAsString().startsWith("{\"recordsFiltered\":2345"));
    }
}
