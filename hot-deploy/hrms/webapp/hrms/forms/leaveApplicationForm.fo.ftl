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
<fo:block font-size="15pt" text-align="center" font-weight="bold">Leave Application Form</fo:block>
<fo:block border-style="solid" padding="5pt">
<fo:block font-weight="bold"><fo:inline border-bottom-style="solid">APPLICANT (To Be Completed by the Employee)</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed">
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-header>
        <fo:table-row>
            <fo:table-cell><fo:block>Emp No:</fo:block></fo:table-cell>
            <fo:table-cell><fo:block><fo:inline border-bottom-style="dotted" font-weight="bold">${employeeId?if_exists}</fo:inline></fo:block></fo:table-cell>
            <fo:table-cell><fo:block></fo:block></fo:table-cell>
            <fo:table-cell><fo:block>Full Name:</fo:block></fo:table-cell>
            <fo:table-cell><fo:block><fo:inline border-bottom-style="dotted" font-weight="bold">${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:inline></fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell><fo:block>Job Title:</fo:block></fo:table-cell>
            <fo:table-cell>
                <fo:block>
                    <#if description?exists>
                        <fo:inline border-bottom-style="dotted" font-weight="bold">${description?if_exists}</fo:inline>
                        <#else>
                        .............................................
                    </#if>
                </fo:block>
            </fo:table-cell>
            <fo:table-cell><fo:block></fo:block></fo:table-cell>
            <fo:table-cell><fo:block>Department:</fo:block></fo:table-cell>
            <fo:table-cell>
                <fo:block>
                    <#if departmentName?exists>
                    <fo:inline border-bottom-style="dotted" font-weight="bold">${departmentName?if_exists}</fo:inline>
                        <#else>
                        ..............................................
                    </#if>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<#--<fo:block>Emp No: <fo:inline border-bottom-style="dotted" font-weight="bold">${employeeId?if_exists}</fo:inline> Full Name: <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:inline></fo:block>
<fo:block>Job Title: <fo:inline border-bottom-style="dotted" font-weight="bold">${description?if_exists}</fo:inline> Department: <fo:inline border-bottom-style="dotted" font-weight="bold">${departmentName?if_exists}</fo:inline></fo:block>-->
<fo:block><fo:leader/></fo:block>
<fo:block>Leave Type being Applied For (Tick One(1) Only)</fo:block>
<fo:block>
    <fo:inline font-size="11pt">Annual Leave</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="16pt">&#x274F;</fo:inline>
    <fo:inline font-size="11pt">Compassionate Leave</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="16pt">&#x274F;</fo:inline>
    <fo:inline font-size="11pt">Sick Leave</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="16pt">&#x274F;</fo:inline>
    <fo:inline font-size="11pt">Maternity Leave</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="16pt">&#x274F;</fo:inline>
    <fo:inline font-size="11pt">Other Unpaid Leave</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="16pt">&#x274F;</fo:inline>
</fo:block>
<fo:block font-size="8pt" text-align="center">(Sick Note Required)</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Leave Commencement Date: ..................................... No. of Days Applied For ....................................</fo:block>
<fo:block>Free Days During Requested Leave Period: ...................... Expected Date of Return ...........................</fo:block>
<fo:block>Reason/s: <fo:leader leader-pattern="dots" leader-length="17cm"></fo:leader></fo:block>
<fo:block><fo:leader leader-pattern="dots" leader-length="19cm"></fo:leader></fo:block>
<fo:block>Contact Phone No(s) during Leave Period: <fo:leader leader-pattern="dots" leader-length="10.9cm"></fo:leader></fo:block>
<fo:block>Contact Address during Leave Period: <fo:leader leader-pattern="dots" leader-length="11.7cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Date ............................................... Applicants Signature:......................................................................</fo:block>
<fo:block border-bottom-style="solid"></fo:block>
<fo:block><fo:inline font-weight="bold">HUMAN RESOURCE DEPARTMENT</fo:inline> (To Be Completed by The HR Dept. only)</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Available Leave Days of Type Applied For: ........ No. of Days Applied For ........ Leave Balance ........</fo:block>
<fo:block font-size="10pt">(i.e Leave Type Ticked in Applicants Section)</fo:block>
<fo:block>Comment : <fo:leader leader-pattern="dots" leader-length="16.5cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Date: .......................... Designation: ................................. Signature: ..................................................</fo:block>
<fo:block border-bottom-style="solid"></fo:block>
<fo:block><fo:inline font-weight="bold">DEPARTMENT</fo:inline> (To Be Completed by Applicant's Department Only)</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>
    <fo:inline font-size="11pt">Leave Recommended</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="16pt">&#x274F;</fo:inline>
    <fo:inline font-size="11pt" padding-left="70pt">Leave </fo:inline><fo:inline font-size="11pt" font-weight="bold" text-decoration="underline">Not </fo:inline><fo:inline font-size="11pt">Recommended</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="16pt">&#x274F;</fo:inline>
</fo:block>
<fo:block>Comment: <fo:leader leader-pattern="dots" leader-length="16.5cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Date: .............................. Designation: .................................. Signature: ............................................</fo:block>
<fo:block border-bottom-style="solid"></fo:block>
<fo:block><fo:inline font-weight="bold">General Manager</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>
    <fo:inline font-size="11pt">Leave Recommended</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="16pt">&#x274F;</fo:inline>
    <fo:inline font-size="11pt" padding-left="70pt">Leave </fo:inline><fo:inline font-size="11pt" font-weight="bold" text-decoration="underline">Not </fo:inline><fo:inline font-size="11pt">Recommended</fo:inline><fo:inline font-family="ZapfDingbats" padding-left="5pt" font-size="16pt">&#x274F;</fo:inline>
</fo:block>
<fo:block>Comment: <fo:leader leader-pattern="dots" leader-length="16.5cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Date: .............................. Designation: .................................. Signature: ............................................</fo:block>
<fo:table table-layout="fixed" font-size="8pt" border-style="solid" border-color="black">
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>Document ID</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Revision No.</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Ref. Document</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Document Owner</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Page(s)</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>ZPC/HR/SF005</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>1</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block></fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>HR</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>1 of 1</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
</fo:block>
<fo:block  text-align="center" font-size="8pt">ADMINISTRATION (HR)</fo:block>
</#escape>