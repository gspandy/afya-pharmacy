package com.ndz.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Paging;
import org.zkoss.zul.event.PagingEvent;
import org.zkoss.zul.impl.XulElement;

import com.ndz.zkoss.PagingSimpleListModel;

public class SearchController extends GenericSearchController {

    private static final long serialVersionUID = -5241849166856683804L;

    private final int _pageSize = 10;
    private int _startPageNumber = 0;
    private int _totalSize = 0;
    private boolean _needsTotalSizeUpdate = true;
    private XulElement dataGrid;
    private Paging paging;
    private int previousPageNo = 0;
    private Map<Integer, Integer[]> perPageSelectionIndices = new HashMap<Integer, Integer[]>();
    private Map<Integer, GenericValue[]> perPageSelectedObjects = new HashMap<Integer, GenericValue[]>();

    public String employeeId;

    PagingSimpleListModel model = null;

    public SearchController() {
        super();
        super.componentId = "searchPanel";
    }

    public SearchController(String CompId) {
        super();
        super.componentId = CompId;
    }

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
    }

    public void onClick$searchPerCompany(Event event) {
        populateGrid(event, false);
    }

    public void onClick$searchPerCompany1(Event event) {
        populateGrid(event, false);
    }

    public void onClick$searchPerCompany2(Event event) {
        populateGrid(event, false);
    }

    public void onOk$searchPerCompany(Event event) {
        populateGrid(event, false);
    }

    public void onClick$searchButton(Event event) {
        populateGrid(event, true);
    }

    public void onOk$searchButton(Event event) {
        populateGrid(event, true);
    }

    protected void populateGrid(Event event, boolean perEmployee) {
        super.event = event;
        super.eventTarget = event.getTarget();
        paging.setPageSize(_pageSize);
        Map inputParams = createSearchData();
        if (perEmployee)
            inputParams.put("partyId", UtilValidate.isEmpty(employeeId) ? userLogin.getString("partyId") : employeeId);
        Map context = UtilMisc.toMap("userLogin", super.userLogin,
                "inputFields", inputParams, "noConditionFind", "Y",
                "entityName", event.getTarget().getAttribute("entityName"));

        try {
            model = new PagingSimpleListModel(context, 0, _pageSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        _totalSize = model.getTotalSize();
        paging.setTotalSize(_totalSize);

        if (dataGrid instanceof Grid) {
            Grid grid = (Grid) dataGrid;
            grid.setModel(model);
            //grid.setPageSize(5);
            paging.setActivePage(0);
        } else if (dataGrid instanceof Listbox) {
            Listbox lbox = (Listbox) dataGrid;
            paging.setActivePage(0);
            lbox.setModel(model);
            //lbox.setPageSize(5);
        }
    }

    public void onPaging$paging(ForwardEvent event) {
        final PagingEvent pe = (PagingEvent) event.getOrigin();
        _startPageNumber = pe.getActivePage();
        storeSelections();
        refreshModel(event.getTarget(), _startPageNumber);
        populateSelections();
    }

    private void populateSelections() {
        if (dataGrid instanceof Grid) return;
        Integer[] tobeSelected = perPageSelectionIndices.get(_startPageNumber);
        if (tobeSelected != null) {
            for (int j = 0; j < tobeSelected.length; j++)
                ((Listbox) dataGrid).getItemAtIndex(tobeSelected[j] - 1).setSelected(true);
        }
        previousPageNo = _startPageNumber;
    }

    private void storeSelections() {
        if (dataGrid instanceof Grid) return;
        Set<Listitem> selectedItems = (Set<Listitem>) ((Listbox) dataGrid).getSelectedItems();
        List<GenericValue> gvItems = model.getItems();
        Integer[] currentPageSelectionIndices = new Integer[selectedItems.size()];
        GenericValue currentPageSelectionGvs[] = new GenericValue[selectedItems.size()];
        int i = 0;
        for (Listitem getItem : selectedItems) {
            int index = dataGrid.getChildren().indexOf(getItem);
            currentPageSelectionGvs[i] = gvItems.get(index - 1);
            currentPageSelectionIndices[i++] = index;
        }
        perPageSelectedObjects.put(previousPageNo, currentPageSelectionGvs);
        perPageSelectionIndices.put(previousPageNo, currentPageSelectionIndices);
    }

    public List<GenericValue> getSelectedItems() {
        storeSelections();
        List<GenericValue> gvs = new ArrayList<GenericValue>();
        for (GenericValue[] gvsPerPage : perPageSelectedObjects.values()) {
            gvs.addAll(Arrays.asList(gvsPerPage));
        }
        return gvs;
    }

    protected void refreshModel(Component comp, int activePage) {
        Boolean companyWideSearch = Boolean.parseBoolean((String) comp
                .getAttribute("companyWide"));

        paging = paging == null ? (Paging) comp : paging;

        if (paging != null) {
            paging.setPageSize(_pageSize);
            Map inputParams = createSearchData();
            if (!companyWideSearch)
                inputParams.put("partyId", UtilValidate.isEmpty(employeeId) ? userLogin.getString("partyId") : employeeId);

            Map context = UtilMisc.toMap("userLogin", super.userLogin,
                    "inputFields", inputParams, "noConditionFind", "Y",
                    "entityName", event.getTarget().getAttribute("entityName"));
            try {
                model = new PagingSimpleListModel(context, activePage,
                        _pageSize);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (_needsTotalSizeUpdate) {
                _totalSize = model.getTotalSize();
                _needsTotalSizeUpdate = false;
            }
            System.out.println(" refreshModel _totalSize = " + _totalSize);
            paging.setTotalSize(_totalSize);
            if (dataGrid instanceof Grid) {
                Grid grid = (Grid) dataGrid;
                grid.setModel(model);
            } else if (dataGrid instanceof Listbox) {
                Listbox lbox = (Listbox) dataGrid;
                lbox.setModel(model);
            }
        }
    }

}
