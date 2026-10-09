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

package org.egov.edcr.security.oauth2.config;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.egov.edcr.security.oauth2.custom.CustomAuthenticationProvider;
import org.egov.edcr.security.oauth2.entity.SecuredResource;
import org.egov.edcr.security.oauth2.utils.BearerTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Security for the token protected REST APIs and the token endpoint. Replaces the legacy spring-security-oauth2
 * resource/authorization server, which does not run on Spring Security 6.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
public class RestApiSecurityConfiguration {

    public static final String TOKEN_ENDPOINT = "/oauth/token";
    private static final String APIS_CONFIG = "config/restapi-secured-apis-config.json";
    private static final String APIS_CONFIG_OVERRIDE = "config/restapi-secured-apis-config-override.json";

    @Autowired
    private CustomAuthenticationProvider authProvider;

    @Autowired
    private BearerTokenFilter bearerTokenFilter;

    @Bean
    public AuthenticationManager restApiAuthenticationManager() {
        return new ProviderManager(authProvider);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain restApiSecurityFilterChain(final HttpSecurity http) throws Exception {
        final List<org.egov.edcr.security.oauth2.entity.ResourceDetail> records = securedResources().getResources();
        final RequestMatcher[] matchers = new RequestMatcher[records.size() + 1];
        matchers[0] = new AntPathRequestMatcher(TOKEN_ENDPOINT + "/**");
        for (int i = 0; i < records.size(); i++)
            matchers[i + 1] = new AntPathRequestMatcher(records.get(i).getUrl());
        http.securityMatcher(new OrRequestMatcher(matchers)).csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(bearerTokenFilter, BasicAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(new AntPathRequestMatcher(TOKEN_ENDPOINT + "/**")).permitAll();
                    records.forEach(record -> {
                        final RequestMatcher matcher = new AntPathRequestMatcher(record.getUrl());
                        if (StringUtils.isNotEmpty(record.getRoles()))
                            auth.requestMatchers(matcher).access(new WebExpressionAuthorizationManager(record.getRoles()));
                        else
                            auth.requestMatchers(matcher).authenticated();
                    });
                    auth.anyRequest().denyAll();
                });
        return http.build();
    }

    private SecuredResource securedResources() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        Resource res = new ClassPathResource(APIS_CONFIG_OVERRIDE);
        if (!res.exists())
            res = new ClassPathResource(APIS_CONFIG);
        return mapper.readValue(res.getInputStream(), SecuredResource.class);
    }
}
