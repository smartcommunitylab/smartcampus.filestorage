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

package eu.trentorise.smartcampus.filestorage.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.filestorage.managers.PermissionManager;
import eu.trentorise.smartcampus.filestorage.managers.UserAccountManager;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;

public class UserAccountController extends RestController {

	@Autowired
	UserAccountManager accountManager;

	@Autowired
	PermissionManager permissionManager;

	@RequestMapping(method = RequestMethod.POST, value = "/eu.trentorise.smartcampus.mediastorage.UserAccount")
	public @ResponseBody
	void save(HttpServletRequest request, @RequestBody UserAccount account)
			throws SmartcampusException, AlreadyStoredException {
		User user = retrieveUser(request);

		if (!permissionManager.checkAccountPermission(user, account)) {
			throw new SecurityException();
		}
		accountManager.save(account);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/eu.trentorise.smartcampus.mediastorage.UserAccount/{aid}")
	public @ResponseBody
	void update(HttpServletRequest request, @RequestBody UserAccount account,
			@PathVariable("aid") String aid) throws SmartcampusException {
		User user = retrieveUser(request);

		if (account.getId() == null) {
			account.setId(aid);
		}

		if (!permissionManager.checkAccountPermission(user, account)) {
			throw new SecurityException();
		}

		accountManager.update(account);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/eu.trentorise.smartcampus.mediastorage.UserAccount/{aid}")
	public @ResponseBody
	void delete(HttpServletRequest request, @PathVariable("aid") String aid)
			throws SmartcampusException, NotFoundException {
		User user = retrieveUser(request);

		if (!permissionManager.checkAccountPermission(user, aid)) {
			throw new SecurityException();
		}
		accountManager.delete(aid);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/eu.trentorise.smartcampus.mediastorage.UserAccount")
	public @ResponseBody
	List<UserAccount> getMyAccounts(HttpServletRequest request)
			throws SmartcampusException {
		User user = retrieveUser(request);
		return accountManager.findBy(user.getId());
	}

	@RequestMapping(method = RequestMethod.GET, value = "/eu.trentorise.smartcampus.mediastorage.UserAccount/{aid}")
	public @ResponseBody
	UserAccount getMyAccount(HttpServletRequest request,
			@PathVariable String aid) throws SmartcampusException,
			NotFoundException {
		User user = retrieveUser(request);
		if (!permissionManager.checkAccountPermission(user, aid)) {
			throw new SecurityException();
		}
		return accountManager.findById(aid);
	}
}
