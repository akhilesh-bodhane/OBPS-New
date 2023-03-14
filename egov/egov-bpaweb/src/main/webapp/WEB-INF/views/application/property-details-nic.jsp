<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/WEB-INF/taglib/cdn.tld" prefix="cdn" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<div class="panel-heading toggle-header custom_form_panel_heading">
    <div class="panel-title">
        <spring:message code="lbl.property.details.nic"/>
    </div>
    <div class="history-icon toggle-icon">
        <i class="fa fa-angle-up fa-2x"></i>
    </div>
</div>
<div class="panel-body display-hide">
    <div class="form-group">
        <label
                class="col-sm-3 control-label text-right demolitionArea"><spring:message
                code="lbl.property.details.nic"/><span ></span></label>
        <div class="col-sm-3 add-margin">
            <form:input
                    class="form-control patternvalidation maximumArea decimalfixed"
                    maxlength="10" data-pattern="decimalvalue" id="demolitionArea"
                    path="siteDetail[0].fileName"/>
        </div>
    </div>
</div>