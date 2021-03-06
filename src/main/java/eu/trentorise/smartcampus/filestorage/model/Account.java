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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * User storage account informations
 * 
 * @author mirko perillo
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Account {
	/**
	 * id of the account
	 */
	private String id;
	/**
	 * id of the user
	 */
	private String userId;
	/**
	 * appId reference
	 */
	private String appId;

	/**
	 * type of the storage
	 */
	private StorageType storageType;

	/**
	 * name for the user storage account
	 */
	private String name;
	/**
	 * list of the configurations of the account storage
	 */
	@XmlElementWrapper
	@XmlElement(name = "configuration")
	private List<Configuration> configurations;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storage) {
		this.storageType = storage;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public boolean isValid() {
		return appId != null && userId != null && storageType != null;
	}

	public boolean isSame(Account account) {
		return id.equals(account.getId()) && appId.equals(account.getAppId())
				&& userId.equals(account.getUserId())
				&& storageType == account.getStorageType();
	}

}
