package com.hrms.payroll.composer;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.security.Security;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zkplus.databind.BindingListModel;
import org.zkoss.zkplus.databind.BindingListModelList;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Panel;
import org.zkoss.zul.Toolbarbutton;

import com.hrms.composer.HrmsComposer;
import com.ndz.zkoss.util.MessageUtils;
import com.nthdimenzion.humanres.taxdecl.TaxCategory;
import com.nthdimenzion.humanres.taxdecl.TaxDeclarationEvents;
import com.nthdimenzion.humanres.taxdecl.TaxItem;
import com.smebiz.common.PartyUtil;

public class TaxDeclarationComposer extends HrmsComposer {

    private Combobox fiscalYearBox;

    private GenericValue validTaxDecl;

    private String employeeId;

    private BindingListModel taxCategoryModel;

    BindingListModel validTaxDeclList;

    Toolbarbutton saveTaxDeclaration;
    Toolbarbutton approveTaxDeclaration;
    boolean editable;
    private Panel taxDeclPanel;
    Grid taxCategoryGrid;

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    @Override
    protected void executeAfterCompose(Component comp) throws Exception {

        employeeId = userLogin.getString("partyId");
        List<GenericValue> validTaxDecls = delegator.findList("ValidTaxDecl", null, null, UtilMisc.toList("-endDate"), null, false);
        if (UtilValidate.isNotEmpty(validTaxDecls)) {
            validTaxDecl = validTaxDecls.get(0);
            editable = TaxDeclarationEvents.validate(validTaxDecl);
        }
        if (editable) {
            saveTaxDeclaration.setVisible(true);
        } else {
            saveTaxDeclaration.setVisible(false);
        }

        validTaxDeclList = new BindingListModelList(validTaxDecls, false);
        if (validTaxDecl != null) {
            List<TaxCategory> taxcategories = getTaxCategoriesAndItem(employeeId);
            if (taxcategories == null) {
                Messagebox.show("No Tax Declaration found", "Information", 1, null);
                return;
            }
            taxCategoryModel = new BindingListModelList(taxcategories, false);
        }
        enableApproveButton();
        binder.loadAttribute(fiscalYearBox, "model");
        binder.loadAll();
    }

    private void enableApproveButton() throws Exception {

        Security security = (Security) Executions.getCurrent().getAttributes().get("security");

        boolean isAdmin = security.hasPermission("HUMANRES_ADMIN", userLogin);

        if (!isAdmin)
            return;
        if (UtilValidate.isEmpty(validTaxDecl)) {
            return;
        }
        GenericValue taxDeclGV = delegator.findOne("EmplTaxDeclStatus", UtilMisc.toMap("partyId", employeeId, "validTaxDeclId", validTaxDecl.getString("validTaxDeclId")), false);
        if (isAdmin && taxDeclGV != null && "TAX_DECL_UPDATED".equals(taxDeclGV.getString("statusId"))) {
            approveTaxDeclaration.setVisible(true);
        } else
            approveTaxDeclaration.setVisible(false);

        // Cannot approve his own tax declaration
        String partyId = userLogin.getString("partyId");
        if (partyId.equals(employeeId) && editable && !isAdmin) {
            approveTaxDeclaration.setVisible(false);
        }
    }

    public BindingListModel getValidTaxDeclList() {
        return validTaxDeclList;
    }

    public void setValidTaxDeclList(BindingListModel validTaxDeclList) {
        this.validTaxDeclList = validTaxDeclList;
    }

