<#escape x as x?xml>
<#assign shipment = delegator.findOne("Shipment", {"shipmentId" : parameters.shipmentId}, false)>
<#assign  orderId = shipment.primaryOrderId/>
<#assign shipmentItems = delegator.findByAnd("ShipmentItem",{"shipmentId":parameters.shipmentId})/>
<#assign orderRoles = delegator.findByAnd("OrderRole",{"orderId":orderId,"roleTypeId":"BILL_TO_CUSTOMER"})/>
<#assign customerId = orderRoles.get(0).get("partyId")/>
<#assign partyGroupGv = delegator.findOne("PartyGroup",{"partyId":customerId},false)/>



<fo:table table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column column-width="proportional-column-width(78)"/>
    <fo:table-column column-width="proportional-column-width(22)"/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell>
                <fo:block/>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block font-size="14pt" number-columns-spanned="2">
                    Sr No: ${shipment.shipmentId?if_exists}</fo:block>
            </fo:table-cell>
        </fo:table-row>

        <fo:table-row>
            <fo:table-cell>
                <fo:block/>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block font-size="14pt" number-columns-spanned="2">
                    Date: ${shipment.createdDate?if_exists?string("dd-MM-yyyy")}</fo:block>
            </fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>

<fo:table table-layout="fixed" space-after.optimum="10pt">
    <fo:table-column column-width="proportional-column-width(37)"/>
    <fo:table-column column-width="proportional-column-width(45)"/>
    <fo:table-column column-width="proportional-column-width(25)"/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell>
                <fo:block/>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block font-size="20pt" font-weight="bold">LOADING SLIP</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block/>
            </fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>

<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
    <fo:table-column column-width="proportional-column-width(20)"/>
    <fo:table-column column-width="proportional-column-width(35)"/>
    <fo:table-column column-width="proportional-column-width(20)"/>
    <fo:table-column column-width="proportional-column-width(35)"/>

    <fo:table-body>

        <fo:table-row border-bottom-style="solid" border-bottom-color="grey" height="25px">
            <fo:table-cell>
                <fo:block margin-top="7px" margin-left="5px">Customer:</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="7px"><#if partyGroupGv?has_content> ${partyGroupGv.groupName?if_exists} </#if></fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="7px">Customer Code:</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="7px">${customerId?if_exists}</fo:block>
            </fo:table-cell>
        </fo:table-row>

        <fo:table-row border-bottom-style="solid" border-bottom-color="grey" height="25px">
            <fo:table-cell>
                <fo:block margin-top="7px" margin-left="5px">Truck Number:</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="7px">${shipment.truckNumber?if_exists}</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="7px">Trailer Number:</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="7px">${shipment.trailerNumber?if_exists}</fo:block>
            </fo:table-cell>
        </fo:table-row>

        <fo:table-row border-bottom-style="solid" border-bottom-color="grey" height="25px">
            <fo:table-cell>
                <fo:block margin-top="7px" margin-left="5px">NRC Number:</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="7px">${shipment.nrcNumber?if_exists}</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="7px">Driver Name:</fo:block>
            </fo:table-cell>
            <fo:table-cell>
                <fo:block margin-top="7px">${shipment.driverName?if_exists}</fo:block>
            </fo:table-cell>
        </fo:table-row>

        <fo:table-row border-bottom-style="solid" border-bottom-color="grey" height="25px">
            <fo:table-cell>
                <fo:block margin-top="7px" margin-left="5px">Shipment Notes:</fo:block>
            </fo:table-cell>
            <fo:table-cell number-columns-spanned="3">
                <fo:block margin-top="7px">${shipment.note?if_exists}</fo:block>
            </fo:table-cell>
        </fo:table-row>

    </fo:table-body>
</fo:table>
<fo:table table-layout="fixed" space-before.optimum="20pt" space-b.optimum="10pt">
    <fo:table-column/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell>
                <fo:block font-size="17pt" font-weight="bold">Order Detail</fo:block>
            </fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>

