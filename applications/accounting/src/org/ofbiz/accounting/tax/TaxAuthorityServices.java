/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.ofbiz.accounting.tax;

import javolution.util.FastList;
import javolution.util.FastSet;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

/**
 * Tax Authority tax calculation and other misc services
 */

public class TaxAuthorityServices {

    public static final String module = TaxAuthorityServices.class.getName();
    public static final BigDecimal ZERO_BASE = BigDecimal.ZERO;
    public static final BigDecimal ONE_BASE = BigDecimal.ONE;
    public static final BigDecimal PERCENT_SCALE = new BigDecimal("100.000");
    //This flag is to test tax calculation for Product that include VAT.
    public static final Boolean traditional=false;

    public static int salestaxFinalDecimals = UtilNumber.getBigDecimalScale("salestax.final.decimals");
    public static int salestaxCalcDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    public static int salestaxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");

    public static Map rateProductTaxCalcForDisplay(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        String productStoreId = (String) context.get("productStoreId");
        String billToPartyId = (String) context.get("billToPartyId");
        String productId = (String) context.get("productId");
        BigDecimal quantity = (BigDecimal) context.get("quantity");
        BigDecimal basePrice = (BigDecimal) context.get("basePrice");
        BigDecimal shippingPrice = (BigDecimal) context.get("shippingPrice");
        String taxAuthorityType = (String) context.get("taxAuthorityType");
        if (quantity == null)
            quantity = ONE_BASE;
        BigDecimal amount = basePrice.multiply(quantity);

        BigDecimal taxTotal = ZERO_BASE;
        BigDecimal taxPercentage = ZERO_BASE;
        BigDecimal priceWithTax = basePrice;
        if (shippingPrice != null)
            priceWithTax = priceWithTax.add(shippingPrice);

        try {
            GenericValue product = delegator.findByPrimaryKeyCache("Product", UtilMisc.toMap("productId", productId));
            GenericValue productStore =
                    delegator.findByPrimaryKeyCache("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
            if (productStore == null) {
                throw new IllegalArgumentException("Could not find ProductStore with ID [" + productStoreId
                        + "] for tax calculation");
            }

            if ("Y".equals(productStore.getString("showPricesWithVatTax"))) {
                Set taxAuthoritySet = FastSet.newInstance();
                if (productStore.get("vatTaxAuthPartyId") == null) {
                    List taxAuthorityRawList = null;
                    if (taxAuthorityType != null) {
                        taxAuthorityRawList =
                                delegator.findList("TaxAuthority", EntityCondition.makeCondition(EntityCondition
                                                .makeCondition("taxAuthGeoId", productStore.get("vatTaxAuthGeoId")), EntityCondition
                                                .makeCondition("taxAuthorityType", EntityOperator.EQUALS, taxAuthorityType)), null,
                                        null, null, true);
                    } else {
                        taxAuthorityRawList =
                                delegator.findList(
                                        "TaxAuthority",
                                        EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.EQUALS,
                                                productStore.get("vatTaxAuthGeoId")), null, null, null, true);
                    }
                    taxAuthoritySet.addAll(taxAuthorityRawList);
                } else {
                    GenericValue taxAuthority = null;
                    if (taxAuthorityType != null) {
                        taxAuthority =
                                delegator.findByPrimaryKeyCache("TaxAuthority", UtilMisc.toMap("taxAuthGeoId",
                                        productStore.get("vatTaxAuthGeoId"), "taxAuthPartyId",
                                        productStore.get("vatTaxAuthPartyId"), "taxAuthorityType", taxAuthorityType));
                    } else {
                        taxAuthority =
                                delegator.findByPrimaryKeyCache("TaxAuthority", UtilMisc.toMap("taxAuthGeoId",
                                        productStore.get("vatTaxAuthGeoId"), "taxAuthPartyId",
                                        productStore.get("vatTaxAuthPartyId")));
                    }
                    taxAuthoritySet.add(taxAuthority);
                }

                if (taxAuthoritySet.size() == 0) {
                    throw new IllegalArgumentException("Could not find any Tax Authories for store with ID ["
                            + productStoreId + "] for tax calculation; the store settings may need to be corrected.");
                }

                // FIX ME CFormAvailable is fixed to false
                List taxAdustmentList =
                        getTaxAdjustments(delegator, product, productStore, null, billToPartyId, taxAuthoritySet,
                                basePrice, amount, shippingPrice, ZERO_BASE, null, false);
                if (taxAdustmentList.size() == 0) {
                    // this is something that happens every so often for
                    // different products and such, so don't blow up on
                    // it...
                    Debug.logWarning("Could not find any Tax Authories Rate Rules for store with ID [" + productStoreId
                            + "], productId [" + productId + "], basePrice [" + basePrice + "], amount [" + amount
                            + "], for tax calculation; the store settings may need to be corrected.", module);
                }

                // add up amounts from adjustments (amount OR exemptAmount,
                // sourcePercentage)
                Iterator taxAdustmentIter = taxAdustmentList.iterator();
                while (taxAdustmentIter.hasNext()) {
                    GenericValue taxAdjustment = (GenericValue) taxAdustmentIter.next();
                    taxPercentage = taxPercentage.add(taxAdjustment.getBigDecimal("sourcePercentage"));
                    BigDecimal adjAmount = taxAdjustment.getBigDecimal("amount");
                    taxTotal = taxTotal.add(adjAmount);
                    priceWithTax = priceWithTax.add(adjAmount.divide(quantity, salestaxCalcDecimals, salestaxRounding));
                    Debug.logInfo(
                            "For productId [" + productId + "] added ["
                                    + adjAmount.divide(quantity, salestaxCalcDecimals, salestaxRounding)
                                    + "] of tax to price for geoId [" + taxAdjustment.getString("taxAuthGeoId")
                                    + "], new price is [" + priceWithTax + "]", module);
                }
            }
        } catch (GenericEntityException e) {
            String errMsg = "Data error getting tax settings: " + e.toString();
            Debug.logError(e, errMsg, module);
            return ServiceUtil.returnError(errMsg);
        }

        // round to 2 decimal places for display/etc
        taxTotal = taxTotal.setScale(salestaxFinalDecimals, salestaxRounding);
        priceWithTax = priceWithTax.setScale(salestaxFinalDecimals, salestaxRounding);

        Map result = ServiceUtil.returnSuccess();
        result.put("taxTotal", taxTotal);
        result.put("taxPercentage", taxPercentage);
        result.put("priceWithTax", priceWithTax);
        return result;
    }

