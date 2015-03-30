package org.ofbiz.manufacturing.utility;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.jdbc.SQLProcessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by ASUS on 18-Nov-14.
 */
public class ManufacturingUtility {

    private static Map getTotalWithDaysByExecutingQuery(String query, Delegator delegator) {
        double hours =0.0;
        Set<String> days = new HashSet<String>();
        ResultSet resultSet = null;
        try {
            resultSet = execute(query,delegator);
            while (resultSet.next()){
                String readingDate = resultSet.getString("M.PRODUCTION_RUN_ID");
                days.add(readingDate);
                if(resultSet.getString("UOM_ID").equals("SHT_DWN_min")){
                    hours =  hours+(resultSet.getDouble("METER_VALUE")/60.0);
                }else{
                    hours =  hours+resultSet.getDouble("METER_VALUE");
                }
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return UtilMisc.toMap("hours",hours,"days",days.size());
    }


    public static Map getDownTimeForAParticularCauseWithConcatenatedReason(String fixedAssetId,String workEffortId,Delegator delegator) throws GenericEntityException, SQLException {
        String reasons = "";
        Map<String,Object> causeDownTimePair = getZeroInitializedMapForDifferentCauses();
        String query = "SELECT * FROM FIXED_ASSET_METER M " +
                " JOIN FIXED_ASSET A ON M.`FIXED_ASSET_ID`=A.`FIXED_ASSET_ID`" +
                " JOIN WORK_EFFORT W ON M.`PRODUCTION_RUN_ID`=W.`WORK_EFFORT_ID` " +
                " WHERE M.`PRODUCT_METER_TYPE_ID`='STOPPAGE'" +
                " AND W.`WORK_EFFORT_ID`='"+workEffortId+"'"+
                " AND M.`DEPARTMENT_CAUSE` IN ('Zesco Power Cut','Electrical','Mechanical','Instrumentation','General','FCB FULL')";
        if(UtilValidate.isNotEmpty(fixedAssetId)){
            query = query.concat(" AND M.`FIXED_ASSET_ID`='"+fixedAssetId+"'");
        }
        ResultSet resultSet = execute(query,delegator);
        while (resultSet.next()){
            if(UtilValidate.isNotEmpty(resultSet.getString("COMMENTS_REASON"))){
                reasons = reasons.concat(" , ").concat(resultSet.getString("COMMENTS_REASON"));
            }
            String cause = resultSet.getString("DEPARTMENT_CAUSE");
            double hours = (Double)causeDownTimePair.get(cause);
            if(resultSet.getString("UOM_ID").equals("SHT_DWN_min")){
                hours =  hours+(resultSet.getDouble("METER_VALUE")/60.0);
            }else{
                hours =  hours+resultSet.getDouble("METER_VALUE");
            }
            hours = (double)Math.round(hours*100)/100;
            causeDownTimePair.put(cause,hours);
        }
        reasons = reasons.replaceFirst(" , ","");
        causeDownTimePair.put("reason",reasons);
        return causeDownTimePair;
    }


    public static Map getTotalDownTime(String fixedAssetId,Timestamp fromDate,Timestamp toDate,String workEffortId,Delegator delegator) throws SQLException, GenericEntityException {
        String query = "SELECT * FROM FIXED_ASSET_METER M " +
                " JOIN FIXED_ASSET A ON M.`FIXED_ASSET_ID`=A.`FIXED_ASSET_ID`" +
                " JOIN WORK_EFFORT W ON M.`PRODUCTION_RUN_ID`=W.`WORK_EFFORT_ID` " +
                " WHERE M.`PRODUCT_METER_TYPE_ID`='STOPPAGE'" ;
        if(UtilValidate.isNotEmpty(fromDate)&&UtilValidate.isNotEmpty(toDate)){
            query = query.concat(" AND W.`ACTUAL_COMPLETION_DATE`>='"+ UtilDateTime.getDayStart(fromDate)+"'")
            .concat(" AND W.`ACTUAL_COMPLETION_DATE`<='"+UtilDateTime.getDayEnd(toDate)+"'");
        }
        if(UtilValidate.isNotEmpty(workEffortId)){
            query = query.concat(" AND W.`WORK_EFFORT_ID`='"+workEffortId+"'");
        }
        if(UtilValidate.isNotEmpty(fixedAssetId)){
            query = query.concat(" AND M.`FIXED_ASSET_ID`='"+fixedAssetId+"'");
        }
        return getTotalWithDaysByExecutingQuery(query, delegator);
    }

    public static int getTotalNumberOfDaysOfProductionRun(String fixedAssetId,Timestamp fromDate,Timestamp toDate,Delegator delegator){
        int days =0;
        String query = "SELECT COUNT(DISTINCT M.`PRODUCTION_RUN_ID`) AS DAYS FROM FIXED_ASSET_METER M" +
                " JOIN FIXED_ASSET A ON M.`FIXED_ASSET_ID`=A.`FIXED_ASSET_ID`" +
                " JOIN WORK_EFFORT W ON M.`PRODUCTION_RUN_ID`=W.`WORK_EFFORT_ID`" +
                " WHERE W.`ACTUAL_COMPLETION_DATE`>='"+UtilDateTime.getDayStart(fromDate)+"'" +
                " AND W.`ACTUAL_COMPLETION_DATE`<='"+UtilDateTime.getDayEnd(toDate)+"'" +
                " AND W.`CURRENT_STATUS_ID`='PRUN_CLOSED'"+
                " AND M.`FIXED_ASSET_ID`='"+fixedAssetId+"'";
        try {
            ResultSet resultSet = execute(query,delegator);
            while (resultSet.next()){
                days = resultSet.getInt("DAYS");
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return days;
    }

    private static Map getZeroInitializedMapForDifferentCauses(){
        return new HashMap<String,Double>(){{
            put("Zesco Power Cut",0.0);
            put("Electrical",0.0);
            put("Mechanical",0.0);
            put("Instrumentation",0.0);
            put("General",0.0);
            put("FCB FULL",0.0);
        }};
    }

    public static ResultSet execute(String query,Delegator delegator) throws GenericEntityException {
        SQLProcessor sqlProc = new SQLProcessor(delegator.getGroupHelperInfo("org.ofbiz"));
        sqlProc.prepareStatement(query);
        return sqlProc.executeQuery();
    }

    public static double getProductionQty(String fixedAssetId,Timestamp fromDate,Timestamp toDate,Delegator delegator) throws GenericEntityException, SQLException {
        double productionQty =0.0;
        String query = "SELECT C.quantity_produced AS QTY FROM work_effort A" +
                " JOIN work_effort_assoc B ON A.work_effort_id = B.work_effort_id_to" +
                " JOIN work_effort C ON A.work_effort_parent_id = C.work_effort_id" +
                " WHERE A.fixed_asset_id = '"+fixedAssetId+"'"+
                " AND A.`ACTUAL_COMPLETION_DATE`>='" + UtilDateTime.getDayStart(fromDate) + "'" +
                " AND A.`ACTUAL_COMPLETION_DATE`<='" + UtilDateTime.getDayEnd(toDate) + "'";
        ResultSet resultSet = ManufacturingUtility.execute(query, delegator);
        while (resultSet.next()){
            productionQty = productionQty+resultSet.getDouble("QTY");
        }
        return productionQty;
    }


}
