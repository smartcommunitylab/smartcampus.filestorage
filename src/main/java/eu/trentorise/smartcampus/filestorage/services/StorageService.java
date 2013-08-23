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

package eu.trentorise.smartcampus.filestorage.services;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Token;

/**
 * The interface collects operation about storage of a resource
 * 
 * @author mirko perillo
 * 
 */
public interface StorageService {
	/**
	 * stores a resource
	 * 
	 * @param accountId
	 *            the id of user storage account in which store the resource
	 * @param resource
	 *            the resource to store
	 * @return the resource populated with the id given from storage
	 * @throws AlreadyStoredException
	 *             if resource already exists
	 * @throws SmartcampusException
	 *             general exception
	 */
	public Resource store(String accountId, Resource resource)
			throws AlreadyStoredException, SmartcampusException;

	/**
	 * updates of a resource
	 * 
	 * @param resource
	 *            the informations of resource to update
	 * @throws NotFoundException
	 *             if resource doesn't exists
	 * @throws SmartcampusException
	 *             general exception
	 */
	public void replace(Resource resource) throws NotFoundException,
			SmartcampusException;

	/**
	 * deletes a resource
	 * 
	 * @param rid
	 *            id of the resource
	 * @throws NotFoundException
	 *             if resource doesn't exist
	 * @throws SmartcampusException
	 *             general exception
	 */
	public void remove(String rid) throws NotFoundException,
			SmartcampusException;

	/**
	 * retrieves Token to access to the resource
	 * 
	 * @param accountId
	 *            id of the account
	 * @param rid
	 *            id of the resource
	 * @return the token to access the resource
	 * @throws NotFoundException
	 *             if resource doesn't exist
	 * @throws SmartcampusException
	 *             general exception
	 */
	public Token getToken(String accountId, String rid)
			throws NotFoundException, SmartcampusException;

	public String getAccountAuthUrl(String storageId) throws NotFoundException;

}
