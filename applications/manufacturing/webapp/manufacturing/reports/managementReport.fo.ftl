<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">Management Report</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt">
    <fo:table-column column-width="proportional-column-width(5)"/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell padding-top="10pt">
                <fo:block font-weight="bold" text-decoration="underline">DAILY PRODUCTION AND SALES REPORT - ${parameters.fromDate?if_exists}</fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell padding-top="10pt">
                <fo:block font-weight="bold" text-decoration="underline">TODAY&#39;S PLANT OPERATION DATED - ${parameters.fromDate?if_exists}</fo:block>
                <fo:block margin-left="25pt">${parameters.tpo}</fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell padding-top="10pt">
                <fo:block font-weight="bold" text-decoration="underline">YESTERDAY&#39;S PRODUCTION &#38; SALES DATED - ${parameters.previousDate?if_exists}</fo:block>
                <fo:block margin-left="25pt">${parameters.cep}</fo:block>
                <fo:block margin-left="25pt">${parameters.ces}</fo:block>
                <fo:block margin-left="25pt">${parameters.clp}</fo:block>
                <fo:block margin-left="25pt">${parameters.cls}</fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell padding-top="5pt">
                <fo:block font-weight="bold" text-decoration="underline">YESTERDAY&#39;S CEMENT PACKING AND SALES DATED - ${parameters.previousDate?if_exists}</fo:block>
                <fo:block margin-left="25pt">${parameters.pl}</fo:block>
                <fo:block margin-left="25pt">${parameters.wb}</fo:block>
                <fo:block margin-left="25pt">${parameters.ut}</fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell  padding-top="10pt"><fo:block margin-left="25pt">${parameters.data}</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
</#escape>