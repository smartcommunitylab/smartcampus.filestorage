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

import eu.trentorise.smartcampus.ac.provider.model.User;
import eu.trentorise.smartcampus.filestorage.model.AppAccount;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;
import eu.trentorise.smartcampus.filestorage.utils.SocialEngineOperation;
import eu.trentorise.smartcampus.filestorage.utils.SocialEngineOperation.EntityTypes;
import eu.trentorise.smartcampus.filestorage.utils.TestUtils;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = "/spring/SpringAppDispatcher-servlet.xml")
public class MetadataManagerTest {

	@Autowired
	UserAccountManager accountManager;

	@Autowired
	AppAccountManager appAccountManager;

	@Autowired
	MediaManager mediaManager;

	@Autowired
	SocialEngineOperation socialEngine;

	@Autowired
	MetadataManager metaManager;

	@Autowired
	TestUtils testUtils;

	private Resource resource;
	private UserAccount userAccount;
	private User user;
	private AppAccount appAccount;

	@Before
	public void setup() throws URISyntaxException, IOException, WebApiException {
		try {
			resource = testUtils.createResource();
			appAccount = TestUtils.createAppAccount("sc test");
			appAccount = appAccountManager.save(appAccount);

			user = testUtils.createUser();

			userAccount = TestUtils.createUserAccount(appAccount, user.getId());
			userAccount = accountManager.save(userAccount);

			resource = mediaManager
					.storage(userAccount.getId(), user, resource);
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
			Assert.assertEquals(updatedMeta.getEid(), readed.getEid());

		} catch (Exception e) {
			Assert.fail("Exception occurred " + e.getMessage());
		}
	}

	@After
	public void cleanup() {
		try {
			mediaManager.remove(userAccount.getId(), user, resource.getId());
		} catch (Exception e) {
		}
		accountManager.delete(userAccount);
		appAccountManager.delete(appAccount.getId());
	}

}
