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
<fo:block font-size="15pt" text-align="center" font-weight="bold">ORIENTATION FORM</fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="12pt" border-style="solid" border-color="black">
    <fo:table-column></fo:table-column>
    <fo:table-column></fo:table-column>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>Department</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>HUMAN RESOURCES</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>Document id</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>ZPC/HR/SF 010</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>Revision no.</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>0.00</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>Revision date</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>25/08/2013</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>no. of pages</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>1.0</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>iso 9001-2008 clause</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>6.2.2d</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<fo:block><fo:leader/></fo:block>
<fo:block>From : ___________________________</fo:block>
<fo:block>To&#160;&#160;&#160;&#160;&#160;: ___________________________</fo:block>
<fo:block>Date&#160;: ___________________________</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>This serves to confirm that Mr/Mrs/Ms <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:inline>
    NRC No. <#if employee.nrcNo?exists> <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.nrcNo}</fo:inline><#else>......................... </#if> &#32;
    is on an orientation programme as <#if description?exists><fo:inline border-bottom-style="dotted" font-weight="bold">${description}</fo:inline> <#else>.......................</#if>&#32;
    in the <#if departmentName?exists><fo:inline border-bottom-style="dotted" font-weight="bold">${departmentName}</fo:inline><#else>............................ </#if> &#32;
    department. Kindly receive him/her and assist the officer to familiarize with the operations of your department. </fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
    <fo:table-column column-width="proportional-column-width(1)"></fo:table-column>
    <fo:table-column column-width="proportional-column-width(2)"></fo:table-column>
    <fo:table-column column-width="proportional-column-width(2)"></fo:table-column>
    <fo:table-column column-width="proportional-column-width(2)"></fo:table-column>
    <fo:table-column column-width="proportional-column-width(2)"></fo:table-column>
    <fo:table-column column-width="proportional-column-width(2)"></fo:table-column>
    <fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Department</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Inducted By</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Date</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Signature</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Employee&#39;s Signature</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
        <#list 1..11 as i>
            <fo:table-row>
                <fo:table-cell border-style="solid"><fo:block>${i}</fo:block></fo:table-cell>
                <fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>
                <fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>
                <fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>
                <fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>
                <fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>
            </fo:table-row>
        </#list>
    </fo:table-body>
</fo:table>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Signed by: _______________________ Position: ____________________ Date: _______________</fo:block>
</#escape>