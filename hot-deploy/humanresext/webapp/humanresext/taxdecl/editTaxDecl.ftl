
<#assign categories = request.getAttribute("categories")>
<#assign editable= request.getAttribute("isEditable")>
<#assign formName = "editTaxDecl">
<form class="basic-form" name="${formName}" action="<@ofbizUrl>storeTaxDecl</@ofbizUrl>" method="POST">

<input type="hidden" name="validTaxDeclId" value="${parameters.validTaxDeclId}"/>
<input type="hidden" name="partyId" value="${parameters.partyId}"/>
<table class="basic-table">
	<tbody>
		<#list categories as category>
			<tr style="background-color:silver;">
				<td colspan="2"><h3>${category.categoryName}<h3></td>
			</tr>
			<tr style="background-color:silver;">
				<td colspan="2"><h4>**${category.description}<h4></td>
			</tr>
			<#assign items = category.getTaxItems()>
			<#list items as item>
			<tr>
				<td>${item.itemName}</td>
				<td>
					<#assign fieldName="_"+category.categoryId+"_"+item.itemId>
					<#if editable>
						<#include "fieldRenderer.ftl"/>
					<#else>
						${item.numValue?if_exists}
					</#if>
				</td>
			</tr>
			</#list>
		</#list>
		<tr>
			<td/>
			<td>
				<#if editable>
					<input type="submit" value="Submit"/>
				</#if>
			</td>
		</tr>
	</tbody>
</table>

</form>