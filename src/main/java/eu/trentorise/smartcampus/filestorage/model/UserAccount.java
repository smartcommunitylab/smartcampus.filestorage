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

package eu.trentorise.smartcampus.filestorage.model;

import java.util.List;

/**
 * User storage account informations
 * 
 * @author mirko perillo
 * 
 */
public class UserAccount {
	/**
	 * id of the account
	 */
	private String id;
	/**
	 * id of the user
	 */
	private long userId;
	/**
	 * type of the storage
	 */
	private StorageType storage;
	/**
	 * list of the configurations of the account storage
	 */
	private List<Configuration> configurations;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public StorageType getStorage() {
		return storage;
	}

	public void setStorage(StorageType storage) {
		this.storage = storage;
	}

	public List<Configuration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<Configuration> configurations) {
		this.configurations = configurations;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
