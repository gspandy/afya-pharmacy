function getDependentUoms(productMeterTypeId) {
    var result = false;
    var uomOptions = null;
    var optionList = [];
    $.getJSON('getDependentUoms?'+productMeterTypeId, function(data) {

        $('#uomId').empty();

        $.each(data.meterUom, function(key, val) {
            var parts = val.split(":");
            $('#uomId').append("<option value = " + parts[0] + " > " + parts[1] + " </option>");
        });
        result = true;

    });

    return result;
}

function getDependentFixedAssets(workEffortId) {
    var result = false;
    var fixedAssetOptions = null;
    var optionList = [];
    $.getJSON('getDependentFixedAssets?'+workEffortId, function(data) {

        $('#fixedAssetId').empty();

        var fixedAsset = data.routingTaskFixedAsset;

        $('#fixedAssetId').append("<option value = " + fixedAsset.fixedAssetId + " > " + fixedAsset.fixedAssetName + " [" + fixedAsset.fixedAssetId + "]" + "</option>");
        result = true;

    });

    return result;
}

function assignAssetUsage(element) {
    var productMeterTypeId = $('#'+element.id).val();
    var autoGenId = element.id.slice(18);
    var workEffortId = "#workEffortId"+autoGenId;
    var fixedAssetId = "#fixedAssetId"+autoGenId;
    var productionRunId = "#productionRunId"+autoGenId;

    if(productMeterTypeId=="stoppage") {
        window.location="ProductionRunFixedAssetStoppageReading?workEffortId=" + $(workEffortId).val() + "&fixedAssetId="  + $(fixedAssetId).val() + "&productionRunId="  + $(productionRunId).val(); 
    }
    if(productMeterTypeId=="electric") {
        window.location="ProductionRunFixedAssetElectricMeterReading?workEffortId=" + $(workEffortId).val() + "&fixedAssetId="  + $(fixedAssetId).val() + "&productionRunId="  + $(productionRunId).val(); 
    }
}