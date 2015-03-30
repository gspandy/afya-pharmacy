<#escape x as x?xml>

    <fo:block font-size="15pt" text-align="center" font-weight="bold">Stoppage Analysis</fo:block>
    <#-- blank line -->
    <fo:table table-layout="fixed" space-after.optimum="2pt">
        <fo:table-column/>
        <fo:table-column/>
        <fo:table-body>
            <fo:table-row height="12.5px" space-start=".15in">
                <fo:table-cell>
                    <fo:block />
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block />
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:table table-layout="fixed" space-after.optimum="2pt">
        <fo:table-column column-width="proportional-column-width(5)"/>
        <fo:table-column column-width="proportional-column-width(70)"/>
        <fo:table-column column-width="proportional-column-width(40)"/>
        <fo:table-body>
            <fo:table-row>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block number-columns-spanned="2">
                        Production Run : ${productionRunId?if_exists}
                    </fo:block>
                </fo:table-cell>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
                <fo:table-cell>
                    <fo:block/>
                </fo:table-cell>
                <fo:table-cell>
                    <#if fromDate?has_content>
                        <fo:block number-columns-spanned="2">
                            From Date : ${fromDate?if_exists?string("dd/MM/yyyy")}
                        </fo:block>
                    <#else>
                        <fo:block number-columns-spanned="2">
                            From Date :
                        </fo:block>
                    </#if>
                </fo:table-cell>
                <fo:table-cell>
                    <#if thruDate?has_content>
                        <fo:block number-columns-spanned="2">
                            Thru Date : ${thruDate?if_exists?string("dd/MM/yyyy")}
                        </fo:block>
                    <#else>
                        <fo:block number-columns-spanned="2">
                            Thru Date :
                        </fo:block>
                    </#if>
                </fo:table-cell>
            </fo:table-row>
        </fo:table-body>
    </fo:table>
    <fo:block><fo:leader></fo:leader></fo:block>

    <#-- <#if stoppageAnalysisList?has_content> -->
        <fo:block space-after.optimum="10pt" font-size="8pt">
            <fo:table table-layout="fixed" border-style="solid" border-color="black">
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(3)"/>
                <fo:table-column column-width="proportional-column-width(2)"/>
                <fo:table-header>
                    <fo:table-row font-weight="bold" text-align="center">
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Date</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Production Run Name</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Description</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Produced Qty.</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Total Production Run Time (hrs:min)</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Total Down Time (Stoppage Time) (hrs:min)</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Actual Run Time (hrs:min)</fo:block></fo:table-cell>
                        <fo:table-cell background-color="#D4D0C8" height="20pt" display-align="center" border-top-style="solid" border-bottom-style="solid"><fo:block>Stoppage Reason</fo:block></fo:table-cell>
                    </fo:table-row>
                </fo:table-header>
                <fo:table-body>
                    <#assign rowColor = "white">
                    <#-- <#list stoppageAnalysisList as stoppageAnalysis> -->
                        <fo:table-row text-align="center" height="15pt">
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px"></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px"></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px"></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px"></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px"></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px"></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px"></fo:block>
                            </fo:table-cell>
                            <fo:table-cell padding="2pt" background-color="${rowColor}">
                                <fo:block margin-top="3px"></fo:block>
                            </fo:table-cell>
                        </fo:table-row>
                        <#-- toggle the row color -->
                        <#if rowColor == "white">
                            <#assign rowColor = "#D4D0C8">
                        <#else>
                            <#assign rowColor = "white">
                        </#if>
                    <#-- </#list> -->
                </fo:table-body>
             </fo:table>
        </fo:block>
    <#-- <#else>
        <fo:block>No Record Found.</fo:block>
    </#if> -->

</#escape>
