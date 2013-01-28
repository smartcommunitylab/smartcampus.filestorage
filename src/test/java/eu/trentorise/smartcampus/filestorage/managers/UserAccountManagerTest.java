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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.filestorage.managers.UserAccountManager;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/spring/SpringAppDispatcher-servlet.xml")
public class UserAccountManagerTest {

	private static final int USER_ID = 50;

	@Autowired
	UserAccountManager manager;

	@After
	public void cleanup() {
		for (UserAccount a : manager.findAll()) {
			manager.delete(a);
		}
	}

	@Test
	public void crud() throws AlreadyStoredException {
		UserAccount account = new UserAccount();
		account.setUserId(USER_ID);
		account.setStorage(StorageType.DROPBOX);
		List<Configuration> configurations = new ArrayList<Configuration>();
		configurations.add(new Configuration("USER_KEY", "123456789sample"));
		configurations
				.add(new Configuration("USER_SECRET", "00123456789sample"));
		account.setConfigurations(configurations);

		Assert.assertEquals(0, manager.findAll().size());
		manager.save(account);
		Assert.assertEquals(1, manager.findAll().size());
		account = manager.findAll().get(0);
		Assert.assertEquals(StorageType.DROPBOX, account.getStorage());
		account.setStorage(StorageType.S3);
		manager.update(account);
		account = manager.findAll().get(0);
		Assert.assertNotSame(StorageType.DROPBOX, account.getStorage());

		manager.delete(account);
		Assert.assertEquals(0, manager.findAll().size());
	}

	@Test(expected = AlreadyStoredException.class)
	public void alreadyStored() throws AlreadyStoredException {
		UserAccount account = new UserAccount();
		account.setUserId(USER_ID);
		account.setStorage(StorageType.DROPBOX);
		List<Configuration> configurations = new ArrayList<Configuration>();
		configurations.add(new Configuration("USER_KEY", "123456789sample"));
		configurations
				.add(new Configuration("USER_SECRET", "00123456789sample"));
		account.setConfigurations(configurations);
		manager.save(account);

		account = manager.findAll().get(0);
		manager.save(account);
	}

}
