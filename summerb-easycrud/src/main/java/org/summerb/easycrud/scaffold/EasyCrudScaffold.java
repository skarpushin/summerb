/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.easycrud.scaffold;

import org.summerb.easycrud.EasyCrudService;
import org.summerb.easycrud.dao.EasyCrudDao;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.easycrud.row.HasId;

/**
 * This gives you a quick start for EasyCrud back-end functionality. It will bootstrap Dao and
 * Service and will also inject any dependencies if specified
 *
 * <p>You'll still have to manually define REST API controller if needed, but at least you'll have a
 * Service ready in couple lines of code
 *
 * <p>You can use generic interface {@link EasyCrudService} or you can subclass it with your own
 * interface and have instance of typed interface. lAfter will give you clearer code.
 *
 * @author sergeyk
 */
public interface EasyCrudScaffold {

  /**
   * Build impl of the custom service interface
   *
   * @param serviceInterface custom service interface
   * @param messageCode entity message code
   * @param tableName name of the table in the database
   * @param injections optional list of injections you want to make into service. Scaffolder will
   *     automatically detect supported types and will wrap it into wire taps if needed (or other
   *     way, depending on impl)
   * @param <TId> type of id
   * @param <TRow> row type
   * @param <TService> service type
   * @return impl of the service
   */
  <TId, TRow extends HasId<TId>, TService extends EasyCrudService<TId, TRow>> TService fromService(
      Class<TService> serviceInterface, String messageCode, String tableName, Object... injections);

  /**
   * Initialize given impl of a service with scaffolded DAO. This is useful in case when you want to
   * add own methods to service, but do not want/need to deal with DAO specifics
   *
   * @param serviceInterface service interface
   * @param serviceImpl implementation. Expected to be partially initialized (namely - rowClass is
   *     set, but afterPropertiesSet() is not called yet)
   * @param tableName name of the table in the database
   * @param injections optional list of injections you want to make into service. Scaffolder will
   *     automatically detect supported types and will wrap it into wire taps if needed (or other
   *     way, depending on impl)
   * @return implementation itself cast to given interface
   * @param <TId> type of id
   * @param <TRow> row type
   * @param <TService> service type
   * @param <TServiceImpl> type of the service impl
   */
  <
          TId,
          TRow extends HasId<TId>,
          TService extends EasyCrudService<TId, TRow>,
          TServiceImpl extends EasyCrudServiceImpl<TId, TRow, EasyCrudDao<TId, TRow>>>
      TService fromService(
          Class<TService> serviceInterface,
          TServiceImpl serviceImpl,
          String tableName,
          Object... injections);

  /**
   * Build impl of {@link EasyCrudService} based on the provided DTO class
   *
   * <p>Message code and Table name will be assumed based on DTO class name. I.e. if name of the DTO
   * class is SomeDto then message code will be "SomeDto" and table name will be "some_dto".
   *
   * <p>You can use {@link #fromRowClass(Class, String, String, Object...)} in case you want to
   * specify those manually. It also allows you to provide list of injections you want to do into
   * service implementation.
   *
   * @param <TId> type of id
   * @param <TRow> type of row
   * @param rowClass row class
   * @return impl of the service
   */
  <TId, TRow extends HasId<TId>> EasyCrudService<TId, TRow> fromRowClass(Class<TRow> rowClass);

  /**
   * @param rowClass dto that reflects row in a database
   * @param messageCode message code used to identify service
   * @param tableName name of the table in the database
   * @param injections optional list of injections you want to make into service. Scaffolder will
   *     automatically detect supported types and will wrap it into wire taps if needed (or other
   *     way, depending on impl)
   * @param <TId> type of id
   * @param <TRow> type of row
   * @return EasyCrudService ready for use
   */
  <TId, TRow extends HasId<TId>> EasyCrudService<TId, TRow> fromRowClass(
      Class<TRow> rowClass, String messageCode, String tableName, Object... injections);
}
