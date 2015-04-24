<#escape x as x?xml>
    <#assign employeeId = parameters.partyId>
    <#assign employee = delegator.findOne("Person", {"partyId" : employeeId}, false)>
<fo:block font-size="15pt" text-align="center" font-weight="bold" text-decoration="underline">MEDICAL ENTITLEMENT FORM</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>NAME: <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}&#32;&#32;</fo:inline> MAN NO.: <fo:inline border-bottom-style="dotted" font-weight="bold">${employeeId?if_exists}</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>ENTITLEMENT (KR): <fo:leader leader-pattern="dots" leader-length="13.5cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(3)"/>
    <fo:table-column column-width="proportional-column-width(3)"/>
    <fo:table-column column-width="proportional-column-width(3)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell border-style="solid" font-weight="bold"><fo:block>DATE</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" font-weight="bold"><fo:block>AMOUNT B/FORWARD</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" font-weight="bold"><fo:block>AMOUNT OF REFUND/BILL</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" font-weight="bold"><fo:block>AMOUNT C/FORWARD</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" font-weight="bold"><fo:block>INSTITUTION</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" font-weight="bold"><fo:block>SIGN.</fo:block></fo:table-cell>
        </fo:table-row>
        <#list 30..1 as i>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
        </fo:table-row>
        </#list>
    </fo:table-body>
</fo:table>
</#escape>