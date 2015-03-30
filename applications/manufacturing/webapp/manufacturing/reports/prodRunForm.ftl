<!-- Begin Section Widget  -->
<!-- Begin  Form Widget - Form Element  component://manufacturing/widget/manufacturing/ReportForms.xml#ProductionRunReportForm -->
<form method="post" action="/manufacturing/control/ProdRunFilteredDoc.xls" target="_blank" id="ProductionRunReportForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="ProductionRunReportForm">

    <input type="hidden" name="workEffortTypeId" value="PROD_ORDER_HEADER" id="ProductionRunReportForm_workEffortTypeId"/>
    <input type="hidden" name="viewReportFor" value="GENERAL_RUN" id="ProductionRunReportForm_viewReportFor"/>
<div class="fieldgroup" id="_G45_">
<div class="fieldgroup-title-bar">
 </div>
<div id="_G45__body" class="fieldgroup-body" >
    <table cellspacing="0" class="basic-table">
    <tr>
    <td class="label">
Status    </td>
    <td>

<select name="currentStatusId" 
 id="ProductionRunReportForm_currentStatusId" size="1">
<option value="">&nbsp;</option>
 <option value="PRUN_CANCELLED">Cancelled</option> <option value="PRUN_CLOSED">Closed</option> <option value="PRUN_COMPLETED">Completed</option> <option value="PRUN_CREATED">Created</option> <option value="PRUN_DOC_PRINTED">Confirmed</option> <option value="PRUN_RUNNING">Running</option> <option value="PRUN_SCHEDULED">Scheduled</option></select>
 <input type="hidden" name="currentStatusId_op" id="ProductionRunReportForm_currentStatusId_op" value="equals"/>
    </td>
    </tr>
    <tr>
    <td class="label">
Product ID    </td>
    <td>
<span class="field-lookup">
<input type="text" 
 name="productId" size="25" id="ProductionRunReportForm_productId" autocomplete="off"/>
 <a href="javascript:call_fieldlookupLayer(document.ProductionRunReportForm.productId,'LookupProduct','','','topleft', 'true');"></a>
</span>

<script language="JavaScript" type="text/javascript">ajaxAutoCompleter('ProductionRunReportForm_productId,/manufacturing/control/LookupProduct,ajaxLookup=Y&amp;searchValueField=productId', true);</script>
    </td>
    </tr>
    <tr>
    <td class="label">
Production Run Name    </td>
    <td>
<select name="workEffortName_op"    class="selectBox"><option value="equals">Equals</option><option value="like">Begins With</option><option value="contains" selected="selected">Contains</option><option value="empty">Is Empty</option><option value="notEqual">Not Equal</option></select>

 <input type="text" 
 name="workEffortName" size="25"/>
 <input type="checkbox" name="workEffortName_ic" value="Y"  checked="checked" /> Ignore Case
    </td>
    </tr>
    <tr>
    <td class="label">
Start Date    </td>
    <td>
<span class="view-calendar">

<input type="text" 
 name="estimatedStartDate_fld0_value" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30"/><a href="javascript:call_cal(document.ProductionRunReportForm.estimatedStartDate_fld0_value,'2012-05-25%2011&#58;48&#58;33.701');" title="View Calendar"></a><select name="estimatedStartDate_fld0_op" class="selectBox"><option value="equals" selected="selected">Equals</option><option value="sameDay">Same Day</option><option value="greaterThanFromDayStart">Greater Than From Day Start</option><option value="greaterThan">Greater Than</option></select><input type="text" 
 name="estimatedStartDate_fld1_value" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30"/><a href="javascript:call_cal(document.ProductionRunReportForm.estimatedStartDate_fld1_value,'2012-05-25%2011&#58;48&#58;33.701');" title="View Calendar"></a><select name="estimatedStartDate_fld1_op" class="selectBox"><option value="opLessThan">Less Than</option><option value="upToDay">Up To Day</option><option value="upThruDay">Up Thru Day</option><option value="empty">Is Empty</option></select></span>
    </td>
    </tr>
    <tr>
    <td class="label">

Facility ID    </td>
    <td>
   <select name="facilityId" id="ProductionRunReportForm_facilityId">
   <option value=""></option>
	<#list facilities as facility>
		 <option value="${facility.facilityId}">${facility.facilityName?if_exists} [${facility.facilityId}]</option>
	</#list>
    </select> 
     
    </td>
    </tr>

    <tr>
    <td class="label">
Export Type    </td>
    <td>
<select name="exportType" 
 id="ProductionRunReportForm_exportType" size="1">
 <option value="pdf">PDF</option> <option value="xls">EXCEL</option></select>
    </td>
    </tr>
	 <tr>
			    <td class="label">
			
			&nbsp;    </td>
			    <td>
			
			    </td>
     </tr>
    <tr>
    
   		 <td colspan="2" width="100%" align="middle">
			<input type="button" 
			 name="submitButton1" value="Run" onClick="submitForm('GENERAL_RUN');" class="btn btn-primary"/>
			 
			 <input type="button" 
			 name="submitButton2" value="Jobs Completed On Time" onClick="submitForm('ON_TIME_RUN');" class="btn btn-primary"/>
			 
			 <input type="button" 
			 name="submitButton3" value="Pending Jobs With Status" onClick="submitForm('PENDING_RUN');" class="btn btn-primary"/>
		   
		   </td>
	 </tr>
	
	 <tr>
			    <td class="label">
			&nbsp;    </td>
			    <td colspan="4">
			
    </td>
    </tr>
    </table>
</div></div></form>
    <script language="JavaScript" type="text/javascript">
        		Event.observe(window, 'load', function() {
        		var formId = document.ProductionRunReportForm.id;
				if ($(formId))
					validateNewUser = new Validation(formId, {immediate: true});
				});
				
				function submitForm(viewReportFor){
				document.getElementById('ProductionRunReportForm_viewReportFor').value=viewReportFor;			
				document.ProductionRunReportForm.submit();
				}
</script>
<!-- End  Form Widget - Form Element  component://manufacturing/widget/manufacturing/ReportForms.xml#ProductionRunReportForm --><!-- End Section Widget  -->