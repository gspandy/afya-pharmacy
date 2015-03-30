<h1>${title}</h1>
<form method="post" action="<@ofbizUrl>createSerializedInvIssuance</@ofbizUrl>" name="transferform" style="margin: 0;">

  <table cellspacing="0" class="basic-table">
     <tr>
     	<td colspan="2">
     		<block style="color:red;font-weight:bold;"> Issuance quantity is not matching with BOM Quantity. </block>
     	</td>   	
     </tr>
     <tr>
     	<td colspan="2" >
     		&nbsp;
     	</td>   	
     </tr>
     <tr>
     	<td colspan="2" >
     		<block style="font-weight:bold;"> Reason </block>
     		<input type="text" name="serializedComments" SIZE="50"/>
     	</td>   	
     </tr>
     <tr>
     	<td colspan="2" >
     		&nbsp;
     	</td>   	
     </tr>
     <tr>
     	<td colspan="2">
     		<div align="left" style="margin-left:6px;" >
	     	<input type="submit" class="btn btn-danger" value="Submit" />
	     	<a href="<@ofbizUrl>issuanceItems?facilityId=${facilityId}</@ofbizUrl>" class="buttontext">Cancel</a>
	     	</div>
     	</td>
     </tr>
     
   </table>
</form>
