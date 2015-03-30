Map tallyCompany = tallyResponse.companyMap;
context.company=[:];
if(tallyCompany)
context.company=["groupName":tallyCompany.companyName,
				"USER_ADDRESS1":tallyCompany.address_1,"USER_ADDRESS2":tallyCompany.address_2,
				"USER_COUNTRY":"IND","USER_STATE":tallyCompany.USER_STATE,
				"USER_POSTAL_CODE":tallyCompany.USER_POSTAL_CODE,
				"USER_WORK_CONTACT":tallyCompany.USER_WORK_CONTACT,
				"USER_EMAIL":tallyCompany.USER_EMAIL,
				"USER_COUNTRY":tallyCompany.USER_COUNTRY,
				"interstateServiceTaxNumber":tallyCompany.interstateServiceTaxNumber,
				"salesTaxNumber":tallyCompany.salesTaxNumber,
				"incomeTaxNumber":tallyCompany.incomeTaxNumber
				];