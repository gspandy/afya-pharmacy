<table width="100%">
    <tr>
        <td style="margin-top:10px">Dear ${personName},</td>
    </tr>

    <tr>
        <td><br/>The Interview performance of ${employeeName} has given below</td>
    </tr>
	<tr>
        <td><br/>Applied Requisition Id : ${requisitionId} </td>
    </tr>
	
</table>
<br/>
<#if performanceNoteList?has_content>
<table width="100%" border="1" cellspacing="1" cellpadding="1">
    <tr>
        <th colspan="2">Interviewer Id</th>
        <th colspan="2">Performance Review</th>
        <th colspan="2">Interviewer Comment</th>
        <th colspan="2">Interviewed Date</th>
    </tr>
    <#list performanceNoteList as performance>
 	<tr>
        <td colspan="2" align="center">${performance.interviewerId?if_exists}</td>
        <td colspan="2" align="center">${performance.performanceRating?if_exists}</td>
        <td colspan="2" align="center">${performance.comments?if_exists}</td>
        <td colspan="2" align="center">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(performance.communicationDate, "dd/MM/yyyy")?if_exists}</td>
        
    </tr>
   </#list>
   </table>
</#if>
<br/>
<table>
  <tr>
        <td>Please send us your approval for initiating the offer.</td>
    </tr>
    <tr>
        <td colspan="2" style="margin-top:40px"><br/>Regards</td>
    </tr>
    <tr>
        <td colspan="2">${teamFrom} &#45; ${companyName} </td>
    </tr>
  </table>