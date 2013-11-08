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
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import eu.trentorise.smartcampus.filestorage.managers.AccountManager;
import eu.trentorise.smartcampus.filestorage.managers.MediaManager;
import eu.trentorise.smartcampus.filestorage.managers.MetadataManager;
import eu.trentorise.smartcampus.filestorage.managers.PermissionManager;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Operation;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.services.ACLService;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;
import eu.trentorise.smartcampus.social.model.User;

@Controller
public class MediaController extends SCController {

	@Autowired
	MediaManager mediaManager;

	@Autowired
	AccountManager accountManager;

	@Autowired
	MetadataManager metadataManager;

	@Autowired
	PermissionManager permissionManager;

	@Autowired
	ACLService scAcl;

	@Autowired
	AuthServices authServices;

	@Autowired
	@Value("${max.number.metadata}")
	private int maxNumberMetadataResult;

	@RequestMapping(method = RequestMethod.POST, value = "/resource/create/app/{appId}/{accountId}")
	public @ResponseBody
	Metadata storeResource(@PathVariable String appId,
			@PathVariable String accountId,
			@RequestParam("file") MultipartFile resource,
			@RequestParam(defaultValue = "true") boolean createSocialData)
			throws AlreadyStoredException, SmartcampusException,
			NotFoundException {

		Account account = accountManager.findById(accountId);
		User user = getUserObject(account.getUserId());

		if (!permissionManager.checkAccountPermission(user, account)) {
			throw new SecurityException();
		}
		try {
			String resourceId = mediaManager.storage(accountId, user,
					getResource(resource), createSocialData).getId();
			return metadataManager.findByResource(resourceId);
		} catch (IOException e) {
			throw new SmartcampusException(e);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/resource/create/user/{appId}/{accountId}")
	public @ResponseBody
	Metadata storeMyResource(@PathVariable String appId,
			@PathVariable String accountId,
			@RequestParam("file") MultipartFile resource,
			@RequestParam(defaultValue = "true") boolean createSocialData)
			throws AlreadyStoredException, SmartcampusException,
			NotFoundException {
		User user = getUserObject(getUserId());

		if (!permissionManager.checkAccountPermission(user, appId, accountId)) {
			throw new SecurityException();
		}
		try {
			String resourceId = mediaManager.storage(accountId, user,
					getResource(resource), createSocialData).getId();
			return metadataManager.findByResource(resourceId);
		} catch (IOException e) {
			throw new SmartcampusException(e);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/resource/app/{appId}/{resourceId}")
	public @ResponseBody
	void replaceResource(@PathVariable String appId,
			@PathVariable("resourceId") String resourceId,
			@RequestParam("file") MultipartFile resource)
			throws SmartcampusException, NotFoundException {

		if (!permissionManager.checkResourcePermission(appId, resourceId)) {
			throw new SecurityException();
		}

		try {
			mediaManager.replace(getResource(resourceId, resource));
		} catch (IOException e) {
			throw new SmartcampusException(e);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/resource/user/{appId}/{resourceId}")
	public @ResponseBody
	void replaceMyResource(@PathVariable String appId,
			@PathVariable("resourceId") String resourceId,
			@RequestParam("file") MultipartFile resource)
			throws SmartcampusException, NotFoundException {
		User user = getUserObject(getUserId());

		if (!permissionManager.checkResourcePermission(user, appId, resourceId)) {
			throw new SecurityException();
		}

		try {
			mediaManager.replace(getResource(resourceId, resource));
		} catch (IOException e) {
			throw new SmartcampusException(e);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/resource/app/{appId}/{resourceId}")
	public @ResponseBody
	void removeResource(@PathVariable String appId,
			@PathVariable String resourceId) throws SmartcampusException,
			NotFoundException {

		if (!permissionManager.checkResourcePermission(appId, resourceId)) {
			throw new SecurityException();
		}
		mediaManager.remove(resourceId);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/resource/user/{appId}/{resourceId}")
	public @ResponseBody
	void removeMyResource(@PathVariable String appId,
			@PathVariable String resourceId) throws SmartcampusException,
			NotFoundException {
		User user = getUserObject(getUserId());

		if (!permissionManager.checkResourcePermission(user, appId, resourceId)) {
			throw new SecurityException();
		}
		mediaManager.remove(resourceId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metadata/app/{appId}/{resourceId}")
	public @ResponseBody
	Metadata getResourceMetadata(@PathVariable String appId,
			@PathVariable String resourceId) throws SmartcampusException,
			SecurityException, NotFoundException {

		if (!permissionManager.checkResourcePermission(appId, resourceId)) {
			new SecurityException();
		}
		return metadataManager.findByResource(resourceId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metadata/user/{appId}/{resourceId}")
	public @ResponseBody
	Metadata getMyResourceMetadata(@PathVariable String appId,
			@PathVariable String resourceId) throws SmartcampusException,
			SecurityException, NotFoundException {
		User user = getUserObject(getUserId());

		if (!scAcl.isPermitted(Operation.DOWNLOAD, resourceId, user)) {
			throw new SecurityException();
		}
		return metadataManager.findByResource(resourceId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metadata/all/user/{appId}")
	public @ResponseBody
	List<Metadata> getAllMyResourceMetadata(@PathVariable String appId,
			@RequestParam(required = false) Integer position,
			@RequestParam(required = false) Integer size)
			throws SmartcampusException, SecurityException, NotFoundException {

		if (size == null || size > maxNumberMetadataResult) {
			size = maxNumberMetadataResult;
		}
		return metadataManager.findAllBy(appId, getUserId(), position, size);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metadata/all/app/{appId}")
	public @ResponseBody
	List<Metadata> getAllAppResourceMetadata(@PathVariable String appId,
			@RequestParam(required = false) Integer position,
			@RequestParam(required = false) Integer size)
			throws SmartcampusException, SecurityException, NotFoundException {

		if (size == null || size > maxNumberMetadataResult) {
			size = maxNumberMetadataResult;
		}
		return metadataManager.findAllBy(appId, null, position, size);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/resource/user/{appId}/{resourceId}")
	public @ResponseBody
	Token getMyResourceUser(HttpServletRequest request,
			@PathVariable String appId, @PathVariable String resourceId)
			throws SmartcampusException, SecurityException, NotFoundException {
		User user = getUserObject(getUserId());
		if (!permissionManager.checkResourcePermission(appId, resourceId)) {
			throw new SecurityException();
		}
		return scAcl
				.getSessionToken(Operation.DOWNLOAD, user, resourceId, true);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/resource/app/{appId}/{resourceId}")
	public @ResponseBody
	Token getMyResourceApp(@PathVariable String appId,
			@PathVariable String resourceId) throws SmartcampusException,
			SecurityException, NotFoundException {
		String userId = metadataManager.getOwner(resourceId);
		User user = getUserObject(userId);
		if (!permissionManager.checkResourcePermission(appId, resourceId)) {
			throw new SecurityException();
		}
		return scAcl
				.getSessionToken(Operation.DOWNLOAD, user, resourceId, true);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/sharedresource/user/{appId}/{resourceId}")
	public @ResponseBody
	Token getMySharedResource(@PathVariable String appId,
			@PathVariable String resourceId) throws SmartcampusException,
			SecurityException, NotFoundException {
		if (!permissionManager.checkResourcePermission(appId, resourceId)) {
			throw new SecurityException();
		}
		User user = getUserObject(getUserId());

		return scAcl.getSessionToken(Operation.DOWNLOAD, user, resourceId,
				false);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/sharedresource/app/{appId}/{userId}/{resourceId}")
	public @ResponseBody
	Token getSharedResource(@PathVariable String userId,
			@PathVariable String appId, @PathVariable String resourceId)
			throws SmartcampusException, SecurityException, NotFoundException {
		if (!permissionManager.checkResourcePermission(appId, resourceId)) {
			throw new SecurityException();
		}
		User user = getUserObject(userId);

		return scAcl.getSessionToken(Operation.DOWNLOAD, user, resourceId,
				false);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/updatesocial/app/{appId}/{resourceId}/{entityId}")
	public @ResponseBody
	Metadata updateSocialData(@PathVariable String appId,
			@PathVariable String resourceId, @PathVariable String entityId)
			throws SmartcampusException, SecurityException, NotFoundException {

		String userId = metadataManager.getOwner(resourceId);
		User user = getUserObject(userId);

		return metadataManager.updateSocialData(user, resourceId, entityId);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/updatesocial/user/{appId}/{resourceId}/{entityId}")
	public @ResponseBody
	Metadata updateMySocialData(@PathVariable String appId,
			@PathVariable String resourceId, @PathVariable String entityId)
			throws SmartcampusException, SecurityException, NotFoundException {

		User user = getUserObject(getUserId());
		if (!permissionManager.checkResourcePermission(user, appId, resourceId)) {
			throw new SecurityException();
		}

		return metadataManager.updateSocialData(user, resourceId, entityId);
	}

	private Resource getResource(String resourceId, MultipartFile file)
			throws IOException {
		Resource res = getResource(file);
		res.setId(resourceId);
		return res;
	}

	private Resource getResource(MultipartFile file) throws IOException {
		Resource res = new Resource();
		res.setContent(file.getBytes());
		res.setContentType(file.getContentType());
		res.setName(file.getOriginalFilename());
		return res;
	}

	@Override
	protected AuthServices getAuthServices() {
		return authServices;
	}
}
