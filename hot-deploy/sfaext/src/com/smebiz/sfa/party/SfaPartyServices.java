package com.smebiz.sfa.party;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.model.ModelField;
import org.ofbiz.entity.model.ModelRelation;
import org.ofbiz.entity.model.ModelViewEntity;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.smebiz.sfa.security.CrmsfaSecurity;
import com.smebiz.sfa.util.SfaEntityHelper;

public class SfaPartyServices {

	public static final String module = SfaPartyServices.class.getName();
	public static final String resource = "CRMSFAUiLabels";

	/** Merging function for two unique GenericValues. */
	private static void mergeTwoValues(String entityName, Map fromKeys,
			Map toKeys, GenericDelegator delegator)
			throws GenericEntityException {
		GenericValue from = delegator.findByPrimaryKey(entityName, fromKeys);
		GenericValue to = delegator.findByPrimaryKey(entityName, toKeys);
		if (from == null || to == null)
			return;
		from.setNonPKFields(to.getAllFields());
		to.setNonPKFields(from.getAllFields());
		to.store();
	}

	public static Map validateMergeCrmParties(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		Security security = dctx.getSecurity();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		String partyIdFrom = (String) context.get("partyIdFrom");
		String partyIdTo = (String) context.get("partyIdTo");
		try {
			// ensure that merging parties are the same type (ACCOUNT, CONTACT,
			// PROSPECT)
			String fromPartyType = com.smebiz.sfa.party.SfaPartyHelper
					.getFirstValidInternalPartyRoleTypeId(partyIdFrom,
							delegator);
			String toPartyType = com.smebiz.sfa.party.SfaPartyHelper
					.getFirstValidInternalPartyRoleTypeId(partyIdTo, delegator);

			if ((fromPartyType == null) || !fromPartyType.equals(toPartyType)) {
				String errorMessage = "Cannot merge party [" + partyIdFrom
						+ "] of type [" + fromPartyType + "] with party ["
						+ partyIdTo + "] of type [" + toPartyType
						+ "] because they are not the same type.";
				errorMessage += UtilProperties.getMessage(resource,
						"CrmErrorMergePartiesFail", locale);
				return ServiceUtil.returnError(errorMessage);
			}
			if (partyIdFrom.equals(partyIdTo)) {
				String errorMessage = "Cannot merge party [" + partyIdFrom
						+ "] to itself!";
				errorMessage += UtilProperties.getMessage(resource,
						"CrmErrorMergeParties", locale);
				return ServiceUtil.returnError(errorMessage);
			}

			// convert ACCOUNT/CONTACT/LEAD to ACCOUNT/CONTACT/LEAD
			// String partyTypeCrm = (fromPartyType.equals("PROSPECT") ? "LEAD"
			// : fromPartyType);
			String partyTypeCrm = fromPartyType;

			// make sure userLogin has CRMSFA_${partyTypeCrm}_UPDATE permission
			// for both parties TODO: and delete, check security config
			if (!CrmsfaSecurity.hasPartyRelationSecurity(security, "CRMSFA_"
					+ partyTypeCrm, "_UPDATE", userLogin, partyIdFrom)
					|| !CrmsfaSecurity.hasPartyRelationSecurity(security,
							"CRMSFA_" + partyTypeCrm, "_UPDATE", userLogin,
							partyIdTo)) {
				String errorMessage = UtilProperties.getMessage(resource,
						"CrmErrorPermissionDenied" + ": CRMSFA_" + partyTypeCrm
								+ "_UPDATE", locale);
				return ServiceUtil.returnError(errorMessage);
			}
		} catch (GenericEntityException e) {
			String errorMessage = UtilProperties.getMessage(resource,
					"CrmErrorMergePartiesFail", locale);
			return ServiceUtil.returnError(errorMessage);
		}
		return ServiceUtil.returnSuccess();
	}

