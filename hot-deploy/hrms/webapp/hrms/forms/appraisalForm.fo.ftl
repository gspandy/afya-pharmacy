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
<fo:block font-weight="bold" font-size="12pt">Performance Appraisal Assessment Form for Confirmation of Appointment</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table>
    <fo:table-column></fo:table-column>
    <fo:table-column></fo:table-column>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell><fo:block>Name: <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:inline></fo:block></fo:table-cell>
            <fo:table-cell><fo:block>Department: <fo:inline border-bottom-style="dotted" font-weight="bold">${departmentName?if_exists}</fo:inline></fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block>Man No: <fo:inline border-bottom-style="dotted" font-weight="bold">${employeeId?if_exists}</fo:inline></fo:block></fo:table-cell>
            <fo:table-cell><fo:block>Date of Appointment: <fo:inline border-bottom-style="dotted" font-weight="bold">${joiningDate?if_exists}</fo:inline></fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block>Position: <fo:inline border-bottom-style="dotted" font-weight="bold">${description?if_exists}</fo:inline>&#32;</fo:block></fo:table-cell>
            <fo:table-cell><fo:block></fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
    <fo:table-column></fo:table-column>
    <fo:table-column></fo:table-column>
    <fo:table-column></fo:table-column>
    <fo:table-column></fo:table-column>
    <fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border-style="solid"><fo:block>Technical &amp; Personal Attributes</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Features</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Supervisor remarks</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Scores out of 20 points</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>Knowledge of the Job</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Technical Knowledge of the Job and practical abilities/ understanding facts and factors of the job</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>Quality of work</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Accuracy, neatness and carefulness on the job</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>Diligence</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Consistency in work output and ability to meet targets given</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>Initiative</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Self starting ability and eagerness to widen work range</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>Decision Making and judgment ability</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Ability to good judgment under difficult circumstances</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block>Self Discipline </fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Balanced attribute towards work </fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Not to be rated on score sheet.</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block></fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Total Scores</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-size="10pt">Key</fo:block>
<fo:block font-size="10pt">0-49%   <fo:inline font-weight="bold">below standard</fo:inline> &#45; Unsuccessful Probation</fo:block>
<fo:block font-size="10pt">50-69% <fo:inline font-weight="bold">average performance</fo:inline> &#45; Extension of probation or confirmation upon HOD&#39;S recommendation</fo:block>
<fo:block font-size="10pt">70- 100% <fo:inline font-weight="bold">Very good performance</fo:inline> &#45; Successful probation and suitable for confirmation</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table font-size="10pt">
    <fo:table-column></fo:table-column>
    <fo:table-column></fo:table-column>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell><fo:block>Signed by Supervisor: ........................................................</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>Date:  .........................................................</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block>Signed by: Assesee: ..........................................................</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>Date:  .........................................................</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block>HOD&#39;s recommendation: ....................................................</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>Date:  .........................................................</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block>H/R Recommendation: .......................................................</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>Date:  .........................................................</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block>Approved by: ......................................................................</fo:block></fo:table-cell>
            <fo:table-cell><fo:block>Date:  .........................................................</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
</#escape>