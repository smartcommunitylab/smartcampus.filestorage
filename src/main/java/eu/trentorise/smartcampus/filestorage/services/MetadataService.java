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

import java.util.Collection;
import java.util.List;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.Resource;

/**
 * The interface collects operation on {@link Metadata} of a {@link Resource}
 * 
 * @author mirko perillo
 * 
 */
public interface MetadataService {

	/**
	 * retrieves resource id given user storage account and name of the resource
	 * 
	 * @param accountId
	 *            id of the storage account
	 * @param filename
	 *            name of the resource
	 * @return the id of the resource
	 * @throws NotFoundException
	 *             if resource doesn't exist
	 */
	public String getResourceByFilename(String accountId, String filename)
			throws NotFoundException;

	/**
	 * retrieves id of resource given its entity social id
	 * 
	 * @param eid
	 *            id of the social entity binded to the resource
	 * @return id of the resource
	 * @throws NotFoundException
	 *             if resource dosn't exist
	 */
	public String getResourceBySocialId(String sociaId)
			throws NotFoundException;

	/**
	 * retrieves social entity id given the id of the resource
	 * 
	 * @param resourceId
	 *            id of the resource
	 * @return social entity id
	 * @throws NotFoundException
	 *             if resource doesn't exist
	 */
	public String getEntityByResource(String resourceId)
			throws NotFoundException;

	/**
	 * retrieves {@link Metadata} given its resource id
	 * 
	 * @param resourceId
	 *            id of the resource
	 * @return the metadata binded to the resource
	 * @throws NotFoundException
	 *             if resource doesn't exist
	 */
	public Metadata getMetadata(String resourceId) throws NotFoundException;

	/**
	 * retrieves all of {@link Metadata} binded to a given account id
	 * 
	 * @param accountId
	 *            the id of the user storage account
	 * @return the list of metadata found
	 * @throws NotFoundException
	 *             if the user storage account doesn't exist
	 */
	public List<Metadata> getAccountMetadata(String accountId)
			throws NotFoundException;

	/**
	 * retrieves all of {@link Metadata} of given appId
	 * 
	 * @param appId
	 *            appId
	 * @param position
	 *            integer to paginate result, null to not use it
	 * @param size
	 *            integer to paginate result,null to not use it
	 * @return the list of metadata found
	 * @throws NotFoundException
	 *             if the user storage account doesn't exist
	 */
	public List<Metadata> getMetadataByApp(String appId, Integer position,
			Integer size);

	/**
	 * retrieves all of {@link Metadata} present in a collection of accountIds
	 * 
	 * @param accountIds
	 *            collections of accountId owners of Metadata
	 * @param position
	 *            integer to paginate result, null to not use it
	 * @param size
	 *            integer to paginate result,null to not use it
	 * @return
	 */
	public List<Metadata> getMetadataByAccountIds(
			Collection<String> accountIds, Integer position, Integer size);

	/**
	 * saves a {@link Metadata}
	 * 
	 * @param metadata
	 *            the metadata to save
	 * @throws AlreadyStoredException
	 *             if metadata already exists
	 */
	public void save(Metadata metadata) throws AlreadyStoredException;

	/**
	 * deletes a {@link Metadata}
	 * 
	 * @param resourceId
	 *            the id of the resource binded to the metadata to delete
	 */
	public void delete(String resourceId);

	/**
	 * updates a {@link Metadata}. In particular updates the last modification
	 * time
	 * 
	 * @param metadata
	 *            metadata to update
	 * @throws NotFoundException
	 *             if resourceId field in metadata doesn't exist
	 */
	public void update(Metadata metadata) throws NotFoundException;
}
