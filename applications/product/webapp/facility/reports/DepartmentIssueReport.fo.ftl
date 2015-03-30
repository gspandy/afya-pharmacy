<#escape x as x?xml>
    <#if parameters.departmentId?has_content>
        <#assign department = delegator.findOne("PartyGroup", {"partyId" : parameters.departmentId}, false)>
    </#if>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Department Issuance</fo:block>
<fo:block>
    <fo:inline font-weight="bold">Department : </fo:inline>
    <fo:inline >
        <#if department?exists>
        ${department.groupName}
        <#else>
            All
        </#if>
    </fo:inline>
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
    <#if departmentIssueReportMapGroupedByProductCategory?has_content>

    <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(5)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(1)"/>
        <fo:table-column column-width="proportional-column-width(2)"/>
        <fo:table-header>
            <fo:table-row font-weight="bold" border-bottom-style="solid" border-bottom-color="grey">
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Date</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Material Description</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Unit</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Requisition No.</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Requested Qty</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Issued Qty</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Cost/Item</fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Total Cost </fo:block></fo:table-cell>
                <fo:table-cell border-bottom="thin solid grey" background-color="#D4D0C8"><fo:block>Cost Center</fo:block></fo:table-cell>
            </fo:table-row>
        </fo:table-header>
        <fo:table-body>
            <#--<#list departmentIssueReportListGroupedByProductCategory as dirl>-->
                <#list departmentIssueReportMapGroupedByProductCategory.entrySet() as entry>
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
                <#list entry.value as value>
                    <fo:table-row>
                        <fo:table-cell background-color="${rowColor}"><fo:block>${value.requestDate?string("dd/MM/yyyy")?if_exists}</fo:block></fo:table-cell>
                        <fo:table-cell background-color="${rowColor}"><fo:block>${value.productDescription?if_exists}</fo:block></fo:table-cell>
                        <fo:table-cell background-color="${rowColor}"><fo:block>${value.unitDescription?if_exists}</fo:block></fo:table-cell>
                        <fo:table-cell background-color="${rowColor}"><fo:block>${value.invRequisitionId?if_exists}</fo:block></fo:table-cell>
                        <fo:table-cell background-color="${rowColor}"><fo:block>${value.quantity?if_exists}</fo:block></fo:table-cell>
                        <fo:table-cell background-color="${rowColor}"><fo:block>${value.quantity?if_exists}</fo:block></fo:table-cell>
                        <fo:table-cell background-color="${rowColor}"><fo:block>${value.unitCost?if_exists}</fo:block></fo:table-cell>
                        <fo:table-cell background-color="${rowColor}"><fo:block>${value.unitCost * value.quantity}</fo:block></fo:table-cell>
                        <fo:table-cell background-color="${rowColor}"><fo:block>${value.costCenter?if_exists}</fo:block></fo:table-cell>
                    </fo:table-row>
                    <#if rowColor == "white">
                        <#assign rowColor = "#D4D0C8">
                    <#else>
                        <#assign rowColor = "white">
                    </#if>
                </#list>
                </#list>
            <#--</#list>-->
        </fo:table-body>
    </fo:table>
    <#else>
    <fo:block>No Record Found.</fo:block>
    </#if>

</#escape>