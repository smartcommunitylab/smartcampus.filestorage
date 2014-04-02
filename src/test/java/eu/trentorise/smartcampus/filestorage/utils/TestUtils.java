/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.social.model.User;

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
	public static final String userId = null;

	private static Random random = new Random();
	/**
	 * Utility methods
	 */

	@Autowired
	SocialEngineOperation socialEngine;

	public static Storage createAppAccount(String appId) {
		Storage account = new Storage();
		account.setAppId(appId);
		account.setName("smartcampus test");
		account.setStorageType(StorageType.DROPBOX);
		List<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new Configuration("APP_KEY", DropboxUtils.appkey));
		confs.add(new Configuration("APP_SECRET", DropboxUtils.appsecret));
		account.setConfigurations(confs);
		return account;
	}

	public static Account createUserAccount(Storage appAccount, String userId) {
		Account account = new Account();
		account.setId(new ObjectId().toString());
		account.setUserId(userId);
		account.setName("smartcampus test");
		account.setAppId(appAccount.getAppId());
		account.setStorageType(appAccount.getStorageType());

		List<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new Configuration("USER_KEY", DropboxUtils.userkey));
		confs.add(new Configuration("USER_SECRET", DropboxUtils.usersecret));

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
		user.setSocialId("" + socialUser.getId());
		user.setId("" + random.nextInt(1000));
		return user;
	}

}
