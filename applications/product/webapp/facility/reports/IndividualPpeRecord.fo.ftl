<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Employee Issuance</fo:block>
<fo:block>
    <fo:leader/>
</fo:block>
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
    <#if individualPpeRecordList?has_content>
    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(4)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-header>

            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-color="grey">
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Employee Name</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Employee Number</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Material Description</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Quantity</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Issued On</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <#assign rowColor = "white">
            <#list individualPpeRecordList as ipr>
                <fo:table-row>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${ipr.employeeName?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${ipr.employeeNumber?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}"><fo:block>${ipr.materialDescription?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}" margin-left="4px"><fo:block>${ipr.quantity?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell background-color="${rowColor}">
                        <fo:block>
                            <#if ipr.issuedOn?exists>
                                ${ipr.issuedOn?string("dd/MM/yyyy")}
                            </#if>
                        </fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <#if rowColor == "white">
                    <#assign rowColor = "#D4D0C8">
                <#else>
                    <#assign rowColor = "white">
                </#if>
            </#list>
        </fo:table-body>
    </fo:table>
    <#else>
    <fo:block>No Record Found.</fo:block>
    </#if>
</#escape>