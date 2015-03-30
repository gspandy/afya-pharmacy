package com.smebiz.widget.screen;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.webapp.view.ViewHandler;
import org.ofbiz.webapp.view.ViewHandlerException;
import org.ofbiz.widget.screen.ScreenRenderer;
import org.xml.sax.SAXException;

import com.smebiz.widget.excel.ExcelFormRenderer;
import com.smebiz.widget.excel.ExcelScreenRenderer;

public class ScreenExcelViewHandler implements ViewHandler {

	protected ServletContext servletContext = null;

	ExcelScreenRenderer renderer = new ExcelScreenRenderer();

	public void init(ServletContext context) throws ViewHandlerException {
		this.servletContext = context;
	}

	public void render(String name, String page, String info,
			String contentType, String encoding, HttpServletRequest request,
			HttpServletResponse response) {

		Writer writer = new StringWriter();
		ScreenRenderer screens = new ScreenRenderer(writer, null, renderer);
		screens.populateContextForRequest(request, response, servletContext);
		ExcelFormRenderer formRenderer = new ExcelFormRenderer(request,
				response);
		screens.getContext().put("formStringRenderer", formRenderer);
		try {
			screens.render(page);
		} catch (GeneralException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<Map<String, Object>> dataList = formRenderer.getExcelDataList();
		List<String> columns = formRenderer.getColumns();
		Map<String, String> columnHeaderMap = new HashMap<String, String>();
		for (String key : columns) {
			columnHeaderMap.put(key, key);
		}
		
		List excelDataList = new ArrayList();
		excelDataList.add(columnHeaderMap);
		excelDataList.addAll(dataList);
		HSSFWorkbook workbook = null;
		try {
			workbook = createExcel(columns, excelDataList);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response.setContentType("application/ms-excel");
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			workbook.write(os);
			os.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public HSSFWorkbook createExcel(final List<String> columnNameList,
			final List<Map<String, Object>> data) throws IOException {

		if (columnNameList == null || columnNameList.isEmpty()) {
			throw new IllegalArgumentException(
					"Argument columnNameList can't be empty");
		}

		// the data list should have at least one element for the column headers
		if (data == null || data.isEmpty()) {
			throw new IllegalArgumentException("Argument data can't be empty");
		}

		HSSFWorkbook workBook = new HSSFWorkbook();
		assert workBook != null;

		HSSFSheet workSheet = workBook.createSheet();
		assert workSheet != null;

		// create the header row

		HSSFRow headerRow = workSheet.createRow(0);
		assert workSheet != null;

		HSSFFont headerFont = workBook.createFont();
		assert headerFont != null;

		headerFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		headerFont.setColor(HSSFColor.BLACK.index);

		HSSFCellStyle headerCellStyle = workBook.createCellStyle();
		assert headerCellStyle != null;

		headerCellStyle.setFont(headerFont);
		headerCellStyle.setWrapText(true);

		// the first data list element should always be the column header map
		Map<String, Object> columnHeaderMap = data.get(0);

		if (columnHeaderMap != null) {
			for (short i = 0; i < columnNameList.size(); i++) {
				HSSFCell cell = headerRow.createCell(i);
				assert cell != null;

				cell.setCellStyle(headerCellStyle);

				Object columnHeaderTitle = columnHeaderMap.get(columnNameList
						.get(i));
				if (columnHeaderTitle != null) {
					cell.setCellValue(new HSSFRichTextString(columnHeaderTitle
							.toString()));
				}
			}
		}

		// create data rows

		// column data starts from the second element
		if (data.size() > 1) {
			for (int dataRowIndex = 1; dataRowIndex < data.size(); dataRowIndex++) {
				Map<String, Object> rowDataMap = data.get(dataRowIndex);
				if (rowDataMap == null) {
					continue;
				}

				HSSFRow dataRow = workSheet.createRow(dataRowIndex);
				assert dataRow != null;

				for (short i = 0; i < columnNameList.size(); i++) {
					HSSFCell cell = dataRow.createCell(i);
					assert cell != null;

					Object cellData = rowDataMap.get(columnNameList.get(i));
					if (cellData != null) {
						if (cellData instanceof BigDecimal) {
							cell.setCellValue(((BigDecimal) cellData)
									.doubleValue());
						} else if (cellData instanceof Double) {
							cell
									.setCellValue(((Double) cellData)
											.doubleValue());
						} else if (cellData instanceof Integer) {
							cell.setCellValue(((Integer) cellData)
									.doubleValue());
						} else if (cellData instanceof BigInteger) {
							cell.setCellValue(((BigInteger) cellData)
									.doubleValue());
						} else {
							cell.setCellValue(new HSSFRichTextString(cellData
									.toString()));
						}
					}
				}
			}
		}

		// auto size the column width
		if (columnHeaderMap != null) {
			for (short i = 0; i < columnNameList.size(); i++) {
				workSheet.autoSizeColumn(i);
			}
		}

		return workBook;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub

	}

}
