package org.egov.infra.security.util;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.Assert.assertEquals;

public class RequestIdentityResolverTest {

    private final RequestIdentityResolver requestIdentityResolver = new RequestIdentityResolver();

    @Test
    public void shouldResolveTrimmedUserAgent() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("User-Agent", "  Mozilla/5.0  ");

        assertEquals("Mozilla/5.0", requestIdentityResolver.resolveUserAgent(request));
    }

    @Test
    public void shouldResolveForwardedClientIpFromFirstForwardedAddress() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.10");
        request.addHeader("X-Forwarded-For", "203.0.113.8, 10.0.0.10");

        assertEquals("203.0.113.8", requestIdentityResolver.resolveClientIp(request));
    }

    @Test
    public void shouldFallBackToRemoteAddressWhenForwardedHeaderMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.10");

        assertEquals("10.0.0.10", requestIdentityResolver.resolveClientIp(request));
    }
}
