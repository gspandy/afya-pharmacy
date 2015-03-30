/*
 * Copyright (c) 2006 - 2007 Open Source Strategies, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the Honest Public License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Honest Public License for more details.
 * 
 * You should have received a copy of the Honest Public License
 * along with this program; if not, write to Funambol,
 * 643 Bair Island Road, Suite 305 - Redwood City, CA 94063, USA
 */

/* Initialize the 'namespace' */
if (! smebiz) var smebiz = {};


/* Wrapper functions for external libraries*/


dojo.require("dojo.fx");
dojo.require("dojo.dnd.source");
dojo.require("dojo.dnd.manager");
dojo.require("dojo.currency");
dojo.require("dojo.data.JsonItemStore");
dojo.require("dojo.data.JsonItemStoreAutoComplete");
dojo.require("dijit.Tree");
dojo.require("dojo.parser");
dojo.require("dijit.form.ComboBox");
dojo.require("dijit.util.manager");

smebiz.escapeHTML = function(text) {
    return text.split("&").join("&amp;").split("<").join("&lt;").split(">").join("&gt;").split("\"").join("&quot;");
}

smebiz.trim = function(text) {
    return text.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
}

smebiz.escapeHTML = function(text) {
    return text.split("&").join("&amp;").split("<").join("&lt;").split(">").join("&gt;")
}

smebiz.getBoundsByMargin = function(/* Node */ node) {
    if (! node) return;
    return dojo.marginBox(node); 
}

smebiz.getRequest = function(/* String */ requestName, /* Object */ context, /* Function */ handler, /* Boolean */ asJson, /* Number */ timeout) {
    asJson = asJson ? asJson : true;
    timeout = timeout ? timeout : 10000;
    var request = dojo.xhrGet({
      url: requestName,
      content: context,
      timeout: timeout,
      preventCache: true,
      handleAs: asJson ? 'json' : 'text'
    });
    return request;
}

smebiz.show = function(/* Node */ node, /* Number */ duration) {
    if (! duration) duration = 200;
    dojo.fadeIn({ node: node, duration: duration }).play();
}

smebiz.hide = function(/* Node */ node, /* Number */ duration) {
    if (! duration) duration = 200;
    dojo.fadeOut({ node: node, duration: duration }).play();
}

smebiz.addListenerToNode = function(/* Node */ node, /* String */ eventName, /* Function */ functionToAdd) {
    if (! node) return;
    dojo.connect(node, eventName, functionToAdd);
}

smebiz.addOnLoad = function(/* Function */ functionToAdd) {
    dojo.addOnLoad(functionToAdd);
}

smebiz.addClass = function(/* Node */ node, /* String */ newClass) {
    if (! node) return;
    dojo.addClass(node, newClass);
}

smebiz.removeClass = function(/* Node */ node, /* String */ oldClass) {
    if (! node) return;
    dojo.removeClass(node, oldClass);
}

smebiz.toggleClass = function( /* Node */ node, /* String */ className) {
    if (! node) return;
    return dojo.toggleClass(node, className);
}

smebiz.replaceClass = function(/* Node */ node, /* String */ newClass, /* String */ oldClass) {
    if (! node) return;
    dojo.removeClass(node, oldClass);
    dojo.addClass(node, newClass);
}

smebiz.evalJson = function(/* String */ jsonString) {
    return dojo.fromJson(jsonString);
}

smebiz.shrinkAndFade = function(/* Node */ node) {
    if (! node) return;
    var open = 'true' == node.getAttribute('open');
    if (open) {
        smebiz.hide(node, 200);
        dojo.fx.slideOut({ node: node, duration:300 }).play();
    } else {
        smebiz.show(node, 300);
        dojo.fx.slideIn({ node: node, duration:300 }).play();
    }
}

smebiz.replaceNode = function(/* Element */ oldNode, /* Element */ newNode) {
    if (! oldNode) return;
    if (! newNode) return;
    dojo.place(newNode, oldNode, 'before');
    smebiz.removeNode(oldNode);
}

smebiz.insertBefore = function(/* Element */ nodeToInsert, /* Element */ node) {
    if (! nodeToInsert) return;
    if (! node) return;
    dojo.place(nodeToInsert, node, 'before');
}

smebiz.insertAfter = function(/* Element */ nodeToInsert, /* Element */ node) {
    if (! nodeToInsert) return;
    if (! node) return;
    dojo.place(nodeToInsert, node, 'after');
}

smebiz.makeDropTarget = function(/* String */ elementId) {
    return new dojo.dnd.Source(elementId, {creator: function(data){ return {node: data.element, data: data}}});
}

smebiz.makeDragSource = function(/* dojo.dnd.Source */ dropTarget, /* Element */ dragSourceElement) {
    if (! dropTarget) return;
    dropTarget.insertNodes(false, [ {element: dragSourceElement} ]);
}

