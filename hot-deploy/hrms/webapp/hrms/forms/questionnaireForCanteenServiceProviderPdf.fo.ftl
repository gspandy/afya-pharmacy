<#escape x as x?xml>
<fo:block font-size="15pt" text-align="center" font-weight="bold">QUESTIONNAIRE FOR SELECTION OF THE CANTEEN SERVICE PROVIDER</fo:block>
<#assign canteen=['1. Give the Background of your company',
'2. Do you have qualified staff?',
'3. If so, how many and how available are they?',
'4. Are you able to cater for 600 people satisfactorily?',
'5. How do you intend to manage this huge number of employees?',
'6. What is your Strategy and plan in terms of structure and design which would most likely meet our satisfaction? ',
'7. What would be your offer per plate?',
'8. Any argument and clarification',
'9. If offered this contract when are you ready to take up the offer?',
'10. What are your expectations regarding provision of service to Zambezi Portland Cement Ltd?',
'11. How financially sound is your company',
'12. Do you possess the relevant statutory registration',
'13. How do you rate the quality of food you serve',
'14. Briefly run us through your menus',
'15. Do you have a good track record? Please explain to the panel',
'16. Hygienically, run us through the necessary steps and guidelines to be taken by all food handlers',
'17. Do you own your own cultlery and other requirements to used in the kitchen',
'18. How reliable is your company',
'19. Do you send your staff for medical check-ups',
'20. How often?'
    ]>
<#list canteen as can>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>${can}</fo:block>
<fo:block><fo:leader/></fo:block>
<fo:table table-layout="fixed" border-style="solid">
    <fo:table-column column-width="proportional-column-width(4)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-column column-width="proportional-column-width(2)"/>
    <fo:table-header>
        <fo:table-row font-weight="bold">
            <fo:table-cell border-style="solid"><fo:block>Caterer</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" text-align="center"><fo:block>A</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" text-align="center"><fo:block>B</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" text-align="center"><fo:block>C</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" text-align="center"><fo:block>D</fo:block></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid" font-weight="bold"><fo:block>Scores</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" text-align="center"><fo:block>1</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" text-align="center"><fo:block>3</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" text-align="center"><fo:block>5</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid" text-align="center"><fo:block>10</fo:block></fo:table-cell>
        </fo:table-row>
    </fo:table-header>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
        <fo:table-row>
            <fo:table-cell border-style="solid"><fo:block>&#160;</fo:block></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
            <fo:table-cell border-style="solid"></fo:table-cell>
        </fo:table-row>
    </fo:table-body>
</fo:table>
</#list>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block><fo:leader/></fo:block>
<fo:block>
    <fo:inline>Total scores: </fo:inline>
    <fo:inline  font-weight="bold">....................................</fo:inline><fo:inline padding-left="50pt" font-weight="bold">....................................</fo:inline>
</fo:block>
<fo:block font-weight="bold" margin-left="75pt">
    <fo:inline  font-weight="bold">....................................</fo:inline><fo:inline padding-left="50pt" font-weight="bold">....................................</fo:inline>
</fo:block>
<fo:block font-weight="bold" margin-left="75pt">
    <fo:inline  font-weight="bold">....................................</fo:inline><fo:inline padding-left="50pt" font-weight="bold">....................................</fo:inline>
</fo:block>
<fo:block font-weight="bold" margin-left="75pt">
    <fo:inline  font-weight="bold">....................................</fo:inline><fo:inline padding-left="50pt" font-weight="bold">....................................</fo:inline>
</fo:block>
</#escape>