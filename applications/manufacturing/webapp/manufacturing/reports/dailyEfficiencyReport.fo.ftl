<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Daily Efficiency Report</fo:block>
<fo:block><fo:leader/></fo:block>
<#--<fo:block>
    <fo:inline font-weight="bold">Date : </fo:inline>
    <fo:inline >
        <#if parameters.date?has_content>
          ${parameters.date?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
        </#if>
    </fo:inline>
</fo:block>-->
<fo:block><fo:leader/></fo:block>
    <#if dailyEfficiencyReport?has_content>
    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(4)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(3)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(3)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(3)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-header>
            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-color="grey">
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8" border-right="solid black"><fo:block text-align="center">ASSET</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8" number-columns-spanned="5" border-right="solid black"><fo:block text-align="center">ON DATE</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8" number-columns-spanned="5" border-right="solid black"><fo:block text-align="center">MONTH TO DATE</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8" number-columns-spanned="5"><fo:block text-align="center">YEAR TO DATE</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <#assign rowColor = "white">
        <fo:table-body>
            <fo:table-row font-weight="bold" font-size="6pt" border-bottom-style="solid" border-bottom-color="grey">
                <fo:table-cell border-right="solid black"><fo:block text-align="center">&#160;&#32;</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">R.HRS</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">PROD</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">TPH</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">UNITS</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">KWH/T OF MAT</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">R.HRS</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">PROD</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">TPH</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">UNITS</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">KWH/T OF MAT</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">R.HRS</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">PROD</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">TPH</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">UNITS</fo:block></fo:table-cell>
                <fo:table-cell border-right="solid black"><fo:block text-align="center">KWH/T OF MAT</fo:block></fo:table-cell>
            </fo:table-row>
            <#list dailyEfficiencyReport.entrySet() as entry>
                <fo:table-row border-bottom="solid black">
                    <fo:table-cell background-color="${rowColor}" border-right="solid black"><fo:block text-align="center">${entry.key?if_exists}</fo:block></fo:table-cell>
                    <#assign mainColumns =['onDate','monthToDate','yearToDate']/>
                    <#assign subColumns = ['runsPerHour','prod','tph','unit','kwhTOfMat']/>
                    <#list mainColumns as mainCols>
                        <#list subColumns as subCols>
                            <fo:table-cell background-color="${rowColor}" border-right="solid black">
                                <#if entry.value[mainCols]?exists>
                                    <fo:block text-align="center">
                                    ${entry.value[mainCols][subCols]?if_exists}
                                    </fo:block>
                                <#else>
                                    <fo:block text-align="center">&#160;&#32;</fo:block>
                                </#if>
                            </fo:table-cell>
                        </#list>
                    </#list>
                </fo:table-row>
            </#list>
        </fo:table-body>
    </fo:table>
    <fo:block break-after="page">&#160;</fo:block>
    <fo:block font-size="15pt" text-align="center" font-weight="bold">STOPPAGE &#38; REASONS</fo:block>
    <#if stoppageAndReason?has_content>
    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-header>
            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-color="grey">
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">ASSET</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">STOP</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">START</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">DURATION (hrs)</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">REASONS</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block text-align="center">DEPT/Reason</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <#list stoppageAndReason.entrySet() as entry>
                <fo:table-row>
                    <fo:table-cell border-bottom="solid black"><fo:block text-align="center">${entry.key}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="5">
                        <fo:table>
                            <fo:table-column column-width="proportional-column-width(2)"/>
                            <fo:table-column column-width="proportional-column-width(2)"/>
                            <fo:table-column column-width="proportional-column-width(2)"/>
                            <fo:table-column column-width="proportional-column-width(2)"/>
                            <fo:table-column column-width="proportional-column-width(2)"/>
                            <fo:table-body>
                                <#list entry.value as value>
                                    <fo:table-row>
                                        <fo:table-cell border="solid black"><fo:block text-align="center">${value.stop?if_exists}</fo:block></fo:table-cell>
                                        <fo:table-cell border="solid black"><fo:block text-align="center">${value.start?if_exists}</fo:block></fo:table-cell>
                                        <fo:table-cell border="solid black"><fo:block text-align="center">${value.meterValue?if_exists}</fo:block></fo:table-cell>
                                        <fo:table-cell border="solid black"><fo:block text-align="center">${value.reason?if_exists}</fo:block></fo:table-cell>
                                        <fo:table-cell border="solid black"><fo:block text-align="center">${value.cause?if_exists}</fo:block></fo:table-cell>
                                    </fo:table-row>
                                </#list>
                            </fo:table-body>
                        </fo:table>
                    </fo:table-cell>
                </fo:table-row>
            </#list>
        </fo:table-body>
    </fo:table>
    <#else>
    <fo:block text-align="center">No Record Found.</fo:block>
    </#if>
    <#else>
    <fo:block text-align="center">No Record Found.</fo:block>
    </#if>
</#escape>