smebiz.formatCurrency = function( /* Number */ amount, /* String */ currencyUomId, /* String */ localex) {
    return dojo.currency.format(amount, {currency: currencyUomId, locale: localex});
}

// Extending Dojo's tree widget so that we can control formatting and the initial expanded or contracted state of all branches
dojo.declare("smebiz.GLAccountTree", dijit.Tree, {

    formatValueNode: function(node, value) {
        return smebiz.createSpan(null, value, 'amount');
    },

    createExpandedValueNode: function (node) {
        node.expandedValueNode = this.formatValueNode(node, node.amountSelf);
    },

    createCollapsedValueNode: function (node) {
        node.collapsedValueNode = this.formatValueNode(node, node.amountSelfAndChildren);
    },

    createValueNode: function (node) {
        var balanceOfSelf = this.tree.store.getValue(node.item, 'balanceOfSelf');
        var balanceOfSelfAndChildren = this.tree.store.getValue(node.item, 'balanceOfSelfAndChildren');
        var balanceOfSelfStr = smebiz.formatCurrency(balanceOfSelf, this.currencyUomId, this.locale);
        var balanceOfSelfAndChildrenStr = smebiz.formatCurrency(balanceOfSelfAndChildren, this.currencyUomId, this.locale);
        // set the balances as node attributes so we can retreive them later on toggle
        node.amountSelf = balanceOfSelfStr;
        node.amountSelfAndChildren = balanceOfSelfAndChildrenStr;
        // create the value nodes
        this.createExpandedValueNode(node);
        this.createCollapsedValueNode(node);
        // show the value according to the default state
        if (this.defaultExpanded) {
          return node.expandedValueNode;
        } else {
          return node.collapsedValueNode;
        }
    },

    expander: function (node) {
        if (node.declaredClass == 'dijit._TreeNode') {
            dojo.connect(node, 'addChild', this, 'expander');
            var glAccountId = this.tree.store.getIdentity(node.item);
            var glAccountName = this.tree.store.getValue(node.item, 'name');
            var label = smebiz.createSpan(null, '', 'account');
            label.appendChild(smebiz.createAnchor(null, 'AccountActivitiesDetail?glAccountId=' + glAccountId + '&amp;organizationPartyId=' + this.organizationPartyId, glAccountId, 'linktext'));
            label.appendChild(smebiz.createSpan(null, ':&nbsp;' + glAccountName));
            smebiz.replaceChildren(node.labelNode, label);
            node.labelNode.appendChild(this.createValueNode(node));
            node.expanded = this.defaultExpanded;
        }
        if (this.defaultExpanded) {
          if (node.isFolder) {
            this._controller._expand(node);
          }
        }
    },

    // The Dojo Tree widget intercepts all onclick events and publishes an 'execute' message, without allowing the event to propagate. Subscribing to this execute
    //  event doesn't allow us to determine which part of the node was clicked (EG. link vs. text) so we're overriding the _onClick function.
    //  If the source of the event is a link, do nothing - otherwise call the superclass _onClick function.
    _onClick: function (/* Event */ e) {
        var domElement = e.target;
        if (domElement.nodeName.toUpperCase() == 'A') return;
        dijit.Tree.prototype._onClick.apply(this, arguments);
    },
    addChild: function () {
        dijit.Tree.prototype.addChild.apply(this, arguments);
        this.expander(arguments[0], 1);
    },
    postCreate: function () {
        dijit.Tree.prototype.postCreate.apply(this, arguments);
        dojo.subscribe(this.id, 'toggleOpen', function(arg) {
            smebiz.removeNode(arg.node.labelNode.lastChild);
            if (arg.node.isExpanded) {
              arg.node.labelNode.appendChild(arg.node.expandedValueNode);
            } else {
              arg.node.labelNode.appendChild(arg.node.collapsedValueNode);
            }
        });
    },
    expandAll: function () {
      var children = this.getChildren();
      for(var i=0; i < children.length; i++) {
        var child = children[i];
        if (!child.isExpanded) {
          dojo.publish(this.id, [dojo.mixin({tree: this, event: 'toggleOpen', node: child})]);
        }
        var descendants = child.getDescendants();
        for(var j=0; j < descendants.length; j++) {
          var descendant = descendants[j];
          if (descendant.isFolder && !descendant.isExpanded) {
            dojo.publish(this.id, [dojo.mixin({tree: this, event: 'toggleOpen', node: descendant})]);
          }
        }
      }
    },
    collapseAll: function () {
      var children = this.getChildren();
      for(var i=0; i < children.length; i++) {
        var child = children[i];
        var descendants = child.getDescendants();
        for(var j=descendants.length-1; j >= 0; j--) {
          var descendant = descendants[j];
          if (descendant.isFolder && descendant.isExpanded) {
            dojo.publish(this.id, [dojo.mixin({tree: this, event: 'toggleOpen', node: descendant})]);
          }
        }
        if (child.isExpanded) {
          dojo.publish(this.id, [dojo.mixin({tree: this, event: 'toggleOpen', node: child})]);
        }
      }
    }
});

