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

import it.unitn.disi.sweb.webapi.client.WebApiException;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.filestorage.managers.StorageManager;
import eu.trentorise.smartcampus.filestorage.managers.MediaManager;
import eu.trentorise.smartcampus.filestorage.managers.MetadataManager;
import eu.trentorise.smartcampus.filestorage.managers.AccountManager;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Operation;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.utils.SocialEngineOperation;
import eu.trentorise.smartcampus.filestorage.utils.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/spring/SpringAppDispatcher-servlet.xml")
public class CompleteScenarioTest {

	@Autowired
	AccountManager accountManager;

	@Autowired
	StorageManager appAccountManager;

	@Autowired
	MediaManager mediaManager;

	@Autowired
	MetadataManager metaManager;

	@Autowired
	SocialEngineOperation socialEngine;

	@Autowired
	TestUtils testUtils;

	/**
	 * Test scenario A: user stores a resource and then attempts to access it
	 * 
	 * @throws AlreadyStoredException
	 * @throws SmartcampusException
	 * @throws Exception
	 */
	@Test
	public void scenarioA() throws AlreadyStoredException,
			SmartcampusException, Exception {
		Resource resource = testUtils.createResource();
		Storage appAccount = TestUtils.createAppAccount("smartcampus");
		appAccountManager.save(appAccount);

		Account account = TestUtils.createUserAccount(appAccount,
				TestUtils.userId);
		accountManager.save(account);

		User user = testUtils.createUser();

		resource = mediaManager.storage(account.getId(), user, resource, true);
		Token token = mediaManager.getResourceToken(user, resource.getId(),
				Operation.DOWNLOAD);
		Assert.assertNotNull(token.getUrl());

	}

	/**
	 * Test scenario B: user1 stores resource and then shares it with user2
	 * Scenario tests security constraints on access to shared resource
	 * 
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws WebApiException
	 * @throws AlreadyStoredException
	 * @throws SmartcampusException
	 * @throws NumberFormatException
	 * @throws NotFoundException
	 */
	@Test
	public void scenarioB() throws URISyntaxException, IOException,
			WebApiException, AlreadyStoredException, SmartcampusException,
			NumberFormatException, NotFoundException {
		Resource resource = testUtils.createResource();
		Storage appAccount = TestUtils.createAppAccount("smartcampus");
		appAccountManager.save(appAccount);
		Account account = TestUtils.createUserAccount(appAccount,
				TestUtils.userId);
		accountManager.save(account);
		User user1 = testUtils.createUser();
		User user2 = testUtils.createUser();

		resource = mediaManager.storage(account.getId(), user1, resource, true);

		try {
			mediaManager.getResourceToken(user2, resource.getId(),
					Operation.DOWNLOAD);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		long eid = Long.parseLong(metaManager.findByResource(resource.getId())
				.getSocialId());

		socialEngine.shareEntityWith(eid, user1.getSocialId(),
				user2.getSocialId());

		Token token = mediaManager.getResourceToken(user2, resource.getId(),
				Operation.DOWNLOAD);

		Assert.assertNotNull(token.getUrl());

	}

}
