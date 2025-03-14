package org.summerb.easycrud.rest.permissions;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import org.summerb.easycrud.api.row.HasId;
import org.summerb.easycrud.impl.auth.EasyCrudAuthorizationPerTableStrategy;
import org.summerb.easycrud.rest.commonpathvars.PathVariablesMap;
import org.summerb.easycrud.rest.dto.MultipleItemsResult;
import org.summerb.easycrud.rest.dto.SingleItemResult;

/**
 * Impl of {@link PermissionsResolverStrategy} that gets data from {@link
 * EasyCrudAuthorizationPerTableStrategy}. Therefore, it provides only table-wide permissions and
 * Row permissions are not filled
 *
 * @author Sergey Karpushin
 * @param <TId> type of row id
 * @param <TRow> type of row
 */
public class PermissionsResolverStrategyPerTable<TId, TRow extends HasId<TId>>
    implements PermissionsResolverStrategy<TId, HasId<TId>> {

  protected EasyCrudAuthorizationPerTableStrategy authStrategy;

  public PermissionsResolverStrategyPerTable(EasyCrudAuthorizationPerTableStrategy authStrategy) {
    Preconditions.checkArgument(authStrategy != null, "authStrategy required");
    this.authStrategy = authStrategy;
  }

  @Override
  public void resolvePermissions(
      MultipleItemsResult<TId, HasId<TId>> ret, PathVariablesMap contextVariables) {
    ret.setTablePermissions(buildTablePermissions());
  }

  @Override
  public void resolvePermissions(
      SingleItemResult<TId, HasId<TId>> ret, PathVariablesMap contextVariables) {

    ret.setPermissions(buildTablePermissions());
  }

  protected Map<String, Boolean> buildTablePermissions() {
    Map<String, Boolean> permissions = new HashMap<>();
    permissions.put(Permissions.CREATE, authStrategy.isAuthorizedToCreate());
    permissions.put(Permissions.READ, authStrategy.isAuthorizedToRead());
    permissions.put(Permissions.UPDATE, authStrategy.isAuthorizedToUpdate());
    permissions.put(Permissions.DELETE, authStrategy.isAuthorizedToDelete());
    return permissions;
  }
}