// Extend smebiz.GLAccountTree to provide specific two column value formating.
dojo.declare("smebiz.GLAccountTree2Col", smebiz.GLAccountTree, {

    formatValueNode: function (node, value) {
        var debitCredit = this.tree.store.getValue(node.item, 'debitCredit');
        var table = smebiz.createElement(null, 'table');
        smebiz.addClass(table, 'twoColumnAmount');
        var tbody = smebiz.createElement(null, 'tbody');
        table.appendChild(tbody);
        var row = smebiz.createTableRow();
        if (debitCredit == 'DEBIT') {
            row.appendChild(smebiz.createTableCell(null, 'debitAmount', value));
            row.appendChild(smebiz.createTableCell(null, 'creditAmount', ''));
        } else if (debitCredit == 'CREDIT') {
            row.appendChild(smebiz.createTableCell(null, 'debitAmount', ''));
            row.appendChild(smebiz.createTableCell(null, 'creditAmount', value));
        }
        tbody.appendChild(row);
        var outerNode = smebiz.createSpan(null, null, 'amount');
        outerNode.appendChild(table);
        return outerNode;
    }

});

smebiz.expandGlAccountTree = function(/* String */ treeDomId) {
    var tree = dijit.byId(treeDomId);
    if (tree == null) return;
    tree.expandAll();
}

smebiz.collapseGlAccountTree = function(/* String */ treeDomId) {
    var tree = dijit.byId(treeDomId);
    if (tree == null) return;
    tree.collapseAll();
}

// Wrapping Dojo's JSONStore class
dojo.declare("smebiz.GLAccountJsonStore", dojo.data.JsonItemStore, {});


/* smebiz DOM functions */


smebiz.uniqueIdSequence = 0;
smebiz.getUniqueId = function() {
    return "smebiz_unique_id_" + ++smebiz.uniqueIdSequence;
}

smebiz.createElement = function(/* String */ id, /* String */ nodeName, /* String */ innerHtml, /* String */ className, /* Array */ eventFunctions, /* String */ src, /* String */ altText) {
    if (! nodeName) return;
    var newValue = document.createElement(nodeName);
    if (innerHtml) newValue.innerHTML = innerHtml;
    if (src) newValue.src = src;
    if (id) newValue.id = id;
    if (className) smebiz.addClass(newValue, className);
    if (eventFunctions) {
        for (var eventName in eventFunctions) {
            smebiz.addListenerToNode(newValue, eventName, eventFunctions[eventName]);
        }
    }
    return newValue;
}

smebiz.createSpan = function(/* String */ id, /* String */ innerHtml, /* String */ className, /* Array */ eventFunctions) {
    return smebiz.createElement(id, 'span', innerHtml, className, eventFunctions);
}

smebiz.createDiv = function(/* String */ id, /* String */ innerHtml, /* String */ className, /* Array */ eventFunctions) {
    return smebiz.createElement(id, 'div', innerHtml, className, eventFunctions);
}

smebiz.createImg = function(/* String */ id, /* String */ src, /* String */ className, /* Array */ eventFunctions, /* String */ altText) {
    var newValue = smebiz.createElement(id, 'img', null, className, eventFunctions, src, altText);
    newValue.border = 0;
    return newValue;
}

smebiz.createAnchor = function(/* String */ id, /* String */ href, /* String */ innerHtml, /* String */ className, /* Array */ eventFunctions) {
    var newValue = smebiz.createElement(id, 'a', innerHtml, className, eventFunctions);
    if (href) newValue.href = href;
    return newValue;
}

smebiz.createInput = function(/* String */ id, /* String */ name, /* String */ type, /* String */ className, /* Array */ eventFunctions, /* String */ value, /* String */ size) {
    var newValue = smebiz.createElement(id, 'input', null, className, eventFunctions);
    if (type) newValue.type = type;
    newValue.name = name ? name : id;
    if (value) newValue.value = value;
    if (size) newValue.size = size;
    return newValue;
}

smebiz.createTextarea = function(/* String */ id, /* String */ name, /* String */ className, /* Array */ eventFunctions, /* String */ value, /* String */ rows, /* String */ cols) {
    var newValue = smebiz.createElement(id, 'textarea', null, className, eventFunctions);
    newValue.name = name ? name : id;
    if (value) newValue.innerHTML = value;
    if (rows) newValue.rows = rows;
    if (cols) newValue.cols = cols;
    return newValue;
}

