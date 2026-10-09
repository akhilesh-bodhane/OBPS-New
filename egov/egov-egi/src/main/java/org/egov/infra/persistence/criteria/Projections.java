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

package org.egov.infra.persistence.criteria;

import java.util.Collections;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Selection;

/**
 * Compatibility replacement for the legacy org.hibernate.criterion.Projections (removed in Hibernate 6).
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class Projections {

    private Projections() {
    }

    private interface SelectionFactory {
        Selection<?> create(CriteriaBuilder cb, PathResolver resolver);
    }

    private static Projection of(final SelectionFactory factory, final String groupProperty) {
        return new Projection() {
            @Override
            public List<Selection<?>> selections(final CriteriaBuilder cb, final PathResolver resolver) {
                return Collections.singletonList(factory.create(cb, resolver));
            }

            @Override
            public List<Expression<?>> groupings(final PathResolver resolver) {
                return groupProperty != null ? Collections.singletonList(resolver.path(groupProperty))
                        : super.groupings(resolver);
            }
        };
    }

    public static ProjectionList projectionList() {
        return new ProjectionList();
    }

    public static Projection property(final String property) {
        return of((cb, r) -> r.path(property), null);
    }

    public static Projection id() {
        return of((cb, r) -> r.root().get(r.idName()), null);
    }

    public static Projection groupProperty(final String property) {
        return of((cb, r) -> r.path(property), property);
    }

    public static Projection rowCount() {
        return of((cb, r) -> cb.count(r.root()), null);
    }

    public static Projection count(final String property) {
        return of((cb, r) -> cb.count(r.path(property)), null);
    }

    public static Projection countDistinct(final String property) {
        return of((cb, r) -> cb.countDistinct(r.path(property)), null);
    }

    public static Projection sum(final String property) {
        return of((cb, r) -> cb.sum((Expression<Number>) r.path(property)), null);
    }

    public static Projection max(final String property) {
        return of((cb, r) -> cb.max((Expression<Number>) r.path(property)), null);
    }

    public static Projection min(final String property) {
        return of((cb, r) -> cb.min((Expression<Number>) r.path(property)), null);
    }

    public static Projection avg(final String property) {
        return of((cb, r) -> cb.avg((Expression<Number>) r.path(property)), null);
    }

    public static Projection distinct(final Projection projection) {
        return new Projection() {
            @Override
            public List<Selection<?>> selections(final CriteriaBuilder cb, final PathResolver resolver) {
                return projection.selections(cb, resolver);
            }

            @Override
            public boolean isDistinct() {
                return true;
            }

            @Override
            public List<String> aliases() {
                return projection.aliases();
            }
        };
    }
}
