<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Stoppage Analysis</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>
    <fo:inline font-weight="bold">Issue From Date : </fo:inline>
    <fo:inline >
        <#if parameters.fromDate?has_content>
          ${parameters.fromDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
        </#if>
    </fo:inline>
    <fo:inline padding-left="190pt" font-weight="bold">Issue Thru Date : </fo:inline>
    <fo:inline >
        <#if parameters.thruDate?has_content>
          ${parameters.thruDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}
        </#if>
    </fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
    <#if stoppageAnalysisMap?has_content>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
    <fo:table-column column-width="proportional-column-width(3)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(4)"/>
    <fo:table-header>
        <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-color="grey">
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Date</fo:block></fo:table-cell>
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>R/Hrs</fo:block></fo:table-cell>
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Down time</fo:block></fo:table-cell>
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Prod. (t)</fo:block></fo:table-cell>
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Zesco Power Cut</fo:block></fo:table-cell>
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Electrical</fo:block></fo:table-cell>
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Mech</fo:block></fo:table-cell>
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Inst</fo:block></fo:table-cell>
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>General</fo:block></fo:table-cell>
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>FCB FULL</fo:block></fo:table-cell>
            <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Reasons</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
        <#list stoppageAnalysisMap.entrySet() as entry>
            <fo:table-row font-weight="bold" border-style="solid">
                <fo:table-cell number-columns-spanned="11">
                    <fo:block  font-size="12pt">
                        <#assign heading = entry.key/>
                        ${heading?if_exists}
                    </fo:block>
                </fo:table-cell>
            </fo:table-row>
            <#assign rowColor = "white">
            <#list entry.value as value>
                <fo:table-row>
                    <fo:table-cell background-color="${rowColor}">
                        <fo:block>
                            ${value.startDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")?if_exists} to
                            ${value.completeDate?date("yyyy-MM-dd")?string("dd/MM/yyyy")?if_exists}
                        </fo:block>
                    </fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.rHrs?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.downtime?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.productionTime?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.ZescoPowerCut?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.Electrical?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.Mechanical?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.Instrumentation?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.General?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.FcbFull?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${value.reason?if_exists}</fo:block></fo:table-cell>
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