<#if shipmentItems?has_content>
    <fo:table table-layout="fixed" space-before.optimum="10pt" space-after.optimum="10pt" font-size="10pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(3)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-header>
            <fo:table-row font-weight="bold">
                <fo:table-cell  background-color="#D4D0C8"><fo:block>Sl No</fo:block></fo:table-cell>
                <fo:table-cell  background-color="#D4D0C8"><fo:block>Order No</fo:block></fo:table-cell>
                <fo:table-cell  background-color="#D4D0C8"><fo:block>Product</fo:block></fo:table-cell>
                <fo:table-cell  background-color="#D4D0C8"><fo:block>Unit</fo:block></fo:table-cell>
                <fo:table-cell  background-color="#D4D0C8"><fo:block>Loading Quantity</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <#assign  count=0/>
            <#list shipmentItems as shipmentItem>
                <#assign  count=count+1/>
                <#if ((line_index % 2) == 0)>
                    <#assign rowColor = "white">
                <#else>
                    <#assign rowColor = "#CCCCCC">
                </#if>

                <fo:table-row>
                    <fo:table-cell padding="2pt" background-color="${rowColor}">
                        <fo:block>${count?if_exists}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2pt" background-color="${rowColor}">
                        <fo:block>${orderId?if_exists}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2pt" background-color="${rowColor}">
                        <#assign  productGv = delegator.findOne("Product",{"productId":shipmentItem.productId},false)/>
                        <#assign  uomGv = delegator.findOne("Uom",{"uomId":productGv.quantityUomId},false)/>
                        <fo:block>${productGv.productName?if_exists}</fo:block>
                    </fo:table-cell>
                    <fo:table-cell padding="2pt" background-color="${rowColor}">
                        <#if uomGv?has_content>
                            <fo:block>${uomGv.description?if_exists}</fo:block>
                        <#else>
                            <fo:block></fo:block>
                        </#if>
                    </fo:table-cell>
                    <fo:table-cell padding="2pt" background-color="${rowColor}">
                        <fo:block>${shipmentItem.quantity?if_exists}</fo:block>
                    </fo:table-cell>
                </fo:table-row>
            </#list>
        </fo:table-body>
    </fo:table>
    <fo:table table-layout="fixed" font-size="10pt" border-style="none">
        <fo:table-column column-number="1" column-width="proportional-column-width(9)"/>
        <fo:table-column column-number="2" column-width="proportional-column-width(32)"/>
        <fo:table-column column-number="2" column-width="proportional-column-width(4)"/>
        <fo:table-column column-number="3" column-width="proportional-column-width(30)"/>
        <fo:table-column column-number="4" column-width="proportional-column-width(25)"/>
        <fo:table-body>
            <fo:table-row space-start="2.15in" height="35px">
                <#assign createdParty = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyAndUserLoginAndPerson", {"userLoginId" : shipment.createdByUserLogin?if_exists}))/>
                <#if createdParty?has_content>
                    <fo:table-cell>
                        <fo:block text-align="left" margin-left="3px" margin-top="30px">Prepared By: </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block text-align="left" margin-left="3px" margin-top="30px" border-bottom="dotted">${createdParty.firstName?if_exists} ${createdParty.middleName?if_exists} ${createdParty.lastName?if_exists}</fo:block>
                    </fo:table-cell>
                <#else>
                    <fo:table-cell>
                        <fo:block text-align="left" margin-left="3px" margin-top="30px">Prepared By: </fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <fo:block text-align="left" margin-left="3px" margin-top="30px"><fo:leader leader-pattern="dots" leader-length="8.1cm"></fo:leader></fo:block>
                    </fo:table-cell>
                </#if>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="30px"></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="30px">Sign: <fo:leader leader-pattern="dots" leader-length="5.6cm"></fo:leader></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="30px">Date: <fo:leader leader-pattern="dots" leader-length="5cm"></fo:leader></fo:block>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row space-start="2.15in" height="35px">
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px">Checked By: </fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px"><fo:leader leader-pattern="dots" leader-length="8.1cm"></fo:leader></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px"></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px">Sign: <fo:leader leader-pattern="dots" leader-length="5.6cm"></fo:leader></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px">Date: <fo:leader leader-pattern="dots" leader-length="5cm"></fo:leader></fo:block>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row space-start="2.15in" height="35px">
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px" margin-bottom="25px">Security: </fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px" margin-bottom="25px"><fo:leader leader-pattern="dots" leader-length="8.1cm"></fo:leader></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px"></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px" margin-bottom="25px">Sign: <fo:leader leader-pattern="dots" leader-length="5.6cm"></fo:leader></fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block text-align="left" margin-left="3px" margin-top="25px" margin-bottom="25px">Date: <fo:leader leader-pattern="dots" leader-length="5cm"></fo:leader></fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
<#else>
    <fo:block font-size="14pt">No order items shipped</fo:block>
</#if>

</#escape>