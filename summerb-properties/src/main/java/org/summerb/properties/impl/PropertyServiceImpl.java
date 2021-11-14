/*******************************************************************************
 * Copyright 2015-2021 Sergey Karpushin
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

import java.sql.DataTruncation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.summerb.easycrud.common.DaoExceptionUtils;
import org.summerb.easycrud.common.ServiceDataTruncationException;
import org.summerb.properties.api.PropertyService;
import org.summerb.properties.api.dto.NamedProperty;
import org.summerb.properties.api.exceptions.PropertyServiceUnexpectedException;
import org.summerb.properties.impl.dao.PropertyDao;
import org.summerb.properties.impl.dto.NamedIdProperty;
import org.summerb.properties.internal.StringIdAliasService;
import org.summerb.utils.exceptions.ExceptionUtils;

import com.google.common.base.Preconditions;

public class PropertyServiceImpl implements PropertyService {
	// private Logger log = LogManager.getLogger(PropertyServiceImpl.class);

	private StringIdAliasService appNameAlias;
	private StringIdAliasService domainNameAlias;
	private StringIdAliasService propertyNameAlias;

	private PropertyDao propertyDao;

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public void putSubjectProperty(String appName, String domainName, String subjectId, String name, String value) {
		checkArgumentsHaveText(appName, domainName, subjectId, name);

		try {
			long appId = appNameAlias.getAliasFor(appName);
			long domainId = domainNameAlias.getAliasFor(domainName);
			long propertyNameId = propertyNameAlias.getAliasFor(name);

			propertyDao.putProperty(appId, domainId, subjectId, propertyNameId, value);
		} catch (Throwable t) {
			propagatePropertyNameIfTruncationError(t, name);

			throw new PropertyServiceUnexpectedException("Failed to put property", t);
		}
	}

	/**
	 * Will detect truncation error and substitute value field name with property
	 * name
	 * 
	 * @param t
	 * @param propertyName
	 */
	private void propagatePropertyNameIfTruncationError(Throwable t, String propertyName) {
		String fieldName = DaoExceptionUtils.findTruncatedFieldNameIfAny(t);
		if (!PropertyDao.VALUE_FIELD_NAME.equals(fieldName)) {
			return;
		}

		DataTruncation exc = ExceptionUtils.findExceptionOfType(t, DataTruncation.class);
		if (exc == null) {
			return;
		}

		throw ServiceDataTruncationException.envelopeFor(propertyName, t);
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void putSubjectProperties(String appName, String domainName, String subjectId,
			List<NamedProperty> namedProperties) {
		if (CollectionUtils.isEmpty(namedProperties)) {
			return;
		}
		checkArgumentsHaveText(appName, domainName, subjectId);

		String currentPropertyName = null;
		try {
			long appId = appNameAlias.getAliasFor(appName);
			long domainId = domainNameAlias.getAliasFor(domainName);

			for (NamedProperty namedProperty : namedProperties) {
				currentPropertyName = namedProperty.getName();
				long propertyNameId = propertyNameAlias.getAliasFor(currentPropertyName);
				String value = namedProperty.getPropertyValue();
				propertyDao.putProperty(appId, domainId, subjectId, propertyNameId, value);
			}
		} catch (Throwable t) {
			propagatePropertyNameIfTruncationError(t, currentPropertyName);

			throw new PropertyServiceUnexpectedException("Failed to put subject properties", t);
		}
	}

	@Transactional(rollbackFor = Throwable.class)
	@Override
	public void putSubjectsProperty(String appName, String domainName, List<String> subjectsIds, String name,
			String value) {
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
			propagatePropertyNameIfTruncationError(t, name);

			throw new PropertyServiceUnexpectedException("Failed to put subjects property", t);
		}
	}

	@Override
	public String findSubjectProperty(String appName, String domainName, String subjectId, String name) {
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
	public Map<String, String> findSubjectProperties(String appName, String domainName, String subjectId) {
		checkArgumentsHaveText(appName, domainName, subjectId);

		try {
			long appId = appNameAlias.getAliasFor(appName);
			long domainId = domainNameAlias.getAliasFor(domainName);

			return internalFindSubjectProperties(appId, domainId, subjectId);
		} catch (Throwable t) {
			throw new PropertyServiceUnexpectedException("Failed to find subject properties", t);
		}
	}

	protected Map<String, String> internalFindSubjectProperties(long appId, long domainId, String subjectId) {
		List<NamedIdProperty> foundProperties = propertyDao.findSubjectProperties(appId, domainId, subjectId);

		Map<String, String> ret = new HashMap<String, String>();
		for (NamedIdProperty namedIdProperty : foundProperties) {
			long nameId = namedIdProperty.getNameId();
			String name = propertyNameAlias.getNameByAlias(nameId);
			ret.put(name, namedIdProperty.getPropertyValue());
		}
		return ret;
	}

	@Override
	public Map<String, Map<String, String>> findSubjectsProperties(String appName, String domainName,
			List<String> subjectsIds) {
		if (CollectionUtils.isEmpty(subjectsIds)) {
			return new HashMap<String, Map<String, String>>();
		}
		checkArgumentsHaveText(appName, domainName);

		try {
			long appId = appNameAlias.getAliasFor(appName);
			long domainId = domainNameAlias.getAliasFor(domainName);
			Map<String, Map<String, String>> ret = new HashMap<String, Map<String, String>>();

			for (String subjectId : subjectsIds) {
				Map<String, String> properties = internalFindSubjectProperties(appId, domainId, subjectId);
				if (properties != null && properties.size() > 0) {
					ret.put(subjectId, properties);
				}
			}

			return ret;
		} catch (Throwable t) {
			throw new PropertyServiceUnexpectedException("Failed to find subjects properties", t);
		}
	}

	@Override
	public Map<String, String> findSubjectsProperty(String appName, String domainName, List<String> subjectsIds,
			String name) {
		if (CollectionUtils.isEmpty(subjectsIds)) {
			return new HashMap<String, String>();
		}
		checkArgumentsHaveText(appName, domainName, name);

		try {
			long appId = appNameAlias.getAliasFor(appName);
			long domainId = domainNameAlias.getAliasFor(domainName);
			long propertyNameId = propertyNameAlias.getAliasFor(name);

			Map<String, String> ret = new HashMap<String, String>();
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
	public void deleteSubjectsProperties(String appName, String domainName, List<String> subjectsIds) {
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
	private void checkArgumentsHaveText(String... strings) {
		for (String str : strings) {
			Preconditions.checkArgument(StringUtils.hasText(str));
		}
	}

	public PropertyDao getPropertyDao() {
		return propertyDao;
	}

	public void setPropertyDao(PropertyDao propertyDao) {
		this.propertyDao = propertyDao;
	}

	public StringIdAliasService getAppNameAlias() {
		return appNameAlias;
	}

	public void setAppNameAlias(StringIdAliasService appNameAlias) {
		this.appNameAlias = appNameAlias;
	}

	public StringIdAliasService getDomainNameAlias() {
		return domainNameAlias;
	}

	public void setDomainNameAlias(StringIdAliasService domainNameAlias) {
		this.domainNameAlias = domainNameAlias;
	}

	public StringIdAliasService getPropertyNameAlias() {
		return propertyNameAlias;
	}

	public void setPropertyNameAlias(StringIdAliasService propertyNameAlias) {
		this.propertyNameAlias = propertyNameAlias;
	}
}
