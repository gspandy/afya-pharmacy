<#escape x as x?xml>
    <#assign employeeId = parameters.partyId>
    <#assign employee = delegator.findOne("Person", {"partyId" : employeeId}, false)>
    <#assign training = Static["com.ndz.zkoss.HrmsUtil"].getTrainingDetailsForTheEmployee(employeeId)>
    <#assign statusDescMap = Static["com.ndz.zkoss.HrmsUtil"].getStatusIdDescriptionMap("TRNG_STATUS")>
<fo:block>Training details of <fo:inline font-weight="bold"> ${employee.firstName?if_exists}&#32;${employee.middleName?if_exists}&#32;${employee.lastName?if_exists}</fo:inline></fo:block>
<fo:block><fo:leader></fo:leader></fo:block>
<#if training?has_content>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="12pt" border-style="solid" border-color="black">
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-column column-width="proportional-column-width(1)"/>
    <fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border-style="solid"><fo:block>Training ID</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Training Name</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>From Date</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Thru Date</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>Training Status</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
        <#list training as trng>
            <fo:table-row>
                <fo:table-cell border-style="solid"><fo:block>${trng.trainingId?if_exists}</fo:block></fo:table-cell>
                <fo:table-cell border-style="solid"><fo:block>${trng.trainingName?if_exists}</fo:block></fo:table-cell>
                <fo:table-cell border-style="solid"><fo:block>${trng.estimatedStartDate?string("dd/MM/yyyy")?if_exists}</fo:block></fo:table-cell>
                <fo:table-cell border-style="solid"><fo:block>${trng.estimatedCompletionDate?string("dd/MM/yyyy")?if_exists}</fo:block></fo:table-cell>
                <fo:table-cell border-style="solid">
                    <fo:block>
                        <#if statusDescMap?has_content>
                            <#assign statusId = trng.statusId>
                            <#if statusId?has_content>
                                ${statusDescMap[statusId]?if_exists}
                            </#if>
                        </#if>
                    </fo:block>
                </fo:table-cell>
            </fo:table-row>
        </#list>
    </fo:table-body>
</fo:table>
<#else>
<fo:block>No Records</fo:block>
</#if>

</#escape>