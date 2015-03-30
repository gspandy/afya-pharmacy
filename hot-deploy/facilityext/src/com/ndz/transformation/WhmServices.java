package com.ndz.transformation;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.ndz.WhmConstants;

public class WhmServices {

	public final static String module = WhmServices.class.getName();

    public static final MathContext generalRounding = new MathContext(10);
    /***
     * @TODO : Check the security related code
     * @param dctx
     * @param context
     * @return
     */
    public static Map<String, Object> createProductBundleInventoryTransfer(DispatchContext dctx, Map<String, ? extends Object> context) {
        Delegator delegator =  dctx.getDelegator();
        String productBundleTxnId = (String) context.get("productBundleTxnId");
        GenericValue ProductBundleTxn = null;
        GenericValue ProductBundle = null;
        GenericValue InventoryItem = null;
        Timestamp MinExpiryDate = null;
        Map<String, Object> results = new HashMap<String, Object>();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String errorMsg = null;
        String inventoryItemId = null;
        String productId = null;
	    try{
        	try{
	        		ProductBundleTxn = delegator.findOne("ProductBundleTxn", false, "productBundleTxnId",productBundleTxnId);
	        }
	        catch (GenericEntityException e) {
	        	errorMsg = "Product Bundlle Transaction lookup problem [" + e.getMessage() + "]";
	        	throw e;
	        }

	        if (ProductBundleTxn == null) {
	        	errorMsg = "Cannot locate Product Bundlle Transaction.";
	        	throw new GenericEntityException ();
	        }


        	BigDecimal quantityToBundle = ProductBundleTxn.getBigDecimal("quantityToBundle");
        	if(quantityToBundle==null){
        		errorMsg = "Error while retrieving quantity to bundle for Bundle Transaction Id " + productBundleTxnId;
        		throw new GenericEntityException ();
        	}

	        try{
	        	ProductBundle = delegator.findOne("ProductBundle", false, "productBundleId",ProductBundleTxn.getString("productBundleId"));
	        	productId = ProductBundle.getString("makeSku");
	        }
	        catch (GenericEntityException e) {
	        	errorMsg = "Product Bundlle lookup problem [" + e.getMessage() + "]";
	        	throw e;
	        }

        	try{
	        	List<GenericValue> ProductBundleTxnDetailList =  ProductBundleTxn.getRelated("ProductBundleTxnDetail");
	        	for(GenericValue oProductBundleTxnDetail:ProductBundleTxnDetailList){
	        		inventoryItemId = oProductBundleTxnDetail.getString("inventoryItemId");
	        		InventoryItem =  delegator.findOne("InventoryItem", false, "inventoryItemId",inventoryItemId);
	        		Timestamp ExpireDate = InventoryItem.getTimestamp("expireDate");
	        		if(MinExpiryDate ==null || MinExpiryDate.after(ExpireDate) )
	        			MinExpiryDate = ExpireDate;
	        	}
        	}
        	catch (GenericEntityException e) {
        		errorMsg = "Inventory Item lookup problem [" + e.getMessage() + "]";
        		throw e;
            }
        	/**
        	 * Create Inventory Item
        	 **/
        	Map<String,? extends Object> createNewInventoryMap = UtilMisc.toMap("userLogin", userLogin,"inventoryItemTypeId","NON_SERIAL_INV_ITEM",
        																		"productId",productId,"facilityId",ProductBundleTxn.getString("facilityId"),
        																		"locationSeqId",ProductBundleTxn.getString("toLocationId"),"expireDate",MinExpiryDate);

        	 results = dctx.getDispatcher().runSync("createInventoryItem", createNewInventoryMap);
             if (ServiceUtil.isError(results)) {
            	 errorMsg = "Inventory Item Detail create problem in prepare inventory transfer";
            	 throw new GenericServiceException();
             }
        	/**
        	 * Create Inventory Item Detail
        	 **/
             Map<String,? extends Object> createNewInventoryDetailMap = UtilMisc.toMap("userLogin", userLogin,"inventoryItemId",results.get("inventoryItemId"),
            		 																	"quantityOnHandDiff",quantityToBundle,"availableToPromiseDiff",quantityToBundle,
            		 																	"productBundleTxnId",ProductBundleTxn.getString("productBundleTxnId"));
             results = dctx.getDispatcher().runSync("createInventoryItemDetail", createNewInventoryDetailMap);
             if (ServiceUtil.isError(results)) {
            	 errorMsg = "Inventory Item Detail create problem in prepare inventory transfer";
            	 throw new GenericServiceException();
             }

	    }
        catch (GenericServiceException e) {
        	results = ServiceUtil.returnError(errorMsg);
        	results.put("productBundleTxnId",productBundleTxnId);
        	results.put("inventoryItemId",inventoryItemId);
        	results.put("productId",productId);
        	return results ;

        }
        catch (GenericEntityException e) {
        	results = ServiceUtil.returnError(errorMsg);
        	results.put("productBundleTxnId",productBundleTxnId);
        	results.put("inventoryItemId",inventoryItemId);
        	results.put("productId",productId);
        	return results ;
        }
        results = ServiceUtil.returnSuccess();
    	results.put("productBundleTxnId",productBundleTxnId);
    	results.put("inventoryItemId",inventoryItemId);
    	results.put("productId",productId);
		return results;
    }

