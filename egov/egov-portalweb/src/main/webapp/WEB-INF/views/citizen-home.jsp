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
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<c:set var="req" value="${pageContext.request}" />
<c:set var="url">${req.requestURL}</c:set>
<c:set var="uri" value="${req.requestURI}" />
<c:set var="domainURL" value="${fn:substring(url, 0, fn:length(url) - fn:length(uri))}" />
<div class="ob-citizen">

	<!-- ===== Sidebar: module navigation (JS hooks: .modules-li[data-module]) ===== -->
	<aside class="left-menu ob-side" aria-label="Modules">
		<div class="ob-side-brand">
			<img src="<c:url value='/resources/global/images/logo.png' context='/egi'/>" alt="Chandigarh Administration" class="homepage_logo">
			<div class="ob-side-brand-text">
				<span class="corporation-name"><spring:message code="lbl.portalservices" /></span>
				<span class="ob-side-sub">Citizen Portal</span>
			</div>
		</div>
		<div class="ob-side-label">Navigate</div>
		<ul class="modules-ul">
			<li class="modules-li inbox active" data-module="home"><a
				class="home-module-menu-link" href="javascript:void(0)">
					<div><i class="fa fa-th-large img-view" aria-hidden="true"></i></div>
					<div class="module-text">Dashboard</div>
			</a></li>
			<c:forEach items="${moduleNames}" var="moduleName" varStatus="item">
				<li class="modules-li" data-module="${moduleName }"><a
					class="home-module-menu-link" href="javascript:void(0)">
						<div><i class="fa fa-building-o img-view" aria-hidden="true"></i></div>
						<div class="module-text"><c:out value="${moduleName}"/></div>
				</a></li>
			</c:forEach>
		</ul>
	</aside>

	<div class="right-content ob-main">

		<!-- ===== Top bar ===== -->
		<header class="ob-ctop">
			<div class="ob-ctop-title">
				<span class="ob-eyebrow">Citizen Portal</span>
			</div>
			<div class="ob-ctop-actions">
				<div class="ob-userwrap">
					<button type="button" class="ob-userbtn profile-name" data-ob-menu aria-haspopup="true" aria-expanded="false">
						<span class="ob-avatar" data-ob-initials="${userName}"><i class="fa fa-user" aria-hidden="true"></i></span>
						<span class="ob-username text"><c:out value="${userName}"/></span>
						<i class="fa fa-angle-down" aria-hidden="true"></i>
					</button>
					<ul class="ob-menu" role="menu">
						<li><a href="/egi/home/profile/edit" role="menuitem"
							onClick="MyWindow=window.open('/egi/home/profile/edit','MyWindow',width=980,height=650); return false;"><i class="fa fa-user"></i>Edit Profile</a></li>
						<li><a href="javascript:void(0)" role="menuitem"
							onclick="jQuery('.change-password').modal('show', {backdrop: 'static'});"><i class="fa fa-key"></i>Change Password</a></li>
						<li class="ob-menu-sep"></li>
						<li><a href="/egi/logout" role="menuitem"><i class="fa fa-sign-out" aria-hidden="true"></i>Sign Out</a></li>
					</ul>
				</div>
			</div>
		</header>

		<div class="main-content ob-content">

			<!-- ===== Module toolbar (shown by JS when a module is selected) ===== -->
			<div class="action-bar ob-toolbar hide">
				<div class="module-heading"></div>
				<div class="action-item ob-search">
					<i class="fa fa-search" aria-hidden="true"></i>
					<input type="text" id="search" placeholder="Search services" aria-label="Search services">
				</div>
			</div>

			<!-- ===== Dashboard ===== -->
			<div class="inbox-modules clearfix">
				<div id="showServiceGroup" class="ob-hero">
					<input type="hidden" id="userId" value="${currentUser.id}">
					<div class="ob-hero-text">
						<h1 class="welcome-message">Welcome back, <span class="ob-name"><c:out value="${userName}"/></span></h1>
						<p class="ob-hero-sub">Track your applications and start new services from one place.</p>
					</div>
					<div class="ob-hero-filter">
						<label for="serviceGroup"><spring:message code="lbl.servicegroup" /></label>
						<select class="form-control" id="serviceGroup">
							<option value="all">ALL</option>
							<c:forEach items="${distinctModuleNames}" var="module" varStatus="item">
								<option value="${module.contextRoot }">${module.displayName}</option>
							</c:forEach>
						</select>
					</div>
				</div>

				<div class="stats-item ob-kpis">
					<div name="totalServicesApplied" class="bpa-service-card ob-kpi" id="totalServicesAppliedDiv" role="button" tabindex="0">
						<div class="bpa-home-card content x">
							<span class="ob-kpi-ico ico-total"><i class="fa fa-folder-open-o" aria-hidden="true"></i></span>
							<div class="ob-kpi-body">
								<div class="count" id="totalServicesAppliedSize">${totalServicesAppliedSize }</div>
								<div class="text"><spring:message code="lbl.totalservicesapplied" /></div>
							</div>
						</div>
					</div>
					<div name="servicesUnderScrutiny" class="bpa-service-card ob-kpi" id="servicesUnderScrutinyDiv" role="button" tabindex="0">
						<div class="bpa-home-card content x clicked-card">
							<span class="ob-kpi-ico ico-pending"><i class="fa fa-hourglass-half" aria-hidden="true"></i></span>
							<div class="ob-kpi-body">
								<div class="count" id="totalServicesPendingSize">${totalServicesPendingSize}</div>
								<div class="text color-generic-new"><spring:message code="lbl.servicesunderscrutiny" /></div>
							</div>
						</div>
					</div>
					<div name="servicesCompleted" class="bpa-service-card ob-kpi" id="servicesCmpletedDiv" role="button" tabindex="0">
						<div class="bpa-home-card content x">
							<span class="ob-kpi-ico ico-done"><i class="fa fa-check-circle-o" aria-hidden="true"></i></span>
							<div class="ob-kpi-body">
								<div class="count" id="totalServicesCompletedSize">${totalServicesCompletedSize }</div>
								<div class="text"><spring:message code="lbl.servicescompleted" /></div>
							</div>
						</div>
					</div>
				</div>

				<!-- Distribution of the three counts above (computed in the browser from the numbers shown; nothing is invented) -->
				<div class="ob-card ob-dist" id="obDist" hidden>
					<div class="ob-dist-head"><span class="ob-section-title">Application progress</span><span class="ob-dist-total" id="obDistTotal"></span></div>
					<div class="ob-dist-bar" role="img" aria-label="Distribution of your applications by status" id="obDistBar"></div>
					<ul class="ob-dist-legend" id="obDistLegend"></ul>
				</div>

				<div class="bpa-table-container ob-card ob-tablecard">
					<table class="hover row-border" id="bpa-home-table">
						<thead>
							<tr>
								<th><spring:message code="lbl.slno" /></th>
								<th><spring:message code="lbl.applicant.name" /></th>
								<th><spring:message code="lbl.applicartionno" /></th>
								<th><spring:message code="lbl.applicationdate" /></th>
								<th style="display:none;"><spring:message code="lbl.servicegroup"/></th>
								<th><spring:message code="lbl.servicename" /></th>
								<th><spring:message code="lbl.status" /></th>
								<th><spring:message code="lbl.pendingaction" /></th>
							</tr>
						</thead>
					</table>
				</div>

				<c:if test="${not empty services}">
					<section class="ob-quick" aria-labelledby="obQuickTitle">
						<h2 class="ob-section-title" id="obQuickTitle">Start a new service</h2>
						<div class="ob-quick-grid">
							<c:forEach items="${services}" var="service" varStatus="item">
								<c:choose>
									<c:when test="${isState }">
										<a class="ob-quick-item" href="javascript:openPopUp('/portal/common/city/selection-form?url=${domainURL}${service.url}')">
											<span class="ob-quick-ico"><i class="fa fa-plus" aria-hidden="true"></i></span>
											<span class="ob-quick-name"><c:out value="${service.name}"/></span>
											<i class="fa fa-angle-right ob-quick-go" aria-hidden="true"></i>
										</a>
									</c:when>
									<c:otherwise>
										<a class="ob-quick-item" href="javascript:openPopUp('${service.url}')">
											<span class="ob-quick-ico"><i class="fa fa-plus" aria-hidden="true"></i></span>
											<span class="ob-quick-name"><c:out value="${service.name}"/></span>
											<i class="fa fa-angle-right ob-quick-go" aria-hidden="true"></i>
										</a>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</div>
					</section>
				</c:if>
			</div>

			<input type="hidden" value="<spring:message code="error.pwd.invalid.case" />" id="errorPwdInvalid" />
			<input type="hidden" value="${clientId}" id="clientId" name="clientId" />

			<!-- ===== Per-module service list (JS hooks: .services-item .services[data-services]) ===== -->
			<c:forEach items="${services}" var="service" varStatus="item">
				<div class="is-flex services-item ob-svc-row">
					<div class="col-lg-6 col-md-6 col-sm-6 col-xs-12 services" data-services="${service.module.displayName }">
						<c:choose>
						<c:when test="${isState }">
							<a href="javascript:openPopUp('/portal/common/city/selection-form?url=${domainURL}${service.url}')">
								<div class="content a"><i class="fa fa-file-text-o" aria-hidden="true"></i><span><c:out value="${service.name}"/></span><i class="fa fa-angle-right ob-go" aria-hidden="true"></i></div>
							</a>
						</c:when>
						<c:otherwise>
							<a href="javascript:openPopUp('${service.url}')">
								<div class="content a"><i class="fa fa-file-text-o" aria-hidden="true"></i><span><c:out value="${service.name}"/></span><i class="fa fa-angle-right ob-go" aria-hidden="true"></i></div>
							</a>
						</c:otherwise>
						</c:choose>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>
</div>

<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jq/plugins/datatables/jq.dataTables.min.css' context='/egi'/>"/>
<link rel="stylesheet" href="<cdn:url value='/resources/global/css/jq/plugins/datatables/dataTables.bts.min.css' context='/egi'/>">
<script	src="<cdn:url value='/resources/global/js/jq/plugins/datatables/jq.dataTables.min.js' context='/egi'/>"></script>
<script	src="<cdn:url value='/resources/global/js/jq/plugins/datatables/responsive/js/datatables.responsive.js' context='/egi'/>"></script>
<script	src="<cdn:url value='/resources/global/js/jq/plugins/datatables/dataTables.bts.js' context='/egi'/>"></script>
<script src="<cdn:url value='/resources/js/citizen-dashboard.js?rnd=${app_release_no}'/>"></script>
