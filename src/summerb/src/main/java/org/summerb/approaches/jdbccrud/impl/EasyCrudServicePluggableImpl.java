package org.summerb.approaches.jdbccrud.impl;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.approaches.jdbccrud.api.EasyCrudDao;
import org.summerb.approaches.jdbccrud.api.EasyCrudExceptionStrategy;
import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.EasyCrudWireTap;
import org.summerb.approaches.jdbccrud.api.dto.HasAuthor;
import org.summerb.approaches.jdbccrud.api.dto.HasAutoincrementId;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.HasTimestamps;
import org.summerb.approaches.jdbccrud.api.dto.HasUuid;
import org.summerb.approaches.jdbccrud.api.dto.PagerParams;
import org.summerb.approaches.jdbccrud.api.dto.PaginatedList;
import org.summerb.approaches.jdbccrud.api.exceptions.EntityNotFoundException;
import org.summerb.approaches.jdbccrud.api.query.OrderBy;
import org.summerb.approaches.jdbccrud.api.query.Query;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.approaches.security.api.CurrentUserResolver;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationUtils;
import org.summerb.utils.Clonnable;
import org.summerb.utils.DeepCopy;

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
 * @param <TId>
 *            type of id
 * @param <TDto>
 *            type of dto (must have {@link HasId} interface
 * @param <TDao>
 *            type of dao, must be a subclass of {@link EasyCrudDao}
 */
public class EasyCrudServicePluggableImpl<TId, TDto extends HasId<TId>, TDao extends EasyCrudDao<TId, TDto>>
		implements EasyCrudService<TId, TDto>, InitializingBean {
	public static final String OPTIMISTIC_LOCK_FAILED_TECH_MESSAGE = "Optimistic lock failed, record was already updated but someone else";

	protected EasyCrudExceptionStrategy<TId> exceptionStrategy;
	private EasyCrudWireTap<TId, TDto> wireTap;

	protected TDao dao;
	@Autowired(required = false)
	protected CurrentUserResolver<?> currentUserResolver;
	private Class<TDto> dtoClass;
	private String entityTypeMessageCode;

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkState(dtoClass != null, "DtoClass is required");
		Preconditions.checkState(!HasAuthor.class.isAssignableFrom(dtoClass) || currentUserResolver != null,
				"CurrentUserResolver required");

		if (entityTypeMessageCode == null) {
			entityTypeMessageCode = dtoClass.getCanonicalName();
		}

		if (wireTap == null) {
			wireTap = new EasyCrudWireTapNoOpImpl<TId, TDto>();
		}

		if (exceptionStrategy == null) {
			exceptionStrategy = new EasyCrudExceptionStrategyDefaultImpl<TId>(getEntityTypeMessageCode());
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public TDto create(TDto dto) throws FieldValidationException, NotAuthorizedException {
		try {
			Preconditions.checkArgument(dto != null);

			TDto ret = copyDto(dto);

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
				Preconditions.checkState(ValidationUtils.isValidNotNullableUuid(((HasUuid) ret).getId()),
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
	@Transactional(rollbackFor = Throwable.class)
	public TDto update(TDto newVersion)
			throws FieldValidationException, NotAuthorizedException, EntityNotFoundException {
		try {
			Preconditions.checkArgument(newVersion != null);

			TDto currentVersion = dao.findById(newVersion.getId());
			if (currentVersion == null) {
				throw exceptionStrategy.buildNotFoundException(getEntityTypeMessageCode(), newVersion.getId());
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
	@Transactional(rollbackFor = Throwable.class)
	public void deleteById(TId id) throws NotAuthorizedException, EntityNotFoundException {
		try {
			Preconditions.checkArgument(id != null);

			TDto existing = findById(id);
			if (existing == null) {
				throw exceptionStrategy.buildNotFoundException(getEntityTypeMessageCode(), id);
			}
			boolean wireTapRequired = wireTap.requiresOnDelete();
			if (wireTapRequired) {
				wireTap.beforeDelete(existing);
			}

			int affected = dao.delete(id);
			if (affected != 1) {
				throw exceptionStrategy.buildNotFoundException(getEntityTypeMessageCode(), id);
			}

			if (wireTapRequired) {
				wireTap.afterDelete(existing);
			}
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtDelete(t);
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteByIdOptimistic(TId id, long modifiedAt) throws NotAuthorizedException, EntityNotFoundException {
		try {
			Preconditions.checkArgument(id != null);
			Preconditions.checkState(HasTimestamps.class.isAssignableFrom(dtoClass),
					"Delete using optimistic lock is not allowed for DTO which doesn't support HasTimestamps");

			TDto existing = findById(id);
			if (existing == null) {
				throw exceptionStrategy.buildNotFoundException(getEntityTypeMessageCode(), id);
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
	@Transactional(rollbackFor = Throwable.class)
	public int deleteByQuery(Query query) throws NotAuthorizedException {
		try {
			Preconditions.checkArgument(query != null);

			if (!wireTap.requiresOnDelete()) {
				return dao.deleteByQuery(query);
			}

			PaginatedList<TDto> toDelete = query(PagerParams.ALL, query);
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
	public PaginatedList<TDto> query(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy)
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

	public TDao getDao() {
		return dao;
	}

	@Required
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
	public Class<TDto> getDtoClass() {
		return dtoClass;
	}

	public void setDtoClass(Class<TDto> dtoClass) {
		this.dtoClass = dtoClass;
	}

	public EasyCrudExceptionStrategy<TId> getGenericExceptionStrategy() {
		return exceptionStrategy;
	}

	public void setGenericExceptionStrategy(EasyCrudExceptionStrategy<TId> easyCrudExceptionStrategy) {
		this.exceptionStrategy = easyCrudExceptionStrategy;
	}

	@Override
	public String getEntityTypeMessageCode() {
		return entityTypeMessageCode;
	}

	public void setEntityTypeMessageCode(String entityTypeMessageCode) {
		this.entityTypeMessageCode = entityTypeMessageCode;
	}

	public EasyCrudWireTap<TId, TDto> getWireTap() {
		return wireTap;
	}

	public void setWireTap(EasyCrudWireTap<TId, TDto> wireTap) {
		this.wireTap = wireTap;
	}

}