	// TODO: avoid merging parties with particular statuses. implement with an
	// array
	public static Map mergeCrmParties(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		String partyIdFrom = (String) context.get("partyIdFrom");
		String partyIdTo = (String) context.get("partyIdTo");
		String validate = (String) context.get("validate");

		try {

			Map serviceResults = null;

			if (!(("N").equalsIgnoreCase(validate))) {
				// validate again
				serviceResults = dispatcher.runSync(
						"crmsfa.validateMergeCrmParties", UtilMisc.toMap(
								"userLogin", userLogin, "partyIdFrom",
								partyIdFrom, "partyIdTo", partyIdTo));
				if (ServiceUtil.isError(serviceResults)) {
					return serviceResults;
				}
			}

			// merge the party objects
			/*
			 * mergeTwoValues("PartySupplementalData", UtilMisc.toMap("partyId",
			 * partyIdFrom), UtilMisc.toMap("partyId", partyIdTo), delegator);
			 */
			mergeTwoValues("Person", UtilMisc.toMap("partyId", partyIdFrom),
					UtilMisc.toMap("partyId", partyIdTo), delegator);
			mergeTwoValues("PartyGroup",
					UtilMisc.toMap("partyId", partyIdFrom), UtilMisc.toMap(
							"partyId", partyIdTo), delegator);
			mergeTwoValues("Party", UtilMisc.toMap("partyId", partyIdFrom),
					UtilMisc.toMap("partyId", partyIdTo), delegator);

			List toRemove = new ArrayList();

			EntityCondition entityCondition = EntityCondition
					.makeCondition(UtilMisc.toMap("partyIdTo", partyIdFrom));

			List<GenericValue> partyIdToEntities = delegator.findList(
					"PartyRelationship", entityCondition, null, null, null,
					false);
			for (GenericValue entity : partyIdToEntities) {
				entity.remove();
			}
			// Get a list of entities related to the Party entity, in descending
			// order by relation
			List relatedEntities = getRelatedEntities("Party", delegator);

			// Go through the related entities in forward order - this makes
			// sure that parent records are created before child records
			Iterator reit = relatedEntities.iterator();
			while (reit.hasNext()) {
				ModelEntity modelEntity = (ModelEntity) reit.next();

				// Examine each field of the entity
				Iterator mefit = modelEntity.getFieldsIterator();
				while (mefit.hasNext()) {
					ModelField modelField = (ModelField) mefit.next();
					if (modelField.getName().matches(".*[pP]artyId.*")) {

						// If the name of the field has something to do with a
						// partyId, get all the existing records from that
						// entity which have the
						// partyIdFrom in that particular field
						List existingRecords = delegator.findByAnd(modelEntity
								.getEntityName(), UtilMisc.toMap(modelField
								.getName(), partyIdFrom));
						if (existingRecords.size() > 0) {
							Iterator eit = existingRecords.iterator();
							while (eit.hasNext()) {
								GenericValue existingRecord = (GenericValue) eit
										.next();
								if (modelField.getIsPk()) {

									// If the partyId field is part of a primary
									// key, create a new record with the
									// partyIdTo in place of the partyIdFrom
									GenericValue newRecord = delegator
											.makeValue(modelEntity
													.getEntityName(),
													existingRecord
															.getAllFields());
									newRecord.set(modelField.getName(),
											partyIdTo);

									// Create the new record if a record with
									// the same primary key doesn't already
									// exist
									if (delegator.findOne(modelEntity
											.getEntityName(),false,newRecord.getPrimaryKey()) == null) {
										newRecord.create();
									}

									// Add the old record to the list of records
									// to remove
									toRemove.add(existingRecord);
								} else {

									// If the partyId field is not party of a
									// primary key, simply update the field with
									// the new value and store it
									existingRecord.set(modelField.getName(),
											partyIdTo);
									existingRecord.store();
								}
							}
						}
					}
				}
			}

			// Go through the list of records to remove in REVERSE order! Since
			// they're still in descending order of relation to the Party
			// entity, reversing makes sure that child records are removed
			// before parent records, all the way back to the original Party
			// record
			ListIterator rit = toRemove.listIterator(toRemove.size());
			while (rit.hasPrevious()) {
				GenericValue existingRecord = (GenericValue) rit.previous();
				existingRecord.remove();
			}

		} catch (GenericServiceException e) {
			String error = UtilProperties.getMessage(resource,
					"CrmErrorMergePartiesFail", locale);
			return ServiceUtil.returnError(error);
		} catch (GenericEntityException e) {
			String error = UtilProperties.getMessage(resource,
					"CrmErrorMergePartiesFail", locale);
			return ServiceUtil.returnError(error);
		}
		return ServiceUtil.returnSuccess();
	}

