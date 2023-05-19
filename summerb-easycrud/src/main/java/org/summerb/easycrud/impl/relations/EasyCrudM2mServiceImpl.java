/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package org.summerb.easycrud.impl.relations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.relations.EasyCrudM2mService;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.api.row.relations.ManyToManyRow;
import org.summerb.easycrud.api.row.relations.Ref;
import org.summerb.easycrud.api.row.tools.EasyCrudDtoUtils;
import org.summerb.easycrud.impl.EasyCrudServiceImpl;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.easycrud.api.dto.PagerParams;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * This is default impl for {@link EasyCrudM2mService}.
 *
 * <p>IMPORTANT: By default this impl will auto generate entity type message code based on
 * referencer and referencee, like referencer.to.referencee.
 *
 * <p>IMPORTANT: Queries will result in 2 round trips to the server (find ids, retrieve rows) which
 * is not perfect in terms of the performance. In case it's critical consider using your own DAO
 * implementation that can make use of joins -OR- consider caching options.
 *
 * <p>NOTE: Proposed authorization approach is to re-use (with wrapper {@link
 * M2mAuthorizationWireTapImpl}) referencer's authorization. All reads of m2m is considered as
 * referencer read, and changes to m2m collection is considered as update operation to referencer
 * object.
 *
 * <p>TBD: Consider providing default impl for cached wrapper for these m2m relationships
 *
 * @author sergeyk
 * @param <T1Id> T1Id
 * @param <T1Dto> T1Dto
 * @param <T2Id> T2Id
 * @param <T2Dto> T2Dto
 */
