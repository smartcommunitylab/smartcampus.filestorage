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

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.AppAccount;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.StorageType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/spring/SpringAppDispatcher-servlet.xml")
public class AppAccountManagerTest {

	@Autowired
	AppAccountManager manager;

	private static final String APP_NAME = "smartcampus";

	@After
	public void cleanup() {
		for (AppAccount temp : manager.getAppAccounts(APP_NAME)) {
			manager.delete(temp.getId());
		}
	}

	@Test
	public void crud() throws AlreadyStoredException, NotFoundException {
		AppAccount appAccount = createAccount(APP_NAME);

		// create
		Assert.assertEquals(0, manager.getAppAccounts(APP_NAME).size());
		manager.save(appAccount);

		Assert.assertEquals(1, manager.getAppAccounts(APP_NAME).size());
		appAccount = manager.getAppAccounts(APP_NAME).get(0);
		Assert.assertEquals(2, appAccount.getConfigurations().size());
		Assert.assertEquals("sampleAccount", appAccount.getAppAccountName());

		// update
		appAccount.setConfigurations(new ArrayList<Configuration>());
		manager.update(appAccount);

		appAccount = manager.getAppAccounts(APP_NAME).get(0);
		Assert.assertEquals(0, appAccount.getConfigurations().size());

		// delete
		manager.delete(appAccount.getId());
		Assert.assertEquals(0, manager.getAppAccounts(APP_NAME).size());

	}

	private AppAccount createAccount(String appName) {
		AppAccount appAccount = new AppAccount();
		appAccount.setAppName(appName);
		appAccount.setAppAccountName("sampleAccount");
		appAccount.setStorageType(StorageType.DROPBOX);
		List<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new Configuration("APP_KEY", "samplekey"));
		confs.add(new Configuration("APP_SECRET", "samplesecret"));
		appAccount.setConfigurations(confs);
		return appAccount;
	}

}
