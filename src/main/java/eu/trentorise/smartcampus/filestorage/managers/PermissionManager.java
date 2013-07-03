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
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Storage;

/**
 * <i>PermissionManager</i> checks the permissions about resources and storage
 * accounts
 * 
 * @author mirko perillo
 * 
 */
@Service
public class PermissionManager {
	@Autowired
	AccountManager accountManager;

	@Autowired
	StorageManager storageManager;

	@Autowired
	MetadataManager metaManager;

	/**
	 * checks if a user can access to a storage account
	 * 
	 * @param user
	 *            user who want to access the storage account
	 * @param account
	 *            storage account
	 * @return true if user can access, false otherwise
	 * @see Account
	 */
	public boolean checkAccountPermission(User user, Account account) {
		return user.getId().equals(account.getUserId());
	}

	/**
	 * checks if a user can access to a storage account
	 * 
	 * @param user
	 *            user who want to access the storage account
	 * @param accountId
	 *            storage account id
	 * @return true if user can access, false otherwise
	 * @see Account
	 * @throws NotFoundException
	 *             if account doesn't exist
	 */
	public boolean checkAccountPermission(User user, String accountId)
			throws NotFoundException {
		Account account = accountManager.findById(accountId);
		return user.getId().equals(account.getUserId());
	}

	/**
	 * checks if a user can access to a resource
	 * 
	 * @param user
	 *            the user who want to access to the resource
	 * @param rid
	 *            the resource id
	 * @return true if user can access, false otherwise
	 * @throws NotFoundException
	 *             if resource doesn't exist
	 */
	public boolean checkResourcePermission(User user, String rid)
			throws NotFoundException {
		Metadata meta = metaManager.findByResource(rid);
		return user.getId().equals(
				accountManager.findById(meta.getAccountId()).getUserId());
	}

	/**
	 * checks if application can access to the storage configurations
	 * 
	 * @param appId
	 * @param storageId
	 * @return
	 */
	public boolean checkStoragePermission(String appId, String storageId) {
		Storage retrieved;
		try {
			retrieved = storageManager.getStorageById(storageId);
			return retrieved.getAppId().equals(appId);
		} catch (NotFoundException e) {
			return false;
		}
	}
}
