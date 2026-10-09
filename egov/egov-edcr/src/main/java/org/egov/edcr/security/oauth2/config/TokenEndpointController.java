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
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.egov.edcr.security.oauth2.entity.ClientDetail;
import org.egov.edcr.security.oauth2.entity.SecuredClient;
import org.egov.edcr.security.oauth2.utils.TokenRecord;
import org.egov.edcr.security.oauth2.utils.TokenServiceUtils;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.rest.support.CustomTokenEnhancer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * OAuth2 style token endpoint supporting the password and refresh_token grants for the configured REST API clients.
 */
@RestController
public class TokenEndpointController {

    private static final String CLIENTS_CONFIG = "config/restapi-secured-clients-config.json";
    private static final String CLIENTS_CONFIG_OVERRIDE = "config/restapi-secured-clients-config-override.json";

    @Autowired
    @Qualifier("restApiAuthenticationManager")
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenServiceUtils tokenServiceUtils;

    @Autowired
    private CustomTokenEnhancer customTokenEnhancer;

    @PostMapping(value = RestApiSecurityConfiguration.TOKEN_ENDPOINT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> token(
            @RequestHeader(value = "Authorization", required = false) final String authorization,
            @RequestParam("grant_type") final String grantType,
            @RequestParam(value = "username", required = false) final String username,
            @RequestParam(value = "password", required = false) final String password,
            @RequestParam(value = "refresh_token", required = false) final String refreshToken,
            @RequestParam(value = "client_id", required = false) final String clientIdParam,
            @RequestParam(value = "client_secret", required = false) final String clientSecretParam) throws IOException {
        final ClientDetail client = authenticateClient(authorization, clientIdParam, clientSecretParam);
        if (client == null)
            return error(HttpStatus.UNAUTHORIZED, "invalid_client", "Bad client credentials");
        try {
            if ("password".equals(grantType))
                return issue(client, passwordGrant(username, password));
            if ("refresh_token".equals(grantType))
                return refresh(client, refreshToken);
        } catch (AuthenticationException e) {
            return error(HttpStatus.BAD_REQUEST, "invalid_grant", "Invalid login credentials");
        }
        return error(HttpStatus.BAD_REQUEST, "unsupported_grant_type", "Unsupported grant type: " + grantType);
    }

    private Authentication passwordGrant(final String username, final String password) {
        if (username == null || password == null)
            throw new AuthenticationServiceException("Missing username or password");
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private ResponseEntity<Map<String, Object>> refresh(final ClientDetail client, final String refreshToken) {
        final TokenRecord previous = refreshToken == null ? null : tokenServiceUtils.readRefreshToken(refreshToken);
        if (previous == null || !client.getClientId().equals(previous.getClientId()))
            return error(HttpStatus.BAD_REQUEST, "invalid_grant", "Invalid refresh token");
        tokenServiceUtils.removeRefreshToken(refreshToken);
        return respond(client, previous.getUsername(), previous.getAuthorities(), previous.getAdditionalInformation());
    }

    private ResponseEntity<Map<String, Object>> issue(final ClientDetail client, final Authentication authentication) {
        return respond(client, authentication.getName(), TokenServiceUtils.authorityNames(authentication.getAuthorities()),
                new HashMap<>(customTokenEnhancer.additionalInformation(authentication)));
    }

    private ResponseEntity<Map<String, Object>> respond(final ClientDetail client, final String username,
            final java.util.List<String> authorities, final Map<String, Object> additionalInformation) {
        final int accessValidity = client.getAccessTokenValidity() * 60;
        final int refreshValidity = client.getRefreshTokenValidity() * 60;
        final String accessToken = tokenServiceUtils.newToken();
        final String newRefreshToken = tokenServiceUtils.newToken();

        final TokenRecord record = new TokenRecord();
        record.setClientId(client.getClientId());
        record.setUsername(username);
        record.setAuthorities(authorities);
        record.setRefreshToken(newRefreshToken);
        record.setAdditionalInformation(additionalInformation);
        tokenServiceUtils.storeAccessToken(accessToken, record, accessValidity);
        tokenServiceUtils.storeRefreshToken(newRefreshToken, record, refreshValidity);

        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("access_token", accessToken);
        body.put("token_type", "bearer");
        body.put("refresh_token", newRefreshToken);
        body.put("expires_in", accessValidity);
        body.put("scope", "read write");
        body.putAll(additionalInformation);
        return ResponseEntity.ok().header("Cache-Control", "no-store").header("Pragma", "no-cache").body(body);
    }

    private ResponseEntity<Map<String, Object>> error(final HttpStatus status, final String error, final String description) {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("error", error);
        body.put("error_description", description);
        return ResponseEntity.status(status).body(body);
    }

    private ClientDetail authenticateClient(final String authorization, final String clientIdParam,
            final String clientSecretParam) throws IOException {
        String clientId = clientIdParam;
        String clientSecret = clientSecretParam;
        if (authorization != null && authorization.regionMatches(true, 0, "Basic ", 0, 6)) {
            final String[] parts = new String(Base64.getDecoder().decode(authorization.substring(6).trim()),
                    StandardCharsets.UTF_8).split(":", 2);
            if (parts.length == 2) {
                clientId = parts[0];
                clientSecret = parts[1];
            }
        }
        if (clientId == null || clientSecret == null)
            return null;
        final String id = clientId;
        final String secret = clientSecret;
        final Optional<ClientDetail> client = securedClients().getClients().stream()
                .filter(c -> c.getClientId().equals(id) && c.getClientSecret().equals(secret)).findFirst();
        return client.orElse(null);
    }

    private SecuredClient securedClients() throws IOException {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        Resource res = new ClassPathResource(CLIENTS_CONFIG_OVERRIDE);
        if (!res.exists())
            res = new ClassPathResource(CLIENTS_CONFIG);
        try {
            return mapper.readValue(res.getInputStream(), SecuredClient.class);
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Could not read REST API clients configuration", e);
        }
    }
}
