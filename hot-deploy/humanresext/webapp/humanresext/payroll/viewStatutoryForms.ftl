<table width="100%"  border="1" class="basic-table">

<thead style="font-size:+1;font-weight:bold">
	<tr>
		<td>
			Form Name
		</td>
		<td>
			Form Description
		</td>
	</tr>
</thead>

<tbody>

	<tr>
		<td>
			<a href="FindForm16">Form16</a>
		</td>
	</tr>
	<tr>
		<td>
			<a href="FindPF3A">PFForm3A</a>
		</td>
	</tr>
	<tr>
		<td>
			<a href="FindPF6A">PFForm6A</a>
		</td>
	</tr>
	<#assign entities = customForms>
	<#list entities as entity>
	<tr>
		<td>
			<a href="/formdesign/control/previewLayout?formId=${entity.formId}" target="_new">${entity.formName}</a>
		</td>
		<td>
			${entity.description}
		</td>
	</tr>
	</#list>
	
	<tr>
			<td>
				<a href="FindForm16">Form-10</a>
			</td>
		</tr>
		<tr>
			<td>
				<a href="FindForm16">Combined Challan</a>
			</td>
		</tr>
		<tr>
			<td>
				<a href="FindForm16">Form-12A</a>
			</td>
	</tr>
</tbody>

</table>