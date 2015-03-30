import org.ofbiz.base.util.UtilValidate;
if(UtilValidate.isEmpty(parameters.facilityId)){
return "selectFacility"
}
return "facilityInfo"
