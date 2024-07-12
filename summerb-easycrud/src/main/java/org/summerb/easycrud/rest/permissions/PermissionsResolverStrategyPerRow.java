package org.summerb.easycrud.rest.permissions;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.auth.EascyCrudAuthorizationPerRowStrategy;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.easycrud.rest.dto.MultipleItemsResult;
import org.summerb.easycrud.rest.dto.SingleItemResult;

import com.google.common.base.Preconditions;

/**
 * Impl of {@link PermissionsResolverStrategy} that gets data from {@link
 * EascyCrudAuthorizationPerRowStrategy}, it means that provide per row permissions information
 *
 * @author Sergey Karpushin
 * @param <TId> type of row id
 * @param <TRow> type of row
 */
public class PermissionsResolverStrategyPerRow<TId, TRow extends HasId<TId>>
    implements PermissionsResolverStrategy<TId, HasId<TId>> {

  protected EascyCrudAuthorizationPerRowStrategy<TRow> perRowAuthStrategy;

  public PermissionsResolverStrategyPerRow(
      EascyCrudAuthorizationPerRowStrategy<TRow> perRowAuthStrategy) {
    Preconditions.checkArgument(perRowAuthStrategy != null, "perRowAuthStrategy required");
    this.perRowAuthStrategy = perRowAuthStrategy;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void resolvePermissions(
      MultipleItemsResult<TId, HasId<TId>> ret, PathVariablesMap contextVariables) {

    ret.setRowPermissions(
        ret.getRows().stream()
            .collect(Collectors.toMap(k -> k.getId(), v -> buildRowPermissions((TRow) v))));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void resolvePermissions(
      SingleItemResult<TId, HasId<TId>> ret, PathVariablesMap contextVariables) {

    ret.setPermissions(buildRowPermissions((TRow) ret.getRow()));
  }

  protected Map<String, Boolean> buildRowPermissions(TRow row) {
    Map<String, Boolean> permissions = new HashMap<>();
    permissions.put(Permissions.CREATE, perRowAuthStrategy.isAuthorizedToCreate(row));
    permissions.put(Permissions.READ, perRowAuthStrategy.isAuthorizedToRead(row));
    permissions.put(Permissions.UPDATE, perRowAuthStrategy.isAuthorizedToUpdate(null, row));
    permissions.put(Permissions.DELETE, perRowAuthStrategy.isAuthorizedToDelete(row));
    return permissions;
  }
}
