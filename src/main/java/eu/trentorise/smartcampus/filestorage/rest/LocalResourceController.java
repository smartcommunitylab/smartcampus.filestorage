package eu.trentorise.smartcampus.filestorage.rest;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.filestorage.managers.LocalResourceManager;
import eu.trentorise.smartcampus.filestorage.managers.MetadataManager;
import eu.trentorise.smartcampus.filestorage.model.LocalResource;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;

@Controller
public class LocalResourceController extends SCController {
	private static final Logger logger = Logger
			.getLogger(LocalResourceController.class);

	@Autowired
	LocalResourceManager localManager;

	@Autowired
	MetadataManager metadataManager;

	@Override
	protected AuthServices getAuthServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/localstorage/{localResourceId}")
	public @ResponseBody
	Resource GetMyResource(
			@PathVariable("localResourceId") String localResourceId)
			throws SmartcampusException, NotFoundException {
		Metadata metadata = null;
		LocalResource localRes = null;
		localRes = localManager.getLocalResById(localResourceId);
		Resource resource = null;
		if (localRes.getDate() < System.currentTimeMillis()) {
			logger.warn(String.format("LocalResource %s time expired",
					localRes.getId()));
			throw new IllegalArgumentException("Token time expired");
		} else {
			resource = new Resource();
			FileInputStream fileInputStream = null;
			File file = new File(localRes.getUrl());
			byte[] bFile = new byte[(int) file.length()];
			try {
				// convert file into array of bytes
				fileInputStream = new FileInputStream(file);
				fileInputStream.read(bFile);
				fileInputStream.close();
			} catch (Exception e) {
				logger.error("Error converting file to byte[]");
			}

			metadata = metadataManager.findByResource(localRes.getResourceId());
			resource.setContent(bFile);
			resource.setName(metadata.getName());
			resource.setSize(metadata.getSize());
			resource.setContentType(metadata.getContentType());
		}

		return resource;
	}
}
