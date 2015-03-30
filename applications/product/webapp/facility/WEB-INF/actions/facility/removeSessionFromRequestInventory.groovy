import org.ofbiz.base.util.UtilValidate;


if(session.getAttribute("requestItemList") && session.getAttribute("headerMap")){
	session.removeAttribute("headerMap");
	session.removeAttribute("requestItemList");
}

return "success";