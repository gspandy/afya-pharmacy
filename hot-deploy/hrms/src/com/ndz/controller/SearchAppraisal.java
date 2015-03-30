package com.ndz.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.axis.types.Entity;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.humanresext.util.HumanResUtil;
import org.ofbiz.service.GenericServiceException;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;

import com.ndz.zkoss.HrmsUtil;
import com.ndz.zkoss.util.HrmsInfrastructure;

public class SearchAppraisal extends GenericForwardComposer {

    private static final long serialVersionUID = 1L;

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        if ("employeePerformanceReport".equals(comp.getId())) {
            employeePerformanceReport(comp);
        }
    }

    @SuppressWarnings("deprecation")
    public void searchAppraisalButton(Event event, boolean isAdmin, boolean isManager) throws GenericEntityException, ParseException {
        Component comp = event.getTarget();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        java.sql.Date fromDate = null;
        java.sql.Date thruDate = null;

        Date fromDateInput = (Date) ((Datebox) comp.getFellow("fromDate")).getValue();
        Date thruDateInput = (Date) ((Datebox) comp.getFellow("thruDate")).getValue();
        String partyIdComp = (Textbox) comp.getFellowIfAny("partyId") == null ? null : ((Textbox) comp.getFellowIfAny("partyId")).getValue();

        if (fromDateInput != null) {
            fromDate = new java.sql.Date(fromDateInput.getTime());
        }
        if (thruDateInput != null) {
            thruDate = new java.sql.Date(thruDateInput.getTime());
        }
        List<GenericValue> emplperfReviewsList = null;
        EntityCondition fromDateCondition = null;
        EntityCondition thruDateCondition = null;
        EntityCondition makeCondition = null;
        EntityCondition mainCondition = null;

        if (UtilValidate.isNotEmpty(partyIdComp)) {
            mainCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyIdComp);
        } else if (!isAdmin) {
            mainCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
        }
        if (!isAdmin && !isManager)
            mainCondition = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);

        if (fromDate != null || thruDate != null) {
            if (fromDate != null) {
                fromDateCondition = EntityCondition.makeCondition("periodStartDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(fromDate.getTime()));
            }
            if (thruDate != null) {
                thruDateCondition = EntityCondition.makeCondition("periodThruDate", EntityOperator.LESS_THAN_EQUAL_TO, new Timestamp(thruDate.getTime()));
            }
            EntityCondition cn1 = EntityCondition.makeCondition(fromDateCondition, EntityOperator.AND, thruDateCondition);
            if (mainCondition != null)
                mainCondition = EntityCondition.makeCondition(cn1, EntityOperator.AND, mainCondition);
            else
                mainCondition = EntityCondition.makeCondition(cn1);

        }
        EntityCondition cn2 = EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "PERF_REVIEW_COMPLETE");
        if (mainCondition != null)
            makeCondition = EntityCondition.makeCondition(cn2, EntityOperator.AND, mainCondition);
        else
            makeCondition = EntityCondition.makeCondition(cn2);
        emplperfReviewsList = delegator.findList("SearchEmplPerfReviewerView", makeCondition, null, null, null, false);
        SimpleListModel statusIDList = new SimpleListModel(emplperfReviewsList);
        Grid dataGrid = (Grid) comp.getFellow("dataGrid");
        dataGrid.setModel(statusIDList);
    }

    public void employeeReports(Event event, GenericValue gv) throws GenericServiceException, SuspendNotAllowedException,
            InterruptedException, GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        List<GenericValue> emplPerfReviewers = delegator.findByAnd("EmplPerfReviewers", UtilMisc.toMap("emplPerfReviewId", gv.getString("emplPerfReviewId")));
        Executions.sendRedirect("/control/appraisalReports?reviewIdForApproval=" + gv.getString("emplPerfReviewId") + "&managerId=" + emplPerfReviewers.get(0).getString("reviewerId") + "&emplPartyId=" + gv.getString("partyId"));
    }

    @SuppressWarnings("deprecation")
    private void employeePerformanceReport(Component root) throws GenericEntityException {
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        String emplPerfReviewId = (String) Executions.getCurrent().getParameter("reviewIdForApproval");
        EntityCondition emplPerfReviewAttribute_condition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        List<GenericValue> emplPerfReviewAttribute_list = delegator.findList("EmplPerfReviewAttribute", emplPerfReviewAttribute_condition, null,
                null, null, false);
        EntityCondition emplPerfReview_Condition = EntityCondition.makeCondition("emplPerfReviewId", EntityOperator.EQUALS, emplPerfReviewId);
        List<GenericValue> emplPerfReview_List = delegator.findList("EmplPerfReview", emplPerfReview_Condition, null, null, null, false);
        String statusType = (String) emplPerfReview_List.get(0).get("statusType");
        Label status = (Label) root.getFellow("status");
        status.setValue(HrmsUtil.getStatusItemDescription(statusType));

        String perfTemplateId = (String) emplPerfReviewAttribute_list.get(0).get("perfTemplateId");
        EntityCondition perfTemplateSection_condition = EntityCondition.makeCondition("perfTemplateId", EntityOperator.EQUALS, perfTemplateId);
        List<GenericValue> perfTemplateSection_List = delegator.findList("PerfTemplateSection", perfTemplateSection_condition, null, null, null,
                false);
        int i = 0;
        for (final GenericValue perfTemplateSection_GV : perfTemplateSection_List) {
            String perfReviewSectionId = (String) perfTemplateSection_GV.getString("perfReviewSectionId");
            EntityCondition perfReviewSection_Condition = EntityCondition.makeCondition(UtilMisc.toMap("perfReviewSectionId", perfReviewSectionId));
            List<GenericValue> listPerfReviewSectionName = delegator.findList("PerfReviewSection", perfReviewSection_Condition, null, null, null,
                    false);
            Vbox vboxPerformanceReviewSection = (Vbox) root.getFellow("vboxPerformanceReviewSection");
            Toolbarbutton toolbarbutton = new Toolbarbutton();
            toolbarbutton.setParent(vboxPerformanceReviewSection);
            toolbarbutton.setLabel((String) (listPerfReviewSectionName.get(0).get("sectionName")));
            toolbarbutton.setId("toolBtnManager" + i);

            toolbarbutton.addEventListener("onClick", new org.zkoss.zk.ui.event.EventListener() {
                public void onEvent(Event event) throws Exception {
                    unselectedToolbarbuttonColor(event.getTarget());
                    selectedToolbarbuttonColor(event.getTarget());
                    SearchAppraisal.appraisalReports(event, perfTemplateSection_GV);
                }
            });
            EntityCondition emplPerfReviewAttribute_Condition = EntityCondition.makeCondition("perfReviewSectionId", EntityOperator.EQUALS, perfReviewSectionId);
            List<GenericValue> emplPerfReviewAttribute_List = delegator.findList("EmplPerfReviewAttribute", emplPerfReviewAttribute_Condition, null,
                    null, null, false);
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

    @SuppressWarnings("deprecation")
    protected static void appraisalReports(Event event, GenericValue gv) throws InterruptedException, GenericEntityException {
        Component comp = event.getTarget();
        GenericDelegator delegator = HrmsInfrastructure.getDelegator();//GenericDelegator.getGenericDelegator("default");
        GenericValue userLogin = (GenericValue) Executions.getCurrent().getDesktop().getSession().getAttribute("userLogin");
        String partyId = (String) userLogin.get("partyId");
        String emplPerfReviewId = (String) ((Label) comp.getFellow("hiddenemplPerfReviewIdForApproval")).getValue();
        String perfReviewSectionId = (String) gv.getString("perfReviewSectionId");
        EntityCondition perfReviewSectionNameCondition = EntityCondition.makeCondition("perfReviewSectionId", EntityOperator.EQUALS, perfReviewSectionId);
        List<GenericValue> perfReviewSectionNameList = delegator.findList("PerfReviewSection", perfReviewSectionNameCondition, null, null, null,
                false);
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
        EntityCondition emplPerReviewAttributeConditionAndperfReviewSectionNameCondition = EntityCondition.makeCondition(
                emplPerReviewAttributeCondition, perfReviewSectionNameCondition);
        List<GenericValue> emplPerReviewAttributeList = delegator.findList("EmplPerfReviewAttribute",
                emplPerReviewAttributeConditionAndperfReviewSectionNameCondition, null, null, null, false);
        EntityCondition condition = null;
        String managerId = (String) ((Label) comp.getFellow("hiddenemplmanagerId")).getValue();
        condition = EntityCondition.makeCondition("reviewerId", EntityOperator.EQUALS, managerId);
        emplPerReviewAttributeConditionAndperfReviewSectionNameCondition = EntityCondition.makeCondition(
                emplPerReviewAttributeConditionAndperfReviewSectionNameCondition, condition);
        List<GenericValue> emplPerReviewersList = delegator.findList("EmplPerfReviewers",
                emplPerReviewAttributeConditionAndperfReviewSectionNameCondition, null, null, null, false);

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
            List<GenericValue> perfReviewSectionAttributeList = delegator.findList("PerfReviewSectionAttribute", perfReviewSectionNameCondition,
                    null, null, null, false);
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
            labelEmployeeRating.setValue("Employee Ratings");

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

            //Label labelRatingDescriptoionDynamic1 = new Label();
            //labelRatingDescriptoionDynamic1.setParent(hboxinvbox2);
            //labelRatingDescriptoionDynamic1.setValue((String) perfReviewSectionAttributeList.get(i).get("description"));

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

        List<GenericValue> emplPerReviewerList = delegator.findList("EmplPerfReview", emplPerReviewAttributeCondition, null, null, null, false);
        Textbox textboxOverallRating = (Textbox) comp.getFellow("textboxOverallRating");
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
