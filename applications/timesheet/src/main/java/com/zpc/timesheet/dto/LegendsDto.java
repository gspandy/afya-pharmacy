package com.zpc.timesheet.dto;

import java.util.*;

/**
 * Created by Administrator on 10/14/2014.
 */
public class LegendsDto {
    public static Set<Object> dropDownValuesOfSheetCellsForAdminSheet = new LinkedHashSet<Object>();
    public static Set<String> dropDownValuesOfSheetCells = new LinkedHashSet<String>();
    static {
        dropDownValuesOfSheetCellsForAdminSheet.add("P");
        dropDownValuesOfSheetCellsForAdminSheet.add("SB");
        dropDownValuesOfSheetCellsForAdminSheet.add("PF");
        dropDownValuesOfSheetCellsForAdminSheet.add("A");
        dropDownValuesOfSheetCellsForAdminSheet.add("AD");
        dropDownValuesOfSheetCellsForAdminSheet.add("CL");
        dropDownValuesOfSheetCellsForAdminSheet.add("D");
        dropDownValuesOfSheetCellsForAdminSheet.add("F");
        dropDownValuesOfSheetCellsForAdminSheet.add("L");
        dropDownValuesOfSheetCellsForAdminSheet.add("LP");
        dropDownValuesOfSheetCellsForAdminSheet.add("ML");
        dropDownValuesOfSheetCellsForAdminSheet.add("UL");
        dropDownValuesOfSheetCellsForAdminSheet.add("R");
        dropDownValuesOfSheetCellsForAdminSheet.add("S");
        dropDownValuesOfSheetCellsForAdminSheet.add("SL");
        dropDownValuesOfSheetCellsForAdminSheet.add("SU");
        dropDownValuesOfSheetCellsForAdminSheet.add("T");
        dropDownValuesOfSheetCellsForAdminSheet.add("TR");
        dropDownValuesOfSheetCellsForAdminSheet.add("WD");
        dropDownValuesOfSheetCellsForAdminSheet.add("p");
        dropDownValuesOfSheetCellsForAdminSheet.add("sb");
        dropDownValuesOfSheetCellsForAdminSheet.add("pf");
        dropDownValuesOfSheetCellsForAdminSheet.add("a");
        dropDownValuesOfSheetCellsForAdminSheet.add("ad");
        dropDownValuesOfSheetCellsForAdminSheet.add("cl");
        dropDownValuesOfSheetCellsForAdminSheet.add("d");
        dropDownValuesOfSheetCellsForAdminSheet.add("f");
        dropDownValuesOfSheetCellsForAdminSheet.add("l");
        dropDownValuesOfSheetCellsForAdminSheet.add("lp");
        dropDownValuesOfSheetCellsForAdminSheet.add("ml");
        dropDownValuesOfSheetCellsForAdminSheet.add("ul");
        dropDownValuesOfSheetCellsForAdminSheet.add("r");
        dropDownValuesOfSheetCellsForAdminSheet.add("s");
        dropDownValuesOfSheetCellsForAdminSheet.add("sl");
        dropDownValuesOfSheetCellsForAdminSheet.add("su");
        dropDownValuesOfSheetCellsForAdminSheet.add("t");
        dropDownValuesOfSheetCellsForAdminSheet.add("tr");
        dropDownValuesOfSheetCellsForAdminSheet.add("wd");
        for(double i = 0.5;i <= 20;i++){
            dropDownValuesOfSheetCellsForAdminSheet.add(String.valueOf(i).trim());
        }
        for(double i = 1;i <= 20;i++){
            dropDownValuesOfSheetCellsForAdminSheet.add(String.valueOf(i).trim());
        }
    }

    static {
        dropDownValuesOfSheetCells.add("P");
        dropDownValuesOfSheetCells.add("SB");
        dropDownValuesOfSheetCells.add("PF");
        dropDownValuesOfSheetCells.add("A");
        dropDownValuesOfSheetCells.add("AD");
        dropDownValuesOfSheetCells.add("CL");
        dropDownValuesOfSheetCells.add("D");
        dropDownValuesOfSheetCells.add("F");
        dropDownValuesOfSheetCells.add("L");
        dropDownValuesOfSheetCells.add("LP");
        dropDownValuesOfSheetCells.add("ML");
        dropDownValuesOfSheetCells.add("UL");
        dropDownValuesOfSheetCells.add("R");
        dropDownValuesOfSheetCells.add("S");
        dropDownValuesOfSheetCells.add("SL");
        dropDownValuesOfSheetCells.add("SU");
        dropDownValuesOfSheetCells.add("T");
        dropDownValuesOfSheetCells.add("TR");
        dropDownValuesOfSheetCells.add("WD");
        dropDownValuesOfSheetCells.add("p");
        dropDownValuesOfSheetCells.add("sb");
        dropDownValuesOfSheetCells.add("pf");
        dropDownValuesOfSheetCells.add("a");
        dropDownValuesOfSheetCells.add("ad");
        dropDownValuesOfSheetCells.add("cl");
        dropDownValuesOfSheetCells.add("d");
        dropDownValuesOfSheetCells.add("f");
        dropDownValuesOfSheetCells.add("l");
        dropDownValuesOfSheetCells.add("lp");
        dropDownValuesOfSheetCells.add("ml");
        dropDownValuesOfSheetCells.add("ul");
        dropDownValuesOfSheetCells.add("r");
        dropDownValuesOfSheetCells.add("s");
        dropDownValuesOfSheetCells.add("sl");
        dropDownValuesOfSheetCells.add("su");
        dropDownValuesOfSheetCells.add("t");
        dropDownValuesOfSheetCells.add("tr");
        dropDownValuesOfSheetCells.add("wd");

        for(double i = 0.5;i <= 20;i++){
            dropDownValuesOfSheetCells.add(String.valueOf(i).trim());
        }
        for(double i = 1;i <= 20;i++){
            dropDownValuesOfSheetCells.add(String.valueOf(i).trim());
        }
    }
}
