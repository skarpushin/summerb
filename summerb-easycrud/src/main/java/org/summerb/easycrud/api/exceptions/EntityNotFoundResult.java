package org.summerb.easycrud.api.exceptions;

import org.summerb.utils.DtoBase;

/**
 * This result describes entity missing case. NOTE: identity is type of String intentionally for 2
 * reasons: a) because sometimes identity is a String (as opposed to long) and b) to prevent
 * non-primitives types from ending up in this DTO
 */
public class EntityNotFoundResult implements DtoBase {
  protected String subjectTypeMessageCode;
  protected String identity;

  public EntityNotFoundResult() {}

  public EntityNotFoundResult(String subjectTypeMessageCode, String identity) {
    this.identity = identity;
    this.subjectTypeMessageCode = subjectTypeMessageCode;
  }

  public String getIdentity() {
    return identity;
  }

  public void setIdentity(String identity) {
    this.identity = identity;
  }

  public String getSubjectTypeMessageCode() {
    return subjectTypeMessageCode;
  }

  public void setSubjectTypeMessageCode(String subjectTypeMessageCode) {
    this.subjectTypeMessageCode = subjectTypeMessageCode;
  }
}
