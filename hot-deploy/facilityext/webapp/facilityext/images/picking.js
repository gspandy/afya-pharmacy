
function quantityCheck(pickingSelectObj,quantityToPick,quantityPicked){
	pickingStatus = pickingSelectObj.value;
	
	quantityToPick = parseInt(quantityToPick);
	quantityPicked = parseInt(quantityPicked);
	if(pickingStatus=="PICKITEM_COMPLETED"){
		if(quantityPicked>quantityToPick || quantityPicked<quantityToPick){
			alert('Quantity Picked should be same as Quantity To Pick for it to be Complete.');
			pickingSelectObj.value="PICKITEM_PENDING";
		}
	}
	if(pickingStatus=="PICKITEM_SHORTPICK"){
		if(quantityPicked>quantityToPick || quantityPicked==quantityToPick){
			alert('Quantity Picked has to be less than Quantity To Pick for Short Close.');
			pickingSelectObj.value="PICKITEM_PENDING";
		}
	}
}

function chek(pickingSelectObj){
	var master_str = pickingSelectObj.name;
	var str1 = master_str.substring(12,16);
	var str2 = "_rowSubmit" + str1;
	document.getElementsByName(str2)[0].checked=true;
}

function updateQuantityForInventoryRes(formObj){
	
	quantityReserved = parseInt(formObj.quantity.value);
	quantityToCancel = parseInt(formObj.quantityToCancel.value);
	
	if(quantityToCancel>quantityReserved){
		alert('Can not cancel more than reserved quantity.');
		return false;
	}

	formObj.quantity.value=quantityReserved-quantityToCancel;	
	return true;
}
