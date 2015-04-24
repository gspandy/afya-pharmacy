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

<fo:block font-size="15pt" text-align="center" font-weight="bold">ARRIVAL ADVICE NOTE</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-family="Comic Sans MS">
    I <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:inline>
    NRC N0: <#if employee.nrcNo?exists> <fo:inline border-bottom-style="dotted" font-weight="bold">
     ${employee.nrcNo}
    </fo:inline>
<#else>
    ..............................
</#if>
    , hereby confirm that I have been offered employment
    by Zambezi Portland Cement Company Limited as <#if description?exists><fo:inline border-bottom-style="dotted" font-weight="bold">${description}</fo:inline><#else> .......................... </#if>&#32;
    under <fo:inline border-bottom-style="dotted" font-weight="bold">${departmentName?if_exists}</fo:inline> department.
    I further confirm that following this appointment,
    I have reported for work and took up appointment on <#if joiningDate?exists> <fo:inline border-bottom-style="dotted" font-weight="bold">${joiningDate}</fo:inline><#else> ...................... </#if> ,
    and this shall be the effective date of my first appointment.
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-family="Comic Sans MS">
    I wish to advise the Company to pay my monthly salary through my bank (Bank details)&#32;&#160;
    ....................................................
    account no. .......................................................
    at............................... Branch.
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-family="Comic Sans MS">
    <fo:inline>Signed by: ........................................................</fo:inline>
    <fo:inline>Signature: ........................................................</fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-family="Comic Sans MS">
    Date: .................................................................
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-family="Comic Sans MS">
    Signed by Administrative Officer of Behalf of Zambezi Portland Cement Company Ltd;
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-family="Comic Sans MS">
    <fo:inline>Signed by: ........................................................</fo:inline>
    <fo:inline>Signature: ........................................................</fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-family="Comic Sans MS">
    <fo:inline>Position: ............................................................   </fo:inline>
    <fo:inline>Date: ...................................................................  </fo:inline>
</fo:block>
</#escape>