package com.hrms.settings.composer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

import au.com.bytecode.opencsv.CSVReader;

import com.hrms.composer.HrmsComposer;

public class SalaryImportComposer extends HrmsComposer {

	private List<String[]> allRows;
	private String[] firstRow;
	private List<Map<String, String>> allRowsAsMap;

	@Override
	protected void executeAfterCompose(Component comp) throws Exception {

	}

	public void setFileUploadMedia(Media media) {
		if (media == null)
			return;
		CSVReader reader = new CSVReader(new InputStreamReader(media.getStreamData()));
		allRows = new ArrayList<String[]>();
		try {
			allRows = reader.readAll();
		} catch (IOException e) {
			e.printStackTrace();
		}
		firstRow = allRows.remove(0);
		allRowsAsMap = convertToDataMap(firstRow, allRows);
	}

	private List<Map<String, String>> convertToDataMap(String[] columnsToImport, List<String[]> allRows) {
		List<Map<String, String>> l = new ArrayList<Map<String, String>>();
		for (String[] row : allRows) {
			Map<String, String> m = new HashMap<String, String>();
			int index = 0;
			for (String s : columnsToImport) {
				m.put(s, row[index++]);
			}
			l.add(m);
		}
		return l;
	}

	public void onClick$createSalaryHeads(Event event) throws Exception {
		boolean beganTransaction = TransactionUtil.begin();

		List<GenericValue> salaryHeadRecs = delegator.findList("SalaryHead", null, null, null, null, false);

		Map<String, String> salaryHeadMap = new HashMap<String, String>();
		for (GenericValue gv : salaryHeadRecs) {
			salaryHeadMap.put(gv.getString("hrName"), gv.getString("salaryHeadId"));
		}

		List<String> salaryHeadsForTemplate = new ArrayList<String>();
		for (int i = 1; i < firstRow.length; i++) {
			if (salaryHeadMap.get(firstRow[i]) == null) {
				String salaryHeadId = delegator.getNextSeqId("SalaryHead");
				GenericValue newSalaryHead = delegator.makeValidValue("SalaryHead", UtilMisc.toMap("salaryHeadId", salaryHeadId, "hrName",
						firstRow[i], "isCr", "Y", "geoId", "IND", "salaryHeadTypeId", "Benefits", "currencyUomId", "INR", "isTaxable", "Y",
						"isMandatory", "Y", "salaryComputationTypeId", "LUMPSUM"));
				delegator.create(newSalaryHead);
				salaryHeadMap.put(firstRow[i], salaryHeadId);
			}
			salaryHeadsForTemplate.add(salaryHeadMap.get(firstRow[i]));
		}

		String salStructureId = delegator.getNextSeqId("SalaryStructure");
		GenericValue salStructureGV = delegator.makeValidValue("SalaryStructure", "salaryStructureId", salStructureId, "hrName",
				"Migration Template", "geoId", "IND", "isActive", "1", "currencyUomId", "INR", "positionId", "_NA_", "fromDate",
				new java.sql.Date(UtilDateTime.nowDate().getTime()), "thruDate", new java.sql.Date(UtilDateTime.nowDate().getTime()));
		
		delegator.create(salStructureGV);

		for (String salaryHeadId : salaryHeadsForTemplate) {
			String salStructureHeadId = delegator.getNextSeqId("SalaryStructureHead");
			GenericValue value = delegator.makeValidValue("SalaryStructureHead", UtilMisc.toMap("salaryStructureId", salStructureId,
					"salaryStructureHeadId", salStructureHeadId, "salaryHeadId", salaryHeadId));
			delegator.createOrStore(value);
		}

		for (String[] row : allRows) {
			String partyId = row[0];
			for (int i = 1; i < firstRow.length; i++) {
				String salaryHeadName = firstRow[i];
				String salaryHeadId = salaryHeadMap.get(salaryHeadName);
				BigDecimal amount = new BigDecimal(row[i]);
				GenericValue value = delegator.makeValidValue("EmplSal", UtilMisc.toMap("salaryHeadId", salaryHeadId, "partyId", partyId,
						"amount", amount, "fromDate", new java.sql.Date(UtilDateTime.nowDate().getTime())));
				delegator.createOrStore(value);
			}
		}

		TransactionUtil.commit(beganTransaction);
	}

}
