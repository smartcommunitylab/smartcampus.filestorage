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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxLink;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.WebAuthSession;

import eu.trentorise.smartcampus.filestorage.managers.AppAccountManager;
import eu.trentorise.smartcampus.filestorage.managers.UserAccountManager;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.AppAccount;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;
import eu.trentorise.smartcampus.filestorage.services.MetadataService;
import eu.trentorise.smartcampus.filestorage.services.StorageService;

/**
 * Storage on a user Dropbox account
 * 
 * @author mirko perillo
 * 
 */
@Service
public class DropboxStorage implements StorageService {

	private static final Logger logger = Logger.getLogger(DropboxStorage.class);

	private static final String USER_KEY = "USER_KEY";
	private static final String USER_SECRET = "USER_SECRET";

	private static final String APP_KEY = "APP_KEY";
	private static final String APP_SECRET = "APP_SECRET";

	@Autowired
	private UserAccountManager accountManager;

	@Autowired
	AppAccountManager appAccountManager;

	@Autowired
	private MetadataService metaService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Resource store(String userAccountId, Resource resource)
			throws AlreadyStoredException, SmartcampusException {

		// check if file already exists int
		try {
			metaService
					.getResourceByFilename(userAccountId, resource.getName());
			throw new AlreadyStoredException();
		} catch (NotFoundException e1) {

			AccessTokenPair token = null;
			AppKeyPair app = null;
			try {
				token = getUserToken(userAccountId);
				app = getAppToken(userAccountId);
				logger.info("Retrieved dropbox account informations");
			} catch (NotFoundException e2) {
				throw new SmartcampusException(e2);
			}

			WebAuthSession sourceSession = new WebAuthSession(app,
					Session.AccessType.APP_FOLDER, token);
			DropboxAPI<?> sourceClient = new DropboxAPI<WebAuthSession>(
					sourceSession);

			InputStream in = new ByteArrayInputStream(resource.getContent());
			try {
				sourceClient.putFile(resource.getName(), in,
						resource.getContent().length, null, null);
				sourceSession.unlink();
				in.close();
				logger.info("Resource stored on dropbox");
				if (resource.getId() == null) {
					resource.setId(new ObjectId().toString());
				}
				return resource;
			} catch (IOException e) {
				throw new SmartcampusException(e);
			} catch (DropboxException e) {
				throw new SmartcampusException(e);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replace(String userAccountId, Resource resource)
			throws NotFoundException, SmartcampusException {
		if (resource.getId() == null) {
			throw new NotFoundException();
		}

		AccessTokenPair token = null;
		AppKeyPair app = null;
		try {
			token = getUserToken(userAccountId);
			app = getAppToken(userAccountId);
		} catch (NotFoundException e2) {
			throw new SmartcampusException(e2);
		}

		Metadata meta = metaService.getMetadata(resource.getId());

		WebAuthSession sourceSession = new WebAuthSession(app,
				Session.AccessType.APP_FOLDER, token);
		DropboxAPI<?> sourceClient = new DropboxAPI<WebAuthSession>(
				sourceSession);

		InputStream in = new ByteArrayInputStream(resource.getContent());
		try {
			sourceClient.putFileOverwrite(meta.getName(), in,
					resource.getContent().length, null);
			sourceSession.unlink();
			in.close();
		} catch (IOException e) {
			throw new SmartcampusException(e);
		} catch (DropboxException e) {
			throw new SmartcampusException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(String userAccountId, String rid)
			throws NotFoundException, SmartcampusException {
		// get user token

		AccessTokenPair token = null;
		AppKeyPair app = null;

		try {
			token = getUserToken(userAccountId);
			app = getAppToken(userAccountId);
		} catch (NotFoundException e2) {
			throw new SmartcampusException(e2);
		}
		// find resource name
		Metadata metadata = metaService.getMetadata(rid);

		WebAuthSession sourceSession = new WebAuthSession(app,
				Session.AccessType.APP_FOLDER, token);
		DropboxAPI<?> sourceClient = new DropboxAPI<WebAuthSession>(
				sourceSession);

		try {
			sourceClient.delete(metadata.getName());
			sourceSession.unlink();
		} catch (DropboxException e) {
			throw new SmartcampusException(e);
		}

	}

	private AccessTokenPair getUserToken(String userAccountId)
			throws NotFoundException {
		UserAccount account = accountManager.findById(userAccountId);

		return getUserToken(account.getConfigurations());
	}

	private AppKeyPair getAppToken(String userAccountId)
			throws NotFoundException {
		UserAccount account = accountManager.findById(userAccountId);

		AppAccount appAccount = appAccountManager.getAppAccountById(account
				.getAppAccountId());

		return getAppToken(appAccount.getConfigurations());
	}

	private AppKeyPair getAppToken(List<Configuration> confs) {
		String appKey = null;
		String appSecret = null;
		if (confs == null) {
			return null;
		}
		for (Configuration tmp : confs) {
			if (tmp.getName().equals(APP_KEY)) {
				appKey = tmp.getValue();
			}
			if (tmp.getName().equals(APP_SECRET)) {
				appSecret = tmp.getValue();
			}
		}
		return new AppKeyPair(appKey, appSecret);

	}

	private AccessTokenPair getUserToken(List<Configuration> confs) {
		String userKey = null;
		String userSecret = null;
		if (confs == null) {
			return null;
		}
		for (Configuration tmp : confs) {
			if (tmp.getName().equals(USER_KEY)) {
				userKey = tmp.getValue();
			}
			if (tmp.getName().equals(USER_SECRET)) {
				userSecret = tmp.getValue();
			}
		}

		return new AccessTokenPair(userKey, userSecret);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Token getToken(String userAccountId, String rid)
			throws NotFoundException, SmartcampusException {
		// get user token
		AccessTokenPair token = null;
		AppKeyPair app = null;

		try {
			token = getUserToken(userAccountId);
			app = getAppToken(userAccountId);
		} catch (NotFoundException e2) {
			throw new SmartcampusException(e2);
		}

		// find resource name
		Metadata metadata = metaService.getMetadata(rid);
		AppAccount appAccount = appAccountManager.getAppAccountById(metadata
				.getAppAccountId());

		WebAuthSession sourceSession = new WebAuthSession(app,
				Session.AccessType.APP_FOLDER, token);
		DropboxAPI<?> sourceClient = new DropboxAPI<WebAuthSession>(
				sourceSession);

		DropboxLink link;
		try {
			link = sourceClient.media("/" + metadata.getName(), true);
		} catch (DropboxException e) {
			throw new SmartcampusException();
		}
		Token userSessionToken = new Token();
		userSessionToken.setUrl(link.url);
		userSessionToken.setMethodREST("GET");
		userSessionToken.setStorageType(appAccount.getStorageType());
		return userSessionToken;
	}
}
