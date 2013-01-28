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

package eu.trentorise.smartcampus.filestorage.managers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;

@Service
public class UserAccountManager {

	@Autowired
	MongoTemplate db;

	public void save(UserAccount ua) throws AlreadyStoredException {
		if (ua.getId() != null
				&& db.findById(ua.getId(), UserAccount.class) != null) {
			throw new AlreadyStoredException();
		}
		db.save(ua);
	}

	public void update(UserAccount ua) {
		db.save(ua);
	}

	public List<UserAccount> findAll() {
		return db.findAll(UserAccount.class);
	}

	public List<UserAccount> findBy(long uid) {
		Criteria criteria = new Criteria();
		criteria.and("userId").is(uid);
		return db.find(Query.query(criteria), UserAccount.class);
	}

	public UserAccount findById(String accountId) throws NotFoundException {
		UserAccount account = db.findById(accountId, UserAccount.class);
		if (account == null) {
			throw new NotFoundException();
		}
		return account;
	}

	public List<UserAccount> findBy(long uid, StorageType storage) {
		Criteria criteria = new Criteria();
		criteria.and("userId").is(uid);
		criteria.and("storage").is(storage);
		return db.find(Query.query(criteria), UserAccount.class);
	}

	public void delete(String id) {
		db.remove(Query.query(new Criteria("id").is(id)), UserAccount.class);
	}

	public void delete(UserAccount ua) {
		db.remove(ua);
	}

}
