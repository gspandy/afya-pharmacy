<#escape x as x?xml>
    <#assign requisitionId = parameters.requisitionId>
    <#assign requisition = delegator.findOne("EmployeeRequisition", {"requisitionId" : requisitionId}, false)>
    <#if requisition?has_content>
        <#if requisition.approverPositionId?has_content>
            <#assign approver = Static["com.ndz.zkoss.HrmsUtil"].getEmployeeName(requisition.approverPositionId)>
        </#if>
        <#if requisition.partyId?has_content>
            <#assign managerName = Static["com.ndz.zkoss.HrmsUtil"].getFullName(delegator,requisition.partyId) >
        </#if>
        <#assign jobDesc = requisition.jobDescription?replace("</?[^>]+(>|$)", "", "r")>
        <#assign qualification = requisition.qualification?replace("</?[^>]+(>|$)", "", "r")>
        <#assign exprience = requisition.minExprience + "-" +requisition.maxExprience >
        <#assign department = Static["com.ndz.zkoss.HrmsUtil"].getDepartmentName(requisition.reqRaisedByDept)>
        <#assign position = Static["com.ndz.zkoss.HrmsUtil"].getPositionTypeDescription(requisition.positionType)>
        <#assign numberOfPosition = requisition.numberOfPosition>
            <#if requisition.replacementpositionId?has_content>
                <#assign emplPositionFulfillment = Static["com.ndz.zkoss.HrmsUtil"].getEmployeePositionFulfillment(requisition.replacementpositionId)>
                    <#if emplPositionFulfillment?has_content>
                        <#assign replacementOf = Static["com.ndz.zkoss.HrmsUtil"].getFullName(delegator,emplPositionFulfillment.partyId)>
                    </#if>
            </#if>
    </#if>
<fo:block font-size="15pt" text-align="center" font-weight="bold"> MANPOWER REQUISITION FORM TO EMPLOY</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>From:&#160;&#160;
    <fo:inline font-weight="bold">Department </fo:inline>
    <#if department?exists>
        <fo:inline border-bottom-style="dotted" font-weight="bold">${department}</fo:inline>
    <#else>
    ............................................
    </#if>

</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>Kindly process request to employ</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block margin-left="25pt">1.&#160;&#160;Position requested for
    <#if position?exists>
        <fo:inline border-bottom-style="dotted" font-weight="bold">${position}</fo:inline>
    <#else>
    ..................................
    </#if>
    <fo:inline padding-left="50pt">No. required</fo:inline>
    <#if numberOfPosition?exists>
        <fo:inline border-bottom-style="dotted" font-weight="bold">${numberOfPosition?if_exists}</fo:inline>
    <#else>
    ..................................
    </#if>
    </fo:block>
<fo:block margin-left="25pt">2.&#160;&#160;The vacancy has arisen due to</fo:block>
<fo:block margin-left="50pt">a)&#160;&#160;Replacement of
    <#if replacementOf?exists>
        <fo:inline border-bottom-style="dotted" font-weight="bold">${replacementOf}</fo:inline>
    <#else >
    ...............................................................
    </#if>

</fo:block>
<fo:block margin-left="50pt">b)&#160;&#160;New Position due to ..........................................................................................................</fo:block>
<fo:block margin-left="25pt">3.&#160;&#160;Professional Qualifications required
    <#if qualification?exists>
        <fo:inline border-bottom-style="dotted" font-weight="bold">${qualification}</fo:inline>
    <#else >
    ..........................................
    </#if>
</fo:block>
<fo:block margin-left="25pt">4.&#160;&#160;Relevance field experience required
    <#if exprience?exists>
        <fo:inline border-bottom-style="dotted" font-weight="bold"> ${exprience}</fo:inline>
    <#else >
    ........................................................
    </#if>

</fo:block>
<fo:block margin-left="25pt">5.&#160;&#160;Summary of the job/ job description
    <#if jobDesc?exists>
        <fo:inline border-bottom-style="dotted" font-weight="bold">${jobDesc?if_exists}</fo:inline>
    <#else >
    .....................................................
    </#if>
</fo:block>
<fo:block margin-left="25pt">6.&#160;&#160;The job holder reports to
    <#if managerName?exists>
        <fo:inline border-bottom-style="dotted" font-weight="bold">${managerName?if_exists}</fo:inline>
    <#else >
    ...........................................
    </#if>
</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-weight="bold">Requested by: Name
    <#if managerName?exists>
        <fo:inline border-bottom-style="dotted" font-weight="bold">${managerName?if_exists}</fo:inline>
    <#else >
        ...........................................
    </#if>
    Signature .............................. Date ...................................</fo:block>
<fo:block font-weight="bold">HOD for Operations Staff &amp; GM for Technical dept: <fo:inline border-bottom-style="dotted" font-weight="bold">${approver?if_exists}</fo:inline></fo:block>
<fo:block font-weight="bold">Name ....................................... Signature ......................................... Date ..........................................</fo:block>
<fo:block>Verification by Human Resource department ..........................................................................................</fo:block>
<fo:block  ><fo:leader leader-pattern="dots" leader-length="19cm"></fo:leader></fo:block>
<fo:block><fo:inline font-weight="bold">Human Resource Manager&#39;s</fo:inline> recommendation ....................................................................................</fo:block>
<fo:block  ><fo:leader leader-pattern="dots" leader-length="19cm"></fo:leader></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block font-weight="bold">Signature ................................................................. Date ...................................................................</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block >Approved/ Not approved by ED/CEO ...........................................  Signature: ....................................... </fo:block>
<fo:block>Date : ..................................</fo:block>
</#escape>