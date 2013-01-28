/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.smartcampus.filestorage.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.services.MetadataService;

@Service
public class MongoMetadataService implements MetadataService {

	@Autowired
	MongoTemplate db;

	@Override
	public String getResourceByEntity(String eid) throws NotFoundException {
		Criteria criteria = new Criteria("eid").is(eid);
		Metadata meta = db.findOne(Query.query(criteria), Metadata.class);
		if (meta == null) {
			throw new NotFoundException();
		} else {
			return meta.getRid();
		}
	}

	@Override
	public String getEntityByResource(String rid) throws NotFoundException {
		Metadata meta = getMetadata(rid);
		return meta.getEid();
	}

	@Override
	public StorageType getResourceStorage(String rid) throws NotFoundException {
		// FIXME not correct
		Metadata meta = getMetadata(rid);
		return null;
	}

	@Override
	public Metadata getMetadata(String rid) throws NotFoundException {
		Criteria criteria = new Criteria("rid").is(rid);
		Metadata meta = db.findOne(Query.query(criteria), Metadata.class);
		if (meta == null) {
			throw new NotFoundException();
		} else {
			return meta;
		}
	}

	@Override
	public void save(Metadata metadata) throws AlreadyStoredException {
		if (metadata.getRid() != null
				&& db.findById(metadata.getRid(), Metadata.class) != null) {
			throw new AlreadyStoredException();
		}
		db.save(metadata);

	}

	@Override
	public void delete(String rid) {
		Criteria criteria = new Criteria("rid").is(rid);
		db.remove(Query.query(criteria), Metadata.class);

	}

	@Override
	public void update(Metadata metadata) throws NotFoundException {
		if (metadata.getRid() != null) {
			int results = db.find(
					Query.query(new Criteria("rid").is(metadata.getRid())),
					Metadata.class).size();
			if (results < 1) {
				throw new NotFoundException();
			}
			if (results > 1) {
				throw new IllegalArgumentException("Found more than one result");
			}
			db.save(metadata);
		} else {
			throw new NotFoundException();
		}

	}

	@Override
	public String getResourceByFilename(String accountId, String filename)
			throws NotFoundException {
		Criteria criteria = new Criteria();
		criteria.and("accountId").is(accountId);
		criteria.and("name").is(filename);
		List<Metadata> results = db.find(Query.query(criteria), Metadata.class);
		if (results.size() < 1) {
			throw new NotFoundException();
		}
		if (results.size() > 1) {
			throw new IllegalArgumentException("Found more than one result");
		}
		return results.get(0).getRid();
	}
}
