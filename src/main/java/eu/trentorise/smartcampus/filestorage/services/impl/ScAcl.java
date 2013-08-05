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

package eu.trentorise.smartcampus.filestorage.services.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.filestorage.managers.AccountManager;
import eu.trentorise.smartcampus.filestorage.managers.SocialManager;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Operation;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.services.ACLService;
import eu.trentorise.smartcampus.filestorage.services.MetadataService;
import eu.trentorise.smartcampus.filestorage.services.StorageService;
import eu.trentorise.smartcampus.filestorage.utils.StorageUtils;
import eu.trentorise.smartcampus.social.model.User;

@Service
public class ScAcl implements ACLService {

	private static final Logger logger = Logger.getLogger(ScAcl.class);
	@Autowired
	MetadataService metaService;

	@Autowired
	AccountManager userAccountManager;

	@Autowired
	SocialManager socialManager;

	@Autowired
	StorageUtils storageUtils;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isPermitted(Operation operation, String resourceId, User user) {
		throw new IllegalArgumentException("Operation not implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Operation[] getPermissions(String resourceId, User user) {
		throw new IllegalArgumentException("Operation not implemented");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Token getSessionToken(Operation operation, User user,
			String resourceId, boolean owned) throws SmartcampusException,
			SecurityException {
		Token token = null;
		switch (operation) {
		case DOWNLOAD:
			try {
				if ((owned && isMyResource(user, resourceId))
						|| (!owned && socialManager.checkPermission(user,
								metaService.getEntityByResource(resourceId)))) {
					logger.info(String.format(
							"Download permission ok, user: %s, resource: %s",
							user.getId(), resourceId));
					token = generateToken(resourceId);
					logger.info("Session token for download operation created successfully");
				} else {
					logger.error(String
							.format("User %s not have download permission to resource %s",
									user.getId(), resourceId));
					throw new SecurityException(
							"User has not permission on this resource");
				}
			} catch (SecurityException e) {
				throw e;
			} catch (Exception e) {
				throw new SmartcampusException(e);
			}
			break;

		default:
			throw new IllegalArgumentException("Operation not supported");
		}
		return token;
	}

	private boolean isMyResource(User user, String resourceId) {
		Metadata resourceInfo;
		try {
			resourceInfo = metaService.getMetadata(resourceId);
			Account account = userAccountManager.findById(resourceInfo
					.getAccountId());
			return account.getUserId().equals(user.getId());
		} catch (NotFoundException e) {
			logger.error(String.format("%s resource not found", resourceId));
			return false;
		}

	}

	private Token generateToken(String resourceId) throws NotFoundException,
			SmartcampusException {
		Metadata meta = metaService.getMetadata(resourceId);
		StorageService storageService = storageUtils.getStorageService(meta
				.getAccountId());
		return storageService.getToken(meta.getAccountId(), resourceId);
	}
}
