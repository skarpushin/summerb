package org.summerb.easycrud.api;

import java.util.List;
import org.summerb.easycrud.api.dto.OrderBy;
import org.summerb.easycrud.api.query.Query;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.utils.easycrud.api.dto.PagerParams;

/**
 * This interface is used to check read/create/update/delete permissions for each row. EasyCrud will
 * call (when configured) methods of such an interface to check for permissions. And if user is not
 * allowed, then {@link org.summerb.security.api.exceptions.NotAuthorizedException} would be thrown.
 *
 * <p>Unline {@link EasyCrudAuthorizationPerTable}, this interface is used to check permissions on
 * individual rows.
 *
 * @param <T> the type of the row
 * @since 8.1.0 -- currently piloting as proof-of-concept
 */
public interface EasyCrudAuthorizationPerRow<T> {
  /**
   * This is called before creation will take place.
   *
   * @param row that is about to be created
   * @return true if the current user is allowed to create the given row, false otherwise
   */
  boolean isAllowedToCreate(T row);

  /**
   * This is invoked after row(s) were read from DB and before returned to the consuming code.
   *
   * @param rows rows to check read permissions for. In some cases there will be just 1 row (in case
   *     you called {@link EasyCrudService#getById(Object)}, {@link
   *     EasyCrudService#findOneByQuery(Query)}, etc) but could also be called for multiple entries
   *     which were retrieved via {@link EasyCrudService#find(PagerParams, Query, OrderBy...)} or
   *     via scaffolded service method marked with annotation {@link
   *     org.summerb.easycrud.scaffold.api.Query} which returns List of service's rows
   * @return true if the current user is allowed to read a given row(s), false otherwise
   */
  boolean isAllowedToRead(List<T> rows);

  /**
   * This is called before the update operation will take place. There are 2 possible ways how this
   * method will be called, depending on the value that you return by {@link
   * #isCurrentVersionNeededForUpdatePermissionCheck()}:
   *
   * <ul>
   *   <li>If {@link #isCurrentVersionNeededForUpdatePermissionCheck()} returns <code>false</code>,
   *       then: <code>currentVersion
   *       </code> is null, <code>newVersion</code> will contain a row version that was passed to
   *       {@link EasyCrudService#update(HasId)} method
   *   <li>If {@link #isCurrentVersionNeededForUpdatePermissionCheck()} returns <code>true</code>,
   *       then: <code>currentVersion
   *       </code> of the row fetched by EasyCrud before calling this method, <code>newVersion
   *       </code> will contain a row version that was passed to {@link
   *       EasyCrudService#update(HasId)} method
   * </ul>
   *
   * @param currentVersion current version of the row loaded from DB, or <code>null</code> if {@link
   *     #isCurrentVersionNeededForUpdatePermissionCheck()} returned false
   * @param newVersion new version of the row that is about to be persisted in DB
   * @return true if the current user is allowed to update the given row, false otherwise.
   * @see #isCurrentVersionNeededForUpdatePermissionCheck()
   */
  boolean isAllowedToUpdate(T currentVersion, T newVersion);

  /**
   * Is the current version of the row needed for update operation permission check? This method
   * defines the way how {@link #isAllowedToUpdate(Object, Object)} method is invoked by EasyCrud.
   *
   * @return true if the current version of the row is needed to be loaded from DB and passed into
   *     {@link #isAllowedToUpdate(Object, Object)} method. If you do not need the current version
   *     for update operation permission check, return false here to optimize performance
   * @see #isAllowedToUpdate(Object, Object)
   */
  boolean isCurrentVersionNeededForUpdatePermissionCheck();

  /**
   * This is called before the delete operation will take place.
   *
   * @param rows the row(s) that about to be deleted. When you call {@link
   *     EasyCrudService#deleteByQuery(Query)} this method will receive a collection of objects
   *     matching the query, in other cases it will be just one row that is about to be deleted.
   * @return true if the current user is allowed to delete the given row(s), false otherwise.
   */
  boolean isAllowedToDelete(List<T> rows);
}
