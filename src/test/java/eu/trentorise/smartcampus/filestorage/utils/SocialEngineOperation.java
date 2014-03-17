/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.smartcampus.filestorage.utils;

import it.unitn.disi.sweb.webapi.client.smartcampus.SCWebApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import eu.trentorise.smartcampus.common.SemanticHelper;
import eu.trentorise.smartcampus.social.model.EntityType;

//@Service
public class SocialEngineOperation {

	private static final Logger logger = Logger
			.getLogger(SocialEngineOperation.class);

	@org.springframework.beans.factory.annotation.Value("${smartcampus.vas.web.socialengine.host}")
	private String seHost;
	@org.springframework.beans.factory.annotation.Value("${smartcampus.vas.web.socialengine.port}")
	private int sePort;

	private static final String knownledgeBase = "uk";

	SCWebApiClient client = null;

	@PostConstruct
	@SuppressWarnings("unused")
	private void init() {
		client = SCWebApiClient.getInstance(Locale.ENGLISH, seHost, sePort);
	}

	public User createUser() throws WebApiException {
		// client = SCWebApiClient.getInstance(Locale.ENGLISH, seHost, sePort);
		EntityBase eb = new EntityBase();
		eb.setLabel("TEST_SC_EB_" + System.currentTimeMillis());
		Long ebId = client.create(eb);
		// Re-read to get the ID of the default KB
		eb = client.readEntityBase(ebId);
		logger.info("Created an entity base " + eb.getLabel() + " with ID "
				+ ebId);
		logger.info("Creating an entity...");
		EntityType person = client.readEntityType("person", eb.getKbLabel());
		Entity entity = new Entity();
		entity.setEntityBase(eb);
		entity.setEtype(person);
		Long eid = client.create(entity);
		logger.info("Created entity with ID " + eid);
		logger.info("Creating a user...");
		User user = new User();
		user.setName("Test user " + System.currentTimeMillis());
		user.setEntityBaseId(eb.getId());
		user.setPersonEntityId(eid);
		long id = client.create(user);
		logger.info("New user's ID: " + id);
		return client.readUser(id);
	}

	public void deleteUser(long id) throws WebApiException {

		User u = client.readUser(id);
		if (u != null) {
			client.deleteEntity(u.getPersonEntityId());
			client.deleteEntityBase(u.getEntityBaseId());
			if (client.deleteUser(id)) {
				logger.info("Deleted user with ID " + id);
			}
		}
	}

	public void shareEntityWith(long eid, long owner, long addressee)
			throws WebApiException {
		SemanticHelper.shareEntityWith(client, eid, owner, addressee);
	}

	public Entity createEntity(
			eu.trentorise.smartcampus.social.model.User owner, EntityTypes type)
			throws WebApiException {
		User socialUser = client.readUser(owner.getSocialId());
		return createEntity(socialUser.getEntityBaseId(), type);
	}

	public Entity createEntity(long ebid, EntityTypes type)
			throws WebApiException {

		if (type == null) {
			throw new IllegalArgumentException("type null");
		}
		logger.info("Creating an entity base...");
		EntityBase eb1 = client.readEntityBase(ebid);
		EntityType et = client
				.readEntityType(type.toString(), eb1.getKbLabel());

		Entity social = new Entity();
		social.setEntityBase(eb1);
		social.setEtype(et);

		// Entity related = new Entity();
		// related.setEntityBase(eb1);
		// related.setEtype(et);
		// long relatedId = client.create(related);

		List<Attribute> attrs = new ArrayList<Attribute>();
		List<Value> values = new ArrayList<Value>();
		Value v = new Value();
		// String tag attribute
		v.setType(DataType.STRING);
		v.setStringValue("This is a text tag");
		values.add(v);
		Attribute a = new Attribute();
		a.setAttributeDefinition(et.getAttributeDefByName("text"));
		a.setValues(values);
		attrs.add(a);

		// // Entity name
		values = new ArrayList<Value>();
		v = new Value();
		v.setType(DataType.STRING);
		v.setStringValue(type.toString());
		values.add(v);
		a = new Attribute();
		a.setAttributeDefinition(et.getAttributeDefByName("name"));
		a.setValues(values);
		attrs.add(a);

		// Entity description
		// values = new ArrayList<Value>();
		// v = new Value();
		// v.setType(DataType.STRING);
		// v.setStringValue("Event description");
		// values.add(v);
		// a = new Attribute();
		// a.setAttributeDefinition(et.getAttributeDefByName("description"));
		// a.setValues(values);
		// attrs.add(a);

		// Entity tag attribute
		// values = new ArrayList<Value>();
		// v = new Value();
		// v.setType(DataType.RELATION);
		// v.setRelationEntity(client.readEntity(relatedId, null));
		// values.add(v);
		// a = new Attribute();
		// a.setAttributeDefinition(et.getAttributeDefByName("entity"));
		// a.setValues(values);
		// attrs.add(a);

		// Semantic tag attribute
		values = new ArrayList<Value>();
		v = new Value();
		v.setType(DataType.SEMANTIC_STRING);
		// // The semantic string itself
		SemanticString ss = new SemanticString();
		ss.setString("java");
		List<Token> tokens = new ArrayList<Token>();
		Concept c = client.readConceptByGlobalId(36982L, eb1.getKbLabel());
		List<Concept> concepts = new ArrayList<Concept>();
		concepts.add(c);
		Token t = new Token("java", c.getLabel(), c.getId(), concepts);
		tokens.add(t);
		ss.setTokens(tokens);
		v.setSemanticStringValue(ss);
		values.add(v);
		a = new Attribute();
		a.setAttributeDefinition(et.getAttributeDefByName("semantic"));
		a.setValues(values);
		attrs.add(a);

		social.setAttributes(attrs);
		long eid = client.create(social);
		logger.info("Created entity ID:" + eid);
		social = client.readEntity(eid, null);
		return social;
	}

	public EntityBase createEntityBase() throws WebApiException {
		EntityBase eb = new EntityBase();
		eb.setLabel("SC_TEST_EB_" + System.currentTimeMillis());
		long ebid = client.create(eb);
		return client.readEntityBase(ebid);
	}

	public void deleteEntity(Entity e) throws WebApiException {
		EntityBase eb = e.getEntityBase();
		client.deleteEntity(e.getId());
		client.deleteEntityBase(eb.getId());
	}

	public Entity getEntity(long id) throws WebApiException {
		return client.readEntity(id, null);
	}

	public List<Entity> getEntities(List<Long> ids) throws WebApiException {
		return client.readEntities(ids, null);
	}

	public long getDefaultCommunity() throws WebApiException {
		return client.readCommunity("Smartcampus").getId();
	}

	public enum EntityTypes {
		event, experience, computerFile, journey, location, portfolio, narrative;

		public String toString() {
			switch (this) {
			case event:
				return "event";
			case experience:
				return "experience";
			case computerFile:
				return "computer file";
			case journey:
				return "journey";
			case location:
				return "location";
			case portfolio:
				return "portfolio";
			case narrative:
				return "narrative";
			default:
				return "";
			}
		}

	};
}
