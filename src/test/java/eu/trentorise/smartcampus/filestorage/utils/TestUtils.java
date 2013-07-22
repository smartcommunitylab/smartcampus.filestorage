package eu.trentorise.smartcampus.filestorage.utils;

import it.unitn.disi.sweb.webapi.client.WebApiException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.filestorage.model.AppAccount;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;

@Service
public class TestUtils {

	/**
	 * Put valid test data
	 */

	/**
	 * base URL of application web
	 */
	public static final String BASE_URL = "http://localhost:8080/smartcampus.filestorage";
	/**
	 * authorization token of user to validate REST calls
	 */
	public static final String AUTH_TOKEN = "";

	/**
	 * system user id
	 */
	public static final long userId = -1L;

	private static Random random = new Random();
	/**
	 * Utility methods
	 */

	@Autowired
	SocialEngineOperation socialEngine;

	public static AppAccount createAppAccount(String appName) {
		AppAccount account = new AppAccount();
		account.setAppName(appName);
		account.setAppAccountName("smartcampus test");
		account.setStorageType(StorageType.DROPBOX);
		List<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new Configuration("APP_KEY", DropboxTestUtils.appkey));
		confs.add(new Configuration("APP_SECRET", DropboxTestUtils.appsecret));
		account.setConfigurations(confs);
		return account;
	}

	public static UserAccount createUserAccount(AppAccount appAccount,
			long userId) {
		UserAccount account = new UserAccount();
		account.setId(new ObjectId().toString());
		account.setUserId(userId);
		account.setAccountName("smartcampus test");
		account.setAppAccountId(appAccount.getId());
		account.setAppName(appAccount.getAppName());
		account.setStorageType(appAccount.getStorageType());

		List<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new Configuration("USER_KEY", DropboxTestUtils.userkey));
		confs.add(new Configuration("USER_SECRET", DropboxTestUtils.usersecret));

		account.setConfigurations(confs);
		return account;

	}

	public static File getSampleTextFile(String fileContent) throws IOException {
		File t = File.createTempFile("dropbox", ".txt");
		t.deleteOnExit();
		FileWriter fw = new FileWriter(t);
		fw.write(fileContent);
		fw.close();
		return t;
	}

	public Resource createResource() throws URISyntaxException, IOException {
		File png = new File(getClass().getResource(
				"/eu/trentorise/smartcampus/filestorage/image.png").toURI());
		Resource res = new Resource();
		res.setContent(FileUtils.readFileToByteArray(png));
		res.setContentType("image/png");
		res.setName("image.png");
		return res;
	}

	public User createUser() throws WebApiException {
		User user = new User();
		it.unitn.disi.sweb.webapi.model.smartcampus.social.User socialUser = socialEngine
				.createUser();
		user.setSocialId(socialUser.getId());
		user.setId((long) random.nextInt(1000));
		user.setAuthToken("token");
		user.setExpTime(System.currentTimeMillis() + 3600 * 1000);
		return user;
	}

}
