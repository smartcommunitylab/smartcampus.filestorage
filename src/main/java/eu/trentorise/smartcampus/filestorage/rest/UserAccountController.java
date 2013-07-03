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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.filestorage.managers.AccountManager;
import eu.trentorise.smartcampus.filestorage.managers.PermissionManager;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.ListAccount;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;

@Controller
public class UserAccountController extends SCController {

	@Autowired
	AccountManager accountManager;

	@Autowired
	PermissionManager permissionManager;

	@RequestMapping(method = RequestMethod.POST, value = "/account")
	public @ResponseBody
	Account save(HttpServletRequest request, @RequestBody Account account)
			throws SmartcampusException, AlreadyStoredException {
		User user = retrieveUser(request);
		String appId = retrieveAppId(request);

		// if userId isn't setted, it will be use the authToken to retrieve it
		if (account.getUserId() <= 0) {
			account.setUserId(user.getId());
		}
		if (!permissionManager.checkAccountPermission(user, account)) {
			throw new SecurityException();
		}
		return accountManager.save(account);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/account/{accountId}")
	public @ResponseBody
	void update(HttpServletRequest request, @RequestBody Account account,
			@PathVariable String accountId) throws SmartcampusException {
		User user = retrieveUser(request);
		String appId = retrieveAppId(request);

		if (account.getId() == null) {
			account.setId(accountId);
		}

		if (!permissionManager.checkAccountPermission(user, account)) {
			throw new SecurityException();
		}

		accountManager.update(account);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/account/{accountId}")
	public @ResponseBody
	void delete(HttpServletRequest request, @PathVariable String accountId)
			throws SmartcampusException, NotFoundException {
		User user = retrieveUser(request);
		String appId = retrieveAppId(request);

		if (!permissionManager.checkAccountPermission(user, accountId)) {
			throw new SecurityException();
		}
		accountManager.delete(accountId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/account/{accountId}")
	public @ResponseBody
	Account getAccountById(HttpServletRequest request,
			@PathVariable String accountId) throws SmartcampusException,
			NotFoundException {
		User user = retrieveUser(request);
		String appId = retrieveAppId(request);

		return accountManager.findById(accountId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/account")
	public @ResponseBody
	ListAccount getAccounts(HttpServletRequest request)
			throws SmartcampusException {
		String appId = retrieveAppId(request);
		ListAccount result = new ListAccount();
		result.setAccounts(accountManager.findUserAccounts(appId));
		return result;
	}
}
