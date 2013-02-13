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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.filestorage.managers.UserAccountManager;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;
import eu.trentorise.smartcampus.filestorage.services.StorageService;
import eu.trentorise.smartcampus.filestorage.services.impl.DropboxStorage;

/**
 * Utility on storage
 * 
 * @author mirko perillo
 * 
 */
@Service
public class StorageUtils {

	private static final Logger logger = Logger.getLogger(StorageUtils.class);

	@Autowired
	private UserAccountManager accountManager;

	@Autowired
	private ApplicationContextProvider ctxProvider;

	/**
	 * methods is a factory and provide the correct implementations of
	 * StorageService given a user storage account
	 * 
	 * @param accountId
	 *            id of the user account storage
	 * @return the implementation of StorageService suitable with given storage
	 *         account
	 * @throws SmartcampusException
	 */
	public StorageService getStorageService(String accountId)
			throws SmartcampusException {
		BeanFactory beanFactory = ctxProvider.getApplicationContext();
		UserAccount account;
		try {
			account = accountManager.findById(accountId);
		} catch (NotFoundException e) {
			logger.error(String.format("Account %s doesn't exist", accountId));
			throw new SmartcampusException("Account doesn't exist");
		}
		StorageService service = null;
		switch (account.getStorageType()) {
		case DROPBOX:
			logger.info(String.format(
					"Requested storageService for account %s, type %s",
					accountId, "DROPBOX"));
			service = beanFactory.getBean(DropboxStorage.class);
			break;
		default:
			throw new SmartcampusException("Storage type not supported");
		}

		return service;
	}
}
