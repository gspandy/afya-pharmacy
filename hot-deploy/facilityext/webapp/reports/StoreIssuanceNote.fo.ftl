<#escape x as x?xml>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Stores Issuance</fo:block>
    <#-- blank line -->
    <fo:table table-layout="fixed" space-after.optimum="2pt">
        <fo:table-column/>
        <fo:table-column/>
        <fo:table-body>
            <fo:table-row height="12.5px" space-start=".15in">
                <fo:table-cell>
                    <fo:block />
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block />
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:table table-layout="fixed" space-after.optimum="2pt">
        <fo:table-column column-width="proportional-column-width(5)"/>
        <fo:table-column column-width="proportional-column-width(70)"/>
        <fo:table-column column-width="proportional-column-width(40)"/>
        <fo:table-body>
            <fo:table-row>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                        Issued From Date : ${fromDate?if_exists?string("dd/MM/yyyy")}</fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                        Issued Thru Date : ${thruDate?if_exists?string("dd/MM/yyyy")}</fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:block><fo:leader></fo:leader></fo:block>

    <#if mainMap?has_content>
        <fo:block space-after.optimum="10pt" font-size="8pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Issued On</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Issuance No.</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Product Id - Product Name</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Department</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Opening Balance</fo:block></fo:table-cell>
                        <#-- <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Received</fo:block></fo:table-cell> -->
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Issued Qty</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Closing Balance</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Reorder @</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Received By</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Issued By</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Cost Center</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                <#list mainMap.entrySet() as entry>
                    <fo:table-row font-weight="bold" border-style="solid">
                        <fo:table-cell number-columns-spanned="9">
                            <fo:block  font-size="12pt">
                            <#assign heading = entry.key/>
                            <#if heading=='Default'>
                                <fo:inline>&#32;&#160;</fo:inline>
                            <#else>
                            	${heading?if_exists}
                            </#if>
                        </fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                    <#assign rowColor = "white">
                    <#list entry.value as storeIssuanceNote>
                        <fo:table-row text-align="center" height="15pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">
                                <#if storeIssuanceNote.issuedOn?has_content>
                                	${storeIssuanceNote.issuedOn?string("dd/MM/yyyy")}
                                </#if>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${storeIssuanceNote.requisitionId?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${storeIssuanceNote.productId?if_exists} - ${storeIssuanceNote.internalName?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${storeIssuanceNote.department?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${storeIssuanceNote.openingBalance?if_exists}</fo:block>
                            </fo:table-cell>
                            <!-- <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${storeIssuanceNote.received?if_exists}</fo:block>
                            </fo:table-cell> -->
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${storeIssuanceNote.issuedQty?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${storeIssuanceNote.closingBalance?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${storeIssuanceNote.reorder?if_exists}</fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#if storeIssuanceNote.receivedBy?has_content>
                                    <#assign receivedByPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":storeIssuanceNote.receivedBy})>
                                    <#if receivedByPerson?has_content>
                                        <#assign receivedByPersonName = receivedByPerson.firstName?if_exists + " " + receivedByPerson.lastName?if_exists + " " + receivedByPerson.groupName?if_exists>
                                        <fo:block margin-top="3px">${receivedByPersonName?if_exists}</fo:block>
                                    <#else>
                                        <fo:block margin-top="3px">${storeIssuanceNote.receivedBy?if_exists}</fo:block>
                                    </#if>
                                <#else>
                                    <fo:block margin-top="3px">${storeIssuanceNote.receivedBy?if_exists}</fo:block>
                                </#if>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <#if storeIssuanceNote.issuedBy?has_content>
                                    <#assign issuedByPerson = delegator.findByPrimaryKey("PartyNameView",{"partyId":storeIssuanceNote.issuedBy})>
                                    <#if issuedByPerson?has_content>
                                        <#assign issuedByPersonName = issuedByPerson.firstName?if_exists + " " + issuedByPerson.lastName?if_exists + " " + issuedByPerson.groupName?if_exists>
                                        <fo:block margin-top="3px">${issuedByPersonName?if_exists}</fo:block>
                                    <#else>
                                        <fo:block margin-top="3px">${storeIssuanceNote.issuedBy?if_exists}</fo:block>
                                    </#if>
                                <#else>
                                    <fo:block margin-top="3px">${storeIssuanceNote.issuedBy?if_exists}</fo:block>
                                </#if>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px">${storeIssuanceNote.costCenter?if_exists}</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>
                    </#list>
                    </#list>
                </fo:table-body>
             </fo:table>
        </fo:block>
    <#else>
        <fo:block>No Record Found.</fo:block>
    </#if>

</#escape>
