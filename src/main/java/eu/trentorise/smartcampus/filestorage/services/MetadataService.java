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

import java.util.List;

import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.Metadata;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;

public interface MetadataService {

	public String getResourceByFilename(String accountId, String filename)
			throws NotFoundException;

	public String getResourceByEntity(String eid) throws NotFoundException;

	public String getEntityByResource(String rid) throws NotFoundException;

	public Metadata getMetadata(String rid) throws NotFoundException;

	public List<Metadata> getAccountMetadata(String accountId)
			throws NotFoundException;

	public void save(Metadata metadata) throws AlreadyStoredException;

	public void delete(String rid);

	public void update(Metadata metadata) throws NotFoundException;
}
