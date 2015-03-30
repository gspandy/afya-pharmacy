<#escape x as x?xml>
    <#assign employeeId = parameters.partyId>
    <#assign employee = delegator.findOne("Person", {"partyId" : employeeId}, false)>
    <#if parameters.positionId?has_content>
        <#assign emplPositionTypeList = delegator.findByAnd("EmplPosition",{"emplPositionId":parameters.positionId})>
        <#if emplPositionTypeList?has_content>
            <#assign emplPositionTypeGv= emplPositionTypeList[0]>
            <#assign positionType = emplPositionTypeGv.emplPositionTypeId>
            <#assign emplPosGv = delegator.findByPrimaryKey("EmplPositionType",{"emplPositionTypeId":positionType})>
            <#assign description = emplPosGv.description>
            <#assign departmentId = emplPositionTypeGv.partyId>
            <#assign departmentPositionList = delegator.findByAnd("DepartmentPosition", {"departmentId":departmentId})>
            <#if departmentPositionList?has_content>
                <#assign departmentPosition = departmentPositionList[0]>
                <#assign departmentName = departmentPosition.departmentName>
            </#if>
        </#if>
    </#if>
    <#if employeeId?has_content>
        <#assign employmentList= delegator.findByAnd("Employment",{"partyIdTo":employeeId})>
        <#assign joiningDate = employmentList[0].fromDate?string("dd/MM/yyyy")>
    </#if>
<fo:block font-size="15pt" text-align="center" font-weight="bold" text-decoration="underline">BANK TRANSFER AUTHORISATION FORM</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>I &#32;(FULL NAME OF EMPLOYEE) <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>N.R.C NO <#if employee.nrcNo?exists><fo:inline border-bottom-style="dotted" font-weight="bold">${employee.nrcNo}</fo:inline><#else>..............................................................................................................................................</#if></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>OCCUPATION <fo:inline border-bottom-style="dotted" font-weight="bold">${description?if_exists}</fo:inline>&#32;</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>MAN NO <fo:inline border-bottom-style="dotted" font-weight="bold">${employeeId?if_exists}</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>SECTION/DEPARTMENT <#if departmentName?exists><fo:inline border-bottom-style="dotted" font-weight="bold">${departmentName}</fo:inline><#else>......................................................................................</#if></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>RESIDENTIAL ADDRESS</fo:block>
<fo:block>
    <#if postalAddress?exists>
    <fo:inline border-bottom-style="dotted" font-weight="bold">
    ${postalAddress.postalAddress.toName?if_exists}&#32;
    ${postalAddress.postalAddress.attnName?if_exists}&#32;
    ${postalAddress.postalAddress.address1?if_exists}&#32;
    ${postalAddress.postalAddress.address2?if_exists}&#32;
    ${postalAddress.postalAddress.countryGeoId?if_exists}&#32;
    ${postalAddress.postalAddress.stateProvinceGeoId?if_exists}&#32;
    </fo:inline>
        <#else>
            <fo:block><fo:leader leader-pattern="dots" leader-length="18.8cm"></fo:leader></fo:block>
            <fo:block><fo:leader leader-pattern="dots" leader-length="18.8cm"></fo:leader></fo:block>
    </#if>
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>REQUEST TO CHANGE PAY POINT</fo:block>
<fo:block>I HEREBY REQUEST MY EMPLOYER ZPC LTD TO EFFECT CHANGE OF PAY POINT FROM</fo:block>
<fo:block><fo:leader leader-pattern="dots" leader-length="18.8cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>TO</fo:block>
<fo:block><fo:leader leader-pattern="dots" leader-length="18.8cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>REASON</fo:block>
<fo:block><fo:leader leader-pattern="dots" leader-length="18.8cm"></fo:leader></fo:block>
<fo:block><fo:leader leader-pattern="dots" leader-length="18.8cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>SIGNATURE ......................................................</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>DATE: ................................................................</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>SIGNATURE BY</fo:block>
<fo:block>SUPERVISOR:..................................................</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>SIGNATURE BY</fo:block>
<fo:block>HOD: ............................................................. DATE ..................................................</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="12pt" border-style="solid" border-color="black">
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border-style="solid"><fo:block>DOCUMENT ID.</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>REV No.</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>REF.DOC.ID</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>PAGE</fo:block></fo:table-cell>
        </fo:table-row>
        </fo:table-header>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>ZPC/HR/SFO06</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>0</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>1 of 1</fo:block></fo:table-cell>
        </fo:table-row>
      </fo:table-body>
</fo:table>
</#escape>