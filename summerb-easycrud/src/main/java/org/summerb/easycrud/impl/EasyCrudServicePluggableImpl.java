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
package org.summerb.easycrud.impl;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easycrud.api.EasyCrudDao;
import org.summerb.easycrud.api.EasyCrudExceptionStrategy;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.StringIdGenerator;
import org.summerb.easycrud.api.dto.HasAuthor;
import org.summerb.easycrud.api.dto.HasAutoincrementId;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.HasTimestamps;
import org.summerb.easycrud.api.dto.HasUuid;
import org.summerb.easycrud.api.dto.PagerParams;
import org.summerb.easycrud.api.dto.PaginatedList;
import org.summerb.easycrud.api.dto.Top;
import org.summerb.easycrud.api.exceptions.EntityNotFoundException;
import org.summerb.easycrud.api.exceptions.GenericEntityNotFoundException;
import org.summerb.easycrud.api.query.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.security.api.CurrentUserResolver;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.utils.objectcopy.Clonnable;
import org.summerb.utils.objectcopy.DeepCopy;
import org.summerb.validation.FieldValidationException;

import com.google.common.base.Preconditions;

/**
 * Default impl of EasyCrudService, with focus on OOD:OCP principle. In case
 * some logic needs to be changed you can do it via {@link EasyCrudWireTap}
 * interface - you don't need to write another impl of {@link EasyCrudService}
 * each time you need to change it's behavior.
 * 
 * If your DTO implements {@link HasAuthor} interface then this impl requires
 * compatible {@link CurrentUserResolver} to be injected.
 * 
 * @author sergeyk
 *
 * @param <TId>  type of id
 * @param <TDto> type of dto (must have {@link HasId} interface
 * @param <TDao> type of dao, must be a subclass of {@link EasyCrudDao}
 */
