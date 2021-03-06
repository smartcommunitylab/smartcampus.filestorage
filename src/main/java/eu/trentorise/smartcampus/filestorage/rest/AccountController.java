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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.filestorage.managers.AccountManager;
import eu.trentorise.smartcampus.filestorage.managers.PermissionManager;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.ListAccount;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;

@Controller
public class AccountController extends SCController {

	@Autowired
	AccountManager accountManager;

	@Autowired
	PermissionManager permissionManager;

	@Autowired
	AuthServices authServices;

	// METHODS USED BY SERVER SIDE

	@RequestMapping(method = RequestMethod.POST, value = "/account/app/{appId}")
	public @ResponseBody
	Account save(@RequestBody Account account, @PathVariable String appId)
			throws SmartcampusException, AlreadyStoredException,
			NotFoundException {

		if (!account.isValid()) {
			throw new IllegalArgumentException("Account is not valid");
		}

		try {
			getUserObject("" + account.getUserId());
		} catch (Exception e) {
			throw new IllegalArgumentException("userId MUST be valid");
		}

		account.setAppId(appId);
		if (!permissionManager.checkAccountPermission(appId, account)) {
			throw new SecurityException();
		}
		return accountManager.save(account);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/account/app/{appId}/{accountId}")
	public @ResponseBody
	void update(@RequestBody Account account, @PathVariable String appId,
			@PathVariable String accountId) throws SmartcampusException,
			NotFoundException {

		Account old = accountManager.findById(accountId);

		if (account.getId() == null) {
			account.setId(accountId);
		}

		if (!account.isValid()) {
			throw new IllegalArgumentException(
					"Account is not valid, some fields are empty");
		}

		if (!account.isSame(old)) {
			throw new IllegalArgumentException(String.format(
					"Not the same account of %s", accountId));
		}

		accountManager.update(account);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/account/app/{appId}/{accountId}")
	public @ResponseBody
	void delete(@PathVariable String accountId, @PathVariable String appId)
			throws SmartcampusException, NotFoundException {

		Account todel = accountManager.findById(accountId);

		if (!permissionManager.checkAccountPermission(appId, todel)) {
			throw new SecurityException();
		}

		accountManager.delete(accountId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/account/app/{appId}/{accountId}")
	public @ResponseBody
	Account getAccountById(@PathVariable String accountId,
			@PathVariable String appId) throws SmartcampusException,
			NotFoundException {

		Account account = accountManager.findById(accountId);
		if (!permissionManager.checkAccountPermission(appId, account)) {
			throw new SecurityException();
		}
		return account;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/account/app/{appId}")
	public @ResponseBody
	ListAccount getAccounts(@PathVariable String appId)
			throws SmartcampusException {
		ListAccount result = new ListAccount();
		result.setAccounts(accountManager.findAccounts(appId));
		return result;
	}

	// METHODS USED BY USER

	@RequestMapping(method = RequestMethod.PUT, value = "/account/user/{appId}")
	public @ResponseBody
	void updateMyAccount(@RequestBody Account account,
			@PathVariable String appId)
			throws SmartcampusException, NotFoundException {

		Account old = accountManager.findUserAccount(appId,getUserId());

		if (!account.isValid()) {
			throw new IllegalArgumentException(
					"Account is not valid, some fields are empty");
		}

		if (!account.isSame(old)) {
			throw new IllegalArgumentException(String.format(
					"Not the same account user %s account of %s", getUserId(), appId));
		}
		account.setAppId(appId);
		account.setUserId(getUserId());
		account.setId(old.getId());
		
		accountManager.update(account);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/account/user/{appId}")
	public @ResponseBody
	Account saveMyAccount(@RequestBody Account account,
			@PathVariable String appId) throws SmartcampusException,
			AlreadyStoredException, NotFoundException {

		if (!account.isValid()) {
			throw new IllegalArgumentException("Account is not valid");
		}

		try {
			account.setUserId(getUserId());
		} catch (Exception e) {
			throw new IllegalArgumentException("userId MUST be valid");
		}

		account.setAppId(appId);
		if (!permissionManager.checkAccountPermission(appId, account)) {
			throw new SecurityException();
		}
		return accountManager.save(account);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/account/user/{appId}")
	public @ResponseBody
	Account getMyAccount(@PathVariable String appId) throws SmartcampusException,
			NotFoundException {
		try {
			Account account = accountManager.findUserAccount(appId, getUserId());
			return account;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/account/user/{appId}")
	public @ResponseBody
	void deleteMyAccount(@PathVariable String appId) throws SmartcampusException,
			NotFoundException {

		Account account = accountManager.findUserAccount(appId, getUserId());
		if (account != null) {
			accountManager.delete(account);
		}
	}

	@Override
	protected AuthServices getAuthServices() {
		return authServices;
	}
}
