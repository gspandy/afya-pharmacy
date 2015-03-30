<#assign context = session.getServletContext().getContext("/formdesign")>
${context.getRequestDispatcher("/control/previewLayout?formId=10000&partyId=10000").include(request,response)}