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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.summerb.properties.api.exceptions.PropertyServiceUnexpectedException;
import org.summerb.properties.impl.dao.StringIdAliasDao;
import org.summerb.properties.internal.StringIdAliasService;

import com.google.common.base.Preconditions;

/**
 * Simple impl which will go to the database for each request
 * 
 * TBD: Class is not tested!!!
 * 
 * @author skarpushin
 * 
 */
public class StringIdAliasServiceSimpleImpl implements StringIdAliasService, InitializingBean {
	private static Logger log = LogManager.getLogger(StringIdAliasServiceSimpleImpl.class);

	private StringIdAliasDao stringIdAliasDao;

	@Override
	public void afterPropertiesSet() throws Exception {
		Preconditions.checkState(stringIdAliasDao != null);
	}

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public long getAliasFor(String str) {
		Long ret = null;
		try {
			ret = stringIdAliasDao.findAliasFor(str);
			if (ret != null) {
				return ret;
			}

			try {
				return stringIdAliasDao.createAliasFor(str);
			} catch (DuplicateKeyException dke) {
				log.debug("Looks like alias already exist in database, will load it");
				ret = stringIdAliasDao.findAliasFor(str);
				if (ret == null) {
					throw new PropertyServiceUnexpectedException(
							"Failed to create alias because of duplicate, but later was unable to find that duplicate.");
				}
				return ret;
			}
		} catch (Throwable t) {
			throw new PropertyServiceUnexpectedException("Failed to store alias", t);
		}
	}

	@Override
	public String getNameByAlias(long alias) {
		try {
			String ret = stringIdAliasDao.findAliasName(alias);
			if (ret == null) {
				throw new IllegalAccessException("Alias with id '" + alias + "' does not exist");
			}
			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to resolve alias name '" + alias + "'", t);
		}
	}

	public StringIdAliasDao getStringIdAliasDao() {
		return stringIdAliasDao;
	}

	@Required
	public void setStringIdAliasDao(StringIdAliasDao stringIdAliasDao) {
		this.stringIdAliasDao = stringIdAliasDao;
	}

}