smebiz.createTableRow = function(/* String */ id, /* String */ className, /* Number */ numberOfCells) {
    var row = smebiz.createElement(id, 'tr', null, className);
    if (numberOfCells) {
        for (var x = 0; x < numberOfCells; x++) {
            row.appendChild(smebiz.createElement(null, 'td'));
        }
    }
    return row;
}

smebiz.createTableCell = function(/* String */ id, /* String */ className, /* String */ innerHtml, /* String */ align, /* String */ colspan) {
    var cell = smebiz.createElement(id, 'td', innerHtml, className);
    if (align) cell.align = align;
    if (colspan) cell.colspan = colspan;
    return cell;
}

/**
 * The options param is in the form [option1Value, option1Text, option2Value, option2Text...] Trailing odd values will be discarded.
 */
smebiz.createSelect = function(/* String */ id, /* String */ name, /* String */ className, /* Array */ options, /* String */ selectedValue, /* Array */ eventFunctions) {
    if (! options) options = [];
    if (options.length % 2 !=  0) x.pop();
    var newValue = smebiz.createElement(id, 'select', null, className, eventFunctions);
    newValue.name = name ? name : id;
    var idx = 0;
    for (var x = 0; x < options.length; x = x + 2) {
        newValue.options[idx] = new Option(options[x + 1], options[x], options[x] == selectedValue, options[x] == selectedValue);
        idx++;
    }
    return newValue;
}

smebiz.removeNode = function(/* Element */ node) {
    if (! node) return;
    node.parentNode.removeChild(node)
}

smebiz.removeChildNodes = function(/* Element */ node) {
    if (! node) return;
    for (var i = node.childNodes.length - 1; i >= 0; i--) {
        smebiz.removeNode(node.childNodes[i]);
    }
}

smebiz.replaceChildren = function(/* Element */ parentNode, /* Element */ newNode) {
    if (! newNode) return;
    if (! parentNode) return;
    smebiz.removeChildNodes(parentNode);
    parentNode.appendChild(newNode);
}

/* Date/time functions */

/*
 smebiz.parse[format]Date functions use format pattern symbols in jscalendar style 
 and they are just wrappers for Date.print() and Date.parseDate() custom methods.
 This methods should be added to Date class during calendar.js load.
 
    %a abbreviated weekday name
    %A full weekday name
    %b abbreviated month name
    %B full month name
    %C century number
    %d the day of the month ( 00 .. 31 )
    %e the day of the month ( 0 .. 31 )
    %H hour ( 00 .. 23 )
    %I hour ( 01 .. 12 )
    %j day of the year ( 000 .. 366 )
    %k hour ( 0 .. 23 )
    %l hour ( 1 .. 12 )
    %m month ( 01 .. 12 )
    %M minute ( 00 .. 59 )
    %n a newline character
    %p "PM" or "AM"
    %P "pm" or "am"
    %S second ( 00 .. 59 )
    %s number of seconds since Epoch (since Jan 01 1970 00:00:00 UTC)
    %t a tab character
    %U, %W, %V the week number5
    %u the day of the week ( 1 .. 7, 1 = MON )
    %w the day of the week ( 0 .. 6, 0 = SUN )
    %y year without the century ( 00 .. 99 )
    %Y year including the century ( ex. 1979 )
    %% a literal % character
*/

smebiz.parseDate = function(/*String*/date, /*String*/fmt) {
	if (!date || date.length == 0 || !fmt || fmt.length == 0) {
		alert("Illegal arguments are passed to function smebiz.parseDate.");
		return null;
	}
	if (typeof Date.parseDate != "function") {
		alert("Function Date.parseDate is unavailable! Load calendar.js before this script.");
		return null;
	}
	var dateObj = Date.parseDate(date, fmt);
	return dateObj;
}

smebiz.formatDate = function(/*Date*/date, /*String*/fmt) {
	if (!date || !fmt || fmt.length == 0) {
		alert("Illegal arguments are passed to function smebiz.formatDate.");
		return null;
	}
	if (typeof Date.prototype.print != "function") {
		alert("Date.print is unavailable! Load calendar.js before this script.");
		return null;
	}
	return date.print(fmt);
}

/* smebiz functions */


