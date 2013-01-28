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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.filestorage.managers.SocialManager;
import eu.trentorise.smartcampus.filestorage.model.Operation;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.services.ACLService;
import eu.trentorise.smartcampus.filestorage.services.MetadataService;

@Service
public class ScAcl implements ACLService {

	@Autowired
	MetadataService metaService;

	@Autowired
	SocialManager socialManager;

	@Override
	public boolean isPermitted(Operation operation, String rid, User user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Operation[] getPermissions(String rid, User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Token getSessionToken(Operation operation, User user, String rid)
			throws SmartcampusException, SecurityException {
		Token token = null;
		switch (operation) {
		case DOWNLOAD:
			try {
				StorageType type = metaService.getResourceStorage(rid);
				if (socialManager.checkPermission(user,
						metaService.getEntityByResource(rid))) {
					token = generateToken(type, rid);
				} else {
					throw new SecurityException(
							"User has not permission on this resource");
				}
			} catch (Exception e) {
				throw new SmartcampusException(e);
			}
			break;

		default:
			break;
		}
		return token;
	}

	private Token generateToken(StorageType storageType, String rid) {
		Token token = new Token();
		token.setUrl("https://api.dropbox.com/r/resource");
		token.setMethodREST("GET");

		return token;
	}
}
