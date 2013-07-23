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

@Controller
public class StorageController extends SCController {

	private static final Logger logger = Logger
			.getLogger(StorageController.class);

	@Autowired
	private StorageManager storageManager;

	@Autowired
	private PermissionManager permissionManager;

	@RequestMapping(method = RequestMethod.POST, value = "/storage")
	public @ResponseBody
	Storage create(HttpServletRequest request, @RequestBody Storage storage)
			throws SmartcampusException, AlreadyStoredException {
		String appId = retrieveAppId(request);
		storage.setAppName(appId);
		return storageManager.save(storage);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/storage/{storageId}")
	public @ResponseBody
	Storage update(HttpServletRequest request, @RequestBody Storage storage,
			@PathVariable String storageId) throws SmartcampusException,
			NotFoundException {

		storage.setId(storageId);

		// TODO retrieved from SecurityContext
		String appId = retrieveAppId(request);
		if (!permissionManager.checkStoragePermission(appId, storageId)) {
			throw new SecurityException();
		}
		return storageManager.update(storage);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/storage/{storageId}")
	public @ResponseBody
	boolean delete(HttpServletRequest request, @PathVariable String storageId)
			throws SmartcampusException {

		// TODO retrieved from SecurityContext
		String appId = retrieveAppId(request);
		if (!permissionManager.checkStoragePermission(appId, storageId)) {
			throw new SecurityException();
		}
		storageManager.delete(storageId);
		return true;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/storage")
	public @ResponseBody
	ListStorage getStorages(HttpServletRequest request)
			throws SmartcampusException {

		// TODO retrieved from SecurityContext
		String appId = retrieveAppId(request);

		ListStorage result = new ListStorage();
		result.setStorages(storageManager.getStorages(appId));
		return result;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/storage/{storageAccountId}")
	public @ResponseBody
	Storage getStorage(HttpServletRequest request,
			@PathVariable String storageAccountId) throws SmartcampusException,
			NotFoundException {

		// TODO retrieved from SecurityContext
		String appId = retrieveAppId(request);

		if (!permissionManager.checkStoragePermission(appId, storageAccountId)) {
			throw new SecurityException();
		}

		return storageManager.getStorageById(storageAccountId);
	}

}