smebiz.sendRequest = function(/* String */ requestName, /* Object */ context, /* Function */ handler, /* Object */ spinnerData, /* Boolean */ asJson, /* Number */ timeout) {

    var request = smebiz.getRequest(requestName, context, handler, asJson, timeout);
    
    if (spinnerData && spinnerData.target) {
        
        // Ensure a DOM element and ID for the target
        if (typeof(spinnerData.target) == 'string') spinnerData.target = document.getElementById(spinnerData.target);
        if (! spinnerData.target.id) spinnerData.target.id = smebiz.getUniqueId();
        
        // Establish defaults for the spinner
        if (! spinnerData.colour) spinnerData.colour = configProperties.bgColor;
        spinnerData.colour = spinnerData.colour.replace('#', '');
        if (! spinnerData.src) spinnerData.src = '/smebiz_images/spinners/roundPointy_' + spinnerData.colour + '.gif';
        if (! spinnerData.size) spinnerData.size = 14;
        
        // Remove the spinner container if it already exists
        if (document.getElementById(spinnerData.target.id + '_spinnerContainer')) smebiz.removeNode(document.getElementById(spinnerData.target.id + '_spinnerContainer'));

        // Create a container of the same size and position as the target, so that the container overlays the target
        var spinnerContainer = smebiz.createDiv(spinnerData.target.id + '_spinnerContainer', null, 'spinner');
        var box = smebiz.getBoundsByMargin(spinnerData.target); 
        spinnerContainer.style.height = box.h; 
        spinnerContainer.style.width = box.w; 
        spinnerContainer.style.left = box.l; 
        spinnerContainer.style.top = box.t; 
        spinnerContainer.style.position = 'absolute'; 
        
        // Add the spinner centered vertically in the container
        <!-- [if !IE]>spinnerContainer.style.paddingTop = (box.h - spinnerData.size) / 2 + 'px';<![endif]-->
        var spinner = smebiz.createImg(spinnerData.target.id + '_spinner', spinnerData.src);
        spinner.style.width = spinnerData.size + 'px';
        spinnerContainer.appendChild(spinner);
        smebiz.insertAfter(spinnerContainer, spinnerData.target);

        // Show the spinner and hide the target
        smebiz.show(spinnerContainer, 100);
        smebiz.hide(spinnerData.target, 100);

        // Add the callback to remove the spinner and show the target
        request.addCallback(function(response){
            smebiz.removeNode(document.getElementById(spinnerData.target.id + '_spinnerContainer'));
            smebiz.show(document.getElementById(spinnerData.target.id), 100);
            return response;
        });
    }
    
    // Add the callback to trigger the required function
    if (handler) {
        request.addCallback(function(response){
            handler.call(this, response);
            return response;
        });
    }
    
    // Add a callback to check for an error and hide the target and replace the spinner image with an error image,
    //  if an error exists
    if (spinnerData && spinnerData.target) {
        request.addBoth(function(response){
            if (response instanceof Error) {
                smebiz.hide(document.getElementById(spinnerData.target.id), 100);
                var spinner = document.getElementById(spinnerData.target.id + '_spinner');
                if (spinner) spinner.src = '/smebiz_images/buttons/glass_button_red_excl.png';
            }
        });
    
    }
}

/**
 * Javascript's closures can be confusing. Adding a handler to a node inside a loop, like this:
 *     
 *     for (var x = 1; x <= 3; x++) {
 *         var link = smebiz.createAnchor(null, 'this is link #' + x, text, null, {'onclick' : function(){ alert(x) }});
 *         document.appendChild(link)
 *     }
 *     
 * will result in three links with the correct link numbers, but which will each each display an alert box reading '3', because the closures inherit the *final* state of the counter.
 * 
 * Use this function to generate closures which will inherit the state of the variables during the loop:
 *     
 *     for (var x = 1; x <= 3; x++) {
 *         var link = smebiz.createAnchor(null, 'this is link #' + x, text, null, {'onclick' : smebiz.makeFunction('alert', [x])});
 *         document.appendChild(link)
 *     }
 * 
 * this example will generate three links with the correct link numbers, and alert boxes displaying '1', '2' or '3' depending on which link is clicked.
 *     
 */
smebiz.makeFunction = function(/* String */ functionName, /* Array */ params) {
    if (! functionName) return;
    if (! params) params = [];
    var paramString = '';
    for (var x = 0; x < params.length; x++) {
        if (typeof(params[x]) == 'string') {
            paramString += "'" + params[x] + "'";
        } else {
            paramString += params[x];
        }
        if (x + 1 != params.length) paramString += ', ';
    }
    return new Function(functionName + '(' + paramString + ')');
}

smebiz.stripWhitespace = function( /* String */ string) {
    return string.replace(/^\s+/, '').replace(/\s+$/, '');
}

smebiz.replaceFormElementValue = function(/* Element */ node, /* String */ value) {
    if ((! node) || (! smebiz.stripWhitespace(value))) return;
    if (node.nodeName.toUpperCase() == 'INPUT') {
        node.value = value;
    } else if (node.nodeName.toUpperCase() == 'SELECT') {
        if (! node.options) return;
        node.selectedIndex = 0;
        for (var i = 0; i < node.options.length; i++) {
            if (value == node.options[i].value) {
                node.selectedIndex = i;
                break;
            }
        }
    }
}

