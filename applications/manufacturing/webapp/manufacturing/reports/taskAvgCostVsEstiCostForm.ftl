<!-- Begin Section Widget  -->
<!-- Begin  Form Widget - Form Element  component://manufacturing/widget/manufacturing/ReportForms.xml#TaskActualEstCostRepForm -->
<form method="post" action="/manufacturing/control/TaskAvgCostEstCostRep.xls" target="_blank" id="TaskActualEstCostRepForm" class="basic-form" onsubmit="javascript:submitFormDisableSubmits(this)" name="TaskActualEstCostRepForm">

    <input type="hidden" name="workEffortTypeId" value="PROD_ORDER_HEADER" id="TaskActualEstCostRepForm_workEffortTypeId"/>   
    <input type="hidden" name="currentStatusId_op" id="TaskActualEstCostRepForm_currentStatusId_op" value="equals"/>
    <input type="hidden" name="currentStatusId" id="TaskActualEstCostRepForm_currentStatusId" value="PRUN_CLOSED"/>
 
<div class="fieldgroup" id="_G45_">
<div class="fieldgroup-title-bar">
 </div>
<div id="_G45__body" class="fieldgroup-body" >
    <table cellspacing="0" class="basic-table">
    
    <tr>
    <td class="label">
Product ID    </td>
    <td>
<span class="field-lookup">
<input type="text" 
 name="productId" size="25" id="TaskActualEstCostRepForm_productId" autocomplete="off"/>
 <a href="javascript:call_fieldlookupLayer(document.TaskActualEstCostRepForm.productId,'LookupProduct','','','topleft', 'true');"></a>
</span>

<script language="JavaScript" type="text/javascript">ajaxAutoCompleter('TaskActualEstCostRepForm_productId,/manufacturing/control/LookupProduct,ajaxLookup=Y&amp;searchValueField=productId', true);</script>
    </td>
    </tr>
    <tr>
    <td class="label">
Production Run Name    </td>
    <td>
<select name="workEffortName_op" id="TaskActualEstCostRepForm_workEffortName_op"   class="selectBox"><option value="equals">Equals</option><option value="like">Begins With</option><option value="contains" selected="selected">Contains</option><option value="empty">Is Empty</option><option value="notEqual">Not Equal</option></select>

 <input type="text" 
 name="workEffortName"  id="TaskActualEstCostRepForm_workEffortName" size="25"/>
 <input type="checkbox" name="workEffortName_ic" value="Y"  checked="checked" /> Ignore Case
    </td>
    </tr>
    <tr>
    <td class="label">
Start Date    </td>
    <td>
<span class="view-calendar">

<input type="text" 
 name="estimatedStartDate_fld0_value" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30"/><a href="javascript:call_cal(document.TaskActualEstCostRepForm.estimatedStartDate_fld0_value,'2012-05-25%2011&#58;48&#58;33.701');" title="View Calendar"></a><select name="estimatedStartDate_fld0_op" class="selectBox"><option value="equals" selected="selected">Equals</option><option value="sameDay">Same Day</option><option value="greaterThanFromDayStart">Greater Than From Day Start</option><option value="greaterThan">Greater Than</option></select><input type="text" 
 name="estimatedStartDate_fld1_value" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30"/><a href="javascript:call_cal(document.TaskActualEstCostRepForm.estimatedStartDate_fld1_value,'2012-05-25%2011&#58;48&#58;33.701');" title="View Calendar"></a><select name="estimatedStartDate_fld1_op" class="selectBox"><option value="opLessThan">Less Than</option><option value="upToDay">Up To Day</option><option value="upThruDay">Up Thru Day</option><option value="empty">Is Empty</option></select></span>
    </td>
    </tr>
    <tr>
    <td class="label">

Facility ID    </td>
    <td>
     <select name="facilityId" id="TaskActualEstCostRepForm_facilityId">
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
 id="TaskActualEstCostRepForm_exportType" size="1">
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
    
		<td></td>
		<td colspan="2" width="100%" align="">
			<input type="submit" 
			 name="submitButton1" value="Run" class="btn btn-primary"/>
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
        		var formId = document.TaskActualEstCostRepForm.id;
				if ($(formId))
					validateNewUser = new Validation(formId, {immediate: true});
				});
				
</script>
<!-- End  Form Widget - Form Element  component://manufacturing/widget/manufacturing/ReportForms.xml#TaskActualEstCostRepForm --><!-- End Section Widget  -->