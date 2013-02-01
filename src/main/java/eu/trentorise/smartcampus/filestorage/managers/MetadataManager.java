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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.services.MetadataService;

/**
 * <i>MetadataManager</i> manages all aspects about {@link Metadata} of a
 * resource.
 * 
 * @author mirko perillo
 * 
 */
@Service
public class MetadataManager {

	private static final Logger logger = Logger.getLogger(Metadata.class);
	@Autowired
	MetadataService metadataSrv;

	@Autowired
	SocialManager socialManager;

	/**
	 * creates and saves metadata info for given {@link Resource}
	 * 
	 * @param accountId
	 *            the id of user storage account in which resource is stored
	 * @param user
	 *            user owner of the resource
	 * @param resource
	 *            the resource
	 * @throws AlreadyStoredException
	 *             if metadata already exists
	 * @throws SmartcampusException
	 *             general exception
	 */
	public void create(String accountId, User user, Resource resource)
			throws AlreadyStoredException, SmartcampusException {
		metadataSrv.save(createMetadata(accountId, user, resource));
	}

	/**
	 * deletes {@link Metadata}
	 * 
	 * @param rid
	 *            the id of the resource binded with metadata
	 * @throws NotFoundException
	 *             if metadata for given resource doesn't exist
	 * @throws SmartcampusException
	 *             general exception
	 */
	public void delete(String rid) throws NotFoundException,
			SmartcampusException {
		String eid = null;
		try {
			eid = metadataSrv.getEntityByResource(rid);
			metadataSrv.delete(rid);
			socialManager.deleteEntity(Long.parseLong(eid));
		} catch (NumberFormatException e) {
			logger.error("Exception parsing entity id: " + eid);
			throw new NotFoundException();
		} catch (WebApiException e) {
			logger.error("Exception invoking social engine", e);
			throw new SmartcampusException(
					"Social engine error deleting entity");
		}
	}

	/**
	 * updates a {@link Metadata}. Only field lastModifiedTs is updated
	 * 
	 * @param resource
	 *            resource to update
	 * @throws NotFoundException
	 *             if metadata for given resource doesn't exist
	 */
	public void update(Resource resource) throws NotFoundException {
		Metadata metadata = metadataSrv.getMetadata(resource.getId());
		metadata.setLastModifiedTs(System.currentTimeMillis());
		metadataSrv.update(metadata);
	}

	/**
	 * retrieves a {@link Metadata} given binded resource id
	 * 
	 * @param rid
	 *            the resource id
	 * @return {@link Metadata} binded to resource
	 * @throws NotFoundException
	 *             if metadata doesn't exist
	 */
	public Metadata findByResource(String rid) throws NotFoundException {
		return metadataSrv.getMetadata(rid);
	}

	private Metadata createMetadata(String accountId, User user,
			Resource resource) throws SmartcampusException {
		Metadata metadata = new Metadata();
		metadata.setContentType(resource.getContentType());
		metadata.setCreationTs(System.currentTimeMillis());
		metadata.setName(resource.getName());
		metadata.setRid(resource.getId());
		metadata.setAccountId(accountId);

		try {
			metadata.setEid(socialManager.createEntity(resource, user)
					.toString());
		} catch (WebApiException e) {
			logger.error("Exception invoking social engine", e);
			throw new SmartcampusException(
					"Social engine error creating entity");
		}

		return metadata;
	}
}