smebiz.replaceHtmlEditorValue = function(/* String */ textAreaId, /* String */ text) {
    var textArea = document.getElementById(textAreaId);
    if (! text || ! textArea) return;
    textArea.value = text;
    if (! FCKeditorAPI) return;
    var fckEditor = FCKeditorAPI.GetInstance(textAreaId);
    if (! fckEditor || ! fckEditor.EditorDocument || ! fckEditor.EditorDocument.body) return;
    fckEditor.EditorDocument.body.innerHTML = text;
}

// Confirm Popup for hypertext links
smebiz.confirmLinkAction = function(confirmText, href) {
    var answer = confirm(confirmText);
    if (answer) {
        if (href && href.length > 0) window.location = href;
    }
}

// Confirm Popup for forms
smebiz.confirmSubmitAction = function(confirmText, form) {
    var answer = confirm(confirmText);
    if (answer && form) form.submit();
}

// Function to execute either of the above depending on whether a href or form name is supplied (useful for macros)
smebiz.confirmAction = function(confirmText, href, formName) {
    if (href && href.length > 0) {
        smebiz.confirmLinkAction(confirmText, href);
    } else if (formName && formName.length > 0) {
        form = document.forms[formName];
        smebiz.confirmSubmitAction(confirmText, form);
    }
}

smebiz.changeLocation = function (/*String*/ href, /*String*/ id) {
    if (href && href.length > 0) {
        window.location.href = href;
        return; 
    } else {
       var ctrl = document.getElementById(id);
       if (ctrl && ctrl.value) {
           window.location.href = ctrl.value;
       }
    }
}

// Change a tax party dropdown
smebiz.changeTaxParty = function(geo, party) {
  var index = geo.selectedIndex;
  var partyId = geo.options[index].getAttribute("taxAuthPartyId");
  for (i = 0; i < party.options.length; i++) {
    if (party.options[i].value == partyId) {
      party.options[i].selected = true;
      break;
    }
  }
}

// Function to copy first set of address fields to second set, or to clear the second set.
// Note that this requires the fields to use camel case.  It also handles the dynamic state/country dropdowns.
smebiz.copyOrClearAddressFields = function(prefix1, prefix2, checkbox, defaultCountryGeoId) {
    var form = checkbox.form;
    var countriesSame = form[prefix1+"CountryGeoId"].value == form[prefix2+"CountryGeoId"].value;
    var container1 = document.getElementById(prefix1 + "AddressContainer");
    var container2 = document.getElementById(prefix2 + "AddressContainer");

    if (checkbox.checked) {
        form[prefix2+'ToName'].value = form[prefix1+'ToName'].value;
        form[prefix2+'AttnName'].value = form[prefix1+'AttnName'].value;
        form[prefix2+'Address1'].value = form[prefix1+'Address1'].value;
        form[prefix2+'Address2'].value = form[prefix1+'Address2'].value;
        form[prefix2+'City'].value = form[prefix1+'City'].value;
        form[prefix2+'PostalCode'].value = form[prefix1+'PostalCode'].value;
        form[prefix2+'PostalCodeExt'].value = form[prefix1+'PostalCodeExt'].value;
        if (!countriesSame) form[prefix2+'CountryGeoId'].value = form[prefix1+'CountryGeoId'].value;
        if (!countriesSame) {
            smebiz.swapStatesInDropdown(form[prefix2+'CountryGeoId'], prefix2+'StateProvinceGeoId');
        }
        form[prefix2+'StateProvinceGeoId'].value = form[prefix1+'StateProvinceGeoId'].value;

        form[prefix1+'ToName'].readOnly = true;
        form[prefix1+'AttnName'].readOnly = true;
        form[prefix1+'Address1'].readOnly = true;
        form[prefix1+'Address2'].readOnly = true;
        form[prefix1+'City'].readOnly = true;
        form[prefix1+'PostalCode'].readOnly = true;
        form[prefix1+'PostalCodeExt'].readOnly = true;
        form[prefix1+'CountryGeoId'].readOnly = true;
        form[prefix1+'StateProvinceGeoId'].readOnly = true;

        form[prefix2+'ToName'].readOnly = true;
        form[prefix2+'AttnName'].readOnly = true;
        form[prefix2+'Address1'].readOnly = true;
        form[prefix2+'Address2'].readOnly = true;
        form[prefix2+'City'].readOnly = true;
        form[prefix2+'PostalCode'].readOnly = true;
        form[prefix2+'PostalCodeExt'].readOnly = true;
        form[prefix2+'CountryGeoId'].readOnly = true;
        form[prefix2+'StateProvinceGeoId'].readOnly = true;

        if (container1) container1.className = 'readOnly';
        if (container2) container2.className = 'readOnly';
    } else {
        form[prefix2+'ToName'].value = '';
        form[prefix2+'AttnName'].value = '';
        form[prefix2+'Address1'].value = '';
        form[prefix2+'Address2'].value = '';
        form[prefix2+'City'].value = '';
        form[prefix2+'PostalCode'].value = '';
        form[prefix2+'PostalCodeExt'].value = '';
        form[prefix2+'CountryGeoId'].value = defaultCountryGeoId;
        if (!countriesSame) {
            smebiz.swapStatesInDropdown(form[prefix2+'CountryGeoId'], prefix2+'StateProvinceGeoId');
        }
        form[prefix2+'StateProvinceGeoId'].value = '';
        form[prefix2+'ToName'].readOnly = false;
        form[prefix2+'AttnName'].readOnly = false;
        form[prefix2+'Address1'].readOnly = false;
        form[prefix2+'Address2'].readOnly = false;
        form[prefix2+'City'].readOnly = false;
        form[prefix2+'PostalCode'].readOnly = false;
        form[prefix2+'PostalCodeExt'].readOnly = false;
        form[prefix2+'CountryGeoId'].readOnly = false;
        form[prefix2+'StateProvinceGeoId'].readOnly = false;

        form[prefix1+'ToName'].readOnly = false;
        form[prefix1+'AttnName'].readOnly = false;
        form[prefix1+'Address1'].readOnly = false;
        form[prefix1+'Address2'].readOnly = false;
        form[prefix1+'City'].readOnly = false;
        form[prefix1+'PostalCode'].readOnly = false;
        form[prefix1+'PostalCodeExt'].readOnly = false;
        form[prefix1+'CountryGeoId'].readOnly = false;
        form[prefix1+'StateProvinceGeoId'].readOnly = false;

        if (container1) container1.className = 'readOnlyRevert';
        if (container2) container2.className = 'readOnlyRevert';
    }
}

