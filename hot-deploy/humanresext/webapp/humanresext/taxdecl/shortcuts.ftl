<div class="screenlet">
    <div class="screenlet-header"><div class="boxhead">${uiLabelMap.Shortcuts}</div></div>
    <div class="screenlet-body">
      <ul class="shortcuts">
        <li><a href="<@ofbizUrl>FindTaxDeclaration</@ofbizUrl>">${uiLabelMap.TaxDeclarations}</a></li>
        <li><a href="<@ofbizUrl>ApproveTaxDeclaration</@ofbizUrl>">${uiLabelMap.ApproveTaxDeclarations}</a></li>
        <li><a href="<@ofbizUrl>FindForm16</@ofbizUrl>">${uiLabelMap.Form16}</a></li>
        <li><a href="<@ofbizUrl>FindPF</@ofbizUrl>">${uiLabelMap.PFForm6A}</a></li>
        <li><a href="<@ofbizUrl>FindPF3A</@ofbizUrl>">${uiLabelMap.PFForm3A}</a></li>
        <#if security.hasEntityPermission("HUMANRES","_ADMIN",session)>
	        <li><a href="<@ofbizUrl>FormField</@ofbizUrl>">${uiLabelMap.FormField}</a></li>
    	    <li><a href="<@ofbizUrl>EditFormField</@ofbizUrl>">${uiLabelMap.EditFormField}</a></li>
        	<li><a href="<@ofbizUrl>FindTaxDecl</@ofbizUrl>">${uiLabelMap.CommonFind} ${uiLabelMap.TaxDeclarations}</a></li>
        </#if>
        <#---
        <#if security.hasEntityPermission("HUMANRES","_ADMIN",session)>
	        <li><a href="<@ofbizUrl>editTaxCategory</@ofbizUrl>">${uiLabelMap.TaxCategoryCreate}</a></li>
	       	<li><a href="<@ofbizUrl>findTaxCategory</@ofbizUrl>">${uiLabelMap.TaxCategoryFind}</a></li>
			<li><a href="<@ofbizUrl>editTaxItem</@ofbizUrl>">${uiLabelMap.TaxItemCreate}</a></li>
			<li><a href="<@ofbizUrl>findTaxItem</@ofbizUrl>">${uiLabelMap.TaxItemFind}</a></li>
			<li><a href="<@ofbizUrl>taxDecl</@ofbizUrl>">${uiLabelMap.TaxDeclarationManage}</a></li>
		</#if>
		-->
		
	  </ul>
    </div>
</div>