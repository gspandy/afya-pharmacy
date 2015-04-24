<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
<fo:layout-master-set>
    <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
            margin-top="0.1in" margin-bottom="1in" margin-left="0.1in" margin-right="0.1in">
        <fo:region-body margin-top="0.5in"/>
        <fo:region-before extent="0.5in"/>
        <fo:region-after extent="0.5in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
 <fo:page-sequence master-reference="main">
 <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
 <fo:block font-size="15pt" text-align="center" font-weight="bold">Report by Service Level Agreement</fo:block>
 <fo:block><fo:leader></fo:leader></fo:block>
<#if issueReportList?has_content>
            <#if parameters.fromDate?has_content>
            	<fo:block font-size="10pt">From - ${parameters.fromDate}</fo:block>
            </#if>
            <#if parameters.toDate?has_content>
            	<fo:block font-size="10pt">To - ${parameters.toDate}</fo:block>
            </#if>
            <fo:block color="white" font-size="10pt">Blank</fo:block >
    		<fo:block space-after.optimum="10pt" font-size="10pt">
	            <fo:table font-size="8pt">
	            	<fo:table-column column-width="40pt"/>
	                <fo:table-column column-width="110pt"/>
	                <fo:table-column column-width="110pt"/>
	                <fo:table-column column-width="60pt"/>
	                <fo:table-column column-width="70pt"/>
	                <fo:table-column column-width="80pt"/>
	                <fo:table-column column-width="60pt"/>
	                <fo:table-column column-width="60pt"/>
	                <fo:table-column column-width="60pt"/>
	                <fo:table-column column-width="50pt"/>
	                <fo:table-header>
	                    <fo:table-row font-weight="bold">
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Issue-Id</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Owner</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Created By</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Created On</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Response Time(Alloted)</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Responded At</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Response Violation</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Resolution Time(Alloted)</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Resolved At</fo:block></fo:table-cell>
	                        <fo:table-cell border-bottom="thin solid grey"><fo:block>Resolution Violation</fo:block></fo:table-cell>
	                    </fo:table-row>
	                </fo:table-header>
	                <fo:table-body>
	                    <#assign rowColor = "white">
	                    <#list issueReportList as issueReport>
	                        <fo:table-row>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${issueReport.issueId?default("")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${Static["com.smebiz.common.PartyUtil"].getFullName(delegator,issueReport.categoryOwner," ")}</fo:block>
	                            </fo:table-cell>
	                  			<fo:table-cell padding="2pt" background-color="${rowColor}">
	                                 <fo:block>${Static["com.smebiz.common.PartyUtil"].getFullName(delegator,issueReport.createdBy," ")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${issueReport.createdOn?default("")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${issueReport.allotedResponseTime?default("")}Hrs</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${issueReport.responseTime?default("")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${issueReport.responseViolation?default("")} Hrs</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${issueReport.allotedResolutionTime?default("")}Hrs</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${issueReport.resolutionTime?default("")}</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell padding="2pt" background-color="${rowColor}">
	                                <fo:block>${issueReport.resolutionViolation?default("")} Hrs</fo:block>
	                            </fo:table-cell>
	                        </fo:table-row>
	                        <#-- toggle the row color -->
	                        <#if rowColor == "white">
	                            <#assign rowColor = "#eeeeee">
	                        <#else>
	                            <#assign rowColor = "white">
	                        </#if>        
	                    </#list>          
	                </fo:table-body>
	            </fo:table>
            </fo:block>
<#else>
<fo:block>No Record Found.</fo:block>
</#if>
</fo:flow>
 </fo:page-sequence>
</fo:root>
</#escape>