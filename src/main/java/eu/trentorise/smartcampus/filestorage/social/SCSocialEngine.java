package eu.trentorise.smartcampus.filestorage.social;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import eu.trentorise.smartcampus.aac.AACException;
import eu.trentorise.smartcampus.aac.AACService;
import eu.trentorise.smartcampus.aac.model.TokenData;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.rest.OauthUser;
import eu.trentorise.smartcampus.socialservice.SocialService;
import eu.trentorise.smartcampus.socialservice.SocialServiceException;
import eu.trentorise.smartcampus.socialservice.beans.EntityInfo;
import eu.trentorise.smartcampus.socialservice.beans.EntityType;

public class SCSocialEngine implements SocialEngine {

	private static final Logger logger = Logger.getLogger(SCSocialEngine.class);

	private static SocialService socialService;

	@Autowired
	@Value("${smartcampus.socialengine.endpoint}")
	private String socialEndpoint;

	@Autowired
	@Value("${aacURL}")
	private String aacEndpoint;

	@Autowired
	@Value("${smartcampus.clientId}")
	private String scClientId;

	@Autowired
	@Value("${smartcampus.clientSecret}")
	private String scClientSecret;

	private static AACService aacService;

	EntityType fileEntityType;

	private TokenData authToken;

	public AACService getAACClient() {
		if (aacService == null) {
			aacService = new AACService(aacEndpoint, scClientId, scClientSecret);
		}

		return aacService;
	}

	public SocialService getSocialClient() {
		if (socialService == null) {
			socialService = new SocialService(socialEndpoint);
		}

		return socialService;
	}

	@Override
	public String createEntity(OauthUser user, Resource resource) {
		/**
		 * I don't know the social APPID in which create the entity
		 */
		logger.info("createEntity not implemented");
		return null;
	}

	@Override
	public boolean deleteEntity(OauthUser user, String entityId) {
		/**
		 * I don't know the social APPID space containing entity
		 */
		logger.info("deleteEntity not implemented");
		return false;
	}

	@Override
	public boolean checkPermission(OauthUser user, String entityId) {

		try {
			return getSocialClient().getEntitySharedWithUser(
					user.getUserToken(), entityId) != null;
		} catch (SecurityException e) {
			logger.error(String.format(
					"Security exception getting resource owner: %s",
					e.getMessage()));
		} catch (SocialServiceException e) {
			logger.error(String.format(
					"General exception getting resource owner: %s",
					e.getMessage()));
		}

		return false;
	}

	@Override
	public boolean isOwnedBy(OauthUser user, String entityId) {
		try {
			EntityInfo info = getSocialClient().getEntityInfoByApp(
					getAuthToken(), entityId);
			return info != null && info.getUserOwner() != null
					&& info.getUserOwner().equals(user.getId());
		} catch (SecurityException e) {
			logger.error(String.format(
					"Security exception getting resource owner: %s",
					e.getMessage()));
		} catch (SocialServiceException e) {
			logger.error(String.format(
					"General exception getting resource owner: %s",
					e.getMessage()));
		} catch (AACException e) {
			logger.error(String.format(
					"Authentication  exception getting resource owner: %s",
					e.getMessage()));
		}
		return false;
	}

	private String getAuthToken() throws AACException {
		if (authToken == null) {
			authToken = getAACClient().generateClientToken();
		} else {
			if (authToken.getExpires_on() < System.currentTimeMillis()) {
				logger.info("Client authToken expired, trying refresh....");
				authToken = getAACClient().refreshToken(
						authToken.getRefresh_token());
				logger.info("Client authToken refreshed!");
			}
		}

		return authToken.getAccess_token();
	}
}
