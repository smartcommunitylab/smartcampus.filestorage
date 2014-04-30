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

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.filestorage.managers.AccountManager;
import eu.trentorise.smartcampus.filestorage.managers.StorageManager;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.services.StorageService;
import eu.trentorise.smartcampus.filestorage.utils.StorageUtils;
import eu.trentorise.smartcampus.filestorage.utils.StringUtils;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;

/**
 * Controller used to operate the user account creation authorization for
 * specific types of storages.
 * <p/>
 * The procedure starts from obtaining a temporary authorization URL
 * (/requestAuth), that is used to initiate the procedure from the client app or
 * from the server-side app. Then, using the generated URL, the procedure is
 * initiated in a browser. The user is involved in the authorization process and
 * finally, the browser is redirected to the success page with the status code
 * specifying the outcome. In this moment the account is registered and can be
 * requested. If the storage has the redirect URI assigned, the status will be
 * notified on the that address, otherwise, the result is redirected to
 * /authorize/done address. the outcome is represented with the status
 * (ok/error) request param and with optional error_message.
 * 
 * @author raman
 * 
 */
@Controller
public class GetAccountController extends SCController {

	@Autowired
	StorageUtils storageUtils;
	@Autowired
	AuthServices authServices;
	@Autowired
	AccountManager accountManager;
	@Autowired
	private StorageManager storageManager;

	private static final Logger logger = Logger
			.getLogger(GetAccountController.class);

	static Map<String, AuthorizationRequest> cache = new HashMap<String, AuthorizationRequest>();

	@RequestMapping(method = RequestMethod.GET, value = "/requestAuth/{appId}")
	public @ResponseBody
	String getAuthorizationURL(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String appId)
			throws IOException {

		Storage storage = storageManager.getStorageByAppId(appId);
		if (storage == null) {
			throw new IllegalArgumentException(
					"No storage for specified app ID");
		}

		String k = UUID.randomUUID().toString();

		cache.put(k, new AuthorizationRequest(getUserId(), storage.getId()));
		String pre = StringUtils.appURL(request);
		return pre + "/authorize/" + k;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/authorize/{k}")
	public void authorize(HttpServletRequest request,
			HttpServletResponse response, @PathVariable String k)
			throws Exception {
		AuthorizationRequest req = cache.remove(k);
		if (req == null) {
			throw new SecurityException("Incorrect authorization request key");
		}
		request.getSession().setAttribute("authorizationRequest", req);
		StorageService storageService = storageUtils
				.getStorageServiceByStorage(req.storageId);
		storageService.startSession(req.storageId, req.userId, request,
				response);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/authorize/done")
	public @ResponseBody
	String done() throws Exception {
		return "";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/authorize/success")
	public void storeAccount(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		AuthorizationRequest req = (AuthorizationRequest) request.getSession()
				.getAttribute("authorizationRequest");
		if (req == null) {
			throw new SecurityException("No authorization info available");
		}

		Storage storage = storageManager.getStorageById(req.storageId);
		if (storage == null) {
			throw new IllegalArgumentException("Storage is not valid");
		}

		StorageService storageService = storageUtils
				.getStorageServiceByStorage(req.storageId);

		try {
			Account a = storageService.finishSession(req.storageId, req.userId,
					request, response);
			if (a == null || !a.isValid()) {
				throw new IllegalArgumentException("Account is not valid");
			}
			a = accountManager.save(a);
			if (StringUtils.isNullOrEmpty(storage.getRedirect(), true)) {
				redirect(response, a.getId(), StringUtils.appURL(request)
						+ "/authorize/done", null);
			} else {
				redirect(response, a.getId(), storage.getRedirect(), null);
			}
		} catch (Exception e) {
			if (StringUtils.isNullOrEmpty(storage.getRedirect(), true)) {
				redirect(response, null, StringUtils.appURL(request)
						+ "/authorize/done", "authorization failed");
			} else {
				redirect(response, null, storage.getRedirect(),
						"authorization failed");
			}
		}

	}

	@RequestMapping(method = RequestMethod.GET, value = "/localstorage/google/{userId}/{storageId}")
	public @ResponseBody
	void storeAccount(@PathVariable("userId") String userId,
			@PathVariable("storageId") String storageId,
			HttpServletResponse response, HttpServletRequest request)
			throws SmartcampusException, NotFoundException, Exception {
		StorageService storageService = storageUtils
				.getStorageServiceByStorage(storageId);
		Storage storage = storageManager.getStorageById(storageId);
		String code = request.getParameter("code");
		request.getSession().setAttribute("code", code);
		logger.debug("Store Account Servlet, " + storage.getStorageType()
				+ " id: " + storage.getId());
		String error = request.getParameter("error");
		if (error != null) {
			logger.error(error);
			response.sendError(500, error);
		} else {
			try {
				Account a = storageService.finishSession(storageId, userId,
						request, response);
				if (a == null || !a.isValid()) {
					throw new IllegalArgumentException("Account is not valid");
				}
				a = accountManager.save(a);
				if (StringUtils.isNullOrEmpty(storage.getRedirect(), true)) {
					redirect(response, a.getId(), StringUtils.appURL(request)
							+ "/authorize/done", null);
				} else {
					redirect(response, a.getId(), storage.getRedirect(), null);
				}
			} catch (Exception e) {
				if (StringUtils.isNullOrEmpty(storage.getRedirect(), true)) {
					redirect(response, null, StringUtils.appURL(request)
							+ "/authorize/done", "authorization failed");
				} else {
					redirect(response, null, storage.getRedirect(),
							"authorization failed");
				}
			}
		}
	}

	/*
	 * This servlet is used ONLY to test the .startSession() function
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/localstorage/googletest/{userId}/{storageId}")
	public @ResponseBody
	void testServlet(@PathVariable("userId") String userId,
			@PathVariable("storageId") String storageId,
			HttpServletResponse response, HttpServletRequest request)
			throws SmartcampusException, NotFoundException {
		logger.debug("Started session servlet");
		StorageService storageService = storageUtils
				.getStorageServiceByStorage(storageId);
		try {
			storageService.startSession(storageId, userId, request, response);
		} catch (Exception e) {
			logger.error("Starting session failed");
		}
	}

	/**
	 * @param redirect
	 * @param message
	 * @throws IOException
	 */
	private void redirect(HttpServletResponse response, String id,
			String redirect, String message) throws IOException {
		if (redirect.indexOf('?') > 0) {
			redirect += "&";
		} else {
			redirect += "?";
		}
		if (message == null && !StringUtils.isNullOrEmpty(id, true)) {
			redirect += "status=ok&accountId=" + id;
		} else {
			redirect += "status=error&error_message=" + message;
		}
		response.sendRedirect(redirect);
	}

	@Override
	protected AuthServices getAuthServices() {
		return authServices;
	}

	private static class AuthorizationRequest implements Serializable {
		private static final long serialVersionUID = -6631409364756775381L;

		String userId;
		String storageId;

		public AuthorizationRequest(String userId, String storageId) {
			super();
			this.userId = userId;
			this.storageId = storageId;
		}

	}

}
