
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

<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.egov.infra.config.core.ApplicationThreadLocals"%>
<%
    String ipAddress = request.getRemoteAddr();
			String proxiedIPAddress = request.getHeader("X-Forwarded-For");
			if (StringUtils.isNotBlank(proxiedIPAddress)) {
				String[] ipAddresses = proxiedIPAddress.split(",");
				ipAddress = ipAddresses[ipAddresses.length - 1].trim();
			}
	String userAgentInfo = request.getHeader("User-Agent");
	String tenantId = ApplicationThreadLocals.getTenantID();
%>
<!DOCTYPE html>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn"%>
<html lang="en">
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
	<meta name="description" content="Chandigarh Building Plan Approvals" />
	<meta name="author" content="Chandigarh Administration" />
	<title>Chandigarh Building Plan Approvals</title>
	<link rel="icon" href="<cdn:url value='/resources/global/images/favicon.png'/>" sizes="32x32">
	<link rel="stylesheet" href="<cdn:url value='/resources/global/css/bts/bts5.css'/>">
	<link rel="stylesheet" href="<cdn:url value='/resources/global/css/font-icons/font-awesome/css/font-awesome.min.css'/>">
	<link rel="stylesheet" href="<cdn:url value='/resources/global/css/egov/obps-ui.css?rnd=${app_release_no}'/>">
	<link rel="stylesheet" href="<cdn:url value='/resources/global/css/egov/obps-login.css?rnd=${app_release_no}'/>">
	<script src="<cdn:url value='/resources/global/js/jq/jq.js'/>" type="text/javascript"></script>
	<script src="<cdn:url value='/resources/global/js/jq/plugins/jq.migrate.min.js'/>" type="text/javascript"></script>
	<script src="<cdn:url value='/resources/global/js/egov/obps-ui.js?rnd=${app_release_no}'/>" type="text/javascript"></script>

	<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
	<!--[if lt IE 9]>
		<script src="<cdn:url value='/resources/global/js/ie8/html5shiv.min.js'/>"></script>
		<script src="<cdn:url value='/resources/global/js/ie8/respond.min.js'/>"></script>
	<![endif]-->
