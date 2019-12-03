/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.easycrud.scaffold.api;

import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.dto.HasId;

/**
 * This gives you a quick start for EasyCrud back-end functionality. It will
 * bootstrap Dao and Service and will also inject any dependencies if specified
 * 
 * <p>
 * 
 * You'll still have to manually define REST API controller if needed, but at
 * least you'll have a Service ready in couple lines of code
 * 
 * <p>
 * 
 * You can use generic interface {@link EasyCrudService} or you can subclass it
 * with your own interface and have instance of typed interface. lAtter will
 * give you more clearer code.
 * 
 * @author sergeyk
 *
 */
public interface EasyCrudScaffold {

	/**
	 * Build impl of the custom service interface
	 * 
	 * @param serviceInterface
	 *            custom service interface
	 * @param messageCode
	 *            entity message code
	 * @param tableName
	 *            name of the datble in the database
	 * @param injections
	 *            optional list of injections you want to make into service.
	 *            Scaffolder will automatically detect supported types and will wrap
	 *            it into wire taps if needed (or other way, depending on impl)
	 * @return impl of the service
	 */
	<TId, TDto extends HasId<TId>, TService extends EasyCrudService<TId, TDto>> TService fromService(
			Class<TService> serviceInterface, String messageCode, String tableName, Object... injections);

	/**
	 * Build impl of {@link EasyCrudService} based on the provided DTO class
	 * 
	 * <p>
	 * 
	 * Message code and Table name will be assumed based on DTO class name. I.e. if
	 * name of the DTO class is SomeDto then message code will be "SomeDto" and
	 * table name will be "some_dto".
	 * 
	 * You can use {@link #fromDto(Class, String, String, Object...)} in case you
	 * want to specify those manually. It also allows you to provide list of
	 * injections you want to do into service implementation.
	 * 
	 */
	<TId, TDto extends HasId<TId>> EasyCrudService<TId, TDto> fromDto(Class<TDto> dtoClass);

	/**
	 * 
	 * 
	 * @param dtoClass
	 *            dto that reflects row in a database
	 * @param messageCode
	 *            message code used to identify service
	 * @param tableName
	 *            name of the table in the database
	 * @param injections
	 *            optional list of injections you want to make into service.
	 *            Scaffolder will automatically detect supported types and will wrap
	 *            it into wire taps if needed (or other way, depending on impl)
	 * @return EasyCrudService ready for use
	 */
	<TId, TDto extends HasId<TId>> EasyCrudService<TId, TDto> fromDto(Class<TDto> dtoClass, String messageCode,
			String tableName, Object... injections);

}
