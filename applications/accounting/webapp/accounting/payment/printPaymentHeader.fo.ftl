
<#escape x as x?xml>

  <#assign payment = delegator.findOne("Payment", {"paymentId" : parameters.paymentId}, true)>
  <#assign paymentMethodType = delegator.findOne("PaymentMethodType", {"paymentMethodTypeId" : payment.paymentMethodTypeId}, true)>
  <#assign statusItem = delegator.findOne("StatusItem", {"statusId" : payment.statusId}, true)>
  <#assign paymentType = delegator.findOne("PaymentType", {"paymentTypeId" : payment.paymentTypeId}, true)>

  <fo:table table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column column-width="proportional-column-width(78)"/>
    <fo:table-column column-width="proportional-column-width(22)"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
          <fo:block/>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block font-size="14pt" number-columns-spanned="2">Date: ${payment.createdStamp?if_exists?string("dd-MM-yyyy")}</fo:block>
        </fo:table-cell>
      </fo:table-row>
      <fo:table-row>
        <fo:table-cell>
          <fo:block/>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block font-size="14pt" number-columns-spanned="2">No. ${payment.paymentId?if_exists}</fo:block>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>

  <fo:table table-layout="fixed" space-after.optimum="2pt">
    <fo:table-column column-width="proportional-column-width(30)"/>
    <fo:table-column column-width="proportional-column-width(45)"/>
    <fo:table-column column-width="proportional-column-width(25)"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
          <fo:block/>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block font-size="20pt" font-weight="bold">PAYMENT RECEIPT</fo:block>
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

  <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" border-style="solid" border-color="black">
    <fo:table-column column-width="proportional-column-width(25)"/>
    <fo:table-column column-width="proportional-column-width(35)"/>
    <fo:table-column column-width="proportional-column-width(25)"/>
    <fo:table-column column-width="proportional-column-width(35)"/>

    <fo:table-body>

      <fo:table-row border-bottom-style="solid" border-bottom-color="grey" height="25px">
        <fo:table-cell>
          <fo:block margin-top="7px" margin-left="5px">Customer:</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">${partyNameResultFrom.fullName?if_exists}</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">Order Id:</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">${payment.orderId?if_exists}</fo:block>
        </fo:table-cell>
      </fo:table-row>

      <fo:table-row border-bottom-style="solid" border-bottom-color="grey" height="25px">
        <fo:table-cell>
          <fo:block margin-top="7px" margin-left="5px">Amount:</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px"><@ofbizCurrency amount=payment.amount?if_exists isoCode=payment.currencyUomId?if_exists /></fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">Payment Type:</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">${paymentType.description?if_exists}</fo:block>
        </fo:table-cell>
      </fo:table-row>

      <fo:table-row border-bottom-style="solid" border-bottom-color="grey" height="25px">
        <fo:table-cell>
          <fo:block margin-top="7px" margin-left="5px">Status:</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">${statusItem.description?if_exists}</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">Payment Method Type:</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">
          <#if paymentMethodType?has_content>
          ${paymentMethodType.description?if_exists}
          </#if>
          </fo:block>
        </fo:table-cell>
      </fo:table-row>

      <fo:table-row border-bottom-style="solid" border-bottom-color="grey" height="25px">
        <fo:table-cell>
          <fo:block margin-top="7px" margin-left="5px">Reference No:</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">${payment.paymentRefNum?if_exists}</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">Company:</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">${partyNameResultTo.fullName?if_exists}</fo:block>
        </fo:table-cell>
      </fo:table-row>

      <fo:table-row height="25px">
        <fo:table-cell>
          <fo:block margin-top="7px" margin-left="5px">Effective Date:</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">${payment.effectiveDate?if_exists?string("dd-MM-yyyy")}</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">Comments:</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="7px">${payment.comments?if_exists}</fo:block>
        </fo:table-cell>
      </fo:table-row>

    </fo:table-body>
  </fo:table>

  <fo:table table-layout="fixed" space-after.optimum="2pt" font-size="10pt" font-weight="bold">
    <fo:table-column column-width="proportional-column-width(30)"/>
    <fo:table-column column-width="proportional-column-width(17)"/>
    <fo:table-column column-width="proportional-column-width(53)"/>
    <fo:table-body>
      <fo:table-row>
        <fo:table-cell>
          <fo:block margin-top="20px">Received With Thanks</fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block margin-top="20px">The Sum of (In Words): </fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <#assign amountInWord = Static["org.ofbiz.accounting.util.Converter"].currencyInWords(payment.amount,payment.currencyUomId)>
          <fo:block margin-top="20px"><fo:inline font-weight="normal" border-bottom-style="dotted">${amountInWord?if_exists}</fo:inline></fo:block>
        </fo:table-cell>
      </fo:table-row>
      <fo:table-row>
        <fo:table-cell>
          <fo:block margin-top="40px">Sign : <fo:leader leader-pattern="dots" leader-length="5cm"></fo:leader></fo:block>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block/>
        </fo:table-cell>
        <fo:table-cell>
          <fo:block/>
        </fo:table-cell>
      </fo:table-row>
    </fo:table-body>
  </fo:table>

</#escape>