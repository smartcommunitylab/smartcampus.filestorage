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
		for (Storage temp : manager.getStorages(APP_NAME)) {
			manager.delete(temp.getId());
		}
	}

	@Test
	public void crud() throws AlreadyStoredException, NotFoundException {
		Storage storage = createStorage(APP_NAME);

		// create
		Assert.assertEquals(0, manager.getStorages(APP_NAME).size());
		manager.save(storage);

		Assert.assertEquals(1, manager.getStorages(APP_NAME).size());
		storage = manager.getStorages(APP_NAME).get(0);
		Assert.assertEquals(2, storage.getConfigurations().size());
		Assert.assertEquals("sampleAccount", storage.getName());

		// update
		storage.setConfigurations(new ArrayList<Configuration>());
		manager.update(storage);

		storage = manager.getStorages(APP_NAME).get(0);
		Assert.assertEquals(0, storage.getConfigurations().size());

		// delete
		manager.delete(storage.getId());
		Assert.assertEquals(0, manager.getStorages(APP_NAME).size());

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
