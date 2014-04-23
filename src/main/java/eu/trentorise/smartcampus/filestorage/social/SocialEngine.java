package eu.trentorise.smartcampus.filestorage.social;

import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.rest.OauthUser;

public interface SocialEngine {

	/**
	 * creates a social entity
	 * 
	 * @param user
	 *            user owner of the resource
	 * @param resource
	 *            resource to bind with new social entity
	 * 
	 * @return the id of the new entity
	 * @throws WebApiException
	 *             exception thrown by social engine
	 */

	public String createEntity(OauthUser user, Resource resource);

	/**
	 * deletes a social entity
	 * 
	 * @param user
	 *            user owner
	 * @param entityId
	 *            id of the social entity to delete
	 * 
	 * @return true if operation gone fine, false otherwise
	 * @throws WebApiException
	 *             exception thrown by social engine
	 */
	public boolean deleteEntity(OauthUser user, String entityId);

	/**
	 * checks if an entity is shared with a user
	 * 
	 * @param user
	 *            the user to check
	 * @param entityId
	 *            id of the entity
	 * @return true if entity is shared with the user, false otherwise
	 * @throws WebApiException
	 *             exception thrown by social engine
	 */
	public boolean checkPermission(OauthUser user, String entityId);

	/**
	 * checks if social entity is owned by the given user
	 * 
	 * @param user
	 *            user owner of entity
	 * @param entityId
	 *            social entity id
	 * @return true if entity is owned by the user, false otherwise
	 */
	public boolean isOwnedBy(OauthUser user, String entityId);
}
