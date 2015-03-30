<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#if !sessionAttributes.userLogin?exists>
  <div class='label'> ${uiLabelMap.ProductGeneralMessage}.</div>
</#if>
<br />
<#if security.hasEntityPermission("CATALOG", "_VIEW", session)>
  <div class="label">${uiLabelMap.ProductEditCatalogWithCatalogId}:</div>
  <form method="post" action="<@ofbizUrl>EditProdCatalog</@ofbizUrl>" style="margin: 0;" name="EditProdCatalogForm">
   <@htmlTemplate.lookupField name="prodCatalogId" id="prodCatalogId" formName="EditProdCatalogForm" fieldFormName="LookupProdCatalog" /> 	
    <!--<input type="text" size="20" maxlength="20" name="prodCatalogId" value=""/>-->
    <input type="submit" value=" ${uiLabelMap.ProductEditCatalog}" class="buttontext"/>
    <div class="label">${uiLabelMap.CommonOr}: <a href="<@ofbizUrl>EditProdCatalog</@ofbizUrl>" class="btn">${uiLabelMap.ProductCreateNewCatalog}</a></div>
    <br />
  </form>
  <br /> 
  <div class="label">${uiLabelMap.ProductEditCategoryWithCategoryId}:</div>
  <form method="post" action="<@ofbizUrl>EditCategory</@ofbizUrl>" style="margin: 0;" name="EditCategoryForm">
    <@htmlTemplate.lookupField name="productCategoryId" id="productCategoryId" formName="EditCategoryForm" fieldFormName="LookupProductCategory"/>
    <input type="submit" value="${uiLabelMap.ProductEditCategory}" class="buttontext"/>
    <div class="label">${uiLabelMap.CommonOr}: <a href="<@ofbizUrl>EditCategory</@ofbizUrl>" class="btn">${uiLabelMap.ProductCreateNewCategory}</a></div>
    <br />
  </form>
  <br />
  <div class="label">${uiLabelMap.ProductEditProductWithProductId}:</div>
  <form method="post" action="<@ofbizUrl>EditProduct</@ofbizUrl>" style="margin: 0;" name="EditProductForm">
    <@htmlTemplate.lookupField name="productId" id="productId" formName="EditProductForm" fieldFormName="LookupProduct"/>
    <input type="submit" value=" ${uiLabelMap.ProductEditProduct}" class="buttontext"/>
    <div class="label">${uiLabelMap.CommonOr}: <a href="<@ofbizUrl>EditProduct</@ofbizUrl>" class="btn">${uiLabelMap.ProductCreateNewProduct}</a></div>
    <br />
  </form>
  <br />
  
  <!-- <div class="label">${uiLabelMap.CommonOr}: <a href="<@ofbizUrl>CreateVirtualWithVariantsForm</@ofbizUrl>" class="btn btn-success">${uiLabelMap.ProductQuickCreateVirtualFromVariants}</a></div> -->

  <!-- 
  <div class="label">${uiLabelMap.ProductFindProductWithIdValue}:</div>
  <form method="post" action="<@ofbizUrl>FindProductById</@ofbizUrl>" style="margin: 0;">
    <input type="text" size="20" maxlength="20" name="idValue" value=""/>
    <input type="submit" value=" ${uiLabelMap.ProductFindProduct}" class="btn btn-primary"/>
  </form>
  <br />
  <div><a href="<@ofbizUrl>UpdateAllKeywords</@ofbizUrl>" class="buttontext"> ${uiLabelMap.ProductAutoCreateKeywordsForAllProducts}</a></div>
  <div><a href="<@ofbizUrl>FastLoadCache</@ofbizUrl>" class="buttontext"> ${uiLabelMap.ProductFastLoadCatalogIntoCache}</a></div>
  -->
  <br />
</#if>
