<#escape x as x?xml>
	<fo:flow flow-name="xsl-region-body">
	
	<fo:block text-align="left">
    <#if logoImageUrl?has_content><fo:external-graphic src="<@ofbizContentUrl>${logoImageUrl}</@ofbizContentUrl>" overflow="hidden" height="40px" content-height="scale-to-fit"/></#if>
	</fo:block>
	
	<fo:block font-size="8pt">
    <fo:block>${companyName}</fo:block>
    <#if postalAddress?exists>
        <#if postalAddress?has_content>
            <fo:block>${postalAddress.address1?if_exists}</fo:block>
            <#if postalAddress.address2?has_content><fo:block>${postalAddress.address2?if_exists}</fo:block></#if>
            <fo:block>${postalAddress.city?if_exists}, ${stateProvinceAbbr?if_exists} ${postalAddress.postalCode?if_exists}, ${countryName?if_exists}</fo:block>
        </#if>
    <#else>
        <fo:block>${uiLabelMap.CommonNoPostalAddress}</fo:block>
        <fo:block>${uiLabelMap.CommonFor}: ${companyName}</fo:block>
    </#if>
 
    <#if phone?exists || email?exists || website?exists || eftAccount?exists> 
    <fo:list-block provisional-distance-between-starts="1in">
        <#if phone?exists>
        <fo:list-item>
            <fo:list-item-label>
                <fo:block>${uiLabelMap.CommonTelephoneAbbr}:</fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
                <fo:block><#if phone.countryCode?exists>${phone.countryCode}-</#if><#if phone.areaCode?exists>${phone.areaCode}-</#if>${phone.contactNumber?if_exists}</fo:block>
            </fo:list-item-body>
        </fo:list-item>
        </#if>
        <#if email?exists>
        <fo:list-item>
            <fo:list-item-label>
                <fo:block>${uiLabelMap.CommonEmail}:</fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
                <fo:block>${email.infoString?if_exists}</fo:block>
            </fo:list-item-body>
        </fo:list-item>
        </#if>
        <#if website?exists>
        <fo:list-item>
            <fo:list-item-label>
                <fo:block>${uiLabelMap.CommonWebsite}:</fo:block>
            </fo:list-item-label>
            <fo:list-item-body start-indent="body-start()">
                <fo:block>${website.infoString?if_exists}</fo:block>
            </fo:list-item-body>
        </fo:list-item>
        </#if>
    </fo:list-block>
    </#if>
	</fo:block>

	<fo:block space-after="10mm" space-before="10mm">
			Performance Period ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(perfPeriodFrom)} - ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDate(perfPeriodTo)}
	</fo:block>
	<fo:table table-layout="fixed" width="100%">
		<fo:table-column/>
		<fo:table-column/>
		<fo:table-column/>
		<fo:table-column/>
		<fo:table-body>
			<fo:table-row>
				<fo:table-cell>
					 <fo:block>
                          Employee ID
                     </fo:block>
                </fo:table-cell>
                <fo:table-cell>
                      <fo:block>
                           ${partyId}
                     </fo:block>
				</fo:table-cell>
				<fo:table-cell>
				<fo:block>
                           Rating
                     </fo:block>
				</fo:table-cell>
				<fo:table-cell><fo:block>${overallRating}</fo:block></fo:table-cell>
			</fo:table-row>
			
			<fo:table-row>
				<fo:table-cell>
					 <fo:block>
                           Name 
                     </fo:block>
                </fo:table-cell>
                <fo:table-cell number-columns-spanned="3">
                      <fo:block>
                           ${Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(delegator,partyId," ")}
                     </fo:block>
				</fo:table-cell>
			</fo:table-row>		
		</fo:table-body>
	</fo:table>	
	
	<#assign total_1 = 0/>	
    <#assign total_2 = 0/>	
	<fo:block space-after="5mm" space-before="5mm">
		<fo:table table-layout="fixed" border-collapse="collapse" font-size="12pt" width="100%" border="thin solid black">
 	       <fo:table-column/>
 	       <fo:table-column/>
	       <fo:table-body>
	        <#list reviewers as reviewerId>
	       		<fo:table-row break-before="page">
	       			 <fo:table-cell  number-columns-spanned="2">
	       				<fo:block space-before="10mm" space-after="10mm">
							Reviewed by ${Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(delegator,reviewerId," ")}		  
						</fo:block>							
							<#assign perfSectionMap=reviewerMap.get(reviewerId)>
							<#list sections as section>
								<#assign sectionmap = perfSectionMap.get(section)>
								<fo:table>
									<fo:table-column/>
									<fo:table-column/>
									<fo:table-body>
										<fo:table-row>
											<fo:table-cell number-columns-spanned="2">
											    <fo:block background-color="#000000" color="white">
											    		Feedback on ${sectionmap.sectionName}
											    </fo:block>
											</fo:table-cell>
										</fo:table-row>

					                    <fo:table-row>
					                        <fo:table-cell number-columns-spanned="2">
					                        	<fo:block background-color="#EEEEEE" font-size="8pt" text-indent="5mm"
					                        	 font-weight="normal">
					                        		${sectionmap.sectionDesc}
					                        	</fo:block>
					                        	<fo:block space-before="3mm" space-after="5mm">
					                        	</fo:block>
					                        </fo:table-cell>
					                   	</fo:table-row>
					                   	
					                   	<fo:table-row>   
              								<fo:table-cell number-columns-spanned="2">  
              									<fo:table>
							              			<fo:table-column/>
							              			<fo:table-column/>
							              			<fo:table-column/>
								              			<fo:table-header>
								              			 	<fo:table-row>
											              	 	<fo:table-cell><fo:block font-weight="bold" border-bottom="thin solid black">Attribute</fo:block></fo:table-cell>
											              	 	<fo:table-cell><fo:block font-weight="bold" border-bottom="thin solid black">Self Rating</fo:block></fo:table-cell>
											               		<fo:table-cell><fo:block font-weight="bold" border-bottom="thin solid black">Manager Rating</fo:block></fo:table-cell>
											           		</fo:table-row>        
								              			</fo:table-header>
              			
              											<fo:table-body>
              											<#assign total_3 = 0/>	
									    				<#assign total_4 = 0/>	
									              		<#list sectionmap.attributes as attribute>
									              			<fo:table-row>        
										                        <fo:table-cell>
										                            <fo:block>
										                            		${attribute.attributeName?if_exists}
										                            </fo:block>
										                        </fo:table-cell>
										                        <fo:table-cell>
										                            <fo:block>
										                            		${attribute.selfRating?number}
										                            		<#assign total_3=total_3+attribute.selfRating?number>
										                            </fo:block>
										                        </fo:table-cell>
										                      
										                        <fo:table-cell>
										                            <fo:block>
										                            		${attribute.appraiserRating?number}
										                            		<#assign total_4=total_4+attribute.appraiserRating?number>
										                            </fo:block>
										                        </fo:table-cell>	                    
									                      	</fo:table-row>
									                     </#list>
														<#assign total_1=total_1+total_3>
														<#assign total_2=total_2+total_4>  
															  
														<fo:table-row  background-color="grey" >
															<fo:table-cell>
																<fo:block> Scores </fo:block>
															</fo:table-cell>
															<fo:table-cell>
																<fo:block>${total_3}</fo:block>
															</fo:table-cell>
															<fo:table-cell>
																<fo:block>${total_4}</fo:block>
															</fo:table-cell>
													   </fo:table-row>        

								                     </fo:table-body>   
													</fo:table>
              									</fo:table-cell>
              								</fo:table-row>					                   	
					                  </fo:table-body>
					               </fo:table>
								</#list>								
						</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
      							<fo:table-cell>
      								<fo:block>Self</fo:block>
      							</fo:table-cell>
      							<fo:table-cell>
      								<fo:block>${total_1}</fo:block>
      							</fo:table-cell>
      				</fo:table-row>
      				<fo:table-row>
						<fo:table-cell>
							<fo:block>Manager(${Static["org.ofbiz.humanresext.util.HumanResUtil"].getFullName(delegator,reviewerId," ")})</fo:block>
						</fo:table-cell>
						<fo:table-cell>
							<fo:block>${total_2}</fo:block>
						</fo:table-cell>
      				</fo:table-row>
      		</#list>
            </fo:table-body> 
         </fo:table>
      </fo:block>
  		
  	 
      </fo:flow>
</#escape>