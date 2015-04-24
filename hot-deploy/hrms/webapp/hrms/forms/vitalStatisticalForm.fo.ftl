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
<#assign telecomNumberDetails = Static["org.ofbiz.party.contact.ContactMechWorker"].getPartyContactMechValueMaps(delegator,employeeId,false,"TELECOM_NUMBER")/>
    <#list telecomNumberDetails as tel>
        <#assign contactMechId = tel.telecomNumber.contactMechId>
        <#assign PartyContactMechPurpose = delegator.findByAnd("PartyContactMechPurpose", {"contactMechId":contactMechId})/>
        <#if PartyContactMechPurpose?has_content>
           <#if PartyContactMechPurpose[0].contactMechPurposeTypeId="PRIMARY_PHONE">
                <#assign telephone = tel.telecomNumber>
           </#if>
        </#if>
    </#list>
<fo:block font-size="15pt" text-align="center" font-weight="bold">EMPLOYMENT STATISTICAL FORM 2</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Surname: <fo:inline border-bottom-style="dotted" font-weight="bold">&#32;${employee.lastName?if_exists}&#32;</fo:inline> <fo:inline padding-left="5pt">&#32;</fo:inline> Other Names: ............................................................</fo:block>

<fo:block>Works No: ............................................
    <fo:inline padding-left="5pt">&#32;</fo:inline>
    Dept: <fo:inline border-bottom-style="dotted" font-weight="bold">${departmentName?if_exists}&#32;</fo:inline>
    <fo:inline padding-left="5pt">&#32;</fo:inline>
    Title: <fo:inline border-bottom-style="dotted" font-weight="bold">${description?if_exists}&#32;</fo:inline></fo:block>

<fo:block>Date of Birth:
    <#if employee.birthDate?exists>
    <fo:inline border-bottom-style="dotted" font-weight="bold">
        ${employee.birthDate?string("dd/MM/yyyy")}&#32;
    </fo:inline>
    <#else>
        <fo:inline>.....................</fo:inline>
    </#if>
    <fo:inline padding-left="5pt">&#32;</fo:inline>
    Entry Grade: <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.grades?if_exists}&#32;</fo:inline></fo:block>

<fo:block>NRC/Passport No:
    <#if employee.nrcNo?exists>
        <fo:inline border-bottom-style="dotted" font-weight="bold">
                   ${employee.nrcNo}
        </fo:inline>
    <#else>
        <#if employee.passportNumber?exists>
            <fo:inline border-bottom-style="dotted" font-weight="bold">
            ${employee.passportNumber}
            </fo:inline>
        <#else>
            ......................................................
        </#if>
    </#if>
        <fo:inline padding-left="5pt">&#32;</fo:inline>
        Nationality:
        <#if employee.nationality?exists>
            <fo:inline border-bottom-style="dotted" font-weight="bold">${employee.nationality}</fo:inline>
        <#else>.....................................................
        </#if>
</fo:block>

<fo:block>Village: .....................................
    <fo:inline padding-left="5pt">&#32;</fo:inline>Chief: ..........................................
    <fo:inline padding-left="5pt">&#32;</fo:inline>District: ....................................... </fo:block>
<fo:block>Present Address:
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
        <fo:block><fo:leader leader-pattern="dots" leader-length="19cm"></fo:leader></fo:block>
        <fo:block><fo:leader leader-pattern="dots" leader-length="19cm"></fo:leader></fo:block>
    </#if>
</fo:block>

<fo:block>
    Tel:
    <#if telephone?exists>
        <fo:inline>${telephone.countryCode?if_exists}&#32;&#32;</fo:inline><fo:inline>${telephone.areaCode?if_exists}&#32;&#32;</fo:inline><fo:inline>${telephone.contactNumber?if_exists}&#32;&#32;</fo:inline>
    <#else>
        ..................................................................................
    </#if>
</fo:block>

<fo:block>Marital Status:
    <#if employee.maritalStatus?exists>
    <fo:inline border-bottom-style="dotted" font-weight="bold">
            <#if employee.maritalStatus = "U">
                Unmarried&#32;
            <#else>
                Married&#32;
            </#if>
    </fo:inline>
    <#else>
    .................................
    </#if>
    Name of Spouse:
        <#if spouse?exists>
            <fo:inline border-bottom-style="dotted" font-weight="bold">
        ${spouse.dependantName?if_exists}&#32;
        </fo:inline>
          <#else>
            ......................................
        </#if>

    DOB:
    <#if spouse?exists>
    <fo:inline border-bottom-style="dotted" font-weight="bold">
    ${spouse.dateOfBirth?string("dd/MM/yyyy")?if_exists}&#32;
    </fo:inline>
    <#else>
        .......................
    </#if>
</fo:block>
<fo:block><fo:inline padding-left="178pt">
    NRC No:
    <#if spouse?exists>
        <#if spouse.nrcNo?has_content>
            <fo:inline border-bottom-style="dotted" font-weight="bold"> ${spouse.nrcNo} &#32;</fo:inline>

    </#if>
    <#else>
        ......................................

    </#if>

</fo:inline>
    <fo:inline> Tel: .................................</fo:inline></fo:block>
<fo:block><fo:leader/></fo:block>




<fo:block>Children&#39;s Names (Under 18 yrs)</fo:block>
<fo:block><fo:leader/></fo:block>
    <#if childrenUnder18?has_content >
        <#list childrenUnder18 as children>
            <fo:table>
                <fo:table-column/>
                <fo:table-column/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell><fo:block>${children_index+1}. ${children.dependantName?if_exists}</fo:block></fo:table-cell>
                        <fo:table-cell><fo:block>DOB: ${children.dateOfBirth?string("dd/MM/yyyy")?if_exists}</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        </#list>
        <#assign remaining = childrenUnder18?size+1>
        <#list remaining..6 as i>
        <fo:table>
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell><fo:block>${i}. ............................</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block>DOB: .................................</fo:block></fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        </#list>
    <#else>
        <#list 1..6 as i>
        <fo:table>
            <fo:table-column/>
            <fo:table-column/>
            <fo:table-body>
                <fo:table-row>
                    <fo:table-cell><fo:block>${i}. ............................</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block>DOB: .................................</fo:block></fo:table-cell>
                </fo:table-row>
            </fo:table-body>
        </fo:table>
        </#list>
    </#if>
<fo:block><fo:leader/></fo:block>



<fo:block>Details of Parents (* Cancel what is not applicable)</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Father&#39;s Name:
    <#if employee.fatherName?exists>
        ${employee.fatherName?if_exists}&#32;
    <#else>
        .......................................
    </#if>

    Living/Deceased*</fo:block>
<fo:block>Mother&#39;s Name:
    <#if employee.mothersMaidenName?exists>
    ${employee.mothersMaidenName?if_exists}&#32;
    <#else>
        .......................................
    </#if>
    Living/Deceased*</fo:block>
<fo:block>Next of Kin or assignee: <fo:leader leader-pattern="dots" leader-length="14.2cm"></fo:leader></fo:block>
<fo:block>Relationship: <fo:leader leader-pattern="dots" leader-length="16.2cm"></fo:leader></fo:block>
<fo:block>Address of Next Kin: <fo:leader leader-pattern="dots" leader-length="14.8cm"></fo:leader></fo:block>
<fo:block><fo:inline padding-left="111pt">...........................................................................</fo:inline> Tel: ..........................................</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Signature: ........................................................................ Date: ..........................................................</fo:block>
<fo:block>Employment Commencement Date: <fo:leader leader-pattern="dots" leader-length="11.8cm"></fo:leader></fo:block>
</#escape>