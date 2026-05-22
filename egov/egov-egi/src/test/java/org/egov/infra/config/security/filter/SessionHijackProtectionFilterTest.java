package org.egov.infra.config.security.filter;

import org.egov.infra.config.security.authentication.handler.ApplicationLogoutHandler;
import org.egov.infra.security.util.RequestIdentityResolver;
import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.egov.infra.security.utils.SecurityConstants.SESSION_USER_AGENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SessionHijackProtectionFilterTest {

    private final SessionHijackProtectionFilter filter = new SessionHijackProtectionFilter(
            new RequestIdentityResolver(),
            new ApplicationLogoutHandler()
    );

    @After
    public void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void shouldAllowAuthenticatedRequestWhenUserAgentMatches() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(SESSION_USER_AGENT, "Mozilla/5.0");
        request.addHeader("User-Agent", "Mozilla/5.0");
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser());

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertNull(response.getRedirectedUrl());
    }

    @Test
    public void shouldInvalidateSessionAndRedirectWhenUserAgentDoesNotMatch() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setContextPath("/egi");
        request.getSession().setAttribute(SESSION_USER_AGENT, "Browser-A");
        request.addHeader("User-Agent", "Browser-B");
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser());

        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertEquals("/egi/login/secure?sessionSecurity=true", response.getRedirectedUrl());
        assertNull(request.getSession(false));
    }

    @Test
    public void shouldAllowAnonymousRequestWithoutSessionValidation() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession().setAttribute(SESSION_USER_AGENT, "Browser-A");
        request.addHeader("User-Agent", "Browser-B");
        SecurityContextHolder.getContext().setAuthentication(new AnonymousAuthenticationToken(
                "anonymous",
                "anonymousUser",
                AuthorityUtils.createAuthorityList("PUBLIC")
        ));

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertNull(response.getRedirectedUrl());
    }

    @Test
    public void shouldAllowAuthenticatedRequestWhenSessionBindingIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.getSession();
        request.addHeader("User-Agent", "Browser-B");
        SecurityContextHolder.getContext().setAuthentication(authenticatedUser());

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain filterChain = new MockFilterChain();

        filter.doFilter(request, response, filterChain);

        assertNull(response.getRedirectedUrl());
    }

    private UsernamePasswordAuthenticationToken authenticatedUser() {
        return new UsernamePasswordAuthenticationToken(
                "user",
                "password",
                AuthorityUtils.createAuthorityList("ROLE_USER")
        );
    }
}
