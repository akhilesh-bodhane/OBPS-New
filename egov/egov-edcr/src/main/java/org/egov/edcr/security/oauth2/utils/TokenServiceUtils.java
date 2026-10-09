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

package org.egov.edcr.security.oauth2.utils;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Redis backed store for the opaque access and refresh tokens issued by the REST API token endpoint.
 */
@Service
public class TokenServiceUtils {

    private static final String SOURCE = "source";
    private static final String ACCESS_PREFIX = "edcr:oauth:access:";
    private static final String REFRESH_PREFIX = "edcr:oauth:refresh:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public TokenServiceUtils(final RedisConnectionFactory redisConnectionFactory) {
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
    }

    public String newToken() {
        return UUID.randomUUID().toString();
    }

    public void storeAccessToken(final String token, final TokenRecord record, final int validitySeconds) {
        redisTemplate.opsForValue().set(ACCESS_PREFIX + token, record, Duration.ofSeconds(validitySeconds));
    }

    public void storeRefreshToken(final String token, final TokenRecord record, final int validitySeconds) {
        redisTemplate.opsForValue().set(REFRESH_PREFIX + token, record, Duration.ofSeconds(validitySeconds));
    }

    public TokenRecord readAccessToken(final String token) {
        return (TokenRecord) redisTemplate.opsForValue().get(ACCESS_PREFIX + token);
    }

    public TokenRecord readRefreshToken(final String token) {
        return (TokenRecord) redisTemplate.opsForValue().get(REFRESH_PREFIX + token);
    }

    public void removeAccessToken(final String token) {
        redisTemplate.delete(ACCESS_PREFIX + token);
    }

    public void removeRefreshToken(final String token) {
        redisTemplate.delete(REFRESH_PREFIX + token);
    }

    public Object getSource(final String accessToken) {
        final TokenRecord record = readAccessToken(accessToken);
        return record == null ? null : record.getAdditionalInformation().get(SOURCE);
    }

    public static List<String> authorityNames(final Collection<? extends GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
}
