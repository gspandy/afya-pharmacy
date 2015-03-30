package com.smebiz.dynforms.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.smebiz.dynamicform.Form;
import com.smebiz.dynamicform.FormCell;
import com.smebiz.dynamicform.FormRow;
import com.smebiz.dynamicform.FormSection;
import com.smebiz.dynamicform.renderer.RendererContext;

public class DummyController {

	public static Form createDummyData() {

		Form form16 = new Form();
		form16.setName("Form(16)");
		form16.setDescription("{See Rule 31(1)(a) }");

		FormSection section1 = new FormSection(
				"\n Certificate under section 203 of the Income-tax Act, 1961 for tax deducted at source from income chargeable under the head 'Salaries'");

		FormCell elem1 = new FormCell("Name and address of the employer");
		FormCell elem11 = new FormCell(
				"ORACLE INDIA PRIVATE LIMITED \n India Development Centre \n"
						+ " Oracle Technology Park,\n3, Bannerghatta Road,\n Bangalore \nKARNATAKA \nINDIA");

		FormCell elem2 = new FormCell(
				"Name and designation of the employee");
		FormCell elem22 = new FormCell(
				"Mohapatra Pradyumna Kumar \n Senior Member Technical Staff");

		FormCell cell_01 = new FormCell();
		cell_01.addComponent(elem1);

		FormCell cell_02 = new FormCell();
		cell_02.addComponent(elem2);

		FormRow row1 = new FormRow();
		row1.addComponent(cell_01);
		row1.addComponent(cell_02);

		FormCell cell_11 = new FormCell();
		cell_11.addComponent(elem11);

		FormCell cell_12 = new FormCell();
		cell_12.addComponent(elem22);

		FormRow row2 = new FormRow();
		row2.addComponent(cell_11);
		row2.addComponent(cell_12);

		section1.addComponent(row1);
		section1.addComponent(row2);

		FormCell elem3 = new FormCell("PAN/GIR NO.");
		FormCell elem4 = new FormCell("AAACO0158L");
		FormCell elem5 = new FormCell("TAN");
		FormCell elem6 = new FormCell("BLRO00194F");
		FormCell elem7 = new FormCell("PAN");
		FormCell elem8 = new FormCell("AHRPM3387J");

		FormRow row3 = new FormRow();

		FormCell cell1 = new FormCell();
		cell1.addComponent(elem3);
		cell1.addComponent(elem5);
		row3.addComponent(cell1);

		FormCell cell2 = new FormCell();
		cell2.addComponent(elem7);
		row3.addComponent(cell2);

		// 4th Row
		FormRow row4 = new FormRow();
		FormCell cell_4_1 = new FormCell();
		cell_4_1.addComponent(elem4);
		cell_4_1.addComponent(elem6);
		row4.addComponent(cell_4_1);

		FormCell cell_4_2 = new FormCell();
		cell_4_2.addComponent(elem8);
		row4.addComponent(cell_4_2);

		section1.addComponent(row3);
		section1.addComponent(row4);

		FormCell elem_5_1 = new FormCell(
				"Acknowledgement Nos. of all quarterly statements of TDS under subsection \n"
						+ " (3) of section 200 as provided by TIN Facilitation Centre or \n"
						+ " NSDL website");
		FormCell cell_5_1 = new FormCell();
		cell_5_1.addComponent(elem_5_1);

		FormRow row_5 = new FormRow();
		row_5.addComponent(cell_5_1);
		
		FormCell elem_5_2 = new FormCell("Period");
		FormCell cell_5_2_1 = new FormCell();
		cell_5_2_1.addComponent(elem_5_2);
		FormRow row_5_1 = new FormRow();
		row_5_1.addComponent(cell_5_2_1);
		
		FormRow row_5_2 = new FormRow();
		
		FormCell cell_5_2_2 = new FormCell();
		FormCell elem_5_2_2 = new FormCell("From");
		cell_5_2_2.addComponent(elem_5_2_2);
		
		FormCell elem_5_2_1 = new FormCell("To");
		cell_5_2_2.addComponent(elem_5_2_1);
		row_5_2.addComponent(cell_5_2_2);
		
		FormCell cell_5_1_2 = new FormCell();
		cell_5_1_2.addComponent(row_5_1);
		cell_5_1_2.addComponent(row_5_2);
		
		FormCell assesmentYear = new FormCell();
		assesmentYear.addComponent(new FormCell("Assesment Year"));
		row_5_1.addComponent(assesmentYear);
		
		row_5.addComponent(cell_5_1_2);
		
		section1.addComponent(row_5);

		form16.addComponent(section1);

		return form16;
	}

	public static void main(String[] args) throws IOException {
		RendererContext context = new RendererContext();
		Writer pw = new FileWriter(new File("D:\\testform.html"));
		context.setWriter(pw);
		RendererContext.setRendererContext(context);

		Form form16 = DummyController.createDummyData();
		form16.render();
		pw.flush();
		pw.close();
	}
}
