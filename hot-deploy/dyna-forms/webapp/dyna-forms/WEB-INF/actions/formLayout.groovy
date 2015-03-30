
request.getSession(false).setAttribute("formId",request.getParameter("formId"));
context.put("formId",request.getSession(false).getAttribute("formId"));