package eu.trentorise.smartcampus.filestorage.managers;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.LocalResource;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;

@Service
public class LocalResourceManager {

	private static final Logger logger = Logger
			.getLogger(LocalResourceManager.class);

	@Autowired
	MongoTemplate db;

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
