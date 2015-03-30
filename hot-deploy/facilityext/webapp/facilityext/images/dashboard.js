function showPortlet(divId){
	divId = parseInt(divId);
	for(i=1;i<4;i++){
		if(i==divId){
			$("portlet_"+i).show();
			$("portlet_"+i).addClass('');
		}else{
			$("portlet_"+i).hide();
		}
	}
}

