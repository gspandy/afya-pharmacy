import org.ofbiz.base.util.UtilMisc;

// fetch the list possible account sources
sources = delegator.findByAnd("DataSource", UtilMisc.toMap("dataSourceTypeId", "LEAD_GENERATION"), UtilMisc.toList("description"));
context.put("sourcesList", sources);


//fetch the list possible account sources
industries = delegator.findByAnd("Enumeration", UtilMisc.toMap("enumTypeId", "PARTY_INDUSTRY"), UtilMisc.toList("description"));
context.put("industriesList", industries);

// fetch the list of possible marketing campaigns
marketingCampaigns = delegator.findByAnd("MarketingCampaign", UtilMisc.toMap("statusId", "MKTG_CAMP_INPROGRESS"), UtilMisc.toList("campaignName"));
context.put("marketingCampaignsList", marketingCampaigns);

// fetch the list of possible teams
teams = delegator.findByAnd("PartyRoleAndPartyDetail", UtilMisc.toMap("roleTypeId", "ACCOUNT_TEAM"), UtilMisc.toList("groupName"));
context.put("teamsList", teams);

