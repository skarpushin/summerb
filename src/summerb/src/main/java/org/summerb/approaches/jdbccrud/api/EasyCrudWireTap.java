package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

/**
 * This interface defines "wire tap" for all common CRUD service methods
 * 
 * @author sergeyk
 *
 * @param <TId>
 * @param <TDto>
 */
public interface EasyCrudWireTap<TId, TDto extends HasId<TId>> {
	/**
	 * @return false if all methods will work OK with just ID field. True if all
	 *         dto's must be fully filled
	 */
	boolean requiresFullDto();

	boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException;

	void beforeCreate(TDto dto) throws NotAuthorizedException, FieldValidationException;

	void afterCreate(TDto dto) throws FieldValidationException, NotAuthorizedException;

	boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException;

	void beforeUpdate(TDto from, TDto to) throws FieldValidationException, NotAuthorizedException;

	void afterUpdate(TDto from, TDto to) throws NotAuthorizedException, FieldValidationException;

	boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException;

	void beforeDelete(TDto dto) throws NotAuthorizedException, FieldValidationException;

	void afterDelete(TDto dto) throws FieldValidationException, NotAuthorizedException;

	boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException;

	void afterRead(TDto dto) throws FieldValidationException, NotAuthorizedException;
}
