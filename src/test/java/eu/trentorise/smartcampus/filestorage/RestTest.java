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
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.utils.TestUtils;

public class RestTest {

	@Test
	public void sessionToken() throws JsonGenerationException,
			JsonMappingException, UnsupportedEncodingException, IOException,
			URISyntaxException {
		RestCaller caller = new RestCaller();
		List<HttpHeader> headers = Arrays.asList(new HttpHeader("AUTH_TOKEN",
				TestUtils.AUTH_TOKEN));

		String appId = "smartcampus";
		// creation of appAccount
		Storage appAccount = TestUtils.createAppAccount(appId);
		appAccount = caller.callOneResult(RequestType.POST, TestUtils.BASE_URL
				+ "/appaccount", headers, appAccount, Storage.class);

		// creation userAccount
		Account userAccount = TestUtils.createUserAccount(appAccount,
				TestUtils.userId);
		userAccount = caller.callOneResult(RequestType.POST, TestUtils.BASE_URL
				+ "/useraccount/" + appId, headers, userAccount, Account.class);

		// storeResource

		File resource = new File(this.getClass().getResource("image.png")
				.toURI());
		Metadata info = caller.callOneResult(
				RequestType.POST,
				TestUtils.BASE_URL + "/resource/" + appId + "/"
						+ userAccount.getId() + "?createSocialData=false",
				headers, resource, "file", Metadata.class);
		Assert.assertNotNull(info.getResourceId());

		// sessionToken
		Token sessionToken = caller.callOneResult(
				RequestType.GET,
				TestUtils.BASE_URL + "/myresource/" + appId + "/"
						+ info.getResourceId(), headers, Token.class);
		Assert.assertNotNull(sessionToken);

		Metadata metadata = caller.callOneResult(
				RequestType.GET,
				TestUtils.BASE_URL + "/metadata/" + appId + "/"
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

		appAccount = caller
				.callOneResult(RequestType.PUT, TestUtils.BASE_URL
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