	public static Map findCrmPartiesForMerge(DispatchContext dctx, Map context) {
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");

		try {

			Map fullMerge = new HashMap();

			// Find parties that already have an entry in PartyMergeCandidates
			List existingMergeCandidates = delegator
					.findList("PartyMergeCandidates",null,null,null,null,false);
			List existingMergeCandidateFromParties = EntityUtil
					.getFieldListFromEntityList(existingMergeCandidates,
							"partyIdFrom", true);

			// Find parties with similar postal addresses
			List postalAddressMergeCandidateConditions = new ArrayList();
			postalAddressMergeCandidateConditions.add(EntityCondition.makeCondition(
					"address1", EntityOperator.NOT_EQUAL, null));
			postalAddressMergeCandidateConditions.add(EntityCondition.makeCondition(
					"postalCode", EntityOperator.NOT_EQUAL, null));
			postalAddressMergeCandidateConditions.add(EntityCondition.makeCondition(
					"countryGeoId", EntityOperator.NOT_EQUAL, null));
			postalAddressMergeCandidateConditions.add(EntityCondition.makeCondition(
					"contactMechId", EntityOperator.NOT_EQUAL, null));

			TransactionUtil.begin();
			EntityListIterator partyAndPostalAddresses = delegator
					.find("PartyAndPostalAddress",
							EntityCondition.makeCondition(
									postalAddressMergeCandidateConditions,
									EntityOperator.AND), null, null,null,null);
			TransactionUtil.commit();

			// Iterate through the partyAndPostalAddress records, constructing a
			// four-level-deep map with countryGeoId as the first level,
			// postalCode as the second,
			// the numeric portions of address1 as the third, and
			// partyId->GenericValue as the fourth.
			Map postalAddressMerge = new TreeMap();
			GenericValue partyAndPostalAddress = null;
			while ((partyAndPostalAddress = partyAndPostalAddresses.next()) != null) {

				// We're comparing only the alphanumeric portions of address1 -
				// ignoring punctuation, symbols and spaces
				String address = partyAndPostalAddress.getString("address1")
						.toUpperCase().replaceAll("[^0-9A-Z]", "");
				if (!UtilValidate.isEmpty(address)) {
					String partyId = partyAndPostalAddress.getString("partyId");

					// Ignore any parties that are already in some state of
					// merge candidacy
					if (existingMergeCandidateFromParties.contains(partyId)) {
						continue;
					}

					String countryGeoId = partyAndPostalAddress.getString(
							"countryGeoId").toUpperCase();
					String postalCode = partyAndPostalAddress.getString(
							"postalCode").toUpperCase();
					Map countryMap = (Map) postalAddressMerge.get(countryGeoId);
					if (countryMap == null) {
						countryMap = new TreeMap();
						postalAddressMerge.put(countryGeoId, countryMap);
					}
					Map postalCodeMap = (Map) countryMap.get(postalCode);
					if (postalCodeMap == null) {
						postalCodeMap = new TreeMap();
						countryMap.put(postalCode, postalCodeMap);
					}
					Map partyIds = (Map) postalCodeMap.get(address);
					if (partyIds == null) {
						partyIds = new TreeMap();
						postalCodeMap.put(address, partyIds);
					}
					partyIds.put(partyId, partyAndPostalAddress);
				}
			}
			partyAndPostalAddresses.close();

			// Iterate through the resolved postal address map, checking which
			// of the groups of similar addresses have more than one party
			// associated with them.
			Iterator pit = postalAddressMerge.keySet().iterator();
			while (pit.hasNext()) {
				String countryGeoId = (String) pit.next();
				Map postalCodes = (Map) postalAddressMerge.get(countryGeoId);
				Iterator pcit = postalCodes.keySet().iterator();
				while (pcit.hasNext()) {
					String postalCode = (String) pcit.next();
					Map addresses = (Map) postalCodes.get(postalCode);
					Iterator ait = addresses.keySet().iterator();
					while (ait.hasNext()) {
						String address = (String) ait.next();
						TreeMap parties = (TreeMap) addresses.get(address);
						if (parties.size() > 1) {

							// If so, take the first party and use it as the map
							// key in fullMerge
							String toPartyId = (String) parties.firstKey();
							GenericValue toContactMech = (GenericValue) parties
									.get(toPartyId);
							parties.remove(parties.firstKey());

							// The fullMerge value is the rest of the stuff
							fullMerge.put(toPartyId, UtilMisc.toMap(
									"toContactMech", toContactMech,
									"partiesToMerge", parties));

							// Add all the parties which are going to be merged
							// with the toParty into the
							// existingMergeCandidateFromParties list,
							// so that we don't try to merge them again when
							// examining email addresses
							existingMergeCandidateFromParties.addAll(parties
									.keySet());
						}
					}
				}
			}

			// Find parties with similar email addresses
			List emailAddressMergeCandidateConditions = new ArrayList();
			emailAddressMergeCandidateConditions
					.add(EntityCondition.makeCondition("contactMechTypeId",
							EntityOperator.EQUALS, "EMAIL_ADDRESS"));
			emailAddressMergeCandidateConditions.add(EntityCondition.makeCondition(
					"infoString", EntityOperator.NOT_EQUAL, null));

			TransactionUtil.begin();
			EntityListIterator partyAndEmailAddresses = delegator
					.find("PartyAndContactMech",
							EntityCondition.makeCondition(
									emailAddressMergeCandidateConditions,
									EntityOperator.AND), null, null,null,null);
			TransactionUtil.commit();

			// Iterate through the partyAndContactMech records, constructing an
			// address->(map of partyId->genericValue)
			Map emailAddressMerge = new TreeMap();
			GenericValue partyAndEmailAddress = null;
			while ((partyAndEmailAddress = partyAndEmailAddresses.next()) != null) {
				String address = partyAndEmailAddress.getString("infoString")
						.toUpperCase().replaceAll(" ", "");
				if (!UtilValidate.isEmpty(address)) {
					String partyId = partyAndEmailAddress.getString("partyId");

					// Ignore any parties that are already in some state of
					// merge candidacy
					if (existingMergeCandidateFromParties.contains(partyId)) {
						continue;
					}

					Map partyIds = (Map) emailAddressMerge.get(address);
					if (partyIds == null) {
						partyIds = new TreeMap();
						emailAddressMerge.put(address, partyIds);
					}
					partyIds.put(partyId, partyAndEmailAddress);
				}
			}
			partyAndEmailAddresses.close();

			// Iterate through the resolved email address map, checking which of
			// the groups of similar addresses have more than one party
			// associated with them.
			Iterator eit = emailAddressMerge.keySet().iterator();
			while (eit.hasNext()) {
				String address = (String) eit.next();
				TreeMap parties = (TreeMap) emailAddressMerge.get(address);
				if (parties.size() > 1) {
					String toPartyId = (String) parties.firstKey();
					GenericValue toContactMech = (GenericValue) parties
							.get(toPartyId);
					parties.remove(parties.firstKey());
					fullMerge.put(toPartyId, UtilMisc.toMap("toContactMech",
							toContactMech, "partiesToMerge", parties));
				}
			}

			// Iterate through the full set of groups of parties with similar
			// contact info
			Iterator fit = fullMerge.keySet().iterator();
			while (fit.hasNext()) {

				// Use the key as the toPartyId
				String toPartyId = (String) fit.next();

				// Get the name of the toParty
				String toPartyName = org.ofbiz.party.party.PartyHelper
						.getPartyName(delegator, toPartyId, false);
				String toPartyNameDisplay = toPartyName + " (" + toPartyId
						+ ")";
				Map partiesMergeMap = (Map) fullMerge.get(toPartyId);
				GenericValue toContactMech = (GenericValue) partiesMergeMap
						.get("toContactMech");
				String toPartyTypeId = toContactMech.getString("partyTypeId");

				// Format the postal/email address as a string
				String toContactMechString = "";
				if ("POSTAL_ADDRESS".equals(toContactMech
						.getString("contactMechTypeId"))) {
					toContactMechString += toContactMech.getString("address1");
					if (!UtilValidate.isEmpty(toContactMech.get("address2")))
						toContactMechString += " "
								+ toContactMech.get("address2");
					if (!UtilValidate.isEmpty(toContactMech.get("city")))
						toContactMechString += " " + toContactMech.get("city");
					if (!UtilValidate.isEmpty(toContactMech
							.get("stateProvinceGeoId")))
						toContactMechString += ", "
								+ toContactMech.get("stateProvinceGeoId");
					toContactMechString += " "
							+ toContactMech.getString("postalCode");
					toContactMechString += " "
							+ toContactMech.getString("countryGeoId");
				} else if ("EMAIL_ADDRESS".equals(toContactMech
						.getString("contactMechTypeId"))) {
					toContactMechString = toContactMech.getString("infoString");
				}

				// Get a map of all the other parties which will be merged with
				// the toParty
				Map partiesToMerge = (Map) partiesMergeMap
						.get("partiesToMerge");

				// Iterate through them
				Iterator pmit = partiesToMerge.keySet().iterator();
				while (pmit.hasNext()) {

					// Get the fromPartyId and name, and format the address
					String fromPartyId = (String) pmit.next();
					String fromPartyName = org.ofbiz.party.party.PartyHelper
							.getPartyName(delegator, fromPartyId, false);
					String fromPartyNameDisplay = fromPartyName + " ("
							+ fromPartyId + ")";
					GenericValue fromContactMech = (GenericValue) partiesToMerge
							.get(fromPartyId);
					String fromPartyTypeId = fromContactMech
							.getString("partyTypeId");

					// Ignore parties of different types (eg. PERSON vs.
					// PARTY_GROUP)
					if (!toPartyTypeId.equalsIgnoreCase(fromPartyTypeId)) {
						String skipRationale = UtilProperties.getMessage(
								resource,
								"crmsfa.findCrmPartiesForMerge_skipDueToType",
								UtilMisc.toMap("toPartyName",
										toPartyNameDisplay, "fromPartyName",
										fromPartyNameDisplay), locale);
						GenericValue existingPartyMergeCandidate = delegator
								.findByPrimaryKey("PartyMergeCandidates",
										UtilMisc.toMap("partyIdTo", toPartyId,
												"partyIdFrom", fromPartyId));
						if (existingPartyMergeCandidate != null) {
							existingPartyMergeCandidate.set("doNotMerge", "Y");
							existingPartyMergeCandidate.set("comments",
									skipRationale);
							existingPartyMergeCandidate.store();
						}
						Debug.logInfo(skipRationale, module);
						continue;
					}

					// Ignore parties with different names (considering only
					// alphanumeric characters)
					if (!toPartyName.toUpperCase().replaceAll("[^0-9A-Z]", "")
							.equals(
									fromPartyName.toUpperCase().replaceAll(
											"[^0-9A-Z]", ""))) {
						String skipRationale = UtilProperties.getMessage(
								resource,
								"crmsfa.findCrmPartiesForMerge_skipDueToName",
								UtilMisc.toMap("toPartyName",
										toPartyNameDisplay, "fromPartyName",
										fromPartyNameDisplay), locale);
						GenericValue existingPartyMergeCandidate = delegator
								.findByPrimaryKey("PartyMergeCandidates",
										UtilMisc.toMap("partyIdTo", toPartyId,
												"partyIdFrom", fromPartyId));
						if (existingPartyMergeCandidate != null) {
							existingPartyMergeCandidate.set("doNotMerge", "Y");
							existingPartyMergeCandidate.set("comments",
									skipRationale);
							existingPartyMergeCandidate.store();
						}
						Debug.logInfo(skipRationale, module);
						continue;
					}

					String fromContactMechString = "";
					String mergeRationale = "";
					if ("POSTAL_ADDRESS".equals(fromContactMech
							.getString("contactMechTypeId"))) {
						fromContactMechString += fromContactMech
								.getString("address1");
						if (!UtilValidate.isEmpty(fromContactMech
								.get("address2")))
							fromContactMechString += " "
									+ fromContactMech.get("address2");
						if (!UtilValidate.isEmpty(fromContactMech.get("city")))
							fromContactMechString += " "
									+ fromContactMech.get("city");
						if (!UtilValidate.isEmpty(fromContactMech
								.get("stateProvinceGeoId")))
							fromContactMechString += ", "
									+ fromContactMech.get("stateProvinceGeoId");
						fromContactMechString += " "
								+ fromContactMech.get("postalCode");
						fromContactMechString += " "
								+ fromContactMech.get("countryGeoId");
						mergeRationale = UtilProperties
								.getMessage(
										resource,
										"crmsfa.findCrmPartiesForMerge_mergeRationalePostal",
										UtilMisc.toMap("toPartyName",
												toPartyNameDisplay,
												"toContactMechString",
												toContactMechString,
												"fromPartyName",
												fromPartyNameDisplay,
												"fromContactMechString",
												fromContactMechString), locale);
					} else if ("EMAIL_ADDRESS".equals(fromContactMech
							.getString("contactMechTypeId"))) {
						fromContactMechString = fromContactMech
								.getString("infoString");
						mergeRationale = UtilProperties
								.getMessage(
										resource,
										"crmsfa.findCrmPartiesForMerge_mergeRationaleEmail",
										UtilMisc.toMap("toPartyName",
												toPartyNameDisplay,
												"toContactMechString",
												toContactMechString,
												"fromPartyName",
												fromPartyNameDisplay,
												"fromContactMechString",
												fromContactMechString), locale);
					}
					//To Resolve some other Isuues commented bellow code. Commented By = Nafis
					/* String hasOrderRoles = (delegator
							.findCountByCondition("OrderRole", UtilMisc.toMap(
									"partyId", fromPartyId)) > 0) ? "Y" : "N";
					delegator.create("PartyMergeCandidates", UtilMisc.toMap(
							"partyIdTo", toPartyId, "partyIdFrom", fromPartyId,
							"hasOrderRoles", hasOrderRoles, "mergeRationale",
							mergeRationale), null,null);*/
				}
			}
		} catch (GenericEntityException e) {
			String error = UtilProperties.getMessage(resource,
					"CrmErrorFindPartiesForMergeFail", locale);
			return ServiceUtil.returnError(error);
		}
		return ServiceUtil.returnSuccess();
	}

