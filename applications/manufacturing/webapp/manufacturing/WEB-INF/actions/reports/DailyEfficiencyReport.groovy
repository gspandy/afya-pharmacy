import org.apache.commons.lang.time.DateUtils
import org.ofbiz.base.util.UtilDateTime
import org.ofbiz.base.util.UtilValidate
import org.ofbiz.manufacturing.utility.ManufacturingUtility

import java.sql.ResultSet
import java.sql.Timestamp

fixedAssetId = parameters.fixedAssetId;
Timestamp today = new Timestamp(new Date().getTime());
String queryForDailyEfficiencyReport = "SELECT A.`FIXED_ASSET_NAME` as FAN,(SUM(COALESCE(M.`METER_VALUE`,0))) AS SUM, M.`FIXED_ASSET_ID` AS FAID, W.`WORK_EFFORT_ID` AS WEI" +
        " FROM fixed_asset_meter M " +
        " JOIN FIXED_ASSET A ON M.`FIXED_ASSET_ID`=A.`FIXED_ASSET_ID` " +
        " JOIN WORK_EFFORT W ON M.`PRODUCTION_RUN_ID`=W.`WORK_EFFORT_ID`"+
        " WHERE M.`PRODUCT_METER_TYPE_ID`='ELECTRIC_METER' ";

String queryForStoppageAndReason = "SELECT *" +
        " FROM fixed_asset_meter M " +
        " JOIN FIXED_ASSET A ON M.`FIXED_ASSET_ID`=A.`FIXED_ASSET_ID` " +
        " WHERE M.`PRODUCT_METER_TYPE_ID`='STOPPAGE' ";
queryForStoppageAndReason = concatHavingClause(queryForStoppageAndReason,fixedAssetId);
Map stoppageAndReason = [:];
ResultSet resultSetStoppageAndReason = ManufacturingUtility.execute(queryForStoppageAndReason,delegator);


while (resultSetStoppageAndReason.next()){
    List listOfRowsForAsset = getTheRowForTheAsset(resultSetStoppageAndReason.getString("FIXED_ASSET_NAME"),stoppageAndReason);
    listOfRowsForAsset.add(prepareColumnValuesForStoppageAndReason(resultSetStoppageAndReason));
}

def getTheRowForTheAsset(String asset,Map stoppageAndReason){
    if(!stoppageAndReason.containsKey(asset)){
        stoppageAndReason.put(asset,[]);
    }
    return stoppageAndReason.get(asset)
}

def prepareColumnValuesForStoppageAndReason(ResultSet resultSet){
    Map columnMap = [:];
    Timestamp readingDate = resultSet.getTimestamp("READING_DATE");
    Double meterValue =  resultSet.getDouble("METER_VALUE");
    double meterValueInHours = meterValue;
    String metric =  resultSet.getString("UOM_ID");
    Boolean isHour = true;
    if(metric.equals("SHT_DWN_min")){
        meterValueInHours = meterValueInHours/60;
        meterValueInHours = roundDouble(meterValueInHours);
        isHour= false;
    }
    Date stopTime = addMeterValue(readingDate,meterValue,isHour);

    columnMap.put("start",getTimeString(readingDate.toString()));
    columnMap.put("meterValue",meterValueInHours);
    columnMap.put("stop",getTimeString(stopTime.toString()));
    columnMap.put("reason",resultSet.getString("COMMENTS_REASON"));
    columnMap.put("cause",resultSet.getString("DEPARTMENT_CAUSE"));
    return columnMap;
}

def getTimeString(String timestamp){
    return timestamp.substring(11,16);
}

def addMeterValue(Timestamp timestamp,Double meterValue,Boolean isHour){
    Date date = new Date(timestamp.getTime());
    if(isHour){
        int hours = meterValue.intValue();
        int min = (meterValue - hours)*100;
        date = DateUtils.addHours(date,hours);
        date = DateUtils.addMinutes(date,min);
    }else{
        int min =meterValue.intValue();
        int sec = (meterValue - min)*100;
        date = DateUtils.addMinutes(date,min);
        date = DateUtils.addSeconds(date,sec);
    }
    return date;
}

context.stoppageAndReason = stoppageAndReason;




Map<String,Map<String,Map>> dailyEfficiencyReport =  [:];

ResultSet resultSetOnDate = getOnDateReport(today,queryForDailyEfficiencyReport,fixedAssetId);
ResultSet resultSetMonthToDate = getMonthToDateReport(today,queryForDailyEfficiencyReport,fixedAssetId);
ResultSet resultSetYearToDate = getYearToDateReport(today,queryForDailyEfficiencyReport,fixedAssetId);


while (resultSetOnDate.next()){
    prepareColumns("onDate",today,today,resultSetOnDate,dailyEfficiencyReport);
}

