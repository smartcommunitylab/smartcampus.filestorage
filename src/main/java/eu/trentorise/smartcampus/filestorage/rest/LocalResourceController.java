package eu.trentorise.smartcampus.filestorage.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.filestorage.managers.LocalResourceManager;
import eu.trentorise.smartcampus.filestorage.model.LocalResource;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;

@Controller
public class LocalResourceController extends SCController {

	@Autowired
	LocalResourceManager localManager;

	@Override
	protected AuthServices getAuthServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/local/{localResourceId}")
	public @ResponseBody
	Resource GetMyResource(
			@PathVariable("localResourceId") String localResourceId)
			throws SmartcampusException, NotFoundException {
		LocalResource localRes = localManager.getLocalResById(localResourceId);
		if (localRes.getDate() < System.currentTimeMillis()) {
			// SESSION EXPIRED
		} else {
			Resource resource = new Resource();
			return resource;
		}
		return null;
	}

}
