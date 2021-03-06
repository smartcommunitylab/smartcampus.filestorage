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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.filestorage.managers.PermissionManager;
import eu.trentorise.smartcampus.filestorage.managers.StorageManager;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;

@Controller
public class StorageController extends SCController {

	static final Logger logger = Logger.getLogger(StorageController.class);

	@Autowired
	private StorageManager storageManager;

	@Autowired
	private PermissionManager permissionManager;

	@Autowired
	AuthServices authServices;

	@RequestMapping(method = RequestMethod.POST, value = "/storage/app/{appId}")
	public @ResponseBody
	Storage create(@RequestBody Storage storage, @PathVariable String appId)
			throws SmartcampusException, AlreadyStoredException {
		storage.setAppId(appId);
		return storageManager.save(storage);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/storage/app/{appId}")
	public @ResponseBody
	Storage update(@RequestBody Storage storage, @PathVariable String appId)
			throws SmartcampusException, NotFoundException {

		storage.setAppId(appId);
		return storageManager.update(storage);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/storage/app/{appId}")
	public @ResponseBody
	boolean delete(@PathVariable String appId) throws SmartcampusException {

		storageManager.delete(appId);
		return true;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/storage/app/{appId}")
	public @ResponseBody
	Storage getStorage(@PathVariable String appId) throws SmartcampusException,
			NotFoundException {

		return storageManager.getStorageByAppId(appId);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/conf/storage")
	public String storageConf() throws SmartcampusException, NotFoundException {
		return "storage";
	}

	@Override
	protected AuthServices getAuthServices() {
		return authServices;
	}

}
