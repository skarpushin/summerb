package org.summerb.easycrud_swagger.swagger;

import io.swagger.models.Path;
import io.swagger.models.Swagger;
import io.swagger.models.Tag;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.CollectionUtils;
import org.summerb.spring.security.api.SecurityContextResolver;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.Documentation;
import springfox.documentation.service.Operation;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.swagger2.mappers.ServiceModelToSwagger2MapperImpl;

/**
 * This extension will trim operations returned by Swagger based on roles they are permitted for to
 * match current user Roles. This works based on data populated by {@link
 * OperationBuilderPluginSecuredAware}
 *
 * @author sergeyk
 */
public class ServiceModelToSwagger2MapperImplEx<TUser extends User>
    extends ServiceModelToSwagger2MapperImpl {
  @Autowired private SecurityContextResolver<TUser> securityContextResolver;

  @Override
  protected io.swagger.models.Operation mapOperation(Operation from) {
    if (from == null) {
      return null;
    }
    if (!isPermittedForCurrentUser(findTrimToRolesExtension(from.getVendorExtensions()))) {
      return null;
    }
    return super.mapOperation(from);
  }

  private boolean isPermittedForCurrentUser(TrimToRoles trimToRoles) {
    if (trimToRoles == null) {
      return true;
    }
    if (securityContextResolver.hasAnyRole(trimToRoles.getValue())) {
      return true;
    }
    return false;
  }

  private TrimToRoles findTrimToRolesExtension(
      @SuppressWarnings("rawtypes") List<VendorExtension> list) {
    if (CollectionUtils.isEmpty(list)) {
      return null;
    }
    return list.stream()
        .filter(x -> x instanceof TrimToRoles)
        .map(TrimToRoles.class::cast)
        .findFirst()
        .orElse(null);
  }

  /** This extension will prevent empty controller from appearing in the list (step 1) */
  @Override
  protected Map<String, Path> mapApiListings(Map<String, List<ApiListing>> apiListings) {
    Map<String, Path> paths = super.mapApiListings(apiListings);
    return paths.entrySet().stream()
        .filter(x -> !x.getValue().isEmpty())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /** This extension will prevent empty controller from appearing in the list (step 2) */
  @Override
  public Swagger mapDocumentation(Documentation from) {
    Swagger ret = super.mapDocumentation(from);
    Predicate<? super Tag> hasAtLeastOneOperation =
        tag ->
            ret.getPaths().values().stream()
                .anyMatch(
                    x ->
                        x.getOperations().stream()
                            .anyMatch(y -> y.getTags().contains(tag.getName())));
    ret.setTags(ret.getTags().stream().filter(hasAtLeastOneOperation).collect(Collectors.toList()));
    return ret;
  }
}
