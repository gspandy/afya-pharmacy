<#escape x as x?xml>
		<#if result.listIt.hasNext()>
	        <fo:block space-after.optimum="10pt" font-size="10pt">
	            <fo:table font-size="8pt" width="100%">
	                <fo:table-header>
	                    <fo:table-row font-weight="bold" >
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Fixed Asset Id</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Fixed Asset Name</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Fixed Asset Type</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Fixed Asset Parent</fo:block></fo:table-cell>
	                    </fo:table-row>
	                </fo:table-header>
	                <fo:table-body>
	                    <#assign rowColor = "white">
	                    <#list result.listIt as fixedassetReport>
	                        <fo:table-row>
	                            <fo:table-cell padding="1pt" background-color="${rowColor}">
	                                <fo:block>${fixedassetReport.fixedAssetId?default("")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${fixedassetReport.fixedAssetName?default("")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${fixedassetReport.fixedAssetTypeId?default("")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${fixedassetReport.parentFixedAssetId?default("")}</fo:block>
	                            </fo:table-cell>
	                        </fo:table-row>
	                        <#-- toggle the row color -->
	                        <#if rowColor == "white">
	                            <#assign rowColor = "#eeeeee">
	                        <#else>
	                            <#assign rowColor = "white">
	                        </#if>        
	                    </#list>          
	                </fo:table-body>
	            </fo:table>
	        </fo:block>
        <#else>
                <fo:block font-size="14pt">${uiLabelMap.NoRecordsFound}</fo:block>
        </#if>
</#escape>