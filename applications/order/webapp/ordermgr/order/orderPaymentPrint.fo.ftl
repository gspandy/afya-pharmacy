
<#escape x as x?xml>

  <fo:table table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column column-width="proportional-column-width(78)"/>
    <fo:table-column column-width="proportional-column-width(22)"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
          <fo:block/>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block font-size="14pt" number-columns-spanned="2">Date: ${orderPaymentPreferenceGv.createdDate?if_exists?string("dd-MM-yyyy")}</fo:block>
        </fo:table-cell>
      </fo:table-row>
      <fo:table-row>
        <fo:table-cell>
          <fo:block/>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block font-size="14pt" number-columns-spanned="2">No. ${payment.paymentId}</fo:block>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>
  
  <fo:table table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column column-width="proportional-column-width(40)"/>
    <fo:table-column column-width="proportional-column-width(35)"/>
    <fo:table-column column-width="proportional-column-width(25)"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
          <fo:block/>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block font-size="20pt" font-weight="bold">RECEIPT</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block/>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>
  
  <#-- blank line -->
  <fo:table table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-body>
      <fo:table-row height="20px" space-start=".15in">
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
    <fo:table-column column-width="proportional-column-width(21)"/>
    <fo:table-column column-width="proportional-column-width(79)"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
          <fo:block font-size="15pt">Received From: </fo:block>
        </fo:table-cell>
        <fo:table-cell>
         <fo:block><fo:inline font-weight="normal" border-bottom-style="dotted">${partyFrom}</fo:inline></fo:block>
      </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>
  
  <#-- blank line -->
  <fo:table table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-body>
      <fo:table-row height="20px" space-start=".15in">
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
    <fo:table-column column-width="proportional-column-width(30)"/>
    <fo:table-column column-width="proportional-column-width(70)"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
          <fo:block font-size="15pt">The Sum of (In Words): </fo:block>
        </fo:table-cell>
        <fo:table-cell>
         <#assign amountInWord = Static["org.ofbiz.accounting.util.Converter"].currencyInWords(payment.amount,orderHeader.currencyUom)>
        <fo:block><fo:inline font-weight="normal" border-bottom-style="dotted">${amountInWord?if_exists}</fo:inline></fo:block>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>
  
  <#-- blank line -->
  <fo:table table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-body>
      <fo:table-row height="20px" space-start=".15in">
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
    <fo:table-column column-width="proportional-column-width(18)"/>
    <fo:table-column column-width="proportional-column-width(82)"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
          <fo:block font-size="15pt">In Respect of </fo:block>
        </fo:table-cell>
        <fo:table-cell>
         <fo:block><fo:inline font-weight="normal" border-bottom-style="dotted">${quantity?default(0)?string("#,##0.00")} ${productUomName?if_exists} of ${productName?if_exists} against Order No. : ${orderHeader.orderId?if_exists}</fo:inline></fo:block>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>
  
  <#-- blank line -->
  <fo:table table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-body>
      <fo:table-row height="20px" space-start=".15in">
        <fo:table-cell>
          <fo:block />
        </fo:table-cell>
        <fo:table-cell>
          <fo:block />
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>
  
  <#-- blank line -->
  <fo:table table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column/>
    <fo:table-column/>
    <fo:table-body>
      <fo:table-row height="20px" space-start=".15in">
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
    <fo:table-column column-width="proportional-column-width(60)"/>
    <fo:table-column column-width="proportional-column-width(40)"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
          <fo:block font-size="15pt" margin-top="20px">Sign : <fo:leader leader-pattern="dots" leader-length="5cm"></fo:leader></fo:block>
        </fo:table-cell>
        <fo:table-cell border-style="double" border-color="black" height="50px">
          <fo:block font-size="15pt" margin-top="20px" margin-left="10px"><@ofbizCurrency amount=payment.amount?if_exists isoCode=orderHeader.currencyUom?if_exists/>/-</fo:block>
        </fo:table-cell>
      </fo:table-row>
      <fo:table-row>
          <fo:table-cell>
              <fo:block font-size="15pt" margin-top="20px">Received With Thanks</fo:block>
          </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>

</#escape>