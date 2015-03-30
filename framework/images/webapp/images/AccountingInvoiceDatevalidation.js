function getDates(invoiceDate,dueDate) {
	var dt1 ;
	var mon1 ;
	var yr1 ;
	var dt2 ;
	var mon2 ;
	var yr2;

	if(invoiceDate!=null) {
		dt1 = parseInt(invoiceDate.substring(8,10),10);
		mon1 = parseInt(invoiceDate.substring(5,7),10);
		yr1 = parseInt(invoiceDate.substring(0,4),10);
		hr1 = parseInt(invoiceDate.substring(11,13),10);
		min1 = parseInt(invoiceDate.substring(14,16),10);
		sec1 = parseInt(invoiceDate.substring(17,19),10);
	}

	if(dueDate!=null) { 
		dt2 = parseInt(dueDate.substring(8,10),10);
		mon2 = parseInt(dueDate.substring(5,7),10);
		yr2 = parseInt(dueDate.substring(0,4),10);
		hr2 = parseInt(dueDate.substring(11,13),10);
		min2 = parseInt(dueDate.substring(14,16),10);
		sec2 = parseInt(dueDate.substring(17,19),10);
	}

	invoice = new Date(yr1,mon1,dt1,hr1,min1,sec1);
	due = new Date(yr2,mon2,dt2,hr2,min2,sec2);

	if(invoice > due){
		alert('Invoice DUE DATE TIME should be Greater than INVOICE DATE TIME');
		return false;
	}

}
