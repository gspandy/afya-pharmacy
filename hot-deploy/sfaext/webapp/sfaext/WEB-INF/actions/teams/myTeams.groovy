import com.smebiz.sfa.teams.TeamHelper;


partyId = parameters.get("partyId");
delegator = request.getAttribute("delegator");

teams = TeamHelper.getTeamsForPartyId(partyId,delegator);
context.put("teams",teams);