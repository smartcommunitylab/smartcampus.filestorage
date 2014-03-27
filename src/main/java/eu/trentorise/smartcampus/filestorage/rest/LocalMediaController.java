package eu.trentorise.smartcampus.filestorage.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import eu.trentorise.smartcampus.filestorage.model.LocalResource;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;

@Controller
public class LocalMediaController extends SCController {
	private static final Logger logger = Logger
			.getLogger(LocalMediaController.class);

	@Autowired
	LocalResourceManager localManager;

	@Autowired
	MetadataManager metadataManager;

	@Autowired
	MediaManager mediaManager;

	@Autowired
	AccountManager accountManager;

	@Override
	protected AuthServices getAuthServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/localstorage/{localResourceId}")
	public @ResponseBody
	void getMyResource(@PathVariable("localResourceId") String localResourceId,
			HttpServletResponse response) throws SmartcampusException,
			NotFoundException, IOException {
		byte[] bFile = null;
		FileInputStream fileInputStream = null;
		LocalResource localRes = null;
		localRes = localManager.getLocalResById(localResourceId);
		if (localRes.getDate() < System.currentTimeMillis()) {
			logger.warn(String.format("LocalResource %s time expired",
					localRes.getId()));
			throw new IllegalArgumentException("Token time expired");
		} else {
			FileInputStream fis = new FileInputStream(new File(
					localRes.getUrl()));
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

	}

}