    public static Map<String, Object> createProductBundleTxnDetail (DispatchContext dctx, Map<String, ? extends Object> context) {
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	BigDecimal qtyToBundle  = new BigDecimal((String)context.get("quantityToBundle"));
    	String inventoryItemId = (String)context.get("inventoryItemId");
    	String productBundleTxnId =  (String)context.get("productBundleTxnId");
    	String productIdTo = (String)context.get("productIdTo");
    	GenericValue inventoryItem = null;
    	GenericValue productBundleTxn = null;
    	Map<String,Object> resultNew = new HashMap<String, Object>();
    	Map<String,Object> inventoryItemDetailResult = null;
    	resultNew.put("productBundleTxnId", productBundleTxnId);
    	String errorMsg = null;
    	GenericValue productBundle = null;
    	String productId = null;
    	String facilityId = null;
    	try{

    		try {
	    			productBundleTxn = delegator.findOne("ProductBundleTxn",false,"productBundleTxnId",productBundleTxnId);
	    		}
	    		catch (GenericEntityException e) {
	    				errorMsg = "Product Bundle Detail lookup problem [" + e.getMessage() + "]";
	    				throw e;
	    		}
	    		facilityId =  productBundleTxn.getString("facilityId");

	    	try {
	    			productBundle = delegator.getRelatedOne("ProductBundle", productBundleTxn);
	    		}
	    		catch (GenericEntityException e) {
	    			errorMsg = "Product Bundle lookup problem [" + e.getMessage() + "]";
	    			throw e;
	    		}

	    		productId =  productBundle.getString("makeSku");

	    	try {
					inventoryItem = delegator.findOne("InventoryItem",false,"inventoryItemId",inventoryItemId);
				}
	    		catch (GenericEntityException e) {
				 errorMsg = "Inventory Item lookup problem [" + e.getMessage() + "]";
				 throw e;
			}

	    	GenericValue txnDetail = delegator.makeValue("ProductBundleTxnDetail");
	    	txnDetail.put("productBundleTxnId",productBundleTxnId);
	    	txnDetail.put("inventoryItemId",inventoryItemId);
	    	txnDetail.put("baseSkuId",productIdTo);
	    	txnDetail.put("quantityToUse",qtyToBundle);

	    	if(inventoryItem.get("availableToPromiseTotal")==null ||  ((BigDecimal)inventoryItem.get("availableToPromiseTotal")).compareTo(qtyToBundle)==-1)
	    	{
	    		ServiceUtil.returnError("ATP of InventoryItem "+inventoryItemId + " has to be greater than or equal to Quantity To Bundle.",null,null,resultNew);
	    	}

	    	try {
					delegator.create(txnDetail);
				}
	    	catch (GenericEntityException e) {
				errorMsg = "Problem in creating Product Bundle detail [" + e.getMessage() + "]";
				throw e;
			}

			//DO Reservation
			try{
		    	Map<String,? extends Object> createNewInventoryDetail = UtilMisc.toMap("userLogin", userLogin,
																		"inventoryItemId",inventoryItemId,"quantityOnHandDiff",BigDecimal.ZERO,"availableToPromiseDiff",qtyToBundle.negate(),
																		"productBundleTxnId",productBundleTxnId);
		    	LocalDispatcher dispatcher = dctx.getDispatcher();
		    	inventoryItemDetailResult = dispatcher.runSync("createInventoryItemDetail",createNewInventoryDetail);
		    	if (ServiceUtil.isError(resultNew)) {
		    		throw new GenericServiceException();
		        }
			}
			catch (GenericServiceException e) {
				errorMsg = "Inventory store/create problem [" + e.getMessage() + "]";
				throw e;
	    	}
    	}
    	catch (GenericServiceException e) {
    		resultNew = ServiceUtil.returnError(errorMsg);
    		resultNew.put("productBundleTxnId", productBundleTxnId);
			resultNew.put("productId", productId);
			resultNew.put("facilityId", facilityId);
    		return resultNew;
    	}
    	catch (GenericEntityException e) {
    		resultNew = ServiceUtil.returnError(errorMsg);
    		resultNew.put("productBundleTxnId", productBundleTxnId);
			resultNew.put("productId", productId);
			resultNew.put("facilityId", facilityId);
    		return resultNew;
    	}
    resultNew = ServiceUtil.returnSuccess();
    resultNew.put("productBundleTxnId", productBundleTxnId);
	resultNew.put("productId", productId);
	resultNew.put("facilityId", facilityId);
    return resultNew;
    }

