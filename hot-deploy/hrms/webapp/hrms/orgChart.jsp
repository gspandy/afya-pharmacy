
<?xml version="1.0" encoding="ISO-8859-1" ?>

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="org.ofbiz.entity.GenericDelegator"%>
<%@page import="org.ofbiz.entity.GenericValue"%>
<%@page import="org.zkoss.zk.ui.Executions"%>
<%@page import="org.ofbiz.humanresext.util.HumanResUtil"%>
<%@page import="org.ofbiz.party.contact.ContactMechWorker"%>
<%@page import="java.util.List"%>
<%@page import="org.ofbiz.entity.condition.EntityCondition"%>
<%@page import="org.ofbiz.base.util.UtilMisc"%>
<%@page import="org.ofbiz.entity.GenericEntityException"%>
<head>
<meta http-equiv="Content-Type" conteTnt="text/html; charset=ISO-8859-1" />
<title>Insert title here</title>
<!--Load the AJAX API-->
<script type="text/javascript" src="http://www.google.com/jsapi"></script>
<script type='text/javascript'> 

      google.load('visualization', '1', {packages:['orgchart']}); 
      google.setOnLoadCallback(drawChart); 
      function drawChart() { 
        var data = new google.visualization.DataTable(); 
        data.addColumn('string', 'Name'); 
        data.addColumn('string', 'Manager'); 
 <%
 		    GenericDelegator delegator = GenericDelegator
					.getGenericDelegator("default");
			GenericValue userLogin = (GenericValue) session
					.getAttribute("userLogin");
			String partyId = userLogin.getString("partyId");
			GenericValue person = delegator.findByPrimaryKey("Person", UtilMisc
					.toMap("partyId", partyId));
			String firstName=person.getString("firstName");
			String lastName=person.getString("lastName");
			List telecomNumberDetails = org.ofbiz.party.contact.ContactMechWorker
			.getPartyContactMechValueMaps(delegator, partyId, false,
					"TELECOM_NUMBER");
		    Map mapGv=null;
		    Object contact=null;
			for(Object o:telecomNumberDetails)
			{
				Map gv=(Map)o;
				 mapGv= (Map)gv.get("telecomNumber");
				 contact= mapGv.get("contactNumber");

			}
			
    	List <GenericValue> subOrdinatesList=HumanResUtil.getSubordinatesForParty(partyId,delegator);
		GenericValue managerParty=HumanResUtil.getReportingMangerForParty(partyId,delegator);
		String mgrPartyId =null;
		String  blockFormat = "data.addRow([{v:'%s',f:'%s<div>" +
		"<table border=\"0\">" +
		"<tr>" +
			"<td>%s</td>" +
			"<td>%s</td>" +
		"</tr>" +
		"<tr>" +
		"<td colspan=\"2\">Phone No: %s</td>" +
		"</tr>" +
	"</table>" +
 "</div>'},'%s']);";

		if(managerParty!=null && managerParty.size()>0)
		{
		 mgrPartyId = managerParty.getString("partyId");
	    GenericValue manDetails = delegator.findByPrimaryKey("Person", UtilMisc
				.toMap("partyId", mgrPartyId));
		String manFirstName=manDetails.getString("firstName");
		String manLastName=manDetails.getString("lastName");
		List telecomNumberDetailsMan = org.ofbiz.party.contact.ContactMechWorker
		.getPartyContactMechValueMaps(delegator, mgrPartyId, false,
				"TELECOM_NUMBER");
	    Map mapGvMan=null;
	    Object contactMan=null;
		for(Object o:telecomNumberDetailsMan)
		{
			Map gvMan=(Map)o;
			 mapGvMan= (Map)gvMan.get("telecomNumber");
			 contactMan= mapGvMan.get("contactNumber");
		}
		
	 	out.print(String.format(blockFormat, mgrPartyId, mgrPartyId, manFirstName, manLastName, contactMan,""));
	 	out.print(String.format(blockFormat, partyId, partyId, firstName, lastName, contact,mgrPartyId));
	 	}
		
		else
		{	out.print(String.format(blockFormat, partyId, partyId, firstName, lastName, contact,""));

		}
		    if(subOrdinatesList!=null && subOrdinatesList.size()>0){

    	for (GenericValue gv: subOrdinatesList)
  	 	{
 	 	String subord= gv.getString("emplPositionIdManagedBy");
	   	GenericValue partyGv=HumanResUtil.getLatestFulfillmentForPosition(subord,delegator);
	   	String subOrdPartyId=partyGv.getString("partyId");
	    GenericValue subOrdDetails = delegator.findByPrimaryKey("Person", UtilMisc
		.toMap("partyId", subOrdPartyId));
		String subOrdFirst=subOrdDetails.getString("firstName");
		String subOrdLast=subOrdDetails.getString("lastName");
		
		List telecomNumberDetailsOrd = org.ofbiz.party.contact.ContactMechWorker
		.getPartyContactMechValueMaps(delegator, subOrdPartyId, false,
				"TELECOM_NUMBER");
	    Map mapGvOrd=null;
	    Object contactOrd=null;
		for(Object o:telecomNumberDetailsOrd)
		{
			Map gvOrd=(Map)o;
			 mapGvOrd= (Map)gvOrd.get("telecomNumber");
			 contactOrd= mapGvOrd.get("contactNumber");

		}

	 	out.print(String.format(blockFormat, partyGv.getString("partyId"), partyGv.getString("partyId"), subOrdFirst, subOrdLast, contactOrd,partyId));

	   	 }
  	 	 }
  %>  
         var chart = new google.visualization.OrgChart(document.getElementById('chart_div')); 
                chart.draw(data, {allowHtml:true,allowCollapse:true}); 
                collapse(1, collapsed);
                getChildrenIndexes(0);
              } 
		   // Create a table visualization on your page. 
			      var table = new google.visualization.Table(document.getElementById('chart_div'));
			      table.draw(data, options);
			
			      // Every time the table fires the "select" event, it should call your
			      // selectHandler() function.
			      google.visualization.events.addListener(table, 'select', selectHandler);
  </script>
</head>
<body>
<div id="chart_div"></div>
</body>

<%@page import="java.util.Map"%></html>