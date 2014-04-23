package eu.trentorise.smartcampus.filestorage.social;

import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.rest.OauthUser;

public class DefaultSocialEngine implements SocialEngine {

	@Override
	public String createEntity(OauthUser user, Resource resource) {
		return null;
	}

	@Override
	public boolean deleteEntity(OauthUser user, String entityId) {
		return true;
	}

	@Override
	public boolean checkPermission(OauthUser user, String entityId) {
		return true;
	}

	@Override
	public boolean isOwnedBy(OauthUser user, String entityId) {
		return true;
	}

}