	private static List getRelatedEntities(String parentEntityName,
			GenericDelegator delegator) {
		ModelEntity parentEntity = delegator.getModelEntity(parentEntityName);

		// Start the recursion
		return getRelatedEntities(new ArrayList(), parentEntity, delegator);
	}

	/**
	 * Recursive method to map relations from a single entity
	 * 
	 * @param relatedEntities
	 *            List of related ModelEntity objects in descending order of
	 *            relation from the parent entity
	 * @param parentEntity
	 *            Root ModelEntity for deriving relations
	 * @param delegator
	 *            GenericDelegator
	 * @return List of ModelEntity objects in descending order of relation from
	 *         the original parent entity
	 */
	private static List getRelatedEntities(List relatedEntities,
			ModelEntity parentEntity, GenericDelegator delegator) {

		// Do nothing if the parent entity has already been mapped
		if (relatedEntities.contains(parentEntity))
			return relatedEntities;

		relatedEntities.add(parentEntity);
		Iterator reit = parentEntity.getRelationsIterator();

		// Recurse for each relation from the parent entity that doesn't refer
		// to a view-entity
		while (reit.hasNext()) {
			ModelRelation relation = (ModelRelation) reit.next();
			String relatedEntityName = relation.getRelEntityName();
			ModelEntity relatedEntity = delegator
					.getModelEntity(relatedEntityName);
			if (!(relatedEntity instanceof ModelViewEntity)) {
				relatedEntities = getRelatedEntities(relatedEntities,
						relatedEntity, delegator);
			}
		}
		return relatedEntities;
	}

