    <#if requirementsList?has_content>
      <table class="basic-table hover-bar" cellspacing='0'>
        <tr class="header-row">
          <td width="10%">Requirement Id</td>
          <td width="15%">Status</td>
          <td width="15%">Requirement Type</td>
          <#-- <td width="10%">Product Id</td> -->
          <td width="15%">Product Name</td>
          <td width="15%">Created By</td>
          <td width="15%">From Department</td>
          <td width="15%">Requirement Start Date</td>
          <td width="10%" style="text-align:right;">Quantity &nbsp;&nbsp;</td>
        <#-- <td width="10%">Orders Link</td>-->
        </tr>
        <#assign alt_row = false>
        <#list requirementsList as requirement>
          <#assign status = requirement.getRelatedOneCache("StatusItem")>
          <#assign reqType = requirement.getRelatedOneCache("RequirementType")>
          <#assign product = requirement.getRelatedOneCache("Product")>
          <#assign partyAndUserLoginAndPerson = Static["org.ofbiz.entity.util.EntityUtil"].getFirst(delegator.findByAnd("PartyAndUserLoginAndPerson", {"userLoginId" : requirement.createdByUserLogin?if_exists}))/>
          <#assign facility = requirement.getRelatedOneCache("Facility")>
          <tr<#if alt_row> class="alternate-row"</#if>>
            <#assign alt_row = !alt_row>
            <td><a href="/ordermgr/control/ApproveRequirements?requirementId=${requirement.requirementId}" class="btn btn-link">${requirement.requirementId}</a></td>
            <td>${status.description?if_exists}</td>
            <td>${reqType.description?if_exists}</td>
            <#-- <td>${requirement.productId?if_exists}</td> -->
            <td>${product.description?if_exists}</td>
            <td>${partyAndUserLoginAndPerson.firstName?if_exists} ${partyAndUserLoginAndPerson.lastName?if_exists}</td>
            <#if facility.ownerPartyId?has_content>
                <#assign ownerParty = delegator.findByPrimaryKey("PartyGroup",{"partyId":facility.ownerPartyId})>
                <td>
                    <#if ownerParty?has_content>
                        ${ownerParty.groupName?if_exists}
                    </#if>
                </td>
            <#else>
                <td>${facility.ownerPartyId?if_exists}</td>
            </#if>
            <td><#if requirement.requirementStartDate?has_content>${requirement.requirementStartDate?string("dd-MM-yyyy")}</#if></td>
            <td class="align-text">${requirement.quantity?if_exists}</td>
           <#-- <td><a href="/ordermgr/control/ListRequirementOrders?requirementId=${requirement.requirementId}" class="btn btn-link">Orders</a></td>-->
        </#list>
      </table>
    </#if>
