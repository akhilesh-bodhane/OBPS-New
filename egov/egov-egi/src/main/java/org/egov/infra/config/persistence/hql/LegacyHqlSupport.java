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

package org.egov.infra.config.persistence.hql;

import java.lang.reflect.Field;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.query.hql.HqlTranslator;
import org.hibernate.query.spi.QueryEngine;
import org.hibernate.query.sqm.tree.SqmStatement;

/**
 * Hibernate 6 no longer accepts JDBC-style positional parameters ({@code ?}) in HQL, which this code base uses
 * extensively (including in hbm.xml named queries). This installs a translator wrapper that rewrites every unnamed
 * {@code ?} to the JPA style ordinal {@code ?1, ?2, ...} before the query is parsed. Parameters are then bound with
 * 1-based indexes.
 */
public final class LegacyHqlSupport {

    private LegacyHqlSupport() {
    }

    public static void install(final SessionFactoryImplementor sessionFactory) {
        try {
            final QueryEngine queryEngine = sessionFactory.getQueryEngine();
            final Field field = findHqlTranslatorField(queryEngine.getClass());
            field.setAccessible(true);
            final HqlTranslator original = (HqlTranslator) field.get(queryEngine);
            if (!(original instanceof PositionalParameterTranslator))
                field.set(queryEngine, new PositionalParameterTranslator(original));
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Could not install legacy positional HQL parameter support", e);
        }
    }

    private static Field findHqlTranslatorField(final Class<?> type) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField("hqlTranslator");
            } catch (NoSuchFieldException e) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException("hqlTranslator");
    }

    /**
     * Rewrites unnamed positional parameters outside of string literals to {@code ?1..?n}.
     */
    public static String rewritePositionalParameters(final String hql) {
        if (hql == null || hql.indexOf('?') < 0)
            return hql;
        final StringBuilder result = new StringBuilder(hql.length() + 8);
        boolean inQuote = false;
        int ordinal = 0;
        for (int i = 0; i < hql.length(); i++) {
            final char c = hql.charAt(i);
            if (c == '\'')
                inQuote = !inQuote;
            result.append(c);
            if (c == '?' && !inQuote) {
                final boolean explicit = i + 1 < hql.length() && Character.isDigit(hql.charAt(i + 1));
                if (!explicit)
                    result.append(++ordinal);
            }
        }
        return result.toString();
    }

    private static final class PositionalParameterTranslator implements HqlTranslator {

        private final HqlTranslator delegate;

        private PositionalParameterTranslator(final HqlTranslator delegate) {
            this.delegate = delegate;
        }

        @Override
        public <R> SqmStatement<R> translate(final String hql, final Class<R> expectedResultType) {
            return delegate.translate(rewritePositionalParameters(hql), expectedResultType);
        }
    }
}
