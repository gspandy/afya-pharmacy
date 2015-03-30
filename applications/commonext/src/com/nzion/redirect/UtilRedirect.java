package com.nzion.redirect;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UtilRedirect {
	
	
	public static String checkExprotType(HttpServletRequest request, HttpServletResponse response) {
	      if("pdf".equalsIgnoreCase(request.getParameter("exportType"))){
				return "pdf";
			}

	      if("xls".equalsIgnoreCase(request.getParameter("exportType"))){
				return "xls"; 
			}

			return "screen";
			}
	
	public static String salesByStoreExcel(HttpServletRequest request, HttpServletResponse response) {
		if ( "xls".equalsIgnoreCase(request.getParameter("exportType"))) {
			return "excel";
		}
		if ("pdf".equalsIgnoreCase(request.getParameter("exportType"))) {
			return "pdf"; 
		}
		return "pdf";
		}
	
	public static String orderPurchaseReportExcel(HttpServletRequest request, HttpServletResponse response) {
		if ( "xls".equalsIgnoreCase(request.getParameter("exportType"))) {
			return "excel";
		}
		if ("pdf".equalsIgnoreCase(request.getParameter("exportType"))) {
			return "pdf"; 
		}
		return "pdf";
		}
	public static String purchasesByOrganizationReportExcel(HttpServletRequest request, HttpServletResponse response) {
		if ( "xls".equalsIgnoreCase(request.getParameter("exportType"))) {
			return "excel";
		}
		if ("pdf".equalsIgnoreCase(request.getParameter("exportType"))) {
			return "pdf"; 
		}
		return "pdf";
		}
	public static String openOrderItemsReportExcel(HttpServletRequest request, HttpServletResponse response) {
		if ( "xls".equalsIgnoreCase(request.getParameter("exportType"))) {
			return "excel";
		}
		if ("pdf".equalsIgnoreCase(request.getParameter("exportType"))) {
			return "pdf"; 
		}
		return "pdf";
		}
	public static String checkOrderType(HttpServletRequest request, HttpServletResponse response) {
	      if("PURCHASE_ORDER".equalsIgnoreCase(request.getParameter("orderTypeId"))){
				return "PurchaseOrder";
			}else{
				return "SalesOrder"; 
			}
	}		
}
