/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */

package org.egov.infra.config.security.filter;

import org.apache.commons.lang3.StringUtils;
import org.egov.infra.config.security.authentication.handler.ApplicationLogoutHandler;
import org.egov.infra.security.util.RequestIdentityResolver;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.egov.infra.security.utils.SecurityConstants.SESSION_USER_AGENT;

public class SessionHijackProtectionFilter extends GenericFilterBean {

    private static final String SESSION_SECURITY_REDIRECT = "/login/secure?sessionSecurity=true";

    private final RequestIdentityResolver requestIdentityResolver;
    private final ApplicationLogoutHandler applicationLogoutHandler;

    public SessionHijackProtectionFilter(RequestIdentityResolver requestIdentityResolver,
                                         ApplicationLogoutHandler applicationLogoutHandler) {
        this.requestIdentityResolver = requestIdentityResolver;
        this.applicationLogoutHandler = applicationLogoutHandler;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!shouldValidateSession(authentication, session)
                || isMatchingUserAgent(session, httpRequest)) {
            chain.doFilter(request, response);
            return;
        }

        applicationLogoutHandler.logout(httpRequest, httpResponse, authentication);
        session.invalidate();
        SecurityContextHolder.clearContext();
        httpResponse.sendRedirect(httpRequest.getContextPath() + SESSION_SECURITY_REDIRECT);
    }

    private boolean shouldValidateSession(Authentication authentication, HttpSession session) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && session != null
                && session.getAttribute(SESSION_USER_AGENT) != null;
    }

    private boolean isMatchingUserAgent(HttpSession session, HttpServletRequest request) {
        String sessionUserAgent = (String) session.getAttribute(SESSION_USER_AGENT);
        String currentUserAgent = requestIdentityResolver.resolveUserAgent(request);
        return StringUtils.equals(sessionUserAgent, currentUserAgent);
    }
}
