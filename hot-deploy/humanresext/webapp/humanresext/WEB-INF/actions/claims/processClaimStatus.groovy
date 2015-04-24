

/** Enable processing button for mgr and admin **/
isMgr = security.hasEntityPermission("HUMANRES", "_MGR", session);
isAdmin = security.hasEntityPermission("HUMANRES", "_ADMIN", session);
context.isProcessAllowed = isMgr||isAdmin;

if (isMgr) {
	approver = "MGR";
}
if (isAdmin) {
	approver = "ADM";
}

context.approver = approver;