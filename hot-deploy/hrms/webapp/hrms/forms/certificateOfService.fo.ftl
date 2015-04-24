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
    <#assign employeTerimationDetailList = delegator.findByAnd("MaxTerminationDetail",{"partyId":employeeId,"statusId":"ET_ADM_APPROVED"})>
    <#if employeTerimationDetailList?has_content>
        <#assign employeTerimationDetail= employeTerimationDetailList[0]>
        <#assign durationOfEmployement = Static["org.ofbiz.base.util.UtilDateTime"].formatInterval(employmentList[0].fromDate,employeTerimationDetailList[0].terminationDate,Static["java.util.Locale"].ENGLISH)>
    </#if>
<fo:block font-size="15pt" text-align="center" font-weight="bold">
    CERTIFICATE OF SERVICE
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed">
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell font-weight="bold" padding-top="15pt"><fo:block>NAME:</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block>${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block></fo:block></fo:table-cell>
            <fo:table-cell font-weight="bold" padding-top="15pt"><fo:block>MAN No:</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block>${employeeId?if_exists}</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell font-weight="bold" padding-top="15pt"><fo:block>NRC No.:</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block>${employee.nrcNo?if_exists}</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block></fo:block></fo:table-cell>
            <fo:table-cell font-weight="bold" padding-top="15pt"><fo:block>SSS No:</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block>${employee.socialSecurityNumber?if_exists}</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell font-weight="bold" padding-top="15pt"><fo:block>POSITION:</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block>${description?if_exists}</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block></fo:block></fo:table-cell>
            <fo:table-cell font-weight="bold" padding-top="15pt"><fo:block>DEPARTMENT:</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block>${departmentName?if_exists}</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell font-weight="bold" padding-top="15pt"><fo:block>DATE JOINED:</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block>${joiningDate?if_exists}</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt"><fo:block></fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt" font-weight="bold"><fo:block>DATE LEFT:</fo:block></fo:table-cell>
            <fo:table-cell padding-top="15pt">
            <#if employeTerimationDetail?exists>
                <fo:block>
                    <fo:inline border-bottom-style="dotted" font-weight="bold">${employeTerimationDetail.terminationDate?string("dd/MM/yyyy")?if_exists}</fo:inline>
                </fo:block>
                <#else>
                <fo:block></fo:block>
            </#if>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell  padding-top="15pt" number-columns-spanned="5">
                <fo:block>
                    <fo:inline font-weight="bold">NATURE OF TERMINATION:&#160;&#160;&#160;&#160;</fo:inline>
                    <fo:inline>
                        <#if employeTerimationDetail?exists>
                            ${employeTerimationDetail.reason}
                        </#if>
                    </fo:inline>
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<fo:block><fo:leader/></fo:block>
<fo:block text-align="center" font-weight="bold" text-decoration="underline">RECORD OF SERVICE</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" border-style="solid" border-color="black">
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-body>
        <fo:table-row font-weight="bold">
            <fo:table-cell border-style="solid"><fo:block>FROM</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>TO</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>POSITION</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>${joiningDate?if_exists}</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid">
                <#if employeTerimationDetail?exists>
                    <fo:block>
                        <fo:inline border-bottom-style="dotted" font-weight="bold">${employeTerimationDetail.terminationDate?string("dd/MM/yyyy")?if_exists}</fo:inline>
                    </fo:block>
                <#else>
                    <fo:block></fo:block>
                </#if>
            </fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>${description?if_exists}</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<fo:block><fo:leader/></fo:block>
<fo:block>
    Signed by: <fo:inline font-weight="bold">......................................</fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>
    Position: HUMAN RESOURCE MANAGER
    <fo:inline padding-left="50pt">Signature: ...........................................</fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>
    In witness of <fo:inline font-weight="bold">.....................................</fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>
    Position: ASSISTANT HUMAN RESOURCE MGR
    <fo:inline padding-left="10pt">Signature: ...........................................</fo:inline>
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>
    Date: ..................................
</fo:block>


<fo:block padding-top="50pt" font-style="italic">
    Please reject this record if found tempered with.
</fo:block>
</#escape>