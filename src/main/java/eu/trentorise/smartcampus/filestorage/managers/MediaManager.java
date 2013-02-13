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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Operation;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.services.ACLService;
import eu.trentorise.smartcampus.filestorage.services.StorageService;
import eu.trentorise.smartcampus.filestorage.utils.StorageUtils;

/**
 * <i>MediaManager</i> manages all other managers and exposes the main core
 * functionalities about {@link Resource}
 * 
 * @author mirko perillo
 * 
 */
@Service
public class MediaManager {

	@Autowired
	MetadataManager metadataManager;

	@Autowired
	ACLService scAcl;

	@Autowired
	StorageUtils storageUtils;

	/**
	 * stores a {@link Resource} in the storage.
	 * 
	 * @param accountId
	 *            id of user storage account where store the resource
	 * @param user
	 *            the user who stores the resource
	 * @param resource
	 *            resource to store
	 * @return the resource stored with the id assigned from storage
	 * @throws AlreadyStoredException
	 *             if resource is already stored.
	 * @throws SmartcampusException
	 *             general exception
	 */
	public Resource storage(String accountId, User user, Resource resource)
			throws AlreadyStoredException, SmartcampusException {

		StorageService storageService = storageUtils
				.getStorageService(accountId);
		resource = storageService.store(accountId, resource);
		metadataManager.create(accountId, user, resource);
		return resource;
	}

	/**
	 * deletes a {@link Resource}
	 * 
	 * @param accountId
	 *            id of user storage account in which resource is stored
	 * @param user
	 *            user that do the operation
	 * @param resourceId
	 *            id of the resource to delete
	 * @throws SmartcampusException
	 *             general exception
	 * @throws NotFoundException
	 *             if resource doesn't exist
	 */
	public void remove(String accountId, User user, String resourceId)
			throws SmartcampusException, NotFoundException {
		StorageService storageService = storageUtils
				.getStorageService(accountId);
		storageService.remove(accountId, resourceId);
		metadataManager.delete(resourceId);
	}

	/**
	 * updates the content of the {@link Resource} in the storage, then updates
	 * only lastModifiedTs field of
	 * {@link eu.trentorise.smartcampus.filestorage.model.Metadata}.
	 * 
	 * @param accountId
	 *            id of user storage account in which resource is stored
	 * @param user
	 *            user that do the operation
	 * @param resource
	 *            the new resource
	 * @throws NotFoundException
	 *             if resource doesn't exist
	 * @throws SmartcampusException
	 *             general exception
	 */
	public void replace(String accountId, User user, Resource resource)
			throws NotFoundException, SmartcampusException {
		StorageService storageService = storageUtils
				.getStorageService(accountId);
		storageService.replace(accountId, resource);
		metadataManager.update(resource);
	}

	/**
	 * retrieves a {@link Token} to access the resource content
	 * 
	 * @param user
	 *            user who want to access the resource
	 * @param rid
	 *            id of the resource to download
	 * @param op
	 *            {@link Operation} to do on the resource
	 * @return the {@link Token}
	 * @throws SmartcampusException
	 *             general exception
	 * @throws SecurityException
	 *             if user haven't privileges to access the resource
	 */
	public Token getResourceToken(User user, String rid, Operation op)
			throws SmartcampusException, SecurityException {

		return scAcl.getSessionToken(op, user, rid);
	}

}
