package com.hrms.composer;

import static com.ndz.zkoss.HrmsUtil.isHod;
import static com.ndz.zkoss.HrmsUtil.isManager;
import static com.ndz.zkoss.HrmsUtil.isRequisitionAdmin;
import static com.ndz.zkoss.HrmsUtil.isSystemAdmin;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceValidationException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zul.Messagebox;

import com.ndz.vo.CreatePositionVo;
import com.ndz.vo.RequisitionVo;
import com.ndz.zkoss.HrmsUtil;

public class RecruitmentComposer extends HrmsComposer {

	private GenericValue requisitionGv;

	private RequisitionVo requisitionVo;

	private String requisitionId;
	
	private String departmentId;
	
	private GenericValue positionTypeGv;
	
	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {
	delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get("delegator");
	requisitionId = Executions.getCurrent().getParameter("requisitionId");
	requisitionGv = getRecuisitionGvFor(requisitionId);
	requisitionVo = new RequisitionVo();
	if (requisitionGv != null){ 
		requisitionVo.setRequisitionGv(requisitionGv);
	}	
	return super.doBeforeCompose(page, parent, compInfo);
	}
	
	
	@Override
	public void doAfterCompose(Component component) throws Exception {
	super.doAfterCompose(component);
	GenericValue userLogin = (GenericValue) Executions.getCurrent().getSession().getAttribute("userLogin");
	GenericValue emplPosGV = HumanResUtil.getEmplPositionForParty(userLogin.getString("partyId"), delegator);
	departmentId = emplPosGV!=null? emplPosGV.getString("partyId"):null;
	requisitionVo.setApproverPositionId(HumanResUtil.getHODPositionId(departmentId, delegator));
	if(requisitionVo.getLocationGv() == null){
	requisitionVo.setRequisitionRaisedBy(userLogin.getString("partyId"));
	requisitionVo.setLocationGv(HrmsUtil.getPersonLocation(userLogin));
	}
	}

	public void saveRequisition() throws GenericEntityException, InterruptedException {
	
	if(! hasAuthorizationToProcessRequistion()){
		Messagebox.show("You are not authorized to process the Requistion", "Error", 1, null);
		return;
	}
	Map<Object, Object> dataMap = new HashMap<Object, Object>();
	if(UtilValidate.isEmpty(requisitionId))
		requisitionId = delegator.getNextSeqId("EmployeeRequisition");
	
	dataMap.put("requisitionId", requisitionId);
	dataMap.put("positionType", UtilValidate.isEmpty(requisitionVo.getReplacementPositionId())?
			requisitionVo.getPositionTypeGv().getString("emplPositionTypeId"):HrmsUtil.getEmployeePositionGv(requisitionVo.getReplacementPositionId()).getString("emplPositionTypeId"));
	dataMap.put("numberOfPosition",  UtilValidate.isEmpty(requisitionVo.getReplacementPositionId())? 
			new Long(requisitionVo.getNoOfPosition()):new Long(1));
	dataMap.put("approverPositionId", requisitionVo.getApproverPositionId());
	dataMap.put("qualification",  requisitionVo.getQualifications());
	dataMap.put("minExprience", new Long(requisitionVo.getMinimumExprience()));
	dataMap.put("maxExprience", new Long(requisitionVo.getMaximumExprience()));
	dataMap.put("justificationForHiring", requisitionVo.getJustificationForHiring());
	dataMap.put("jobTitle", requisitionVo.getJobTitle());
	dataMap.put("jobDescription", requisitionVo.getJobDescription());
	dataMap.put("roleDetails", requisitionVo.getRoleDetails());
	dataMap.put("softSkills", requisitionVo.getSoftSkills());
	dataMap.put("minimumSalary", new BigDecimal(requisitionVo.getMinimumSalary()));
	dataMap.put("maximumSalary", new BigDecimal(requisitionVo.getMaximumSalary()));
	dataMap.put("fromDate", new  Timestamp(requisitionVo.getStartDate().getTime()));
	dataMap.put("reqRaisedByDept", requisitionVo.getRequisitionRaisedBy());
	dataMap.put("requisitionType", requisitionVo.getRequisitionType());
	dataMap.put("replacementpositionId", requisitionVo.getReplacementPositionId());
	dataMap.put("uomId", requisitionVo.getCurrencyTypeGv().getString("uomId"));
	dataMap.put("enumId", requisitionVo.getCurrencyBaseLineGv().getString("enumId"));
	dataMap.put("requirementType", requisitionVo.getRequirementType());
	dataMap.put("thruDate", new Timestamp(requisitionVo.getEndDate().getTime()));
	dataMap.put("balancePositionToFulfill",UtilValidate.isEmpty(requisitionVo.getReplacementPositionId())? new Long(requisitionVo.getNoOfPosition()):new Long(1));
	dataMap.put("statusId", requisitionVo.getStatusId());
	dataMap.put("budgetId", requisitionVo.getBudgetId());
	dataMap.put("budgetItemSeqId", requisitionVo.getBudgetItemSequenceId());
	dataMap.put("approverPositionId", requisitionVo.getApproverPositionId());
	GenericValue emplPosGV = HumanResUtil.getEmplPositionForParty(requisitionVo.getRequisitionRaisedBy(), delegator);
	String initiaterDepartmentId = emplPosGV!=null? emplPosGV.getString("partyId"):null;
	dataMap.put("reqRaisedByDept", initiaterDepartmentId);
	dataMap.put("partyId", requisitionVo.getRequisitionRaisedBy());
	dataMap.put("hodComment", requisitionVo.getHodComment());
	dataMap.put("adminComment", requisitionVo.getAdminComment());
	dataMap.put("managerId", requisitionVo.getManagerId());
	dataMap.put("grade",requisitionVo.getGrade());
	dataMap.put("positionCategory",requisitionVo.getPositionCategory());
	dataMap.put("employeeType",requisitionVo.getEmployeeType());
	if(UtilValidate.isNotEmpty(requisitionVo.getLocationGv()))
		dataMap.put("locationId", requisitionVo.getLocationGv().getString("locationId"));
	requisitionGv = delegator.makeValue("EmployeeRequisition", dataMap);
	delegator.createOrStore(requisitionGv);
	
	}

