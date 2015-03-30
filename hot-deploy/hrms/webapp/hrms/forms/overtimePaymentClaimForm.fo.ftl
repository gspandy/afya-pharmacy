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
<#--<fo:block font-size="15pt" text-align="center" font-weight="bold">ZAMBEZI PORTLAND CEMENT  COMPANY LIMITED</fo:block>-->
<fo:block font-size="15pt" text-align="center" font-weight="bold">AUTHORISATION FORM  TO PAY OVERTIME </fo:block>
<fo:block font-size="8pt" text-align="right">Form No. (for ISO listing)</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="12pt">
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell><fo:block>EMPLOYEES NAME <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:inline></fo:block></fo:table-cell>
            <fo:table-cell><fo:block>MAN  No <fo:inline border-bottom-style="dotted" font-weight="bold">${employeeId?if_exists}</fo:inline></fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block>DESIGNATION
                <#if description?exists>
                    <fo:inline border-bottom-style="dotted" font-weight="bold">${description}</fo:inline>&#32;
                <#else>
                    .............................................
                </#if>
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>GRADE
                <#if employee.grades?exists>
                    <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.grades?if_exists}</fo:inline>&#32;
                <#else>
                    .............................................
                </#if>

            </fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell><fo:block>DEPARTMENT
                <#if departmentName?exists>
                    <fo:inline border-bottom-style="dotted" font-weight="bold">${departmentName?if_exists}</fo:inline>
                <#else>
                    .............................................
                </#if>
            </fo:block></fo:table-cell>
            <fo:table-cell><fo:block>SECTION .............................................</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell number-columns-spanned="2"><fo:block>SHIFT/GENERAL WORKER (delete what is not applicable) ................................................</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<fo:block><fo:leader/></fo:block>
<fo:block>(ATTACH APPROVED AUTHORITY TO WORK OVERTIME) MONTH OVERTIME WORK ..............</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(3)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(3)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-header>
        <fo:table-row>
            <fo:table-cell border-style="solid">
                <fo:block>DATE</fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block>DESCRIPTION OF WORK</fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block>HOURS REQUESTED</fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block>ACTUAL HOURS
                    WORKED</fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block>ACTUAL
                    HOURS
                    APPROVED</fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block>WEEK
                    DAYS</fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block>SUNDAYS
                    AND PUBLIC
                    HOLIDAYS</fo:block>
            </fo:table-cell>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
    <#list 1..8 as i>
        <fo:table-row>
            <fo:table-cell border-style="solid">
                <fo:block></fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block></fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block></fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:table>
                    <fo:table-column/>
                    <fo:table-column/>
                    <fo:table-body>
                        <fo:table-row>
                            <fo:table-cell number-columns-spanned="2" border-bottom-style="solid" height="0.5cm">

                            </fo:table-cell>
                        </fo:table-row>
                        <fo:table-row>
                            <fo:table-cell border-right-style="solid">
                                <fo:block>From</fo:block>
                            </fo:table-cell>
                            <fo:table-cell>
                                <fo:block>To</fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block></fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block></fo:block>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:block></fo:block>
            </fo:table-cell>
        </fo:table-row>
    </#list>
        <fo:table-row height="1cm">
            <fo:table-cell number-columns-spanned="2" border-style="solid" >
                <fo:table table-layout="fixed">
                    <fo:table-column column-width="proportional-column-width(2)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-body>
                        <fo:table-row height="1cm">
                            <fo:table-cell border-right-style="solid"><fo:block>STAFF NUMBER</fo:block></fo:table-cell>
                            <fo:table-cell border-right-style="solid"></fo:table-cell>
                            <fo:table-cell border-right-style="solid"></fo:table-cell>
                            <fo:table-cell border-right-style="solid"></fo:table-cell>
                            <fo:table-cell></fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:table>
                    <fo:table-column/>
                    <fo:table-column/>
                    <fo:table-body>
                        <fo:table-row height="1cm">
                            <fo:table-cell border-right-style="solid" ><fo:block>&#160;</fo:block></fo:table-cell>
                            <fo:table-cell ><fo:block>&#160;</fo:block></fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
            </fo:table-cell>
            <fo:table-cell border-style="solid">
                <fo:table table-layout="fixed">
                    <fo:table-column column-width="proportional-column-width(4)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-column column-width="proportional-column-width(1)"/>
                    <fo:table-body>
                        <fo:table-row height="1cm">
                            <fo:table-cell border-right-style="solid" ><fo:block>Departmental Code (if any)</fo:block></fo:table-cell>
                            <fo:table-cell border-right-style="solid"></fo:table-cell>
                            <fo:table-cell border-right-style="solid"></fo:table-cell>
                            <fo:table-cell ></fo:table-cell>
                        </fo:table-row>
                    </fo:table-body>
                </fo:table>
            </fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>TOTAL</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
<fo:block><fo:leader/></fo:block>
<fo:block font-size="12pt">PAYMENT  REQUESTED BY ............................. Designation .............................. <fo:inline padding-left="10pt"> .............................</fo:inline></fo:block>
<fo:block><fo:inline padding-left="185pt" font-size="6pt">(NAME)</fo:inline><fo:inline padding-left="245pt" font-size="6pt">(SIGNATURE)</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-size="12pt">OVERTIME PAYMENT APPROVED BY ......................   <fo:inline padding-left="25pt">DESIGNATION .................................</fo:inline></fo:block>
<fo:block><fo:inline padding-left="235pt" font-size="6pt">(NAME)</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-size="12pt">
    <fo:inline>DATE STAMP</fo:inline>
    <fo:inline padding-left="250pt">SIGNATURE</fo:inline>
</fo:block>
</#escape>