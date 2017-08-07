package org.summerb.approaches.jdbccrud.api;

import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;

/**
 * This interface defines "wire tap" for all common CRUD service methods. It
 * helps to follow OCP:OOD principle.
 * 
 * It's inconvenient to impl interface, consider extending
 * {@link EasyCrudWireTapNoOpImpl} class
 * 
 * @author sergeyk
 */
public interface EasyCrudWireTap<TId, TDto extends HasId<TId>> {
	/**
	 * @return False if all methods will work OK with just ID field. True if all
	 *         dto's must be fully filled befor invoking method of this
	 *         interface. In case False is returned then {@link EasyCrudService}
	 *         will perform batch operations when applicable instead of
	 *         iterating elements one-by-one
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