public class EasyCrudServicePluggableImpl<TId, TDto extends HasId<TId>, TDao extends EasyCrudDao<TId, TDto>>
		implements EasyCrudService<TId, TDto>, InitializingBean {
	public static final String OPTIMISTIC_LOCK_FAILED_TECH_MESSAGE = "Optimistic lock failed, record was already updated but someone else";
	private static final PagerParams TOP_ONE = new Top(1);

	protected EasyCrudExceptionStrategy<TId> exceptionStrategy;
	private EasyCrudWireTap<TId, TDto> wireTap;

	protected TDao dao;
	@Autowired(required = false)
	protected CurrentUserResolver<?> currentUserResolver;
	private Class<TDto> rowClass;
	private String rowMessageCode;
	private StringIdGenerator stringIdGenerator;

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkState(rowClass != null, "DtoClass is required");
		Preconditions.checkState(!HasAuthor.class.isAssignableFrom(rowClass) || currentUserResolver != null,
				"CurrentUserResolver required");
		Preconditions.checkState(dao != null, "dao is required");

		if (rowMessageCode == null) {
			rowMessageCode = rowClass.getCanonicalName();
		}

		if (wireTap == null) {
			wireTap = new EasyCrudWireTapNoOpImpl<TId, TDto>();
		}

		if (exceptionStrategy == null) {
			exceptionStrategy = new EasyCrudExceptionStrategyDefaultImpl<TId>(rowMessageCode);
		}

		if (stringIdGenerator == null) {
			stringIdGenerator = new StringIdGeneratorUuidImpl();
		}
	}

	@Override
	public TDto create(TDto row) throws FieldValidationException, NotAuthorizedException {
		try {
			Preconditions.checkArgument(row != null);

			TDto ret = copyDto(row);

			boolean wireTapRequired = wireTap.requiresOnCreate();
			if (wireTapRequired) {
				wireTap.beforeCreate(ret);
			}

			if (ret instanceof HasAuthor) {
				HasAuthor hasAuthor = (HasAuthor) ret;
				String currentUserUuid = currentUserResolver.getUserUuid();
				hasAuthor.setCreatedBy(currentUserUuid);
				hasAuthor.setModifiedBy(currentUserUuid);
			}

			dao.create(ret);

			if (ret instanceof HasAutoincrementId) {
				Preconditions.checkState(((HasAutoincrementId) ret).getId() != null,
						"For DTO with HasAutoincrementId id field expected to be filled after creation");
			}

			if (ret instanceof HasUuid) {
				Preconditions.checkState(stringIdGenerator.isValidId(((HasUuid) ret).getId()),
						"For DTO with HasUuid id field expected to be filled after creation");
			}

			if (wireTapRequired) {
				wireTap.afterCreate(ret);
			}
			return ret;
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtCreate(t);
		}
	}

	@SuppressWarnings("unchecked")
	private TDto copyDto(TDto dto) {
		try {
			if (dto instanceof Clonnable) {
				return ((Clonnable<TDto>) dto).clone();
			}
			return DeepCopy.copyOrPopagateExcIfAny(dto);
		} catch (NotSerializableException nse) {
			throw new RuntimeException("Some files are not serializable. Consider implementing Clonnable interface",
					nse);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to clone dto", t);
		}
	}

	@Override
	public TDto update(TDto newVersion)
			throws FieldValidationException, NotAuthorizedException, EntityNotFoundException {
		try {
			Preconditions.checkArgument(newVersion != null);

			TDto currentVersion = dao.findById(newVersion.getId());
			if (currentVersion == null) {
				throw exceptionStrategy.buildNotFoundException(rowMessageCode, newVersion.getId());
			}

			TDto ret = copyDto(newVersion);

			boolean wireTapRequired = wireTap.requiresOnUpdate();
			if (wireTapRequired) {
				wireTap.beforeUpdate(currentVersion, ret);
			}

			if (ret instanceof HasAuthor) {
				HasAuthor hasAuthor = (HasAuthor) ret;
				hasAuthor.setModifiedBy(currentUserResolver.getUserUuid());
			}

			if (dao.update(ret) != 1) {
				throw exceptionStrategy.buildOptimisticLockException();
			}

			if (ret instanceof HasTimestamps) {
				HasTimestamps retTimestamps = (HasTimestamps) ret;
				HasTimestamps newTimestamps = (HasTimestamps) newVersion;

				Preconditions.checkState(retTimestamps.getModifiedAt() > newTimestamps.getModifiedAt(),
						"ModifiedAt expected to be increased after update");
			}

			if (wireTapRequired) {
				wireTap.afterUpdate(currentVersion, ret);
			}
			return ret;
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtUpdate(t);
		}
	}

	@Override
	public void delete(TDto row) {
		try {
			Preconditions.checkArgument(row != null);
			Preconditions.checkArgument(row.getId() != null);

			if (row instanceof HasTimestamps) {
				deleteByIdOptimistic(row.getId(), ((HasTimestamps) row).getModifiedAt());
			} else {
				deleteById(row.getId());
			}
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtDelete(t);
		}
	}

	@Override
	public void deleteById(TId id) throws NotAuthorizedException, EntityNotFoundException {
		try {
			Preconditions.checkArgument(id != null);

			TDto existing = findById(id);
			if (existing == null) {
				throw exceptionStrategy.buildNotFoundException(rowMessageCode, id);
			}
			boolean wireTapRequired = wireTap.requiresOnDelete();
			if (wireTapRequired) {
				wireTap.beforeDelete(existing);
			}

			int affected = dao.delete(id);
			if (affected != 1) {
				throw exceptionStrategy.buildNotFoundException(rowMessageCode, id);
			}

			if (wireTapRequired) {
				wireTap.afterDelete(existing);
			}
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtDelete(t);
		}
	}

	@Override
	public void deleteByIdOptimistic(TId id, long modifiedAt) throws NotAuthorizedException, EntityNotFoundException {
		try {
			Preconditions.checkArgument(id != null);
			Preconditions.checkState(HasTimestamps.class.isAssignableFrom(rowClass),
					"Delete using optimistic lock is not allowed for DTO which doesn't support HasTimestamps");

			TDto existing = findById(id);
			if (existing == null) {
				throw exceptionStrategy.buildNotFoundException(rowMessageCode, id);
			}
			boolean wireTapRequired = wireTap.requiresOnDelete();
			if (wireTapRequired) {
				wireTap.beforeDelete(existing);
			}

			int affected = dao.delete(id, modifiedAt);
			if (affected != 1) {
				throw exceptionStrategy.buildOptimisticLockException();
			}

			if (wireTapRequired) {
				wireTap.afterDelete(existing);
			}
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtDelete(t);
		}
	}

	@Override
	public int deleteByQuery(Query query) throws NotAuthorizedException {
		try {
			Preconditions.checkArgument(query != null);

			if (!wireTap.requiresOnDelete()) {
				return dao.deleteByQuery(query);
			}

			PaginatedList<TDto> toDelete = find(PagerParams.ALL, query);
			List<TDto> deleted = new ArrayList<TDto>();
			for (TDto dto : toDelete.getItems()) {
				wireTap.beforeDelete(dto);
				if (dto instanceof HasTimestamps) {
					int affected = dao.delete(dto.getId(), ((HasTimestamps) dto).getModifiedAt());
					if (affected == 1) {
						deleted.add(dto);
					}
				} else {
					int affected = dao.delete(dto.getId());
					if (affected == 1) {
						deleted.add(dto);
					}
				}
			}

			for (TDto dto : deleted) {
				wireTap.afterDelete(dto);
			}

			return deleted.size();
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtDeleteByQuery(t);
		}
	}

	@Override
	public TDto findById(TId id) throws NotAuthorizedException {
		try {
			Preconditions.checkArgument(id != null);
			boolean wireTapRequired = wireTap.requiresOnRead();
			TDto ret = dao.findById(id);
			if (ret != null && wireTapRequired) {
				wireTap.afterRead(ret);
			}
			return ret;
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtFind(t);
		}
	}

	@Override
	public TDto getById(TId id) {
		TDto ret = findById(id);
		if (ret == null) {
			throw new GenericEntityNotFoundException(rowMessageCode, id);
		}
		return ret;
	}

	@Override
	public TDto findOneByQuery(Query query) throws NotAuthorizedException {
		try {
			Preconditions.checkArgument(query != null);
			boolean wireTapRequired = wireTap.requiresOnRead();
			TDto ret = dao.findOneByQuery(query);
			if (ret != null && wireTapRequired) {
				wireTap.afterRead(ret);
			}
			return ret;
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtFind(t);
		}
	}

	@Override
	public PaginatedList<TDto> find(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy)
			throws NotAuthorizedException {
		try {
			Preconditions.checkArgument(pagerParams != null, "PagerParams is a must");
			PaginatedList<TDto> ret = dao.query(pagerParams, optionalQuery, orderBy);
			if (wireTap.requiresOnRead()) {
				for (TDto dto : ret.getItems()) {
					wireTap.afterRead(dto);
				}
			}
			return ret;
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtFind(t);
		}
	}

	@Override
	public List<TDto> findAll(Query optionalQuery, OrderBy... orderBy) {
		return find(PagerParams.ALL, optionalQuery, orderBy).getItems();
	}

	@Override
	public List<TDto> findAll(OrderBy... orderBy) {
		return find(PagerParams.ALL, null, orderBy).getItems();
	}

	@Override
	public TDto getFirstByQuery(Query query, OrderBy... orderBy) {
		PaginatedList<TDto> results = find(TOP_ONE, null, orderBy);
		if (results.getItems().isEmpty()) {
			throw new GenericEntityNotFoundException(rowMessageCode, query);
		}
		return results.getItems().get(0);
	}

	public TDao getDao() {
		return dao;
	}

	public void setDao(TDao dao) {
		this.dao = dao;
	}

	public CurrentUserResolver<?> getCurrentUserResolver() {
		return currentUserResolver;
	}

	/**
	 * NOTE: Not required for Dto that don't have HasAuthor
	 */
	public void setCurrentUserResolver(CurrentUserResolver<?> currentUserResolver) {
		this.currentUserResolver = currentUserResolver;
	}

	@Override
	public Class<TDto> getRowClass() {
		return rowClass;
	}

	public void setRowClass(Class<TDto> dtoClass) {
		this.rowClass = dtoClass;
	}

	public EasyCrudExceptionStrategy<TId> getGenericExceptionStrategy() {
		return exceptionStrategy;
	}

	public void setGenericExceptionStrategy(EasyCrudExceptionStrategy<TId> easyCrudExceptionStrategy) {
		this.exceptionStrategy = easyCrudExceptionStrategy;
	}

	@Override
	public String getRowMessageCode() {
		return rowMessageCode;
	}

	public void setRowMessageCode(String entityTypeMessageCode) {
		this.rowMessageCode = entityTypeMessageCode;
	}

	public EasyCrudWireTap<TId, TDto> getWireTap() {
		return wireTap;
	}

	public void setWireTap(EasyCrudWireTap<TId, TDto> wireTap) {
		this.wireTap = wireTap;
	}

	public StringIdGenerator getStringIdGenerator() {
		return stringIdGenerator;
	}

	@Autowired(required = false)
	public void setStringIdGenerator(StringIdGenerator stringIdGenerator) {
		this.stringIdGenerator = stringIdGenerator;
	}

}
