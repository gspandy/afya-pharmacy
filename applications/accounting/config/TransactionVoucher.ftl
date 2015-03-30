<#setting number_format="0.00">

<TALLYMESSAGE>
	<#assign voucherType=orderHeader.voucherType>
	<VOUCHER VCHTYPE="${orderHeader.voucherType?if_exists}" ACTION="Create">
		<DATE>${orderHeader.orderDate?if_exists?string("yyyyMMdd")}</DATE>
		<#if orderHeader.formToIssueDate?exists>
		<CSTFORMISSUEDATE>${orderHeader.formToIssueDate?string("yyyyMMdd")}</CSTFORMISSUEDATE>
	</#if>
	<#if "Sales"=voucherType>
	 <#if orderHeader.formToIssueDate?exists>
	  <CSTFORMRECVDATE>${orderHeader.formToIssueDate?string("yyyyMMdd")}</CSTFORMRECVDATE>
	 </#if>
	</#if>
	<VOUCHERTYPENAME>${orderHeader.voucherType?if_exists}</VOUCHERTYPENAME>
	<REFERENCE>${orderHeader.orderName?if_exists?html}</REFERENCE>
	<#if "Sales"=voucherType>
		<PARTYLEDGERNAME>${orderHeader.billToParty?if_exists?html}</PARTYLEDGERNAME>
		<#else>
		<PARTYLEDGERNAME>${orderHeader.billFromParty?if_exists?html}</PARTYLEDGERNAME>
	</#if>
	<#if "Purchase"=voucherType>
		<#if orderHeader.formToIssue?exists>
		<CSTFORMISSUETYPE>${orderHeader.formToIssue?if_exists}</CSTFORMISSUETYPE>
		<CSTFORMISSUENUMBER>${orderHeader.formToIssueFormNo?if_exists}</CSTFORMISSUENUMBER>
		<CSTFORMISSUESERIESNUM>${orderHeader.formToIssueSeriesNo?if_exists}</CSTFORMISSUESERIESNUM>
		</#if>
		<#if orderHeader.formToReceive?exists>
		<CSTFORMRECVTYPE>${orderHeader.formToReceive?if_exists}</CSTFORMRECVTYPE>
		<CSTFORMRECVNUMBER>${orderHeader.formToReceiveFormNo?if_exists}</CSTFORMRECVNUMBER>
		<CSTFORMRECVSERIESNUM>${orderHeader.formToReceiveSeriesNo?if_exists}</CSTFORMRECVSERIESNUM>
		</#if>
	</#if>
	<#if "Sales"=voucherType>
		<#if orderHeader.formToIssue?exists>
		<CSTFORMRECVTYPE>${orderHeader.formToIssue?if_exists}</CSTFORMRECVTYPE>
		<CSTFORMRECVNUMBER>${orderHeader.formToIssueFormNo?if_exists}</CSTFORMRECVNUMBER>
		<CSTFORMRECVSERIESNUM>${orderHeader.formToIssueSeriesNo?if_exists}</CSTFORMRECVSERIESNUM>
		</#if>
		<#if orderHeader.formToReceive?exists>
		<CSTFORMISSUETYPE>${orderHeader.formToReceive?if_exists}</CSTFORMISSUETYPE>
		<CSTFORMISSUENUMBER>${orderHeader.formToReceiveFormNo?if_exists}</CSTFORMISSUENUMBER>
		<CSTFORMISSUESERIESNUM>${orderHeader.formToReceiveSeriesNo?if_exists}</CSTFORMISSUESERIESNUM>
		</#if>
	</#if>
	<FBTPAYMENTTYPE>Default</FBTPAYMENTTYPE>
	<PERSISTEDVIEW>Invoice Voucher View</PERSISTEDVIEW>
	<VCHGSTCLASS />
	<DIFFACTUALQTY>No</DIFFACTUALQTY>
	<AUDITED>No</AUDITED>
	<FORJOBCOSTING>No</FORJOBCOSTING>
	<ISOPTIONAL>No</ISOPTIONAL>
	<EFFECTIVEDATE>${orderHeader.orderDate?string("yyyyMMdd")}</EFFECTIVEDATE>
	<ISFORJOBWORKIN>No</ISFORJOBWORKIN>
	<ALLOWCONSUMPTION>No</ALLOWCONSUMPTION>
	<USEFORINTEREST>No</USEFORINTEREST>
	<USEFORGAINLOSS>No</USEFORGAINLOSS>
	<USEFORGODOWNTRANSFER>No</USEFORGODOWNTRANSFER>
	<USEFORCOMPOUND>No</USEFORCOMPOUND>
	<EXCISEOPENING>No</EXCISEOPENING>
	<USEFORFINALPRODUCTION>No</USEFORFINALPRODUCTION>
	<ISCANCELLED>No</ISCANCELLED>
	<HASCASHFLOW>No</HASCASHFLOW>
	<ISPOSTDATED>No</ISPOSTDATED>
	<USETRACKINGNUMBER>No</USETRACKINGNUMBER>
	<ISINVOICE>Yes</ISINVOICE>
	<MFGJOURNAL>No</MFGJOURNAL>
	<HASDISCOUNTS>No</HASDISCOUNTS>
	<ASPAYSLIP>No</ASPAYSLIP>
	<ISCOSTCENTRE>No</ISCOSTCENTRE>
	<ISSTXNONREALIZEDVCH>No</ISSTXNONREALIZEDVCH>
	<ISDELETED>No</ISDELETED>
	<ASORIGINAL>No</ASORIGINAL>
	<VCHISFROMSYNC>No</VCHISFROMSYNC>
	<AUDITENTRIES.LIST>
	</AUDITENTRIES.LIST>
	<INVOICEDELNOTES.LIST>
	</INVOICEDELNOTES.LIST>
	<INVOICEORDERLIST.LIST>
	</INVOICEORDERLIST.LIST>
	<INVOICEINDENTLIST.LIST>
	</INVOICEINDENTLIST.LIST>
	<ATTENDANCEENTRIES.LIST>
    </ATTENDANCEENTRIES.LIST>
	<#list acctgTransAndEntries as trans>
		<LEDGERENTRIES.LIST>
			<LEDGERNAME>${trans.accountName?if_exists?html}</LEDGERNAME>
			<GSTCLASS />
			<ISDEEMEDPOSITIVE><#if "D"=trans.debitCreditFlag>No<#else>Yes</#if></ISDEEMEDPOSITIVE>
			<LEDGERFROMITEM>No</LEDGERFROMITEM>
			<REMOVEZEROENTRIES>No</REMOVEZEROENTRIES>
			<ISPARTYLEDGER>No</ISPARTYLEDGER>
			<ISLASTDEEMEDPOSITIVE><#if "D"=trans.debitCreditFlag>No<#else>Yes</#if></ISLASTDEEMEDPOSITIVE>
			<#if "Sales"=voucherType && trans.debitCreditFlag="D">
			<AMOUNT>${trans.origAmount}</AMOUNT>
			<#elseif "Purchase"=voucherType && trans.debitCreditFlag="C">
			<AMOUNT>${trans.origAmount}</AMOUNT>
			<#else>
			<AMOUNT>${trans.origAmount*-1}</AMOUNT>
			</#if>
			<BANKALLOCATIONS.LIST></BANKALLOCATIONS.LIST>
			<INTERESTCOLLECTION.LIST></INTERESTCOLLECTION.LIST>
			<#if "Sales"=voucherType && trans.debitCreditFlag="D">
			<BILLALLOCATIONS.LIST>
				<NAME>${orderHeader.orderName?if_exists?html}</NAME>
				<BILLTYPE>New Ref</BILLTYPE>
				<TDSDEDUCTEEISSPECIALRATE>No</TDSDEDUCTEEISSPECIALRATE>
				<#if "D"=trans.debitCreditFlag>
				<AMOUNT>${trans.origAmount}</AMOUNT>
				<#else>
				<AMOUNT>${trans.origAmount*-1}</AMOUNT>
				</#if>
				<INTERESTCOLLECTION.LIST></INTERESTCOLLECTION.LIST>
			</BILLALLOCATIONS.LIST>
			<#elseif "Purchase"=voucherType && trans.debitCreditFlag="C">
			<BILLALLOCATIONS.LIST>
				<NAME>${orderHeader.orderName?if_exists?html}</NAME>
				<BILLTYPE>New Ref</BILLTYPE>
				<TDSDEDUCTEEISSPECIALRATE>No</TDSDEDUCTEEISSPECIALRATE>
				<#if "C"=trans.debitCreditFlag>
				<AMOUNT>${trans.origAmount}</AMOUNT>
				<#else>
				<AMOUNT>${trans.origAmount*-1}</AMOUNT>
				</#if>
				<INTERESTCOLLECTION.LIST></INTERESTCOLLECTION.LIST>
			</BILLALLOCATIONS.LIST>
			<#else>
				<BILLALLOCATIONS.LIST></BILLALLOCATIONS.LIST>
			</#if>
			<INTERESTCOLLECTION.LIST>
			</INTERESTCOLLECTION.LIST>
			<AUDITENTRIES.LIST>
			</AUDITENTRIES.LIST>
			<TAXBILLALLOCATIONS.LIST>
			</TAXBILLALLOCATIONS.LIST>
			<TAXOBJECTALLOCATIONS.LIST>
			</TAXOBJECTALLOCATIONS.LIST>
			<TDSEXPENSEALLOCATIONS.LIST>
			</TDSEXPENSEALLOCATIONS.LIST>
			<VATSTATUTORYDETAILS.LIST>
			</VATSTATUTORYDETAILS.LIST>
			<COSTTRACKALLOCATIONS.LIST>
			</COSTTRACKALLOCATIONS.LIST>
		</LEDGERENTRIES.LIST>
		</#list>
	</VOUCHER>
	</TALLYMESSAGE>