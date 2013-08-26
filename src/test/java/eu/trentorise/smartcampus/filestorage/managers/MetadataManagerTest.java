package eu.trentorise.smartcampus.filestorage.managers;

import it.unitn.disi.sweb.webapi.client.WebApiException;
import it.unitn.disi.sweb.webapi.model.entity.Entity;

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

import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.utils.SocialEngineOperation;
import eu.trentorise.smartcampus.filestorage.utils.SocialEngineOperation.EntityTypes;
import eu.trentorise.smartcampus.filestorage.utils.TestUtils;
import eu.trentorise.smartcampus.social.model.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/spring/SpringAppDispatcher-servlet.xml")
public class MetadataManagerTest {

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
	public void updateSocialData() {
		try {

			Entity newEntity = socialEngine.createEntity(user,
					EntityTypes.computerFile);

			Metadata updatedMeta = metaManager.updateSocialData(user,
					resource.getId(), newEntity.getId().toString());
			Metadata readed = metaManager.findByResource(resource.getId());
			Assert.assertEquals(updatedMeta.getSocialId(), readed.getSocialId());

		} catch (Exception e) {
			Assert.fail("Exception occurred " + e.getMessage());
		}
	}

	@Test
	public void resourceSize() throws NotFoundException {
		Metadata meta = metaManager.findByResource(resource.getId());
		Assert.assertTrue(meta.getSize() > 0);
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