    public static Map<String, Object> removeProductBundleTxnDetail(DispatchContext dctx, Map<String, ? extends Object> context) throws GenericEntityException, GenericServiceException {
    	String productBundleTxnId = (String)context.get("productBundleTxnId");
    	String baseSkuId = (String)context.get("baseSkuId");
    	Delegator delegator = dctx.getDelegator();
    	GenericValue productBundleTxnDetail = delegator.findOne("ProductBundleTxnDetail", false, "productBundleTxnId",productBundleTxnId,"baseSkuId",baseSkuId);
    	GenericValue productBundleTxn = delegator.findOne("ProductBundleTxn",false,"productBundleTxnId",productBundleTxnId);
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	LocalDispatcher dispatcher = (LocalDispatcher) dctx.getDispatcher();
    	Map<String, Object> resultNew =  new HashMap<String, Object>();
    	String errorMsg = null;
    	/**
    	 * Create Inventory Item Detail
    	 **/
    	 try{
    		 if(WhmConstants.STATUS_COMPLETED.equalsIgnoreCase(productBundleTxn.getString("statusId"))){
    			 errorMsg = "Cannot Unreserve inventory Items after finalizing the product bundle"	;
    			 throw new GenericServiceException();
    	    	}
	         Map<String,? extends Object> createNewInventoryDetailMap = UtilMisc.toMap("userLogin", userLogin,"inventoryItemId",productBundleTxnDetail.getString("inventoryItemId"),
	        		 																	"quantityOnHandDiff",BigDecimal.ZERO,"availableToPromiseDiff",productBundleTxnDetail.getBigDecimal("quantityToUse"),
	        		 																	"productBundleTxnId",productBundleTxnId);
	    	 resultNew = dispatcher.runSync("createInventoryItemDetail", createNewInventoryDetailMap);
	         if (ServiceUtil.isError(resultNew)) {
	        	 errorMsg = "Inventory Detail store/create problem";
	        	 throw new GenericServiceException();
	         }
    	 }
    	 catch (GenericServiceException e1) {
    		 resultNew = ServiceUtil.returnError(errorMsg +   "[" + e1.getMessage() + "]");
    		 resultNew.put("productBundleTxnId", productBundleTxnId);
    		 resultNew.put ("productId",baseSkuId);
    		 return resultNew;
         	}
    	/**
    	 * Delete from prodbundletxndtl
    	 * */
    	productBundleTxnDetail.remove();
    	resultNew = ServiceUtil.returnSuccess("Inventory Item Unreserved Successfully");
    	resultNew.put("productBundleTxnId", productBundleTxnId);
		resultNew.put ("productId",baseSkuId);
    	return resultNew;
    }


}

