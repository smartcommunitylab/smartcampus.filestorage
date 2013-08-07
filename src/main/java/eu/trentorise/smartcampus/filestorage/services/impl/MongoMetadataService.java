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

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.services.MetadataService;

@Service
public class MongoMetadataService implements MetadataService {

	private static final Logger logger = Logger
			.getLogger(MongoMetadataService.class);

	@Autowired
	MongoTemplate db;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResourceBySocialId(String socialId)
			throws NotFoundException {
		Criteria criteria = new Criteria("socialId").is(socialId);
		Metadata meta = db.findOne(Query.query(criteria), Metadata.class);
		if (meta == null) {
			logger.error("Metadata not found: " + socialId);
			throw new NotFoundException();
		} else {
			return meta.getResourceId();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityByResource(String resourceId)
			throws NotFoundException {
		Metadata meta = getMetadata(resourceId);
		return meta.getSocialId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Metadata getMetadata(String resourceId) throws NotFoundException {
		Metadata meta = db.findById(resourceId, Metadata.class);
		if (meta == null) {
			logger.error("Metadata not found: " + resourceId);
			throw new NotFoundException();
		} else {
			return meta;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void save(Metadata metadata) throws AlreadyStoredException {
		if (metadata.getResourceId() != null
				&& db.findById(metadata.getResourceId(), Metadata.class) != null) {
			logger.error("Metadata already stored: " + metadata.getResourceId());
			throw new AlreadyStoredException();
		}
		db.save(metadata);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(String resourceId) {
		Criteria criteria = new Criteria("resourceId").is(resourceId);
		db.remove(Query.query(criteria), Metadata.class);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Metadata metadata) throws NotFoundException {
		if (metadata.getResourceId() != null) {
			int results = db.find(
					Query.query(new Criteria("resourceId").is(metadata
							.getResourceId())), Metadata.class).size();
			if (results < 1) {
				logger.error("Metadata not found: " + metadata.getResourceId());
				throw new NotFoundException();
			}
			if (results > 1) {
				logger.error("Found more than one metadata: "
						+ metadata.getResourceId());
				throw new IllegalArgumentException("Found more than one result");
			}
			db.save(metadata);
		} else {
			throw new NotFoundException();
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResourceByFilename(String accountId, String filename)
			throws NotFoundException {
		Criteria criteria = new Criteria();
		criteria.and("accountId").is(accountId);
		criteria.and("name").is(filename);
		List<Metadata> results = db.find(Query.query(criteria), Metadata.class);
		if (results.size() < 1) {
			logger.warn(String.format("Metadata not found: %s - %s", filename,
					accountId));
			throw new NotFoundException();
		}
		if (results.size() > 1) {
			logger.error(String.format("More than one metadata: %s - %s",
					filename, accountId));
			throw new IllegalArgumentException("Found more than one result");
		}
		return results.get(0).getResourceId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Metadata> getAccountMetadata(String accountId)
			throws NotFoundException {
		Criteria criteria = new Criteria();
		criteria.and("accountId").is(accountId);
		return db.find(Query.query(criteria), Metadata.class);
	}
}
