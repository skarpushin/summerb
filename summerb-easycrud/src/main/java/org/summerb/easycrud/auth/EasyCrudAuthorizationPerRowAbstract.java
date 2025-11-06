package org.summerb.easycrud.auth;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.spring.security.api.CurrentUserRolesResolver;

public abstract class EasyCrudAuthorizationPerRowAbstract<TRow>
    implements EasyCrudAuthorizationPerRow<TRow> {

  @Autowired protected CurrentUserUuidResolver currentUserUuidResolver;
  @Autowired protected CurrentUserRolesResolver currentUserRolesResolver;

  @Override
  public boolean isAllowedToCreate(TRow row) {
    return isAllowedToModify(row);
  }

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
