package eu.trentorise.smartcampus.filestorage;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;

import eu.trentorise.smartcampus.filestorage.client.HttpHeader;
import eu.trentorise.smartcampus.filestorage.client.RestCaller;
import eu.trentorise.smartcampus.filestorage.client.RestCaller.RequestType;
import eu.trentorise.smartcampus.filestorage.model.AppAccount;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;
import eu.trentorise.smartcampus.filestorage.utils.TestUtils;

public class RestTest {

	@Test
	public void sessionToken() throws JsonGenerationException,
			JsonMappingException, UnsupportedEncodingException, IOException,
			URISyntaxException {
		RestCaller caller = new RestCaller();
		List<HttpHeader> headers = Arrays.asList(new HttpHeader("AUTH_TOKEN",
				TestUtils.AUTH_TOKEN));

		// creation of appAccount
		AppAccount appAccount = TestUtils.createAppAccount("smartcampus");
		appAccount = caller.callOneResult(RequestType.POST, TestUtils.BASE_URL
				+ "/appaccount", headers, appAccount, AppAccount.class);

		// creation userAccount
		UserAccount userAccount = TestUtils.createUserAccount(appAccount,
				TestUtils.userId);
		userAccount = caller.callOneResult(RequestType.POST, TestUtils.BASE_URL
				+ "/useraccount", headers, userAccount, UserAccount.class);

		// storeResource

		File resource = new File(this.getClass().getResource("image.png")
				.toURI());
		String rid = caller.callOneResult(RequestType.POST, TestUtils.BASE_URL
				+ "/resource/smartcampus/" + userAccount.getId(), headers,
				resource, "file", String.class);
		Assert.assertNotNull(rid);

		// sessionToken
		Token sessionToken = caller.callOneResult(RequestType.GET,
				TestUtils.BASE_URL + "/resource/smartcampus/" + rid, headers,
				Token.class);
		Assert.assertNotNull(sessionToken);

		Metadata metadata = caller.callOneResult(RequestType.GET,
				TestUtils.BASE_URL + "/metadata/smartcampus/" + rid, headers,
				Metadata.class);
		Assert.assertNotNull(metadata);
	}

	@Test
	public void crudAppAccount() throws JsonGenerationException,
			JsonMappingException, UnsupportedEncodingException, IOException {
		// creation
		AppAccount appAccount = TestUtils.createAppAccount("smartcampus");

		RestCaller caller = new RestCaller();

		List<HttpHeader> headers = Arrays.asList(new HttpHeader("AUTH_TOKEN",
				TestUtils.AUTH_TOKEN));

		List<AppAccount> results = caller.callListResult(RequestType.GET,
				TestUtils.BASE_URL + "/appaccount/smartcampus", headers,
				AppAccount.class);

		Assert.assertEquals(0, results.size());

		appAccount = caller.callOneResult(RequestType.POST, TestUtils.BASE_URL
				+ "/appaccount", headers, appAccount, AppAccount.class);
		Assert.assertNotNull(appAccount.getId());

		// update
		Assert.assertEquals(2, appAccount.getConfigurations().size());
		appAccount.setConfigurations(new ArrayList<Configuration>());

		appAccount = caller.callOneResult(RequestType.PUT, TestUtils.BASE_URL
				+ "/appaccount/smartcampus", headers, appAccount,
				AppAccount.class);
		Assert.assertEquals(0, appAccount.getConfigurations().size());

		// delete
		boolean deleted = caller.callOneResult(
				RequestType.DELETE,
				TestUtils.BASE_URL + "/appaccount/smartcampus/"
						+ appAccount.getId(), headers, null, Boolean.class);

		Assert.assertTrue(deleted);

		results = caller.callListResult(RequestType.GET, TestUtils.BASE_URL
				+ "/appaccount/smartcampus", headers, AppAccount.class);

		Assert.assertEquals(0, results.size());

	}

}
