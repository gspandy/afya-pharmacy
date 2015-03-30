package com.smebiz.widget.excel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.widget.form.FormStringRenderer;
import org.ofbiz.widget.form.ModelForm;
import org.ofbiz.widget.form.ModelFormField;
import org.ofbiz.widget.form.ModelForm.Banner;
import org.ofbiz.widget.form.ModelForm.FieldGroup;
import org.ofbiz.widget.form.ModelFormField.CheckField;
import org.ofbiz.widget.form.ModelFormField.ContainerField;
import org.ofbiz.widget.form.ModelFormField.DateFindField;
import org.ofbiz.widget.form.ModelFormField.DateTimeField;
import org.ofbiz.widget.form.ModelFormField.DisplayField;
import org.ofbiz.widget.form.ModelFormField.DropDownField;
import org.ofbiz.widget.form.ModelFormField.FileField;
import org.ofbiz.widget.form.ModelFormField.HiddenField;
import org.ofbiz.widget.form.ModelFormField.HyperlinkField;
import org.ofbiz.widget.form.ModelFormField.IgnoredField;
import org.ofbiz.widget.form.ModelFormField.ImageField;
import org.ofbiz.widget.form.ModelFormField.LookupField;
import org.ofbiz.widget.form.ModelFormField.PasswordField;
import org.ofbiz.widget.form.ModelFormField.RadioField;
import org.ofbiz.widget.form.ModelFormField.RangeFindField;
import org.ofbiz.widget.form.ModelFormField.ResetField;
import org.ofbiz.widget.form.ModelFormField.SubmitField;
import org.ofbiz.widget.form.ModelFormField.TextField;
import org.ofbiz.widget.form.ModelFormField.TextFindField;
import org.ofbiz.widget.form.ModelFormField.TextareaField;
import org.ofbiz.widget.html.HtmlWidgetRenderer;

