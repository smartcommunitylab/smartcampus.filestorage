package eu.trentorise.smartcampus.filestorage.services.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dropbox.client2.session.AppKeyPair;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files.Insert;
import com.google.api.services.drive.DriveScopes;
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
import eu.trentorise.smartcampus.filestorage.model.StorageType;
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
	private final long ONE_HOUR = 3600000;
	@Value("${local.url}")
	private String localUrl;
	private String FILESTORAGE_URI = "core.filestorage/";
	private String GOOGLE_REDIRECT_URI = "localstorage/google";
	private static final String REFRESH_TOKEN = "REFRESH_TOKEN";
	private static final String APP_KEY = "APP_KEY";
	private static final String APP_SECRET = "APP_SECRET";
	private static final Logger logger = Logger
			.getLogger(GoogleDriveStorage.class);
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
		return store(accountId, resource, null);
	}

	@Override
	public Resource store(String accountId, Resource resource,
			InputStream inputStream) throws AlreadyStoredException,
			SmartcampusException {
		if (inputStream == null) {
			inputStream = new ByteArrayInputStream(resource.getContent());
		}
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
				resource.setId(new ObjectId().toString());
			}
			resource.setExternalId(file.getId());
			inputStream.close();
			return resource;
		} catch (IOException e) {
			logger.error("Storing file error");
			throw new SmartcampusException(e);
		}

	}

	@Override
	public void replace(Resource resource) throws NotFoundException,
			SmartcampusException {
		replace(resource, null);
	}

	private AppKeyPair getAppTokenByStorage(String storageId)
			throws NotFoundException {
		Storage appAccount = appAccountManager.getStorageById(storageId);

		return getAppToken(appAccount.getConfigurations());
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
		String refreshToken = null;
		AppKeyPair app = null;
		try {
			refreshToken = getUserToken(metadata.getAccountId());
			app = getAppToken(metadata.getAccountId());
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
			service.files().delete(metadata.getFileExternalId()).execute();
		} catch (IOException e) {
			logger.error("Error deleting file");
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
			logger.error("Error retrieving the file in google drive storage");
		}

		localRes.setId(new ObjectId().toString());
		// Valid for 24-hours
		localRes.setDate(System.currentTimeMillis() + ONE_HOUR * 24);
		localRes.setUrl(file.getDownloadUrl());
		localRes.setResourceId(rid);

		try {
			localManager.save(localRes);
		} catch (AlreadyStoredException e) {
			logger.error("Saving LocalResource in database failed");
		}

		Token userSessionToken = new Token();
		userSessionToken.setUrl(localUrl + "/gdrivestorage/" + accountId + "/"
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
			logger.error("Thumbnail retriving failed");
		}

		return null;
	}

	@Override
	public boolean authorizationSessionRequired() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void startSession(String storageId, String userId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession session = request.getSession();
		AppKeyPair app = getAppTokenByStorage(storageId);
		HttpTransport httpTransport = new NetHttpTransport();
		JsonFactory jsonFactory = new JacksonFactory();
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
				httpTransport, jsonFactory, app.key, app.secret,
				Arrays.asList(DriveScopes.DRIVE)).setApprovalPrompt("force")
				.setAccessType("offline").build();
		String url = flow
				.newAuthorizationUrl()
				.setRedirectUri(
						localUrl + FILESTORAGE_URI + GOOGLE_REDIRECT_URI)
				.build();
		session.setAttribute("flow", flow);
		session.setAttribute("userId", userId);
		session.setAttribute("storageId", storageId);
		logger.info("Started session, created url: " + url);
		response.sendRedirect(url);
	}

	@Override
	public Account finishSession(String storageId, String userId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		AppKeyPair app = getAppTokenByStorage(storageId);
		String code = (String) request.getSession().getAttribute("code");
		HttpSession session = request.getSession();
		GoogleAuthorizationCodeFlow flow = (GoogleAuthorizationCodeFlow) session
				.getAttribute("flow");
		GoogleTokenResponse tokenResponse = flow
				.newTokenRequest(code)
				.setRedirectUri(
						localUrl + FILESTORAGE_URI + GOOGLE_REDIRECT_URI)
				.execute();
		GoogleCredential credential = new GoogleCredential.Builder()
				.setJsonFactory(new JacksonFactory())
				.setTransport(new NetHttpTransport())
				.setClientSecrets(app.key, app.secret).build();
		credential.setFromTokenResponse(tokenResponse);
		String refreshToken = credential.getRefreshToken();
		Account a = new Account();
		Storage storage = appAccountManager.getStorageById(storageId);
		a.setAppId(storage.getAppId());
		a.setUserId(userId);
		a.setName(null);
		a.setStorageType(StorageType.GDRIVE);
		a.setConfigurations(Arrays
				.asList(new Configuration[] { new Configuration(REFRESH_TOKEN,
						refreshToken) }));
		a.setId(new ObjectId().toString());
		logger.debug("Finished session and created account: " + a.getId());
		return a;
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
			logger.error("Error returning the resource stream");
			return null;
		}
	}

}
