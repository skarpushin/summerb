package org.summerb.easycrud.auth;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.rest.permissions.Permissions;
import org.summerb.easycrud.row.HasId;
import org.summerb.easycrud.wireTaps.EasyCrudWireTapAbstract;
import org.summerb.easycrud.wireTaps.EasyCrudWireTapMode;
import org.summerb.security.api.CurrentUserUuidResolver;
import org.summerb.security.api.exceptions.NotAuthorizedException;

public class EasyCrudAuthorizationPerRowWireTapAdapter<TRow extends HasId<?>>
    extends EasyCrudWireTapAbstract<TRow> {
  public static final String MULTIPLE_IDS = "multiple_ids";
  public static final String NEW_ROW = "new_row";

  protected final String entityMessageCode;
  protected final EasyCrudAuthorizationPerRow<TRow> delegate;
  protected final CurrentUserUuidResolver currentUserUuidResolver;
  protected int maxIdsCountToReportInException = 15;

  public EasyCrudAuthorizationPerRowWireTapAdapter(
      String entityMessageCode,
      CurrentUserUuidResolver currentUserUuidResolver,
      EasyCrudAuthorizationPerRow<TRow> delegate) {
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
    return true;
  }

  @Override
  public EasyCrudWireTapMode requiresOnUpdate() {
    return delegate.isCurrentVersionNeededForUpdatePermissionCheck()
        ? EasyCrudWireTapMode.FULL_DTO_AND_CURRENT_VERSION_NEEDED
        : EasyCrudWireTapMode.FULL_DTO_NEEDED;
  }

  @Override
  public boolean requiresOnDeleteMultiple() {
    return true;
  }

  @Override
  public EasyCrudWireTapMode requiresOnDelete() {
    return EasyCrudWireTapMode.FULL_DTO_NEEDED;
  }

  @Override
  public void beforeCreate(TRow row) {
    if (delegate.isAllowedToCreate(row)) {
      return;
    }
    String subjectTitle =
        entityMessageCode + ":" + (row.getId() == null ? NEW_ROW : String.valueOf(row.getId()));
    throw new NotAuthorizedException(
        currentUserUuidResolver.getUserUuid(), Permissions.CREATE, subjectTitle);
  }

  @Override
  public void afterRead(TRow row) {
    if (delegate.isAllowedToRead(List.of(row))) {
      return;
    }
    throw new NotAuthorizedException(
        currentUserUuidResolver.getUserUuid(), Permissions.READ, buildSubjectTitle(row));
  }

  @Override
  public void afterRead(List<TRow> rows) {
    if (delegate.isAllowedToRead(rows)) {
      return;
    }
    throw new NotAuthorizedException(
        currentUserUuidResolver.getUserUuid(),
        Permissions.READ,
        buildSubjectTitleForMultipleRows(rows));
  }

  @Override
  public void beforeUpdate(TRow from, TRow to) {
    if (delegate.isAllowedToUpdate(from, to)) {
      return;
    }
    String subjectTitle =
        entityMessageCode
            + ":"
            + (from != null ? String.valueOf(from.getId()) : String.valueOf(to.getId()));
    throw new NotAuthorizedException(
        currentUserUuidResolver.getUserUuid(), Permissions.UPDATE, subjectTitle);
  }

  @Override
  public void beforeDelete(TRow row) {
    if (delegate.isAllowedToDelete(List.of(row))) {
      return;
    }
    throw new NotAuthorizedException(
        currentUserUuidResolver.getUserUuid(), Permissions.DELETE, buildSubjectTitle(row));
  }

  @Override
  public void beforeDelete(List<TRow> rows) {
    if (delegate.isAllowedToDelete(rows)) {
      return;
    }
    throw new NotAuthorizedException(
        currentUserUuidResolver.getUserUuid(),
        Permissions.DELETE,
        buildSubjectTitleForMultipleRows(rows));
  }

  protected String idsCvs(List<TRow> rows) {
    return rows.stream().map(x -> String.valueOf(x.getId())).collect(Collectors.joining(","));
  }

  protected String buildSubjectTitleForMultipleRows(List<TRow> rows) {
    if (rows.size() > maxIdsCountToReportInException) {
      return entityMessageCode + ":" + MULTIPLE_IDS;
    } else {
      return entityMessageCode + ":" + idsCvs(rows);
    }
  }

  protected String buildSubjectTitle(TRow row) {
    return entityMessageCode + ":" + row.getId();
  }

  public int getMaxIdsCountToReportInException() {
    return maxIdsCountToReportInException;
  }

  public void setMaxIdsCountToReportInException(int maxIdsCountToReportInException) {
    this.maxIdsCountToReportInException = maxIdsCountToReportInException;
  }
}
