/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.CollectionUtils;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.relations.ManyToManyDto;
import org.summerb.easycrud.api.dto.relations.Ref;
import org.summerb.easycrud.api.dto.tools.EasyCrudDtoUtils;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.relations.EasyCrudM2mService;
import org.summerb.easycrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * This is default impl for {@link EasyCrudM2mService}.
 * 
 * IMPORTANT: By default this impl will auto generate entity type message code
 * based on referencer and referencee, like referencer.to.referencee.
 * 
 * IMPORTANT: Queries will result in 2 round trips to the server (find ids,
 * retrieve dtos) which is not perfect in terms of the performance. In case it's
 * critical consider using your own DAO implementation that can make use of
 * joins -OR- consider caching options.
 * 
 * NOTE: Proposed authorization approach is to re-use (with wrapper
 * {@link M2mAuthorizationWireTapImpl}) referencer's authorization. All reads of
 * m2m is considered as referencer read, add changes to m2m collection is
 * considered as update operation to referencer object.
 * 
 * TBD: Consider providing default impl for cached wrapper for these m2m
 * relationships
 * 
 * @author sergeyk
 *
 * @param <T1Id>
 * @param <T1Dto>
 * @param <T2Id>
 * @param <T2Dto>
 */
public class EasyCrudM2mServiceImpl<T1Id, T1Dto extends HasId<T1Id>, T2Id, T2Dto extends HasId<T2Id>> extends
		EasyCrudServicePluggableImpl<Long, ManyToManyDto<T1Id, T2Id>, EasyCrudM2mDaoImpl<T1Id, T1Dto, T2Id, T2Dto>>
		implements EasyCrudM2mService<T1Id, T1Dto, T2Id, T2Dto> {

	private EasyCrudService<T1Id, T1Dto> serviceFrom;
	private EasyCrudService<T2Id, T2Dto> serviceTo;

	@SuppressWarnings("unchecked")
	@Override
	public void afterPropertiesSet() throws Exception {
		if (getEntityTypeMessageCode() == null) {
			setEntityTypeMessageCode(Ref.buildDefaultM2mEntityName(serviceFrom.getEntityTypeMessageCode(),
					serviceTo.getEntityTypeMessageCode()));
		}

		ManyToManyDto<T1Id, T2Id> example = new ManyToManyDto<>();
		setDtoClass((Class<ManyToManyDto<T1Id, T2Id>>) example.getClass());

		super.afterPropertiesSet();
	}

	@Override
	public List<T2Dto> findReferenceeByReferencer(T1Id referencerId) {
		try {
			Preconditions.checkArgument(referencerId != null, "referencerId is required");
			Query q = buildQueryToFindReferenceeByReferencerId(referencerId);
			List<ManyToManyDto<T1Id, T2Id>> m2mPairs = query(PagerParams.ALL, q).getItems();
			if (m2mPairs.size() == 0) {
				return Collections.emptyList();
			}
			Set<T2Id> referenceeIds = collectReferenceeIds(m2mPairs);
			return serviceTo.query(PagerParams.ALL, buildQueryToFindObjectsByIds(referenceeIds)).getItems();
		} catch (Throwable t) {
			throw new RuntimeException("Failed to find " + serviceTo.getEntityTypeMessageCode() + " refernced by "
					+ serviceFrom.getEntityTypeMessageCode() + " identified by " + referencerId, t);
		}
	}

	private Query buildQueryToFindObjectsByIds(Set<T2Id> referenceeIds) {
		T2Id referenceeId = referenceeIds.iterator().next();
		Query q;
		if (referenceeId instanceof String) {
			q = Query.n().in(ManyToManyDto.FN_ID, referenceeIds.toArray(new String[0]));
		} else if (referenceeId instanceof Long) {
			q = Query.n().in(ManyToManyDto.FN_ID, referenceeIds.toArray(new Long[0]));
		} else {
			throw new RuntimeException("Unsupported type if Id = " + referenceeId.getClass());
		}
		return q;
	}

	private Set<T2Id> collectReferenceeIds(List<ManyToManyDto<T1Id, T2Id>> m2mPairs) {
		Set<T2Id> ret = new HashSet<>();
		for (ManyToManyDto<T1Id, T2Id> pair : m2mPairs) {
			ret.add(pair.getDst());
		}
		return ret;
	}

	private Query buildQueryToFindReferenceeByReferencerId(T1Id referencerId) {
		Query q;
		if (referencerId instanceof String) {
			q = Query.n().eq(ManyToManyDto.FN_SRC, (String) referencerId);
		} else if (referencerId instanceof Long) {
			q = Query.n().eq(ManyToManyDto.FN_SRC, (Long) referencerId);
		} else {
			throw new RuntimeException("Unsupported type if Id = " + referencerId.getClass());
		}
		return q;
	}

	private Query buildQueryToFindReferenceeByReferencerId(Set<T1Id> referencerIds) {
		T1Id referenceeId = referencerIds.iterator().next();
		Query q;
		if (referenceeId instanceof String) {
			q = Query.n().in(ManyToManyDto.FN_SRC, referencerIds.toArray(new String[0]));
		} else if (referenceeId instanceof Long) {
			q = Query.n().in(ManyToManyDto.FN_SRC, referencerIds.toArray(new Long[0]));
		} else {
			throw new RuntimeException("Unsupported type if Id = " + referenceeId.getClass());
		}
		return q;
	}

	@Override
	public Map<T1Id, List<T2Dto>> findReferenceeByReferencers(Set<T1Id> referencerIds) {
		try {
			Preconditions.checkArgument(!CollectionUtils.isEmpty(referencerIds), "referencerId is required");
			Query q = buildQueryToFindReferenceeByReferencerId(referencerIds);
			List<ManyToManyDto<T1Id, T2Id>> m2mPairs = query(PagerParams.ALL, q).getItems();
			if (m2mPairs.size() == 0) {
				// Q: Should we fill key set with null (or empty lists)??...
				return Collections.emptyMap();
			}
			Set<T2Id> referenceeIds = collectReferenceeIds(m2mPairs);
			List<T2Dto> referencee = serviceTo.query(PagerParams.ALL, buildQueryToFindObjectsByIds(referenceeIds))
					.getItems();
			Map<T1Id, List<T2Dto>> ret = buildResultForFindReferenceeByReferencer(m2mPairs, referencee);
			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to find " + serviceTo.getEntityTypeMessageCode() + " refernced by "
					+ serviceFrom.getEntityTypeMessageCode() + " identified by "
					+ Arrays.toString(referencerIds.toArray()), t);
		}
	}

	private Map<T1Id, List<T2Dto>> buildResultForFindReferenceeByReferencer(List<ManyToManyDto<T1Id, T2Id>> m2mPairs,
			List<T2Dto> referencee) {
		Map<T2Id, T2Dto> referenceeMap = EasyCrudDtoUtils.toMapById(referencee);
		Map<T1Id, List<T2Dto>> ret = new HashMap<>();
		for (ManyToManyDto<T1Id, T2Id> pair : m2mPairs) {
			List<T2Dto> curReferencee = ret.get(pair.getSrc());
			if (curReferencee == null) {
				ret.put(pair.getSrc(), curReferencee = new ArrayList<T2Dto>());
			}
			curReferencee.add(referenceeMap.get(pair.getDst()));
		}
		return ret;
	}

	@Override
	public ManyToManyDto<T1Id, T2Id> addReferencee(T1Id referencerId, T2Id referenceeId)
			throws FieldValidationException, NotAuthorizedException {
		try {
			ManyToManyDto<T1Id, T2Id> pair = new ManyToManyDto<>();
			pair.setSrc(referencerId);
			pair.setDst(referenceeId);
			return create(pair);
		} catch (Throwable t) {
			Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
			throw new RuntimeException("Failed to add reference from " + serviceFrom.getEntityTypeMessageCode()
					+ " identified by " + referencerId + " to " + serviceTo.getEntityTypeMessageCode()
					+ " identified by " + referenceeId, t);
		}
	}

	@Override
	public void removeReferencee(T1Id referencerId, T2Id referenceeId) throws NotAuthorizedException {
		try {
			Query q = Query.n();
			addEqQuery(ManyToManyDto.FN_SRC, referencerId, q);
			addEqQuery(ManyToManyDto.FN_DST, referenceeId, q);
			ManyToManyDto<T1Id, T2Id> pair = findOneByQuery(q);
			try {
				if (pair == null) {
					throw new GenericEntityNotFoundException(getEntityTypeMessageCode(),
							"" + referencerId + "<->" + referenceeId);
				}
				deleteById(pair.getId());
			} catch (EntityNotFoundException e) {
				// that's ok, we wanted it to not exist, it's not there. This
				// state
				// is acceptable
			}
		} catch (Throwable t) {
			Throwables.throwIfInstanceOf(t, NotAuthorizedException.class);
			throw new RuntimeException("Failed to remove reference from " + serviceFrom.getEntityTypeMessageCode()
					+ " identified by " + referencerId + " to " + serviceTo.getEntityTypeMessageCode()
					+ " identified by " + referenceeId, t);
		}
	}

	private void addEqQuery(String fnFrom, Object id, Query q) {
		if (id instanceof String) {
			q.eq(fnFrom, (String) id);
		} else if (id instanceof Long) {
			q.eq(fnFrom, (Long) id);
		} else {
			throw new RuntimeException("Unsupported type if Id = " + id.getClass());
		}
	}

	public EasyCrudService<T1Id, T1Dto> getServiceFrom() {
		return serviceFrom;
	}

	@Required
	public void setServiceFrom(EasyCrudService<T1Id, T1Dto> serviceFrom) {
		this.serviceFrom = serviceFrom;
	}

	public EasyCrudService<T2Id, T2Dto> getServiceTo() {
		return serviceTo;
	}

	@Required
	public void setServiceTo(EasyCrudService<T2Id, T2Dto> serviceTo) {
		this.serviceTo = serviceTo;
	}

}