	public List<GenericValue> getCurrencyTypes() throws GenericEntityException{
	return delegator.findByAnd("Uom", UtilMisc.toMap("uomTypeId","CURRENCY_MEASURE"));
	}
	
	public List<GenericValue> getCurrencyBaseLineTypes() throws GenericEntityException{
	return delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "BASELINE_TYPE"));
	}
	
	public List<GenericValue> getPositionTypes() throws GenericEntityException{
	return delegator.findList("EmplPositionType", null, null, null, null, false);
	}
	
	public List<GenericValue> getDepartmentLocations(){
	return HrmsUtil.getLocationsOfDepartment(departmentId);
	}
	
	public List<GenericValue> getEmployeeRequisitionFor(String replacementpositionId){
	List<GenericValue> employeeRequisitionGv = null;
	try {
		employeeRequisitionGv = delegator.findByAnd("EmployeeRequisition", UtilMisc.toMap("replacementpositionId", replacementpositionId));
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return employeeRequisitionGv;
	}
	
	public List<GenericValue> getBudgetSequenceItems(){
	GenericDelegator delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get("delegator");
	List<GenericValue> budgetItemGvs = null;
	try {
		budgetItemGvs = delegator.findList("BudgetItem", null,null, null, null, false);
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return budgetItemGvs;
	}
	
	public List<GenericValue> getBudgetItems(){
	GenericDelegator delegator = (GenericDelegator) Executions.getCurrent().getAttributes().get("delegator");
	List<GenericValue> budgetGvs = null;
	try {
		budgetGvs = delegator.findList("Budget", null, null, null, null, false);
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return budgetGvs;
	}
	public boolean hasAuthorizationToProcessRequistion() throws GenericEntityException{
	if(isHod()){
		GenericValue emplPosGv = HumanResUtil.getEmplPositionForParty(userLogin.getString("partyId"), delegator);
		if(requisitionVo.getApproverPositionId().equalsIgnoreCase(emplPosGv.getString("emplPositionId")));
			return true;
	}
	if(isRequisitionAdmin())
		return true;
	if(isSystemAdmin())
		return true;
	if(isManager() && requisitionVo.getRequisitionRaisedBy().equalsIgnoreCase(userLogin.getString("partyId")))
		return true;
	return false;
	}
	
	
	public List<GenericValue> getEmplPositionsFor(String requisitionId){
	List<GenericValue> positionGvs = null;
	try {
		positionGvs = delegator.findByAnd("EmplPosition", UtilMisc.toMap("requisitionId", requisitionId));
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	return positionGvs;
	}
	
	public Set<GenericValue> searchEmplPositions() throws GenericEntityException{
	List<EntityCondition> conditionList = new ArrayList<EntityCondition>();
	List<GenericValue> terminatedGvs = delegator.findList("MaxTerminationDetail", 
			EntityCondition.makeCondition("statusId", EntityComparisonOperator.EQUALS,"ET_ADM_APPROVED"), null, null, null,false);
	Set<String> employeeIds = new HashSet<String>();
	if(UtilValidate.isNotEmpty(terminatedGvs)){
		for(GenericValue gv : terminatedGvs)
			employeeIds.add(gv.getString("partyId"));
	}
	
	List<EntityCondition> empPosCondList  = new ArrayList<EntityCondition>();
	EntityCondition statuscond = EntityCondition.makeCondition("statusId", "EMPL_POS_INACTIVE");
	EntityCondition deptCond = EntityCondition.makeCondition("partyId",departmentId);
	empPosCondList.add(statuscond);
	empPosCondList.add(deptCond);
	
	List<GenericValue> posFullFillmentGvs = delegator.findList("EmplPositionFulfillment", EntityCondition.makeCondition("partyId",EntityComparisonOperator.IN,employeeIds), null, null, null, false);
	List<String> emplPositionIds = new ArrayList<String>(); 
	for(GenericValue gv : posFullFillmentGvs)
		emplPositionIds.add(gv.getString("emplPositionId"));
	
	EntityCondition ec1 = EntityCondition.makeCondition("emplPositionId",EntityComparisonOperator.IN,emplPositionIds);
	EntityCondition ec2 = EntityCondition.makeCondition("partyId",departmentId);
	if(positionTypeGv!=null){
		EntityCondition ec = EntityCondition.makeCondition("emplPositionTypeId",positionTypeGv.getString("emplPositionTypeId"));
		conditionList.add(ec);
		empPosCondList.add(EntityCondition.makeCondition("emplPositionTypeId",positionTypeGv.getString("emplPositionTypeId")));
	}	
	conditionList.add(ec1);
	conditionList.add(ec2);
	EntityCondition condition = EntityCondition.makeCondition(conditionList);
	Set<GenericValue> searchResult = new HashSet<GenericValue>();
	List<GenericValue> emplPosGvs = delegator.findList("EmplPosition", EntityCondition.makeCondition(empPosCondList), null, null, null, false);
	searchResult.addAll(emplPosGvs);
	searchResult.addAll(delegator.findList("EmplPosition", condition, null, null, null,false));
	return searchResult;
	}
	
	public void createPositionRequested(CreatePositionVo createPositionVo) throws GenericEntityException, ServiceValidationException, GenericServiceException{
	GenericValue emplPositionGv = HumanResUtil.getEmplPositionForParty(createPositionVo.getManagerId(), delegator);
	String managerPositionId = emplPositionGv.getString("emplPositionId");
	GenericDispatcher dispatcher = (GenericDispatcher) GenericDispatcher.getLocalDispatcher("default", delegator);
	Map<String,Object> createPositionMap = UtilMisc.toMap("userLogin", userLogin, "emplPositionTypeId", createPositionVo.getEmpPositionTypeId(), "partyId", createPositionVo.getDepartmenId(), "salaryFlag",
			createPositionVo.getSalaryFlag(), "statusId", "EMPL_POS_ACTIVE", "requisitionId", requisitionId, "estimatedFromDate", new Timestamp(createPositionVo.getEstimatedFromDate().getTime()), "estimatedThruDate",
			createPositionVo.getEstimatedThruDate()!=null?new Timestamp(createPositionVo.getEstimatedThruDate().getTime()):null, "exemptFlag", createPositionVo.getExemptFlag(), "temporaryFlag", createPositionVo.getTemporaryFlag(), "fulltimeFlag", createPositionVo.getFullTimeFlag(), "budgetId", createPositionVo.getBudgetId(),
			"budgetItemSeqId", createPositionVo.getBudgetItemSequenceId());
	for(int i= 0; i<createPositionVo.getNoOfPosition();i++){
		Map<String,Object> result = dispatcher.runSync("createEmplPosition", createPositionMap);
		String employeePositionId = (String)result.get("emplPositionId");
		dispatcher.runSync("createEmplPositionReportingStruct", UtilMisc.toMap("userLogin", userLogin,
				"fromDate",new Timestamp(new Date().getTime()),"emplPositionIdReportingTo",managerPositionId,"emplPositionIdManagedBy",employeePositionId,"primaryFlag","Y"));
	}	
	}
	

	public GenericValue getRequisitionGv() {
	return requisitionGv;
	}

	public void setRequisitionGv(GenericValue requisitionGv) {
	this.requisitionGv = requisitionGv;
	}

	private GenericValue getRecuisitionGvFor(String requisitionId) {
	List<GenericValue> gvs = null;
	if(UtilValidate.isEmpty(requisitionId))
		return null;
	try {
		gvs = delegator.findByAnd("EmployeeRequisition", UtilMisc.toMap("requisitionId", requisitionId));
	} catch (GenericEntityException e) {
		e.printStackTrace();
	}
	if (UtilValidate.isNotEmpty(gvs)) return gvs.get(0);
	return null;
	}

	public RequisitionVo getRequisitionVo() {
	return requisitionVo;
	}

	public void setRequisitionVo(RequisitionVo requisitionVo) {
	this.requisitionVo = requisitionVo;
	}

	public String getRequisitionId() {
	return requisitionId;
	}

	public void setRequisitionId(String requisitionId) {
	this.requisitionId = requisitionId;
	}

	public String getDepartmentId() {
	return departmentId;
	}

	public void setDepartmentId(String departmentId) {
	this.departmentId = departmentId;
	}
	
	public GenericValue getPositionTypeGv() {
	return positionTypeGv;
	}

	public void setPositionTypeGv(GenericValue positionTypeGv) {
	this.positionTypeGv = positionTypeGv;
	}
	
	private static final long serialVersionUID = 1L;
}
