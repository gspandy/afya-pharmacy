package com.ndz.zkoss;

import org.ofbiz.entity.GenericValue;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

public class GenericValueRenderer implements RowRenderer, ListitemRenderer, ComboitemRenderer {

    private String[] columnNames = null;

    public GenericValueRenderer(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public void render(Row row, Object data) throws Exception {

        if (data instanceof GenericValue) {
            GenericValue gv = (GenericValue) data;
            for (String col : columnNames) {
                Object obj = gv.get(col);
                new Label(obj != null ? obj.toString() : "N.A").setParent(row);
            }
        }

    }

    public void render(Listitem listItem, Object data) throws Exception {
        if (data instanceof GenericValue) {
            GenericValue gv = (GenericValue) data;
            for (String col : columnNames) {
                Object obj = gv.get(col);
                Listcell listcell = new Listcell(obj != null ? obj.toString() : "");
                listcell.setParent(listItem);
                listItem.setValue(gv);
            }

        }
    }


    public void render(Comboitem comboitem, Object data) throws Exception {
        if (data instanceof GenericValue) {
            GenericValue gv = (GenericValue) data;
            for (String col : columnNames) {
                Object obj = gv.get(col);
                comboitem.setLabel(obj != null ? obj.toString() : "");
                comboitem.setValue(gv);
            }

        }

    }
}