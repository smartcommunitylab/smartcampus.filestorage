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
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;

@Service
public class PermissionManager {
	@Autowired
	UserAccountManager accountManager;

	@Autowired
	MetadataManager metaManager;

	public boolean checkAccountPermission(User user, UserAccount account) {
		return user.getId().equals(account.getUserId());
	}

	public boolean checkAccountPermission(User user, String accountId)
			throws NotFoundException {
		UserAccount account = accountManager.findById(accountId);
		return user.getId().equals(account.getUserId());
	}

	public boolean checkResourcePermission(User user, String rid)
			throws NotFoundException {
		Metadata meta = metaManager.findByResource(rid);
		return user.getId().equals(
				accountManager.findById(meta.getAccountId()).getUserId());
	}

}
