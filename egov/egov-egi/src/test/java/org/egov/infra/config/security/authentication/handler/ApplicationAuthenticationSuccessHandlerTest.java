package org.egov.infra.config.security.authentication.handler;

import org.egov.infra.security.util.RequestIdentityResolver;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

import static org.egov.infra.security.utils.SecurityConstants.LOGIN_IP;
import static org.egov.infra.security.utils.SecurityConstants.LOGIN_TIME;
import static org.egov.infra.security.utils.SecurityConstants.LOGIN_USER_AGENT;
import static org.egov.infra.security.utils.SecurityConstants.SESSION_USER_AGENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ApplicationAuthenticationSuccessHandlerTest {

    @Test
    public void shouldStoreServerDerivedSessionBindingOnAuthenticationSuccess() throws Exception {
        ApplicationAuthenticationSuccessHandler handler = new ApplicationAuthenticationSuccessHandler();
        handler.setRequestIdentityResolver(new RequestIdentityResolver());
        handler.setDefaultTargetUrl("/home");

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/egi");
        request.setRemoteAddr("10.0.0.10");
        request.addHeader("X-Forwarded-For", "203.0.113.8, 10.0.0.10");
        request.addHeader("User-Agent", "Mozilla/5.0");
        request.getSession();

        MockHttpServletResponse response = new MockHttpServletResponse();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                "user",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );

        handler.onAuthenticationSuccess(request, response, authentication);

        assertNotNull(request.getSession(false).getAttribute(LOGIN_TIME));
        assertEquals("203.0.113.8", request.getSession(false).getAttribute(LOGIN_IP));
        assertEquals("Mozilla/5.0", request.getSession(false).getAttribute(LOGIN_USER_AGENT));
        assertEquals("Mozilla/5.0", request.getSession(false).getAttribute(SESSION_USER_AGENT));
    }
}
