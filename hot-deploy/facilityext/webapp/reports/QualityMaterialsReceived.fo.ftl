<#escape x as x?xml>

<#assign productGv =  delegator.findOne("Product", {"productId":productId},false)>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Quality Materials Received</fo:block>
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
                    <#if productGv?has_content>
                        Product : ${productGv.internalName?if_exists}
                    </#if>    
                    </fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                        From Date : ${fromDate?if_exists?string("dd-MM-yyyy")}</fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                        Thru Date : ${thruDate?if_exists?string("dd-MM-yyyy")}</fo:block>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:block><fo:leader></fo:leader></fo:block>
	<#if qmrList?has_content>
        <fo:block space-after.optimum="10pt" font-size="8pt">
            <fo:table table-layout="fixed" border-style="solid" border-bottom="none" border-color="black">
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(1.2)"/>
                <fo:table-column column-width="proportional-column-width(3.5)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <#if headers?has_content>
	                <#list headers as header>
	                	<fo:table-column column-width="proportional-column-width(1.5)"/>
	                </#list>
                </#if>
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-right-style="solid"><fo:block>Received On</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-right-style="solid"><fo:block>Shipment ID</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-right-style="solid"><fo:block>Supplier Name</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-right-style="solid"><fo:block>Type</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-right-style="solid"><fo:block>Truck</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-right-style="solid"><fo:block>Net Weight</fo:block></fo:table-cell>
                        <#if headers?has_content>
                        <#list headers as header>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid" border-right-style="solid"><fo:block>${header}</fo:block></fo:table-cell>
                        </#list>
                        </#if>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#list qmrList as qmr>
                        <fo:table-row text-align="center" height="15pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid">
                                <fo:block margin-top="3px">
                                	${qmr.receivedOn?if_exists}
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid">
                                <fo:block margin-top="3px">
                                	${qmr.shipmentId?if_exists}
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid">
                                <fo:block margin-top="3px">
                                	${qmr.supplierName?if_exists}
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid">
                            <#assign shipmentType = delegator.findOne("ShipmentType",{"shipmentTypeId":qmr.type?if_exists},false)>
                                <fo:block margin-top="3px">
	                                <#if shipmentType?has_content>
	                                	${shipmentType.description?if_exists}
	                                </#if>
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid">
                                <fo:block margin-top="3px">
                                	${qmr.truck?if_exists}
                                </fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid">
                                <fo:block margin-top="3px">
                                	${qmr.netWeight?if_exists}
                                </fo:block>
                            </fo:table-cell>
                            <#if qmr.items?has_content>
                            <#list qmr.items as item>
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid" text-align="right" >
                                <fo:block margin-top="3px">
                            		${item}
                                </fo:block>
                            </fo:table-cell>
                            </#list>
                            </#if>
                        </fo:table-row>
                        <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>
                    </#list>
                </fo:table-body>
             </fo:table>
             <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(12.7)"/>
                <#if headers?has_content>
	                <#list headers as header>
	                	<fo:table-column column-width="proportional-column-width(1.5)"/>
	                </#list>
                </#if>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#-- <#list returnablesList as returnables> -->
                        <fo:table-row height="14pt" border-bottom-style="solid">
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid">
                                <fo:block margin-top="3px">Average</fo:block>
                            </fo:table-cell>
                            <#if averageList?has_content>
	                            <#list averageList as average>
		                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid" text-align="right" font-weight="bold">
	                                	<fo:block margin-top="3px">${average}</fo:block>
	                            	</fo:table-cell>
	                            </#list>
                            </#if>
                        </fo:table-row>
                        <fo:table-row height="14pt" border-bottom-style="solid">
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid">
                                <fo:block margin-top="3px">Maximum</fo:block>
                            </fo:table-cell>
                            
                            <#if maxValues?has_content>
                            <#list maxValues as maxValue>
                                 <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid" text-align="right" font-weight="bold">
	                                <fo:block margin-top="3px">${maxValue}</fo:block>
	                            </fo:table-cell>
                            </#list>
                            </#if>
                            
                        </fo:table-row>
                        <fo:table-row height="14pt" border-bottom-style="solid">
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid">
                                <fo:block margin-top="3px">Minimum</fo:block>
                            </fo:table-cell>
                            
                            <#if minValues?has_content>
                            <#list minValues as minValue>
                                <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid" text-align="right" font-weight="bold">
	                                <fo:block margin-top="3px">${minValue}</fo:block>
	                            </fo:table-cell>
                            </#list>
                            </#if>
                        </fo:table-row>
                        <fo:table-row height="14pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid">
                                <fo:block margin-top="3px">SDEV</fo:block>
                            </fo:table-cell>
                            
                            <#if sdevs?has_content>
                            <#list sdevs as sdev>
                              <fo:table-cell padding="2pt" background-color="${rowColor}" border-right-style="solid" text-align="right" font-weight="bold">
	                                <fo:block margin-top="3px">${sdev?string("##0.00")}</fo:block>
	                          </fo:table-cell>
                            </#list>
                            </#if>
                        </fo:table-row>
                        <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>
                    <#-- </#list> -->
                </fo:table-body>
             </fo:table>
        </fo:block>
    <#else>
        <fo:block>No Record Found.</fo:block>
    </#if>

</#escape>
