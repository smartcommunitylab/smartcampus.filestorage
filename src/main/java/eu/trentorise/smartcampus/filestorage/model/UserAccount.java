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

import org.springframework.data.annotation.Transient;

/**
 * User storage account informations
 * 
 * @author mirko perillo
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UserAccount {

	@Transient
	private static final long PUBLIC_ACCOUNT = -1000;

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
	private String appAccountId;

	private String appName;

	private StorageType storageType;

	private String accountName;
	/**
	 * list of the configurations of the account storage
	 */
	@XmlElementWrapper
	@XmlElement(name = "configuration")
	private List<Configuration> configurations;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
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

	public String getAppAccountId() {
		return appAccountId;
	}

	public void setAppAccountId(String appAccountId) {
		this.appAccountId = appAccountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public boolean isPublic() {
		return userId == PUBLIC_ACCOUNT;
	}

}
