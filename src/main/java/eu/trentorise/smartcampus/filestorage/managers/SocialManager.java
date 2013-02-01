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

package eu.trentorise.smartcampus.filestorage.managers;

import it.unitn.disi.sweb.webapi.client.WebApiException;
import it.unitn.disi.sweb.webapi.client.smartcampus.SCWebApiClient;
import it.unitn.disi.sweb.webapi.model.entity.Entity;

import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.common.SemanticHelper;
import eu.trentorise.smartcampus.filestorage.model.Resource;

/**
 * <i>SocialManager</i> manages interaction with social engine.
 * 
 * @author mirko perillo
 * 
 */
@Service
public class SocialManager {
	SCWebApiClient socialClient;

	@Value("${smartcampus.vas.web.socialengine.host}")
	private String socialEngineHost;

	@Value("${smartcampus.vas.web.socialengine.port}")
	private int socialEnginePort;

	@PostConstruct
	@SuppressWarnings("unused")
	private void init() {
		socialClient = SCWebApiClient.getInstance(Locale.ENGLISH,
				socialEngineHost, socialEnginePort);
	}

	/**
	 * creates a social entity
	 * 
	 * @param resource
	 *            resource to bind with new social entity
	 * @param user
	 *            user owner of the resource
	 * @return the id of the new entity
	 * @throws WebApiException
	 *             exception thrown by social engine
	 */
	public Long createEntity(Resource resource, User user)
			throws WebApiException {
		Entity entity = SemanticHelper.createEntity(socialClient,
				user.getSocialId(), "computer file", resource.getName(),
				resource.getId(), null, null);

		return entity.getId();
	}

	/**
	 * deletes a social entity
	 * 
	 * @param eid
	 *            id of the social entity to delete
	 * @return true if operation gone fine, false otherwise
	 * @throws WebApiException
	 *             exception thrown by social engine
	 */
	public boolean deleteEntity(long eid) throws WebApiException {
		return SemanticHelper.deleteEntity(socialClient, eid);
	}

	/**
	 * checks if an entity is shared with a user
	 * 
	 * @param user
	 *            the user to check
	 * @param eid
	 *            id of the entity
	 * @return true if entity is shared with the user, false otherwise
	 * @throws WebApiException
	 *             exception thrown by social engine
	 */
	public boolean checkPermission(User user, String eid)
			throws WebApiException {
		return SemanticHelper.isEntitySharedWithUser(socialClient,
				Long.decode(eid), user.getSocialId());

	}

}
