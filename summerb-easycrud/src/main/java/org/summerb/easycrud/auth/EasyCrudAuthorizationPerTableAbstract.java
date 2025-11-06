package org.summerb.easycrud.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.spring.security.api.CurrentUserRolesResolver;

public abstract class EasyCrudAuthorizationPerTableAbstract
    implements EasyCrudAuthorizationPerTable {

  @Autowired protected CurrentUserUuidResolver currentUserUuidResolver;
  @Autowired protected CurrentUserRolesResolver currentUserRolesResolver;

  @Override
  public boolean isAllowedToCreate() {
    return isAllowedToModify();
  }

  @Override
  public boolean isAllowedToUpdate() {
    return isAllowedToModify();
  }

  @Override
  public boolean isAllowedToDelete() {
    return isAllowedToModify();
  }

  protected abstract boolean isAllowedToModify();
}
