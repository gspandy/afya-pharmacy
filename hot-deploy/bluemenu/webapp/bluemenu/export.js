
function invokeExport(form){
	exportType = form.exportType.value;
	
	action = form.action;
	
	jsessionIdx = action.indexOf(';jsessionid');
	
	jsessionStr = '';
	if(jsessionIdx>0){
		jsessionStr=action.substring(jsessionIdx);
		action = action.substring(0,jsessionIdx);
	}
	index = action.lastIndexOf('.');
	action = action.substring(0,index);
	
	if('pdf'==exportType){
		action=action+".pdf";
	}else if('xls'==exportType){
		action=action+".xls";
	}
	form.action=action+jsessionStr;
}