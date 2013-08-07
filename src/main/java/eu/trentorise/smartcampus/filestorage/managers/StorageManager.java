package eu.trentorise.smartcampus.filestorage.managers;

import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Storage;
import eu.trentorise.smartcampus.filestorage.utils.StringUtils;

@Service
public class StorageManager {
	private static final Logger logger = Logger.getLogger(StorageManager.class);

	@Autowired
	MongoTemplate db;

	private static final String DEFAULT_ACCOUNT_NAME = "Connection";

	public Storage save(Storage storage) throws AlreadyStoredException {

		if (storage.getId() != null
				&& db.findById(storage.getId(), Storage.class) != null) {
			logger.error("storage already present, " + storage.getId());
			throw new AlreadyStoredException();
		}

		if (StringUtils.isNullOrEmpty(storage.getAppId(), true)) {
			throw new IllegalArgumentException("appId should have a value");

		}

		if (StringUtils.isNullOrEmpty(storage.getName(), true)) {
			storage.setName(DEFAULT_ACCOUNT_NAME + " "
					+ getStoragesCount(storage.getAppId()));
		}

		if (StringUtils.isNullOrEmpty(storage.getId(), true)) {
			storage.setId(new ObjectId().toString());
		}

		db.save(storage);
		return storage;
	}

	public Storage update(Storage storage) throws NotFoundException {
		Storage toUpdate = getStorageById(storage.getId());
		toUpdate = update(toUpdate, storage);
		db.save(toUpdate);
		return toUpdate;
	}

	private Storage update(Storage destination, Storage source) {
		destination.setName(source.getName());
		destination.setConfigurations(source.getConfigurations());
		return destination;
	}

	public void delete(String storageId) {
		Criteria crit = new Criteria();
		crit.and("id").is(storageId);
		Query query = Query.query(crit);
		db.remove(query, Storage.class);
	}

	private int getStoragesCount(String appId) {
		return getStorages(appId).size();
	}

	public List<Storage> getStorages(String appId) {
		Criteria crit = new Criteria();
		crit.and("appId").is(appId);
		Query query = Query.query(crit);
		return db.find(query, Storage.class);
	}

	public Storage getStorageById(String storageId) throws NotFoundException {
		Storage storage = db.findById(storageId, Storage.class);
		if (storage == null) {
			throw new NotFoundException();
		}
		return storage;
	}

}
