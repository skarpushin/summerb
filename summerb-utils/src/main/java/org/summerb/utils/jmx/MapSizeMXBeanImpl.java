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
package org.summerb.utils.jmx;

import java.lang.management.ManagementFactory;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

@SuppressWarnings("rawtypes")
public class MapSizeMXBeanImpl implements MapSizeMXBean {
	private static Logger log = LogManager.getLogger(MapSizeMXBeanImpl.class);
	private Map map;

	public MapSizeMXBeanImpl(String cname, Map map) {
		Preconditions.checkArgument(map != null);
		Preconditions.checkArgument(StringUtils.hasText(cname));
		this.map = map;
		MBeanServer server = ManagementFactory.getPlatformMBeanServer();
		try {
			String name = String.format("%s:type=Cache,name=%s", map.getClass().getPackage().getName(), cname);
			ObjectName mxBeanName = new ObjectName(name);
			if (!server.isRegistered(mxBeanName)) {
				server.registerMBean(this, new ObjectName(name));
			}
		} catch (Throwable t) {
			log.error("Failed to init jmx bean for map " + cname, t);
		}
	}

	@Override
	public long getSize() {
		return map.size();
	}
}
