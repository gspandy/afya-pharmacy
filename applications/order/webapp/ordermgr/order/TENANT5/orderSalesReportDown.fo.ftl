<#escape x as x?xml>
<fo:table font-size="6pt" table-layout="fixed" width="100%" space-after="0.3in">
 <fo:table-column column-width="94mm"/>
    <fo:table-column column-width="94mm"/>
    <fo:table-body font-size="6pt"  border-style="solid" border-width="thin" border-color="black">
    <#if rows?has_content>
	<#list rows as productAttributes>
		<fo:table-row>
	         <fo:table-cell text-align="center" number-columns-spanned="2" border-top-style="solid" border-top-width="thin" border-top-color="black" border-left-style="solid"  border-left-width="thin" border-left-color="black"  border-right-style="solid" border-right-width="thin" border-right-color="black">
	           <fo:block font-size="8">${productAttributes.attrType?if_exists}</fo:block>
			 </fo:table-cell>
         </fo:table-row>
		 <#assign pAttributeList = productAttributes.attrValue>
		 <#list pAttributeList as paGV>
         <fo:table-row>
	         <fo:table-cell text-align="left"  border-down-style="solid" border-down-width="thin" border-right-style="solid" border-top-style="solid" border-top-width="thin" border-top-color="black" border-right-width="thin" border-right-color="black"  border-left-style="solid"  border-left-width="thin" border-left-color="black" >
	           <fo:block font-size="6"> ${paGV.attrName?if_exists} </fo:block>
			 </fo:table-cell>
			 <fo:table-cell text-align="center" border-down-style="solid" border-down-width="thin"  border-right-style="solid" border-top-style="solid" border-top-width="thin" border-top-color="black" border-right-width="thin" border-right-color="black"  border-left-style="solid"  border-left-width="thin" border-left-color="black" >
	           <fo:block font-size="6"> ${paGV.attrValue?if_exists} </fo:block>
			 </fo:table-cell>
         </fo:table-row>
         </#list>
     </#list>
     </#if>
     
     <fo:table-row  height="30px">
	         <fo:table-cell   text-align="left"  number-columns-spanned="2" border-top-style="solid" border-top-width="thin" border-top-color="black" >
	           <fo:block font-size="8"> </fo:block>
			 </fo:table-cell>
      </fo:table-row>
	<#if test?has_content>
      <#list test as listTest>
      <#list listTest as paTestGV>
         <fo:table-row>
	         <fo:table-cell   text-align="left"  number-columns-spanned="2" border-right-style="solid" border-top-style="solid" border-top-width="thin" border-top-color="black" border-right-width="thin" border-right-color="black"  border-left-style="solid"  border-left-width="thin" border-left-color="black" >
	           <fo:block font-size="6"> ${paTestGV.attrName?if_exists}   ${paTestGV.attrValue?if_exists}</fo:block>
			 </fo:table-cell>
         </fo:table-row>
         </#list>
         </#list>
     </#if>     
     
       </fo:table-body>
         <fo:table-body>
       <fo:table-row >
         <fo:table-cell   text-align="left"  number-columns-spanned="2" >
	           <fo:block  font-size="6"> NOTE: Arrangement may be given to lift the transformer without opening the top cover of the enclosure.</fo:block>
			 </fo:table-cell>
         </fo:table-row>
          <fo:table-row >
         <fo:table-cell  height="10px" text-align="left" number-columns-spanned="2" >
	           <fo:block  font-size="6"></fo:block>
			 </fo:table-cell>
         </fo:table-row>
          <fo:table-row >
         <fo:table-cell   text-align="right" number-columns-spanned="2" >
	           <fo:block  font-size="6"> KMTM</fo:block>
			 </fo:table-cell>
         </fo:table-row>
          <fo:table-row >
         <fo:table-cell   text-align="left" number-columns-spanned="2" >
	           <fo:block  font-size="6"> Desing Dept/production Dept/Planning Dept/Assurance Dept/Finance Dept/ Sales order File/Order File</fo:block>
			 </fo:table-cell>
         </fo:table-row>
    </fo:table-body>
</fo:table>
</#escape>