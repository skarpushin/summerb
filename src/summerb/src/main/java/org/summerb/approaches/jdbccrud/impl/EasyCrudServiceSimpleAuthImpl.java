package org.summerb.approaches.jdbccrud.impl;

import java.io.NotSerializableException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.approaches.jdbccrud.api.EasyCrudDao;
import org.summerb.approaches.jdbccrud.api.EasyCrudExceptionStrategy;
import org.summerb.approaches.jdbccrud.api.EasyCrudService;
import org.summerb.approaches.jdbccrud.api.EasyCrudSimpleAuthStrategy;
import org.summerb.approaches.jdbccrud.api.EasyCrudValidationStrategy;
import org.summerb.approaches.jdbccrud.api.HasEasyCrudSimpleAuthStrategy;
import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
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
import org.summerb.approaches.security.api.CurrentUserResolver;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationUtils;
import org.summerb.utils.Clonnable;
import org.summerb.utils.DeepCopy;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

/**
 * 
 * @author sergey.karpushin
 *
 */
public class EasyCrudServiceSimpleAuthImpl<TId, TDto extends HasId<TId>>
		implements EasyCrudService<TId, TDto>, HasEasyCrudSimpleAuthStrategy, InitializingBean {
	public static final String OPTIMISTIC_LOCK_FAILED_TECH_MESSAGE = "Optimistic lock failed, record was already updated but someone else";

	private EasyCrudValidationStrategy<TDto> validationStrategy;
	protected EasyCrudSimpleAuthStrategy simpleAuthStrategy;
	protected EasyCrudExceptionStrategy<TId> exceptionStrategy;
	protected EventBus eventBus;

	protected EasyCrudDao<TId, TDto> dao;
	protected CurrentUserResolver currentUserResolver;
	private Class<TDto> dtoClass;
	private String entityTypeMessageCode;

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkState(dtoClass != null, "DtoClass is required");

		if (entityTypeMessageCode == null) {
			entityTypeMessageCode = dtoClass.getCanonicalName();
		}

		if (simpleAuthStrategy == null) {
			simpleAuthStrategy = new EasyCrudSimpleAuthStrategyNoOpImpl();
		}
		if (!(simpleAuthStrategy instanceof EasyCrudSimpleAuthStrategyNoOpImpl)
				|| HasAuthor.class.isAssignableFrom(dtoClass)) {
			Preconditions.checkState(currentUserResolver != null,
					"CurrentUserResolver required for DTOs marked as HasAuthor");
		}

		if (validationStrategy == null) {
			validationStrategy = new EasyCrudValidationStrategyNoOpImpl<TDto>();
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

			validationStrategy.validateForCreate(dto);
			simpleAuthStrategy.assertAuthorizedToCreate();

			TDto ret = copyDto(dto);

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

			publishEventForCreate(ret);
			return ret;
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtCreate(t);
		}
	}

	private void publishEventForCreate(TDto ret) {
		if (eventBus == null) {
			return;
		}
		eventBus.post(EntityChangedEvent.added(ret));
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
			validationStrategy.validateForUpdate(currentVersion, newVersion);
			simpleAuthStrategy.assertAuthorizedToUpdate();

			TDto ret = copyDto(newVersion);

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

			publishEventForUpdated(ret);
			return ret;
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtUpdate(t);
		}
	}

	private void publishEventForUpdated(TDto ret) {
		if (eventBus == null) {
			return;
		}
		eventBus.post(EntityChangedEvent.updated(ret));
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteById(TId id) throws NotAuthorizedException, EntityNotFoundException {
		try {
			Preconditions.checkArgument(id != null);
			simpleAuthStrategy.assertAuthorizedToDelete();

			TDto existing = null;
			if (eventBus != null) {
				existing = findById(id);
				Preconditions.checkState(existing != null, "Didn't find delition subject: " + id);
			}

			int affected = dao.delete(id);
			if (affected != 1) {
				throw exceptionStrategy.buildNotFoundException(getEntityTypeMessageCode(), id);
			}

			publishEventForDeleted(existing);
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtDelete(t);
		}
	}

	protected void publishEventForDeleted(TDto ret) {
		if (eventBus == null) {
			return;
		}
		eventBus.post(EntityChangedEvent.removedObject(ret));
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void deleteByIdOptimistic(TId id, long modifiedAt) throws NotAuthorizedException, EntityNotFoundException {
		try {
			Preconditions.checkArgument(id != null);
			Preconditions.checkState(HasTimestamps.class.isAssignableFrom(dtoClass),
					"Delete using optimistic lock is not allowed for DTO which doesn't support HasTimestamps");
			simpleAuthStrategy.assertAuthorizedToDelete();

			TDto existing = null;
			if (eventBus != null) {
				existing = findById(id);
				Preconditions.checkState(existing != null, "Didn't find delition subject: " + id);
			}

			int affected = dao.delete(id, modifiedAt);
			if (affected != 1) {
				throw exceptionStrategy.buildOptimisticLockException();
			}

			publishEventForDeleted(existing);
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtDelete(t);
		}
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public int deleteByQuery(Query query) throws NotAuthorizedException {
		try {
			Preconditions.checkArgument(query != null);
			simpleAuthStrategy.assertAuthorizedToDelete();

			if (eventBus == null) {
				return dao.deleteByQuery(query);
			}

			PaginatedList<TDto> toDelete = query(PagerParams.ALL, query);
			List<TDto> deleted = new ArrayList<TDto>();
			for (TDto dto : toDelete.getItems()) {
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
				publishEventForDeleted(dto);
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
			simpleAuthStrategy.assertAuthorizedToRead();
			return dao.findById(id);
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtFind(t);
		}
	}

	@Override
	public TDto findOneByQuery(Query query) throws NotAuthorizedException {
		try {
			Preconditions.checkArgument(query != null);
			simpleAuthStrategy.assertAuthorizedToRead();
			return dao.findOneByQuery(query);
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtFind(t);
		}
	}

	@Override
	public PaginatedList<TDto> query(PagerParams pagerParams, Query optionalQuery, OrderBy... orderBy)
			throws NotAuthorizedException {
		try {
			Preconditions.checkArgument(pagerParams != null, "PagerParams is a must");
			simpleAuthStrategy.assertAuthorizedToRead();
			return dao.query(pagerParams, optionalQuery, orderBy);
		} catch (Throwable t) {
			throw exceptionStrategy.handleExceptionAtFind(t);
		}
	}

	@Override
	public EasyCrudSimpleAuthStrategy getSimpleAuthStrategy() {
		return simpleAuthStrategy;
	}

	public void setSimpleAuthStrategy(EasyCrudSimpleAuthStrategy genericAuthorizationStrategy) {
		this.simpleAuthStrategy = genericAuthorizationStrategy;
	}

	public EasyCrudDao<TId, TDto> getDao() {
		return dao;
	}

	@Required
	public void setDao(EasyCrudDao<TId, TDto> dao) {
		this.dao = dao;
	}

	public CurrentUserResolver getCurrentUserResolver() {
		return currentUserResolver;
	}

	/**
	 * NOTE: Not required of Dto don't have HasAuthor
	 */
	public void setCurrentUserResolver(CurrentUserResolver currentUserResolver) {
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

	public EasyCrudValidationStrategy<TDto> getValidationStrategy() {
		return validationStrategy;
	}

	public void setValidationStrategy(EasyCrudValidationStrategy<TDto> validationStrategy) {
		this.validationStrategy = validationStrategy;
	}

	@Override
	public String getEntityTypeMessageCode() {
		return entityTypeMessageCode;
	}

	public void setEntityTypeMessageCode(String entityTypeMessageCode) {
		this.entityTypeMessageCode = entityTypeMessageCode;
	}

	public EventBus getEventBus() {
		return eventBus;
	}

	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

}
