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

package eu.trentorise.smartcampus.filestorage.managers;

import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.User;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.Operation;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.services.impl.ScAcl;
import eu.trentorise.smartcampus.filestorage.utils.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/spring/SpringAppDispatcher-servlet.xml")
public class ScAclTest {

	@Autowired
	AccountManager accountManager;

	@Autowired
	StorageManager appAccountManager;

	@Autowired
	MediaManager mediaManager;

	@Autowired
	SocialEngineOperation socialEngine;

	@Autowired
	MetadataManager metaManager;

	@Autowired
	TestUtils testUtils;

	@Autowired
	ScAcl aclManager;

	private Resource resource;
	private Account userAccount;
	private User user;
	private Storage appAccount;

	@Before
	public void setup() throws URISyntaxException, IOException, WebApiException {
		try {
			resource = testUtils.createResource();
			appAccount = TestUtils.createAppAccount("sc test");
			appAccount = appAccountManager.save(appAccount);

			user = testUtils.createUser();

			userAccount = TestUtils.createUserAccount(appAccount, user.getId());
			userAccount = accountManager.save(userAccount);

			resource = mediaManager.storage(userAccount.getId(), user,
					resource, false);
		} catch (Exception e) {
			Assert.fail("Exception in setup env");
		}
	}

	@Test
	public void getMyResources() {
		try {
			Assert.assertNotNull(aclManager.getSessionToken(Operation.DOWNLOAD,
					user, resource.getId(), true));
		} catch (SecurityException e) {
			Assert.fail("SecurityException");
		} catch (SmartcampusException e) {
			Assert.fail("General exception");
		}

		User otherUser;
		try {
			otherUser = testUtils.createUser();
			Assert.assertNull(aclManager.getSessionToken(Operation.DOWNLOAD,
					otherUser, resource.getId(), true));
		} catch (WebApiException e) {
			Assert.fail("SocialEngine exception");
		} catch (SecurityException e) {
			return;
		} catch (SmartcampusException e) {
			Assert.fail("General exception");
		}

		Assert.fail("SecurityException not occurred");
	}

	@After
	public void cleanup() {
		try {
			mediaManager.remove(resource.getId());
		} catch (Exception e) {
		}
		accountManager.delete(userAccount);
		appAccountManager.delete(appAccount.getId());
	}
}
