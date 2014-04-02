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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.model.StorageType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/spring/SpringAppDispatcher-servlet.xml")
public class StorageManagerTest {

	@Autowired
	StorageManager manager;

	private static final String APP_NAME = "smartcampus";

	@Before
	public void cleanup() {
		Storage s = manager.getStorageByAppId(APP_NAME);
		if (s != null) manager.delete(s.getAppId());
	}

	@Test
	public void crud() throws AlreadyStoredException, NotFoundException {
		Storage storage = createStorage(APP_NAME);

		// create
		Assert.assertNull(manager.getStorageByAppId(APP_NAME));
		manager.save(storage);

		Assert.assertNotNull(manager.getStorageByAppId(APP_NAME));
		storage = manager.getStorageByAppId(APP_NAME);
		Assert.assertEquals(2, storage.getConfigurations().size());
		Assert.assertEquals("sampleAccount", storage.getName());

		// update
		storage.setConfigurations(new ArrayList<Configuration>());
		manager.update(storage);

		storage = manager.getStorageByAppId(APP_NAME);
		Assert.assertEquals(0, storage.getConfigurations().size());

		// delete
		manager.delete(storage.getId());
		Assert.assertNull(manager.getStorageByAppId(APP_NAME));

	}

	private Storage createStorage(String appId) {
		Storage storage = new Storage();
		storage.setAppId(appId);
		storage.setName("sampleAccount");
		storage.setStorageType(StorageType.DROPBOX);
		List<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new Configuration("APP_KEY", "samplekey"));
		confs.add(new Configuration("APP_SECRET", "samplesecret"));
		storage.setConfigurations(confs);
		return storage;
	}

}
