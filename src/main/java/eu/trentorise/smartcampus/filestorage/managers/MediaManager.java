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

@Service
public class MediaManager {

	@Autowired
	MetadataManager metadataManager;

	@Autowired
	ACLService scAcl;

	@Autowired
	StorageUtils storageUtils;

	public void storage(String accountId, User user, Resource resource)
			throws AlreadyStoredException, SmartcampusException {

		StorageService storageService = storageUtils
				.getStorageService(accountId);
		resource = storageService.store(accountId, resource);
		metadataManager.create(accountId, user, resource);
	}

	public void remove(String accountId, User user, String resourceId)
			throws SmartcampusException, NotFoundException {
		StorageService storageService = storageUtils
				.getStorageService(accountId);
		storageService.remove(accountId, resourceId);
		metadataManager.delete(resourceId);
	}

	public void replace(String accountId, User user, Resource resource)
			throws NotFoundException, SmartcampusException {
		StorageService storageService = storageUtils
				.getStorageService(accountId);
		storageService.replace(accountId, resource);
		metadataManager.update(resource);
	}

	public Token getResourceToken(User user, String rid, Operation op)
			throws SmartcampusException {

		return scAcl.getSessionToken(op, user, rid);
	}

}
