if(!"edit".equals(parameters.mode))
	return;
	
if("category".equals(parameters.course)){
	issueCategoryId = request.getParameter("issueCategoryId");
	context.issueCategory = delegator.findOne("IssueCategory",false, "issueCategoryId", issueCategoryId);
	return;	
}
issueSubCategoryId = request.getParameter("issueSubCategoryId");
context.issueSubCategory = delegator.findOne("IssueSubCategory",false, "issueSubCategoryId", issueSubCategoryId);
context.issueCategory = null;