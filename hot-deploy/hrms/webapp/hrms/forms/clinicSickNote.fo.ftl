<#escape x as x?xml>
    <#assign employeeId = parameters.partyId>
    <#assign employee = delegator.findOne("Person", {"partyId" : employeeId}, false)>
<#if employeeId?has_content>
    <#assign manager = Static["org.ofbiz.humanresext.util.HumanResUtil"].getReportingMangerForParty(employeeId,delegator)>
    <#if manager?has_content>
        <#assign managerPartyId = manager.partyId>
        <#assign fullnameOfManager = Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(managerPartyId)>
    </#if>
</#if>
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
<fo:block font-weight="bold">PART 1(To be completed by supervisor)</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>DATE ...................................... TIME ...................... DPT <fo:inline border-bottom-style="dotted" font-weight="bold">${departmentName?if_exists}</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>TO THE CLINIC PERSONNEL</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>NAME OF EMPLOYEE <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:inline><fo:inline padding-right="8pt">&#32;</fo:inline> MAN No <fo:inline border-bottom-style="dotted" font-weight="bold">${employeeId?if_exists}</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>The above employee has been granted permission to go to the clinic</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>NAME OF SUPERVISOR <#if fullnameOfManager?exists>
    <fo:inline border-bottom-style="dotted" font-weight="bold">${fullnameOfManager?if_exists}</fo:inline>
<#else>
....................................................
</#if> <fo:inline padding-right="8pt">&#32;</fo:inline> MAN No <#if managerPartyId?exists>
    <fo:inline border-bottom-style="dotted" font-weight="bold">${managerPartyId?if_exists}</fo:inline>
<#else>
.............................................
</#if>
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block border-bottom-style="solid"></fo:block>
<fo:block font-weight="bold">PART 2(To be completed at the clinic)</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>IMPRESSION/DIAGNOSE <fo:leader leader-pattern="dots" leader-length="13.5cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader leader-pattern="dots" leader-length="18.5cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>ADVISED:</fo:block>
<fo:block>
    <fo:inline padding-left="60pt">SENT BACK TO</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="25pt">&#x274F;</fo:inline>
    <fo:inline padding-left="60pt">SENT TO HOSPITAL</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="25pt">&#x274F;</fo:inline>
</fo:block>
<fo:block>
    <fo:inline padding-left="60pt">GIVEN SICK OFF</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="25pt">&#x274F;</fo:inline>
    <fo:inline padding-left="53pt">DETAINED FOR OBSERVATION</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="25pt">&#x274F;</fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>PERIOD TO BE SICK OFF/LIGHT DUTIES .......................................................................................</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>TIME RELEASED FROM CLINIC .......................................................................................................</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block margin-left="15pt">REVIEW DATE ..............................................................................................................................</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>SIGNATURE OF HEALTH OFFICIAL ............................................ DATE ........................................</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>FILL IN DUPLICATE IN CASE OF AN ACCIDENT(S)</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>ZPC/HR/SF/001</fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="12pt" border-style="solid" border-color="black">
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border-style="solid"><fo:block>DOCUMENT ID.</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>REV No.</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>REF.DOC </fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>DOC.OWNER</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>PAGE</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>ZPC/HR/SF/001</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>0</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>HR</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>1 of 1</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
</#escape>