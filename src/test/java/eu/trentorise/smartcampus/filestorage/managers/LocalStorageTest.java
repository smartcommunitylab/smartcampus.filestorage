package eu.trentorise.smartcampus.filestorage.managers;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.services.impl.LocalStorage;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/spring/SpringAppDispatcher-servlet.xml")
public class LocalStorageTest {
	@Autowired
	LocalStorage localStorage;

	@Autowired
	AccountManager accountManager;

	@Autowired
	MetadataManager metadataManager;

	@Test
	public void sample() throws AlreadyStoredException, URISyntaxException,
			IOException, SmartcampusException, NotFoundException {
		Account account = new Account();
		account.setAppId("APP_ID");
		account.setName("user");
		account.setStorageType(StorageType.LOCAL);
		account.setUserId("3");
		account = accountManager.save(account);
		Resource resource = new Resource();
		resource.setName("logo2.png");
		resource.setContentType("image/png");
		File file = new File(getClass().getResource(
				"/eu/trentorise/smartcampus/filestorage/logo2.png").toURI());
		resource.setContent(FileUtils.readFileToByteArray(file));
		localStorage.store(account.getId(), resource);
		metadataManager.create(account.getId(), null, resource, false);

		Token t = localStorage.getToken(account.getId(), resource.getId());
		System.out.println("URL:" + t.getUrl() + " method:" + t.getMethodREST()
				+ " type:" + t.getStorageType());
	}
}
