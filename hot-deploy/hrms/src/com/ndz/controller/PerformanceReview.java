package com.ndz.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javolution.util.FastMap;

import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.PerfReview;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

import com.ndz.component.HrmsMessageBox;
import com.ndz.zkoss.DropDownGenericValueAdapter;
import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class PerformanceReview extends GenericForwardComposer {
    private static final long serialVersionUID = 1L;
    public static final String module = PerfReview.class.getName();

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if ("PerformancePlan".equals(comp.getId())) {
            listboxEmployeePositionType(comp);
            listboxEmployeeSectionName(comp);

        }
        if ("enableAppraisal".equals(comp.getId())) {
            selectPerformancePlans(comp);
        }
        if ("appraisalView".equals(comp.getId()) || "mainTemplatePerformanceReviews".equals(comp.getId())) {
            populateGrid(comp);
            if ("appraisalView".equals(comp.getId())) {
                populateGrid2(comp);
                populateAppraisal(comp);
                populateDataGridEmployeePerformanceForClosure(comp);
            }
        }
        if ("employeeAppraisalView".equals(comp.getId())) {
            employeeAppraisalView(comp);
        }
        if ("employeePerformanceForClosure".equals(comp.getId())) {
            employeeAppraisalViewForClosure(comp);
        }
        if ("employeeAppraisalViewForApproval".equals(comp.getId())) {
            employeeAppraisalViewForApproval(comp);
        }
        if ("perfReviewAgreeDisagree".equals(comp.getId())) {
            perfReviewAgreeDisagree(comp);
        }
    }

    @SuppressWarnings("deprecation")
    private void perfReviewAgreeDisagree(Component root) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        String emplPerfReviewId = (String) Executions.getCurrent().getParameter("reviewId");
        String managerId = (String) ((Label) root.getFellow("hiddenemplmanagerId")).getValue();
        EntityCondition emplPerfReviewAttribute_condition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        List<GenericValue> emplPerfReviewAttribute_list = delegator.findList("EmplPerfReviewAttribute", emplPerfReviewAttribute_condition, null, null, null,
                false);
        List<GenericValue> emplPerfReview_List = delegator.findList("EmplPerfReview", emplPerfReviewAttribute_condition, null, null, null, false);
        String statusType = "PERF_REVIEW_PENDING";
        Label status = (Label) root.getFellow("status");
        EntityCondition mainCondition1 = EntityCondition.makeCondition("reviewerId", EntityOperator.EQUALS, managerId);
        EntityCondition mainCondition2 = EntityCondition.makeCondition(emplPerfReviewAttribute_condition, EntityOperator.AND, mainCondition1);
        List<GenericValue> emplPerfReviewers_List = delegator.findList("EmplPerfReviewers", mainCondition2, null, null, null, false);

        if (emplPerfReviewers_List.size() > 0 && "PERF_REVIEW_DISAGREE".equals(emplPerfReviewers_List.get(0).getString("statusType"))) {
            Div divReasonsToDisagree = (Div) root.getFellow("divReasonsToDisagree");
            divReasonsToDisagree.setVisible(true);
            Textbox textBoxDisagreeComents = (Textbox) root.getFellow("textBoxDisagreeComents");
            List<GenericValue> disAggList = delegator.findByAnd("EmplPerfDisAgreedComments", UtilMisc.toMap("reviewerId", managerId, "emplPerfReviewId", emplPerfReviewId));
            if (UtilValidate.isNotEmpty(disAggList))
                textBoxDisagreeComents.setValue(disAggList.get(0).getString("comments"));
        }

        if (emplPerfReviewers_List.size() > 0) {
            statusType = emplPerfReviewers_List.get(0).getString("statusType");
        }

        if ("PERF_REVIEW_AGREED".equals(emplPerfReview_List.get(0).getString("statusType"))) {
            Div divAgreeDisagreeButton = (Div) root.getFellow("divAgreeDisagreeButton");
            divAgreeDisagreeButton.setVisible(false);
            statusType = "PERF_REVIEW_AGREED";
        }
        status.setValue(HrmsUtil.getStatusItemDescription(statusType));
        String perfTemplateId = (String) emplPerfReviewAttribute_list.get(0).get("perfTemplateId");
        EntityCondition perfTemplateSection_condition = EntityCondition.makeCondition("perfTemplateId", EntityOperator.EQUALS, perfTemplateId);
        List<GenericValue> perfTemplateSection_List = delegator.findList("PerfTemplateSection", perfTemplateSection_condition, null, null, null, false);
        int i = 0;
        for (final GenericValue perfTemplateSection_GV : perfTemplateSection_List) {
            String perfReviewSectionId = (String) perfTemplateSection_GV.getString("perfReviewSectionId");
            EntityCondition perfReviewSection_Condition = EntityCondition.makeCondition(UtilMisc.toMap("perfReviewSectionId", perfReviewSectionId));
            List<GenericValue> listPerfReviewSectionName = delegator.findList("PerfReviewSection", perfReviewSection_Condition, null, null, null, false);
            Vbox vboxPerformanceReviewSection = (Vbox) root.getFellow("vboxPerformanceReviewSection");
            Toolbarbutton toolbarbutton = new Toolbarbutton();
            toolbarbutton.setParent(vboxPerformanceReviewSection);
            toolbarbutton.setLabel((String) (listPerfReviewSectionName.get(0).get("sectionName")));
            toolbarbutton.setId("toolBtnManager" + i);

            toolbarbutton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
                public void onEvent(Event event) throws Exception {
                    unselectedToolbarbuttonColor(event.getTarget());
                    selectedToolbarbuttonColor(event.getTarget());
                    PerformanceReview.perfReviewAgreeDisagreeId(event, perfTemplateSection_GV);
                }
            });
            EntityCondition emplPerfReviewAttribute_Condition = EntityCondition
                    .makeCondition("perfReviewSectionId", EntityOperator.EQUALS, perfReviewSectionId);
            List<GenericValue> emplPerfReviewAttribute_List = delegator.findList("EmplPerfReviewAttribute", emplPerfReviewAttribute_Condition, null, null,
                    null, false);
            Label selfRating = (Label) root.getFellow("selfRating");
            int rating = 0;
            int resultSelfRating = 0;
            for (GenericValue gv : emplPerfReviewAttribute_List) {
                String stringSelfRating = (String) (gv.get("selfRating"));
                if (stringSelfRating != null) {
                    int integerSelfRating = Integer.parseInt(stringSelfRating);

                    resultSelfRating = resultSelfRating + integerSelfRating;
                }
                rating++;
            }
            selfRating.setValue(Integer.toString(resultSelfRating));

            i++;

        }

        Events.postEvent("onClick", root.getFellow("toolBtnManager0"), null);
        unselectedToolbarbuttonColor(root.getFellow("toolBtnManager0"));
        selectedToolbarbuttonColor(root.getFellow("toolBtnManager0"));

    }

    @SuppressWarnings("deprecation")
    protected static void perfReviewAgreeDisagreeId(Event event, GenericValue gv) throws InterruptedException, GenericEntityException {

        Component comp = event.getTarget();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewIdForApproval")).getValue();
        String perfReviewSectionId = (String) gv.getString("perfReviewSectionId");
        EntityCondition perfReviewSectionNameCondition = EntityCondition.makeCondition("perfReviewSectionId", EntityOperator.EQUALS, perfReviewSectionId);
        List<GenericValue> perfReviewSectionNameList = delegator.findList("PerfReviewSection", perfReviewSectionNameCondition, null, null, null, false);
        Hbox managerHbox = (Hbox) comp.getFellow("managerHbox");
        String perfReviewSectionName = (String) (perfReviewSectionNameList.get(0).get("sectionName"));
        Caption dynamicContentCaption = (Caption) comp.getFellow("dynamicContentCaption");
        dynamicContentCaption.setLabel(perfReviewSectionName);
        managerHbox.setParent(dynamicContentCaption);
        Groupbox dynamicContent = (Groupbox) comp.getFellow("dynamicContent");
        Label labelSectionNameDescription = (Label) comp.getFellow("labelSectionNameDescription");
        labelSectionNameDescription.setValue((String) (perfReviewSectionNameList.get(0).get("description")));

        Label weightage = (Label) comp.getFellow("weightage");
        Double doubleWeightage = new Double(gv.getString("weightage"));
        if (doubleWeightage != null) {
            Integer StringWeightage = doubleWeightage.intValue();
            weightage.setValue(StringWeightage.toString());
        }

        EntityCondition emplPerReviewAttributeCondition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        EntityCondition emplPerReviewAttributeConditionAndperfReviewSectionNameCondition = EntityCondition.makeCondition(emplPerReviewAttributeCondition,
                perfReviewSectionNameCondition);
        List<GenericValue> emplPerReviewAttributeList = delegator.findList("EmplPerfReviewAttribute",
                emplPerReviewAttributeConditionAndperfReviewSectionNameCondition, null, null, null, false);
        /*******************************************************************/
        List<GenericValue> emplPerfReviewList = delegator.findList("EmplPerfReview", emplPerReviewAttributeCondition, null, null, null, false);
        String statusType = (String) emplPerfReviewList.get(0).get("statusType");
        EntityCondition condition = null;
        /*
		 * if (statusType.equals("PERF_REVIEWED_BY_MGR") ||
		 * statusType.equals("PERF_REVIEW_AGREED") ||
		 * statusType.equals("PERF_REVIEW_DISAGREE")) { String managerId =
		 * (String) ((Label) comp.getFellow("hiddenemplmanagerId")).getValue();
		 * condition = EntityCondition.makeCondition("reviewerId", EntityOperator.EQUALS,
		 * managerId);
		 * emplPerReviewAttributeConditionAndperfReviewSectionNameCondition =
		 * EntityCondition.makeCondition(
		 * emplPerReviewAttributeConditionAndperfReviewSectionNameCondition
		 * ,EntityOperator.AND,condition); }
		 */
        String managerId = (String) ((Label) comp.getFellow("hiddenemplmanagerId")).getValue();
        if (UtilValidate.isNotEmpty(managerId)) {
            condition = EntityCondition.makeCondition("reviewerId", EntityOperator.EQUALS, managerId);
            emplPerReviewAttributeConditionAndperfReviewSectionNameCondition = EntityCondition.makeCondition(
                    emplPerReviewAttributeConditionAndperfReviewSectionNameCondition, EntityOperator.AND, condition);
        }
        List<GenericValue> emplPerReviewersList = delegator.findList("EmplPerfReviewers", emplPerReviewAttributeConditionAndperfReviewSectionNameCondition,
                null, null, null, false);

        Listbox listBoxName = null;
        List<GenericValue> perfRatingList = delegator.findList("PerfRating", null, null, null, null, false);
        perfRatingList.add(0, null);
        SimpleListModel simplelistModelperfRatingList = new SimpleListModel(perfRatingList);
        int i = 0;
        int j = 1;
        Div divParents = (Div) comp.getFellow("divParents");
        Component firstChild = divParents.getFirstChild();
        if (firstChild != null) {
            divParents.removeChild(firstChild);
        }
        Hbox hbox = new Hbox();
        hbox.setParent(divParents);
        Div divChild = new Div();
        divChild.setParent(hbox);
        Div divChild1 = new Div();
        divChild1.setParent(hbox);
        int emplPerReviewersListCount = 0;
        for (GenericValue gvEmplPerReviewAttribute : emplPerReviewAttributeList) {
            List<GenericValue> perfReviewSectionAttributeList = delegator.findList("PerfReviewSectionAttribute", perfReviewSectionNameCondition, null, null,
                    null, false);
            Groupbox groupBox = new Groupbox();
            groupBox.setParent(divChild);
            groupBox.setWidth("402");
            Caption attributeDescriptionCaption = new Caption();
            attributeDescriptionCaption.setParent(groupBox);
            String attributeName = (String) perfReviewSectionAttributeList.get(i).get("attributeName");
            attributeDescriptionCaption.setLabel(j + "." + attributeName);
            Label attributeNameLabel = new Label();
            attributeNameLabel.setValue(attributeName);
            attributeNameLabel.setVisible(false);
            attributeNameLabel.setParent(groupBox);

            Vbox vbox1 = new Vbox();
            vbox1.setParent(groupBox);
            vbox1.setStyle("margin-top:1px");
            Label labelEmployeeRating = new Label();
            labelEmployeeRating.setParent(vbox1);
            labelEmployeeRating.setValue("Employee Rating");

            Listbox listboxRating = new Listbox();
            listboxRating.setParent(vbox1);
            listboxRating.setMold("select");
            listboxRating.setId("listboxSelfRating1" + j);
            listboxRating.setDisabled(true);

            listBoxName = (Listbox) comp.getFellow("listboxSelfRating1" + j);
            for (int c = 0; c < perfRatingList.size(); c++) {
                GenericValue ratingList = perfRatingList.get(c);
                if (ratingList == null) {
                    listBoxName.appendItemApi(null, null);
                } else {
                    String value = ratingList.getString("rating");
                    listBoxName.appendItemApi(ratingList.getString("description"), value);
                    if (value.equals(gvEmplPerReviewAttribute.getString("selfRating"))) {
                        listBoxName.setSelectedIndex(c);
                    }
                }
            }

            String selfComment = gvEmplPerReviewAttribute.getString("selfComment");
            Vbox vbox2 = new Vbox();
            vbox2.setParent(vbox1);
            vbox2.setStyle("margin-top:1px");
            Label labelComments = new Label();
            labelComments.setParent(vbox2);
            labelComments.setValue("Comments");
            Div divForHtml = new Div();
            divForHtml.setParent(vbox2);
            divForHtml.setWidth("400px");
            divForHtml.setHeight("100px");
            divForHtml.setStyle("background-color: #EEEEEE");
            Html html = new Html();
            html.setParent(divForHtml);
            html.setContent(selfComment);

            // For Manager

            Groupbox groupBox1 = new Groupbox();
            groupBox1.setParent(divChild1);
            groupBox1.setWidth("98%");
            Caption attributeDescriptionCaption1 = new Caption();
            attributeDescriptionCaption1.setParent(groupBox1);
            String attributeName1 = (String) perfReviewSectionAttributeList.get(i).get("attributeName");
            attributeDescriptionCaption1.setLabel(j + "." + attributeName1);
            attributeDescriptionCaption1.setId("attributeId" + j);
            Label attributeNameLabel1 = new Label();
            attributeNameLabel1.setValue(attributeName1);
            attributeNameLabel1.setId("attributeName" + j);
            attributeNameLabel1.setVisible(false);
            attributeNameLabel1.setParent(groupBox1);

            Vbox vbox3 = new Vbox();
            vbox3.setParent(groupBox1);
            vbox1.setStyle("margin-top:1px");
            Label labelEmployeeRating1 = new Label();
            labelEmployeeRating1.setParent(vbox3);
            labelEmployeeRating1.setValue("Manager Rating");
            Hbox hboxinvbox2 = new Hbox();
            hboxinvbox2.setParent(vbox3);

            Listbox listboxRating1 = new Listbox();
            listboxRating1.setParent(hboxinvbox2);
            listboxRating1.setMold("select");
            listboxRating1.setId("listboxSelfRating" + j);
            listboxRating1.setDisabled(true);

            listBoxName = (Listbox) comp.getFellow("listboxSelfRating" + j);
            int c = 0;
            for (GenericValue ratingList : perfRatingList) {
                if (ratingList == null) {
                    listBoxName.appendItemApi(null, null);
                } else {
                    String value = ratingList.getString("rating");
                    listBoxName.appendItemApi(ratingList.getString("description"), value);
                    if (value.equals(emplPerReviewersList.get(emplPerReviewersListCount).get("rating"))) {
                        listBoxName.setSelectedIndex(c);
                    }
                }
                c = c + 1;
            }

            // Label labelRatingDescriptoion1 = new Label();
            // labelRatingDescriptoion1.setParent(hboxinvbox2);
            // labelRatingDescriptoion1.setValue("Rating Description:");
            // Label labelRatingDescriptoionDynamic1 = new Label();
            // labelRatingDescriptoionDynamic1.setParent(hboxinvbox2);
            // labelRatingDescriptoionDynamic1.setValue((String)
            // perfReviewSectionAttributeList.get(i).get("description"));

            String managerComments = (String) emplPerReviewersList.get(emplPerReviewersListCount).get("reviewerComment");
            Vbox vbox4 = new Vbox();
            vbox4.setParent(vbox3);
            vbox4.setStyle("margin-top:1px");
            Label labelComments1 = new Label();
            labelComments1.setParent(vbox4);
            labelComments1.setValue("Comments");
            Div divForHtml1 = new Div();
            divForHtml1.setParent(vbox4);
            divForHtml1.setWidth("400px");
            divForHtml1.setHeight("100px");
            divForHtml1.setStyle("background-color: #EEEEEE");
            Html html1 = new Html();
            html1.setParent(divForHtml1);
            html1.setContent(managerComments);

            i++;
            j++;
            emplPerReviewersListCount++;

        }
        Label selfRating = (Label) comp.getFellow("selfRating");
        int rating = 0;
        int resultSelfRating = 0;
        for (GenericValue gvIterate : emplPerReviewAttributeList) {
            String stringSelfRating = (String) (emplPerReviewAttributeList.get(rating).get("selfRating"));
            if (stringSelfRating != null) {
                int integerSelfRating = Integer.parseInt(stringSelfRating);

                resultSelfRating = resultSelfRating + (integerSelfRating * doubleWeightage.intValue());
            }
            rating++;
        }
        selfRating.setValue(Integer.toString(resultSelfRating));
        Label managerRating = (Label) comp.getFellow("managerRating");
        int rating1 = 0;
        int resultManagerRating = 0;
        for (GenericValue gvIterate : emplPerReviewersList) {
            String stringSelfRating = (String) (gvIterate.getString("rating"));
            if (stringSelfRating != null) {
                int integerSelfRating = Integer.parseInt(stringSelfRating);

                resultManagerRating = resultManagerRating + (integerSelfRating * doubleWeightage.intValue());
            }
            rating1++;
        }
        managerRating.setValue(Integer.toString(resultManagerRating));

    }

    @SuppressWarnings("deprecation")
    private void employeeAppraisalViewForApproval(Component root) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        String emplPerfReviewId = (String) Executions.getCurrent().getParameter("reviewIdForApproval");
        EntityCondition emplPerfReviewAttribute_condition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        List<GenericValue> emplPerfReviewAttribute_list = delegator.findList("EmplPerfReviewAttribute", emplPerfReviewAttribute_condition, null, null, null,
                false);
        EntityCondition emplPerfReviewer_condition = EntityCondition.makeCondition("reviewerId", EntityOperator.EQUALS, partyId);
        List<GenericValue> emplPerfReviewerItem = delegator.findList("EmplPerfReviewers", EntityCondition.makeCondition(emplPerfReviewer_condition, emplPerfReviewAttribute_condition), null, null, null, false);
        String statusType = (String) emplPerfReviewerItem.get(0).get("statusType");

        Label status = (Label) root.getFellow("status");

        EntityCondition emplPerfReviewersCon = EntityCondition.makeCondition("statusType", EntityOperator.EQUALS, "PERF_REVIEW_DISAGREE");
        EntityCondition mainCondition = EntityCondition.makeCondition(emplPerfReviewersCon, emplPerfReviewAttribute_condition);
        List<GenericValue> emplPerfReviewers_List = delegator.findList("EmplPerfReviewers", mainCondition, null, null, null, false);
        if (emplPerfReviewers_List.size() > 0) {
            statusType = "PERF_REVIEW_DISAGREE";
            Div divReasonsToDisagree = (Div) root.getFellow("divReasonsToDisagree");
            divReasonsToDisagree.setVisible(true);
            Textbox textBoxDisagreeComents = (Textbox) root.getFellow("textBoxDisagreeComents");
            List<GenericValue> disAggList = delegator.findByAnd("EmplPerfDisAgreedComments", UtilMisc.toMap("reviewerId", partyId, "emplPerfReviewId", emplPerfReviewId));
            if (UtilValidate.isNotEmpty(disAggList))
                textBoxDisagreeComents.setValue(disAggList.get(0).getString("comments"));
        }
        status.setValue(HrmsUtil.getStatusItemDescription(statusType));

        String perfTemplateId = (String) emplPerfReviewAttribute_list.get(0).get("perfTemplateId");
        EntityCondition perfTemplateSection_condition = EntityCondition.makeCondition("perfTemplateId", EntityOperator.EQUALS, perfTemplateId);
        List<GenericValue> perfTemplateSection_List = delegator.findList("PerfTemplateSection", perfTemplateSection_condition, null, null, null, false);
        int i = 0;
        for (final GenericValue perfTemplateSection_GV : perfTemplateSection_List) {
            String perfReviewSectionId = (String) perfTemplateSection_GV.getString("perfReviewSectionId");
            EntityCondition perfReviewSection_Condition = EntityCondition.makeCondition(UtilMisc.toMap("perfReviewSectionId", perfReviewSectionId));
            List<GenericValue> listPerfReviewSectionName = delegator.findList("PerfReviewSection", perfReviewSection_Condition, null, null, null, false);
            Vbox vboxPerformanceReviewSection = (Vbox) root.getFellow("vboxPerformanceReviewSection");
            Toolbarbutton toolbarbutton = new Toolbarbutton();
            toolbarbutton.setParent(vboxPerformanceReviewSection);
            toolbarbutton.setLabel((String) (listPerfReviewSectionName.get(0).get("sectionName")));
            toolbarbutton.setId("toolBtnManager" + i);

            toolbarbutton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
                public void onEvent(Event event) throws Exception {
                    PerformanceReview.myPerformanceReviewIdForApproval(event, perfTemplateSection_GV);
                    unselectedToolbarbuttonColor(event.getTarget());
                    selectedToolbarbuttonColor(event.getTarget());
                }
            });
            EntityCondition emplPerfReviewAttribute_Condition = EntityCondition.makeCondition("perfReviewSectionId", EntityOperator.EQUALS, perfReviewSectionId);
            List<GenericValue> emplPerfReviewAttribute_List = delegator.findList("EmplPerfReviewAttribute", emplPerfReviewAttribute_Condition, null, null,
                    null, false);
            Label selfRating = (Label) root.getFellow("selfRating");
            int rating = 0;
            int resultSelfRating = 0;
            for (GenericValue gv : emplPerfReviewAttribute_List) {
                String stringSelfRating = (String) (emplPerfReviewAttribute_List.get(rating).get("selfRating"));
                if (stringSelfRating != null) {
                    int integerSelfRating = Integer.parseInt(stringSelfRating);

                    resultSelfRating = resultSelfRating + integerSelfRating;
                }
                rating++;
            }
            selfRating.setValue(Integer.toString(resultSelfRating));

            i++;

        }

        Events.postEvent("onClick", root.getFellow("toolBtnManager0"), null);
        unselectedToolbarbuttonColor(root.getFellow("toolBtnManager0"));
        selectedToolbarbuttonColor(root.getFellow("toolBtnManager0"));

    }

    @SuppressWarnings("deprecation")
    protected static void myPerformanceReviewIdForApproval(Event event, GenericValue gv) throws InterruptedException, GenericEntityException {
        Component comp = event.getTarget();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewIdForApproval")).getValue();
        String perfReviewSectionId = (String) gv.getString("perfReviewSectionId");
        EntityCondition perfReviewSectionNameCondition = EntityCondition.makeCondition("perfReviewSectionId", EntityOperator.EQUALS, perfReviewSectionId);
        List<GenericValue> perfReviewSectionNameList = delegator.findList("PerfReviewSection", perfReviewSectionNameCondition, null, null, null, false);
        String perfReviewSectionName = (String) (perfReviewSectionNameList.get(0).get("sectionName"));
        Caption dynamicContentCaption = (Caption) comp.getFellow("dynamicContentCaption");
        dynamicContentCaption.setLabel(perfReviewSectionName);

        Groupbox dynamicContent = (Groupbox) comp.getFellow("dynamicContent");
        Label labelSectionNameDescription = (Label) comp.getFellow("labelSectionNameDescription");
        labelSectionNameDescription.setValue((String) (perfReviewSectionNameList.get(0).get("description")));

        Label weightage = (Label) comp.getFellow("weightage");
        Double doubleWeightage = new Double(gv.getString("weightage"));
        if (doubleWeightage != null) {
            Integer StringWeightage = doubleWeightage.intValue();
            weightage.setValue(StringWeightage.toString());
        }

        EntityCondition emplPerReviewAttributeCondition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        EntityCondition emplPerReviewAttributeConditionAndperfReviewSectionNameCondition = EntityCondition.makeCondition(emplPerReviewAttributeCondition,
                perfReviewSectionNameCondition);
        EntityCondition partyIdCondition = EntityCondition.makeCondition("reviewerId", EntityOperator.EQUALS, partyId);
        EntityCondition emplPerfReviewersCondition = EntityCondition.makeCondition(emplPerReviewAttributeCondition, perfReviewSectionNameCondition,
                partyIdCondition);
        List<GenericValue> emplPerReviewAttributeList = delegator.findList("EmplPerfReviewAttribute",
                emplPerReviewAttributeConditionAndperfReviewSectionNameCondition, null, null, null, false);
        List<GenericValue> emplPerReviewersList = delegator.findList("EmplPerfReviewers", emplPerfReviewersCondition, null, null, null, false);

        Listbox listBoxName = null;
        List<GenericValue> perfRatingList = delegator.findList("PerfRating", null, null, null, null, false);
        perfRatingList.add(0, null);
        SimpleListModel simplelistModelperfRatingList = new SimpleListModel(perfRatingList);

        int i = 0;
        int j = 1;
        int emplPerReviewersListCount = 0;
        Div divParents = (Div) comp.getFellow("divParents");
        Component firstChild = divParents.getFirstChild();
        if (firstChild != null) {
            divParents.removeChild(firstChild);
        }
        Hbox hbox = new Hbox();
        hbox.setParent(divParents);
        Div divChild = new Div();
        divChild.setParent(hbox);
        Div divChild1 = new Div();
        divChild1.setParent(hbox);
        for (GenericValue gvEmplPerReviewAttribute : emplPerReviewAttributeList) {
            List<GenericValue> perfReviewSectionAttributeList = delegator.findList("PerfReviewSectionAttribute", perfReviewSectionNameCondition, null, null,
                    null, false);
            Groupbox groupBox = new Groupbox();
            groupBox.setParent(divChild);
            groupBox.setWidth("402");
            Caption attributeDescriptionCaption = new Caption();
            attributeDescriptionCaption.setParent(groupBox);
            String attributeName = (String) perfReviewSectionAttributeList.get(i).get("attributeName");
            attributeDescriptionCaption.setLabel(j + "." + attributeName);
            Label attributeNameLabel = new Label();
            attributeNameLabel.setValue(attributeName);
            attributeNameLabel.setVisible(false);
            attributeNameLabel.setParent(groupBox);

            Vbox vbox1 = new Vbox();
            vbox1.setParent(groupBox);
            vbox1.setStyle("margin-top:1px");
            Label labelEmployeeRating = new Label();
            labelEmployeeRating.setParent(vbox1);
            labelEmployeeRating.setValue("Employee Rating");

            Listbox listboxRating = new Listbox();
            listboxRating.setParent(vbox1);
            listboxRating.setMold("select");
            listboxRating.setId("listboxSelfRating1" + j);
            listboxRating.setDisabled(true);

            listBoxName = (Listbox) comp.getFellow("listboxSelfRating1" + j);
            for (int c = 0; c < perfRatingList.size(); c++) {
                GenericValue ratingList = perfRatingList.get(c);
                if (ratingList == null) {
                    listBoxName.appendItemApi(null, null);
                } else {
                    String value = ratingList.getString("rating");
                    listBoxName.appendItemApi(ratingList.getString("description"), value);
                    if (value.equals(gvEmplPerReviewAttribute.getString("selfRating"))) {
                        listBoxName.setSelectedIndex(c);
                    }
                }
            }

            String selfComment = gvEmplPerReviewAttribute.getString("selfComment");
            Vbox vbox2 = new Vbox();
            vbox2.setParent(vbox1);
            vbox2.setStyle("margin-top:1px");
            Label labelComments = new Label();
            labelComments.setParent(vbox2);
            labelComments.setValue("Comments");
            Div divForHtml = new Div();
            divForHtml.setParent(vbox2);
            divForHtml.setWidth("400px");
            divForHtml.setHeight("100px");
            divForHtml.setStyle("background-color: #EEEEEE");
            Html html = new Html();
            html.setParent(divForHtml);
            html.setContent(selfComment);

            // For Manager Approve

            Groupbox groupBox1 = new Groupbox();
            groupBox1.setParent(divChild1);
            groupBox1.setWidth("98%");
            Caption attributeDescriptionCaption1 = new Caption();
            attributeDescriptionCaption1.setParent(groupBox1);
            String attributeName1 = (String) perfReviewSectionAttributeList.get(i).get("attributeName");
            attributeDescriptionCaption1.setLabel(j + "." + attributeName1);
            attributeDescriptionCaption1.setId("attributeId" + j);
            Label attributeNameLabel1 = new Label();
            attributeNameLabel1.setValue(attributeName1);
            attributeNameLabel1.setId("attributeName" + j);
            attributeNameLabel1.setVisible(false);
            attributeNameLabel1.setParent(groupBox1);

            Vbox vbox3 = new Vbox();
            vbox3.setParent(groupBox1);
            vbox1.setStyle("margin-top:1px");
            Hbox hboxManagerRating = new Hbox();
            hboxManagerRating.setParent(vbox3);
            Label labelEmployeeRating1 = new Label();
            labelEmployeeRating1.setParent(hboxManagerRating);
            labelEmployeeRating1.setValue("Manager Rating");
            Label labelEmployeeRatingMandatoryField = new Label();
            labelEmployeeRatingMandatoryField.setParent(hboxManagerRating);
            labelEmployeeRatingMandatoryField.setValue("*");
            labelEmployeeRatingMandatoryField.setStyle("color:red");
            Hbox hboxinvbox2 = new Hbox();
            hboxinvbox2.setParent(vbox3);

            Listbox listboxRating1 = new Listbox();
            listboxRating1.setParent(hboxinvbox2);
            listboxRating1.setMold("select");
            listboxRating1.setId("listboxSelfRating" + j);

            listboxRating1.addEventListener("onSelect", new org.zkoss.zk.ui.event.EventListener() {
                public void onEvent(Event event) throws Exception {
                    Clients.clearWrongValue(event.getTarget());
                }
            });

            listBoxName = (Listbox) comp.getFellow("listboxSelfRating" + j); // Render
            for (int c = 0; c < perfRatingList.size(); c++) {
                GenericValue ratingList = perfRatingList.get(c);
                if (ratingList == null) {
                    listBoxName.appendItemApi(null, null);
                } else {
                    String value = ratingList.getString("rating");
                    listBoxName.appendItemApi(ratingList.getString("description"), value);
                    if (value.equals((String) emplPerReviewersList.get(emplPerReviewersListCount).getString("rating"))) {
                        listBoxName.setSelectedIndex(c);
                    }
                }
            }

            Vbox vbox4 = new Vbox();
            vbox4.setParent(vbox3);
            vbox4.setStyle("margin-top:1px");
            Hbox hboxManagerComments = new Hbox();
            hboxManagerComments.setParent(vbox4);
            Label labelComments1 = new Label();
            labelComments1.setParent(hboxManagerComments);
            labelComments1.setValue("Comments");
            Label labelComments1MandatoryField = new Label();
            labelComments1MandatoryField.setParent(hboxManagerComments);
            labelComments1MandatoryField.setValue("*");
            labelComments1MandatoryField.setStyle("color:red");

            String reviewerComments = (String) emplPerReviewersList.get(emplPerReviewersListCount).get("reviewerComment");
            Textbox fckeditor = new Textbox();
            fckeditor.setParent(vbox4);
            fckeditor.setId("fckeditorComments" + j);
            fckeditor.setWidth("400px");
            fckeditor.setHeight("100px");
            fckeditor.setValue(reviewerComments);
            fckeditor.setMultiline(true);
            fckeditor.addEventListener("onBlur", new org.zkoss.zk.ui.event.EventListener() {
                public void onEvent(Event event) throws Exception {
                    Clients.clearWrongValue(event.getTarget());
                }
            });
            i++;
            j++;
            emplPerReviewersListCount++;

        }
        Label selfRating = (Label) comp.getFellow("selfRating");
        Integer resultSelfRating = 0;
        Integer results = 0;
        String stringWeightage = (String) ((Label) comp.getFellow("weightage")).getValue();
        Integer getDoubleWeightage = Integer.parseInt(stringWeightage);
        for (GenericValue gvIterate : emplPerReviewAttributeList) {
            String stringSelfRating = (String) gvIterate.getString("selfRating");
            if (stringSelfRating != null) {
                int integerSelfRating = Integer.parseInt(stringSelfRating);
                resultSelfRating = resultSelfRating + integerSelfRating;
                results = resultSelfRating * getDoubleWeightage;
            }
        }
        selfRating.setValue(results.toString());

        Label managerRating = (Label) comp.getFellow("managerRating");
        Integer resultManagerRating = 0;
        Integer results1 = 0;
        for (GenericValue gvIterate : emplPerReviewersList) {
            String stringSelfRating = (String) gvIterate.getString("rating");
            if (stringSelfRating != null) {
                int integerSelfRating = Integer.parseInt(stringSelfRating);

                resultManagerRating = resultManagerRating + integerSelfRating;
                results1 = resultManagerRating * getDoubleWeightage;
            }
        }
        managerRating.setValue(results1.toString());
    }

    @SuppressWarnings("deprecation")
    private void employeeAppraisalView(Component root) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String emplPerfReviewId = (String) Executions.getCurrent().getParameter("reviewId");
        EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId));
        List<GenericValue> perfValues = delegator.findList("PerfSectionAndAttribute", entityCondition, null, null, null, false);
        List<GenericValue> emplPerfReviewEntity = delegator.findList("EmplPerfReview", entityCondition, null, null, null, false);
        String partyId = (String) userLogin.get("partyId");
        if(UtilValidate.isNotEmpty(emplPerfReviewEntity)){
        	partyId = emplPerfReviewEntity.get(0).getString("partyId");
        }
        GenericValue emplPosGV = HumanResUtil.getEmplPositionForParty(partyId, delegator);
        String emplPositionTypeId = (String) emplPosGV.getString("emplPositionTypeId");
        Label statusType = (Label) root.getFellow("status");
        String getStatusType = UtilValidate.isNotEmpty(perfValues) ? (String) (perfValues.get(0).get("statusType")) : "";
        EntityCondition condition = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, getStatusType);
        List<GenericValue> statusItemList = delegator.findList("StatusItem", condition, null, null, null, false);
        statusType.setValue(UtilValidate.isNotEmpty(statusItemList) ? (String) (statusItemList.get(0).get("description")) : "");
        // Show Performance Review Section
        String perfReviewId = UtilValidate.isNotEmpty(perfValues) ? (String) (perfValues.get(0).get("perfReviewId")) : "";
        EntityCondition entityCondition1 = EntityCondition.makeCondition(UtilMisc.toMap("perfReviewId", perfReviewId));
        EntityCondition entityCondition2 = EntityCondition.makeCondition(UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId));
        EntityCondition entityConditionPositionType = EntityCondition.makeCondition(entityCondition1, entityCondition2);
        List<GenericValue> listPerfTemplateId = delegator.findList("PerfReviewAssocTemplates", entityConditionPositionType, null, null, null, false);
        String perfTemplateId = UtilValidate.isNotEmpty(listPerfTemplateId) ? (String) listPerfTemplateId.get(0).getString("perfTemplateId") : "";
        EntityCondition entityConditionPerTemplateSection = EntityCondition.makeCondition(UtilMisc.toMap("perfTemplateId", perfTemplateId));
        List<GenericValue> listPerfTemplateSection = delegator.findList("PerfTemplateSection", entityConditionPerTemplateSection, null, null, null, false);
        int i = 0;
        for (final GenericValue gv : listPerfTemplateSection) {
            String perfReviewSectionId = (String) gv.getString("perfReviewSectionId");
            EntityCondition PerfReviewSection = EntityCondition.makeCondition(UtilMisc.toMap("perfReviewSectionId", perfReviewSectionId));
            List<GenericValue> listPerfReviewSectionName = delegator.findList("PerfReviewSection", PerfReviewSection, null, null, null, false);

            Vbox vboxPerformanceReviewSection = (Vbox) root.getFellow("vboxPerformanceReviewSection");
            Toolbarbutton toolbarbutton = new Toolbarbutton();
            toolbarbutton.setParent(vboxPerformanceReviewSection);
            toolbarbutton.setLabel((String) (listPerfReviewSectionName.get(0).get("sectionName")));
            toolbarbutton.setId("toolBtn" + i);

            toolbarbutton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
                public void onEvent(Event event) throws Exception {
                    PerformanceReview.myPerformanceReviewId(event, gv);
                    unselectedToolbarbuttonColor(event.getTarget());
                    selectedToolbarbuttonColor(event.getTarget());
                }
            });

            i++;

        }
        if (root.getFellowIfAny("toolBtn0", true) != null) {
            Events.postEvent("onClick", root.getFellowIfAny("toolBtn0", true), null);
            unselectedToolbarbuttonColor(root.getFellowIfAny("toolBtn0", true));
            selectedToolbarbuttonColor(root.getFellowIfAny("toolBtn0", true));
        }

    }

    private void selectedToolbarbuttonColor(Component cmp) {
        ((Toolbarbutton) cmp).setStyle("color:#D87700");
    }

    private void unselectedToolbarbuttonColor(Component cmp) {
        for (Iterator iter = cmp.getParent().getChildren().iterator(); iter.hasNext(); ) {
            Component c = (Component) iter.next();
            if (c instanceof Toolbarbutton) {
                ((Toolbarbutton) c).setStyle("color:#249");
            }
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    private void employeeAppraisalViewForClosure(Component root) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        String emplPerfReviewId = (String) Executions.getCurrent().getParameter("reviewIdForApproval");
        EntityCondition emplPerfReviewAttribute_condition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        List<GenericValue> emplPerfReviewAttribute_list = delegator.findList("EmplPerfReviewAttribute", emplPerfReviewAttribute_condition, null, null, null,
                false);
        // EntityCondition emplPerfReview_Condition = new
        // EntityExpr("emplPerfReviewId", EntityOperator.EQUALS,
        // emplPerfReviewId);
        List<GenericValue> emplPerfReview_List = delegator.findList("EmplPerfReview", emplPerfReviewAttribute_condition, null, null, null, false);
        String statusType = (String) emplPerfReview_List.get(0).get("statusType");
        Label status = (Label) root.getFellow("status");
        status.setValue(HrmsUtil.getStatusItemDescription(statusType));

        String perfTemplateId = (String) emplPerfReviewAttribute_list.get(0).get("perfTemplateId");
        EntityCondition perfTemplateSection_condition = EntityCondition.makeCondition("perfTemplateId", EntityOperator.EQUALS, perfTemplateId);
        List<GenericValue> perfTemplateSection_List = delegator.findList("PerfTemplateSection", perfTemplateSection_condition, null, null, null, false);
        int i = 0;
        for (final GenericValue perfTemplateSection_GV : perfTemplateSection_List) {
            String perfReviewSectionId = (String) perfTemplateSection_GV.getString("perfReviewSectionId");
            EntityCondition perfReviewSection_Condition = EntityCondition.makeCondition(UtilMisc.toMap("perfReviewSectionId", perfReviewSectionId));
            List<GenericValue> listPerfReviewSectionName = delegator.findList("PerfReviewSection", perfReviewSection_Condition, null, null, null, false);
            Vbox vboxPerformanceReviewSection = (Vbox) root.getFellow("vboxPerformanceReviewSection");
            Toolbarbutton toolbarbutton = new Toolbarbutton();
            toolbarbutton.setParent(vboxPerformanceReviewSection);
            toolbarbutton.setLabel((String) (listPerfReviewSectionName.get(0).get("sectionName")));
            toolbarbutton.setId("toolBtnManager" + i);

            toolbarbutton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
                public void onEvent(Event event) throws Exception {
                    unselectedToolbarbuttonColor(event.getTarget());
                    selectedToolbarbuttonColor(event.getTarget());
                    PerformanceReview.myPerformanceReviewIdForCloser(event, perfTemplateSection_GV);
                }
            });
            EntityCondition emplPerfReviewAttribute_Condition = EntityCondition.makeCondition("perfReviewSectionId", EntityOperator.EQUALS, perfReviewSectionId);
            List<GenericValue> emplPerfReviewAttribute_List = delegator.findList("EmplPerfReviewAttribute", emplPerfReviewAttribute_Condition, null, null,
                    null, false);
            Label selfRating = (Label) root.getFellow("selfRating");
            int rating = 0;
            int resultSelfRating = 0;
            for (GenericValue gv : emplPerfReviewAttribute_List) {
                String stringSelfRating = (String) (emplPerfReviewAttribute_List.get(rating).get("selfRating"));
                if (stringSelfRating != null) {
                    int integerSelfRating = Integer.parseInt(stringSelfRating);

                    resultSelfRating = resultSelfRating + integerSelfRating;
                }
                rating++;
            }
            selfRating.setValue(Integer.toString(resultSelfRating));

            i++;

        }

        Events.postEvent("onClick", root.getFellow("toolBtnManager0"), null);
        unselectedToolbarbuttonColor(root.getFellow("toolBtnManager0"));
        selectedToolbarbuttonColor(root.getFellow("toolBtnManager0"));
    }

    @SuppressWarnings("deprecation")
    protected static void myPerformanceReviewIdForCloser(Event event, GenericValue gv) throws InterruptedException, GenericEntityException {
        Component comp = event.getTarget();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewIdForApproval")).getValue();
        String hiddenManagerId = (String) ((Label) comp.getFellow("hiddenManagerId")).getValue();
        String perfReviewSectionId = (String) gv.getString("perfReviewSectionId");
        EntityCondition perfReviewSectionNameCondition = EntityCondition.makeCondition("perfReviewSectionId", EntityOperator.EQUALS, perfReviewSectionId);
        List<GenericValue> perfReviewSectionNameList = delegator.findList("PerfReviewSection", perfReviewSectionNameCondition, null, null, null, false);
        String perfReviewSectionName = (String) (perfReviewSectionNameList.get(0).get("sectionName"));
        Caption dynamicContentCaption = (Caption) comp.getFellow("dynamicContentCaption");
        dynamicContentCaption.setLabel(perfReviewSectionName);

        Groupbox dynamicContent = (Groupbox) comp.getFellow("dynamicContent");
        Label labelSectionNameDescription = (Label) comp.getFellow("labelSectionNameDescription");
        labelSectionNameDescription.setValue((String) (perfReviewSectionNameList.get(0).get("description")));

        Label weightage = (Label) comp.getFellow("weightage");
        Double doubleWeightage = new Double(gv.getString("weightage"));
        if (doubleWeightage != null) {
            Integer StringWeightage = doubleWeightage.intValue();
            weightage.setValue(StringWeightage.toString());
        }

        EntityCondition emplPerReviewAttributeCondition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        EntityCondition emplPerReviewAttributeConditionAndperfReviewSectionNameCondition = EntityCondition.makeCondition(emplPerReviewAttributeCondition,
                perfReviewSectionNameCondition);
        List<GenericValue> emplPerReviewAttributeList = delegator.findList("EmplPerfReviewAttribute",
                emplPerReviewAttributeConditionAndperfReviewSectionNameCondition, null, null, null, false);
        EntityCondition emplPerfReviewersCon = EntityCondition.makeCondition("reviewerId", EntityOperator.EQUALS, hiddenManagerId);
        EntityCondition emplPerfReviewersMainCondition = EntityCondition.makeCondition(emplPerfReviewersCon,
                emplPerReviewAttributeConditionAndperfReviewSectionNameCondition);
        List<GenericValue> emplPerReviewersList = delegator.findList("EmplPerfReviewers", emplPerfReviewersMainCondition, null, null, null, false);

        Listbox listBoxName = null;
        List<GenericValue> perfRatingList = delegator.findList("PerfRating", null, null, null, null, false);
        perfRatingList.add(0, null);
        SimpleListModel simplelistModelperfRatingList = new SimpleListModel(perfRatingList);

        int i = 0;
        int j = 1;
        int emplPerReviewersListCount = 0;
        Div divParents = (Div) comp.getFellow("divParents");
        Component firstChild = divParents.getFirstChild();
        if (firstChild != null) {
            divParents.removeChild(firstChild);
        }
        Hbox hbox = new Hbox();
        hbox.setParent(divParents);
        Div divChild = new Div();
        divChild.setParent(hbox);
        Div divChild1 = new Div();
        divChild1.setParent(hbox);
        for (GenericValue gvEmplPerReviewAttribute : emplPerReviewAttributeList) {
            List<GenericValue> perfReviewSectionAttributeList = delegator.findList("PerfReviewSectionAttribute", perfReviewSectionNameCondition, null, null,
                    null, false);
            Groupbox groupBox = new Groupbox();
            groupBox.setParent(divChild);
            groupBox.setWidth("402");
            Caption attributeDescriptionCaption = new Caption();
            attributeDescriptionCaption.setParent(groupBox);
            String attributeName = (String) perfReviewSectionAttributeList.get(i).get("attributeName");
            attributeDescriptionCaption.setLabel(j + "." + attributeName);
            Label attributeNameLabel = new Label();
            attributeNameLabel.setValue(attributeName);
            attributeNameLabel.setVisible(false);
            attributeNameLabel.setParent(groupBox);

            Vbox vbox1 = new Vbox();
            vbox1.setParent(groupBox);
            vbox1.setStyle("margin-top:1px");
            Label labelEmployeeRating = new Label();
            labelEmployeeRating.setParent(vbox1);
            labelEmployeeRating.setValue("Employee Rating");

            Listbox listboxRating = new Listbox();
            listboxRating.setParent(vbox1);
            listboxRating.setMold("select");
            listboxRating.setId("listboxSelfRating1" + j);
            listboxRating.setDisabled(true);

            listBoxName = (Listbox) comp.getFellow("listboxSelfRating1" + j);
            for (int c = 0; c < perfRatingList.size(); c++) {
                GenericValue ratingList = perfRatingList.get(c);
                if (ratingList == null) {
                    listBoxName.appendItemApi(null, null);
                } else {
                    String value = ratingList.getString("rating");
                    listBoxName.appendItemApi(ratingList.getString("description"), value);
                    if (value.equals(gvEmplPerReviewAttribute.getString("selfRating"))) {
                        listBoxName.setSelectedIndex(c);
                    }
                }
            }

            String selfComment = gvEmplPerReviewAttribute.getString("selfComment");
            Vbox vbox2 = new Vbox();
            vbox2.setParent(vbox1);
            vbox2.setStyle("margin-top:1px");
            Label labelComments = new Label();
            labelComments.setParent(vbox2);
            labelComments.setValue("Comments");
            Div divForHtml = new Div();
            divForHtml.setParent(vbox2);
            divForHtml.setWidth("400px");
            divForHtml.setHeight("100px");
            divForHtml.setStyle("background-color: #EEEEEE");
            Html html = new Html();
            html.setParent(divForHtml);
            html.setContent(selfComment);

            // For Manager Approve

            Groupbox groupBox1 = new Groupbox();
            groupBox1.setParent(divChild1);
            groupBox1.setWidth("98%");
            Caption attributeDescriptionCaption1 = new Caption();
            attributeDescriptionCaption1.setParent(groupBox1);
            String attributeName1 = (String) perfReviewSectionAttributeList.get(i).get("attributeName");
            attributeDescriptionCaption1.setLabel(j + "." + attributeName1);
            attributeDescriptionCaption1.setId("attributeId" + j);
            Label attributeNameLabel1 = new Label();
            attributeNameLabel1.setValue(attributeName1);
            attributeNameLabel1.setId("attributeName" + j);
            attributeNameLabel1.setVisible(false);
            attributeNameLabel1.setParent(groupBox1);

            Vbox vbox3 = new Vbox();
            vbox3.setParent(groupBox1);
            vbox1.setStyle("margin-top:1px");
            Label labelEmployeeRating1 = new Label();
            labelEmployeeRating1.setParent(vbox3);
            labelEmployeeRating1.setValue("Manager Rating");
            Hbox hboxinvbox2 = new Hbox();
            hboxinvbox2.setParent(vbox3);

            Listbox listboxRating1 = new Listbox();
            listboxRating1.setParent(hboxinvbox2);
            listboxRating1.setMold("select");
            listboxRating1.setId("listboxSelfRating" + j);
            listboxRating1.setDisabled(true);

            listBoxName = (Listbox) comp.getFellow("listboxSelfRating" + j); // Render
            for (int c = 0; c < perfRatingList.size(); c++) {
                GenericValue ratingList = perfRatingList.get(c);
                if (ratingList == null) {
                    listBoxName.appendItemApi(null, null);
                } else {
                    String value = ratingList.getString("rating");
                    listBoxName.appendItemApi(ratingList.getString("description"), value);
                    if (value.equals((String) emplPerReviewersList.get(emplPerReviewersListCount).getString("rating"))) {
                        listBoxName.setSelectedIndex(c);
                    }
                }
            }

            Vbox vbox4 = new Vbox();
            vbox4.setParent(vbox3);
            vbox4.setStyle("margin-top:1px");
            Label labelComments1 = new Label();
            labelComments1.setParent(vbox4);
            labelComments1.setValue("Comments");

            String reviewerComments = (String) emplPerReviewersList.get(emplPerReviewersListCount).get("reviewerComment");
            Div divForHtml1 = new Div();
            divForHtml1.setParent(vbox4);
            divForHtml1.setWidth("400px");
            divForHtml1.setHeight("100px");
            divForHtml1.setStyle("background-color: #EEEEEE");
            Html html1 = new Html();
            html1.setParent(divForHtml1);
            html1.setContent(reviewerComments);
            i++;
            j++;
            emplPerReviewersListCount++;

        }
        Label selfRating = (Label) comp.getFellow("selfRating");
        int rating = 0;
        int resultSelfRating = 0;
        for (GenericValue gvIterate : emplPerReviewAttributeList) {
            String stringSelfRating = (String) (emplPerReviewAttributeList.get(rating).get("selfRating"));
            if (stringSelfRating != null) {
                int integerSelfRating = Integer.parseInt(stringSelfRating);

                resultSelfRating = resultSelfRating + integerSelfRating * doubleWeightage.intValue();
            }
            rating++;
        }
        selfRating.setValue(Integer.toString(resultSelfRating));

        Label managerRating = (Label) comp.getFellow("managerRating");
        int rating1 = 0;
        int resultManagerRating = 0;
        for (GenericValue gvIterate : emplPerReviewersList) {
            String stringSelfRating = (String) (emplPerReviewersList.get(rating1).get("rating"));
            if (stringSelfRating != null) {
                int integerSelfRating = Integer.parseInt(stringSelfRating);

                resultManagerRating = resultManagerRating + integerSelfRating * doubleWeightage.intValue();
            }
            rating1++;
        }
        managerRating.setValue(Integer.toString(resultManagerRating));

        String labelLabelIsAdmin = (String) ((Label) comp.getFellow("labelIsAdmin")).getValue();
        List<GenericValue> emplPerReviewerList = delegator.findList("EmplPerfReview", emplPerReviewAttributeCondition, null, null, null, false);
        if (labelLabelIsAdmin == "false") {
            Intbox textboxOverallRating = (Intbox) comp.getFellow("textboxOverallRating");
            Textbox textboxComments = (Textbox) comp.getFellow("textboxComments");
            textboxComments.setValue((String) emplPerReviewerList.get(0).get("comments"));
            Textbox textboxFeedBack = (Textbox) comp.getFellow("textboxFeedBack");
            textboxFeedBack.setValue((String) emplPerReviewerList.get(0).get("feedback"));
            textboxOverallRating.setVisible(false);
            textboxComments.setReadonly(true);
            textboxFeedBack.setReadonly(true);
            Hbox HboxHrRating = (Hbox) comp.getFellow("HboxHrRating");
            HboxHrRating.setVisible(true);
            Label hrRating = (Label) comp.getFellow("hrRating");
            hrRating.setValue((String) emplPerReviewerList.get(0).get("overallRating"));
            Button buttonEmplPerfClosure = (Button) comp.getFellow("buttonEmplPerfClosure");
            buttonEmplPerfClosure.setVisible(false);
            Label labelOverAllRating = (Label) comp.getFellow("labelOverAllRating");
            labelOverAllRating.setVisible(false);
        }

    }

    @SuppressWarnings("deprecation")
    protected static void myPerformanceReviewId(Event event, GenericValue gv) throws InterruptedException, GenericEntityException {
        Component comp = event.getTarget();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewId")).getValue();
        String perfReviewSectionId = (String) gv.getString("perfReviewSectionId");
        EntityCondition perfReviewSectionNameCondition = EntityCondition.makeCondition("perfReviewSectionId", EntityOperator.EQUALS, perfReviewSectionId);
        List<GenericValue> perfReviewSectionNameList = delegator.findList("PerfReviewSection", perfReviewSectionNameCondition, null, null, null, false);
        String perfReviewSectionName = (String) (perfReviewSectionNameList.get(0).get("sectionName"));
        Caption dynamicContentCaption = (Caption) comp.getFellow("dynamicContentCaption");
        dynamicContentCaption.setLabel(perfReviewSectionName);

        Groupbox dynamicContent = (Groupbox) comp.getFellow("dynamicContent");
        Label labelSectionNameDescription = (Label) comp.getFellow("labelSectionNameDescription");
        labelSectionNameDescription.setValue((String) (perfReviewSectionNameList.get(0).get("description")));

        Label weightage = (Label) comp.getFellow("weightage");
        Double doubleWeightage = new Double(gv.getString("weightage"));
        if (doubleWeightage != null) {
            Integer stringWeightage = doubleWeightage.intValue();
            weightage.setValue(stringWeightage.toString());
        }

        EntityCondition emplPerReviewAttributeCondition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        EntityCondition emplPerReviewAttributeConditionAndperfReviewSectionNameCondition = EntityCondition.makeCondition(emplPerReviewAttributeCondition,
                perfReviewSectionNameCondition);
        List<GenericValue> emplPerReviewAttributeList = delegator.findList("EmplPerfReviewAttribute",
                emplPerReviewAttributeConditionAndperfReviewSectionNameCondition, null, null, null, false);
        String statusType = (String) ((Label) comp.getFellow("status")).getValue();
        Listbox listBoxName = null;
        final List<GenericValue> perfRatingList = delegator.findList("PerfRating", null, null, null, null, false);
        perfRatingList.add(0, null);
        SimpleListModel simplelistModelperfRatingList = new SimpleListModel(perfRatingList);

        int i = 0;
        int j = 1;
        Div divParents = (Div) comp.getFellow("divParents");
        Component firstChild = divParents.getFirstChild();
        if (firstChild != null) {
            divParents.removeChild(firstChild);
        }
        Div divChild = new Div();
        divChild.setParent(divParents);
        for (GenericValue gvEmplPerReviewAttribute : emplPerReviewAttributeList) {
            List<GenericValue> perfReviewSectionAttributeList = delegator.findList("PerfReviewSectionAttribute", perfReviewSectionNameCondition, null, null,
                    null, false);
            Groupbox groupBox = new Groupbox();
            groupBox.setParent(divChild);
            groupBox.setWidth("98%");
            Caption attributeDescriptionCaption = new Caption();
            attributeDescriptionCaption.setParent(groupBox);
            String attributeName = (String) perfReviewSectionAttributeList.get(i).get("attributeName");
            attributeDescriptionCaption.setLabel(j + "." + attributeName);
            attributeDescriptionCaption.setId("attributeId" + j);
            Label attributeNameLabel = new Label();
            attributeNameLabel.setValue(attributeName);
            attributeNameLabel.setId("attributeName" + j);
            attributeNameLabel.setVisible(false);
            attributeNameLabel.setParent(groupBox);

            Vbox vbox1 = new Vbox();
            vbox1.setParent(groupBox);
            vbox1.setStyle("margin-top:5px");
            Hbox hboxEmpRating = new Hbox();
            hboxEmpRating.setParent(vbox1);
            Label labelEmployeeRating = new Label();
            labelEmployeeRating.setParent(hboxEmpRating);
            labelEmployeeRating.setValue("Employee Rating");

            Label labelEmployeeRatingMandatoryField = new Label();
            labelEmployeeRatingMandatoryField.setParent(hboxEmpRating);
            labelEmployeeRatingMandatoryField.setValue("*");
            labelEmployeeRatingMandatoryField.setStyle("color:red");

            Hbox hboxinvbox1 = new Hbox();
            hboxinvbox1.setParent(vbox1);

            Listbox listboxRating = new Listbox();
            listboxRating.setParent(hboxinvbox1);
            listboxRating.setMold("select");
            listboxRating.setId("listboxSelfRating" + j);
            if (statusType.equals("Review Pending"))
                listboxRating.setDisabled(true);
            listBoxName = (Listbox) comp.getFellow("listboxSelfRating" + j); // Render
            for (int c = 0; c < perfRatingList.size(); c++) {
                GenericValue ratingList = perfRatingList.get(c);
                if (ratingList == null) {
                    listBoxName.appendItemApi(null, null);
                } else {
                    String value = ratingList.getString("rating");
                    listBoxName.appendItemApi(ratingList.getString("description"), value);
                    if (value.equals(gvEmplPerReviewAttribute.getString("selfRating"))) {
                        listBoxName.setSelectedIndex(c);
                    }
                }
            }

            listboxRating.addEventListener("onSelect", new org.zkoss.zk.ui.event.EventListener() {
                public void onEvent(Event event) throws Exception {
                    Clients.clearWrongValue(event.getTarget());
                    PerformanceReview.perfRatingListBox(event, perfRatingList);

                }
            });
            Vbox vbox2 = new Vbox();
            vbox2.setParent(vbox1);
            vbox2.setStyle("margin-top:1px");
            Hbox commentHbox = new Hbox();
            commentHbox.setParent(vbox2);
            Label labelComments = new Label();
            labelComments.setParent(commentHbox);
            labelComments.setValue("Comments");
            Label labelCommentsMandatoryField = new Label();
            labelCommentsMandatoryField.setParent(commentHbox);
            labelCommentsMandatoryField.setValue("*");
            labelCommentsMandatoryField.setStyle("color:red");
            if (statusType.equals("Review Pending")) {
                Div divComments = new Div();
                divComments.setParent(vbox2);
                divComments.setWidth("800px");
                divComments.setHeight("50px");
                divComments.setStyle("background-color: #EEEEEE");
                Html html = new Html();
                html.setParent(divComments);
                html.setContent(gvEmplPerReviewAttribute.getString("selfComment"));
                Button selfRatingSubmitButton = (Button) comp.getFellow("selfRatingSubmitButton");
                Button selfRatingSaveButton = (Button) comp.getFellow("selfRatingSaveButton");
                selfRatingSubmitButton.setDisabled(true);
                selfRatingSaveButton.setDisabled(true);
            } else {
                Textbox fckeditor = new Textbox();
                fckeditor.setParent(vbox2);
                fckeditor.setId("fckeditorComments" + j);
                fckeditor.setWidth("800px");
                fckeditor.setHeight("100px");
                fckeditor.setMultiline(true);
                fckeditor.setValue(gvEmplPerReviewAttribute.getString("selfComment"));

                fckeditor.addEventListener("onBlur", new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(Event event) throws Exception {
                        Clients.clearWrongValue(event.getTarget());
                    }
                });
            }
            i++;
            j++;
        }
        Label selfRating = (Label) comp.getFellow("selfRating");
        int rating = 0;
        Integer resultSelfRating = 0;
        Integer results = 0;
        String stringWeightage = (String) ((Label) comp.getFellow("weightage")).getValue();
        if (stringWeightage.equals(""))
            stringWeightage = "1";
        Integer getDoubleWeightage = Integer.parseInt(stringWeightage);
        for (GenericValue gv1 : emplPerReviewAttributeList) {
            String stringSelfRating = (String) (emplPerReviewAttributeList.get(rating).get("selfRating"));
            if (stringSelfRating != null) {
                int integerSelfRating = Integer.parseInt(stringSelfRating);
                resultSelfRating = resultSelfRating + integerSelfRating;
                results = resultSelfRating * getDoubleWeightage;
            }
            rating++;
        }
        selfRating.setValue(results.toString());
    }

    @SuppressWarnings("unused")
    protected static void perfRatingListBox(Event event, List<GenericValue> perfRatingList) throws GenericEntityException {
        Component comp = event.getTarget();
        Listbox listBox = (Listbox) event.getTarget();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String selectedListitemLabel = (String) listBox.getSelectedItem().getLabel();
        String selectedListitemValue = (String) listBox.getSelectedItem().getValue();
        EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("description", selectedListitemLabel, "rating", selectedListitemValue));
        List<GenericValue> perfRatingList1 = delegator.findList("PerfRating", entityCondition, null, null, null, false);

    }

    private void populateGrid(Component root) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        EntityCondition Condition = EntityCondition.makeCondition("partyId", partyId);
        List<GenericValue> Types = delegator.findList("EmplPerfReview", Condition, null, null, null, false);
        SimpleListModel statusIDList = new SimpleListModel(Types);
        Grid gridPerformanceReview = (Grid) root.getFellow("dataGrid");
        gridPerformanceReview.setModel(statusIDList);

    }

    private void populateDataGridEmployeePerformanceForClosure(Component root) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        org.ofbiz.security.Security security = (Security) requestScope.get("security");
        boolean isAdmin = security.hasPermission("HUMANRES_ADMIN", userLogin);
        if (isAdmin == true) {
            EntityCondition condition = EntityCondition.makeCondition("statusType", "PERF_REVIEW_AGREED");
            List<GenericValue> types = delegator.findList("EmplPerfReview", condition, null, null, null, false);
            SimpleListModel simpleListDataGridEmployeePerformanceForClosure = new SimpleListModel(types);
            Grid dataGridEmployeePerformanceForClosure = (Grid) root.getFellow("dataGridEmployeePerformanceForClosure");
            dataGridEmployeePerformanceForClosure.setModel(simpleListDataGridEmployeePerformanceForClosure);

        }
    }

    @SuppressWarnings("deprecation")
    private void populateGrid2(Component root) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        EntityCondition condition1 = EntityCondition.makeCondition("reviewerId", partyId);
        EntityCondition condition2 = EntityCondition.makeCondition("statusType", "PERF_REVIEW_PENDING");
        EntityCondition condition3 = EntityCondition.makeCondition("statusType", "PERF_REVIEW_DISAGREE");
        EntityCondition emplPerfReviewersCondition = EntityCondition.makeCondition(condition2, EntityOperator.OR, condition3);
        EntityCondition condition = EntityCondition.makeCondition(condition1, EntityOperator.AND, emplPerfReviewersCondition);
        List<GenericValue> emplPerfReviewersList = delegator.findList("EmplPerfReviewers", condition, null, null, null, false);
        List<String> perfId = new ArrayList<String>();
        for (GenericValue gv : emplPerfReviewersList) {
            String statusType = (String) gv.get("statusType");
            if (!"PERF_REVIEWED_BY_MGR".equals(statusType)) {
                perfId.add(gv.getString("emplPerfReviewId"));
            }
        }
        EntityCondition emplPerfReviewCondition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.IN, perfId);
        List<GenericValue> emplPerfReviewList = delegator.findList("EmplPerfReview", emplPerfReviewCondition, null, null, null, false);
        SimpleListModel statusIDList = new SimpleListModel(emplPerfReviewList);
        Grid gridPerformanceReview = (Grid) root.getFellow("dataGrid2");
        gridPerformanceReview.setModel(statusIDList);

    }

    @SuppressWarnings("deprecation")
    private void listboxEmployeePositionType(Component root) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        List<GenericValue> employeePositionTypes = delegator.findList("EmplPositionType", null, null, null, null, false);
        Listbox lstBox = (Listbox) root.getFellow("listboxEmployeePositionType");
        Listitem lstItem = new Listitem();
        lstItem.setLabel("All");
        lstItem.setValue(null);
        lstBox.appendItem("All", null);
        for (int i = 0; i < employeePositionTypes.size(); i++) {
            GenericValue emplPos = employeePositionTypes.get(i);
            String itemLabel = emplPos.getString("emplPositionTypeId");
            lstBox.appendItemApi(emplPos.getString("description"), itemLabel);
            lstBox.setSelectedIndex(0);
        }

    }

    private void listboxEmployeeSectionName(Component root) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        List<GenericValue> Type = delegator.findList("PerfReviewSection", null, null, null, null, false);
        Type.add(0, null);
        SimpleListModel Name = new SimpleListModel(Type);
        Listbox listBoxName = (Listbox) root.getFellow("listboxEmployeeSectionName");
        listBoxName.setModel(Name);
        listBoxName.setItemRenderer(new DropDownGenericValueAdapter("sectionName"));
    }

    public void onClick$performancePlanCreatButton(Event event) throws GenericEntityException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        Component comp = event.getTarget();
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

        String textboxPerformancePlanName = ((Textbox) comp.getFellow("textboxPerformancePlanName")).getValue();

        Listitem employeePositionType = ((Listbox) comp.getFellow("listboxEmployeePositionType")).getSelectedItem();
        String employeePositionTypeValue = (String) employeePositionType.getValue();
        Map<String, Object> createPerfReviewTemplate = UtilMisc.toMap("userLogin", userLogin, "perfTemplateId", textboxPerformancePlanName,
                "emplPositionTypeId", employeePositionTypeValue);

        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
        try {
            dispatcher.runSync("createPerfReviewTemplate", createPerfReviewTemplate);
        } catch (GenericServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void onClick$btnAddPerformancePlan(Event event) throws GenericEntityException {

        try {
            String listboxEmployeePositionSectionId = null;
            java.sql.Timestamp fromDate = null;
            java.sql.Timestamp thruDate = null;

            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
            Component comp = event.getTarget();
            GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();

            String textboxPerformancePlanName = ((Textbox) comp.getFellow("textboxPerformancePlanName")).getValue();

            Listitem listboxEmployeeSectionName = ((Listbox) comp.getFellow("listboxEmployeeSectionName")).getSelectedItem();
            if (listboxEmployeeSectionName != null) {
                GenericValue genericValueEmployeePositionType = (GenericValue) listboxEmployeeSectionName.getValue();
                listboxEmployeePositionSectionId = genericValueEmployeePositionType.getString("perfReviewSectionId");
            }
            Integer textboxWeightage = ((Intbox) comp.getFellow("textboxWeightage")).getValue();

            Date fDate = (Date) ((Datebox) comp.getFellow("dateboxFromDate")).getValue();
            if (fDate != null) {
                fromDate = new java.sql.Timestamp(fDate.getTime());
            }

            Date tDate = (Date) ((Datebox) comp.getFellow("dateboxThruDate")).getValue();

            if (tDate != null) {
                thruDate = new java.sql.Timestamp(tDate.getTime());
            }

            String textboxTechnicalSectionName = ((Textbox) comp.getFellow("textboxTechnicalSectionName")).getValue();

            Map<String, Object> createPerfTemplateSection = UtilMisc.toMap("userLogin", userLogin, "perfTemplateId", textboxPerformancePlanName, "weightage",
                    textboxWeightage.toString(), "fromDate", fromDate, "thruDate", thruDate, "comments", textboxTechnicalSectionName, "perfReviewSectionId",
                    listboxEmployeePositionSectionId);
            LocalDispatcher dispatcher1 = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
            dispatcher1.runSync("createPerfTemplateSection", createPerfTemplateSection);
            Messagebox.show("Added successfully", "Success", 1, null);
            Intbox textboxWeightageClear = (Intbox) comp.getFellow("textboxWeightage");
            Datebox dateboxFromDateClear = (Datebox) comp.getFellow("dateboxFromDate");
            Datebox dateboxThruDateClear = (Datebox) comp.getFellow("dateboxThruDate");
            Textbox textboxTechnicalSectionNameClear = (Textbox) comp.getFellow("textboxTechnicalSectionName");
            textboxWeightageClear.setValue(null);
            dateboxFromDateClear.setValue(null);
            dateboxThruDateClear.setValue(null);
            textboxTechnicalSectionNameClear.setValue(null);
            ((Listbox) comp.getFellow("listboxEmployeeSectionName")).setSelectedIndex(0);

        } catch (Exception e) {

            try {
                Messagebox.show("Added unsuccessfully", "Error", 1, null);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

    }

    public void delete(final Event event, final String perfTemplateId,final String perfReviewSectionId) throws GenericServiceException, InterruptedException {
        Messagebox.show("Do you want to delete this record?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION, new EventListener() {
            public void onEvent(Event evt) {
                if ("onYes".equals(evt.getName())) {
                    try {
                        Component com = event.getTarget();
                        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
                        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
                        LocalDispatcher dispatcher = HrmsInfrastructure.getDispatcher();//GenericDispatcher.getLocalDispatcher("default", delegator);
                        EntityCondition perfReviewSectionIdCondition = EntityCondition.makeCondition("perfTemplateId", perfTemplateId);
                        List<GenericValue> perfReviewAssocTemplates = delegator.findList("PerfReviewAssocTemplates", perfReviewSectionIdCondition, null, null, null, false);
                        if (UtilValidate.isNotEmpty(perfReviewAssocTemplates)) {
                            Messagebox.show("Selected Performance Plan And Position is in use;can't be deleted", "Error", 1, null);
                            return;
                        }
                        EntityCondition perfReviewSectionCondition = EntityCondition.makeCondition("perfReviewSectionId",perfReviewSectionId);
                        EntityCondition mainCondition = EntityCondition.makeCondition(perfReviewSectionIdCondition,perfReviewSectionCondition);
                        List<GenericValue> getPerfReviewSectionId = delegator.findList("PerfTemplateSection", mainCondition, null, null, null,
                                false);
                        for (GenericValue gv : getPerfReviewSectionId) {
                            String perfReviewSectionId = (String) gv.get("perfReviewSectionId");
                            Map<String, Object> context2 = UtilMisc.toMap("userLogin", userLogin, "perfTemplateId", perfTemplateId, "perfReviewSectionId",
                                    perfReviewSectionId);
                            dispatcher.runSync("deletePerfTemplateSection", context2);
                        }

                        Map<String, Object> context = UtilMisc.toMap("userLogin", userLogin, "perfTemplateId", perfTemplateId);
                        dispatcher.runSync("deletePerfReviewTemplate", context);

                        Messagebox.show("Deleted successfully", "Success", 1, null);
                        Events.postEvent("onClick$searchButton", com.getPage().getFellow("performanceReviewMain").getFellow("searchPanel"), null);
                    } catch (Exception e) {

                        try {
                            Messagebox.show("Deleted unsuccessfully", "Error", 1, null);
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    public void selectPerformancePlans(Component windowsRoot) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();
        List<GenericValue> emplPositionType = delegator.findList("EmplPositionType", null, null, null, null, false);
        Combobox emplPositionCombobox = (Combobox) windowsRoot.getFellow("emplPositionCombobox");
        for(GenericValue gv : emplPositionType){
        	Comboitem ci = new Comboitem();
        	ci.setLabel(gv.getString("description"));
        	ci.setValue(gv);
        	ci.setParent(emplPositionCombobox);
        }
        
    }

    public void emplPositionCombobox(Event event) throws GenericEntityException {
        Component comp = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String emlPosition = ((Combobox) comp.getFellow("emplPositionCombobox")).getValue();
        if (UtilValidate.isEmpty(emlPosition)) {
            EntityCondition Condition = EntityCondition.makeCondition(UtilMisc.toMap("emplPositionTypeId", null));
            List<GenericValue> perfReviewTemplate = delegator.findList("PerfReviewTemplate", Condition, null, null, null, false);
            SimpleListModel simpleListModel = new SimpleListModel(perfReviewTemplate);
            Combobox templatesCombobox = (Combobox) comp.getFellow("templatesCombobox");
            templatesCombobox.setModel(simpleListModel);
            templatesCombobox.setItemRenderer(new DropDownGenericValueAdapter("perfTemplateId"));
        } else {
            EntityCondition Condition = EntityCondition.makeCondition(UtilMisc.toMap("description", emlPosition));
            List<GenericValue> emplPositionType = delegator.findList("EmplPositionType", Condition, null, null, null, false);
            String emplPositionTypeId = (String) emplPositionType.get(0).get("emplPositionTypeId");
            EntityCondition Condition1 = EntityCondition.makeCondition(UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId));
            List<GenericValue> perfReviewTemplate = delegator.findList("PerfReviewTemplate", Condition1, null, null, null, false);
            SimpleListModel simpleList = new SimpleListModel(perfReviewTemplate);
            Combobox templatesCombobox = (Combobox) comp.getFellow("templatesCombobox");
            templatesCombobox.setModel(simpleList);
            templatesCombobox.setItemRenderer(new DropDownGenericValueAdapter("perfTemplateId"));
        }
    }

    private Map<String, String> templates = new HashMap<String, String>();
    private Set<Entry<String, String>> templates1 = null;

    public Map<String, String> getTemplates() {
        return templates;
    }

    public Set<Entry<String, String>> getTemplates1() {
        return templates1;
    }

    public void onClick$buttonAddPerformancePlan(Event event) throws GenericEntityException, InterruptedException {
        Component comp = event.getTarget();
        String emplPositionCombobox = (String) ((Combobox) comp.getFellow("emplPositionCombobox")).getValue();
        if (StringUtils.isBlank(emplPositionCombobox))
            emplPositionCombobox = "All";

        boolean check = PerfReviewUtil.perfReviewCheckEmployeePositionAll(((Datebox) comp.getFellow("periodStartDate")).getValue(),
                ((Datebox) comp.getFellow("periodThruDate")).getValue(), emplPositionCombobox, (Label) comp.getFellow("labelMessageTimePeriod"));
        if (check) {
            ((Button) comp.getFellow("buttonSave")).setDisabled(true);
            ((Button) comp.getFellow("buttonAddPerformancePlan")).setDisabled(true);
            return;
        }

        if (templates.get("All") != null) {
            Messagebox.show("Template already added for all position type. Clear and add", "Error", 1, null);
            return;
        }
        String bandBoxTemplates = (String) ((Combobox) comp.getFellow("templatesCombobox")).getValue();

        Listbox listboxAddPosition_template = (Listbox) comp.getFellow("listboxAddPosition_template");
        listboxAddPosition_template.setVisible(true);
        templates.put(emplPositionCombobox, bandBoxTemplates);
        templates1 = templates.entrySet();
        Events.postEvent("onClick", comp.getFellow("loadAfter"), null);
    }

    public void onClick$buttonSave(Event event) throws GenericEntityException {

        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        Component comp = event.getTarget();
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String partyId = userLogin.getString("partyId");
        java.sql.Timestamp periodStartDateTime = null;
        java.sql.Timestamp periodThruDateTime = null;
        java.sql.Timestamp fromDate = null;
        java.sql.Timestamp thruDate = null;

        Date periodStartDate = (Date) ((Datebox) comp.getFellow("periodStartDate")).getValue();
        if (periodStartDate != null) {
            periodStartDateTime = new java.sql.Timestamp(periodStartDate.getTime());
        }
        Date dateBoxfromDate = (Date) ((Datebox) comp.getFellow("fromDate")).getValue();
        if (dateBoxfromDate != null) {
            fromDate = new java.sql.Timestamp(dateBoxfromDate.getTime());
        }
        Date dateBoxthruDate = (Date) ((Datebox) comp.getFellow("thruDate")).getValue();
        if (dateBoxthruDate != null) {
            thruDate = new java.sql.Timestamp(dateBoxthruDate.getTime());
        }

        Date periodThruDate = (Date) ((Datebox) comp.getFellow("periodThruDate")).getValue();
        if (periodThruDate != null)
            periodThruDateTime = new java.sql.Timestamp(periodThruDate.getTime());
        java.sql.Timestamp startDateTime = periodStartDateTime;

        java.sql.Timestamp endDateTime = periodThruDateTime;

        String textBoxAnnouncement = ((Textbox) comp.getFellow("textBoxAnnouncement")).getValue();

        Map<String, Object> getPerfReview = FastMap.newInstance();
        GenericValue putPerfReview = null;

        String perfReviewId = delegator.getNextSeqId("PerfReviews");

        getPerfReview.putAll(UtilMisc.toMap("perfReviewId", perfReviewId, "periodStartDate", periodStartDateTime, "periodThruDate", periodThruDateTime,
                "perfReviewStartDate", startDateTime, "perfReviewEndDate", endDateTime, "initiatedBy", partyId));

        try {
            putPerfReview = (GenericValue) delegator.create("PerfReviews", getPerfReview);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        String employeePositionDescription = templates.get("All");

        for (String key : templates.keySet()) {
            if (templates.containsKey("All")) {
                List<GenericValue> allPositions = delegator.findList("EmplPositionType", null, null, null, null, false);
                for (GenericValue position : allPositions) {
                    try {
                        delegator.create(
                                "PerfReviewAssocTemplates",
                                UtilMisc.toMap("perfReviewId", perfReviewId, "perfTemplateId", templates.get(key), "emplPositionTypeId",
                                        position.getString("emplPositionTypeId")));
                    } catch (GenericEntityException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                List<GenericValue> positionTypeList = delegator.findList("EmplPositionType", null, null, null, null, false);
                for (GenericValue positionTypeGV : positionTypeList) {
                    String employeePosition = positionTypeGV.getString("emplPositionTypeId");
                    String description = positionTypeGV.getString("description");
                    if (templates.get(description) != null) {
                        Map<String, Object> getPerfReviewAssoctemplates = FastMap.newInstance();
                        GenericValue putPerfReviewAssoctemplates = null;
                        getPerfReviewAssoctemplates.putAll(UtilMisc.toMap("perfReviewId", perfReviewId, "perfTemplateId", templates.get(description),
                                "emplPositionTypeId", employeePosition));
                        try {
                            putPerfReviewAssoctemplates = (GenericValue) delegator.create("PerfReviewAssocTemplates", getPerfReviewAssoctemplates);
                        } catch (GenericEntityException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

        }

        Map<String, Object> getAnnouncement = FastMap.newInstance();
        GenericValue putAnnouncement = null;

        getAnnouncement.putAll(UtilMisc.toMap("announcementId", delegator.getNextSeqId("Announcement"), "announcement", textBoxAnnouncement, "fromDate",
                fromDate, "thruDate", thruDate, "audience", "ALL"));

        try {
            putAnnouncement = (GenericValue) delegator.create("Announcement", getAnnouncement);

            delegator.create("AnnouncementParty",
                    UtilMisc.toMap("partyId", partyId, "role", "CREATOR", "announcementId", putAnnouncement.getString("announcementId")));
            try {
                Component enableAppraisalToolbarButton = comp.getParent().getParent().getParent().getFellow("appraisalMain")
                        .getFellow("enableAppraisalToolbarButton");
                Events.postEvent("onClick", enableAppraisalToolbarButton, null);
                Messagebox.show("Appraisal enabled successfully", "Success", 1, null);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
    }

    public static Timestamp toTimestamp(String dateTimeStr) {
        Timestamp timestamp = null;
        if (dateTimeStr != null && dateTimeStr.length() > 0) {
            if (dateTimeStr.length() == 10)
                dateTimeStr += " 00:00:00.000";
            try {
                timestamp = java.sql.Timestamp.valueOf(dateTimeStr);
            } catch (IllegalArgumentException e) {
                Debug.logWarning(e, "Bad StartDate input: " + e.getMessage(), module);
                timestamp = null;
            }
        }
        return timestamp;
    }

    private void populateAppraisal(Component comp) throws GenericEntityException {
    	GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
    	Security security = (Security) Executions.getCurrent().getAttributes().get("security");
        boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String employeeId = userLogin.getString("partyId");
        GenericValue emplPosGV = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
        String emplPositionTypeId = emplPosGV == null ? null : emplPosGV.getString("emplPositionTypeId");
        List<GenericValue> perfReviewAssocTemplates = new ArrayList<>();
        if(isManager)
        	perfReviewAssocTemplates = delegator.findList("PerfReviewAssocTemplates", null,null,null,null,false);
        else
        	perfReviewAssocTemplates = delegator.findByAnd("PerfReviewAssocTemplates", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId),null,false);
        if (UtilValidate.isNotEmpty(perfReviewAssocTemplates)) {
        	String oldPerfReviewId = new String();
            for (GenericValue gv : perfReviewAssocTemplates) {
                String perfReviewId = gv.getString("perfReviewId");
                List<GenericValue> emplPerfReview = null;
                if(!isManager)
                	emplPerfReview = delegator.findByAnd("EmplPerfReview", UtilMisc.toMap("perfReviewId", perfReviewId, "partyId", employeeId),null,false);
                if (UtilValidate.isEmpty(emplPerfReview) && !perfReviewId.equals(oldPerfReviewId)) {
                    List<GenericValue> perfReviews = delegator.findByAnd("PerfReviews", UtilMisc.toMap("perfReviewId", perfReviewId),null,false);
                    GenericValue perfReviewsGV = EntityUtil.getFirst(perfReviews);
                    Combobox selectTimePeriodCombobox = (Combobox) comp.getFellow("selectTimePeriodCombobox");
                    Comboitem cb = new Comboitem();
                    cb.setParent(selectTimePeriodCombobox);
                    cb.setLabel(PerfReviewUtil.perfReviewLabelSelectTimePeriodCombobox(perfReviewsGV.getTimestamp("periodStartDate"),
                            perfReviewsGV.getTimestamp("periodThruDate")));
                    cb.setValue(perfReviewId);
                }
                oldPerfReviewId = perfReviewId;
            }
        }
    }

    @SuppressWarnings({"deprecation", "unused"})
    public void onClick$employeeRatingForApprovalSaveButton(Event event) throws GenericEntityException, InterruptedException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        Component comp = event.getTarget();
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String partyId = userLogin.getString("partyId");
        String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewIdForApproval")).getValue();
        EntityCondition cn1 = EntityCondition.makeCondition("reviewerId", EntityOperator.EQUALS, partyId);
        EntityCondition cn2 = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        EntityCondition emplPerfReviewersCondition = EntityCondition.makeCondition(cn1, EntityOperator.AND, cn2);
        List<GenericValue> emplPerfReviewersList = delegator.findList("EmplPerfReviewers", emplPerfReviewersCondition, null, null, null, false);
        int j = 1;
        Integer results = 0;
        Integer resultSelfRating = 0;
        String stringWeightage = (String) ((Label) comp.getFellow("weightage")).getValue();
        Integer getDoubleWeightage = Integer.parseInt(stringWeightage);
        for (GenericValue emplPerfReviewersGv : emplPerfReviewersList) {
            try {
                String attributeNameLabel = (String) ((Label) comp.getFellow("attributeName" + j)).getValue();
                EntityCondition perfReviewSectionAttributeCondition = EntityCondition.makeCondition("attributeName", EntityOperator.EQUALS, attributeNameLabel);
                List<GenericValue> perfReviewSectionAttributeList = delegator.findList("PerfReviewSectionAttribute", perfReviewSectionAttributeCondition, null,
                        null, null, false);
                String attributeId_ = perfReviewSectionAttributeList.get(0).getString("attributeId");
                String comments = (String) ((Textbox) comp.getFellow("fckeditorComments" + j)).getValue();
                String perfReviewSectionId_ = perfReviewSectionAttributeList.get(0).getString("perfReviewSectionId");
                Listitem selfRating = ((Listbox) comp.getFellow("listboxSelfRating" + j)).getSelectedItem();
                String listboxselfRating = null;
                if (selfRating != null) {
                    listboxselfRating = (String) selfRating.getValue();
                }

                Map<String, Object> getSelfRating = FastMap.newInstance();
                GenericValue putSelfRating = null;
                getSelfRating.putAll(UtilMisc.toMap("reviewerComment", comments, "emplPerfReviewId", emplPerfReviewId, "reviewerId", partyId, "attributeId",
                        attributeId_, "perfReviewSectionId", perfReviewSectionId_, "rating", listboxselfRating, "statusType",
                        emplPerfReviewersGv.getString("statusType")));

                putSelfRating = (GenericValue) delegator.makeValue("EmplPerfReviewers", getSelfRating);
                try {
                    delegator.store(putSelfRating);

                    Label managerRating = (Label) comp.getFellow("managerRating");
                    int integerSelfRating = Integer.parseInt(listboxselfRating);
                    resultSelfRating = resultSelfRating + integerSelfRating;
                    results = resultSelfRating * getDoubleWeightage;
                    managerRating.setValue(results.toString());
                } catch (Exception e) {
                }
                j++;
            } catch (Exception e) {
                break;
            }
        }
        org.zkoss.zul.Messagebox.show("Saved successfully", "Success", 1, null);

    }

    @SuppressWarnings("deprecation")
    public void onClick$selfRatingSaveButton(Event event) throws GenericEntityException, InterruptedException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        Component comp = event.getTarget();
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String partyId = userLogin.getString("partyId");
        String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewId")).getValue();
        EntityCondition emplPerfReviewAttributeCondition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        List<GenericValue> emplPerfReviewAttributeList = delegator.findList("EmplPerfReviewAttribute", emplPerfReviewAttributeCondition, null, null, null,
                false);
        int j = 1;
        Integer resultSelfRating = 0;
        Integer results = 0;
        String stringWeightage = (String) ((Label) comp.getFellow("weightage")).getValue();
        Integer getDoubleWeightage = Integer.parseInt(stringWeightage);
        for (GenericValue emplPerfReviewAttributeGv : emplPerfReviewAttributeList) {
            Boolean beganTransaction = false;
            try {
                beganTransaction = TransactionUtil.begin();
                String attributeNameLabel = (String) ((Label) comp.getFellow("attributeName" + j)).getValue();
                EntityCondition perfReviewSectionAttributeCondition = EntityCondition.makeCondition("attributeName", EntityOperator.EQUALS, attributeNameLabel);
                List<GenericValue> perfReviewSectionAttributeList = delegator.findList("PerfReviewSectionAttribute", perfReviewSectionAttributeCondition, null,
                        null, null, false);
                String attributeId_ = perfReviewSectionAttributeList.get(0).getString("attributeId");
                String comments = (String) ((Textbox) comp.getFellow("fckeditorComments" + j)).getValue();
                String perfReviewSectionId_ = perfReviewSectionAttributeList.get(0).getString("perfReviewSectionId");
                Listitem selfRating = ((Listbox) comp.getFellow("listboxSelfRating" + j)).getSelectedItem();
                String listboxselfRating = null;
                if (selfRating != null) {
                    listboxselfRating = (String) selfRating.getValue();
                }

                Map<String, Object> getSelfRating = FastMap.newInstance();
                GenericValue putSelfRating = null;
                getSelfRating.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "attributeId", attributeId_, "perfReviewSectionId",
                        perfReviewSectionId_, "selfRating", listboxselfRating, "selfComment", comments));

                putSelfRating = (GenericValue) delegator.makeValue("EmplPerfReviewAttribute", getSelfRating);
                try {
                    delegator.store(putSelfRating);

                    Label selfRatingLabel = (Label) comp.getFellow("selfRating");
                    int integerSelfRating = Integer.parseInt(listboxselfRating);
                    resultSelfRating = resultSelfRating + integerSelfRating;
                    results = resultSelfRating * getDoubleWeightage;
                    selfRatingLabel.setValue(results.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                j++;
                if (beganTransaction)
                    TransactionUtil.commit();
            } catch (Exception e) {
                if (beganTransaction)
                    try {
                        TransactionUtil.rollback();
                    } catch (GenericTransactionException e2) {
                        e2.printStackTrace();
                    }
                break;
            }
        }
        org.zkoss.zul.Messagebox.show("Saved successfully", "Success", 1, null);

    }

    public void selfRatingSubmitButton(final Event event) throws GenericEntityException, InterruptedException {
        Messagebox.show("Appraisal submitted cannot be modified. Do you want to continue?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(Event evt) {
                        if ("onYes".equals(evt.getName())) {
                            try {
                                GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
                                Component comp = event.getTarget();
                                GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
                                String partyId = userLogin.getString("partyId");
                                String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewId")).getValue();

                                EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId));
                                List<GenericValue> emplPerfReviewPending = delegator.findList("EmplPerfReviewers", entityCondition, null, null, null, false);
                                for (GenericValue gv : emplPerfReviewPending) {
                                    String reviewerId = (String) (gv.getString("reviewerId"));
                                    String perfReviewSectionId = (String) (gv.getString("perfReviewSectionId"));
                                    String attributeId = (String) (gv.getString("attributeId"));
                                    Map<String, Object> getSubmitValue = FastMap.newInstance();
                                    GenericValue putSubmitValue = null;
                                    getSubmitValue.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "reviewerId", reviewerId, "perfReviewSectionId",
                                            perfReviewSectionId, "attributeId", attributeId, "statusType", "PERF_REVIEW_PENDING"));
                                    putSubmitValue = (GenericValue) delegator.makeValue("EmplPerfReviewers", getSubmitValue);
                                    delegator.store(putSubmitValue);

                                }

                                Map<String, Object> getSubmitValue = FastMap.newInstance();
                                GenericValue putSubmitValue = null;
                                getSubmitValue.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "statusType", "PERF_REVIEW_PENDING"));
                                putSubmitValue = (GenericValue) delegator.makeValue("EmplPerfReview", getSubmitValue);
                                delegator.store(putSubmitValue);
                                HrmsMessageBox.showOk("Submitted successfully", "Success", "/control/EditAppraisalView?reviewId=" + emplPerfReviewId);
                            } catch (Exception e) {
                                try {
                                    org.zkoss.zul.Messagebox.show("Submitted unsuccessfully", "Error", 1, null);
                                } catch (InterruptedException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                });

    }

    public void employeeRatingForApprovalSubmitButton(final Event event) throws GenericEntityException, InterruptedException {
        Messagebox.show("Appraisal submitted cannot be modified. Do you want to continue?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                new org.zkoss.zk.ui.event.EventListener() {
                    public void onEvent(Event evt) {
                        if ("onYes".equals(evt.getName())) {
                            try {
                                GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
                                Component comp = event.getTarget();
                                GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
                                String partyId = userLogin.getString("partyId");
                                String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewIdForApproval")).getValue();

                                EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId));

                                List<GenericValue> emplPerfReviewPending = delegator.findList("EmplPerfReviewers", entityCondition, null, null, null, false);
                                for (GenericValue gv : emplPerfReviewPending) {
                                    String perfReviewSectionId = (String) (gv.getString("perfReviewSectionId"));
                                    String attributeId = (String) (gv.getString("attributeId"));
                                    Map<String, Object> getSubmitValue = FastMap.newInstance();
                                    GenericValue putSubmitValue = null;
                                    getSubmitValue.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "reviewerId", partyId, "perfReviewSectionId",
                                            perfReviewSectionId, "attributeId", attributeId, "statusType", "PERF_REVIEWED_BY_MGR"));
                                    putSubmitValue = (GenericValue) delegator.makeValue("EmplPerfReviewers", getSubmitValue);
                                    delegator.store(putSubmitValue);

                                }
                                EntityCondition checkPerfReviewPending = EntityCondition.makeCondition(UtilMisc.toMap("statusType", "PERF_REVIEW_PENDING"));
                                EntityCondition checkPerfReviewDisAgree = EntityCondition.makeCondition(UtilMisc.toMap("statusType", "PERF_REVIEW_DISAGREE"));
                                EntityCondition condition = EntityCondition.makeCondition(checkPerfReviewDisAgree, EntityOperator.OR, checkPerfReviewPending);
                                EntityCondition makeTwoCondition = EntityCondition.makeCondition(condition, EntityOperator.AND, entityCondition);
                                List<GenericValue> emplPerfReviewersList = delegator.findList("EmplPerfReviewers", makeTwoCondition, null, null, null, false);
                                if ((UtilValidate.isEmpty(emplPerfReviewersList))) {
                                    Map<String, Object> getSubmitValue = FastMap.newInstance();
                                    GenericValue putSubmitValue = null;
                                    GenericValue empPerfReview = EntityUtil.getFirst(delegator.findByAnd("EmplPerfReview", UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId),null,false));
                                    if(empPerfReview != null && partyId.equals(empPerfReview.getString("createdPartyId"))){
                                    	getSubmitValue.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "statusType", "PERF_REVIEW_AGREED"));
                                    }else{
                                    	getSubmitValue.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "statusType", "PERF_REVIEWED_BY_MGR"));
                                    }
                                    putSubmitValue = (GenericValue) delegator.makeValue("EmplPerfReview", getSubmitValue);
                                    delegator.store(putSubmitValue);
                                } else {
                                    Map<String, Object> getSubmitValue = FastMap.newInstance();
                                    GenericValue putSubmitValue = null;
                                    getSubmitValue.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "statusType", "PERF_REVIEW_PENDING"));
                                    putSubmitValue = (GenericValue) delegator.makeValue("EmplPerfReview", getSubmitValue);
                                    delegator.store(putSubmitValue);
                                }

                                //org.zkoss.zul.Messagebox.show("Submitted successfully", "Success", 1, null);
                                // Executions.sendRedirect("/control/main");
                                //Executions.sendRedirect("/control/AppraisalView");
                                HrmsMessageBox.showOk("Submitted successfully", "Success", "/control/AppraisalView");
                            } catch (Exception e) {
                                try {
                                    org.zkoss.zul.Messagebox.show("Submitted unsuccessfully", "Error", 1, null);
                                } catch (InterruptedException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }
                });

    }
    
    private void createEmployeeAppraisal(GenericDelegator delegator,String employeeId,String perfReviewId,List<String> reviewerIds,String createById) throws GenericEntityException, InterruptedException{
    	String emplPerfReviewId = delegator.getNextSeqId("EmplPerfReview");
    	 Map<String, Object> getValueEmplPerfReview = FastMap.newInstance();
         getValueEmplPerfReview.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "partyId", employeeId, "perfReviewId", perfReviewId,
                 "statusType", "PERF_IN_PROGRESS","createdPartyId",createById));
         GenericValue putValueEmplPerfReview = (GenericValue) delegator.makeValue("EmplPerfReview", getValueEmplPerfReview);
         delegator.create(putValueEmplPerfReview);

         EntityCondition perfRevAssocTemplatesCondition = EntityCondition.makeCondition("perfReviewId", EntityOperator.EQUALS, perfReviewId);
         List<GenericValue> perfRevAssocTemplates = delegator.findList("PerfReviewAssocTemplates", perfRevAssocTemplatesCondition, null, null, null, false);
         String perfTemplateId = (String) perfRevAssocTemplates.get(0).get("perfTemplateId");
         EntityCondition perfTemplateIdCondition = EntityCondition.makeCondition("perfTemplateId", EntityOperator.EQUALS, perfTemplateId);
         List<GenericValue> perfTemplateSection = delegator.findList("PerfTemplateSection", perfTemplateIdCondition, null, null, null, false);

         for (GenericValue gvperfTemplateSection : perfTemplateSection) {
             String perfReviewSectionId = (String) gvperfTemplateSection.getString("perfReviewSectionId");
             EntityCondition perfReviewSectionIdCondition = EntityCondition.makeCondition("perfReviewSectionId", EntityOperator.EQUALS, perfReviewSectionId);
             List<GenericValue> perfReviewSectionAttribute = delegator.findList("PerfReviewSectionAttribute", perfReviewSectionIdCondition, null, null,
                     null, false);
             for (GenericValue gvperfReviewSectionAttribute : perfReviewSectionAttribute) {

                 String attributeId = (String) gvperfReviewSectionAttribute.getString("attributeId");
                 String perfReviewSecId = (String) gvperfReviewSectionAttribute.getString("perfReviewSectionId");
                 Map<String, Object> getValuePerfRevSectionAttAndPerfTemSection = FastMap.newInstance();
                 GenericValue putEmplPerfReviewAttribute = null;
                 getValuePerfRevSectionAttAndPerfTemSection.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "perfTemplateId", perfTemplateId,
                         "attributeId", attributeId, "perfReviewSectionId", perfReviewSecId));
                 putEmplPerfReviewAttribute = delegator.makeValue("EmplPerfReviewAttribute", getValuePerfRevSectionAttAndPerfTemSection);
                 delegator.create(putEmplPerfReviewAttribute);

                 Map<String, Object> getValueForEmplPerfReviewers = FastMap.newInstance();
                 GenericValue putValueForEmplPerfReviewers = null;
                 for (int i = 0; i < reviewerIds.size(); i++) {
                     getValueForEmplPerfReviewers.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "attributeId", attributeId,
                             "perfReviewSectionId", perfReviewSecId, "reviewerId", reviewerIds.get(i)));
                     putValueForEmplPerfReviewers = delegator.makeValue("EmplPerfReviewers", getValueForEmplPerfReviewers);
                     delegator.create(putValueForEmplPerfReviewers);

                 }
             }
         }
    }

    public void onClick$toolbarbuttonSelfApraisal(Event event) throws GenericEntityException, InterruptedException {
        Component comp = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String employeeId = (String) userLogin.get("partyId");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String perfReviewId = (String) ((Combobox) comp.getFellow("selectTimePeriodCombobox")).getSelectedItem().getValue();
        List<String> reviewerIds = PerfReviewUtil.getAllReportingStructure(employeeId, perfReviewId);
        
        GenericValue emplPosGV = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
        String emplPositionTypeId = emplPosGV == null ? null : emplPosGV.getString("emplPositionTypeId");
        List<GenericValue> perfReviewAssocTemplates = delegator.findByAnd("PerfReviewAssocTemplates", UtilMisc.toMap("perfReviewId",perfReviewId,"emplPositionTypeId", emplPositionTypeId),null,false);
        if(UtilValidate.isEmpty(perfReviewAssocTemplates)){
        	Messagebox.show("Appraisal is not enable for this Employee Position", "Error", 1, null);
        	return;
        }

    	Security security = (Security) Executions.getCurrent().getAttributes().get("security");
        boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);
        String managerPositionId = HumanResUtil.getManagerPositionIdForParty(employeeId, delegator);
        if(UtilValidate.isEmpty(managerPositionId) && isManager && UtilValidate.isEmpty(reviewerIds)){
        	reviewerIds=new ArrayList<String>();
        	reviewerIds.add(employeeId);
        }
       
        if (UtilValidate.isNotEmpty(reviewerIds)) {
        	createEmployeeAppraisal(delegator, employeeId, perfReviewId, reviewerIds,employeeId);
            Executions.sendRedirect("/control/AppraisalView");
        } else {
            Messagebox.show("You are not eligible to apply for appraisal", "Error", 1, null);
        }

    }
    
    public void onClick$toolbarbuttonCreateEdit(Event event) throws GenericEntityException, InterruptedException, SuspendNotAllowedException, GenericServiceException {
        Component comp = event.getTarget();
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String employeeId = (String) ((Combobox) comp.getFellow("subordinatePartyId")).getSelectedItem().getValue();
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String perfReviewId = (String) ((Combobox) comp.getFellow("selectTimePeriodCombobox")).getSelectedItem().getValue();
        List<String> reviewerIds = PerfReviewUtil.getAllReportingStructure(employeeId, perfReviewId);
        
        GenericValue emplPosGV = HumanResUtil.getEmplPositionForParty(employeeId, delegator);
        String emplPositionTypeId = emplPosGV == null ? null : emplPosGV.getString("emplPositionTypeId");
        List<GenericValue> perfReviewAssocTemplates = delegator.findByAnd("PerfReviewAssocTemplates", UtilMisc.toMap("perfReviewId",perfReviewId,"emplPositionTypeId", emplPositionTypeId),null,false);
        if(UtilValidate.isEmpty(perfReviewAssocTemplates)){
        	Messagebox.show("Appraisal is not enable for this Employee Position", "Error", 1, null);
        	return;
        }
        
        if (UtilValidate.isNotEmpty(reviewerIds)) {
        	GenericValue emplPerfReview = EntityUtil.getFirst(delegator.findByAnd("EmplPerfReview",UtilMisc.toMap("partyId",employeeId,"perfReviewId",perfReviewId),null,false));
        	if(UtilValidate.isEmpty(emplPerfReview)){
        		createEmployeeAppraisal(delegator, employeeId, perfReviewId, reviewerIds, userLogin.getString("partyId"));
        	}
        	emplPerfReview = EntityUtil.getFirst(delegator.findByAnd("EmplPerfReview",UtilMisc.toMap("partyId",employeeId,"perfReviewId",perfReviewId),null,false));
        	if("PERF_IN_PROGRESS".equals(emplPerfReview.getString("statusType")) || "PERF_REVIEW_PENDING".equals(emplPerfReview.getString("statusType")) || "PERF_REVIEW_COMPLETE".equals(emplPerfReview.getString("statusType")) )
        		myPerformanceReview(null,emplPerfReview);
        	else
        		Messagebox.show("Appraisal already submitted, Once complete you can view", "Error", 1, null);
        	
        } else {
            Messagebox.show("You are not eligible to apply for appraisal", "Error", 1, null);
        }

    }

    public void myPerformanceReview(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException, InterruptedException,
            GenericEntityException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String emplPerfReviewId = gv.getString("emplPerfReviewId");
        String emplPartyId = gv.getString("partyId");
        String[] managerIdArry = HumanResUtil.getAllReportingMangerForParty(emplPartyId, delegator);
        String managerId = null;
        GenericValue emplPerfReview = delegator.findByPrimaryKey("EmplPerfReview", UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId));
        
        managerId = UtilValidate.isNotEmpty(PerfReviewUtil.getAllReportingStructure(emplPartyId, emplPerfReview.getString("perfReviewId")))?
        		PerfReviewUtil.getAllReportingStructure(emplPartyId, emplPerfReview.getString("perfReviewId")).get(0) : null; // PerfReviewUtil.getLatestManager(emplPerfReviewId,
        // emplPartyId);
    	Security security = (Security) Executions.getCurrent().getAttributes().get("security");
        boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);
        
        if (managerId == null){
        	if(UtilValidate.isNotEmpty(managerIdArry))
        		managerId = managerIdArry[0];
        	else if(isManager)
        		managerId=emplPartyId;
        }
        String statusType = gv.getString("statusType");
        List<GenericValue> emplPerfReviewersList = delegator.findByAnd("EmplPerfReviewers",
                UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "statusType", "PERF_REVIEW_DISAGREE"));
        if (emplPerfReviewersList.size() > 0) {
            statusType = emplPerfReviewersList.get(0).getString("statusType");
        } else {
            emplPerfReviewersList = delegator.findByAnd("EmplPerfReviewers",
                    UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "statusType", "PERF_REVIEW_AGREED"));
            if (emplPerfReviewersList.size() > 0) {
                statusType = "PERF_REVIEWED_BY_MGR";
            }
        }
        if (statusType.equals("PERF_REVIEWED_BY_MGR") || statusType.equals("PERF_REVIEW_DISAGREE") || statusType.equals("PERF_REVIEW_AGREED")
                || statusType.equals("PERF_REVIEW_COMPLETE") || statusType.equals("PERF_REVIEW_DISAGREE")) {
            if (statusType.equals("PERF_REVIEW_COMPLETE")) {
                Executions.sendRedirect("/control/EmployeePerformanceForClosure?reviewIdForApproval=" + emplPerfReviewId + "&managerId=" + managerId
                        + "&emplPartyId=" + emplPartyId);
            } else if (statusType.equals("PERF_REVIEWED_BY_MGR") || statusType.equals("PERF_REVIEW_DISAGREE") || statusType.equals("PERF_REVIEW_AGREED")) {
                Executions.sendRedirect("/control/PerReviewAgreeDisagree?reviewId=" + emplPerfReviewId + "&managerId=" + managerId);
            } else {
                Executions.sendRedirect("/control/PerReviewAgreeDisagree?reviewId=" + emplPerfReviewId);
            }
        } else {
            Executions.sendRedirect("/control/EditAppraisalView?reviewId=" + emplPerfReviewId);
        }

    }

    public void employeePerformanceForApproval(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException, InterruptedException,
            GenericEntityException {
        String emplPerfReviewId = gv.getString("emplPerfReviewId");
        Executions.sendRedirect("/control/EditAppraisalViewForApproval?reviewIdForApproval=" + emplPerfReviewId);

    }

    public void employeePerformanceForCloser(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException, InterruptedException,
            GenericEntityException {

        String emplPerfReviewId = gv.getString("emplPerfReviewId");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String partyId = gv.getString("partyId");
		/*
		 * String[] managerIdArry =
		 * HumanResUtil.getAllReportingMangerForParty(partyId, delegator);
		 * String managerId = null; managerId =
		 * PerfReviewUtil.getLatestManager(emplPerfReviewId, partyId);
		 */
        String[] managerIdArry = HumanResUtil.getAllReportingMangerForParty(partyId, delegator);
        String managerId = null;
        GenericValue emplPerfReview = delegator.findByPrimaryKey("EmplPerfReview", UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId));
        managerId = UtilValidate.isNotEmpty(PerfReviewUtil.getAllReportingStructure(partyId, emplPerfReview.getString("perfReviewId")))?
        		PerfReviewUtil.getAllReportingStructure(partyId, emplPerfReview.getString("perfReviewId")).get(0) : null; // PerfReviewUtil.getLatestManager(emplPerfReviewId,
        // emplPartyId);
        Security security = (Security) Executions.getCurrent().getAttributes().get("security");
        boolean isManager = security.hasPermission("HUMANRES_MGR", userLogin);
        
        if (managerId == null){
        	if(UtilValidate.isNotEmpty(managerIdArry))
        		managerId = managerIdArry[0];
        	else
        		managerId=partyId;
        }
        String emplPartyId = gv.getString("partyId");
        Executions.sendRedirect("/control/EmployeePerformanceForClosure?reviewIdForApproval=" + emplPerfReviewId + "&managerId=" + managerId + "&emplPartyId="
                + emplPartyId);

    }

    @SuppressWarnings("deprecation")
    public void onClick$buttonPerfReviewDisagree(Event event) throws GenericEntityException, InterruptedException {
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        Component comp = event.getTarget();
        GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
        String partyId = userLogin.getString("partyId");
        String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewIdForApproval")).getValue();
        String hiddenemplmanagerId = (String) ((Label) comp.getFellow("hiddenemplmanagerId")).getValue();
        EntityCondition emplPerfReviewersCondition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        EntityCondition managerCondition = EntityCondition.makeCondition("reviewerId", EntityOperator.EQUALS, hiddenemplmanagerId);
        EntityCondition mainCondition = EntityCondition.makeCondition(emplPerfReviewersCondition, managerCondition);
        List<GenericValue> emplPerfReviewersList = delegator.findList("EmplPerfReviewers", mainCondition, null, null, null, false);
        for (GenericValue emplPerfReviewersGV : emplPerfReviewersList) {
            String reviewerId = emplPerfReviewersGV.getString("reviewerId");
            String perfReviewSectionId = emplPerfReviewersGV.getString("perfReviewSectionId");
            String attributeId = emplPerfReviewersGV.getString("attributeId");
            Map<String, Object> getDisagree = FastMap.newInstance();
            GenericValue putDisagree = null;
            getDisagree.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "reviewerId", reviewerId, "perfReviewSectionId", perfReviewSectionId,
                    "attributeId", attributeId, "statusType", "PERF_REVIEW_DISAGREE"));// PERF_REVIEW_PENDING
            putDisagree = (GenericValue) delegator.makeValue("EmplPerfReviewers", getDisagree);
            delegator.store(putDisagree);
        }
        String textboxDisagreeComments = (String) ((Textbox) comp.getFellow("textboxDisagreeComments")).getValue();
        if (UtilValidate.isEmpty(textboxDisagreeComments)) {
            org.zkoss.zul.Messagebox.show("Please provide reasons to disagree", "Error", 1, null);
            return;
        } else {
            List<GenericValue> empDisAgreeList = delegator.findByAnd("EmplPerfDisAgreedComments", UtilMisc.toMap("reviewerId", hiddenemplmanagerId, "emplPerfReviewId", emplPerfReviewId));
            String empDesAgreeId = null;
            if (UtilValidate.isEmpty(empDisAgreeList))
                empDesAgreeId = delegator.getNextSeqId("EmplPerfDisAgreedComments");
            else
                empDesAgreeId = empDisAgreeList.get(0).getString("commentId");
            GenericValue empDisComGv = delegator.makeValue("EmplPerfDisAgreedComments", UtilMisc.toMap("commentId", empDesAgreeId, "reviewerId", hiddenemplmanagerId, "emplPerfReviewId", emplPerfReviewId, "comments", textboxDisagreeComments));
            delegator.createOrStore(empDisComGv);
            Map<String, Object> getValue = FastMap.newInstance();
            GenericValue putValue = null;
            getValue.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "statusType", "PERF_REVIEW_PENDING"));// PERF_REVIEW_DISAGREE
            putValue = (GenericValue) delegator.makeValue("EmplPerfReview", getValue);
            delegator.store(putValue);
        }
        //HrmsMessageBox.showOk("Performance review disagreed successfully; needs to submit", "Success", "/control/PerReviewAgreeDisagree?reviewId=" + emplPerfReviewId + "&managerId=" + hiddenemplmanagerId);
        //Executions.sendRedirect("/control/PerReviewAgreeDisagree?reviewId=" + emplPerfReviewId + "&managerId=" + hiddenemplmanagerId);
        Executions.sendRedirect("/control/main");

    }

    public void onClick$buttonPerfReviewAgree(final Event finalEvent) throws GenericEntityException, InterruptedException {
        Messagebox.show("Are you sure you want to agree Manager's review?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                new EventListener() {
                    public void onEvent(Event evt) {
                        if ("onYes".equals(evt.getName())) {
                            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
                            Component comp = finalEvent.getTarget();
                            GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
                            String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewIdForApproval")).getValue();
                            String managerId = (String) ((Label) comp.getFellow("hiddenemplmanagerId")).getValue();

                            List<GenericValue> emplPerfReviewers = null;
                            try {
                                emplPerfReviewers = delegator.findByAnd("EmplPerfReviewers",
                                        UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "reviewerId", managerId));
                            } catch (GenericEntityException e) {
                                e.printStackTrace();
                            }
                            for (GenericValue emplPerfReviewer : emplPerfReviewers) {
                                try {
                                    delegator.store(delegator.makeValue("EmplPerfReviewers", UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "reviewerId",
                                            emplPerfReviewer.getString("reviewerId"), "perfReviewSectionId", emplPerfReviewer.getString("perfReviewSectionId"),
                                            "attributeId", emplPerfReviewer.getString("attributeId"), "statusType", "PERF_REVIEW_AGREED")));
                                } catch (GenericEntityException e) {
                                    e.printStackTrace();
                                }
                            }

                            EntityCondition cn1 = EntityCondition.makeCondition("statusType", EntityOperator.NOT_EQUAL, "PERF_REVIEW_AGREED");
                            EntityCondition cn2 = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
                            EntityCondition cn = EntityCondition.makeCondition(cn1, EntityOperator.AND, cn2);
                            try {
                                List<GenericValue> emplPerfReviwerList = delegator.findList("EmplPerfReviewers", cn, null, null, null, false);
                                if (emplPerfReviwerList.size() < 1) {
                                    GenericValue emplPerfReviewGv = delegator.makeValue("EmplPerfReview", UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "statusType", "PERF_REVIEWED_BY_MGR"));
                                    delegator.store(emplPerfReviewGv);
                                }
                            } catch (GenericEntityException e) {
                            }
                            HrmsMessageBox.showOk("Performance review agreed successfully; needs to submit", "Success", "/control/PerReviewAgreeDisagree?reviewId=" + emplPerfReviewId + "&managerId=" + managerId);

                        }
                    }
                });

    }

    public void onClick$buttonFinalSubmit(final Event finalEvent) throws GenericEntityException, InterruptedException {
        Messagebox.show("Appraisal submitted cannot be modified. Do you want to continue?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                new EventListener() {
                    public void onEvent(Event evt) {
                        if ("onYes".equals(evt.getName())) {
                            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
                            Component comp = finalEvent.getTarget();
                            GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
                            String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewIdForApproval")).getValue();
                            String managerId = (String) ((Label) comp.getFellow("hiddenemplmanagerId")).getValue();

                            List<GenericValue> emplPerfReviewers = null;
                            try {
                                emplPerfReviewers = delegator.findByAnd("EmplPerfReviewers",
                                        UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "reviewerId", managerId));
                            } catch (GenericEntityException e) {
                                e.printStackTrace();
                            }
                            List<GenericValue> checkEmplPerfReviewers = null;
                            try {
                                EntityCondition cn1 = EntityCondition.makeCondition("statusType", EntityOperator.EQUALS, "PERF_REVIEWED_BY_MGR");
                                EntityCondition cn2 = EntityCondition.makeCondition("statusType", EntityOperator.EQUALS, "PERF_REVIEW_DISAGREE");
                                EntityCondition cn3 = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
                                EntityCondition condition1 = EntityCondition.makeCondition(cn1, EntityOperator.OR, cn2);
                                EntityCondition mainCondition = EntityCondition.makeCondition(condition1, EntityOperator.AND, cn3);
                                checkEmplPerfReviewers = delegator.findList("EmplPerfReviewers", mainCondition, null, null, null, false);
                            } catch (GenericEntityException e1) {
                                e1.printStackTrace();
                            }
                            if (UtilValidate.isEmpty(checkEmplPerfReviewers)) {
                                String[] managerArr = null;
                                try {
                                    managerArr = HumanResUtil.getAllReportingMangerForParty(userLogin.getString("partyId"), delegator);
                                } catch (GenericEntityException e2) {
                                    e2.printStackTrace();
                                }
                                for (String mgrId : managerArr) {
                                    try {
                                        emplPerfReviewers = delegator.findByAnd("EmplPerfReviewers",
                                                UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "reviewerId", mgrId));
                                    } catch (GenericEntityException e1) {
                                        e1.printStackTrace();
                                    }
                                    for (GenericValue emplPerfReviewer : emplPerfReviewers) {
                                        try {
                                            delegator.store(delegator.makeValue("EmplPerfReviewers", UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId,
                                                    "reviewerId", emplPerfReviewer.getString("reviewerId"), "perfReviewSectionId",
                                                    emplPerfReviewer.getString("perfReviewSectionId"), "attributeId",
                                                    emplPerfReviewer.getString("attributeId"), "statusType", "PERF_REVIEWED_BY_MGR")));
                                        } catch (GenericEntityException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                Map<String, Object> getValue = FastMap.newInstance();
                                GenericValue putValue = null;
                                getValue.putAll(UtilMisc.toMap("emplPerfReviewId", emplPerfReviewId, "statusType", "PERF_REVIEW_AGREED"));
                                putValue = (GenericValue) delegator.makeValue("EmplPerfReview", getValue);
                                try {
                                    delegator.store(putValue);
                                } catch (Exception e) {
                                    // TODO: handle exception
                                }
                                Executions.sendRedirect("/control/main");
                            } else {
                                try {
                                    Messagebox.show("Please agree all Manager reviews", "Error", 1, null);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    }
                });

    }

    public void onClick$buttonEmplPerfClosure(final Event event) throws GenericEntityException, InterruptedException {
        Messagebox.show("Appraisal closed cannot be modified. Do you want to continue?", "Warning", Messagebox.YES | Messagebox.NO, Messagebox.QUESTION,
                new EventListener() {
                    public void onEvent(Event evt) {
                        if ("onYes".equals(evt.getName())) {
                            GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
                            Component comp = event.getTarget();
                            GenericDelegator delegator = (GenericDelegator) userLogin.getDelegator();
                            String hiddenemplPerfReviewIdForApproval = (String) ((Label) comp.getFellow("hiddenemplPerfReviewIdForApproval")).getValue();

                            Integer textboxOverallRating = ((Intbox) comp.getFellow("textboxOverallRating")).getValue();
                            String textboxComments = ((Textbox) comp.getFellow("textboxComments")).getValue();
                            String textboxFeedBack = ((Textbox) comp.getFellow("textboxFeedBack")).getValue();

                            Map<String, Object> getValue = FastMap.newInstance();
                            GenericValue putValue = null;
                            getValue.putAll(UtilMisc.toMap("emplPerfReviewId", hiddenemplPerfReviewIdForApproval, "overallRating", textboxOverallRating.toString(), "comments",
                                    textboxComments, "feedback", textboxFeedBack, "statusType", "PERF_REVIEW_COMPLETE"));
                            putValue = (GenericValue) delegator.makeValue("EmplPerfReview", getValue);
                            try {
                                delegator.store(putValue);
                            } catch (Exception e) {
                                // TODO: handle exception
                            }
                            Executions.sendRedirect("/control/AppraisalView");
                        }
                    }
                });

    }

}
