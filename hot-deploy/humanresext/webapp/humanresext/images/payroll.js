
function onChangeEventListener(formObj,optionValue) {
	if("Monthly"==optionValue){
		Form.Element.disable('modifyLatestByDate');
		Form.Element.enable('modifyLatestBy');
		Form.Element.focus('modifyLatestBy');
	}else if("Yearly"==optionValue){
		Form.Element.enable('modifyLatestByDate');
		Form.Element.disable('modifyLatestBy');
		Form.Element.focus('modifyLatestByDate');
	}
	
}

function onChangeItemType(formObj,optionValue) {
	if("INPUT"!=optionValue){
		Form.Element.disable('minAmount');
		Form.Element.disable('maxAmount');
		Form.Element.focus('fromDate');
	}else{
		Form.Element.enable('minAmount');
		Form.Element.enable('maxAmount');
		Form.Element.focus('minAmount');
	}
}

function onChangeOperandType(obj) {
	obj.hide();
}


/*function onChangeOperandType(obj) {
	optionValue = obj.value;
	if("ENTITY-FIELD"==optionValue){
		$('operandTwo').hide();
	}else{
		$('operandTwo').show();		
	}
}  
*/