</head>
<body class="ob-login">
	<c:set var="tenantId" value="<%=tenantId%>" />

	<!-- ===== Public home: navigation + hero; the sign-in form opens from the Login button ===== -->
	<header class="ob-land-nav" role="banner">
		<a class="ob-land-brand" href="javascript:void(0);" aria-label="Online Building Plan Approval System">
			<img src="<c:url value='/resources/global/images/logo.png' context='/egi'/>" alt="Chandigarh Administration">
			<span class="ob-land-brand-text"><strong>OBPAS</strong><em>Chandigarh Administration</em></span>
		</a>
		<div class="ob-actions">
			<a class="ob-land-link" href="#highlights">Help &amp; resources</a>
			<button type="button" class="btn btn-primary ob-land-login" data-ob-login><i class="fa fa-sign-in" aria-hidden="true"></i> <spring:message code="lbl.login" /></button>
		</div>
	</header>

	<section class="ob-land-hero" aria-label="Home">
		<div class="ob-land-inner">
			<span class="ob-eyebrow">Online Building Plan Approval System</span>
			<h1>Building Plan Approvals</h1>
			<p>Online building plan approval system (OBPAS) is a single window portal for online scrutiny and approval of building plan applications within the municipal limits of Chandigarh. Citizens, Officials, and other stakeholders can submit and track applications in real time and obtain approvals without having to physically visit an office.</p>
			<ul class="ob-login-points">
				<li><i class="fa fa-search" aria-hidden="true"></i><span>Online scrutiny of building plans</span></li>
				<li><i class="fa fa-line-chart" aria-hidden="true"></i><span>Track every application in real time</span></li>
				<li><i class="fa fa-check-circle" aria-hidden="true"></i><span>Obtain approvals without visiting an office</span></li>
			</ul>
			<div class="ob-land-cta">
				<button type="button" class="btn btn-primary btn-lg" data-ob-login><i class="fa fa-sign-in" aria-hidden="true"></i> <spring:message code="lbl.login" /></button>
				<a class="btn btn-lg ob-land-ghost" href="#highlights">Help &amp; resources</a>
			</div>
		</div>
		<a class="ob-land-scroll" href="#highlights" aria-label="Scroll to highlights"><i class="fa fa-angle-down" aria-hidden="true"></i></a>
	</section>

	<!-- ===== Sign-in dialog (same form, ids and hooks as before) ===== -->
	<div class="modal fade ob-login-modal" id="loginModal" tabindex="-1" role="dialog" aria-label="Sign in" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<button type="button" class="close ob-login-x" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				<div class="ob-login-card">
				<div class="ob-login-card-head">
					<h2><spring:message code="lbl.login" /></h2>
					<p>Sign in with your Login ID to continue.</p>
				</div>

				<c:if test="${not empty reset}">
					<div class="ob-alert ${reset ? 'ok' : 'bad'}" role="alert">
						<c:choose>
							<c:when test="${reset}"><spring:message code="msg.success.pwd.reset" /></c:when>
							<c:otherwise><spring:message code="msg.fail.pwd.reset" /></c:otherwise>
						</c:choose>
					</div>
				</c:if>

				<form action="${pageContext.request.contextPath}/j_security_check" method="post" role="form" id="signform">
					<div class="ob-field">
						<label class="ob-label" for="j_username">Login ID</label>
						<div class="ob-input">
							<i class="fa fa-user ob-input-ico" aria-hidden="true"></i>
							<input name="j_username" id="j_username" placeholder="Enter Login ID" required="" type="text" autofocus="autofocus" autocomplete="off" class="form-control" />
						</div>
					</div>
					<div class="ob-field">
						<div class="ob-label-row">
							<label class="ob-label" for="j_password">Password</label>
							<button type="button" class="ob-link" data-ob-forgot>Forgot password?</button>
						</div>
						<div class="ob-input">
							<i class="fa fa-lock ob-input-ico" aria-hidden="true"></i>
							<input name="j_password" id="j_password" required="required" placeholder="Enter Password" type="password" class="form-control" autocomplete="off" data-ob-toggle-pass />
						</div>
					</div>

					<div class="ob-field display-hide" id="counter-section">
						<label class="ob-label" for="locationId">Location</label>
						<div class="ob-input">
							<i class="fa fa-map-marker ob-input-ico" aria-hidden="true"></i>
							<select class="form-control" name="locationId" id="locationId"></select>
						</div>
						<label id="locationId-error" class="error" for="locationId"></label>
					</div>

					<div class="ob-field ob-captcha">
						<spring:eval expression="@environment.getProperty('captcha.strength')" var="strength" />
						<c:import url="/WEB-INF/views/common/captcha-${strength}.jsp" context="/egi" />
					</div>

					<c:if test="${param.sessionSecurity}">
						<div class="ob-alert bad" role="alert"><spring:message code="msg.session.security" /></div>
					</c:if>
					<c:if test="${param.error}">
						<div class="ob-alert bad" role="alert">
							<c:set var="security_message" value="${sessionScope.SPRING_SECURITY_LAST_EXCEPTION.message}" />
							<c:choose>
								<c:when test="${security_message.contains('Maximum sessions')}">
									<spring:message code="msg.multiple.login" />
								</c:when>
								<c:when test="${security_message.contains('expired')}">
									<spring:message code="msg.cred.exprd1" />
									<a href="javascript:void(0);" data-ob-forgot> <spring:message code="msg.cred.exprd2" /></a>
									<spring:message code="msg.cred.exprd3" />
								</c:when>
								<c:when test="${fn:contains(security_message, 'User account is locked')}">
									<spring:message code="msg.acc.locked" />
								</c:when>
								<c:when test="${fn:contains(security_message, 'Recaptcha Invalid')}">
									<spring:message code="err.recaptcha.invalid" />
								</c:when>
								<c:when test="${fn:contains(security_message, 'Too many attempts')}">
									<c:set var="attempts" value="${fn:substringAfter(security_message, 'Too many attempts')}" />
									<spring:message code="msg.acc.toomany.attempt" arguments="${attempts}" />
								</c:when>
								<c:otherwise>
									<spring:message code="msg.cred.invalid" />
								</c:otherwise>
							</c:choose>
						</div>
					</c:if>

					<input type="hidden" id="ipAddress" name="ipAddress" value="<%=ipAddress%>" />
					<input type="hidden" id="loginType" name="loginType" />
					<input type="hidden" name="userAgentInfo" value="<%=userAgentInfo%>" />

					<button id="signin-action" type="submit" class="btn btn-primary btn-lg btn-block ob-login-submit">
						<spring:message code="lbl.login" /> <i class="fa fa-arrow-right" aria-hidden="true"></i>
					</button>
				</form>
				<noscript><div class="ob-alert bad">You don't have javascript enabled. Make sure Javascript is enabled.</div></noscript>
				</div>
			</div>
		</div>
	</div>
	<c:if test="${not empty reset or param.error or param.sessionSecurity}">
		<script>document.addEventListener('DOMContentLoaded', function () { window.jQuery('#loginModal').modal('show'); });</script>
	</c:if>

	<!-- ===== Platform highlights (existing content, unchanged) ===== -->
	<section class="ob-login-features" id="highlights" aria-label="Platform highlights">
		<div class="bpa-feature-wrapper">
			<%@include file="../../resources/guide/bpa-features.jsp"%>
		</div>
	</section>

	<!-- ===== Forgot password ===== -->
	<div class="modal fade" id="fpassword" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="myModalLabel"><spring:message code="lbl.recover.pwd" /></h4>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
				</div>
				<form method="post" role="form" id="forgotPasswordForm">
					<div class="modal-body">
						<div class="ob-field" style="margin:0">
							<label class="ob-label" for="emailOrMobileNum">Login ID</label>
							<div class="ob-input">
								<i class="fa fa-user ob-input-ico" aria-hidden="true"></i>
								<input type="text" class="form-control" name="identity" id="emailOrMobileNum" required="required" placeholder="Login ID" autocomplete="off" />
							</div>
							<input type="hidden" name="originURL" id="originURL" />
							<input type="hidden" name="byOTP" id="byOtp" />
						</div>
						<div id="emailOrMobileNoReq" class="error-msg display-hide" style="margin-top:6px"><spring:message code="lbl.pwd.recover.un.req" /></div>
						<p class="ob-hint"><spring:message code="lbl.pwd.reset.link" /></p>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="lbl.close" /></button>
						<button type="button" class="btn btn-primary recovrbtn"><spring:message code="btn.lbl.recover.link" /></button>
						<button type="button" id="recoveryotpbtn" class="btn btn-primary recovrbtn"><spring:message code="btn.lbl.recover.otp" /></button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<div class="modal fade" id="cookieornoscript" data-backdrop="static">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header"><h4 class="modal-title">Enable Cookies</h4></div>
				<div class="modal-body">Your browser seems to have cookies disabled. Make sure cookies are enabled or try opening a new browser window.</div>
			</div>
		</div>
	</div>

	<c:if test="${not empty recovered}">
		<div class="modal fade" data-backdrop="static" id="resetpwd">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header"><h4 class="modal-title"><spring:message code="lbl.recover.pwd" /></h4></div>
					<form method="post" role="form">
						<c:choose>
							<c:when test="${recovered}">
								<c:if test="${byOTP}">
									<div class="modal-body">
										<div class="ob-field" style="margin:0">
											<label class="ob-label" for="token">Enter OTP</label>
											<input type="hidden" name="token" id="token" />
											<div class="otp-wrap">
												<div class="otp-boxes" id="otpBoxes">
													<input type="password" class="otp-box" maxlength="1" inputmode="numeric" pattern="[0-9]*" autocomplete="off" aria-label="OTP digit 1" />
													<input type="password" class="otp-box" maxlength="1" inputmode="numeric" pattern="[0-9]*" autocomplete="off" aria-label="OTP digit 2" />
													<input type="password" class="otp-box" maxlength="1" inputmode="numeric" pattern="[0-9]*" autocomplete="off" aria-label="OTP digit 3" />
													<input type="password" class="otp-box" maxlength="1" inputmode="numeric" pattern="[0-9]*" autocomplete="off" aria-label="OTP digit 4" />
													<input type="password" class="otp-box" maxlength="1" inputmode="numeric" pattern="[0-9]*" autocomplete="off" aria-label="OTP digit 5" />
													<input type="password" class="otp-box" maxlength="1" inputmode="numeric" pattern="[0-9]*" autocomplete="off" aria-label="OTP digit 6" />
												</div>
												<button type="button" id="otpToggle" class="otp-toggle" aria-label="Show OTP" aria-pressed="false" title="Show OTP"><i class="fa fa-eye" aria-hidden="true"></i></button>
											</div>
										</div>
										<p class="ob-hint">OTP sent to your registered mobile / email</p>
									</div>
									<div class="modal-footer">
										<button type="button" class="btn btn-primary recovrbtn" id="otprecoverybtn"><spring:message code="title.reset.password" /></button>
									</div>
								</c:if>
								<c:if test="${not byOTP}">
									<div class="modal-body"><spring:message code="msg.success.pwd.recov.otp.${byOTP}" /></div>
									<div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
								</c:if>
							</c:when>
							<c:otherwise>
								<div class="modal-body"><div class="error-msg"><spring:message code="msg.fail.pwd.recov" /></div></div>
								<div class="modal-footer"><button type="button" class="btn btn-default" data-dismiss="modal">Close</button></div>
							</c:otherwise>
						</c:choose>
					</form>
				</div>
			</div>
		</div>
		<script>
			$(document).ready(function() {
				$('#resetpwd').modal('show', {
					backdrop : 'static'
				});
			});
		</script>
	</c:if>

	<script src="<cdn:url value='/resources/global/js/bts/bts5.js'/>" type="text/javascript"></script>
	<script src="<cdn:url value='/resources/global/js/bts/bts-compat.js'/>" type="text/javascript"></script>
	<script src="<cdn:url value='/resources/global/js/egov/custom.js?rnd=${app_release_no}'/>" type="text/javascript"></script>
	<script src="<cdn:url value='/resources/global/js/jq/plugins/jq.validate.min.js'/>"></script>
	<script src="<cdn:url value='/resources/js/app/login.js?rnd=${app_release_no}'/>" type="text/javascript"></script>
	<script type="text/javascript">
		// Landing page: the Login buttons open the sign-in dialog; "Forgot password" swaps it for the recovery dialog.
		jQuery(function ($) {
			$('[data-ob-login]').on('click', function () { $('#loginModal').modal('show'); });
			$('#loginModal').on('shown.bs.modal', function () { $('#j_username').trigger('focus'); });
			$(document).on('click', '[data-ob-forgot]', function (e) {
				e.preventDefault();
				var $login = $('#loginModal');
				function openRecovery() { $('#fpassword').modal('show', {backdrop: 'static'}); }
				if ($login.hasClass('show') || $login.hasClass('in')) { $login.one('hidden.bs.modal', openRecovery); $login.modal('hide'); } else { openRecovery(); }
			});
			// closing recovery brings the sign-in dialog back
			$('#fpassword').on('hidden.bs.modal', function () { if (!$('#resetpwd').length) { $('#loginModal').modal('show'); } });
		});
	</script>
</body>
</html>
