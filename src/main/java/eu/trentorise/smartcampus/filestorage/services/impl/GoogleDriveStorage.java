package eu.trentorise.smartcampus.filestorage.services.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropbox.client2.session.AppKeyPair;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.model.File;

import eu.trentorise.smartcampus.filestorage.managers.AccountManager;
import eu.trentorise.smartcampus.filestorage.managers.LocalResourceManager;
import eu.trentorise.smartcampus.filestorage.managers.StorageManager;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.LocalResource;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.services.MetadataService;
import eu.trentorise.smartcampus.filestorage.services.StorageService;

/**
 * Storage on a user Google account
 * 
 * 
 */
@Service
public class GoogleDriveStorage implements StorageService {
	private String LOCAL_URL = "http://localhost:8080/core.filestorage";
	private final long ONE_HOUR = 3600000;

	private static final Logger logger = Logger
			.getLogger(GoogleDriveStorage.class);

	private static final String REFRESH_TOKEN = "REFRESH_TOKEN";

	private static final String APP_KEY = "APP_KEY";
	private static final String APP_SECRET = "APP_SECRET";

	@Autowired
	private AccountManager accountManager;

	@Autowired
	StorageManager appAccountManager;

	@Autowired
	private MetadataService metaService;

	@Autowired
	LocalResourceManager localManager;

	@Override
	public Resource store(String accountId, Resource resource)
			throws AlreadyStoredException, SmartcampusException {
		// check if file already exists int
		return store(accountId, resource, null);
	}

	@Override
	public Resource store(String accountId, Resource resource,
			InputStream inputStream) throws AlreadyStoredException,
			SmartcampusException {
		if (inputStream == null) {
			inputStream = new ByteArrayInputStream(resource.getContent());
		}
		// check if file already exists int

		String refreshToken = null;
		AppKeyPair app = null;
		try {
			refreshToken = getUserToken(accountId);
			app = getAppToken(accountId);
			logger.info("Retrieved gdrive account informations");
		} catch (NotFoundException e2) {
			throw new SmartcampusException(e2);
		}
		GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(new JacksonFactory())
				.setTransport(new NetHttpTransport())
				.setClientSecrets(app.key, app.secret).build();
		credential.setRefreshToken(refreshToken);
		Drive service = new Drive.Builder(new NetHttpTransport(),
				new JacksonFactory(), credential).build();

		try {
			File body = new File();
			body.setTitle(FilenameUtils.getBaseName(resource.getName()));
			body.setMimeType(resource.getContentType());
			InputStreamContent mediaContent = new InputStreamContent(
					resource.getContentType(), new BufferedInputStream(
							inputStream));
			mediaContent.setLength(resource.getSize());
			Insert insert = service.files().insert(body, mediaContent);
			MediaHttpUploader uploader = insert.getMediaHttpUploader();
			uploader.setDirectUploadEnabled(false);
			File file = insert.execute();
			if (resource.getId() == null) {
				// TODO per ora l'id è quello di google drive
				resource.setId(new ObjectId().toString());
			}
			resource.setExternalId(file.getId());
			inputStream.close();
			return resource;
		} catch (IOException e) {
			throw new SmartcampusException(e);
		}

	}

	@Override
	public void replace(Resource resource) throws NotFoundException,
			SmartcampusException {
		replace(resource, null);
	}

	@Override
	public void replace(Resource resource, InputStream inputStream)
			throws NotFoundException, SmartcampusException {
		if (resource.getId() == null) {
			throw new NotFoundException();
		}
		if (inputStream == null) {
			inputStream = new ByteArrayInputStream(resource.getContent());
		}
		Metadata meta = metaService.getMetadata(resource.getId());
		String refreshToken = null;
		AppKeyPair app = null;
		try {
			refreshToken = getUserToken(meta.getAccountId());
			app = getAppToken(meta.getAccountId());
		} catch (NotFoundException e2) {
			throw new SmartcampusException(e2);
		}
		GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(new JacksonFactory())
				.setTransport(new NetHttpTransport())
				.setClientSecrets(app.key, app.secret).build();
		credential.setRefreshToken(refreshToken);
		Drive service = new Drive.Builder(new NetHttpTransport(),
				new JacksonFactory(), credential).build();

		try {
			service.files().delete(meta.getFileExternalId()).execute();
			File body = new File();
			body.setTitle(FilenameUtils.getBaseName(resource.getName()));
			body.setMimeType(resource.getContentType());
			InputStreamContent mediaContent = new InputStreamContent(
					resource.getContentType(), new BufferedInputStream(
							inputStream));
			mediaContent.setLength(resource.getSize());
			Insert insert = service.files().insert(body, mediaContent);
			MediaHttpUploader uploader = insert.getMediaHttpUploader();
			uploader.setDirectUploadEnabled(false);
			File file = insert.execute();
			logger.info("Resource stored on gdrive");
			resource.setExternalId(file.getId());
			inputStream.close();
		} catch (IOException e) {
			throw new SmartcampusException(e);
		}

	}

