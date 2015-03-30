<div class="screenlet-title-bar">
  <div class="h3">${uiLabelMap.CommonNotes}</div>
  <#-- <div class="subMenuBar">
    <a class="subMenuButton" href="#">Create New</a>
  </div> -->
</div>
<div class="form">
  <#if notes?has_content>
    <table width="100%" border="0" cellpadding="1">
      <#list notes as noteRef>
        <tr>
          <td>
            <div><b>${uiLabelMap.CommonBy}: </b>${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, noteRef.noteParty, true)}</div>
            <div><b>${uiLabelMap.CommonAt}: </b>${noteRef.noteDateTime.toString()}</div>
          </td>
          <td>
            ${noteRef.noteInfo}
          </td>
        </tr>
        <#if noteRef_has_next>
          <tr><td colspan="2"><hr></td></tr>
        </#if>
      </#list>
    </table>
  <#else>
    ${uiLabelMap.PartyNoNotesForParty}
  </#if>
</div>
