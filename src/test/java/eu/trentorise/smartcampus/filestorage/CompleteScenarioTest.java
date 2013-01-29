package eu.trentorise.smartcampus.filestorage;

import it.unitn.disi.sweb.webapi.client.WebApiException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.filestorage.managers.MediaManager;
import eu.trentorise.smartcampus.filestorage.managers.MetadataManager;
import eu.trentorise.smartcampus.filestorage.managers.UserAccountManager;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Operation;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;
import eu.trentorise.smartcampus.filestorage.utils.DropboxUtils;
import eu.trentorise.smartcampus.filestorage.utils.SocialEngineOperation;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/spring/SpringAppDispatcher-servlet.xml")
public class CompleteScenarioTest {

	@Autowired
	UserAccountManager accountManager;

	@Autowired
	MediaManager mediaManager;

	@Autowired
	MetadataManager metaManager;

	@Autowired
	SocialEngineOperation socialEngine;

	@Test
	public void scenarioA() throws AlreadyStoredException,
			SmartcampusException, Exception {
		Resource resource = createResource();
		UserAccount account = createAccount();
		User user = createUser();

		accountManager.save(account);
		resource = mediaManager.storage(account.getId(), user, resource);
		Token token = mediaManager.getResourceToken(user, resource.getId(),
				Operation.DOWNLOAD);
		Assert.assertNotNull(token.getUrl());

	}

	@Test
	public void scenarioB() throws URISyntaxException, IOException,
			WebApiException, AlreadyStoredException, SmartcampusException,
			NumberFormatException, NotFoundException {
		Resource resource = createResource();
		UserAccount account = createAccount();
		User user1 = createUser();
		User user2 = createUser();

		accountManager.save(account);
		resource = mediaManager.storage(account.getId(), user1, resource);

		try {
			mediaManager.getResourceToken(user2, resource.getId(),
					Operation.DOWNLOAD);
			Assert.assertTrue(false);
		} catch (Exception e) {
			Assert.assertTrue(true);
		}

		long eid = Long.parseLong(metaManager.findByResource(resource.getId())
				.getEid());

		socialEngine.shareEntityWith(eid, user1.getSocialId(),
				user2.getSocialId());

		Token token = mediaManager.getResourceToken(user2, resource.getId(),
				Operation.DOWNLOAD);

		Assert.assertNotNull(token.getUrl());

	}

	private Resource createResource() throws URISyntaxException, IOException {
		File png = new File(getClass().getResource("image.png").toURI());
		Resource res = new Resource();
		res.setContent(FileUtils.readFileToByteArray(png));
		res.setContentType("image/png");
		res.setName("image.png");
		return res;
	}

	private User createUser() throws WebApiException {
		User user = new User();
		it.unitn.disi.sweb.webapi.model.smartcampus.social.User socialUser = socialEngine
				.createUser();
		user.setSocialId(socialUser.getId());
		user.setId(50l);
		user.setAuthToken("token");
		user.setExpTime(System.currentTimeMillis() + 3600 * 1000);
		return user;
	}

	private UserAccount createAccount() {
		UserAccount account = new UserAccount();
		account.setId(new ObjectId().toString());
		account.setUserId(50l);
		account.setStorage(StorageType.DROPBOX);

		List<Configuration> confs = new ArrayList<Configuration>();
		confs.add(new Configuration("USER_KEY", DropboxUtils.userkey));
		confs.add(new Configuration("USER_SECRET", DropboxUtils.usersecret));

		account.setConfigurations(confs);
		return account;

	}

}
