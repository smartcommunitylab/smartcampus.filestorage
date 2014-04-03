package eu.trentorise.smartcampus.filestorage.social;

import eu.trentorise.smartcampus.User;
import eu.trentorise.smartcampus.filestorage.model.Resource;

public interface SocialEngine {

	/**
	 * creates a social entity
	 * 
	 * @param resource
	 *            resource to bind with new social entity
	 * @param user
	 *            user owner of the resource
	 * @return the id of the new entity
	 * @throws WebApiException
	 *             exception thrown by social engine
	 */

	public String createEntity(Resource resource, User user);

	/**
	 * deletes a social entity
	 * 
	 * @param eid
	 *            id of the social entity to delete
	 * @return true if operation gone fine, false otherwise
	 * @throws WebApiException
	 *             exception thrown by social engine
	 */
	public boolean deleteEntity(long eid);

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
	public boolean checkPermission(User user, String entityId);

	/**
	 * checks if social entity is owned by the given user
	 * 
	 * @param user
	 *            user owner of entity
	 * @param entityId
	 *            social entity id
	 * @return true if entity is owned by the user, false otherwise
	 */
	public boolean isOwnedBy(User user, String entityId);
}
