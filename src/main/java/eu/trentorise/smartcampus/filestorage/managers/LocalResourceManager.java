package eu.trentorise.smartcampus.filestorage.managers;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.LocalResource;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;

/**
 * <i>LocalResourceManager</i> mmanages all aspects about {@link LocalResource}.
 * 
 * 
 * 
 */
@Service
public class LocalResourceManager {

	private static final Logger logger = Logger
			.getLogger(LocalResourceManager.class);

	@Autowired
	MongoTemplate db;

	/**
	 * store a localResource in the database
	 * 
	 * @param localRes
	 *            the {@link LocalResource} to store
	 * @return the stored {@link LocalResource}
	 * @throws AlreadyStoredException
	 */
	public LocalResource save(LocalResource localRes)
			throws AlreadyStoredException {
		if (localRes.getId() != null
				&& db.findById(localRes.getId(), LocalResource.class) != null
				&& localRes.getId().trim().length() != 0) {
			logger.error("LocalResource already stored, " + localRes.getId());
			throw new AlreadyStoredException();
		} else {
			localRes.setId(new ObjectId().toString());
			db.save(localRes);
			logger.info(String.format(
					"LocalResource of the file %s saved in db",
					localRes.getUrl()));
		}
		return localRes;
	}

	/**
	 * search of the {@link LocalResource} in the database
	 * 
	 * @param localResourceId
	 *            the id of the {@link LocalResource}
	 * @return the {@link LocalResource}, if exists
	 * @throws NotFoundException
	 */
	public LocalResource getLocalResById(String localResourceId)
			throws NotFoundException {
		LocalResource localRes;
		localRes = db.findById(localResourceId, LocalResource.class);
		if (localRes == null) {
			logger.error("LocalResource not found, " + localResourceId);
			throw new NotFoundException();
		}
		return localRes;
	}
}