    public void onSelect$fiscalYearBox(Event event) throws Exception {
        // employeeId = userLogin.getString("partyId");
        EntityCondition condition = EntityCondition.makeCondition("hrName", EntityOperator.EQUALS, ((Combobox) (event.getTarget().getFellow("fiscalYearBox")))
                .getValue());
        List<GenericValue> validTaxDecls = delegator.findList("ValidTaxDecl", condition, null, null, null, false);
        validTaxDecl = validTaxDecls.get(0);

        if (employeeId == null) {
            employeeId = userLogin.getString("partyId");
        }
        if (validTaxDecl != null) {

            List<TaxCategory> taxcategories = getTaxCategoriesAndItem(employeeId);

            if (taxcategories == null) {
                taxcategories = getBlankTaxCategoriesAndItem();
                taxCategoryModel = new BindingListModelList(taxcategories, false);

                editable = TaxDeclarationEvents.validate(validTaxDecl);
                saveTaxDeclaration.setVisible(false);

                binder.loadAttribute(taxCategoryGrid, "model");
                binder.loadAll();
                enableApproveButton();
                Messagebox.show("No Tax Declaration found", "Information", 1, null);
                return;
            }
            editable = TaxDeclarationEvents.validate(validTaxDecl);
            if (editable) {
                saveTaxDeclaration.setVisible(true);
            } else {
                saveTaxDeclaration.setVisible(false);
            }

            Caption caption = (Caption) taxDeclPanel.getFirstChild();
            if (caption != null) {
                GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", employeeId), true);
                caption.setLabel("Tax Declaration Of" + " " + person.getString("firstName") + " " + person.getString("lastName"));
            }

            taxCategoryModel = new BindingListModelList(taxcategories, false);
            binder.loadAttribute(taxCategoryGrid, "model");
        }
        binder.loadAll();
        enableApproveButton();
    }

    public GenericValue getValidTaxDecl() {
        return validTaxDecl;
    }

    public void setValidTaxDecl(GenericValue validTaxDecl) {
        this.validTaxDecl = validTaxDecl;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public BindingListModel getTaxCategoryModel() {
        return taxCategoryModel;
    }

    public void setTaxCategoryModel(BindingListModel taxCategoryModel) {
        this.taxCategoryModel = taxCategoryModel;
    }

    private List<TaxCategory> getTaxCategoriesAndItem(String partyId) throws Exception {

        EntityCondition entityCondition = EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "validTaxDeclId", validTaxDecl
                .getString("validTaxDeclId")));

        Date endFiscalYearDate = validTaxDecl.getDate("endDate");

        List<GenericValue> values = delegator.findList("EmplTaxDecl", entityCondition, null, null, null, false);
        List<TaxCategory> categories = null;

