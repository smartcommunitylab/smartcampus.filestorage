package eu.trentorise.smartcampus.filestorage.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.filestorage.managers.MediaManager;
import eu.trentorise.smartcampus.filestorage.managers.MetadataManager;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Operation;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.services.ACLService;
import eu.trentorise.smartcampus.filestorage.services.impl.DropboxStorage;
import eu.trentorise.smartcampus.filestorage.utils.ImageUtils;

@Controller
public class PublicResourceController {

	private static final Logger logger = Logger
			.getLogger(PublicResourceController.class);
	@Autowired
	ACLService scAcl;

	@Autowired
	MediaManager mediaManager;

	@Autowired
	DropboxStorage storageService;

	@Autowired
	MetadataManager metaManager;

	@RequestMapping(method = RequestMethod.GET, value = "/publicresource/{appName}/{rid}")
	public @ResponseBody
	Token getSharedResource(HttpServletRequest request,
			@PathVariable String appName, @PathVariable String rid)
			throws SmartcampusException, SecurityException {

		return scAcl.getSessionToken(Operation.DOWNLOAD, null, rid, false);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/mobile/{appName}/{rid}")
	public @ResponseBody
	Token getMobileImage(@PathVariable String appName, @PathVariable String rid)
			throws SmartcampusException {
		final String MOBILE_SUFFIX = "-MOBILE";
		try {
			Metadata metadata = metaManager.findByResource(rid);
			String mobileName = metadata.getName().substring(0,
					metadata.getName().lastIndexOf("."))
					+ MOBILE_SUFFIX
					+ metadata.getName().substring(
							metadata.getName().lastIndexOf("."));
			String url = storageService.getResourceUrlByName(
					metadata.getUserAccountId(), mobileName);
			if (url == null) {
				logger.info(String.format("%s not found", mobileName));
				logger.info(String.format("%s creation...", mobileName));
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				storageService.getResourceById(rid, out);
				byte[] imageOriginal = out.toByteArray();
				out.close();
				byte[] mobileImage = ImageUtils.imageCompression(imageOriginal);
				Resource resource = new Resource();
				resource.setContent(mobileImage);
				resource.setName(mobileName);
				resource.setContentType(metadata.getContentType());

				resource = storageService.store(metadata.getUserAccountId(),
						resource);
				url = storageService.getResourceUrlByName(
						metadata.getUserAccountId(), mobileName);
				logger.info(String.format("%s created", mobileName));
			}
			Token token = new Token();
			token.setMethodREST("GET");
			token.setStorageType(StorageType.DROPBOX);
			token.setUrl(url);
			return token;
		} catch (NotFoundException e) {
			new SmartcampusException("Resource doesn't exist");
		} catch (AlreadyStoredException e) {
			new SmartcampusException("General exception");
		} catch (SmartcampusException e) {
			throw e;
		} catch (IOException e) {
			throw new SmartcampusException("Exception creating mobile image");
		}

		return null;
	}
}
