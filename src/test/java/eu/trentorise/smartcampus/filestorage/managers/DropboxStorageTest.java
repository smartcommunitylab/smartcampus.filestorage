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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.services.MetadataService;
import eu.trentorise.smartcampus.filestorage.services.StorageService;
import eu.trentorise.smartcampus.filestorage.utils.DropboxUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/spring/SpringAppDispatcher-servlet.xml")
public class DropboxStorageTest {

	private static final String TEST_USER_ID = "50";
	@Autowired
	StorageService storageService;

	@Autowired
	AccountManager accountManager;

	@Autowired
	StorageManager appAccountManager;

	@Autowired
	MetadataService metaService;

	@Before
	public void initEnv() throws AlreadyStoredException {
		// Storage creation
		Storage appAccount = new Storage();
		appAccount.setAppId("smartcampus");
		appAccount.setName("smartcampustTest");
		appAccount.setStorageType(StorageType.DROPBOX);
		List<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new Configuration("APP_KEY", DropboxUtils.appkey));
		confs.add(new Configuration("APP_SECRET", DropboxUtils.appsecret));

		appAccount.setConfigurations(confs);
		appAccount = appAccountManager.save(appAccount);

		// userAccount creation
		Account account = new Account();
		account.setName("dropbox personal");
		account.setUserId(TEST_USER_ID);
		account.setStorageType(StorageType.DROPBOX);
		account.setAppId(appAccount.getAppId());
		List<Configuration> configurations = new ArrayList<Configuration>();
		configurations.add(new Configuration("USER_KEY", DropboxUtils.userkey));
		configurations.add(new Configuration("USER_SECRET",
				DropboxUtils.usersecret));
		account.setConfigurations(configurations);
		accountManager.save(account);
	}

	@After
	public void cleanUp() throws NotFoundException, SmartcampusException {
		String accountId = accountManager.findBy(TEST_USER_ID).get(0).getId();

		for (Metadata m : metaService.getAccountMetadata(accountId)) {
			storageService.remove(m.getResourceId());
			metaService.delete(m.getResourceId());
		}

		for (Account a : accountManager.findAll()) {
			accountManager.delete(a);
		}
	}

	@Test
	public void uploadResource() throws AlreadyStoredException,
			SmartcampusException, IOException, NotFoundException {

		// load user account
		List<Account> accounts = accountManager.findBy(TEST_USER_ID);
		Assert.assertEquals(1, accounts.size());

		Resource res = storageService.store(accounts.get(0).getId(),
				getSampleResource());
		metaService.save(getMetadata(accounts.get(0), res));
	}

	@Test(expected = AlreadyStoredException.class)
	public void uploadFailedResource() throws AlreadyStoredException,
			SmartcampusException, IOException {
		// load user account
		List<Account> accounts = accountManager.findBy(TEST_USER_ID);
		Assert.assertEquals(1, accounts.size());
		Resource sample = getSampleResource();
		sample = storageService.store(accounts.get(0).getId(), sample);
		metaService.save(getMetadata(accounts.get(0), sample));
		storageService.store(accounts.get(0).getId(), getSampleResource());

	}

	@Test
	public void getSessionToken() throws IOException, AlreadyStoredException,
			SmartcampusException, NotFoundException {
		// load user account
		List<Account> accounts = accountManager.findBy(TEST_USER_ID);
		Assert.assertEquals(1, accounts.size());
		Resource sample = getSampleResource();
		sample = storageService.store(accounts.get(0).getId(), sample);
		metaService.save(getMetadata(accounts.get(0), sample));
		Token token = storageService.getToken(accounts.get(0).getId(),
				sample.getId());
		Assert.assertNotNull(token.getUrl());
	}

	private Resource getSampleResource() throws IOException {
		Resource res = new Resource();
		res.setName("sample.txt");
		res.setContentType("text/plain");
		File t = File.createTempFile("dropbox", ".txt");
		t.deleteOnExit();
		FileWriter fw = new FileWriter(t);
		fw.write("sample file");
		fw.close();
		res.setContent(FileUtils.readFileToByteArray(t));
		return res;
	}

	private Metadata getMetadata(Account a, Resource r) {
		Metadata meta = new Metadata();
		meta.setContentType(r.getContentType());
		meta.setCreationTs(System.currentTimeMillis());
		meta.setSocialId("eid");
		meta.setName(r.getName());
		meta.setResourceId(r.getId());
		meta.setAccountId(a.getId());
		return meta;
	}
}
