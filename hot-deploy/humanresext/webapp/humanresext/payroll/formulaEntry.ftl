<div class="screenlet">
    <div class="screenlet-title-bar">
        <h3>Rules for ${parameters.computationType} Computation</h3>
    </div>
    <div class="screenlet-body">  
        <form method="post" action="<@ofbizUrl>updatePayrollRule</@ofbizUrl>" name="updatePayrollRule">
        	 <table border="2">
       		    <tr>
    				<td  class="label"><b>${uiLabelMap.PayrollRuleName}</b></td>
    				<td><input type="text" size="15" name="payrollRuleName"></td>
    			</tr>
            	<tr>	
            		<td class="label"><b>${uiLabelMap.PayrollRuleDescription}</b></td>
    				<td><textarea name="description" rows='3' cols='25'></textarea></td>
            	</tr>
            	<tr>	
            		<td class="label"><b>From Date</b></td>
    				<td><input type="text" size="22" name="fromDate"><a href="javascript:call_cal(document.updateProductPriceRule.fromDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a></td>
            	</tr>
            	<tr>	
            		<td class="label"><b>To Date</b></td>
    				<td><input type="text" size="22" name="thruDate"><a href="javascript:call_cal(document.updateProductPriceRule.thruDate, null);"><img src='/images/cal.gif' width='16' height='16' border='0' alt='Calendar'></a></td>
            	</tr>
            </table>
       
        
        <div>
        	<table>
        		<tr>
        			<td class="label">Action</td>
        			<td>
        				<table style="width:100%">
        					<thead>
        						<tr>
        							<td>Seq Id</td>
        							<td>Select Salary Head</td>
        							<td>Action Type</td>
        							<td>Value</td>
        							<td>Perform</td>
        						</tr>
        					</thead>
        					<tbody>
        						<tr>
        							<td/>
        							<td>
        								<select>
        									<option>COL-1</option>
        									<option>COL-1</option>
        								</select>
        							</td>
        							<td>
        								<select>
        									<#list payrollActionTypes as actionType>
        									<option value="${actionType.actionTypeId}">${actionType.actionTypeId}</option>
        									</#list>
        								</select>
        							</td>
        							<td>
        								<input type="text" name="action_id"/>
        							</td>
        							<td>
        								<a href="delete?" class="buttontext">Create</a>
        							</td>
        						</tr>
        					</tbody>
        				</table>
        			</td>
        		</tr>
        	</table>
        </div>
        <input type="submit" value="${uiLabelMap.CommonUpdate}">
        </form>
    </div>
</div>