
<table style="border:none">
	<tr>
		<td class="label">Tax Category ID</td>
		<td>${cat.categoryId}</td>
	</tr>
	<tr>
		<td class="label">Tax Category Name</td>
		<td>${cat.categoryName}</td>
	</tr>
</table>

<br/>

<div class="screenlet">
	<div class="screenlet-title-bar">
		<h3> Available Deductions</h3>
	</div>
	<div class="screenlet-body">
		<table style="border:none">
			<#assign counter=1>
			<#list items as item>
			<tr>
				<td>${counter}. ${item.description}</td>
			</tr>
			<#assign counter=counter+1>
			</#list>
		</table>	
	</div>
</div>