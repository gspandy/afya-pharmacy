package com.hrms.composer;

import static com.ndz.zkoss.HrmsUtil.getDepartmentName;
import static com.ndz.zkoss.HrmsUtil.getEmployeeName;
import static com.ndz.zkoss.HrmsUtil.getFullName;
import static com.ndz.zkoss.HrmsUtil.getPositionTypeDescription;
import static com.ndz.zkoss.HrmsUtil.getRequisitionStatusDescription;
import static com.ndz.zkoss.HrmsUtil.isHod;
import static com.ndz.zkoss.HrmsUtil.isManager;
import static com.ndz.zkoss.HrmsUtil.isRequisitionAdmin;
import static com.ndz.zkoss.HrmsUtil.isSystemAdmin;
import static com.ndz.zkoss.HrmsUtil.isUser;
import static org.ofbiz.base.util.UtilDateTime.formatDate;
import static org.ofbiz.humanresext.util.HumanResUtil.getEmplPositionForParty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;
import org.zkoss.zul.Toolbarbutton;

import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.HrmsConstants;

public class SearchRequisitionComposer extends HrmsComposer {

	private String requisitionId;

	private String operator = "Begins With";

	private GenericValue positionTypeGv;

	private GenericValue userLoginGv;

	private String employeeId;
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
	super.doAfterCompose(comp);
	userLoginGv = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
	employeeId = userLoginGv.getString("partyId");
	binder.loadAttribute(comp, null);
	}

	public List<GenericValue> searchRequisition() throws GenericEntityException {
	List<EntityCondition> conditions = new ArrayList<EntityCondition>();
	EntityCondition userCondition = null;
	EntityCondition operatorCondition = null;
	EntityCondition positionTypeCondition = null;
	boolean isManagerHod=false;
	if(isManager() && isHod())
		isManagerHod=true;
	
	String positionType = positionTypeGv!=null?positionTypeGv.getString("emplPositionTypeId"):null;
		if(isUser())
			userCondition = EntityCondition.makeCondition("statusId", HrmsConstants.REQUISITION_PROCESSING_STATUS);
		if((isHod()||isRequisitionAdmin() || isSystemAdmin())&& !isManager()) 
			userCondition = EntityCondition.makeCondition("statusId", EntityComparisonOperator.NOT_EQUAL,HrmsConstants.REQUISITION_SAVE_STATUS);
		
		if("Begins With".equalsIgnoreCase(operator))
			operatorCondition = EntityCondition.makeCondition("requisitionId", EntityComparisonOperator.LIKE,requisitionId+"%");
		if("Equals".equalsIgnoreCase(operator))
			operatorCondition = EntityCondition.makeCondition("requisitionId",EntityComparisonOperator.EQUALS,requisitionId);
		if("Contains".equalsIgnoreCase(operator))
			operatorCondition = EntityCondition.makeCondition("requisitionId",EntityComparisonOperator.LIKE,"%"+requisitionId+"%");
		if("Not Equal".equalsIgnoreCase(operator))
			operatorCondition = EntityCondition.makeCondition("requisitionId",EntityComparisonOperator.NOT_EQUAL,requisitionId);
		if("is Empty".equalsIgnoreCase(operator))
			operatorCondition = null;
		positionTypeCondition = EntityCondition.makeCondition("positionType",EntityComparisonOperator.EQUALS,positionType);
		if(userCondition!=null)
		conditions.add(userCondition);
		if(UtilValidate.isNotEmpty(requisitionId) && UtilValidate.isEmpty(positionType))
			conditions.add(operatorCondition);
		if(UtilValidate.isEmpty(requisitionId) && UtilValidate.isNotEmpty(positionType))
			conditions.add(positionTypeCondition);
		if(UtilValidate.isNotEmpty(requisitionId) && UtilValidate.isNotEmpty(positionType)){
			conditions.add(positionTypeCondition);
			conditions.add(operatorCondition);
		}
		if(isHod() && !isManager()){
			EntityCondition ec1 = EntityCondition.makeCondition("approverPositionId", getEmplPositionForParty(userLoginGv.getString("partyId"),delegator).getString("emplPositionId"));
			EntityCondition ec2 = EntityCondition.makeCondition("statusId", HrmsConstants.REQUISITION_PROCESSING_STATUS);
			EntityCondition ec3 = EntityCondition.makeCondition(ec1, EntityComparisonOperator.OR, ec2);
			EntityCondition ec =  EntityCondition.makeCondition(ec3, EntityComparisonOperator.AND, EntityCondition.makeCondition("statusId", EntityComparisonOperator.NOT_EQUAL,HrmsConstants.REQUISITION_SAVE_STATUS));
			conditions.add(ec);
		}	
		if(isManager() && !isHod() && !isRequisitionAdmin()){
			EntityCondition ec1 = EntityCondition.makeCondition("partyId", userLoginGv.getString("partyId"));
			EntityCondition ec2 = EntityCondition.makeCondition("statusId", HrmsConstants.REQUISITION_PROCESSING_STATUS);
			EntityCondition ec3 = EntityCondition.makeCondition(ec1, EntityComparisonOperator.OR, ec2);
			EntityCondition ec = EntityCondition.makeCondition(ec3, EntityComparisonOperator.OR, EntityCondition.makeCondition("statusId", EntityComparisonOperator.EQUALS,HrmsConstants.REQUISITION_CLOSE_STATUS));
			conditions.add(ec);
		}	
		if(isManagerHod && !isRequisitionAdmin()){
			EntityCondition ec1 = EntityCondition.makeCondition("approverPositionId", getEmplPositionForParty(userLoginGv.getString("partyId"),delegator).getString("emplPositionId"));
			EntityCondition ec2 = EntityCondition.makeCondition("statusId", HrmsConstants.REQUISITION_PROCESSING_STATUS);
			EntityCondition ec3 = EntityCondition.makeCondition(ec1, EntityComparisonOperator.OR, ec2);
			EntityCondition ec4 = EntityCondition.makeCondition(ec3, EntityComparisonOperator.AND, EntityCondition.makeCondition("statusId", EntityComparisonOperator.NOT_EQUAL,HrmsConstants.REQUISITION_SAVE_STATUS));
			
			
			EntityCondition ec5 = EntityCondition.makeCondition("partyId", userLoginGv.getString("partyId"));
		
			EntityCondition ec6 = EntityCondition.makeCondition("statusId", HrmsConstants.REQUISITION_PROCESSING_STATUS);
			EntityCondition ec7 = EntityCondition.makeCondition(ec5, EntityComparisonOperator.OR, ec6);
			EntityCondition ec8=  EntityCondition.makeCondition(ec7, EntityComparisonOperator.OR, EntityCondition.makeCondition("statusId", EntityComparisonOperator.EQUALS,HrmsConstants.REQUISITION_CLOSE_STATUS));
			EntityCondition ec =  EntityCondition.makeCondition(ec4,EntityComparisonOperator.OR,ec8);
			conditions.add(ec);
		}
		
		
	EntityCondition condition = EntityCondition.makeCondition(conditions);

	return delegator.findList("EmployeeRequisition", condition, null, null, null, false);
	}

	
	public List<GenericValue> getEmployeePositionTypes() throws GenericEntityException{
	return delegator.findList("EmplPositionType", null, null, null, null, false);
	}
	
	
	public RowRenderer getRowRenderer() {
		return new RowRenderer() {
			@Override
			public void render(Row row, Object data) throws Exception {
			GenericValue gv = (GenericValue)data;
			String statusId = gv.getString("statusId");
			final String  requisitionId = gv.getString("requisitionId");
			Toolbarbutton toolbarbutton = new Toolbarbutton(requisitionId);
			/*if(isManager()){
				if(HrmsConstants.REQUISITION_SAVE_STATUS.equalsIgnoreCase(statusId))
					toolbarbutton.setHref("/control/editRequisition?requisitionId="+requisitionId);
				else
					toolbarbutton.setHref("/control/viewRequisition?requisitionId="+requisitionId);
					
			}
			if(isHod()){
				if(HrmsConstants.REQUISITION_SUBMIT_STATUS.equalsIgnoreCase(statusId))
					toolbarbutton.setHref("/control/employeeRequisition/approveRequisition?requisitionId="+requisitionId);
				else
					toolbarbutton.setHref("/control/viewRequisition?requisitionId="+requisitionId);
			}
			
			if(isRequisitionAdmin()){
				if(HrmsConstants.REQUISITION_HOD_APPROVE_STATUS.equalsIgnoreCase(statusId))
					toolbarbutton.setHref("/control/employeeRequisition/approveRequisition?requisitionId="+requisitionId);
				else if(HrmsConstants.REQUISITION_ADMIN_APPROVE_STATUS.equalsIgnoreCase(statusId))
					toolbarbutton.setHref("/control/viewProcessingRequisition?requisitionId="+requisitionId);
				else
					toolbarbutton.setHref("/control/viewRequisition?requisitionId="+requisitionId);
			}
			if(isUser() && HrmsConstants.REQUISITION_PROCESSING_STATUS.equalsIgnoreCase(statusId))
				toolbarbutton.setHref("/control/viewRequisition?requisitionId="+requisitionId);
			if(isSystemAdmin())
				toolbarbutton.setHref("/control/viewRequisition?requisitionId="+requisitionId);
			*/
			
			switch(statusId){
			case HrmsConstants.REQUISITION_SAVE_STATUS:
				if(isManager()) {
					toolbarbutton.setHref("/control/editRequisition?requisitionId="+requisitionId);
					break;
				}else{
					toolbarbutton.setHref("/control/viewRequisition?requisitionId="+requisitionId);
					break;
				}
				
			case HrmsConstants.REQUISITION_SUBMIT_STATUS:
				if(isHod()){
					toolbarbutton.setHref("/control/employeeRequisition/approveRequisition?requisitionId="+requisitionId);
					break;
				}else{
					toolbarbutton.setHref("/control/viewRequisition?requisitionId="+requisitionId);
					break;
				}
			case HrmsConstants.REQUISITION_HOD_APPROVE_STATUS:
				if(isRequisitionAdmin()){
						toolbarbutton.setHref("/control/employeeRequisition/approveRequisition?requisitionId="+requisitionId);
					break;
				}else{
					toolbarbutton.setHref("/control/viewRequisition?requisitionId="+requisitionId);
					break;
				}
				
			case HrmsConstants.REQUISITION_ADMIN_APPROVE_STATUS:
				if(isRequisitionAdmin()){
					toolbarbutton.setHref("/control/viewProcessingRequisition?requisitionId="+requisitionId);
					break;	
				}else{
					toolbarbutton.setHref("/control/viewRequisition?requisitionId="+requisitionId);
					break;
				}
			case HrmsConstants.REQUISITION_PROCESSING_STATUS:
				toolbarbutton.setHref("/control/viewRequisition?requisitionId="+requisitionId);
				break;
			}
				
			toolbarbutton.setParent(row);
			new Label(getEmployeeName(gv.getString("approverPositionId"))).setParent(row);
			new Label(getFullName(delegator, gv.getString("partyId"))).setParent(row);
			new Label(getDepartmentName(gv.getString("reqRaisedByDept"))).setParent(row);
			new Label(HrmsUtil.getLocationName(gv.getString("locationId"))).setParent(row);
			new Label(getRequisitionStatusDescription(statusId)).setParent(row);
			new Label(getPositionTypeDescription(gv.getString("positionType"))).setParent(row);
			if (HrmsConstants.REQUISITION_PROCESSING_STATUS.equals(gv.getString("statusId"))|| HrmsConstants.REQUISITION_CLOSE_STATUS.equals(gv.getString("statusId"))) {
				Long balancePos = gv.getLong("numberOfPosition")- gv.getLong("balancePositionToFulfill");
				String s = balancePos.toString() + "/"+ gv.getString("balancePositionToFulfill") + "/"+ gv.getString("numberOfPosition");
				Label l = new Label(s);
				l.setParent(row);
				l.setTooltiptext("Fulfilled (" + balancePos + ") Open ("+ gv.getString("balancePositionToFulfill")+ ") Total (" + gv.getString("numberOfPosition") + ")");
			} else {
				new Label(" ").setParent(row);
			}
			new Html(gv.getString("jobDescription")).setParent(row);
			new Html(gv.getString("roleDetails")).setParent(row);
			new Html(gv.getString("softSkills")).setParent(row);
			new Html(gv.getString("qualification")).setParent(row);
			new Label(gv.getString("minExprience")).setParent(row);
			new Label(gv.getString("maxExprience")).setParent(row);
			new Html(gv.getString("justificationForHiring")).setParent(row);
			new Label(gv.getString("minimumSalary") + " " +  HrmsUtil.getEnumDescription(gv.getString("enumId")) + " " +HrmsUtil.getUomDescription(gv.getString("uomId"))).setParent(row);
			new Label(gv.getString("maximumSalary")+ " " +  HrmsUtil.getEnumDescription(gv.getString("enumId"))+ " " +HrmsUtil.getUomDescription(gv.getString("uomId"))).setParent(row);
			new Label(formatDate(new Date(gv.getTimestamp("fromDate").getTime()))).setParent(row);


            /*manpower requisition*/
                if(HrmsUtil.isSystemAdmin()){
                    Toolbarbutton requisitionToolbarButton = new Toolbarbutton();
                    requisitionToolbarButton.setLabel("Print Form");
                    requisitionToolbarButton.setParent(row);
                    requisitionToolbarButton.addEventListener("onClick", new EventListener() {
                        public void onEvent(Event event) throws Exception {
                            Executions.getCurrent().sendRedirect("/control/manpowerRequisitionForm?requisitionId="+requisitionId,"_new");
                        }

                    });
                }

			}
		};
	}
	
	public String getRequisitionId() {
	return requisitionId;
	}

	public void setRequisitionId(String requisitionId) {
	this.requisitionId = requisitionId;
	}

	public String getOperator() {
	return operator;
	}

	public void setOperator(String operator) {
	this.operator = operator;
	}

	public GenericValue getPositionTypeGv() {
	return positionTypeGv;
	}

	public void setPositionTypeGv(GenericValue positionTypeGv) {
	this.positionTypeGv = positionTypeGv;
	}

	public GenericValue getUserLoginGv() {
	return userLoginGv;
	}

	public void setUserLoginGv(GenericValue userLoginGv) {
	this.userLoginGv = userLoginGv;

	}

	public String getEmployeeId() {
	return employeeId;
	}

	public void setEmployeeId(String employeeId) {
	this.employeeId = employeeId;
	}

	private static final long serialVersionUID = 1L;

}
