/*******************************************************************************
 * Copyright 2015-2024 Sergey Karpushin
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.summerb.properties.impl;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.api.exceptions.ServiceDataTruncationException;
import org.summerb.properties.api.PropertyService;
import org.summerb.properties.api.dto.NamedProperty;
import org.summerb.properties.api.exceptions.PropertyServiceUnexpectedException;
import org.summerb.properties.impl.dao.PropertyDao;
import org.summerb.properties.impl.dto.NamedIdProperty;
import org.summerb.properties.internal.StringIdAliasService;
import org.summerb.utils.exceptions.ExceptionUtils;

public class PropertyServiceImpl implements PropertyService {
  // protected Logger log = LoggerFactory.getLogger(PropertyServiceImpl.class);

  protected PropertyDao propertyDao;
  protected StringIdAliasService appNameAlias;
  protected StringIdAliasService domainNameAlias;
  protected StringIdAliasService propertyNameAlias;

  public PropertyServiceImpl(
      PropertyDao propertyDao,
      StringIdAliasService domainNameAlias,
      StringIdAliasService appNameAlias,
      StringIdAliasService propertyNameAlias) {
    super();
    Preconditions.checkArgument(propertyDao != null, "propertyDao required");
    Preconditions.checkArgument(domainNameAlias != null, "domainNameAlias required");
    Preconditions.checkArgument(appNameAlias != null, "appNameAlias required");
    Preconditions.checkArgument(propertyNameAlias != null, "propertyNameAlias required");

    this.propertyDao = propertyDao;
    this.domainNameAlias = domainNameAlias;
    this.appNameAlias = appNameAlias;
    this.propertyNameAlias = propertyNameAlias;
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void putSubjectProperty(
      String appName, String domainName, String subjectId, String name, String value) {
    checkArgumentsHaveText(appName, domainName, subjectId, name);

    try {
      long appId = appNameAlias.getAliasFor(appName);
      long domainId = domainNameAlias.getAliasFor(domainName);
      long propertyNameId = propertyNameAlias.getAliasFor(name);

      propertyDao.putProperty(appId, domainId, subjectId, propertyNameId, value);
    } catch (Throwable t) {
      translateServiceDataTruncationExceptionIfAny(name, t);
      throw new PropertyServiceUnexpectedException("Failed to put property", t);
    }
  }

  protected void translateServiceDataTruncationExceptionIfAny(String name, Throwable t) {
    ServiceDataTruncationException exc =
        ExceptionUtils.findExceptionOfType(t, ServiceDataTruncationException.class);
    if (exc != null && "value".equals(exc.getFieldTokenBeingTruncated())) {
      throw new PropertyServiceUnexpectedException(
          "data truncation", new ServiceDataTruncationException(name, exc));
    }
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void putSubjectProperties(
      String appName, String domainName, String subjectId, List<NamedProperty> namedProperties) {
    if (CollectionUtils.isEmpty(namedProperties)) {
      return;
    }
    checkArgumentsHaveText(appName, domainName, subjectId);

    String currentPropertyName;
    try {
      long appId = appNameAlias.getAliasFor(appName);
      long domainId = domainNameAlias.getAliasFor(domainName);

      for (NamedProperty namedProperty : namedProperties) {
        try {
          currentPropertyName = namedProperty.getName();
          long propertyNameId = propertyNameAlias.getAliasFor(currentPropertyName);
          String value = namedProperty.getPropertyValue();
          propertyDao.putProperty(appId, domainId, subjectId, propertyNameId, value);
        } catch (Exception e) {
          translateServiceDataTruncationExceptionIfAny(namedProperty.getName(), e);
          throw e;
        }
      }
    } catch (Throwable t) {
      throw new PropertyServiceUnexpectedException("Failed to put subject properties", t);
    }
  }

  @Transactional(rollbackFor = Throwable.class)
  @Override
  public void putSubjectsProperty(
      String appName, String domainName, List<String> subjectsIds, String name, String value) {
    if (CollectionUtils.isEmpty(subjectsIds)) {
      return;
    }
    checkArgumentsHaveText(appName, domainName, name);

    try {
      long appId = appNameAlias.getAliasFor(appName);
      long domainId = domainNameAlias.getAliasFor(domainName);
      long propertyNameId = propertyNameAlias.getAliasFor(name);

      for (String subjectId : subjectsIds) {
        propertyDao.putProperty(appId, domainId, subjectId, propertyNameId, value);
      }
    } catch (Throwable t) {
      translateServiceDataTruncationExceptionIfAny(name, t);
      throw new PropertyServiceUnexpectedException("Failed to put subjects property", t);
    }
  }

  @Override
  public String findSubjectProperty(
      String appName, String domainName, String subjectId, String name) {
    checkArgumentsHaveText(appName, domainName, subjectId, name);

    try {
      long appId = appNameAlias.getAliasFor(appName);
      long domainId = domainNameAlias.getAliasFor(domainName);
      long propertyNameId = propertyNameAlias.getAliasFor(name);

      return propertyDao.findSubjectProperty(appId, domainId, subjectId, propertyNameId);
    } catch (Throwable t) {
      throw new PropertyServiceUnexpectedException("Failed to find subject property", t);
    }
  }

  @Override
  public Map<String, String> findSubjectProperties(
      String appName, String domainName, String subjectId) {
    checkArgumentsHaveText(appName, domainName, subjectId);

    try {
      long appId = appNameAlias.getAliasFor(appName);
      long domainId = domainNameAlias.getAliasFor(domainName);

      return internalFindSubjectProperties(appId, domainId, subjectId);
    } catch (Throwable t) {
      throw new PropertyServiceUnexpectedException("Failed to find subject properties", t);
    }
  }

  protected Map<String, String> internalFindSubjectProperties(
      long appId, long domainId, String subjectId) {
    List<NamedIdProperty> foundProperties =
        propertyDao.findSubjectProperties(appId, domainId, subjectId);

    Map<String, String> ret = new HashMap<>();
    for (NamedIdProperty namedIdProperty : foundProperties) {
      long nameId = namedIdProperty.getNameId();
      String name = propertyNameAlias.getNameByAlias(nameId);
      ret.put(name, namedIdProperty.getPropertyValue());
    }
    return ret;
  }

  @Override
  public Map<String, Map<String, String>> findSubjectsProperties(
      String appName, String domainName, List<String> subjectsIds) {
    if (CollectionUtils.isEmpty(subjectsIds)) {
      return new HashMap<>();
    }
    checkArgumentsHaveText(appName, domainName);

    try {
      long appId = appNameAlias.getAliasFor(appName);
      long domainId = domainNameAlias.getAliasFor(domainName);
      Map<String, Map<String, String>> ret = new HashMap<>();

      for (String subjectId : subjectsIds) {
        Map<String, String> properties = internalFindSubjectProperties(appId, domainId, subjectId);
        if (properties != null && !properties.isEmpty()) {
          ret.put(subjectId, properties);
        }
      }

      return ret;
    } catch (Throwable t) {
      throw new PropertyServiceUnexpectedException("Failed to find subjects properties", t);
    }
  }

  @Override
  public Map<String, String> findSubjectsProperty(
      String appName, String domainName, List<String> subjectsIds, String name) {
    if (CollectionUtils.isEmpty(subjectsIds)) {
      return new HashMap<>();
    }
    checkArgumentsHaveText(appName, domainName, name);

    try {
      long appId = appNameAlias.getAliasFor(appName);
      long domainId = domainNameAlias.getAliasFor(domainName);
      long propertyNameId = propertyNameAlias.getAliasFor(name);

      Map<String, String> ret = new HashMap<>();
      for (String subjectId : subjectsIds) {
        String value = propertyDao.findSubjectProperty(appId, domainId, subjectId, propertyNameId);
        if (value != null) {
          ret.put(subjectId, value);
        }
      }

      return ret;
    } catch (Throwable t) {
      throw new PropertyServiceUnexpectedException("Failed to find subject property", t);
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void deleteSubjectProperties(String appName, String domainName, String subjectId) {
    checkArgumentsHaveText(appName, domainName, subjectId);
    try {
      long appId = appNameAlias.getAliasFor(appName);
      long domainId = domainNameAlias.getAliasFor(domainName);

      propertyDao.deleteSubjectProperties(appId, domainId, subjectId);
    } catch (Throwable t) {
      throw new PropertyServiceUnexpectedException("Failed to delete subject properties", t);
    }
  }

  @Override
  @Transactional(rollbackFor = Throwable.class)
  public void deleteSubjectsProperties(
      String appName, String domainName, List<String> subjectsIds) {
    if (CollectionUtils.isEmpty(subjectsIds)) {
      return;
    }
    checkArgumentsHaveText(appName, domainName);

    try {
      long appId = appNameAlias.getAliasFor(appName);
      long domainId = domainNameAlias.getAliasFor(domainName);

      for (String subjectId : subjectsIds) {
        propertyDao.deleteSubjectProperties(appId, domainId, subjectId);
      }
    } catch (Throwable t) {
      throw new PropertyServiceUnexpectedException("Failed to delete subject properties", t);
    }
  }

  /**
   * Utility method to check preconditions for all methods in this class
   *
   * @param strings
   */
  protected void checkArgumentsHaveText(String... strings) {
    for (String str : strings) {
      Preconditions.checkArgument(StringUtils.hasText(str));
    }
  }

  public PropertyDao getPropertyDao() {
    return propertyDao;
  }

  public StringIdAliasService getAppNameAlias() {
    return appNameAlias;
  }

  public StringIdAliasService getDomainNameAlias() {
    return domainNameAlias;
  }

  public StringIdAliasService getPropertyNameAlias() {
    return propertyNameAlias;
  }
}
