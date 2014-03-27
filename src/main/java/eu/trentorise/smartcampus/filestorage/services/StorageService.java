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

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.trentorise.smartcampus.filestorage.model.Account;
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
	 * 
	 * @param accountId
	 *            the id of user storage account in which store the resource
	 * @param resource
	 *            the resource to store
	 * @param the
	 *            inputstream of file
	 * @return the resource populated with the id given from storage
	 * @throws AlreadyStoredException
	 * @throws SmartcampusException
	 */
	public Resource store(String accountId, Resource resource,
			InputStream inputStream) throws AlreadyStoredException,
			SmartcampusException;

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

	public InputStream getThumbnailStream(String resourceId)
			throws NotFoundException, SmartcampusException;

	/**
	 * Specifies whether the storage requires user authorization for access.
	 * 
	 * @return true if the authorization required
	 */
	public boolean authorizationSessionRequired();

	/**
	 * Starts the authorization flow session. May persist the temporal
	 * information in the request session.
	 * 
	 * @param storageId
	 *            storage ID
	 * @param userId
	 *            user ID
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void startSession(String storageId, String userId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	/**
	 * Complete the authorization session given the attributes received in the
	 * request. Reconstruct the {@link Account} instance out of those
	 * properties.
	 * 
	 * @param storageId
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public Account finishSession(String storageId, String userId,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception;

	public void replace(Resource resource, InputStream inputStream)
			throws NotFoundException, SmartcampusException;

}
