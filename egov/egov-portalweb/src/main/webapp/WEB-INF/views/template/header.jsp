<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<header class="ob-topbar ob-portal-bar" role="banner">
    <a class="ob-brand" href="javascript:void(0);" aria-label="Citizen Portal">
        <img src="<c:url value='/resources/global/images/logo.png' context='/egi'/>" alt="Chandigarh Administration">
        <span class="ob-brand-text">
            <span class="ob-brand-name">Citizen Portal</span>
            <span class="ob-brand-sub"><c:out value="${cityName}"/></span>
        </span>
    </a>

    <nav class="ob-navgroup hr-menu" aria-label="Primary">
        <ul class="ob-navgroup" style="margin:0">
            <li class="active"><a class="menu-item ob-navlink active" href="/portal/home" data-show-screen="#inbox-template"><i class="fa fa-inbox" aria-hidden="true"></i><span class="title t">Inbox</span><span id="unreadMessageCount" class="badge custom-badge"></span></a></li>
            <li><a class="menu-item ob-navlink" href="javascript:void(0);" data-show-screen="#myaccount"><i class="fa fa-user-circle-o" aria-hidden="true"></i><span class="t">My Account</span></a></li>
            <li><a class="menu-item ob-navlink" href="javascript:void(0);" data-show-screen="#newreq"><i class="fa fa-plus-circle" aria-hidden="true"></i><span class="t">New Request</span></a></li>
        </ul>
    </nav>

    <div class="ob-actions nav-right-menu home">
        <a class="ob-iconbtn" href="javascript:void(0);" title="Help" aria-label="Help"><i class="fa fa-question-circle" aria-hidden="true"></i></a>
        <div class="ob-userwrap">
            <button type="button" class="ob-iconbtn ob-userbtn" data-ob-menu aria-haspopup="true" aria-expanded="false">
                <span class="ob-avatar" data-ob-initials="${userName}"><i class="fa fa-user" aria-hidden="true"></i></span>
                <span class="ob-username t"><c:out value="${userName}"/></span>
                <i class="fa fa-angle-down" aria-hidden="true"></i>
            </button>
            <ul class="ob-menu" role="menu">
                <li><a href="/egi/home/profile/edit" data-open-popup role="menuitem"><i class="fa fa-user"></i>Edit Profile</a></li>
                <li><a href="javascript:void(0);" onclick="jQuery('.change-password').modal('show', {backdrop: 'static'});" role="menuitem"><i class="fa fa-key"></i>Change Password</a></li>
                <li class="ob-menu-sep"></li>
                <li><a href="/egi/logout" role="menuitem"><i class="fa fa-sign-out"></i>Sign out</a></li>
            </ul>
        </div>
    </div>
</header>
