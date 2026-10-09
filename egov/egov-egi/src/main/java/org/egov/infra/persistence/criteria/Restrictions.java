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

import java.util.Collection;

import jakarta.persistence.criteria.Expression;

/**
 * Compatibility replacement for the legacy org.hibernate.criterion.Restrictions (removed in Hibernate 6).
 * Translates to the JPA Criteria API.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class Restrictions {

    private Restrictions() {
    }

    public static SimpleExpression eq(final String property, final Object value) {
        return new SimpleExpression(property, value);
    }

    public static Criterion ne(final String property, final Object value) {
        return (cb, r) -> value == null ? cb.isNotNull(r.path(property)) : cb.notEqual(r.path(property), value);
    }

    public static Criterion gt(final String property, final Object value) {
        return (cb, r) -> cb.greaterThan((Expression<Comparable>) r.path(property), (Comparable) value);
    }

    public static Criterion ge(final String property, final Object value) {
        return (cb, r) -> cb.greaterThanOrEqualTo((Expression<Comparable>) r.path(property), (Comparable) value);
    }

    public static Criterion lt(final String property, final Object value) {
        return (cb, r) -> cb.lessThan((Expression<Comparable>) r.path(property), (Comparable) value);
    }

    public static Criterion le(final String property, final Object value) {
        return (cb, r) -> cb.lessThanOrEqualTo((Expression<Comparable>) r.path(property), (Comparable) value);
    }

    public static Criterion between(final String property, final Object lo, final Object hi) {
        return (cb, r) -> cb.between((Expression<Comparable>) r.path(property), (Comparable) lo, (Comparable) hi);
    }

    public static Criterion like(final String property, final Object value) {
        return (cb, r) -> cb.like((Expression<String>) r.path(property), String.valueOf(value));
    }

    public static Criterion like(final String property, final String value, final MatchMode matchMode) {
        return (cb, r) -> cb.like((Expression<String>) r.path(property), matchMode.toMatchString(value));
    }

    public static Criterion ilike(final String property, final Object value) {
        return ilike(property, String.valueOf(value), MatchMode.EXACT);
    }

    public static Criterion ilike(final String property, final String value, final MatchMode matchMode) {
        return (cb, r) -> cb.like(cb.lower((Expression<String>) r.path(property)),
                matchMode.toMatchString(value == null ? "" : value.toLowerCase()));
    }

    public static Criterion in(final String property, final Collection values) {
        return (cb, r) -> values == null || values.isEmpty() ? cb.disjunction() : r.path(property).in(values);
    }

    public static Criterion in(final String property, final Object... values) {
        return values == null || values.length == 0 ? (cb, r) -> cb.disjunction()
                : (cb, r) -> r.path(property).in(values);
    }

    public static Criterion isNull(final String property) {
        return (cb, r) -> cb.isNull(r.path(property));
    }

    public static Criterion isNotNull(final String property) {
        return (cb, r) -> cb.isNotNull(r.path(property));
    }

    public static Criterion idEq(final Object value) {
        return (cb, r) -> cb.equal(r.root().get(r.idName()), value);
    }

    public static Criterion eqProperty(final String property, final String other) {
        return (cb, r) -> cb.equal(r.path(property), r.path(other));
    }

    public static Criterion neProperty(final String property, final String other) {
        return (cb, r) -> cb.notEqual(r.path(property), r.path(other));
    }

    public static Criterion ltProperty(final String property, final String other) {
        return (cb, r) -> cb.lessThan((Expression<Comparable>) r.path(property), (Expression<Comparable>) r.path(other));
    }

    public static Criterion leProperty(final String property, final String other) {
        return (cb, r) -> cb.lessThanOrEqualTo((Expression<Comparable>) r.path(property),
                (Expression<Comparable>) r.path(other));
    }

    public static Criterion gtProperty(final String property, final String other) {
        return (cb, r) -> cb.greaterThan((Expression<Comparable>) r.path(property), (Expression<Comparable>) r.path(other));
    }

    public static Criterion geProperty(final String property, final String other) {
        return (cb, r) -> cb.greaterThanOrEqualTo((Expression<Comparable>) r.path(property),
                (Expression<Comparable>) r.path(other));
    }

    public static LogicalExpression and(final Criterion left, final Criterion right) {
        return new LogicalExpression(left, right, true);
    }

    public static Criterion and(final Criterion... criteria) {
        final Conjunction conjunction = new Conjunction();
        for (final Criterion c : criteria)
            conjunction.add(c);
        return conjunction;
    }

    public static LogicalExpression or(final Criterion left, final Criterion right) {
        return new LogicalExpression(left, right, false);
    }

    public static Criterion or(final Criterion... criteria) {
        final Disjunction disjunction = new Disjunction();
        for (final Criterion c : criteria)
            disjunction.add(c);
        return disjunction;
    }

    public static Criterion not(final Criterion criterion) {
        return (cb, r) -> cb.not(criterion.toPredicate(cb, r));
    }

    public static Conjunction conjunction() {
        return new Conjunction();
    }

    public static Disjunction disjunction() {
        return new Disjunction();
    }
}
