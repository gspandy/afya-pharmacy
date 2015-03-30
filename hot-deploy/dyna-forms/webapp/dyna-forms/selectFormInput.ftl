  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Select A Value</title>
    <script language="javascript" src="/images/prototypejs/prototype.js" type="text/javascript"></script>
    <script language="javascript" src="/images/prototypejs/scriptaculous.js" type="text/javascript"></script>
    <script language="javascript" src="/dyna-forms/static/jQuery.js" type="text/javascript"></script>
    <script language="javascript" src="/dyna-forms/static/interface.js" type="text/javascript"></script>
    <script language="javascript" src="/images/fieldlookup.js" type="text/javascript"></script>
    <script language="javascript" src="/images/selectall.js" type="text/javascript"></script>
    <script language="javascript" src="/images/calendar_date_select.js" type="text/javascript"></script>
    <link rel="stylesheet" href="&#47;smeVisualTheme&#47;images&#47;maincss.css" type="text/css"/>
    <link   type="text/css"  href="/static/interface-accordion.css" rel="stylesheet"> 

    <script language="JavaScript" type="text/javascript">
        // This code inserts the value lookedup by a popup window back into the associated form element
        var re_id = new RegExp('id=(\\d+)');
        var num_id = (re_id.exec(String(window.location))
                ? new Number(RegExp.$1) : 0);
        var obj_caller = (window.opener ? window.opener.lookups[num_id] : null);
        if (obj_caller == null) 
            obj_caller = window.opener;
        
        var bkColor = "yellow";
        function setSourceColor(src){
        if(src != null)
             src.style.backgroundColor = bkColor;
        }
        // function passing selected value to calling window
        function set_value(value) {
                if (!obj_caller) return;
                setSourceColor(obj_caller.target);
                obj_caller.target.value = value;
                window.close();
        }
        // function passing selected value to calling window
        function set_values(value, value2) {
                set_value(value);
                if (!obj_caller.target2) return;
                if (obj_caller.target2 == null) return;
                setSourceColor(obj_caller.target2);
                obj_caller.target2.value = value2;
        }
        function set_multivalues(value) {
            obj_caller.target.value = value;
            var thisForm = obj_caller.target.form;
            var evalString = "";
             
    		if (arguments.length > 2 ) {
        		for(var i=1; i < arguments.length; i=i+2) {
                    evalString = "setSourceColor(thisForm." + arguments[i] + ")";
                    eval(evalString); 
        			evalString = "thisForm." + arguments[i] + ".value='" + arguments[i+1] + "'";
        			eval(evalString);
        		}
    		}
    		window.close();
         }
    </script>
</head>
<body style="background-color: WHITE;">

<!-- End Template component://common/webcommon/includes/lookup.ftl -->
<!-- Begin Template component://common/webcommon/includes/messages.ftl -->




<!-- End Template component://common/webcommon/includes/messages.ftl -->
<!-- Begin Section Widget  -->
<!-- Begin Template component://dyna-forms/webapp/dyna-forms/selectFormInput.ftl -->
    		
    		
		<#assign root = (application.getAttribute("dynaFormCategories"))>
		<#assign categories = root.getCategories()>
		
		<form action="selectFormInput">
		<dl id="myAccordion">
		<#list categories as cat>
		 
		 	<dt>${cat.name}</dt>
		 	<dd>		 	
		 	<p>
		 		<#list cat.getInputs() as formInput>
		 			<input type="radio" name="dynamicRuleId" value="${formInput.id}" onclick="javascript:set_value('${formInput.id}')"/>${formInput.display} </td>		 			
		 			<br/>
		 		</#list>
		 		</p>
		 	</dd>		
		</#list>	
		</dl>
		<input type="submit" value="Next"/>
		</form>
<script type="text/javascript">
	
	$(document).ready(
		function()
		{
			$('#myAccordion').Accordion(
				{
					headerSelector	: 'dt',
					panelSelector	: 'dd',
					activeClass		: 'myAccordionActive',
					hoverClass		: 'myAccordionHover',
					panelHeight		: 200,
					speed			: 300
				}
			);
		}
	);
</script>

</body>

</html>