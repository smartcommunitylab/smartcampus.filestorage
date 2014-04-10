package eu.trentorise.smartcampus.filestorage.rest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.filestorage.managers.AccountManager;
import eu.trentorise.smartcampus.filestorage.managers.LocalResourceManager;
import eu.trentorise.smartcampus.filestorage.managers.MediaManager;
import eu.trentorise.smartcampus.filestorage.managers.MetadataManager;
import eu.trentorise.smartcampus.filestorage.managers.PermissionManager;
import eu.trentorise.smartcampus.filestorage.model.LocalResource;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.services.ACLService;
import eu.trentorise.smartcampus.filestorage.services.StorageService;
import eu.trentorise.smartcampus.filestorage.services.impl.GoogleDriveStorage;
import eu.trentorise.smartcampus.filestorage.utils.StorageUtils;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;

@Controller
public class GoogleMediaController extends SCController {

	private static final Logger logger = Logger
			.getLogger(GoogleMediaController.class);

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
	LocalResourceManager localManager;

	@Autowired
	StorageUtils storageUtils;

	@RequestMapping(method = RequestMethod.GET, value = "/gdrivestorage/{accountId}/{localResourceId}")
	public @ResponseBody
	void getMyResource(@PathVariable("localResourceId") String localResourceId,
			@PathVariable("accountId") String accountId,
			HttpServletResponse response) throws SmartcampusException,
			IOException {
		LocalResource localRes = null;
		try {
			localRes = localManager.getLocalResById(localResourceId);
			logger.warn(localRes.getDate() + " " + System.currentTimeMillis());
			if (localRes.getDate() < System.currentTimeMillis()) {
				logger.warn(String.format("LocalResource %s time expired",
						localRes.getId()));
				throw new IllegalArgumentException("Token time expired");
			} else {
				StorageService storageService = storageUtils
						.getStorageServiceByAccount(accountId);
				InputStream fis = ((GoogleDriveStorage) storageService)
						.getResourceStream(localRes.getUrl(),
								localRes.getResourceId());
				OutputStream outStream = response.getOutputStream();
				byte[] buffer = new byte[2048 * 10000];
				int bytesRead = -1;
				// write bytes read from the input stream into the output stream
				while ((bytesRead = fis.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				fis.close();
				outStream.close();

			}
		} catch (NotFoundException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Wrong LocalResource id");
		} catch (FileNotFoundException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND,
					"Wrong filepath");
		}

	}

	@Override
	protected AuthServices getAuthServices() {
		// TODO Auto-generated method stub
		return null;
	}

}