	@Override
	public void remove(String rid) throws NotFoundException,
			SmartcampusException {
		Metadata metadata = metaService.getMetadata(rid);
		// get user token

		String refreshToken = null;
		AppKeyPair app = null;

		try {
			refreshToken = getUserToken(metadata.getAccountId());
			app = getAppToken(metadata.getAccountId());
		} catch (NotFoundException e2) {
			throw new SmartcampusException(e2);
		}
		// find resource name

		GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(new JacksonFactory())
				.setTransport(new NetHttpTransport())
				.setClientSecrets(app.key, app.secret).build();
		credential.setRefreshToken(refreshToken);
		Drive service = new Drive.Builder(new NetHttpTransport(),
				new JacksonFactory(), credential).build();

		try {
			service.files().delete(metadata.getFileExternalId()).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Token getToken(String accountId, String rid)
			throws NotFoundException, SmartcampusException {
		String refreshToken = null;
		AppKeyPair app = null;
		LocalResource localRes = new LocalResource();
		try {
			refreshToken = getUserToken(accountId);
			app = getAppToken(accountId);
		} catch (NotFoundException e2) {
			throw new SmartcampusException(e2);
		}

		Metadata metadata = metaService.getMetadata(rid);
		Storage appAccount = appAccountManager.getStorageByAppId(metadata
				.getAppId());

		GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(new JacksonFactory())
				.setTransport(new NetHttpTransport())
				.setClientSecrets(app.key, app.secret).build();
		credential.setRefreshToken(refreshToken);
		Drive service = new Drive.Builder(new NetHttpTransport(),
				new JacksonFactory(), credential).build();
		File file = null;

		try {
			file = service.files().get(metadata.getFileExternalId()).execute();
		} catch (IOException e) {
			e.printStackTrace();
		}

		localRes.setId(new ObjectId().toString());
		// Valid for 24-hours
		localRes.setDate(System.currentTimeMillis() + ONE_HOUR * 24);
		localRes.setUrl(file.getDownloadUrl());
		localRes.setResourceId(rid);

		try {
			localManager.save(localRes);
		} catch (AlreadyStoredException e) {
			e.printStackTrace();
		}

		Token userSessionToken = new Token();
		userSessionToken.setUrl(LOCAL_URL + "/gdrivestorage/" + accountId + "/"
				+ localRes.getId());
		userSessionToken.setMethodREST("GET");
		userSessionToken.setStorageType(appAccount.getStorageType());
		return userSessionToken;
	}

	@Override
	public InputStream getThumbnailStream(String resourceId)
			throws NotFoundException, SmartcampusException {
		Metadata metadata = metaService.getMetadata(resourceId);
		AppKeyPair app = null;
		String refreshToken = null;
		try {
			app = getAppToken(metadata.getAccountId());
			refreshToken = getUserToken(metadata.getAccountId());
		} catch (NotFoundException e) {
			throw new SmartcampusException(e);
		}
		GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(new JacksonFactory())
				.setTransport(new NetHttpTransport())
				.setClientSecrets(app.key, app.secret).build();
		credential.setRefreshToken(refreshToken);
		Drive service = new Drive.Builder(new NetHttpTransport(),
				new JacksonFactory(), credential).build();
		try {
			File f = service.files().get(metadata.getFileExternalId())
					.execute();

			HttpResponse resp;
			resp = service.getRequestFactory()
					.buildGetRequest(new GenericUrl(f.getThumbnailLink()))
					.execute();
			return resp.getContent();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	private String getUserToken(String accountId) throws NotFoundException {
		Account account = accountManager.findById(accountId);
		return getUserToken(account.getConfigurations());
	}

	private AppKeyPair getAppToken(String accountId) throws NotFoundException {
		Account account = accountManager.findById(accountId);
		return getAppTokenByApp(account.getAppId());
	}

	private AppKeyPair getAppTokenByApp(String appId) throws NotFoundException {
		Storage appAccount = appAccountManager.getStorageByAppId(appId);
		return getAppToken(appAccount.getConfigurations());
	}

	private AppKeyPair getAppToken(List<Configuration> confs) {
		String appKey = null;
		String appSecret = null;
		if (confs == null) {
			return null;
		}
		for (Configuration tmp : confs) {
			if (tmp.getName().equals(APP_KEY)) {
				appKey = tmp.getValue();
			}
			if (tmp.getName().equals(APP_SECRET)) {
				appSecret = tmp.getValue();
			}
		}
		return new AppKeyPair(appKey, appSecret);

	}

	private String getUserToken(List<Configuration> confs) {
		String refreshToken = null;

		if (confs == null) {
			return null;
		}
		for (Configuration tmp : confs) {
			if (tmp.getName().equals(REFRESH_TOKEN)) {
				refreshToken = tmp.getValue();
			}
		}

		return refreshToken;
	}

	public InputStream getResourceStream(String url, String resourceId)
			throws NotFoundException, SmartcampusException {
		Metadata metadata = metaService.getMetadata(resourceId);
		AppKeyPair app = null;
		String refreshToken = null;
		try {
			app = getAppToken(metadata.getAccountId());
			refreshToken = getUserToken(metadata.getAccountId());
		} catch (NotFoundException e) {
			throw new SmartcampusException(e);
		}
		GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(new JacksonFactory())
				.setTransport(new NetHttpTransport())
				.setClientSecrets(app.key, app.secret).build();
		credential.setRefreshToken(refreshToken);
		Drive service = new Drive.Builder(new NetHttpTransport(),
				new JacksonFactory(), credential).build();
		try {
			HttpResponse resp = service.getRequestFactory()
					.buildGetRequest(new GenericUrl(url)).execute();
			return resp.getContent();
		} catch (IOException e) {
			// An error occurred.
			e.printStackTrace();
			return null;
		}
	}

}
