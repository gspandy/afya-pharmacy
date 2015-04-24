package com.hrms.training.composer;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import com.hrms.composer.HrmsComposer;

public class TrainingListRenderer implements ListitemRenderer {

	HrmsComposer composer;
	private String defaultStatus;

	public TrainingListRenderer(HrmsComposer composer, String defaultStatus) {
		this.composer = composer;
		this.defaultStatus = defaultStatus;
	}

	public void render(final Listitem trainingListitem, Object data) throws Exception {
		GenericValue gv = (GenericValue) data;

		composer.getClass().getMethod("createRow", new Class[] {Listitem.class, GenericValue.class }).invoke(composer,
				new Object[] { trainingListitem, gv });

		Listcell lc = new Listcell();
		lc.setParent(trainingListitem);

		final Listbox statusListbox = new Listbox();
		statusListbox.setMold("select");
		AnnotateDataBinder annotBinder = new AnnotateDataBinder(statusListbox);
		annotBinder.bindBean("vo", composer);
		annotBinder.addBinding(statusListbox, "selectedItem", "vo.status");
		annotBinder.loadAll();

		final String statusId = gv.getString("statusId");
		System.out.println(" STATUS " + statusId);
		GenericValue si = composer.getDelegator().findByPrimaryKey("StatusItem", UtilMisc.toMap("statusId", statusId));
		
		
		List<GenericValue> list = getTransitionStates(gv.getString("statusId"));

		if (!defaultStatus.equals(gv.getString("statusId")) && list.size()>0) {
			Listitem firstListitem = new Listitem(si.getString("description"), "");
			firstListitem.setSelected(true);
			statusListbox.appendChild(firstListitem);

			for (GenericValue statusGv : list) {
				statusListbox.appendChild(new Listitem(statusGv.getString("transitionName"), statusGv.getString("statusIdTo")));
			}
			
			statusListbox.setItemRenderer(new ListitemRenderer() {
				public void render(Listitem innerListboxItem, Object data) throws Exception {
					if (data instanceof String) {
						innerListboxItem.setLabel((String) data);
					} else if (data instanceof GenericValue) {
						GenericValue gv = (GenericValue) data;
						innerListboxItem.setLabel(gv.getString("transitionName"));
						innerListboxItem.setValue(gv.getString("statusIdTo"));
					}
				}
			});
			statusListbox.addEventListener("onSelect", new EventListener() {
				public void onEvent(Event arg0) throws Exception {
					trainingListitem.setSelected(true);
					Events.postEvent("onSelect", trainingListitem.getParent(), null);
					trainingListitem.setSelected(true);
				}
			});
			statusListbox.setParent(lc);
		} else {
			Label lb = new Label();
			lb.setValue(si.getString("description"));
			lb.setParent(lc);
		}
		// li.setValue(gv);
	}

	private List<GenericValue> getTransitionStates(String statusId) throws Exception {
		List<GenericValue> l = composer.getDelegator().findList("StatusValidChangeToDetail",
				EntityCondition.makeCondition("statusId", statusId), null, null, null, false);
		return l;
	}

}
