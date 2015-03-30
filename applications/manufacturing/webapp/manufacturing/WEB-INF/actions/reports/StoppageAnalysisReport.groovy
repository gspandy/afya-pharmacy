import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.entity.jdbc.SQLProcessor
import org.ofbiz.manufacturing.utility.ManufacturingUtility

import java.sql.ResultSet
import java.sql.Timestamp

fixedAssetId = parameters.fixedAssetId;
fromDate = parameters.fromDate;
thruDate = parameters.thruDate;

String query = "SELECT M.FIXED_ASSET_ID AS FAID,FIXED_ASSET_NAME,READING_DATE,COMMENTS_REASON,W.ACTUAL_COMPLETION_DATE,W.ACTUAL_START_DATE,W.`WORK_EFFORT_ID` AS WEI" +
        " FROM FIXED_ASSET_METER M " +
        " JOIN FIXED_ASSET A ON M.`FIXED_ASSET_ID`=A.`FIXED_ASSET_ID`" +
        " JOIN WORK_EFFORT W ON M.`PRODUCTION_RUN_ID`=W.`WORK_EFFORT_ID` " +
        " WHERE M.`PRODUCT_METER_TYPE_ID`='STOPPAGE'";

if(UtilValidate.isNotEmpty(fixedAssetId)){
    query = query.concat(" AND M.`FIXED_ASSET_ID`='"+fixedAssetId+"'");
}

if(UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate)){
    query = query.concat(" AND M.`READING_DATE`>='"+ UtilDateTime.getDayStart(UtilDateTime.dateStringToTimestampParser(fromDate))+"'")
            .concat(" AND M.`READING_DATE`<='"+UtilDateTime.getDayEnd(UtilDateTime.dateStringToTimestampParser(thruDate))+"'");
}
query = query.concat(" GROUP BY W.`WORK_EFFORT_ID` ");

Map<String,List<Map>> stoppageAnalysisMap = [:];

if(validateDates(fromDate,thruDate)){
    ResultSet resultSet = ManufacturingUtility.execute(query,delegator);

    while(resultSet.next()){
        String fixedAssetName = resultSet.getString("FIXED_ASSET_NAME");
        List row = (ArrayList)createRowEntry(fixedAssetName,stoppageAnalysisMap);
        row.add(createMapForARow(resultSet));
    }
}


context.stoppageAnalysisMap =  stoppageAnalysisMap;

def validateDates(String fromDate,String thruDate){
    if((UtilValidate.isNotEmpty(thruDate) && UtilValidate.isEmpty(fromDate))||(UtilValidate.isEmpty(thruDate) && UtilValidate.isNotEmpty(fromDate))){
        return false;
    }
    if(UtilValidate.isNotEmpty(fromDate) && UtilValidate.isNotEmpty(thruDate) && UtilDateTime.dateStringToTimestampParser(thruDate).before(UtilDateTime.dateStringToTimestampParser(fromDate))){
        return false;
    }
    return true;
}

def createMapForARow(ResultSet resultSet){
    Map rowMap = [:];
    String fixedAssetId = resultSet.getString("FAID");
    Timestamp readingDate =  resultSet.getTimestamp("READING_DATE");
    rowMap.put("completeDate",resultSet.getString("W.ACTUAL_COMPLETION_DATE"));
    rowMap.put("startDate",resultSet.getString("W.ACTUAL_START_DATE"));
    addDownTimeAndRHrsToMap(fixedAssetId,resultSet.getString("WEI"),rowMap);
    addDownTimeAndReasonForDifferentCausesToMap(fixedAssetId,resultSet.getString("WEI"),rowMap);
    rowMap.put("productionTime",ManufacturingUtility.getProductionQty(fixedAssetId,resultSet.getTimestamp("W.ACTUAL_COMPLETION_DATE"),resultSet.getTimestamp("W.ACTUAL_COMPLETION_DATE"),delegator))
    return rowMap;
}

def addDownTimeAndReasonForDifferentCausesToMap(String fixedAssetId,String workEffortId,Map rowMap){
    String[] causes = ["Electrical","Mechanical","Instrumentation","General"];
    Map causeDownTimePair = ManufacturingUtility.getDownTimeForAParticularCauseWithConcatenatedReason(fixedAssetId,workEffortId,delegator);
    for(String cause:causes){
        rowMap.put(cause,causeDownTimePair.get(cause))
    }
    rowMap.put("ZescoPowerCut",causeDownTimePair.get("Zesco Power Cut"));
    rowMap.put("FcbFull",causeDownTimePair.get("FCB FULL"));
    rowMap.put("reason",causeDownTimePair.get("reason"));
}

def addDownTimeAndRHrsToMap(String fixedAssetId,String workEffortId,Map rowMap){
    Map downtimeWithDays = ManufacturingUtility.getTotalDownTime(fixedAssetId,null,null,workEffortId,delegator);
    double downtime = (double)downtimeWithDays.get("hours");
    downtime = (double)Math.round(downtime*100)/100;
    double rHrs = 24.0-downtime;
    rowMap.put("downtime",downtime);
    rowMap.put("rHrs",rHrs);
}


def createRowEntry(String fixedAssetName,Map stoppageAnalysisMap){
    if(UtilValidate.isEmpty(stoppageAnalysisMap.get(fixedAssetName))){
        (List)stoppageAnalysisMap.put(fixedAssetName,new ArrayList());
    }
    return rowEntry = (List)stoppageAnalysisMap.get(fixedAssetName);
}

def execute(String query){
    SQLProcessor sqlproc = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
    sqlproc.prepareStatement(query);
    return sqlproc.executeQuery();
}


