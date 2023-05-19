package org.summerb.easycrud_swagger.swagger;

import springfox.documentation.service.VendorExtension;

public class TrimToRoles implements VendorExtension<String[]> {
  public static final String NAME = "TrimToRoles";

  private String[] roles;

  public TrimToRoles(String[] roles) {
    this.roles = roles;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String[] getValue() {
    return roles;
  }
}
