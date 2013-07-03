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
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.utils.TestUtils;

public class RestTest {

	@Test
	public void sessionToken() throws JsonGenerationException,
			JsonMappingException, UnsupportedEncodingException, IOException,
			URISyntaxException {
		RestCaller caller = new RestCaller();
		List<HttpHeader> headers = Arrays.asList(new HttpHeader("AUTH_TOKEN",
				TestUtils.AUTH_TOKEN));

		String appName = "smartcampus";
		// creation of appAccount
		Storage appAccount = TestUtils.createAppAccount(appName);
		appAccount = caller.callOneResult(RequestType.POST, TestUtils.BASE_URL
				+ "/appaccount", headers, appAccount, Storage.class);

		// creation userAccount
		Account userAccount = TestUtils.createUserAccount(appAccount,
				TestUtils.userId);
		userAccount = caller.callOneResult(RequestType.POST, TestUtils.BASE_URL
				+ "/useraccount/" + appName, headers, userAccount,
				Account.class);

		// storeResource

		File resource = new File(this.getClass().getResource("image.png")
				.toURI());
		Metadata info = caller.callOneResult(
				RequestType.POST,
				TestUtils.BASE_URL + "/resource/" + appName + "/"
						+ userAccount.getId() + "?createSocialData=false",
				headers, resource, "file", Metadata.class);
		Assert.assertNotNull(info.getResourceId());

		// sessionToken
		Token sessionToken = caller.callOneResult(
				RequestType.GET,
				TestUtils.BASE_URL + "/myresource/" + appName + "/"
						+ info.getResourceId(), headers, Token.class);
		Assert.assertNotNull(sessionToken);

		Metadata metadata = caller.callOneResult(
				RequestType.GET,
				TestUtils.BASE_URL + "/metadata/" + appName + "/"
						+ info.getResourceId(), headers, Metadata.class);
		Assert.assertNotNull(metadata);
	}

	@Test
	public void crudAppAccount() throws JsonGenerationException,
			JsonMappingException, UnsupportedEncodingException, IOException {
		// creation
		Storage appAccount = TestUtils.createAppAccount("smartcampus");

		RestCaller caller = new RestCaller();

		List<HttpHeader> headers = Arrays.asList(new HttpHeader("AUTH_TOKEN",
				TestUtils.AUTH_TOKEN));

		List<Storage> results = caller.callListResult(RequestType.GET,
				TestUtils.BASE_URL + "/appaccount/smartcampus", headers,
				Storage.class);

		Assert.assertEquals(0, results.size());

		appAccount = caller.callOneResult(RequestType.POST, TestUtils.BASE_URL
				+ "/appaccount", headers, appAccount, Storage.class);
		Assert.assertNotNull(appAccount.getId());

		// update
		Assert.assertEquals(2, appAccount.getConfigurations().size());
		appAccount.setConfigurations(new ArrayList<Configuration>());

		appAccount = caller.callOneResult(RequestType.PUT, TestUtils.BASE_URL
				+ "/appaccount/smartcampus", headers, appAccount,
				Storage.class);
		Assert.assertEquals(0, appAccount.getConfigurations().size());

		// delete
		boolean deleted = caller.callOneResult(
				RequestType.DELETE,
				TestUtils.BASE_URL + "/appaccount/smartcampus/"
						+ appAccount.getId(), headers, null, Boolean.class);

		Assert.assertTrue(deleted);

		results = caller.callListResult(RequestType.GET, TestUtils.BASE_URL
				+ "/appaccount/smartcampus", headers, Storage.class);

		Assert.assertEquals(0, results.size());

	}

}
