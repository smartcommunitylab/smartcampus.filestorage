package eu.trentorise.smartcampus.filestorage.social;

import eu.trentorise.smartcampus.User;
import eu.trentorise.smartcampus.filestorage.model.Resource;

public class DefaultSocialEngine implements SocialEngine {

	@Override
	public String createEntity(Resource resource, User user) {
		return null;
	}

	@Override
	public boolean deleteEntity(long eid) {
		return true;
	}

	@Override
	public boolean checkPermission(User user, String entityId) {
		return true;
	}

	@Override
	public boolean isOwnedBy(User user, String entityId) {
		return true;
	}

}
