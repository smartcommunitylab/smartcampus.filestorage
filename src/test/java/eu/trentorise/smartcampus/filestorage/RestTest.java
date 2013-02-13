package eu.trentorise.smartcampus.filestorage;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;

import eu.trentorise.smartcampus.filestorage.client.HttpHeader;
import eu.trentorise.smartcampus.filestorage.client.RestCaller;
import eu.trentorise.smartcampus.filestorage.client.RestCaller.RequestType;
import eu.trentorise.smartcampus.filestorage.model.AppAccount;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;
import eu.trentorise.smartcampus.filestorage.utils.DropboxUtils;

public class RestTest {

	private static final String BASE_URL = "http://localhost:8280/smartcampus.filestorage";

	private static final String AUTH_TOKEN = "aee58a92-d42d-42e8-b55e-12e4289586fc";
	private static final long userId = 21L;

	@Test
	public void sessionToken() throws JsonGenerationException,
			JsonMappingException, UnsupportedEncodingException, IOException,
			URISyntaxException {
		RestCaller caller = new RestCaller();
		List<HttpHeader> headers = Arrays.asList(new HttpHeader("AUTH_TOKEN",
				AUTH_TOKEN));

		// creation of appAccount
		AppAccount appAccount = createAppAccount("smartcampus");
		appAccount = caller.callOneResult(RequestType.POST, BASE_URL
				+ "/appaccount", headers, appAccount, AppAccount.class);

		// creation userAccount
		UserAccount userAccount = createUserAccount(appAccount, userId);
		userAccount = caller.callOneResult(RequestType.POST, BASE_URL
				+ "/useraccount", headers, userAccount, UserAccount.class);

		// storeResource

		File resource = new File(this.getClass().getResource("image.png")
				.toURI());
		String rid = caller.callOneResult(RequestType.POST, BASE_URL
				+ "/resource/smartcampus/" + userAccount.getId(), headers,
				resource, "file", String.class);
		Assert.assertNotNull(rid);

		// sessionToken
		Token sessionToken = caller.callOneResult(RequestType.GET, BASE_URL
				+ "/resource/smartcampus/" + rid, headers, Token.class);
		Assert.assertNotNull(sessionToken);

		Metadata metadata = caller.callOneResult(RequestType.GET, BASE_URL
				+ "/metadata/smartcampus/" + rid, headers, Metadata.class);
		Assert.assertNotNull(metadata);
	}

	@Test
	public void crudAppAccount() throws JsonGenerationException,
			JsonMappingException, UnsupportedEncodingException, IOException {
		// creation
		AppAccount appAccount = createAppAccount("smartcampus");

		RestCaller caller = new RestCaller();

		List<HttpHeader> headers = Arrays.asList(new HttpHeader("AUTH_TOKEN",
				AUTH_TOKEN));

		List<AppAccount> results = caller
				.callListResult(RequestType.GET, BASE_URL
						+ "/appaccount/smartcampus", headers, AppAccount.class);

		Assert.assertEquals(0, results.size());

		appAccount = caller.callOneResult(RequestType.POST, BASE_URL
				+ "/appaccount", headers, appAccount, AppAccount.class);
		Assert.assertNotNull(appAccount.getId());

		// update
		Assert.assertEquals(2, appAccount.getConfigurations().size());
		appAccount.setConfigurations(new ArrayList<Configuration>());

		appAccount = caller.callOneResult(RequestType.PUT, BASE_URL
				+ "/appaccount/smartcampus", headers, appAccount,
				AppAccount.class);
		Assert.assertEquals(0, appAccount.getConfigurations().size());

		// delete
		boolean deleted = caller.callOneResult(RequestType.DELETE, BASE_URL
				+ "/appaccount/smartcampus/" + appAccount.getId(), headers,
				null, Boolean.class);

		results = caller.callListResult(RequestType.GET, BASE_URL
				+ "/appaccount/smartcampus", headers, AppAccount.class);

		Assert.assertEquals(0, results.size());

	}

	private AppAccount createAppAccount(String appName) {
		AppAccount account = new AppAccount();
		account.setAppName(appName);
		account.setAppAccountName("smartcampus test");
		account.setStorageType(StorageType.DROPBOX);
		List<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new Configuration("APP_KEY", DropboxUtils.appkey));
		confs.add(new Configuration("APP_SECRET", DropboxUtils.appsecret));
		account.setConfigurations(confs);
		return account;
	}

	private UserAccount createUserAccount(AppAccount appAccount, long userId) {
		UserAccount account = new UserAccount();
		account.setId(new ObjectId().toString());
		account.setUserId(userId);
		account.setAccountName("smartcampus test");
		account.setAppAccountId(appAccount.getId());
		account.setAppName(appAccount.getAppName());
		account.setStorageType(appAccount.getStorageType());

		List<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new Configuration("USER_KEY", DropboxUtils.userkey));
		confs.add(new Configuration("USER_SECRET", DropboxUtils.usersecret));

		account.setConfigurations(confs);
		return account;

	}
}
