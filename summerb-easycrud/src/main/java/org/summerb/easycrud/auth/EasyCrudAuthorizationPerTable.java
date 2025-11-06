package org.summerb.easycrud.auth;

/**
 * This interface is used to check CRUD permissions on a table. EasyCrud will call (when configured)
 * methods of such an interface to check for permissions. And if user is not allowed, then {@link
 * org.summerb.security.api.exceptions.NotAuthorizedException} would be thrown.
 *
 * <p>Unline {@link EasyCrudAuthorizationPerRow}, this interface is used to check permissions on a
 * whole table, instead of individual roles.
 *
 * @author sergeyk
 */
public interface EasyCrudAuthorizationPerTable {
  boolean isAllowedToRead();

  boolean isAllowedToCreate();

  boolean isAllowedToUpdate();

  boolean isAllowedToDelete();
}