        if (UtilValidate.isNotEmpty(values) && values.size() > 0) {
            categories = TaxDeclarationEvents.convertToCategoryList(delegator, values);
        } else if (endFiscalYearDate.after(UtilDateTime.nowDate())) {

            String geoId = PartyUtil.getGeoIdForUserLogin(userLogin);
            EntityCondition whereCondition = EntityCondition.makeCondition(UtilMisc.toMap("geoId", geoId));
            EntityCondition condition = EntityCondition.makeConditionDate("fromDate", "thruDate");
            List<EntityCondition> conds = new ArrayList<EntityCondition>(2);
            conds.add(whereCondition);
            conds.add(condition);

            EntityFindOptions FIND_OPTIONS = new EntityFindOptions();
            FIND_OPTIONS.setDistinct(true);

            entityCondition = EntityCondition.makeCondition(conds);
            Set<String> fieldsToSelect = null;
            values = delegator.findList("TaxCategory", entityCondition, fieldsToSelect, null, FIND_OPTIONS, false);
            categories = TaxDeclarationEvents.convertToCategoryList(delegator, values);
        }
        return categories;
    }

    public void onClick$saveTaxDeclaration(Event event) throws Exception {
        try {
            String validTaxDeclId = validTaxDecl.getString("validTaxDeclId");
            boolean beganTransaction = TransactionUtil.begin();
            if (employeeId == null) {
                employeeId = userLogin.getString("partyId");
            }
            delegator.removeByCondition("EmplTaxDecl", EntityCondition.makeCondition(UtilMisc.toMap("partyId", employeeId,
                    "validTaxDeclId", validTaxDeclId)));
            for (int i = 0; i < taxCategoryModel.getSize(); i++) {
                TaxCategory category = (TaxCategory) taxCategoryModel.getElementAt(i);
                for (TaxItem ti : category.getTaxItems()) {
                    GenericValue tiGV = delegator.makeValidValue("EmplTaxDecl", UtilMisc.toMap("partyId", employeeId, "validTaxDeclId",
                            validTaxDeclId, "categoryId", category.getCategoryId(), "itemId", ti.getItemId()));
                    if ("NUMERIC".equals(ti.getItemTypeId())) {
                        tiGV.put("numValue", new BigDecimal(ti.getNumValue()));
                    } else {
                        tiGV.put("stringValue", ti.getStringValue());
                    }
                    delegator.create(tiGV);
                }
            }

            GenericValue taxStatus = delegator.makeValidValue("EmplTaxDeclStatus", UtilMisc.toMap("partyId", employeeId, "validTaxDeclId",
                    validTaxDeclId, "statusId", "TAX_DECL_UPDATED"));
            delegator.createOrStore(taxStatus);
            TransactionUtil.commit(beganTransaction);

            enableApproveButton();
            binder.loadAll();
            MessageUtils.showSuccessMessage();
        } catch (Throwable t) {
            MessageUtils.showErrorMessage();
        }
    }

    public void onClick$approveTaxDeclaration(Event event) throws Exception {
        try {
            if (employeeId == null) {
                employeeId = userLogin.getString("partyId");
            }
            String validTaxDeclId = validTaxDecl.getString("validTaxDeclId");
            boolean beganTransaction = TransactionUtil.begin();
            for (int i = 0; i < taxCategoryModel.getSize(); i++) {
                TaxCategory category = (TaxCategory) taxCategoryModel.getElementAt(i);
                for (TaxItem ti : category.getTaxItems()) {
                    GenericValue tiGV = delegator.makeValidValue("EmplTaxDecl", UtilMisc.toMap("partyId", employeeId, "validTaxDeclId", validTaxDeclId,
                            "categoryId", category.getCategoryId(), "itemId", ti.getItemId()));
                    if ("NUMERIC".equals(ti.getItemTypeId())) {
                        tiGV.put("acceptedValue", new BigDecimal(ti.getNumValue()));
                    } else {
                        tiGV.put("stringValue", ti.getStringValue());
                    }
                    delegator.createOrStore(tiGV);
                }
            }
            GenericValue statusGv = delegator.makeValidValue("EmplTaxDeclStatus", UtilMisc.toMap("partyId", employeeId, "validTaxDeclId", validTaxDeclId,
                    "statusId", "TAX_DECL_APPROVED"));
            delegator.createOrStore(statusGv);
            TransactionUtil.commit(beganTransaction);

            approveTaxDeclaration.setVisible(false);
            MessageUtils.showSuccessMessage();
        } catch (Throwable t) {
            MessageUtils.showErrorMessage();
        }
    }

    public List<TaxCategory> getBlankTaxCategoriesAndItem() throws GenericEntityException {
        String geoId = PartyUtil.getGeoIdForUserLogin(userLogin);
        EntityCondition whereCondition = EntityCondition.makeCondition(UtilMisc.toMap("geoId", geoId));
        EntityCondition condition1 = EntityCondition.makeConditionDate("fromDate", "thruDate");
        List<EntityCondition> conds = new ArrayList<EntityCondition>(2);
        conds.add(whereCondition);
        conds.add(condition1);

        EntityFindOptions FIND_OPTIONS = new EntityFindOptions();
        FIND_OPTIONS.setDistinct(true);

        EntityCondition entityCondition = EntityCondition.makeCondition(conds);
        Set<String> fieldsToSelect = null;
        List<GenericValue> values = delegator.findList("TaxCategory", entityCondition, fieldsToSelect, null, FIND_OPTIONS, false);
        List<TaxCategory> categories = TaxDeclarationEvents.convertToCategoryList(delegator, values);
        return categories;
    }

}