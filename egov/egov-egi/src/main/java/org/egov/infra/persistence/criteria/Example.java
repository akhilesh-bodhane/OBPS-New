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

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Date;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;

/**
 * Query by example: equality on every non-null simple (non-association) property of the example bean.
 */
public final class Example implements Criterion {

    private final Object example;

    private Example(final Object example) {
        this.example = example;
    }

    public static Example create(final Object example) {
        return new Example(example);
    }

    @Override
    public Predicate toPredicate(final CriteriaBuilder cb, final PathResolver resolver) {
        final Conjunction conjunction = new Conjunction();
        try {
            for (final PropertyDescriptor pd : Introspector.getBeanInfo(example.getClass()).getPropertyDescriptors()) {
                if (pd.getReadMethod() == null || pd.getWriteMethod() == null || "class".equals(pd.getName()))
                    continue;
                final Class<?> type = pd.getPropertyType();
                final boolean simple = type.isPrimitive() || type.isEnum() || type == String.class
                        || Number.class.isAssignableFrom(type) || Boolean.class == type || Date.class.isAssignableFrom(type);
                if (!simple || Collection.class.isAssignableFrom(type))
                    continue;
                final Object value = pd.getReadMethod().invoke(example);
                if (value != null && !"id".equals(pd.getName()))
                    conjunction.add(Restrictions.eq(pd.getName(), value));
            }
        } catch (IntrospectionException | ReflectiveOperationException e) {
            throw new IllegalStateException("Could not build example criterion", e);
        }
        return conjunction.toPredicate(cb, resolver);
    }
}
