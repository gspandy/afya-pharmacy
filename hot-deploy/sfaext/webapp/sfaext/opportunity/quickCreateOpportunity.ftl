<#assign stages = delegator.findAll("SalesOpportunityStage")>
<form method="post" action="/sfa/control/createOpportunity" name="quickCreateOpportunityForm">
<div class="screenlet">
    <div class="screenlet-header"><div class="boxhead">${uiLabelMap.CrmCreateOpportunity}</div></div>
    <div class="screenlet-body">

        
        <span class="requiredFieldNormal">${uiLabelMap.SfaInitialAccount}</span><br/>
        <input type="text" name="initialAccount">
	    <a href="javascript:call_fieldlookup2(document.quickCreateOpportunityForm.initialAccount,'LookupSfaAccount');">
	        <img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'/>
	    </a>

<#--	    <a href="javascript:call_fieldlookup2(document.quickCreateOpportunityForm.initialAccount,'LookupLead');">
	        <img src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'/>
	    </a>
-->
        <br/><br/>

        <span class="requiredFieldNormal">${uiLabelMap.SfaOpportunityName}</span><br/>
        <input type="text" name="opportunityName" size=15 maxlength=60><br/><br/>

        <span class="requiredFieldNormal">${uiLabelMap.SfaInitialStage}</span><br/>
        <select class="inputbox" name="opportunityStageId" size="1">
        <#list stages as stage>
          <option value="${stage.opportunityStageId}">${stage.description}</option>
        </#list>
        </select><br /><br/>
            
        <span class="requiredFieldNormal">${uiLabelMap.SfaEstimatedAmount}</span><br/>
        <input type="text" name="estimatedAmount" size=15 maxlength=60/><br/><br/>

        <span class="requiredFieldNormal">${uiLabelMap.CrmEstimatedCloseDate}</span><br/>
        <@htmlTemplate.renderDateTimeField name="estimatedCloseDate" value="${value!''}" className="" alert="" 
                title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="estimatedCloseDate" dateType="date" shortDateInput=false 
                timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="quickCreateOpportunityForm"/>
        <br/><br/>

        <input type="submit" value="${uiLabelMap.CommonCreate}"/>
    </div>
</div>
</form>