while (resultSetMonthToDate.next()){
    Timestamp monthToDate = UtilDateTime.getMonthStart(today);
    prepareColumns("monthToDate",monthToDate,today,resultSetMonthToDate,dailyEfficiencyReport);
}

while (resultSetYearToDate.next()){
    Timestamp yearToDate = UtilDateTime.getYearStart(today);
    prepareColumns("yearToDate",yearToDate,today,resultSetYearToDate,dailyEfficiencyReport);
}

context.dailyEfficiencyReport = dailyEfficiencyReport

def prepareColumns(String keyToCol,Timestamp fromDate,Timestamp toDate,ResultSet resultSet,Map dailyEfficiencyReport){
    String fixedAssetIdFromResultSet = resultSet.getString("FAID");
    String workEffortId =  resultSet.getString("WEI");
    double prod = ManufacturingUtility.getProductionQty(fixedAssetIdFromResultSet,fromDate,toDate,delegator);
    double unit = resultSet.getDouble("SUM");
    double runsPerHour = getRunsPerHour(fixedAssetIdFromResultSet,fromDate,toDate);
    double tph =0.0
    if(prod!=0 && runsPerHour!=0)
        tph = prod/runsPerHour;
    double kwhTOfMat =0.0;
    if(prod!=0 && unit!=0)
        kwhTOfMat = unit/prod;
    prod = roundDouble(prod);
    unit = roundDouble(unit);
    runsPerHour = roundDouble(runsPerHour);
    tph = roundDouble(tph);
    kwhTOfMat = roundDouble(kwhTOfMat);
    Map mapOfKeyColumns =[:]
    Map map= ["unit":unit,
                      "runsPerHour":runsPerHour,
                      "prod":prod,
                      "tph":tph,
                      "kwhTOfMat":kwhTOfMat
    ];
    if(dailyEfficiencyReport.containsKey(resultSet.getString("FAN"))){
        mapOfKeyColumns = dailyEfficiencyReport.get(resultSet.getString("FAN"));
        mapOfKeyColumns.put(keyToCol,map)
    }else{
        mapOfKeyColumns.put(keyToCol,map)
    }
    dailyEfficiencyReport.put(resultSet.getString("FAN"),mapOfKeyColumns);
}

def getRunsPerHour(String fixedAssetId,Timestamp fromDate,Timestamp toDate){
    Map downTimeWithDays =  ManufacturingUtility.getTotalDownTime(fixedAssetId,fromDate,toDate,null,delegator);
    double downTime = downTimeWithDays.get("hours");
    int numberOfDays =  ManufacturingUtility.getTotalNumberOfDaysOfProductionRun(fixedAssetId,fromDate,toDate,delegator);
    if(numberOfDays!=0){
        return (24.0*numberOfDays - downTime);
    }
    return 24;
}

def getOnDateReport(Timestamp today,String query,String fixedAssetId){
    String onDateQuery = concatDateFilter(query,today,today);
    onDateQuery = concatGroupByClause(onDateQuery);
    onDateQuery = concatHavingClause(onDateQuery,fixedAssetId);
    return ManufacturingUtility.execute(onDateQuery,delegator);
}

def getMonthToDateReport(Timestamp today,String query,String fixedAssetId){
    Timestamp monthStart =  UtilDateTime.getMonthStart(today);
    String monthToDateQuery =  concatDateFilter(query,monthStart,today);
    monthToDateQuery = concatGroupByClause(monthToDateQuery);
    monthToDateQuery = concatHavingClause(monthToDateQuery,fixedAssetId);
    return ManufacturingUtility.execute(monthToDateQuery,delegator);
}

def getYearToDateReport(Timestamp today,String query,String fixedAssetId){
    Timestamp yearStart =  UtilDateTime.getYearStart(today);
    String yearToDateQuery =  concatDateFilter(query,yearStart,today);
    yearToDateQuery = concatGroupByClause(yearToDateQuery);
    yearToDateQuery = concatHavingClause(yearToDateQuery,fixedAssetId);
    return ManufacturingUtility.execute(yearToDateQuery,delegator);
}

def concatDateFilter(String query,Timestamp from,Timestamp to){
   return  query.concat(" AND W.`ACTUAL_COMPLETION_DATE`>='"+ UtilDateTime.getDayStart(from)+"'")
           .concat(" AND W.`ACTUAL_COMPLETION_DATE`<='"+UtilDateTime.getDayEnd(to)+"'");
}

def concatGroupByClause(String query){
    return query.concat(" GROUP BY M.`FIXED_ASSET_ID`");
}

def concatHavingClause(String query,String fixedAssetId){
    if(UtilValidate.isNotEmpty(fixedAssetId)){
        query = query.concat(" HAVING M.`FIXED_ASSET_ID`='"+fixedAssetId+"'");
    }
    return query;
}

def roundDouble(double num){
   return  (double)Math.round(num*100)/100;
}



