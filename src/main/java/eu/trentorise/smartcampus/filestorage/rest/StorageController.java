package eu.trentorise.smartcampus.filestorage.rest;

import javax.servlet.http.HttpServletRequest;

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
import eu.trentorise.smartcampus.filestorage.model.ListStorage;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;

@Controller
public class StorageController extends SCController {

	private static final Logger logger = Logger
			.getLogger(StorageController.class);

	@Autowired
	private StorageManager storageManager;

	@Autowired
	private PermissionManager permissionManager;

	@Autowired
	AuthServices authServices;

	@RequestMapping(method = RequestMethod.POST, value = "/storage/{appId}")
	public @ResponseBody
	Storage create(HttpServletRequest request, @RequestBody Storage storage,
			@PathVariable String appId) throws SmartcampusException,
			AlreadyStoredException {
		storage.setAppId(appId);
		return storageManager.save(storage);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/storage/{appId}/{storageId}")
	public @ResponseBody
	Storage update(HttpServletRequest request, @RequestBody Storage storage,
			@PathVariable String storageId, @PathVariable String appId)
			throws SmartcampusException, NotFoundException {

		storage.setId(storageId);

		if (!permissionManager.checkStoragePermission(appId, storageId)) {
			throw new SecurityException();
		}
		return storageManager.update(storage);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/storage/{appId}/{storageId}")
	public @ResponseBody
	boolean delete(HttpServletRequest request, @PathVariable String storageId,
			@PathVariable String appId) throws SmartcampusException {

		if (!permissionManager.checkStoragePermission(appId, storageId)) {
			throw new SecurityException();
		}
		storageManager.delete(storageId);
		return true;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/storage/{appId}")
	public @ResponseBody
	ListStorage getStorages(HttpServletRequest request,
			@PathVariable String appId) throws SmartcampusException {

		ListStorage result = new ListStorage();
		result.setStorages(storageManager.getStorages(appId));
		return result;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/storage/{appId}/{storageId}")
	public @ResponseBody
	Storage getStorage(HttpServletRequest request,
			@PathVariable String storageId, @PathVariable String appId)
			throws SmartcampusException, NotFoundException {

		if (!permissionManager.checkStoragePermission(appId, storageId)) {
			throw new SecurityException();
		}

		return storageManager.getStorageById(storageId);
	}

	@Override
	protected AuthServices getAuthServices() {
		return authServices;
	}

}
