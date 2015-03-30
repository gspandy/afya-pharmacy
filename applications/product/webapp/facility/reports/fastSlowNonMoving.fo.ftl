<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Fast Slow Non Moving</fo:block>
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
<fo:block>
    <fo:inline font-weight="bold">From Date : </fo:inline>
    <fo:inline >
        <#if parameters.fromDate?has_content>
          ${parameters.fromDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
        </#if>
    </fo:inline>
    <fo:inline padding-left="190pt" font-weight="bold">Thru Date : </fo:inline>
    <fo:inline >
        <#if parameters.thruDate?has_content>
          ${parameters.thruDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
        </#if>
    </fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
    <#if mainMap?has_content>

    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(4)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-header>
            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-color="grey">
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Product Code</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Product Description</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Units</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>% Usage</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Re-Order Level</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
        <#list mainMap.entrySet() as entry>
        	<fo:table-row font-weight="bold" border-style="solid">
                <fo:table-cell number-columns-spanned="5">
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
            <#list entry.value as value>
                <fo:table-row>
                    <fo:table-cell background-color="${rowColor}"><fo:block> ${value.productId?if_exists} </fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block> ${value.productDescription?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.unit?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.usage?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.reorderQuantity?if_exists}</fo:block></fo:table-cell>
                </fo:table-row>
                <#if rowColor == "white">
                    <#assign rowColor = "#D4D0C8">
                <#else>
                    <#assign rowColor = "white">
                </#if>
            </#list>
            </#list>
        </fo:table-body>
    </fo:table>
    <#else>
    <fo:block>No Record Found.</fo:block>
    </#if>

</#escape>