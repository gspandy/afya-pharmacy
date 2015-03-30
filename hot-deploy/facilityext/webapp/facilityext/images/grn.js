function getReceivingArea(buildingId) {
    if(buildingId.indexOf("Select")!=-1){
        return;
    }
    var result = false;
    var receivingAreas = null;
    var optionList = [];
    new Ajax.Request('getReceivingAreas?buildingId='+buildingId, {
        asynchronous: false,
        onSuccess: function(transport) {
            var data = transport.responseText.evalJSON(true);
            receivingAreas = data.receivingAreas;
            receivingAreas.each( function(receivingArea) {
                optionList.push("<option value = " + receivingArea + " > " + receivingArea + " </option>");
                });
                $('locationSeqId').update(optionList);
                result = true;
        }, requestHeaders: {Accept: 'application/x-json'}
    });
    return result;
}


function setLocCodeId(formObj,locCodeObj){
   
   var arr = $('BlindReceiptSearchResult').getElements(); 
   arr.each(function(item) {
       if(item.type=='select-one'){
        item.selectedIndex=locCodeObj.selectedIndex;
      }
   });
}   


function ProductUom(a,cf){
	this.uom=parseFloat(a);
	this.cf = parseFloat(cf);
	this.computeQty = computeQty;
	this.isValidLooseQty=isValidLooseQty;
}


function isValidLooseQty(l){

	if(parseFloat(l)>=this.uom){
		return false;
	}
	return true;
}

function computeQty(p,l){
	return (this.cf *( (this.uom*p) + parseFloat(l) ) );
}

  function computeReceivedQty(selectedIdx){
        var a =$F("packed_o_"+selectedIdx);
        var b =$F("loose_o_"+selectedIdx);
		
		if(a.length==0)
			a='0';
		
		if(b.length==0)
			b='0';
		
		
        var prodUom = ProductUoms[parseFloat(selectedIdx)];
		
		if(!prodUom.isValidLooseQty(b)){
			alert("Loose Quantity should be less than "+prodUom.uom);
			return;
		}

		var receivedQty =prodUom.computeQty(parseFloat(a),parseFloat(b));
        if(!isNaN(receivedQty)){
            $("receivedQty_o_"+selectedIdx).value=receivedQty;
        }
   }
    
    
 
    
    function call_accepted_sed(selectedIdx){
        
        var shipped = parseFloat($F("shippedQty_o_"+selectedIdx));
        var received = parseFloat($F("receivedQty_o_"+selectedIdx));
        var accepted = parseFloat($F("acceptedQty_o_"+selectedIdx));
        
        if(accepted > received){
        	alert('Accepted cannot be greater than Received Quantity.');
        	$("acceptedQty_o_"+selectedIdx).value='0';
        	return;
        }
        
        if(accepted == received){
        	disabled('SRJ_DAMAGED_'+selectedIdx);
        }
    }
    

	function computeAcceptedQty(damagedQty,selectedIdx){
		var receivedQty = $F("receivedQty_o_"+selectedIdx);
		var shippedQty = parseFloat($F("shippedQty_o_"+selectedIdx));

		if(!isNaN(damagedQty)){
			$("acceptedQty_o_"+selectedIdx).value=parseFloat(receivedQty)-parseFloat(damagedQty);
			$("SRJ_SHORTAGE_o_"+selectedIdx).value =parseFloat(shippedQty - receivedQty);
		}
	}


	function validateExpiry(selectedIdx,isLocal){
		//Set 1 day in milliseconds
		var one_day=1000*60*60*24;
		
		if($F("expiryDate_o_"+selectedIdx).length==0)
			return;
		
		if($F("productionDate_o_"+selectedIdx).length==0)
			return;
		
		
		parts = $F("expiryDate_o_"+selectedIdx).split("-");
		
		
		var expDate = new Date(parts[0],parseInt(parts[1]-1),parts[2]);
		
		parts = $F("productionDate_o_"+selectedIdx).split("-");
		
		var productionDate =  new Date(parts[0],parseInt(parts[1]-1),parts[2]);
		var today=new Date();
		
		if(productionDate.getTime()>=today.getTime()){
			alert("Production Date cannot be greater than Today's Date.");
			return;
		}
		
		if(expDate.getTime()<=today.getTime()){
			alert("Expiry Date cannot be lesser than Today's Date.");
			return;
		}
		
		var a = (expDate.getTime() - productionDate.getTime())/(3600*24);
		
		var b = (today.getTime() - productionDate.getTime())/(3600*24);
				
		var showErr=false;
		if(isLocal=="true"){
			if(b/a>0.5){
				showErr=true;
			}
		}else{
			if(b/a>0.2){
				showErr=true;
			}
		}

		if(showErr){
			$('alert_o_'+selectedIdx).setStyle({visibility: 'visible'});
			new Effect.Opacity(
			   'alert_o_'+selectedIdx, { 
				  from: 0.0, 
				  to: 1.0,
				  duration: 1.0
			   }
			);
			$("expiryViolation_o_"+selectedIdx).value="Y";
		}else{
			$('alert_o_'+selectedIdx).setStyle({visibility: 'hidden'});
			new Effect.Opacity(
			   'alert_o_'+selectedIdx, { 
				  from: 0.0, 
				  to: 1.0,
				  duration: 1.0
			   }
			);
		}
			
	}


	function validateReceivedQty(rowNum){
		
		a = $F("SRJ_EXCESS_o_"+rowNum);
		b = $F("SRJ_FREE_OF_COST_o_"+rowNum);
		c = $F("SRJ_SHORTAGE_o_"+rowNum);
		if(a.length==0)
			a='0';
		
		if(b.length==0)
			b='0';
		
		if(c.length==0)
			c='0';
		
			var receivedQty = parseFloat($F("receivedQty_o_"+rowNum));
			var shippedQty = parseFloat($F("shippedQty_o_"+rowNum));
			var excessQty  = parseFloat(a);
			var foc  = parseFloat(b);
			var shortage = parseFloat(c);
			
			var total = shippedQty+excessQty+foc-shortage;
			if(receivedQty!=total){
				alert("Sum of Shipped Qty + Excess + FOC - Shortage should be same as Recevied Qty.");
				return false;
			}
		return true;
	}
	
	function validateAcceptedQty(rowNum){
		var acceptedQty = parseFloat($F("acceptedQty_o_"+rowNum));
		var receivedQty = parseFloat($F("receivedQty_o_"+rowNum));
		
		var damagedQty = 0;
		if($F("SRJ_DAMAGED_o_"+rowNum).length>0)
			damagedQty = parseFloat($F("SRJ_DAMAGED_o_"+rowNum));
		
		if(acceptedQty!=(receivedQty - damagedQty)){
			{
				alert('Accepted Quantity is not equal to Received Qty minus Damaged Qty.');
				return false;
			}
		}
		return true;
	}
	
	function onShortageEntry(rowNum){
		a = $F("SRJ_EXCESS_o_"+rowNum);
		b = $F("SRJ_FREE_OF_COST_o_"+rowNum);
		if(a.length>0 || b.length>0){
			alert('Cannot Enter Shortage and Excess both.');
			$("SRJ_SHORTAGE_o_"+rowNum).value='';
			return;
		}
	}
	
	function onExcessEntry(rowNum){
		a = $F("SRJ_SHORTAGE_o_"+rowNum);
		if(a.length>0){
			alert('Cannot Enter Shortage and Excess both.');
			$("SRJ_EXCESS_o_"+rowNum).value='';
			$("SRJ_FREE_OF_COST_o_"+rowNum).value='';
			return;
		}
	}
