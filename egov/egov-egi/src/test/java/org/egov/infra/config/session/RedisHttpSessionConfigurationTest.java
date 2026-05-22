package org.egov.infra.config.session;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;

import static org.egov.infra.security.utils.SecurityConstants.SESSION_COOKIE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RedisHttpSessionConfigurationTest {

    private static final String DOMAIN_NAME = "egovernments.org";

    private RedisHttpSessionConfiguration configuration;
    private CookieSerializer cookieSerializer;

    @Before
    public void setUp() {
        configuration = new RedisHttpSessionConfiguration();
        ReflectionTestUtils.setField(configuration, "commonDomainName", DOMAIN_NAME);
        cookieSerializer = configuration.cookieSerializer();
    }

    @Test
    public void shouldWriteSessionCookieWithSameSiteLax() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieSerializer.writeCookieValue(new CookieSerializer.CookieValue(request, response, "session-123"));

        String sessionCookieHeader = response.getHeader("Set-Cookie");
        assertTrue(sessionCookieHeader.contains(SESSION_COOKIE_NAME + "=session-123"));
        assertTrue(sessionCookieHeader.contains("Domain=" + DOMAIN_NAME));
        assertTrue(sessionCookieHeader.contains("Path=/"));
        assertTrue(sessionCookieHeader.contains("Secure"));
        assertTrue(sessionCookieHeader.contains("HttpOnly"));
        assertTrue(sessionCookieHeader.contains("SameSite=Lax"));
    }

    @Test
    public void shouldWriteOnlyOneSameSiteAttributePerCookieHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieSerializer.writeCookieValue(new CookieSerializer.CookieValue(request, response, "session-123"));

        String sessionCookieHeader = response.getHeader("Set-Cookie");
        assertEquals(1, countOccurrences(sessionCookieHeader, "SameSite="));
    }

    @Test
    public void shouldPreserveUnrelatedSetCookieHeaders() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.addHeader("Set-Cookie", "OTHER=1; Path=/");

        cookieSerializer.writeCookieValue(new CookieSerializer.CookieValue(request, response, "session-123"));

        Collection<String> headers = response.getHeaders("Set-Cookie");
        assertEquals(2, headers.size());
        assertTrue(headers.contains("OTHER=1; Path=/"));
        assertTrue(headers.stream().anyMatch(header ->
                header.contains(SESSION_COOKIE_NAME + "=session-123") && header.contains("SameSite=Lax")));
    }

    private int countOccurrences(String value, String token) {
        int count = 0;
        int index = 0;
        while ((index = value.indexOf(token, index)) != -1) {
            count++;
            index += token.length();
        }
        return count;
    }
}
