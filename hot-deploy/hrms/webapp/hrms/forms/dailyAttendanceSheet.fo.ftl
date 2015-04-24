<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">ZPC - Daily Attendance Sheet</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" border-style="solid" border-color="black">
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(8)"/>
    <#list 31..1 as i>
        <fo:table-column column-width="proportional-column-width(1)"/>
    </#list>
    <fo:table-header>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block font-weight="bold">No.</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block font-weight="bold">Name</fo:block></fo:table-cell>
            <#list 31..1 as i>
                <fo:table-cell border-style="solid"></fo:table-cell>
            </#list>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
    <#list 30..1 as i>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <#list 31..1 as i>
                <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            </#list>
        </fo:table-row>
    </#list>
    </fo:table-body>
</fo:table>
</#escape>