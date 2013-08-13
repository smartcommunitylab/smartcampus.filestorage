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

import eu.trentorise.smartcampus.filestorage.model.Account;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Configuration;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.StorageType;

/**
 * <i>AccountManager</i> manages functionalities about the user storage accounts
 * 
 * @author mirko perillo
 * 
 */
@Service
public class AccountManager {

	private static final Logger logger = Logger.getLogger(AccountManager.class);
	@Autowired
	MongoTemplate db;

	/**
	 * saves a new account
	 * 
	 * @param account
	 *            the user storage account to save
	 * @return the account saved with id field populated
	 * @throws AlreadyStoredException
	 *             if account is already stored
	 */
	public Account save(Account account) throws AlreadyStoredException {
		if (account.getId() != null
				&& db.findById(account.getId(), Account.class) != null) {
			logger.error("Account already stored, " + account.getId());
			throw new AlreadyStoredException();
		}
		if (account.getId() == null || account.getId().trim().length() == 0) {
			account.setId(new ObjectId().toString());
		}
		db.save(account);
		return account;
	}

	/**
	 * updates {@link Account} informations
	 * 
	 * @param account
	 *            new informations to update
	 */
	public void update(Account account) {
		if (account.getId() == null) {
			logger.error("account d cannot be null");
			throw new IllegalArgumentException("id cannot be null");
		}
		db.save(account);
	}

	public Account update(String appId, String accountId,
			List<Configuration> confs) {
		return null;
	}

	/**
	 * retrieves all the {@link Account} in the system
	 * 
	 * @return the list of all Account
	 */
	public List<Account> findAll() {
		return db.findAll(Account.class);
	}

	public List<Account> findAccounts(String appId) {
		Criteria criteria = new Criteria();
		criteria.and("appId").is(appId);
		return db.find(Query.query(criteria), Account.class);
	}

	public List<Account> findAccounts(String appId, String userId) {
		Criteria criteria = new Criteria();
		criteria.and("appId").is(appId);
		criteria.and("userId").is(userId);
		return db.find(Query.query(criteria), Account.class);
	}

	/**
	 * retrieves all the {@link Account} of a given user
	 * 
	 * @param userId
	 *            id of the owner of user storage accounts
	 * @return a list of Account of the given user id
	 */
	public List<Account> findBy(String userId) {
		Criteria criteria = new Criteria();
		criteria.and("userId").is(userId);
		return db.find(Query.query(criteria), Account.class);
	}

	public List<Account> findBy(String userId, String appId) {
		Criteria criteria = new Criteria();
		criteria.and("userId").is(userId);
		if (appId != null) {
			criteria.and("appId").is(appId);
		}
		return db.find(Query.query(criteria), Account.class);
	}

	/**
	 * retrieves the {@link Account} of given id
	 * 
	 * @param accountId
	 *            the user storage account id
	 * @return the Account
	 * @throws NotFoundException
	 *             if Account doesn't exist
	 */
	public Account findById(String accountId) throws NotFoundException {
		Account account = db.findById(accountId, Account.class);
		if (account == null) {
			logger.error("Account not found: " + accountId);
			throw new NotFoundException();
		}
		return account;
	}

	/**
	 * retrieves a list of {@link Account} of a given user and of a given type
	 * 
	 * @param userId
	 *            id of the user
	 * @param storage
	 *            type of storage
	 * @return the list of Account for given user and type
	 */
	public List<Account> findBy(String userId, StorageType storage) {
		Criteria criteria = new Criteria();
		criteria.and("userId").is(userId);
		criteria.and("storageType").is(storage);
		return db.find(Query.query(criteria), Account.class);
	}

	/**
	 * deletes a {@link Account}
	 * 
	 * @param id
	 *            id of the user storage account to delete
	 */
	public void delete(String id) {
		db.remove(Query.query(new Criteria("id").is(id)), Account.class);
	}

	/**
	 * deletes a {@link Account}
	 * 
	 * @param account
	 *            the user storage account to delete
	 */
	public void delete(Account account) {
		db.remove(account);
	}

}
