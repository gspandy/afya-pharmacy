<#escape x as x?xml>

  <style>
      .rolledUp {
          color: #222;
      }

      .single {
          color: #999;
      }
  </style>
  <#setting number_format="0.00">
  <#assign customTimePeriod = parameters.customTimePeriodId>
  <#assign periodMap = delegator.findOne("CustomTimePeriod",true,"customTimePeriodId",customTimePeriod)>
  <#include "component://common/webcommon/includes/commonMacros.ftl"/>
  <#assign orgParty = delegator.findOne("PartyGroup",true,"partyId",parameters.organizationPartyId)>
  <#assign partyIds = Static["org.ofbiz.party.party.PartyWorker"].getAssociatedPartyIdsByRelationshipType(delegator, parameters.organizationPartyId, "GROUP_ROLLUP")>

  <#assign organizationId = parameters.organizationPartyId>

  <#assign assetAccounts = ["9990300","40000000_9","40000000_1"]>
  <#assign liabilityAccounts =["9600100","300000000_3","300000000_9","9420000","300000000_8","9100000"]>

  <fo:block font-size="15pt" text-align="center" font-weight="bold">Balance Sheet</fo:block>

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

  <#if parameters.print?has_content>
      <fo:block font-size="12pt" font-weight="bold" text-align="left">Balance Sheet As At ${periodMap.thruDate?string("dd-MM-yyyy")}</fo:block>
  <#else>
      <fo:block font-size="12pt" font-weight="bold" text-align="left">Balance Sheet As At ${periodMap.thruDate?string("dd-MM-yyyy")}</fo:block>
  </#if>

  <fo:block><fo:leader></fo:leader></fo:block>
  <fo:block><fo:leader></fo:leader></fo:block>

  <fo:block space-after.optimum="10pt" font-size="9pt">
    <fo:table table-layout="fixed" space-after.optimum="2pt" id="detailBalanceSheet">
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <#if parameters.print?has_content>
              <fo:block font-size="12pt" font-weight="bold" text-align="left">ASSETS</fo:block>
            <#else>
              <fo:block font-size="12pt" font-weight="bold" text-align="left">ASSETS</fo:block>
            </#if>
          </fo:table-cell>
        </fo:table-row>
        <#list assetAccounts as glAccountId>
          <#assign glAccount =delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})>
          <#assign childGlAccounts = delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists})>
          <#assign hasChild=true>
          <fo:table-row>
            <fo:table-cell>
              <fo:block font-weight="bold" margin-left="30px">${glAccount.accountName}</fo:block>
            </fo:table-cell>
            <#if !childGlAccounts?has_content>
              <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId, customTimePeriod, delegator, organizationId)>
              <fo:table-cell>
                <fo:block width="40%" text-align="right" margin-right="270px"><@reportCurrency amount=glAccountAmount?if_exists/></fo:block>
              </fo:table-cell>
              <#assign hasChild=false>
            </#if>
          </fo:table-row>
          <#if childGlAccounts?has_content>
            <#list childGlAccounts as childGlAccount>
              <@glAccountBalances childGlAccount 2/>
            </#list>
          </#if>
          <#if hasChild>
            <fo:table-row>
              <fo:table-cell/>
              <fo:table-cell>
                <fo:block width="40%" text-align="right" margin-right="270px">____________</fo:block>
              </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
              <fo:table-cell/>
              <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId, customTimePeriod, organizationId, true)>
              <fo:table-cell>
                <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=glAccountAmount?if_exists/></fo:block>
              </fo:table-cell>
            </fo:table-row>
          </#if>
        </#list>
        <fo:table-row>
          <fo:table-cell/>
          <fo:table-cell>
            <fo:block width="40%" text-align="right" margin-right="270px">____________</fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator,"40000000", customTimePeriod, organizationId, true)>
          <fo:table-cell>
            <fo:block style="font-style:italic;" font-weight="bold" margin-left="230px" margin-right="10px">Total Assets</fo:block>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=glAccountAmount?if_exists/></fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell/>
          <fo:table-cell>
            <fo:block width="40%" text-align="right" margin-right="270px">
              <fo:external-graphic src="./applications/accounting/webapp/accounting/images/doubleline.png"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
  </fo:block>

  <fo:block><fo:leader></fo:leader></fo:block>
  <fo:block><fo:leader></fo:leader></fo:block>

  <fo:block space-after.optimum="10pt" font-size="9pt">
    <fo:table table-layout="fixed" space-after.optimum="2pt" id="detailBalanceSheet">
      <fo:table-body>
        <fo:table-row>
          <fo:table-cell>
            <fo:block font-size="12pt" font-weight="bold" text-align="left">Equity and Liabilities</fo:block>
          </fo:table-cell>
        </fo:table-row>
        <#list liabilityAccounts as glAccountId>
          <#assign glAccount = delegator.findByPrimaryKey("GlAccount",{"glAccountId":glAccountId})?default({})>
          <#assign childGlAccounts = delegator.findByAnd("GlAccount",{"parentGlAccountId":glAccount.glAccountId?if_exists})>
          <#assign hasChild=true>
          <fo:table-row>
            <fo:table-cell>
              <fo:block font-weight="bold" margin-left="30px">${glAccount.accountName?if_exists}</fo:block>
            </fo:table-cell>
            <#if !childGlAccounts?has_content>
              <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId, customTimePeriod, delegator, organizationId)>
              <fo:table-cell>
                <fo:block width="40%" text-align="right" margin-right="270px"><@reportCurrency amount=glAccountAmount?if_exists/></fo:block>
              </fo:table-cell>
              <#assign hasChild=false>
            </#if>
          </fo:table-row>
          <#if childGlAccounts?has_content>
            <#list childGlAccounts as childGlAccount>
              <@glAccountBalances childGlAccount 2/>
            </#list>
          </#if>
          <#if hasChild>
            <fo:table-row>
                <fo:table-cell/>
                <fo:table-cell>
                  <fo:block width="40%" text-align="right" margin-right="270px">____________</fo:block>
                </fo:table-cell>
            </fo:table-row>
            <fo:table-row>
                <fo:table-cell/>
                <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, glAccount.glAccountId, customTimePeriod, organizationId, false)>
                 <fo:table-cell>
                  <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=glAccountAmount?if_exists/></fo:block>
                </fo:table-cell>
            </fo:table-row>
          </#if>
        </#list>
        <fo:table-row>
          <#assign profitAndLossAmt = Static["org.ofbiz.accounting.util.UtilAccounting"].getProfitAndLossAmount(delegator,customTimePeriod,organizationId)>
            <fo:table-cell>
              <fo:block font-weight="bold" margin-left="30px">
                Profit &amp; Loss A/c
              </fo:block>
            </fo:table-cell>
            <fo:table-cell>
              <fo:block width="40%" text-align="right" margin-right="270px"><@reportCurrency amount=profitAndLossAmt?if_exists/></fo:block>
            </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell/>
          <fo:table-cell>
            <fo:block width="40%" text-align="right" margin-right="270px">____________</fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getGlTotalTransactionAmount(delegator, "300000000", customTimePeriod, organizationId, false)>
          <fo:table-cell>
            <fo:block style="font-style:italic;" font-weight="bold" margin-left="230px" margin-right="10px">Total Liabilities</fo:block>
          </fo:table-cell>
          <fo:table-cell>
            <fo:block width="40%" text-align="right" margin-right="270px" font-weight="bold"><@reportCurrency amount=(glAccountAmount+profitAndLossAmt)?if_exists/></fo:block>
          </fo:table-cell>
        </fo:table-row>
        <fo:table-row>
          <fo:table-cell/>
          <fo:table-cell>
            <fo:block width="40%" text-align="right" margin-right="270px">
              <fo:external-graphic src="./applications/accounting/webapp/accounting/images/doubleline.png"/>
            </fo:block>
          </fo:table-cell>
        </fo:table-row>
      </fo:table-body>
    </fo:table>
  </fo:block>

  <#macro glAccountBalances glAccount level>
    <#assign childGlAccounts = delegator.findByAnd("GlAccount", {"parentGlAccountId" : glAccount.glAccountId?if_exists},["accountName"])>
    <#assign childPresent = childGlAccounts.size()>
    <fo:table-row>
      <fo:table-cell>
        <fo:block style="font-style:italic;" margin-left="${30*level}px" margin-right="10px">${glAccount.accountName}</fo:block>
      </fo:table-cell>
      <#assign glAccountAmount = Static["org.ofbiz.accounting.util.UtilAccounting"].getTransactionAmountForChild(glAccount.glAccountId,customTimePeriod, delegator,organizationId)>
      <fo:table-cell>
        <fo:block width="40%" text-align="right" margin-right="270px"><@reportCurrency amount=glAccountAmount?if_exists/></fo:block>
      </fo:table-cell>
    </fo:table-row>
    <#list childGlAccounts as childGl>
      <@glAccountBalances childGl level+1/>
    </#list>
  </#macro>


  <script>
      function displayChildren(event, glAccountId, level) {
          var innerHtml = '';
          new Ajax.Request('sub?glAccountId=' + glAccountId + '&amp;level=' + level, {
              asynchronous: false,
              onSuccess: function (transport) {
                  innerHtml = transport.responseText;
              }
          });
          $(glAccountId).insert({after: innerHtml});
          Event.stop(event);
      }
  </script>

</#escape>