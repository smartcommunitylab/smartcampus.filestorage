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

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.StorageType;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;

/**
 * <i>UserAccountManager</i> manages functionalities about the user storage
 * accounts
 * 
 * @author mirko perillo
 * 
 */
@Service
public class UserAccountManager {

	private static final Logger logger = Logger
			.getLogger(UserAccountManager.class);
	@Autowired
	MongoTemplate db;

	/**
	 * saves a new account
	 * 
	 * @param ua
	 *            the user storage account to save
	 * @return the account saved with id field populated
	 * @throws AlreadyStoredException
	 *             if account is already stored
	 */
	public UserAccount save(UserAccount ua) throws AlreadyStoredException {
		if (ua.getId() != null
				&& db.findById(ua.getId(), UserAccount.class) != null) {
			logger.error("UserAccount already stored, " + ua.getId());
			throw new AlreadyStoredException();
		}
		if (ua.getId() == null || ua.getId().trim().length() == 0) {
			ua.setId(new ObjectId().toString());
		}
		db.save(ua);
		return ua;
	}

	/**
	 * updates {@link UserAccount} informations
	 * 
	 * @param ua
	 *            new informations to update
	 */
	public void update(UserAccount ua) {
		db.save(ua);
	}

	/**
	 * retrieves all the {@link UserAccount} in the system
	 * 
	 * @return the list of all UserAccount
	 */
	public List<UserAccount> findAll() {
		return db.findAll(UserAccount.class);
	}

	/**
	 * retrieves all the {@link UserAccount} of a given user
	 * 
	 * @param uid
	 *            id of the owner of user storage accounts
	 * @return a list of UserAccount of the given user id
	 */
	public List<UserAccount> findBy(long uid) {
		Criteria criteria = new Criteria();
		criteria.and("userId").is(uid);
		return db.find(Query.query(criteria), UserAccount.class);
	}

	/**
	 * retrieves the {@link UserAccount} of given id
	 * 
	 * @param accountId
	 *            the user storage account id
	 * @return the UserAccount
	 * @throws NotFoundException
	 *             if UserAccount doesn't exist
	 */
	public UserAccount findById(String accountId) throws NotFoundException {
		UserAccount account = db.findById(accountId, UserAccount.class);
		if (account == null) {
			logger.error("UserAccount not found: " + accountId);
			throw new NotFoundException();
		}
		return account;
	}

	/**
	 * retrieves a list of {@link UserAccount} of a given user and of a given
	 * type
	 * 
	 * @param uid
	 *            id of the user
	 * @param storage
	 *            type of storage
	 * @return the list of UserAccount for given user and type
	 */
	public List<UserAccount> findBy(long uid, StorageType storage) {
		Criteria criteria = new Criteria();
		criteria.and("userId").is(uid);
		criteria.and("storage").is(storage);
		return db.find(Query.query(criteria), UserAccount.class);
	}

	/**
	 * deletes a {@link UserAccount}
	 * 
	 * @param id
	 *            id of the user storage account to delete
	 */
	public void delete(String id) {
		db.remove(Query.query(new Criteria("id").is(id)), UserAccount.class);
	}

	/**
	 * deletes a {@link UserAccount}
	 * 
	 * @param ua
	 *            the user storage account to delete
	 */
	public void delete(UserAccount ua) {
		db.remove(ua);
	}

}
