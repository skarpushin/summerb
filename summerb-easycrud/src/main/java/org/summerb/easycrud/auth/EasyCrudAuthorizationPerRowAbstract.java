package org.summerb.easycrud.auth;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.spring.security.api.CurrentUserRolesResolver;

/**
 * Abstract base class for row-level authorization.
 *
 * @param <TRow> type of row
 */
public abstract class EasyCrudAuthorizationPerRowAbstract<TRow>
    implements EasyCrudAuthorizationPerRow<TRow> {

  /** Current user UUID resolver */
  @Autowired protected CurrentUserUuidResolver currentUserUuidResolver;

  /** Current user roles resolver */
  @Autowired protected CurrentUserRolesResolver currentUserRolesResolver;

  @Override
  public boolean isAllowedToCreate(TRow row) {
    return isAllowedToModify(row);
  }

  /**
   * Check if modification is allowed.
   *
   * @param row row to check
   * @return true if modification is allowed
   */
  protected abstract boolean isAllowedToModify(TRow row);

  @Override
  public boolean isAllowedToUpdate(TRow currentVersion, TRow newVersion) {
    return isAllowedToModify(newVersion);
  }

  @Override
  public boolean isCurrentVersionNeededForUpdatePermissionCheck() {
    return false;
  }

  @Override
  public boolean isAllowedToDelete(List<TRow> rows) {
    return rows.stream().allMatch(this::isAllowedToModify);
  }
}
