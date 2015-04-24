<%@ taglib uri="http://www.zkoss.org/dsp/web/core" prefix="c"%>
<%@ taglib uri="http://www.zkoss.org/dsp/zk/core" prefix="z"%>
<c:set var="self" value="${requestScope.arg.self}" />
<c:set var="zcls" value="${self.zclass}" />
<span id="${self.uuid}" ${self.outerAttrs} z.type="zul.db.Dtbox"
	z.combo="true"><input id="${self.uuid}!real" class="${zcls}-inp"
	autocomplete="off" ${self.innerAttrs}/><span id="${self.uuid}!btn"
	class="${zcls}-btn" ${self.buttonVisible?'':' style="display: none"'}><img
	class="${zcls}-img" onmousedown="return false;"
	src="${c:encodeURL('~./img/spacer.gif')}" /></span>
<div id="${self.uuid}!pp" class="${zcls}-pp" style="display: none"
	tabindex="-1"></div>
</span>