public class ExcelFormRenderer extends HtmlWidgetRenderer implements
		FormStringRenderer {

	private final HttpServletRequest request;
	private final HttpServletResponse response;

	private List<Map<String, Object>> excelDataList = null;
	private List<String> columns = null;
	private Map<String, Object> rowMap = null;
	int counter = 0;

	public List<Map<String, Object>> getExcelDataList() {
		return excelDataList;
	}

	public ExcelFormRenderer(HttpServletRequest request,
			HttpServletResponse response) {
		this.request = request;
		this.response = response;
		excelDataList = FastList.newInstance();
		columns = FastList.newInstance();
	}

	public void renderBanner(Appendable writer, Map<String, Object> context,
			Banner banner) throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderCheckField(Appendable writer,
			Map<String, Object> context, CheckField checkField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderDateFindField(Appendable writer,
			Map<String, Object> context, DateFindField textField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderDateTimeField(Appendable writer,
			Map<String, Object> context, DateTimeField dateTimeField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderDisplayField(Appendable writer,
			Map<String, Object> context, DisplayField displayField)
			throws IOException {

		String colName = displayField.getModelFormField().getTitle(context);
		System.out.println("renderDisplayField ::::: " + colName);
		if ("buttontext".equals(displayField.getModelFormField()
				.getWidgetStyle()))
			return;

		rowMap.put(colName, UtilFormatOut.encodeXmlValue(displayField
				.getDescription(context)));
	}

	public void renderDropDownField(Appendable writer,
			Map<String, Object> context, DropDownField dropDownField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFieldGroupClose(Appendable writer,
			Map<String, Object> context, FieldGroup fieldGroup)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFieldGroupOpen(Appendable writer,
			Map<String, Object> context, FieldGroup fieldGroup)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFieldTitle(Appendable writer,
			Map<String, Object> context, ModelFormField modelFormField)
			throws IOException {
	}

	public void renderFileField(Appendable writer, Map<String, Object> context,
			FileField textField) throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormClose(Appendable writer, Map<String, Object> context,
			ModelForm modelForm) throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormOpen(Appendable writer, Map<String, Object> context,
			ModelForm modelForm) throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatEmptySpace(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatFieldRowClose(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		excelDataList.add(rowMap);
	}

	public void renderFormatFieldRowOpen(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		rowMap = FastMap.newInstance();
	}

	public void renderFormatFieldRowSpacerCell(Appendable writer,
			Map<String, Object> context, ModelFormField modelFormField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatFieldRowTitleCellClose(Appendable writer,
			Map<String, Object> context, ModelFormField modelFormField)
			throws IOException {
		// TODO Auto-generated method stub
		System.out.println(" renderFormatFieldRowTitleCellClose ");
	}

	public void renderFormatFieldRowTitleCellOpen(Appendable writer,
			Map<String, Object> context, ModelFormField modelFormField)
			throws IOException {
		// TODO Auto-generated method stub
		System.out.println(" renderFormatFieldRowTitleCellOpen ");

	}

	public void renderFormatFieldRowWidgetCellClose(Appendable writer,
			Map<String, Object> context, ModelFormField modelFormField,
			int positions, int positionSpan, Integer nextPositionInRow)
			throws IOException {
		// TODO Auto-generated method stub
		System.out.println(" renderFormatFieldRowWidgetCellClose ");

	}

	public void renderFormatFieldRowWidgetCellOpen(Appendable writer,
			Map<String, Object> context, ModelFormField modelFormField,
			int positions, int positionSpan, Integer nextPositionInRow)
			throws IOException {
		// TODO Auto-generated method stub
		System.out.println(" renderFormatFieldRowWidgetCellOpen ");
	}

	public void renderFormatHeaderRowCellClose(Appendable writer,
			Map<String, Object> context, ModelForm modelForm,
			ModelFormField modelFormField) throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatHeaderRowCellOpen(Appendable writer,
			Map<String, Object> context, ModelForm modelForm,
			ModelFormField modelFormField, int positionSpan) throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatHeaderRowClose(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatHeaderRowFormCellClose(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatHeaderRowFormCellOpen(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatHeaderRowFormCellTitleSeparator(Appendable writer,
			Map<String, Object> context, ModelForm modelForm,
			ModelFormField modelFormField, boolean isLast) throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatHeaderRowOpen(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatItemRowCellClose(Appendable writer,
			Map<String, Object> context, ModelForm modelForm,
			ModelFormField modelFormField) throws IOException {
		// TODO Auto-generated method stub
		System.out.println(" renderFormatItemRowCellClose ");

	}

	public void renderFormatItemRowCellOpen(Appendable writer,
			Map<String, Object> context, ModelForm modelForm,
			ModelFormField modelFormField, int positionSpan) throws IOException {
		// TODO Auto-generated method stub
		System.out.println(" renderFormatItemRowCellOpen ");

	}

	public void renderFormatItemRowClose(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub
		excelDataList.add(rowMap);
	}

	public void renderFormatItemRowFormCellClose(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub
		System.out.println(" renderFormatItemRowFormCellClose ");
	}

	public void renderFormatItemRowFormCellOpen(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatItemRowOpen(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub
		rowMap = FastMap.newInstance();
	}

	public void renderFormatListWrapperClose(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatListWrapperOpen(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		List<ModelFormField> childFieldList = modelForm.getFieldList();
		for (ModelFormField childField : childFieldList) {
			int childFieldType = childField.getFieldInfo().getFieldType();
			if (childFieldType == ModelFormField.FieldInfo.HIDDEN
					|| childFieldType == ModelFormField.FieldInfo.IGNORED) {
				continue;
			}
			columns.add(childField.getTitle(context));
		}

	}

	public void renderFormatSingleWrapperClose(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderFormatSingleWrapperOpen(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderHiddenField(Appendable writer,
			Map<String, Object> context, ModelFormField modelFormField,
			String value) throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderHiddenField(Appendable writer,
			Map<String, Object> context, HiddenField hiddenField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderHyperlinkField(Appendable writer,
			Map<String, Object> context, HyperlinkField hyperlinkField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderIgnoredField(Appendable writer,
			Map<String, Object> context, IgnoredField ignoredField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderImageField(Appendable writer,
			Map<String, Object> context, ImageField textField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderLookupField(Appendable writer,
			Map<String, Object> context, LookupField textField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderMultiFormClose(Appendable writer,
			Map<String, Object> context, ModelForm modelForm)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderPasswordField(Appendable writer,
			Map<String, Object> context, PasswordField textField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderRadioField(Appendable writer,
			Map<String, Object> context, RadioField radioField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderRangeFindField(Appendable writer,
			Map<String, Object> context, RangeFindField textField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderResetField(Appendable writer,
			Map<String, Object> context, ResetField resetField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderSingleFormFieldTitle(Appendable writer,
			Map<String, Object> context, ModelFormField modelFormField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderSubmitField(Appendable writer,
			Map<String, Object> context, SubmitField submitField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderTextField(Appendable writer, Map<String, Object> context,
			TextField textField) throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderTextFindField(Appendable writer,
			Map<String, Object> context, TextFindField textField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public void renderTextareaField(Appendable writer,
			Map<String, Object> context, TextareaField textareaField)
			throws IOException {
		// TODO Auto-generated method stub

	}

	public List<String> getColumns() {
		// TODO Auto-generated method stub
		return columns;
	}

	@Override
	public void renderContainerFindField(Appendable writer,
			Map<String, Object> context, ContainerField containerField)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

}