
		import org.ofbiz.entity.*;
		import org.ofbiz.base.util.*;
		import org.ofbiz.entity.condition.*;
        import com.ndz.util.ProductHelper;
		String sortField;
		orderId =  request.getParameter("orderId")
		String reserveByLogic =  request.getParameter("reserveByLogic")
		reserveOrderList = [] as ArrayList
		inventoryItemList = [] as ArrayList
        BigDecimal addedQty = BigDecimal.ZERO; 
        BigDecimal quantityToReserved = BigDecimal.ZERO; 
        BigDecimal quantityAlreadyReserved = BigDecimal.ZERO; 
		switch (reserveByLogic) {
				case "1":
					sortField = "datetimeReceived";
					break;
				case "2":
					sortField = "expireDate";
					break;
				case "3":
					sortField = "-datetimeReceived";
					break;
				case "4":
					sortField = "-expireDate";
					break;
		}
	
			reserveOrderList = delegator.findList("ReserveOrderItem",
				           EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId), null, null, null, false);
		           
              
		     reserveOrderList.each{orderItem->
		     	
                BigDecimal orderQuantity = new BigDecimal(orderItem.quantity);
                
		     	reserveOrderItemInvResList = delegator.findList("ReserveOrderItemInvRes",
				           EntityCondition.makeCondition("reserveOrderId", EntityOperator.EQUALS, orderItem.orderId), null, null, null, false);
			    
			    for(int i = 0 ; i < reserveOrderItemInvResList.size() ; i++){
			    	quantityAlreadyReserved = quantityAlreadyReserved + reserveOrderItemInvResList.get(i).quantity ;
			    }
				
                
                if(quantityAlreadyReserved.compareTo(BigDecimal.ZERO) > 0){
                	
                	orderQuantity = orderQuantity - quantityAlreadyReserved;
                }
				
				String productIdStr = orderItem.productId;
		     BigDecimal getAtpFor = ProductHelper.getAtpFor(productIdStr,delegator);
		                
		                
		     inventoryItemList = delegator.findList("InventoryItem",
		                    EntityCondition.makeCondition("productId", EntityOperator.EQUALS, orderItem.productId ),null, [sortField], null, false);
		      
		     for(int i = 0 ; i < inventoryItemList.size() ; i++) {
		     	
		     	  BigDecimal quantityInInv =  inventoryItemList.get(i).availableToPromiseTotal;
		          
		          if(quantityInInv.compareTo(BigDecimal.ZERO) > 0 && orderQuantity.compareTo(BigDecimal.ZERO) > 0){
		          
		          print "\n\n\n orderQuantity.compareTo(quantityInInv) \n\n\n" + orderQuantity.compareTo(quantityInInv) + "\n\n\n";
		          
		          if(orderQuantity.compareTo(quantityInInv) > BigDecimal.ZERO){
		          	orderQuantity = orderQuantity - quantityInInv;
		          	quantityToReserved = quantityInInv.abs();
		          }else{
		          	orderQuantity = quantityToReserved - orderQuantity;
		          	quantityToReserved = orderQuantity.abs();
		          }
		          
		          print "\n\n\n quantityToReserved \n\n\n" + quantityToReserved + "\n\n\n";
		          
				  dispatcher.runSync("reserveReserveOrderItemInventory",
							[userLogin:userLogin,
							 reserveOrderId:orderId,
							 reserveOrderItemSeqId:orderItem.orderItemSeqId,
							 inventoryItemId : inventoryItemList.get(i).inventoryItemId,
							 quantity        : quantityToReserved ]);
							
			      dispatcher.runSync("createInventoryItemDetail",
	    	               [inventoryItemId : inventoryItemList.get(i).inventoryItemId,
	    	               quantityOnHandDiff: BigDecimal.ZERO,
	    	               availableToPromiseDiff: quantityToReserved.negate(),
	    	               userLogin: userLogin ]);
				  
		          if(orderQuantity.compareTo(BigDecimal.ZERO) < 1) break;
		        
		        } 
		          
		        
		         
		       }
		       
		       
		    }
	
	return "success"	
		    
		    