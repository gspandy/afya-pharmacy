<#escape x as x?xml>
    <#assign shipment = delegator.findOne("Shipment", {"shipmentId" : parameters.shipmentId}, false)>
    <#assign shipmentReceipt = delegator.findByAnd("ShipmentReceipt", {"shipmentId" : parameters.shipmentId},null,false)>
    <#assign supplier = delegator.findOne("PartyGroup", {"partyId" : shipment.partyIdFrom}, false)>
    <#assign receivedDate = shipmentReceipt[0].datetimeReceived>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Procurement and Stores Manual</fo:block>
<fo:block>
    <fo:inline>
        Appendix VIII
    </fo:inline>
    <fo:inline padding-left="310pt">
        Service Material Inspection
    </fo:inline>
</fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" >
    <fo:table-column/>
    <fo:table-body>
        <fo:table-row  border-style="solid" border-color="black">
            <fo:table-cell>
                <fo:block font-size="16pt" text-align="center" font-weight="bold">
                    ${companyName?if_exists}
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    <#--Second Row-->
        <fo:table-row  border-style="solid" border-color="black">
            <fo:table-cell>
                <fo:table>
                    <fo:table-column/>
                    <fo:table-column/>
                    <fo:table-body>
                        <fo:table-row font-size="14pt">
                            <fo:table-cell>
                                <fo:block><fo:leader/></fo:block>
                                <fo:block font-weight="bold">
                                    Service/Material Inspection Note
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell border-left-style="solid">
                                <fo:block>
                                    Date: ${shipment.createdDate?string("dd-MM-yyyy")}
                                </fo:block>
                                <fo:block><fo:leader/></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
            </fo:table-cell>
        </fo:table-row>
    <#--End second row-->
    <#--Third row-->
        <fo:table-row  border-style="solid" border-color="black" font-size="11pt">
            <fo:table-cell>
                <fo:block><fo:leader/></fo:block>
                <fo:block>
                    <fo:inline>
                        Order No. <fo:inline>${shipment.primaryOrderId?if_exists}</fo:inline>
                    </fo:inline>
                    <fo:inline padding-left="220pt">
                        Date:${receivedDate?string("dd-MM-yyyy")}
                    </fo:inline>
                </fo:block>
                <fo:block>
                    Supplier: ${supplier.groupName?if_exists}
                </fo:block>
                <fo:block><fo:leader/></fo:block>
                <fo:block>
                    <fo:inline>
                        GOODS RECEIVED NOTE No. <fo:inline>${shipment.shipmentId?if_exists}</fo:inline>
                    </fo:inline>
                    <fo:inline padding-left="150pt">
                        Date:${receivedDate?string("dd-MM-yyyy")}
                    </fo:inline>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    <#--end third row-->
    <#--fourth row-->
        <fo:table-row  border-style="solid" border-color="black">
            <fo:table-cell>
                <fo:block font-size="16pt" text-align="center" font-weight="bold">
                    DESCRIPTION
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row border-style="solid" border-color="black">
            <fo:table-cell>
            <fo:block><fo:leader/></fo:block>
                <fo:block>
                   <fo:inline>${shipment.inspectionDescription?if_exists}</fo:inline>
                </fo:block>
             <fo:block><fo:leader/></fo:block>
            </fo:table-cell>
        </fo:table-row>
    <#--end fourth row-->
    <#--fifth row-->
    <#--end fifth row-->
    <#--sixth row-->
        <fo:table-row  border-style="solid" border-color="black">
            <fo:table-cell>
                <fo:block font-size="16pt" text-align="center" font-weight="bold">
                    RESULT OF INSPECTION
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    <#--end sixth row-->
    <#--7th Row-->
        <fo:table-row  border-style="solid" border-color="black" font-size="11pt" font-weight="bold">
            <fo:table-cell>
                <fo:table>
                    <fo:table-column/>
                    <fo:table-column/>
                    <fo:table-body>
                        <fo:table-row border-bottom-style="solid">
                            <fo:table-cell>
                                <fo:block><fo:leader/></fo:block>
                                <fo:block><fo:leader/></fo:block>
                                <fo:block>Inspection Result : <fo:inline font-weight="bold">${shipment.inspectionResult?if_exists}</fo:inline></fo:block>
                                <fo:block><fo:leader/></fo:block>
                                <fo:block><fo:leader/></fo:block>
                                <fo:block>OBSERVATIONS : <fo:inline text-decoration="underline" font-weight="normal">${shipment.inspectionNote?if_exists}</fo:inline></fo:block>
                                <fo:block><fo:leader/></fo:block>
                            </fo:table-cell>
                            <fo:table-cell border-left-style="solid">
                                <fo:block>Inspection Date : <fo:inline font-weight="normal">${receivedDate?string("dd-MM-yyyy")}</fo:inline></fo:block>
                                <fo:block><fo:leader/></fo:block>
                                <fo:block><fo:leader/></fo:block>
                                <fo:block><fo:leader/></fo:block>
                                <#assign inspectedParty = delegator.findOne("Person", {"partyId":shipment.inspectedBy},false)>
                                <fo:block border-style="solid">Name of Inspector :
                                    <#if inspectedParty?exists>
                                    <fo:inline font-weight="normal">${inspectedParty.firstName?if_exists} ${inspectedParty.middleName?if_exists} ${inspectedParty.lastName?if_exists}</fo:inline>
                                    </#if>
                                    </fo:block>
                                <fo:block><fo:leader/></fo:block>
                                <fo:block><fo:leader/></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row border-bottom-style="solid">
                            <fo:table-cell>
                                <fo:block><fo:leader/></fo:block>
                            </fo:table-cell>
                            <fo:table-cell border-left-style="solid">
                                <fo:block text-align="center" font-weight="bold">Signature</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row >
                            <fo:table-cell>
                                <fo:block>
                                    <fo:inline>
                                        Chief Store Keeper
                                    </fo:inline>
                                    <fo:inline padding-left="20pt">
                                        Required - End User
                                    </fo:inline>
                                </fo:block>
                                <fo:block><fo:leader/></fo:block>
                            </fo:table-cell>
                            <fo:table-cell border-left-style="solid">
                                <fo:block><fo:leader/></fo:block>
                                <fo:block text-align="center" font-weight="bold">Head User Department</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
            </fo:table-cell>
        </fo:table-row>
    <#--End 7th row-->
    </fo:table-body>
</fo:table>
</#escape>