// function to swap states in a dropdown.
smebiz.swapStatesInDropdown = function(countryElement, stateElementName) {
    var stateElement = countryElement.form[stateElementName];
    var countryGeoId = countryElement[countryElement.selectedIndex].value;

    // use AJAX request to get the data
    smebiz.sendRequest(
                            "getStateDataJSON",
                            {"countryGeoId" : countryGeoId},
                            function(data) {smebiz.swapStatesInDropdownResponse(stateElement, data)}
                        );
}

// from the AJAX response, replace the given state options
smebiz.swapStatesInDropdownResponse = function(stateElement, states) {

    // build the state options
    stateElement.options[0] = new Option('', ''); // first element is always empty
    for (i = 0; i < states.length; i++) {
        state = states[i];
        stateElement.options[i+1] = new Option(state.geoName, state.geoId);
    }

    // by setting the length of the select option array, we can truncate it
    stateElement.options.length = states.length + 1;
}

// Functions to get and populate phone number fields from the current VoIP call
smebiz.getPhoneFieldsFromVoIPCall = function (/* Element */ formElement, /* Array */ countryCodeFields, /* Array */ areaCodeFields, /* Array */ numberFields) {
    smebiz.sendRequest("getCurrentIncomingNumberFromVoIPServer", {}, function(data) {smebiz.populatePhoneFieldsFromVoIPCall(formElement, countryCodeFields, areaCodeFields, numberFields, data)});
}
            
smebiz.populatePhoneFieldsFromVoIPCall = function (/* Element */ formElement, /* Array */ countryCodeFields, /* Array */ areaCodeFields, /* Array */ numberFields, /* Object */ data) {
    if (! data) return;
    if (countryCodeFields && data.countryCode) {
        for (var x = 0; x < countryCodeFields.length; x++) {
            var fieldEl = formElement ? formElement[countryCodeFields[x]] : document.getElementById(countryCodeFields[x]);
            if (fieldEl && ! fieldEl.value) fieldEl.value = data.countryCode;
        }
    }
    if (areaCodeFields && data.areaCode) {
        for (x = 0; x < areaCodeFields.length; x++) {
            fieldEl = formElement ? formElement[areaCodeFields[x]] : document.getElementById(areaCodeFields[x]);
            if (fieldEl && ! fieldEl.value) fieldEl.value = data.areaCode;
        }
    }
    if (numberFields && data.contactNumber) {
        for (x = 0; x < numberFields.length; x++) {
            fieldEl = formElement ? formElement[numberFields[x]] : document.getElementById(numberFields[x]);
            if (fieldEl && ! fieldEl.value) fieldEl.value = data.contactNumber;
        }
    }
}

/* Pagination functions */

