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

import eu.trentorise.smartcampus.filestorage.managers.AccountManager;
import eu.trentorise.smartcampus.filestorage.managers.StorageManager;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
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
	private AccountManager accountManager;

	@Autowired
	private StorageManager storageManager;

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
	public StorageService getStorageServiceByAccount(String accountId)
			throws SmartcampusException {
		try {
			Account account = accountManager.findById(accountId);
			return getStorageService(account.getStorageType());
		} catch (NotFoundException e) {
			logger.error(String.format("Account %s doesn't exist", accountId));
			throw new SmartcampusException("Account doesn't exist");
		}
	}

	public StorageService getStorageServiceByStorage(String storageId)
			throws SmartcampusException {
		try {
			Storage storage = storageManager.getStorageById(storageId);
			logger.info(String.format(
					"Requested storageService for storage %s, type %s",
					storageId, storage.getStorageType().toString()));
			return getStorageService(storage.getStorageType());
		} catch (NotFoundException e) {
			logger.error(String.format("Storage %s doesn't exist", storageId));
			throw new SmartcampusException(String.format(
					"Storage %s doesn't exist", storageId));
		}

	}

	private StorageService getStorageService(StorageType type)
			throws SmartcampusException {
		BeanFactory beanFactory = ctxProvider.getApplicationContext();
		StorageService service = null;
		switch (type) {
		case DROPBOX:
			service = beanFactory.getBean(DropboxStorage.class);
			break;
		default:
			throw new SmartcampusException("Storage type not supported");
		}
		return service;
	}
}
