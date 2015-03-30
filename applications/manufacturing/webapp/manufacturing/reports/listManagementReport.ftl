<#if fromdate?has_content>
<form style="display: none" id="managementReportForm" method="post" action="managementReportPdf" target="_blank">
</form>
<table id="managementReportId" style="margin: 15px" cellpadding="0" cellspacing="0" width="100%" border="0">
    <tr>
        <td style="font-weight: bold">
            <p style="display: inline;text-decoration: underline">DAILY PRODUCTION AND SALES REPORT - </p>
            <p id="fromDate" style="display: inline;text-decoration: underline">${fromdate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}</p>
        </td>
    </tr>
    <tr>
        <td style="font-weight: bold">
            <p style="display: inline;text-decoration: underline">TODAY&#39;S PLANT OPERATION DATED - </p>
            <p style="display: inline;text-decoration: underline">${fromdate?date("yyyy-MM-dd")?string("dd/MM/yyyy")}</p>
        </td>
    </tr>
    <tr>
        <td>
            <textarea id="tpo" rows="3" cols="100" ></textarea>
        </td>
    </tr>
    <tr>
        <td style="font-weight: bold">
            <p style="display: inline;text-decoration: underline">YESTERDAY&#39;S PRODUCTION & SALES DATED - </p>
            <p id="previousDate" style="display: inline;text-decoration: underline">${previousDate?date?string("dd/MM/yyyy")}</p>
        </td>
    </tr>
    <tr>
        <td style="padding-left: 25px">
            <p id="cep">Cement Production - ${cementProduction?if_exists}</p>
            <p id="ces">Cement stock in Silo is - ${cementInSilo?if_exists}</p>
            <p id="clp">Clinker Production - ${clinkerProduction?if_exists}</p>
            <p id="cls">Clinker stock in shed is - ${clinkerInShed?if_exists}</p>
        </td>
    </tr>
    <tr>
        <td style="font-weight: bold">
            <p style="display: inline;text-decoration: underline">YESTERDAY&#39;S CEMENT PACKING AND SALES DATED - </p>
            <p style="display: inline;text-decoration: underline">${previousDate?date?string("dd/MM/yyyy")}</p>
        </td>
    </tr>
    <tr>
        <td style="padding-left: 25px">
            <p id="pl">Cement packing & Loading &#45; ${packingAndLoadingQuantity?if_exists}, ${packingAndLoadingTrucks?if_exists} Trucks.</p>
            <p id="wb">Cement Sales (Weigh Bridge) &#45; ${weighBridgeQuantity?if_exists}, ${weighBridgeTrucks?if_exists} Trucks.</p>
            <p id="ut">Unloaded Trucks &#45; ${unloadedTrucks?if_exists}.</p>
        </td>
    </tr>
    <tr>
        <td>
            <textarea id="data" rows="3" cols="100" ></textarea>
        </td>
    </tr>
    <tr>
        <td  style="text-align: center;padding-top: 25px">
            <button class="btn btn-success" onclick="fun()">Export</button>
        </td>
    </tr>
</table>


<script>

    function fun(){
        removeAllNodes();
        jQuery('#managementReportForm').append(jQuery('<textarea name="tpo" id="formTpo">'));
        jQuery('#formTpo').val(jQuery('#tpo').val());
        jQuery('#managementReportForm').append(jQuery('<textarea name="data" id="formData">'));
        jQuery('#formData').val(jQuery('#data').val());

        jQuery.each(['fromDate','previousDate','pl','wb','ut','cep','clp','ces','cls'],function(index,value){
            var text = jQuery('#'+value).text();
            var input = jQuery("<input type='text' value='"+text+"' name='"+value+"'>");
            jQuery('#managementReportForm').append(input);
        });

        jQuery('#managementReportForm').submit();

    }

    function removeAllNodes(){
        jQuery('#managementReportForm *').filter(':input').each(function(k,v){
            jQuery(v).remove();
        });
    }



</script>
</#if>