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

package org.egov.infra.web.context;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

import jakarta.servlet.ServletContext;

/**
 * Spring 6 removed the {@code locatorFactorySelector}/{@code parentContextKey} support of {@code ContextLoader}, which
 * the web modules used to share one parent ("erp") application context with the infrastructure and service beans.
 * This listener recreates that behaviour: the first web module to start builds the parent context, the others reuse
 * it (the EAR library classes, and therefore this static field, are shared between the WARs).
 */
public class SharedParentContextLoaderListener extends ContextLoaderListener {

    private static final String[] PARENT_CONFIG = {
            "classpath*:config/spring/applicationContext-security.xml",
            "classpath*:config/spring/applicationContext-*-services.xml" };

    private static ApplicationContext parentContext;

    @Override
    protected ApplicationContext loadParentContext(final ServletContext servletContext) {
        synchronized (SharedParentContextLoaderListener.class) {
            if (parentContext == null) {
                // web aware, so that servlet context dependent beans (Spring Session listener adapters) work
                final XmlWebApplicationContext context = new XmlWebApplicationContext() {
                    @Override
                    protected org.springframework.core.io.Resource getResourceByPath(final String path) {
                        // unprefixed locations are classpath resources, as with the former ClassPathXmlApplicationContext
                        return new org.springframework.core.io.ClassPathResource(path.startsWith("/") ? path.substring(1) : path,
                                getClassLoader());
                    }
                };
                context.setServletContext(servletContext);
                context.setConfigLocations(PARENT_CONFIG);
                context.refresh();
                parentContext = context;
            }
            return parentContext;
        }
    }
}
