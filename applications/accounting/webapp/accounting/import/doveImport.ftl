<form method="post" enctype="multipart/form-data" action="uploadDoveImport">
    <input type="file" name="uploadedFile" size="20" required="true"/>
    <input type="submit" value="${uiLabelMap.CommonUpload}" class="btn" style="font-weight: bold;"/>
</form>
<div>
    <h1 class="title">Salary Details</h1>
</div>
<div class="screenlet-body">
    <table cellspacing="0" cellpadding="2" class="basic-table hover-bar">
        <tr class="header-row">
            <td style="width: 10px;">Salary Id</td>
            <td style="width: 10px;">Transaction Date</td>
            <td style="width: 10px;">Status</td>
            <td style="width: 10px;">Action</td>
        </tr>
    <#if salaryList?has_content>
        <#assign alt_row = false>
        <#list salaryList?if_exists as salary>
            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
                <td>${salary.salaryId?if_exists}</td>
                <td>${salary.transactionDate?if_exists?string("dd-MM-yyyy")}</td>
                <td>
                    <#if salary.status?if_exists = "IMPORTED" && salary.isReverted?if_exists != "Y" >
                        Imported
                    <#elseif salary.status?if_exists = "COMMITTED" && salary.isReverted?if_exists != "Y" >
                        Committed
                    <#else >
                        Reverted
                    </#if>
                </td>
                <td>
                    <#if salary.status?if_exists = "IMPORTED">
                        <a href="<@ofbizUrl>viewDovePayrollDetails?salaryId=${salary.salaryId?if_exists}</@ofbizUrl>"  class="btn btn-link">${uiLabelMap.CommonPreview}</a>
                    <#else>
                        <a href="<@ofbizUrl>viewDovePayrollDetails?salaryId=${salary.salaryId}</@ofbizUrl>"  class="btn btn-link">${uiLabelMap.CommonView}</a>
                    </#if>
                    <#if salary.status = "IMPORTED">
                        <a href="<@ofbizUrl>commitDoveTransaction?salaryId=${salary.salaryId?if_exists}</@ofbizUrl>"  class="btn btn-link">${uiLabelMap.Commit}</a>
                        <a href="<@ofbizUrl>deleteDovePayrollDetails?salaryId=${salary.salaryId?if_exists}</@ofbizUrl>" class="btn btn-link">${uiLabelMap.CommonDelete}</a>
                    </#if>
                </td>
            </tr>
            <#assign alt_row = !alt_row>
        </#list>
    </#if>
    </table>
</div>
</div>