	public static Map findAccounts(DispatchContext dctx, Map context) {
		Map results = FastMap.newInstance();
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");

		try {
			List<GenericValue> accounts = SfaEntityHelper.getAccounts(partyId,
					true, delegator);
			results = dispatcher.runSync("findParty", context);

			List<GenericValue> searchResults = (List<GenericValue>) results
					.get("partyList");

			if (searchResults != null && searchResults.size() > 0) {
				results.put("partyList", SfaPartyHelper.filterResults(
						searchResults, accounts));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	public static Map findLeads(DispatchContext dctx, Map context) {
		Map results = FastMap.newInstance();
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");

		try {
			List<GenericValue> leads = SfaEntityHelper.getLeads(partyId, true,
					delegator);
			results = dispatcher.runSync("findParty", context);

			List<GenericValue> searchResults = (List<GenericValue>) results
					.get("partyList");

			if (searchResults != null && searchResults.size() > 0) {
				results.put("partyList", SfaPartyHelper.filterResults(
						searchResults, leads));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	public static Map findContacts(DispatchContext dctx, Map context) {
		Map results = FastMap.newInstance();
		GenericDelegator delegator = (GenericDelegator) (GenericDelegator) dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();

		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = userLogin.getString("partyId");

		try {
			List<GenericValue> contacts = SfaEntityHelper.getContacts(partyId,
					true, delegator);
			results = dispatcher.runSync("findParty", context);

			List<GenericValue> searchResults = (List<GenericValue>) results
					.get("partyList");

			if (searchResults != null && searchResults.size() > 0) {
				results.put("partyList", SfaPartyHelper.filterResults(
						searchResults, contacts));
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GenericServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

}