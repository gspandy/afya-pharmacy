/*
 * Copyright (c) 2006 - 2008 Open Source Strategies, Inc.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the Honest Public License.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Honest Public License for more details.
 * 
 * You should have received a copy of the Honest Public License
 * along with this program; if not, write to Funambol,
 * 643 Bair Island Road, Suite 305 - Redwood City, CA 94063, USA
 */

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import java.util.*;

// possible fields we're searching by
groupName = parameters.get("groupName");

// trim text input
if (groupName != null) groupName = groupName.trim();

// normalize empty fields to null
if ("".equals(groupName)) groupName = null;

// construct search conditions
searchConditions = new ArrayList();
if (groupName != null) {
    searchConditions.add(EntityCondition.makeCondition("groupName", EntityOperator.LIKE, "%" + groupName + "%"));
}
searchConditions.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "ACCOUNT_TEAM"));
searchConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PARTY_DISABLED"));
searchConditionList = EntityCondition.makeCondition(searchConditions, EntityOperator.AND);

fields = new HashSet();
fields.add("partyId");
fields.add("groupName");
fields.add("partyGroupComments");

listIt = delegator.find("PartyRoleAndPartyDetail", searchConditionList, null, 
        fields,  // fields to select
        UtilMisc.toList("groupName"), // fields to order by
        // the first true here is for "specifyTypeAndConcur"
        // the second true is for a distinct select.  Apparently this is the only way the entity engine can do a distinct query
        new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true));

context.put("teams", listIt);