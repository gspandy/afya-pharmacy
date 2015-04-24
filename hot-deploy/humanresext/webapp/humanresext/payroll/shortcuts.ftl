<div class="screenlet">
    <div class="screenlet-header"><div class="boxhead">${uiLabelMap.Shortcuts}</div></div>
    <div class="screenlet-body">
    <ul class="shortcuts">

        <li><a href="<@ofbizUrl>FindPayslip</@ofbizUrl>" >${uiLabelMap.HumanResPayslip}</a></li>
        <li><a href="<@ofbizUrl>ViewEmplSal?partyId=${userLogin.partyId}&mode=view</@ofbizUrl>" >${uiLabelMap.HumanResMySalaryDetail}</a></li>
        <li><a href="<@ofbizUrl>FindTDS</@ofbizUrl>" >${uiLabelMap.HumanResTDS}</a></li>
        <li><a href="<@ofbizUrl>FindLoan</@ofbizUrl>" >${uiLabelMap.HumanResLoan}</a></li>
        <li><a href="<@ofbizUrl>Preferences</@ofbizUrl>" >${uiLabelMap.HumanResPreferences}</a></li>
        <#if security.hasEntityPermission("HUMANRES","_ADMIN",session)>
        	 <li><a href="<@ofbizUrl>FindEmplSal</@ofbizUrl>">${uiLabelMap.HumanResEmployeeSal}</a></li>      	
        </#if>   
        <#--
        <#if security.hasEntityPermission("HUMANRES","_ADMIN",session)>
	        <li><a href="<@ofbizUrl>SalaryHeads</@ofbizUrl>">${uiLabelMap.HumanResSalaryHead}</a></li>
	        <li><a href="<@ofbizUrl>SalaryHeadTypes</@ofbizUrl>" >${uiLabelMap.HumanResSalaryHeadType}</a></li>
	        <li><a href="<@ofbizUrl>editRuleAndSalaryHeadAssoc</@ofbizUrl>" >Exemption For Salary Head</a></li>
	        <li><a href="<@ofbizUrl>Conditions</@ofbizUrl>" >${uiLabelMap.HumanResPayrollConditions}</a></li>
	        <li><a href="<@ofbizUrl>Actions</@ofbizUrl>" >${uiLabelMap.HumanResPayrollActions}</a></li>
	        <li><a href="<@ofbizUrl>Rules</@ofbizUrl>" >${uiLabelMap.HumanResPayrollRules}</a></li>
	        <li><a href="<@ofbizUrl>FindSalaryStructure</@ofbizUrl>" >${uiLabelMap.HumanResSalaryStructure}</a></li>    
	        <li><a href="<@ofbizUrl>ManagePayroll</@ofbizUrl>" >${uiLabelMap.HumanResManagePayroll}</a></li>   
        </#if>   
        -->
      </ul>
    </div>
</div>