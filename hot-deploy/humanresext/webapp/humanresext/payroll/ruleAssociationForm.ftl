<br/><br/>
<form action="<@ofbizUrl>CreateRuleConditionAction</@ofbizUrl>" name="RuleAssociationForm" method="post">
	<input type="hidden" name="ruleId" value="${parameters.ruleId}" />
	<div class="label" style="border:1px">
 	  ${uiLabelMap.HumanResPayrollCondition} <input type="text" name="conditionId" />
      <a href="javascript:call_fieldlookup2(document.RuleAssociationForm.conditionId,'<@ofbizUrl>LookupPayrollCondition?expr=N</@ofbizUrl>');">
      	<img style="visible:false" src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'>
      </a>&nbsp;&nbsp;
 	  ${uiLabelMap.HumanResPayrollAction} <input type="text" name="actionId" />
      <a href="javascript:call_fieldlookup2(document.RuleAssociationForm.actionId,'<@ofbizUrl>LookupPayrollAction?expr=N</@ofbizUrl>');">
      	<img style="visible:false" src='/images/fieldlookup.gif' width='15' height='14' border='0' alt='Click here For Field Lookup'>
      </a>&nbsp;&nbsp;
 	  <input type="submit" value="${uiLabelMap.CommonAdd}"/>
	</div>
</form>
<br/><br/>