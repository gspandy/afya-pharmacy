<#assign productAttributes = delegator.findByAnd("ProductAttribute",{"productId":inventoryItem.productId},null,false)>
<#assign todayDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp)>
<#assign inventoryItemAttributes = delegator.findByAnd("InventoryItemAttribute",{"inventoryItemId":inventoryItem.inventoryItemId,"effectiveDate":todayDate},null,false)>
<form action="<@ofbizUrl>UpdateAtrributes?inventoryItemId=${inventoryItem.inventoryItemId}</@ofbizUrl>" method="post" id="transferform" name="transferform" style="margin: 0;">
    <div class="screenlet">
        <div class="screenlet-body">
            <table cellspacing="0" cellpadding="2" class="basic-table">
                <#assign rowCount = 0>
                <#assign alt_row = false>
                <#assign items={}>
                <#if inventoryItemAttributes?has_content>
                    <#assign items=inventoryItemAttributes>
                    <#else>
                    <#assign items=productAttributes>
                </#if>
                <#if productAttributes?has_content>
                    <tr>
                        <td>Date</td>
                        <td>
                            <@htmlTemplate.renderDateTimeField name="effectiveDate" value="${nowTimestamp?string('yyyy-MM-dd')!''}" className="date required" alert="" 
                                        title="Format: dd/MM/yyyy" size="15" maxlength="10" id="transferDate" dateType="date" shortDateInput=true 
                                        timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" 
                                        hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="transferform"/>
                            <label style="color:red;">*</label>
                        </td>
                    </tr>
                </#if>
                <#list items as item>
                  <tr<#if alt_row> class="alternate-row"</#if>>
                    <#assign alt_row = !alt_row>
                    <td>${item.attrName}</td>
                    <td> <input type="text" class="positivedecimalvalue required" name="${item.attrName}" id="${item.attrName}" value="${item.attrValue?if_exists}"/><span><font color="red">*</font></span> </td>
                  </tr>
                    <#assign alt_row = !alt_row>
                    <#assign rowCount = rowCount + 1>
                </#list>
                <#if !productAttributes?has_content>
                    <h2>No Attributes Found</h2>
                </#if>
                <#if productAttributes?has_content>
                    <tr>
                        <td>&nbsp;</td>
                        <td><input type="submit" value="Save" class="btn btn-success"/></td>
                    </tr>
                </#if>
            </table>
        </div>
    </div>
</form>
<script language="JavaScript" type="text/javascript">
    var form = document.transferform;
    jQuery(form).validate();
</script>
<#if itemList?has_content>
    <div class="screenlet" style="margin-top:10px;">
        <div class="screenlet-body">
            <table cellspacing="0" cellpadding="2" class="basic-table">
                <tr class="header-row">
                    <td>Date</td>
                    <#list headers as header>
                      <td>${header}</td>
                    </#list>
                </tr>
                <#assign alt_row = false>
                <#assign rowCount = 0>
                <#list itemList as item>
                    <tr<#if alt_row> class="alternate-row"</#if>>
                        <#assign alt_row = !alt_row>
                        <td>${item.effectiveDate?string("dd/MM/yyyy")}</td>
                        <#list headers as header>
                           <#assign value = item.get(header)>
                           <td> ${value} </td>
                        </#list>
                    </tr>
                    <#assign alt_row = !alt_row>
                    <#assign rowCount = rowCount + 1>
                </#list>
            </table>
        </div>
    </div>
</#if>
<script language="JavaScript" type="text/javascript">
    function submitForm() {
        var cform = document.transferform;
        cform.action = "<@ofbizUrl>UpdateAtrributes?inventoryItemId=${inventoryItem.inventoryItemId}</@ofbizUrl>";
        cform.submit();
    }
</script>