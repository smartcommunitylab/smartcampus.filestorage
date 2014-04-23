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

import eu.trentorise.smartcampus.filestorage.model.Operation;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.rest.OauthUser;

/**
 * This interface collects operation about access control list and permission on
 * resources
 * 
 * @author mirko perillo
 * 
 */
public interface ACLService {

	/**
	 * checks if a user can do a specific operation on a user
	 * 
	 * @param operation
	 *            operation to do
	 * @param resourceId
	 *            id of the resource
	 * @param user
	 *            user
	 * @return true if operation is permitted, false otherwise
	 */
	public boolean isPermitted(Operation operation, String resourceId,
			OauthUser user);

	/**
	 * retrieves the operations permitted to a user on a resource
	 * 
	 * @param resourceId
	 *            id of the resource
	 * @param user
	 *            user
	 * @return the array of operation permitted
	 */
	public Operation[] getPermissions(String resourceId, OauthUser user);

	/**
	 * retrieves the Token to performs given operation on the resource
	 * 
	 * @param operation
	 *            operation to perform on the resource
	 * @param user
	 *            user who do the operation
	 * @param resourceId
	 *            id of the resource
	 * @param owned
	 *            true to look at a owned resource
	 * @return the token
	 * @throws SmartcampusException
	 *             generic exception
	 * @throws SecurityException
	 *             if user has no privileges to do the operation on the resource
	 */
	public Token getSessionToken(Operation operation, OauthUser user,
			String resourceId, boolean owned) throws SmartcampusException,
			SecurityException;

}