// standard pagination response handler will put the raw data (HTML, text, etc.) in the container (see formletPlaceholder.ftl and formletMacros.ftl for what implements these)
smebiz.processPaginationResponse = function(paginatorName, data) {
    container = document.getElementById('paginate_' + paginatorName);
    container.innerHTML = data;
}
smebiz.getCurrentPage = function(paginatorName, appName, responseFunction) {
    if (! responseFunction) responseFunction = smebiz.processPaginationResponse;
    smebiz.sendRequest('paginate', {'paginatorName' : paginatorName, 'action' : 'getCurrentPage', 'applicationName' : appName}, function(data) {responseFunction(paginatorName, data)}, {target: 'paginate_' + paginatorName, containerClass: 'paginate_' + paginatorName, size: 28});
}
smebiz.getNextPage = function(paginatorName, appName, responseFunction) {
    if (! responseFunction) responseFunction = smebiz.processPaginationResponse;
    smebiz.sendRequest('paginate', {'paginatorName' : paginatorName, 'action' : 'getNextPage', 'applicationName' : appName}, function(data) {responseFunction(paginatorName, data)}, {target: 'paginate_' + paginatorName, containerClass: 'paginate_' + paginatorName, size: 28});
}
smebiz.getPreviousPage = function(paginatorName, appName, responseFunction) {
    if (! responseFunction) responseFunction = smebiz.processPaginationResponse;
    smebiz.sendRequest('paginate', {'paginatorName' : paginatorName, 'action' : 'getPreviousPage', 'applicationName' : appName}, function(data) {responseFunction(paginatorName, data)}, {target: 'paginate_' + paginatorName, containerClass: 'paginate_' + paginatorName, size: 28});
}
smebiz.getFirstPage = function(paginatorName, appName, responseFunction) {
    if (! responseFunction) responseFunction = smebiz.processPaginationResponse;
    smebiz.sendRequest('paginate', {'paginatorName' : paginatorName, 'action' : 'getFirstPage', 'applicationName' : appName}, function(data) {responseFunction(paginatorName, data)}, {target: 'paginate_' + paginatorName, containerClass: 'paginate_' + paginatorName, size: 28});
}
smebiz.getLastPage = function(paginatorName, appName, responseFunction) {
    if (! responseFunction) responseFunction = smebiz.processPaginationResponse;
    smebiz.sendRequest('paginate', {'paginatorName' : paginatorName, 'action' : 'getLastPage', 'applicationName' : appName}, function(data) {responseFunction(paginatorName, data)}, {target: 'paginate_' + paginatorName, containerClass: 'paginate_' + paginatorName, size: 28});
}
smebiz.getPageNumber = function(paginatorName, appName, pageNumber, responseFunction) {
    if (! responseFunction) responseFunction = smebiz.processPaginationResponse;
    smebiz.sendRequest('paginate', {'paginatorName' : paginatorName, 'pageNumber' : pageNumber, 'applicationName' : appName}, function(data) {responseFunction(paginatorName, data)}, {target: 'paginate_' + paginatorName, containerClass: 'paginate_' + paginatorName, size: 28});
}
smebiz.changePaginationOrder = function(paginatorName, appName, orderBy, orderByReverse, responseFunction) {
    if (! responseFunction) responseFunction = smebiz.processPaginationResponse;
    smebiz.sendRequest('changePaginationOrder', {'paginatorName' : paginatorName, 'orderBy' : orderBy, 'orderByReverse' : orderByReverse, 'applicationName' : appName}, function(data) {responseFunction(paginatorName, data)}, {target: 'paginate_' + paginatorName, containerClass: 'paginate_' + paginatorName, size: 28});
}
smebiz.changePaginationViewSize = function(paginatorName, appName, delta, responseFunction) {
    if (! responseFunction) responseFunction = smebiz.processPaginationResponse;
    smebiz.sendRequest('changePaginationViewSize', {'paginatorName' : paginatorName, 'delta' : delta, 'applicationName' : appName}, function(data) {responseFunction(paginatorName, data)}, {target: 'paginate_' + paginatorName, containerClass: 'paginate_' + paginatorName, size: 28});
}

/* auto complete functions */

smebiz.initInputAutoCompleteQuick = function(/* String */ jsonItemStoreUrl, /* String */ elementName, /* String */ elementId, /* String */ defaultValue) {
    var store = new dojo.data.JsonItemStoreAutoComplete({url: jsonItemStoreUrl});
    var comboBox = new dijit.form.ComboBox({
        name: elementName,
        value: defaultValue,
        autoComplete: false,
        store: store,
        searchAttr: "name",
        searchDelay: 1500,
        size: 15,
        maxlength: 20,
        hasDownArrow: false
    }, dojo.byId(elementId));
    return comboBox;
}

/* Miscellaneous Functions */

// Multi form function to change the _rowSubmit_o_${index} to Y
smebiz.markRowForSubmit = function(form, index) {
    form["_rowSubmit_o_" + index].value='Y';
}

// Extends dojo ComboBox to publish the value in another hidden input
// so that the value can get submitted
dojo.extend(dijit.form.ComboBox, {
  hiddenId: null,
  setDisplayedValue:function(value){
      this.setValue(value);
      dojo.byId(this.hiddenId).value = value;
    }
  });
