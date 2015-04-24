<#if attachments?has_content>
	<table>
		<tr>
			<td>
				<b>Attachments : </b>
				<#list attachments as attachment>
					<a href="<@ofbizUrl>downloadAttachment/${attachment.fileName}?${attachment.issueAttachmentId}</@ofbizUrl>">${attachment.fileName}</a>, 
				</#list>
			</td>
		</tr>
	</table>
</#if>