public class EasyCrudM2mServiceImpl<
        T1Id, T1Dto extends HasId<T1Id>, T2Id, T2Dto extends HasId<T2Id>>
    extends EasyCrudServiceImpl<
        Long, ManyToManyRow<T1Id, T2Id>, EasyCrudM2mDaoMySqlImpl<T1Id, T1Dto, T2Id, T2Dto>>
    implements EasyCrudM2mService<T1Id, T1Dto, T2Id, T2Dto> {

  protected EasyCrudService<T1Id, T1Dto> serviceA;
  protected EasyCrudService<T2Id, T2Dto> serviceB;

  @SuppressWarnings("deprecation")
  public EasyCrudM2mServiceImpl(
      EasyCrudM2mDaoMySqlImpl<T1Id, T1Dto, T2Id, T2Dto> dao,
      EasyCrudService<T1Id, T1Dto> serviceA,
      EasyCrudService<T2Id, T2Dto> serviceB) {
    super();
    this.dao = dao;
    this.rowClass = determineRowClass();

    this.serviceA = serviceA;
    this.serviceB = serviceB;

    this.setRowMessageCode(
        Ref.buildDefaultM2mEntityName(serviceA.getRowMessageCode(), serviceB.getRowMessageCode()));
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    super.afterPropertiesSet();

    Preconditions.checkArgument(serviceA != null, "serviceA is required");
    Preconditions.checkArgument(serviceB != null, "serviceB is required");
  }

  @SuppressWarnings("unchecked")
  protected Class<ManyToManyRow<T1Id, T2Id>> determineRowClass() {
    ManyToManyRow<T1Id, T2Id> example = new ManyToManyRow<>();
    return (Class<ManyToManyRow<T1Id, T2Id>>) example.getClass();
  }

  @Override
  public List<T2Dto> findReferenceeByReferencer(T1Id referencerId) {
    try {
      Preconditions.checkArgument(referencerId != null, "referencerId is required");
      Query q = buildQueryToFindReferenceeByReferencerId(referencerId);
      List<ManyToManyRow<T1Id, T2Id>> m2mPairs = findAll(q);
      if (m2mPairs.size() == 0) {
        return Collections.emptyList();
      }
      Set<T2Id> referenceeIds = collectReferenceeIds(m2mPairs);
      return serviceB.find(PagerParams.ALL, buildQueryToFindObjectsByIds(referenceeIds)).getItems();
    } catch (Throwable t) {
      throw new RuntimeException(
          "Failed to find "
              + serviceB.getRowMessageCode()
              + " refernced by "
              + serviceA.getRowMessageCode()
              + " identified by "
              + referencerId,
          t);
    }
  }

  protected Query buildQueryToFindObjectsByIds(Set<T2Id> referenceeIds) {
    T2Id referenceeId = referenceeIds.iterator().next();
    Query q;
    if (referenceeId instanceof String) {
      q = Query.n().in(ManyToManyRow.FN_ID, referenceeIds.toArray(new String[0]));
    } else if (referenceeId instanceof Long) {
      q = Query.n().in(ManyToManyRow.FN_ID, referenceeIds.toArray(new Long[0]));
    } else {
      throw new RuntimeException("Unsupported type if Id = " + referenceeId.getClass());
    }
    return q;
  }

  protected Set<T2Id> collectReferenceeIds(List<ManyToManyRow<T1Id, T2Id>> m2mPairs) {
    Set<T2Id> ret = new HashSet<>();
    for (ManyToManyRow<T1Id, T2Id> pair : m2mPairs) {
      ret.add(pair.getDst());
    }
    return ret;
  }

  protected Query buildQueryToFindReferenceeByReferencerId(T1Id referencerId) {
    Query q;
    if (referencerId instanceof String) {
      q = Query.n().eq(ManyToManyRow.FN_SRC, (String) referencerId);
    } else if (referencerId instanceof Long) {
      q = Query.n().eq(ManyToManyRow.FN_SRC, (Long) referencerId);
    } else {
      throw new RuntimeException("Unsupported type if Id = " + referencerId.getClass());
    }
    return q;
  }

  protected Query buildQueryToFindReferenceeByReferencerId(Set<T1Id> referencerIds) {
    T1Id referenceeId = referencerIds.iterator().next();
    Query q;
    if (referenceeId instanceof String) {
      q = Query.n().in(ManyToManyRow.FN_SRC, referencerIds.toArray(new String[0]));
    } else if (referenceeId instanceof Long) {
      q = Query.n().in(ManyToManyRow.FN_SRC, referencerIds.toArray(new Long[0]));
    } else {
      throw new RuntimeException("Unsupported type if Id = " + referenceeId.getClass());
    }
    return q;
  }

  @Override
  public Map<T1Id, List<T2Dto>> findReferenceeByReferencers(Set<T1Id> referencerIds) {
    try {
      Preconditions.checkArgument(
          !CollectionUtils.isEmpty(referencerIds), "referencerId is required");
      Query q = buildQueryToFindReferenceeByReferencerId(referencerIds);
      List<ManyToManyRow<T1Id, T2Id>> m2mPairs = find(PagerParams.ALL, q).getItems();
      if (m2mPairs.size() == 0) {
        return Collections.emptyMap();
      }
      Set<T2Id> referenceeIds = collectReferenceeIds(m2mPairs);
      List<T2Dto> referencee =
          serviceB.find(PagerParams.ALL, buildQueryToFindObjectsByIds(referenceeIds)).getItems();
      Map<T1Id, List<T2Dto>> ret = buildResultForFindReferenceeByReferencer(m2mPairs, referencee);
      return ret;
    } catch (Throwable t) {
      throw new RuntimeException(
          "Failed to find "
              + serviceB.getRowMessageCode()
              + " refernced by "
              + serviceA.getRowMessageCode()
              + " identified by "
              + Arrays.toString(referencerIds.toArray()),
          t);
    }
  }

  protected Map<T1Id, List<T2Dto>> buildResultForFindReferenceeByReferencer(
      List<ManyToManyRow<T1Id, T2Id>> m2mPairs, List<T2Dto> referencee) {
    Map<T2Id, T2Dto> referenceeMap = EasyCrudDtoUtils.toMapById(referencee);
    Map<T1Id, List<T2Dto>> ret = new HashMap<>();
    for (ManyToManyRow<T1Id, T2Id> pair : m2mPairs) {
      List<T2Dto> curReferencee = ret.get(pair.getSrc());
      if (curReferencee == null) {
        ret.put(pair.getSrc(), curReferencee = new ArrayList<T2Dto>());
      }
      curReferencee.add(referenceeMap.get(pair.getDst()));
    }
    return ret;
  }

  @Override
  public ManyToManyRow<T1Id, T2Id> addReferencee(T1Id referencerId, T2Id referenceeId) {
    try {
      ManyToManyRow<T1Id, T2Id> pair = new ManyToManyRow<>();
      pair.setSrc(referencerId);
      pair.setDst(referenceeId);
      return create(pair);
    } catch (Throwable t) {
      Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
      throw new RuntimeException(
          "Failed to add reference from "
              + serviceA.getRowMessageCode()
              + " identified by "
              + referencerId
              + " to "
              + serviceB.getRowMessageCode()
              + " identified by "
              + referenceeId,
          t);
    }
  }

  @Override
  public void removeReferencee(T1Id referencerId, T2Id referenceeId) throws NotAuthorizedException {
    try {
      Query q = Query.n();
      addEqQuery(ManyToManyRow.FN_SRC, referencerId, q);
      addEqQuery(ManyToManyRow.FN_DST, referenceeId, q);
      ManyToManyRow<T1Id, T2Id> pair = findOneByQuery(q);
      if (pair != null) {
        deleteById(pair.getId());
      }
    } catch (Throwable t) {
      Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
      throw new RuntimeException(
          "Failed to remove reference from "
              + serviceA.getRowMessageCode()
              + " identified by "
              + referencerId
              + " to "
              + serviceB.getRowMessageCode()
              + " identified by "
              + referenceeId,
          t);
    }
  }

  protected void addEqQuery(String fnFrom, Object id, Query q) {
    if (id instanceof String) {
      q.eq(fnFrom, (String) id);
    } else if (id instanceof Long) {
      q.eq(fnFrom, (Long) id);
    } else {
      throw new RuntimeException("Unsupported type if Id = " + id.getClass());
    }
  }
}
