
			<form method="post" action="reserveInventoryForOrder" name="reserveInventoryForOrder">
			<table>
			<tr>
	            <td><h3>Reserve By </h3> </td> &nbsp;&nbsp;
			  <td>	<select name="reserveByLogic" id="reserveByLogic">
				          <option value="1">Datetime Received-FIFO</option>
                          <option value="2">Expire Date-FIFO</option>
                          <option value="3">Datetime Received-LIFO</option>
                          <option value="4">Expire Date-LIFO</option>
                          
               
                </select></td>
			 <td><input type="submit" value="Reserve" class="btn btn-success"></td>
			<input type="hidden" name="orderId" value=${parameters.orderId}>	
			<input type="hidden" name="facilityId" value=${parameters.facilityId}>
			</tr>
			</table>
			</form>
