<#escape x as x?xml>

    <#assign employeeItemIssuanceList = delegator.findByAnd("EmployeeItemIssuance", {"inventoryItemId":parameters.inventoryItemId})>
    <#if employeeItemIssuanceGv?has_content>
        <#assign employeeItemIssuanceGv = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(employeeItemIssuanceList)>
        <#assign inventoryItemList = delegator.findByAnd("InventoryItem", {"inventoryItemId":employeeItemIssuanceGv.inventoryItemId,"facilityId":parameters.facilityId})/>
        <#assign inventoryItem = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(inventoryItemList)>
    <#--<#assign inventoryItem = delegator.findOne("InventoryItem", {"inventoryItemId":employeeItemIssuanceGv.inventoryItemId},false)>-->
        <#assign product = delegator.findOne("Product", {"productId":inventoryItem.productId},false)>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Issue To Employee</fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>
    <fo:block>Inventory Item : ${inventoryItem.inventoryItemId?if_exists}</fo:block>
    <fo:block>Product Name : ${product.description?if_exists}</fo:block>
    <#else>
        <#assign inventoryItem = delegator.findOne("InventoryItem", {"inventoryItemId":parameters.inventoryItemId},false)>
        <#assign product = delegator.findOne("Product", {"productId":inventoryItem.productId},false)>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Issue To Employee</fo:block>
    <fo:block><fo:leader></fo:leader></fo:block>
    <fo:block>Inventory Item : ${inventoryItem.inventoryItemId?if_exists}</fo:block>
    <fo:block>Product Name : ${product.description?if_exists}</fo:block>
    </#if>
<fo:block><fo:leader></fo:leader></fo:block>

    <#if employeeItemIssuanceList?has_content>
    <fo:block space-after.optimum="10pt" font-size="10pt">
        <fo:table table-layout="fixed" border-style="solid" border-color="black">
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-header>
                <fo:table-row font-weight="bold" text-align="center">
                    <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Issue ID</fo:block></fo:table-cell>
                    <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Employee</fo:block></fo:table-cell>
                    <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Quantity</fo:block></fo:table-cell>
                    <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Issue Date</fo:block></fo:table-cell>
                </fo:table-row>
            </fo:table-header>
            <fo:table-body>
                <#assign rowColor = "white">
                <#assign  issuedQuantity =0.00/>
                <#list employeeItemIssuanceList as employeeItemIssuance>
                    <#assign issuedQuantity = issuedQuantity+employeeItemIssuance.quantity/>
                    <fo:table-row text-align="center" height="15pt">
                        <fo:table-cell padding="2pt" background-color="${rowColor}">
                            <fo:block margin-top="3px">${employeeItemIssuance.itemIssuanceId?if_exists}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="2pt" background-color="${rowColor}">
                            <#assign employee = delegator.findOne("Person", {"partyId":employeeItemIssuance.employeeId},false)>
                            <fo:block margin-top="3px">${employee.firstName?if_exists} ${employee.middleName?if_exists} ${employee.lastName?if_exists}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="2pt" background-color="${rowColor}">
                            <fo:block margin-top="3px">${employeeItemIssuance.quantity?if_exists}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell padding="2pt" background-color="${rowColor}">
                            <fo:block margin-top="3px">${employeeItemIssuance.issuedOn?if_exists?string("dd/MM/yyyy HH:mm:ss a")}</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                <#-- toggle the row color -->
                    <#if rowColor == "white">
                        <#assign rowColor = "#D4D0C8">
                    <#else>
                        <#assign rowColor = "white">
                    </#if>
                </#list>

                <#assign remainingQty = inventoryItem.quantityOnHandTotal?if_exists>
                <#assign totalQty = issuedQuantity+remainingQty>

                <fo:table-row font-weight="bold">
                    <fo:table-cell>
                        <fo:block />
                    </fo:table-cell>
                    <fo:table-cell padding="2pt">
                        <fo:block margin-top="5px" text-align="right">Issued Quantity (a) :</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2pt">
                        <fo:block margin-top="5px" text-align="center">${issuedQuantity?if_exists}</fo:block>
                    </fo:table-cell>
                </fo:table-row>

            <#-- blank line -->
                <fo:table-row height="20px" space-start=".15in">
                    <fo:table-cell>
                        <fo:block />
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block />
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block />
                    </fo:table-cell>
                </fo:table-row>

                <fo:table-row font-weight="bold">
                    <fo:table-cell>
                        <fo:block />
                    </fo:table-cell>
                    <fo:table-cell padding="2pt">
                        <fo:block text-align="right">Total Quantity (b) :</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2pt">
                        <fo:block text-align="center">${totalQty?if_exists}</fo:block>
                    </fo:table-cell>
                </fo:table-row>

                <fo:table-row font-weight="bold">
                    <fo:table-cell>
                        <fo:block />
                    </fo:table-cell>
                    <fo:table-cell padding="2pt">
                        <fo:block text-align="right">Remaining Quantity (b-a) :</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2pt">
                        <fo:block text-align="center">${remainingQty?if_exists}</fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
    </fo:block>
    <#else>
    <fo:block>No Record Found.</fo:block>
    </#if>

</#escape>
