<#if shipment?exists>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">${uiLabelMap.PageTitleEditShipmentItems}</li>
        </ul>
        <br class="clear"/>
    </div>
    <div class="screenlet-body">
            <table>
                <tr>
                    <td>Product</td>
                    <td><select></select></td>
                </tr>
                <tr>
                    <td>
                        Lot No
                    </td>
                    <td>
                        <select></select>
                    </td>
                </tr>
                <tr>
                    <td></td>
                </tr>
            </table>
    </div>
</div>
<#else>
<div class="screenlet">
    <div class="screenlet-title-bar">
        <ul>
            <li class="h3">${uiLabelMap.ProductShipmentNotFoundId} : [${shipmentId?if_exists}]</li>
        </ul>
        <br class="clear"/>
    </div>
</div>
</#if>