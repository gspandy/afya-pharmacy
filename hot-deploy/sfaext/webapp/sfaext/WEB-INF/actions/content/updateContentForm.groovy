import org.ofbiz.base.util.UtilMisc;

// get the information for updating a Content and DataResource.objectInfo

contentId = parameters.get("contentId");
content = delegator.findByPrimaryKey("Content", UtilMisc.toMap("contentId", contentId));
if (content == null) return;
dataResource = content.getRelatedOne("DataResource");

contentInfo = content.getAllFields();
if (dataResource != null) contentInfo.put("url", dataResource.get("objectInfo"));

context.put("contentInfo", contentInfo);
