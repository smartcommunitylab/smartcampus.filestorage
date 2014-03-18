package eu.trentorise.smartcampus.filestorage.services.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import eu.trentorise.smartcampus.filestorage.managers.AccountManager;
import eu.trentorise.smartcampus.filestorage.managers.LocalResourceManager;
import eu.trentorise.smartcampus.filestorage.managers.MetadataManager;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.LocalResource;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.services.StorageService;

@Component
public class LocalStorage implements StorageService {
	private String LOCAL_STORAGE_PATH = "C:\\local_storage";
	private String LOCAL_URL = "http:\\localhost:8080\\core.filestorage";
	private long ONE_HOUR = 3600000;
	private static final Logger logger = Logger.getLogger(LocalStorage.class);

	@Autowired
	LocalResourceManager localManager;

	@Autowired
	AccountManager accountManager;

	@Autowired
	MetadataManager metadataManager;

	@Override
	public Resource store(String accountId, Resource resource)
			throws AlreadyStoredException, SmartcampusException {
		try {
			Account account = accountManager.findById(accountId);
			if (!isFileExist(LOCAL_STORAGE_PATH)) {
				File folder = new File(LOCAL_STORAGE_PATH);
				folder.mkdirs();
			}
			if (!isFileExist(LOCAL_STORAGE_PATH + "\\" + account.getUserId())) {
				File accountFolder = new File(LOCAL_STORAGE_PATH + "\\"
						+ account.getUserId());
				accountFolder.mkdirs();
			}

			File fileToStore = new File(LOCAL_STORAGE_PATH + "\\"
					+ account.getUserId() + "\\" + resource.getName());
			// Rename the file if it's already exist
			if (fileToStore.exists()) {
				Integer cont = 1;
				File file_temp = fileToStore;
				while (file_temp.exists()) {
					file_temp = new File(LOCAL_STORAGE_PATH
							+ "\\"
							+ account.getUserId()
							+ "\\"
							+ FilenameUtils.removeExtension(fileToStore
									.getName()) + "(" + cont + ")."
							+ FilenameUtils.getExtension(fileToStore.getName()));
					cont++;

				}
				// Decrease variable 'cont' to rename the resource
				cont -= 1;
				resource.setName(FilenameUtils.removeExtension(fileToStore
						.getName())
						+ "("
						+ cont
						+ ")."
						+ FilenameUtils.getExtension(fileToStore.getName()));
				fileToStore = file_temp;
			}
			// Creation of the file
			try {
				FileOutputStream fileOuputStream;
				fileOuputStream = new FileOutputStream(fileToStore);
				fileOuputStream.write(resource.getContent());
				fileOuputStream.close();
				logger.info(String.format("Created file %s",
						fileToStore.getName()));
			} catch (FileNotFoundException e) {
				logger.error("File not found.");
			} catch (IOException e) {
				logger.error("Unable to convert resource to file.");
			}
			// Creation of the id
			if (resource.getId() == null) {
				resource.setId(new ObjectId().toString());
			}

		} catch (NotFoundException e) {
			logger.error(String.format("Account %s not found", accountId));
		}
		return resource;
	}

	@Override
	public void replace(Resource resource) throws NotFoundException,
			SmartcampusException {
		if (resource.getId() == null) {
			throw new NotFoundException();
		}
		Metadata metadata = metadataManager.findByResource(resource.getId());
		Account account = accountManager.findById(metadata.getAccountId());
		File fileToReplace = new File(LOCAL_STORAGE_PATH + "\\"
				+ account.getUserId() + "\\" + metadata.getName());
		if (!fileToReplace.exists()) {
			throw new NotFoundException();
		} else {
			try {
				FileOutputStream fileOuputStream;
				fileOuputStream = new FileOutputStream(fileToReplace);
				fileOuputStream.write(resource.getContent());
				fileOuputStream.close();
			} catch (FileNotFoundException e) {
				logger.error("File not found.");
			} catch (IOException e) {
				logger.error("Unable to convert resource to file.");
			}
		}
		logger.info(String.format("Replaced file %s", metadata.getName()));
	}

	@Override
	public void remove(String rid) throws NotFoundException,
			SmartcampusException {
		Metadata metadata = metadataManager.findByResource(rid);
		Account account = accountManager.findById(metadata.getAccountId());
		File fileToDelete = new File(LOCAL_STORAGE_PATH + "\\"
				+ account.getUserId() + "\\" + metadata.getName());
		if (!fileToDelete.exists()) {
			throw new NotFoundException();
		} else {
			fileToDelete.delete();
			logger.info(String.format("Removed file %s", metadata.getName()));
		}

	}

	@Override
	public Token getToken(String accountId, String rid)
			throws NotFoundException, SmartcampusException {
		LocalResource localRes = new LocalResource();
		Token token = new Token();
		Metadata metadata = metadataManager.findByResource(rid);
		Account account = accountManager.findById(metadata.getAccountId());
		File fileToGet = new File(LOCAL_STORAGE_PATH + "\\"
				+ account.getUserId() + "\\" + metadata.getName());
		if (!fileToGet.exists()) {
			throw new NotFoundException();
		}
		try {
			localRes.setId(new ObjectId().toString());
			// Valid for 24-hours
			localRes.setDate(System.currentTimeMillis() + ONE_HOUR * 24);
			localRes.setUrl(fileToGet.getAbsolutePath());
			localRes.setResourceId(rid);
			localManager.save(localRes);
		} catch (AlreadyStoredException e) {
			logger.error("Cannot save LocalResource data in database");
		}

		token.setUrl(LOCAL_URL + "\\localstorage\\" + localRes.getId());
		token.setMethodREST("GET");
		token.setStorageType(StorageType.LOCAL);
		return token;
	}

	@Override
	public InputStream getThumbnailStream(String resourceId)
			throws NotFoundException, SmartcampusException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean authorizationSessionRequired() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void startSession(String storageId, String userId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Account finishSession(String storageId, String userId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	private Boolean isFileExist(String path) {
		if (new File(path).exists()) {
			return true;
		}
		return false;
	}

}