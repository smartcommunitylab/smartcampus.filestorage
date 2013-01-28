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

@Service
public class MetadataManager {

	private static final Logger logger = Logger.getLogger(Metadata.class);
	@Autowired
	MetadataService metadataSrv;

	@Autowired
	SocialManager socialManager;

	public void create(String accountId, User user, Resource resource)
			throws AlreadyStoredException, SmartcampusException {
		metadataSrv.save(createMetadata(accountId, user, resource));
	}

	public void delete(String rid) throws NotFoundException,
			SmartcampusException {
		metadataSrv.delete(rid);
		String eid = null;
		try {
			eid = metadataSrv.getEntityByResource(rid);
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

	public void update(Resource resource) throws NotFoundException {
		Metadata metadata = metadataSrv.getMetadata(resource.getId());
		metadata.setLastModifiedTs(System.currentTimeMillis());
		metadataSrv.update(metadata);
	}

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