    public static Map rateProductTaxCalc(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        String productStoreId = (String) context.get("productStoreId");
        String payToPartyId = (String) context.get("payToPartyId");// Sales it
        String billToPartyId = (String) context.get("billToPartyId");
        List itemProductList = (List) context.get("itemProductList");
        List itemAmountList = (List) context.get("itemAmountList");
        List itemPriceList = (List) context.get("itemPriceList");
        List itemShippingList = (List) context.get("itemShippingList");
        String taxAuthorityType = (String) context.get("taxAuthorityType");
        BigDecimal orderShippingAmount = (BigDecimal) context.get("orderShippingAmount");
        BigDecimal orderPromotionsAmount = (BigDecimal) context.get("orderPromotionsAmount");
        GenericValue shippingAddress = (GenericValue) context.get("shippingAddress");
        GenericValue shippingOriginAddress = (GenericValue) context.get("shippingOriginAddress");
        String taxForm = (String) context.get("taxForm");

        if (shippingAddress == null || shippingOriginAddress == null
                || shippingAddress.get("stateProvinceGeoId") == null
                || shippingOriginAddress.get("stateProvinceGeoId") == null) {
            return ServiceUtil
                    .returnError("The address(es) used for tax calculation did not have State/Province or Country or other tax jurisdiction values set, so we cannot determine the taxes to charge.");
        }

        boolean vatApplicable = false;
        if (shippingAddress.get("countryGeoId").equals(shippingOriginAddress.get("countryGeoId"))) {
            vatApplicable = true;
        }

        // without knowing the TaxAuthority parties, just find all TaxAuthories
        // for the set of IDs...
        Set taxAuthoritySet = FastSet.newInstance();
        GenericValue productStore = null;
        // Check value productStore *** New
        if (productStoreId != null) {
            try {
                getTaxAuthorities(delegator, shippingAddress, taxAuthoritySet, taxAuthorityType, billToPartyId,
                        shippingOriginAddress);
                if (productStoreId != null) {
                    productStore =
                            delegator.findByPrimaryKey("ProductStore", UtilMisc.toMap("productStoreId", productStoreId));
                }

            } catch (GenericEntityException e) {
                String errMsg = "Data error getting tax settings: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }

            if (productStore == null && payToPartyId == null) {
                throw new IllegalArgumentException("Could not find payToPartyId [" + payToPartyId
                        + "] or ProductStore [" + productStoreId + "] for tax calculation");
            }
        } else {
            try {
                getTaxAuthorities(delegator, shippingAddress, taxAuthoritySet, taxAuthorityType, payToPartyId,
                        shippingOriginAddress);
            } catch (GenericEntityException e) {
                String errMsg = "Data error getting tax settings: " + e.toString();
                Debug.logError(e, errMsg, module);
                return ServiceUtil.returnError(errMsg);
            }
        }

        // Setup the return lists.
        List orderAdjustments = FastList.newInstance();
        List itemAdjustments = FastList.newInstance();

        // Loop through the products; get the taxCategory; and lookup each in
        // the cache.
        for (int i = 0; i < itemProductList.size(); i++) {
            GenericValue product = (GenericValue) itemProductList.get(i);
            BigDecimal itemAmount = (BigDecimal) itemAmountList.get(i);
            BigDecimal itemPrice = (BigDecimal) itemPriceList.get(i);
            BigDecimal shippingAmount = (BigDecimal) itemShippingList.get(i);
            List taxList = null;
            if (shippingAddress != null) {
                taxList =
                        getTaxAdjustments(delegator, product, productStore, payToPartyId, billToPartyId, taxAuthoritySet,
                                itemPrice, itemAmount, shippingAmount, ZERO_BASE, taxForm, vatApplicable);
            }
            // this is an add and not an addAll because we want a List of Lists
            // of GenericValues, one List of Adjustments
            // per item
            itemAdjustments.add(taxList);
        }
        if (orderShippingAmount != null && orderShippingAmount.compareTo(BigDecimal.ZERO) > 0) {
            List taxList =
                    getTaxAdjustments(delegator, null, productStore, payToPartyId, billToPartyId, taxAuthoritySet,
                            ZERO_BASE, ZERO_BASE, orderShippingAmount, ZERO_BASE, taxForm, vatApplicable);
            orderAdjustments.addAll(taxList);
        }
        if (orderPromotionsAmount != null && orderPromotionsAmount.compareTo(BigDecimal.ZERO) != 0) {
            List taxList =
                    getTaxAdjustments(delegator, null, productStore, payToPartyId, billToPartyId, taxAuthoritySet,
                            ZERO_BASE, ZERO_BASE, ZERO_BASE, orderPromotionsAmount, taxForm, vatApplicable);
            orderAdjustments.addAll(taxList);
        }

        Map result = ServiceUtil.returnSuccess();
        result.put("orderAdjustments", orderAdjustments);
        result.put("itemAdjustments", itemAdjustments);

        return result;
    }

    private static void getTaxAuthorities(Delegator delegator, GenericValue shippingAddress, Set taxAuthoritySet,
                                          String taxAuthorityType, String originPartyGroup, GenericValue shippingOriginAddress)
            throws GenericEntityException {
        Set geoIdSet = FastSet.newInstance();

        if (shippingAddress != null) {
            if (UtilValidate.isNotEmpty(shippingAddress.getString("countryGeoId"))) {
                if (!shippingOriginAddress.getString("stateProvinceGeoId").equals(
                        shippingAddress.getString("stateProvinceGeoId"))) {
                    if ("SALES".equals(taxAuthorityType))
                        geoIdSet.add(shippingOriginAddress.getString("countryGeoId"));
                    else
                        geoIdSet.add(shippingAddress.getString("countryGeoId"));
                }
            }
            if (UtilValidate.isNotEmpty(shippingAddress.getString("stateProvinceGeoId"))) {
                if (shippingOriginAddress.getString("stateProvinceGeoId").equals(
                        shippingAddress.getString("stateProvinceGeoId")))
                    geoIdSet.add(shippingAddress.getString("stateProvinceGeoId"));
            }
        } else {
            Debug.logWarning("shippingAddress was null, adding nothing to taxAuthoritySet", module);
        }
        EntityCondition mainCondition =
                EntityCondition.makeCondition(EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.IN, geoIdSet));
        List taxAuthorityRawList = null;
        if (taxAuthorityType != null) {
            taxAuthorityRawList = delegator.findList("TaxAuthority", mainCondition, null, null, null, false);
        } else {
            taxAuthorityRawList =
                    delegator.findList("TaxAuthority",
                            EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.IN, geoIdSet), null, null, null, true);
        }
        taxAuthoritySet.addAll(taxAuthorityRawList);
        System.out.println(" Tax Authority Set " + taxAuthoritySet);
    }

    private static List getTaxAdjustments(Delegator delegator, GenericValue product, GenericValue productStore,
                                          String payToPartyId, String billToPartyId, Set taxAuthoritySet, BigDecimal itemPrice, BigDecimal itemAmount,
                                          BigDecimal shippingAmount, BigDecimal orderPromotionsAmount, String taxForm, boolean vatApplicable) {
        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        List adjustments = FastList.newInstance();
        boolean pricesIncludeTax=false;
        if (payToPartyId == null) {
            if (productStore != null) {
                payToPartyId = productStore.getString("payToPartyId");
            }
        }

        // store expr
        EntityCondition storeCond = null;
        if (productStore != null) {
            storeCond =
                    EntityCondition.makeCondition(
                            EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS,
                                    productStore.get("productStoreId")), EntityOperator.OR,
                            EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, null));
            storeCond =
                    EntityCondition.makeCondition(storeCond, EntityCondition.makeCondition(
                            EntityCondition.makeCondition("dutyType", "SALES"), EntityOperator.OR,
                            EntityCondition.makeCondition("dutyType", "BOTH")));
            if("Y".equals(productStore.getString("pricesIncludeTax")))
                pricesIncludeTax=true;
        } else {

            storeCond = EntityCondition.makeCondition("productStoreId", EntityOperator.EQUALS, null);
            storeCond =
                    EntityCondition.makeCondition(storeCond, EntityCondition.makeCondition(
                            EntityCondition.makeCondition("dutyType", "PURCHASES"), EntityOperator.OR,
                            EntityCondition.makeCondition("dutyType", "BOTH")));
            if("PRICEINCLUDETAX".equals(taxForm)){
                pricesIncludeTax=true;
            }
        }

        EntityCondition rateTypeCond = null;
        if (!vatApplicable) {
            rateTypeCond = EntityCondition.makeCondition("taxAuthorityRateTypeId", productStore == null ? "IMPORT_TAX" : "EXPORT_TAX");
        }else{
            rateTypeCond = EntityCondition.makeCondition("taxAuthorityRateTypeId", "VAT_TAX");
        }

        // build the TaxAuthority expressions (taxAuthGeoId, taxAuthPartyId)
        List taxAuthCondOrList = FastList.newInstance();
        // start with the _NA_ TaxAuthority...
        // taxAuthCondOrList.add(EntityCondition.makeCondition(EntityCondition.makeCondition("taxAuthPartyId",
        // EntityOperator.EQUALS, "_NA_"), EntityOperator.AND,
        // EntityCondition.makeCondition("taxAuthGeoId",
        // EntityOperator.EQUALS, "_NA_")));

        Iterator taxAuthorityIter = taxAuthoritySet.iterator();
        while (taxAuthorityIter.hasNext()) {
            GenericValue taxAuthority = (GenericValue) taxAuthorityIter.next();
            EntityCondition taxAuthCond =
                    EntityCondition.makeCondition(
                            EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS,
                                    taxAuthority.getString("taxAuthPartyId")),
                            EntityOperator.AND,
                            EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.EQUALS,
                                    taxAuthority.getString("taxAuthGeoId")));
            taxAuthCondOrList.add(taxAuthCond);
        }
        EntityCondition taxAuthoritiesCond = EntityCondition.makeCondition(taxAuthCondOrList, EntityOperator.OR);

        try {
            EntityCondition productCategoryCond = null;
            if (product != null) {
                // find the tax categories associated with the product and
                // filter by those, with an IN clause or some such
                // question: get all categories, or just a special type? for now
                // let's do all categories...
                Set productCategoryIdSet = FastSet.newInstance();
                List pcmList =
                        delegator.findByAndCache("ProductCategoryMember",
                                UtilMisc.toMap("productId", product.get("productId")));
                pcmList = EntityUtil.filterByDate(pcmList, true);
                Iterator pcmIter = pcmList.iterator();
                while (pcmIter.hasNext()) {
                    GenericValue pcm = (GenericValue) pcmIter.next();
                    productCategoryIdSet.add(pcm.get("productCategoryId"));
                }

                if (productCategoryIdSet.size() == 0) {
                    productCategoryCond =
                            EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, null);
                } else {
                    // REMOVED
                    // EntityCondition.makeCondition("productCategoryId",
                    // EntityOperator.EQUALS, null),
                    // EntityOperator.OR,
                    productCategoryCond =
                            EntityCondition.makeCondition(EntityCondition.makeCondition("productCategoryId",
                                    EntityOperator.IN, productCategoryIdSet));
                }
            } else {
                productCategoryCond = EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, null);
            }

            // build the main condition clause
            List mainExprs = null;
            if (UtilValidate.isNotEmpty(taxAuthCondOrList)) {
                mainExprs = UtilMisc.toList(storeCond, taxAuthoritiesCond);
            } else {
                mainExprs = UtilMisc.toList(storeCond);
            }
            mainExprs.add(rateTypeCond);
            mainExprs.add(EntityCondition.makeCondition("taxAuthorityRateTypeId", EntityOperator.NOT_EQUAL,
                    "EXCISE_TAX"));
            mainExprs.add(EntityCondition.makeCondition("methodOfCalc", EntityOperator.EQUALS, "ON_ASSESSABLE_VALUE"));
            mainExprs.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition("minItemPrice", EntityOperator.EQUALS, null), EntityOperator.OR,
                    EntityCondition.makeCondition("minItemPrice", EntityOperator.LESS_THAN_EQUAL_TO, itemPrice)));
            mainExprs.add(EntityCondition.makeCondition(
                    EntityCondition.makeCondition("minPurchase", EntityOperator.EQUALS, null), EntityOperator.OR,
                    EntityCondition.makeCondition("minPurchase", EntityOperator.LESS_THAN_EQUAL_TO, itemAmount)));

            EntityCondition mainCondition = null;

            if ("C Form".equals(taxForm)) {
                EntityCondition cond =
                        EntityCondition
                                .makeCondition("taxPercentage", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(2));
                List mainExprsCopy = new ArrayList(mainExprs);
                mainExprsCopy.add(productCategoryCond);
                mainExprsCopy.add(cond);
                mainCondition = EntityCondition.makeCondition(mainExprsCopy, EntityJoinOperator.AND);
            } else if ("H Form".equals(taxForm)) {
                EntityCondition cond =
                        EntityCondition
                                .makeCondition("taxPercentage", EntityOperator.LESS_THAN_EQUAL_TO, new BigDecimal(0));
                List mainExprsCopy = new ArrayList(mainExprs);
                mainExprsCopy.add(cond);
                mainCondition = EntityCondition.makeCondition(mainExprsCopy, EntityJoinOperator.AND);
            } else {
                List mainExprsCopy = new ArrayList(mainExprs);
                mainExprsCopy.add(productCategoryCond);
                mainCondition = EntityCondition.makeCondition(mainExprsCopy, EntityJoinOperator.AND);
            }

            // create the orderby clause
            List orderList = UtilMisc.toList("minItemPrice", "minPurchase", "fromDate");

            // finally ready... do the rate query
            System.out.println(" Finding the TaxAuthority " + mainCondition);
            List lookupList =
                    delegator.findList("TaxAuthorityRateProduct", mainCondition, null, orderList, null, false);
            List filteredList = EntityUtil.filterByDate(lookupList, true);
            if (filteredList.size() == 0) {
                Debug.logWarning(
                        "In TaxAuthority Product Rate no records were found for condition:" + mainCondition.toString(),
                        module);
                // IF FORM C available and there is no tax rate less than CST 2
                // %, try to apply the tax authority with 2%.
                if ("C Form".equals(taxForm)) {
                    EntityCondition cond =
                            EntityCondition.makeCondition("taxPercentage", EntityOperator.EQUALS, new BigDecimal(2));
                    List mainExprsCopy = new ArrayList(mainExprs);
                    mainExprsCopy.add(cond);
                    mainCondition = EntityCondition.makeCondition(mainExprsCopy, EntityOperator.AND);
                    lookupList =
                            delegator.findList("TaxAuthorityRateProduct", mainCondition, null, orderList, null, false);
                    filteredList = EntityUtil.filterByDate(lookupList, true);
                } else if ("H Form".equals(taxForm)) {
                    EntityCondition cond =
                            EntityCondition.makeCondition("taxPercentage", EntityOperator.EQUALS, new BigDecimal(0));
                    List mainExprsCopy = new ArrayList(mainExprs);
                    mainExprsCopy.add(cond);
                    mainCondition = EntityCondition.makeCondition(mainExprsCopy, EntityOperator.AND);
                    lookupList =
                            delegator.findList("TaxAuthorityRateProduct", mainCondition, null, orderList, null, false);
                    filteredList = EntityUtil.filterByDate(lookupList, true);
                }
                if (filteredList.size() == 0) {
                    Debug.logWarning("In TaxAuthority Product Rate no records were found for condition:"
                            + mainCondition.toString(), module);
                    return adjustments;
                }
            }

            // find the right entry(s) based on purchase amount
            Iterator flIt = filteredList.iterator();
            while (flIt.hasNext()) {
                GenericValue taxAuthorityRateProduct = (GenericValue) flIt.next();
                BigDecimal taxRate =
                        taxAuthorityRateProduct.get("taxPercentage") != null ? taxAuthorityRateProduct
                                .getBigDecimal("taxPercentage") : ZERO_BASE;
                BigDecimal taxable = ZERO_BASE;

                if (product != null
                        && (product.get("taxable") == null || (product.get("taxable") != null && product.getBoolean(
                        "taxable").booleanValue()))) {
                    taxable = taxable.add(itemAmount);
                }

                if (shippingAmount != null
                        && taxAuthorityRateProduct != null
                        && (taxAuthorityRateProduct.get("taxShipping") == null || (taxAuthorityRateProduct
                        .get("taxShipping") != null && taxAuthorityRateProduct.getBoolean("taxShipping").booleanValue()))) {
                    taxable = taxable.add(shippingAmount);
                }
                if (orderPromotionsAmount != null
                        && taxAuthorityRateProduct != null
                        && (taxAuthorityRateProduct.get("taxPromotions") == null || (taxAuthorityRateProduct
                        .get("taxPromotions") != null && taxAuthorityRateProduct.getBoolean("taxPromotions")
                        .booleanValue()))) {
                    taxable = taxable.add(orderPromotionsAmount);
                }

                // if (taxable.compareTo(BigDecimal.ZERO) == 0) {
                // // this should make it less confusing if the taxable flag on
                // the product is not Y/true, and there is no
                // // shipping and such
                // continue;
                // }

                // taxRate is in percentage, so needs to be divided by 100
                System.out.println(" Taxable Amount " + taxable);
                BigDecimal taxAmount = null;
                if(!pricesIncludeTax)
                     taxAmount =
                        (taxable.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
                else{
                    BigDecimal subTotal = taxable.divide(new BigDecimal(1).add(taxRate.divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding)),salestaxCalcDecimals, salestaxRounding);
                    taxAmount = taxable.subtract(subTotal);
                }
                String taxAuthGeoId = taxAuthorityRateProduct.getString("taxAuthGeoId");
                String taxAuthPartyId = taxAuthorityRateProduct.getString("taxAuthPartyId");
                // get glAccountId from TaxAuthorityGlAccount entity using the
                // payToPartyId as the organizationPartyId
                String taxAuthGlAccountId = taxAuthorityRateProduct.getString("glAccountId");
                if (taxAuthGlAccountId == null) {
                    // TODO: what to do if no TaxAuthorityGlAccount found? Use
                    // some default, or is that done elsewhere later
                    // on?
                    throw new RuntimeException("No TaxAuthorityGlAccount found");
                }

                GenericValue adjValue = delegator.makeValue("OrderAdjustment");
                adjValue.set("taxAuthorityRateSeqId", taxAuthorityRateProduct.getString("taxAuthorityRateSeqId"));
                taxAmount = taxAmount.setScale(salestaxFinalDecimals, salestaxRounding);
                adjValue.set("amount", taxAmount);
                System.out.println(" Tax Amount " + taxAmount);
                adjValue.set("sourcePercentage", taxRate);
                adjValue.set("orderAdjustmentTypeId", "SALES_TAX");
                adjValue.set("primaryGeoId", taxAuthGeoId);
                adjValue.set("comments", taxAuthorityRateProduct.getString("description"));
                if (taxAuthPartyId != null)
                    adjValue.set("taxAuthPartyId", taxAuthPartyId);
                if (taxAuthGlAccountId != null)
                    adjValue.set("overrideGlAccountId", taxAuthGlAccountId);
                if (taxAuthGeoId != null)
                    adjValue.set("taxAuthGeoId", taxAuthGeoId);
                adjustments.add(adjValue);
                List<GenericValue> taxAuthorityAssocList =
                        delegator.findByAndCache("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthGeoId", taxAuthGeoId,
                                "taxAuthPartyId", taxAuthPartyId, "methodOfCalc", "ON_TOT_VAT", "dutyType",
                                taxAuthorityRateProduct.getString("dutyType")), UtilMisc.toList("-fromDate"));
                taxAuthorityAssocList = EntityUtil.filterByDate(taxAuthorityAssocList, true);
                for (GenericValue surchargeTax : taxAuthorityAssocList) {
                    taxRate =
                            surchargeTax.get("taxPercentage") != null ? surchargeTax.getBigDecimal("taxPercentage")
                                    : ZERO_BASE;
                    BigDecimal cessAmount =
                            (taxAmount.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
                    adjValue = delegator.makeValue("OrderAdjustment");
                    adjValue.set("taxAuthorityRateSeqId", surchargeTax.getString("taxAuthorityRateSeqId"));
                    cessAmount = cessAmount.setScale(salestaxFinalDecimals, salestaxRounding);
                    adjValue.set("amount", cessAmount);
                    System.out.println(" Tax Amount " + cessAmount);
                    adjValue.set("sourcePercentage", taxRate);
                    adjValue.set("orderAdjustmentTypeId", "SURCHARGE_ADJUSTMENT");
                    adjValue.set("primaryGeoId", taxAuthGeoId);
                    adjValue.set("comments", surchargeTax.getString("description"));
                    if (taxAuthPartyId != null)
                        adjValue.set("taxAuthPartyId", surchargeTax.get("taxAuthPartyId"));
                    if (taxAuthGlAccountId != null)
                        adjValue.set("overrideGlAccountId", surchargeTax.get("glAccountId"));
                    if (taxAuthGeoId != null)
                        adjValue.set("taxAuthGeoId", surchargeTax.get("taxAuthGeoId"));
                    adjustments.add(adjValue);
                }
            }
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up tax rates", module);
            return new ArrayList();
        }

        return adjustments;
    }

    private static void handlePartyTaxExempt(GenericValue adjValue, Set billToPartyIdSet, String taxAuthGeoId,
                                             String taxAuthPartyId, BigDecimal taxAmount, Timestamp nowTimestamp, Delegator delegator)
            throws GenericEntityException {
        Debug.logInfo("Checking for tax exemption : " + taxAuthGeoId + " / " + taxAuthPartyId, module);
        List ptiConditionList =
                UtilMisc.toList(EntityCondition.makeCondition("partyId", EntityOperator.IN, billToPartyIdSet),
                        EntityCondition.makeCondition("taxAuthGeoId", EntityOperator.EQUALS, taxAuthGeoId),
                        EntityCondition.makeCondition("taxAuthPartyId", EntityOperator.EQUALS, taxAuthPartyId));
        ptiConditionList
                .add(EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN_EQUAL_TO, nowTimestamp));
        ptiConditionList.add(EntityCondition.makeCondition(
                EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null), EntityOperator.OR,
                EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, nowTimestamp)));
        EntityCondition ptiCondition = EntityCondition.makeCondition(ptiConditionList, EntityOperator.AND);
        // sort by -fromDate to get the newest (largest) first, just in case
        // there is more than one, we only want the most
        // recent valid one, should only be one per jurisdiction...
        List partyTaxInfos =
                delegator.findList("PartyTaxAuthInfo", ptiCondition, null, UtilMisc.toList("-fromDate"), null, false);

        boolean foundExemption = false;
        if (partyTaxInfos.size() > 0) {
            GenericValue partyTaxInfo = (GenericValue) partyTaxInfos.get(0);
            adjValue.set("customerReferenceId", partyTaxInfo.get("partyTaxId"));
            if ("Y".equals(partyTaxInfo.getString("isExempt"))) {
                adjValue.set("amount", BigDecimal.ZERO);
                adjValue.set("exemptAmount", taxAmount);
                foundExemption = true;
            }
        }

        // if no exceptions were found for the current; try the parent
        if (!foundExemption) {
            // try the "parent" TaxAuthority
            List taxAuthorityAssocList =
                    delegator.findByAndCache("TaxAuthorityAssoc", UtilMisc.toMap("toTaxAuthGeoId", taxAuthGeoId,
                            "toTaxAuthPartyId", taxAuthPartyId, "taxAuthorityAssocTypeId", "EXEMPT_INHER"), UtilMisc
                            .toList("-fromDate"));
            taxAuthorityAssocList = EntityUtil.filterByDate(taxAuthorityAssocList, true);
            GenericValue taxAuthorityAssoc = EntityUtil.getFirst(taxAuthorityAssocList);
            // Debug.log("Parent assoc to " + taxAuthGeoId + " : " +
            // taxAuthorityAssoc, module);
            if (taxAuthorityAssoc != null) {
                handlePartyTaxExempt(adjValue, billToPartyIdSet, taxAuthorityAssoc.getString("taxAuthGeoId"),
                        taxAuthorityAssoc.getString("taxAuthPartyId"), taxAmount, nowTimestamp, delegator);
            }
        }
    }

    public static Map getExciseTaxAdjustments(DispatchContext dctx, Map context) {
        Delegator delegator = dctx.getDelegator();
        try {
            GenericValue partyAcctgPref =
                    delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", "Company"), false);
            String exciseEnabled = partyAcctgPref.getString("exciseEnabled");
            if (!"Y".equals(exciseEnabled)) {
                Map m = ServiceUtil.returnSuccess();
                m.put("orderAdjustments", FastList.newInstance());
                return m;
            }
        } catch (GenericEntityException ge) {
            ge.printStackTrace();
        }
        GenericValue product = (GenericValue) context.get("product");
        BigDecimal itemPrice = (BigDecimal) context.get("itemPrice");
        BigDecimal itemAmount = (BigDecimal) context.get("itemAmount");
        String payToPartyId = (String) context.get("payToPartyId");
        String billToPartyId = (String) context.get("billToPartyId");
        String roleType = null;
        try {
            List result =
                    delegator.findList("PartyRole", EntityCondition.makeCondition(
                            EntityCondition.makeCondition("roleTypeId", "CUSTOMER"),
                            EntityCondition.makeCondition("partyId", EntityOperator.IN,
                                    UtilMisc.toList(payToPartyId, billToPartyId))), UtilMisc.toSet("partyId"), null, null, true);
            if (UtilValidate.isNotEmpty(result)) {
                roleType = "CUSTOMER";
            } else {
                result =
                        delegator.findList("PartyRole", EntityCondition.makeCondition(
                                EntityCondition.makeCondition("roleTypeId", "SUPPLIER"),
                                EntityCondition.makeCondition("partyId", EntityOperator.IN,
                                        UtilMisc.toList(payToPartyId, billToPartyId))), UtilMisc.toSet("partyId"), null, null, true);
                if (UtilValidate.isNotEmpty(result))
                    roleType = "SUPPLIER";
            }
        } catch (GenericEntityException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
        List adjustments = FastList.newInstance();
        // build the TaxAuthority expressions (taxAuthGeoId, taxAuthPartyId)
        List taxAuthCondOrList = FastList.newInstance();
        // start with the _NA_ TaxAuthority...
        taxAuthCondOrList.add(EntityCondition.makeCondition(
                EntityCondition.makeCondition("taxAuthorityRateTypeId", EntityOperator.EQUALS, "EXCISE_TAX"),
                EntityCondition.makeCondition(UtilMisc.toMap("taxAuthGeoId", "IND", "taxAuthPartyId", "CBEC"))));

        EntityCondition taxAuthoritiesCond = EntityCondition.makeCondition(taxAuthCondOrList, EntityOperator.OR);
        try {
            EntityCondition productCategoryCond = null;
            if (product != null) {
                // find the tax categories associated with the product and
                // filter by those, with an IN clause or some such
                // question: get all categories, or just a special type? for now
                // let's do all categories...
                Set productCategoryIdSet = FastSet.newInstance();
                List pcmList =
                        delegator.findByAndCache("ProductCategoryMember",
                                UtilMisc.toMap("productId", product.get("productId")));
                pcmList = EntityUtil.filterByDate(pcmList, true);
                Iterator pcmIter = pcmList.iterator();
                while (pcmIter.hasNext()) {
                    GenericValue pcm = (GenericValue) pcmIter.next();
                    productCategoryIdSet.add(pcm.get("productCategoryId"));
                }

                if (productCategoryIdSet.size() == 0) {
                    productCategoryCond =
                            EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, null);
                } else {
                    // REMOVED
                    // EntityCondition.makeCondition("productCategoryId",
                    // EntityOperator.EQUALS, null),
                    // EntityOperator.OR,
                    productCategoryCond =
                            EntityCondition.makeCondition(EntityCondition.makeCondition("productCategoryId",
                                    EntityOperator.IN, productCategoryIdSet));
                }
            } else {
                productCategoryCond = EntityCondition.makeCondition("productCategoryId", EntityOperator.EQUALS, null);
            }

            // build the main condition clause
            List mainExprs = UtilMisc.toList(taxAuthoritiesCond, productCategoryCond);
            if (itemAmount != null || itemPrice != null) {
                mainExprs.add(EntityCondition.makeCondition(
                        EntityCondition.makeCondition("minItemPrice", EntityOperator.EQUALS, null), EntityOperator.OR,
                        EntityCondition.makeCondition("minItemPrice", EntityOperator.LESS_THAN_EQUAL_TO, itemPrice)));
                mainExprs.add(EntityCondition.makeCondition(
                        EntityCondition.makeCondition("minPurchase", EntityOperator.EQUALS, null), EntityOperator.OR,
                        EntityCondition.makeCondition("minPurchase", EntityOperator.LESS_THAN_EQUAL_TO, itemAmount)));
                mainExprs.add(EntityCondition.makeCondition("methodOfCalc", "ON_ASSESSABLE_VALUE"));
            }

            if ("SUPPLIER".equals(roleType)) {
                mainExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("dutyType", "PURCHASES"),
                        EntityOperator.OR, EntityCondition.makeCondition("dutyType", "BOTH")));
            } else if ("CUSTOMER".equals(roleType)) {
                mainExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition("dutyType", "SALES"),
                        EntityOperator.OR, EntityCondition.makeCondition("dutyType", "BOTH")));
            }

            EntityCondition mainCondition = EntityCondition.makeCondition(mainExprs, EntityOperator.AND);
            // create the orderby clause
            // finally ready... do the rate query
            adjustments.addAll(getTaxForLineItem(delegator, product, payToPartyId, billToPartyId, itemAmount,
                    mainCondition));
        } catch (GenericEntityException e) {
            Debug.logError(e, "Problems looking up tax rates", module);
            return ServiceUtil.returnError("Problems looking up tax rates");
        }
        Map result = ServiceUtil.returnSuccess();
        result.put("orderAdjustments", adjustments);
        return result;
    }

    private static List getTaxForLineItem(Delegator delegator, GenericValue product, String payToPartyId,
                                          String billToPartyId, BigDecimal itemAmount, EntityCondition mainCondition) throws GenericEntityException {
        List orderList = UtilMisc.toList("minItemPrice", "minPurchase", "fromDate");
        List adjustments = FastList.newInstance();
        List lookupList = delegator.findList("TaxAuthorityRateProduct", mainCondition, null, orderList, null, false);
        List filteredList = EntityUtil.filterByDate(lookupList, true);

        if (filteredList.size() == 0) {
            Debug.logWarning(
                    "In TaxAuthority Product Rate no records were found for condition:" + mainCondition.toString(), module);
            return adjustments;
        }

        // find the right entry(s) based on purchase amount
        Iterator flIt = filteredList.iterator();
        while (flIt.hasNext()) {
            GenericValue taxAuthorityRateProduct = (GenericValue) flIt.next();
            BigDecimal taxRate =
                    taxAuthorityRateProduct.get("taxPercentage") != null ? taxAuthorityRateProduct
                            .getBigDecimal("taxPercentage") : ZERO_BASE;
            BigDecimal taxable = ZERO_BASE;

            if (product != null
                    && (product.get("taxable") == null || (product.get("taxable") != null && product.getBoolean("taxable")
                    .booleanValue()))) {
                taxable = taxable.add(itemAmount);
            }

            // taxRate is in percentage, so needs to be divided by 100
            BigDecimal taxAmount =
                    (taxable.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);

            String taxAuthGeoId = taxAuthorityRateProduct.getString("taxAuthGeoId");
            String taxAuthPartyId = taxAuthorityRateProduct.getString("taxAuthPartyId");

            // get glAccountId from TaxAuthorityGlAccount entity using the
            // payToPartyId as the organizationPartyId
            String taxAuthGlAccountId = taxAuthorityRateProduct.getString("glAccountId");
            GenericValue adjValue = delegator.makeValue("OrderAdjustment");
            adjValue.set("taxAuthorityRateSeqId", taxAuthorityRateProduct.getString("taxAuthorityRateSeqId"));
            taxAmount = taxAmount.setScale(salestaxFinalDecimals, salestaxRounding);
            adjValue.set("amount", taxAmount);
            adjValue.set("sourcePercentage", taxRate);
            adjValue.set("orderAdjustmentTypeId", "EXCISE_TAX");
            adjValue.set("description", taxAuthorityRateProduct.getString("description"));
            // the primary Geo should be the main jurisdiction that the tax is
            // for, and the secondary would just be to
            // define a parent or wrapping jurisdiction of the primary
            adjValue.set("primaryGeoId", taxAuthGeoId);
            adjValue.set("comments", taxAuthorityRateProduct.getString("description"));
            if (taxAuthPartyId != null)
                adjValue.set("taxAuthPartyId", taxAuthPartyId);
            if (taxAuthGlAccountId != null)
                adjValue.set("overrideGlAccountId", taxAuthGlAccountId);
            if (taxAuthGeoId != null)
                adjValue.set("taxAuthGeoId", taxAuthGeoId);
            adjustments.add(adjValue);

            List<GenericValue> taxAuthorityAssocList =
                    delegator.findByAndCache("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthGeoId", taxAuthGeoId,
                            "taxAuthPartyId", taxAuthPartyId, "methodOfCalc", "ON_TOT_EXCISE", "dutyType",
                            taxAuthorityRateProduct.getString("dutyType")), UtilMisc.toList("-fromDate"));
            taxAuthorityAssocList = EntityUtil.filterByDate(taxAuthorityAssocList, true);
            for (GenericValue taxSurcharge : taxAuthorityAssocList) {
                adjValue = delegator.makeValue("OrderAdjustment");
                adjValue.set("taxAuthorityRateSeqId", taxSurcharge.getString("taxAuthorityRateSeqId"));
                taxRate =
                        taxSurcharge.get("taxPercentage") != null ? taxSurcharge.getBigDecimal("taxPercentage") : ZERO_BASE;
                BigDecimal surchargeAmount =
                        (taxAmount.multiply(taxRate)).divide(PERCENT_SCALE, salestaxCalcDecimals, salestaxRounding);
                surchargeAmount = surchargeAmount.setScale(salestaxFinalDecimals, salestaxRounding);
                adjValue.set("amount", surchargeAmount);
                adjValue.set("sourcePercentage", taxRate);
                if (taxRate.compareTo(BigDecimal.ONE) == 0)
                    adjValue.set("orderAdjustmentTypeId", "HIGH_EDU_CESS");
                else
                    adjValue.set("orderAdjustmentTypeId", "EDUCESS_ADJUSTMENT");

                adjValue.set("primaryGeoId", taxAuthGeoId);
                adjValue.set("comments", taxSurcharge.getString("description"));
                adjValue.set("description", taxSurcharge.getString("description"));
                if (taxAuthPartyId != null)
                    adjValue.set("taxAuthPartyId", taxAuthPartyId);
                if (taxAuthGlAccountId != null)
                    adjValue.set("overrideGlAccountId", taxSurcharge.getString("glAccountId"));
                if (taxAuthGeoId != null)
                    adjValue.set("taxAuthGeoId", taxAuthGeoId);
                adjustments.add(adjValue);

            }
        }

        return adjustments;
    }
}
