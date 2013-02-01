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

import org.springframework.data.annotation.Id;

/**
 * <i>Metadata</i> represents all the informations about a resource
 * 
 * @author mirko perillo
 * 
 */
public class Metadata {
	/**
	 * name of the resource
	 */
	private String name;

	/**
	 * resource id
	 */
	@Id
	private String rid;

	/**
	 * entity id binded to resource
	 */
	private String eid;

	/**
	 * account id in which resource is stored
	 */
	private String accountId;

	/**
	 * MIME type of resource
	 */
	private String contentType;

	/**
	 * creation time
	 */
	private long creationTs;

	/**
	 * last modification time
	 */
	private long lastModifiedTs;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = rid;
	}

	public String getEid() {
		return eid;
	}

	public void setEid(String eid) {
		this.eid = eid;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public long getCreationTs() {
		return creationTs;
	}

	public void setCreationTs(long creationTs) {
		this.creationTs = creationTs;
	}

	public long getLastModifiedTs() {
		return lastModifiedTs;
	}

	public void setLastModifiedTs(long lastModifiedTs) {
		this.lastModifiedTs = lastModifiedTs;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

}
