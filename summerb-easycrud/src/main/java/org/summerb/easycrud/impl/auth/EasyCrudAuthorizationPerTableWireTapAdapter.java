package org.summerb.easycrud.impl.auth;

import com.google.common.base.Preconditions;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.EasyCrudAuthorizationPerTable;
import org.summerb.easycrud.api.EasyCrudWireTapMode;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapAbstract;
import org.summerb.easycrud.rest.permissions.Permissions;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.security.api.exceptions.NotAuthorizedException;

public class EasyCrudAuthorizationPerTableWireTapAdapter extends EasyCrudWireTapAbstract<HasId<?>> {
  protected final String entityMessageCode;
  protected final EasyCrudAuthorizationPerTable delegate;

  protected final CurrentUserUuidResolver currentUserUuidResolver;

  public EasyCrudAuthorizationPerTableWireTapAdapter(
      String entityMessageCode,
      CurrentUserUuidResolver currentUserUuidResolver,
      EasyCrudAuthorizationPerTable delegate) {
    Preconditions.checkArgument(
        StringUtils.hasText(entityMessageCode), "entityMessageCode required");
    Preconditions.checkNotNull(currentUserUuidResolver, "currentUserUuidResolver required");
    Preconditions.checkNotNull(delegate, "delegate required");

    this.entityMessageCode = entityMessageCode;
    this.delegate = delegate;
    this.currentUserUuidResolver = currentUserUuidResolver;
  }

  @Override
  public boolean requiresOnCreate() {
    return true;
  }

  @Override
  public boolean requiresOnRead() {
    return true;
  }

  @Override
  public boolean requiresOnReadMultiple() {
    return false;
  }

  @Override
  public EasyCrudWireTapMode requiresOnUpdate() {
    return EasyCrudWireTapMode.ONLY_INVOKE_WIRETAP;
  }

  @Override
  public boolean requiresOnDeleteMultiple() {
    return false;
  }

  @Override
  public EasyCrudWireTapMode requiresOnDelete() {
    return EasyCrudWireTapMode.ONLY_INVOKE_WIRETAP;
  }

  @Override
  public void beforeCreate(HasId<?> row) {
    if (delegate.isAllowedToCreate()) {
      return;
    }
    throw new NotAuthorizedException(
        currentUserUuidResolver.getUserUuid(), Permissions.CREATE, entityMessageCode);
  }

  @Override
  public void beforeRead() {
    if (delegate.isAllowedToRead()) {
      return;
    }
    throw new NotAuthorizedException(
        currentUserUuidResolver.getUserUuid(), Permissions.READ, entityMessageCode);
  }

  @Override
  public void beforeUpdate(HasId<?> from, HasId<?> to) {
    if (delegate.isAllowedToUpdate()) {
      return;
    }
    throw new NotAuthorizedException(
        currentUserUuidResolver.getUserUuid(), Permissions.UPDATE, entityMessageCode);
  }

  @Override
  public void beforeDelete(HasId<?> row) {
    if (delegate.isAllowedToDelete()) {
      return;
    }
    throw new NotAuthorizedException(
        currentUserUuidResolver.getUserUuid(), Permissions.DELETE, entityMessageCode);
  }
}
