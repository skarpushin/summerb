package org.summerb.easycrud.rest.permissions;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.auth.EasyCrudAuthorizationPerRowStrategy;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.easycrud.rest.dto.MultipleItemsResult;
import org.summerb.easycrud.rest.dto.SingleItemResult;

/**
 * Impl of {@link PermissionsResolverStrategy} that gets data from {@link
 * EasyCrudAuthorizationPerRowStrategy}, it means that provide per row permissions information
 *
 * @author Sergey Karpushin
 * @param <TId> type of row id
 * @param <TRow> type of row
 */
public class PermissionsResolverStrategyPerRow<TId, TRow extends HasId<TId>>
    implements PermissionsResolverStrategy<TId, TRow> {

  protected final EasyCrudAuthorizationPerRowStrategy<TRow> perRowAuthStrategy;

  public PermissionsResolverStrategyPerRow(
      EasyCrudAuthorizationPerRowStrategy<TRow> perRowAuthStrategy) {
    Preconditions.checkArgument(perRowAuthStrategy != null, "perRowAuthStrategy required");
    this.perRowAuthStrategy = perRowAuthStrategy;
  }

  @Override
  public void resolvePermissions(
      MultipleItemsResult<TId, TRow> ret, PathVariablesMap contextVariables) {

    ret.setRowPermissions(
        ret.getRows().stream().collect(Collectors.toMap(HasId::getId, this::buildAllPermissions)));
  }

  @Override
  public void resolvePermissions(
      SingleItemResult<TId, TRow> ret, PathVariablesMap contextVariables) {

    ret.setPermissions(buildAllPermissions(ret.getRow()));
  }

  public Map<String, Boolean> buildAllPermissions(TRow row) {
    Map<String, Boolean> permissions = new HashMap<>();
    permissions.put(Permissions.CREATE, perRowAuthStrategy.isAuthorizedToCreate(row));
    permissions.put(Permissions.READ, perRowAuthStrategy.isAuthorizedToRead(row));
    permissions.put(Permissions.UPDATE, perRowAuthStrategy.isAuthorizedToUpdate(null, row));
    permissions.put(Permissions.DELETE, perRowAuthStrategy.isAuthorizedToDelete(row));
    return permissions;
  }

  public Map<String, Boolean> buildExistingRowPermissions(TRow row) {
    Map<String, Boolean> permissions = new HashMap<>();
    permissions.put(Permissions.UPDATE, perRowAuthStrategy.isAuthorizedToUpdate(null, row));
    permissions.put(Permissions.DELETE, perRowAuthStrategy.isAuthorizedToDelete(row));
    return permissions;
  }
}
