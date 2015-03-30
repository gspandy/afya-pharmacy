<script type="text/javascript">


// Confirm Popup for hypertext links
confirmLinkAction = function(confirmText, href) {
    var answer = confirm(confirmText);
    if (answer) {
        if (href && href.length > 0) window.location = href;
    }
}

// Confirm Popup for forms
confirmSubmitAction = function(confirmText, form) {
    var answer = confirm(confirmText);
    if (answer && form) form.submit();
}

// Function to execute either of the above depending on whether a href or form name is supplied (useful for macros)
confirmAction = function(confirmText, href, formName) {
    if (href && href.length > 0) {
        confirmLinkAction(confirmText, href);
    } else if (formName && formName.length > 0) {
        form = document.forms[formName];
        confirmSubmitAction(confirmText, form);
    }
}


</script>

<div class="screenlet-title-bar">
  <ul>
    <li class="h3">${uiLabelMap.SfaViewAccount}</li>
    <li><a href="javascript:confirmLinkAction('Are you sure','<@ofbizUrl>deactivateAccount</@ofbizUrl>?partyId=${parameters.partyId}');">Deactivate Account</a></li>
    <li><a href="<@ofbizUrl>updateAccountForm</@ofbizUrl>?partyId=${parameters.partyId}">Edit</a></li>
  </ul>
</div>

<#-- <div class="subMenuBar">
  <a class="subMenuButton" href="<@ofbizUrl>updateAccountForm</@ofbizUrl>?partyId=${parameters.partyId}">Edit</a>
  <a class="buttonDangerous"  href="javascript:confirmLinkAction('Are you sure','<@ofbizUrl>deactivateAccount</@ofbizUrl>?partyId=${parameters.partyId}');">Deactivate Account</a>
</div> -->
