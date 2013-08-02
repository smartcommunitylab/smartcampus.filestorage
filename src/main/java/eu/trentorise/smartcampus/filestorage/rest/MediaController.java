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

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import eu.trentorise.smartcampus.filestorage.managers.MediaManager;
import eu.trentorise.smartcampus.filestorage.managers.MetadataManager;
import eu.trentorise.smartcampus.filestorage.managers.PermissionManager;
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
	MetadataManager metadataManager;

	@Autowired
	PermissionManager permissionManager;

	@Autowired
	ACLService scAcl;

	@Autowired
	AuthServices authServices;

	@RequestMapping(method = RequestMethod.POST, value = "/resource/create/app/{appName}/{userId}/{accountId}")
	public @ResponseBody
	Metadata storeResource(@PathVariable String userId,
			@PathVariable String appName, @PathVariable String accountId,
			@RequestParam("file") MultipartFile resource,
			@RequestParam(defaultValue = "true") boolean createSocialData)
			throws AlreadyStoredException, SmartcampusException,
			NotFoundException {
		User user = getUserObject(userId);

		if (!permissionManager.checkAccountPermission(user, accountId)) {
			throw new SecurityException();
		}
		try {
			String rid = mediaManager.storage(accountId, user,
					getResource(resource), createSocialData).getId();
			return metadataManager.findByResource(rid);
		} catch (IOException e) {
			throw new SmartcampusException(e);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/resource/create/user/{appName}/{accountId}")
	public @ResponseBody
	Metadata storeMyResource(@PathVariable String appName,
			@PathVariable String accountId,
			@RequestParam("file") MultipartFile resource,
			@RequestParam(defaultValue = "true") boolean createSocialData)
			throws AlreadyStoredException, SmartcampusException,
			NotFoundException {
		User user = getUserObject(getUserId());

		if (!permissionManager.checkAccountPermission(user, appName, accountId)) {
			throw new SecurityException();
		}
		try {
			String rid = mediaManager.storage(accountId, user,
					getResource(resource), createSocialData).getId();
			return metadataManager.findByResource(rid);
		} catch (IOException e) {
			throw new SmartcampusException(e);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/resource/app/{appName}/{rid}")
	public @ResponseBody
	void replaceResource(@PathVariable String appName,
			@PathVariable("rid") String rid,
			@RequestParam("file") MultipartFile resource)
			throws SmartcampusException, NotFoundException {

		if (!permissionManager.checkResourcePermission(appName, rid)) {
			throw new SecurityException();
		}

		try {
			mediaManager.replace(getResource(rid, resource));
		} catch (IOException e) {
			throw new SmartcampusException(e);
		}
	}

	@RequestMapping(method = RequestMethod.POST, value = "/resource/user/{appName}/{rid}")
	public @ResponseBody
	void replaceMyResource(@PathVariable String appName,
			@PathVariable("rid") String rid,
			@RequestParam("file") MultipartFile resource)
			throws SmartcampusException, NotFoundException {
		User user = getUserObject(getUserId());

		if (!permissionManager.checkResourcePermission(user, appName, rid)) {
			throw new SecurityException();
		}

		try {
			mediaManager.replace(getResource(rid, resource));
		} catch (IOException e) {
			throw new SmartcampusException(e);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/resource/app/{appName}/{rid}")
	public @ResponseBody
	void removeResource(@PathVariable String appName, @PathVariable String rid)
			throws SmartcampusException, NotFoundException {

		if (!permissionManager.checkResourcePermission(appName, rid)) {
			throw new SecurityException();
		}
		mediaManager.remove(rid);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/resource/user/{appName}/{rid}")
	public @ResponseBody
	void removeMyResource(@PathVariable String appName, @PathVariable String rid)
			throws SmartcampusException, NotFoundException {
		User user = getUserObject(getUserId());

		if (!permissionManager.checkResourcePermission(user, appName, rid)) {
			throw new SecurityException();
		}
		mediaManager.remove(rid);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metadata/app/{appName}/{rid}")
	public @ResponseBody
	Metadata getResourceMetadata(@PathVariable String appName,
			@PathVariable String rid) throws SmartcampusException,
			SecurityException, NotFoundException {

		if (!permissionManager.checkResourcePermission(appName, rid)) {
			new SecurityException();
		}
		return metadataManager.findByResource(rid);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metadata/any/{rid}")
	public @ResponseBody
	Metadata getAnyResourceMetadata(@PathVariable String rid)
			throws SmartcampusException, SecurityException, NotFoundException {

		return metadataManager.findByResource(rid);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/metadata/user/{appName}/{rid}")
	public @ResponseBody
	Metadata getMyResourceMetadata(@PathVariable String appName,
			@PathVariable String rid) throws SmartcampusException,
			SecurityException, NotFoundException {
		User user = getUserObject(getUserId());

		if (!permissionManager.checkResourcePermission(user, appName, rid)) {
			throw new SecurityException();
		}
		return metadataManager.findByResource(rid);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/myresource/user/{appName}/{rid}")
	public @ResponseBody
	Token getMyResourceUser(HttpServletRequest request,
			@PathVariable String appName, @PathVariable String rid)
			throws SmartcampusException, SecurityException, NotFoundException {
		User user = getUserObject(getUserId());
		if (!permissionManager.checkResourcePermission(appName, rid)) {
			throw new SecurityException();
		}
		return scAcl.getSessionToken(Operation.DOWNLOAD, user, rid, true);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/myresource/app/{appName}/{userId}/{rid}")
	public @ResponseBody
	Token getMyResourceApp(@PathVariable String userId,
			@PathVariable String appName, @PathVariable String rid)
			throws SmartcampusException, SecurityException, NotFoundException {
		User user = getUserObject(userId);
		if (!permissionManager.checkResourcePermission(appName, rid)) {
			throw new SecurityException();
		}
		return scAcl.getSessionToken(Operation.DOWNLOAD, user, rid, true);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/resource/user/{appName}/{rid}")
	public @ResponseBody
	Token getMySharedResource(HttpServletRequest request,
			@PathVariable String appName, @PathVariable String rid)
			throws SmartcampusException, SecurityException {
		User user = getUserObject(getUserId());

		return scAcl.getSessionToken(Operation.DOWNLOAD, user, rid, false);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/resource/app/{appName}/{userId}/{rid}")
	public @ResponseBody
	Token getSharedResource(HttpServletRequest request,
			@PathVariable String appName, @PathVariable String rid)
			throws SmartcampusException, SecurityException {
		User user = getUserObject(getUserId());

		return scAcl.getSessionToken(Operation.DOWNLOAD, user, rid, false);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/resource/any/{rid}")
	public @ResponseBody
	Token getAnySharedResource(HttpServletRequest request,
			@PathVariable String appName, @PathVariable String rid)
			throws SmartcampusException, SecurityException {
		User user = getUserObject(getUserId());

		return scAcl.getSessionToken(Operation.DOWNLOAD, user, rid, false);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/updatesocial/{appName}/{rid}/{entityId}")
	public @ResponseBody
	Metadata updateSocialData(HttpServletRequest request,
			@PathVariable String appName, @PathVariable String rid,
			@PathVariable String entityId) throws SmartcampusException,
			SecurityException, NotFoundException {

		User user = getUserObject(getUserId());

		return metadataManager.updateSocialData(user, rid, entityId);
	}

	private Resource getResource(String rid, MultipartFile file)
			throws IOException {
		Resource res = getResource(file);
		res.setId(rid);
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
