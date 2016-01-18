package org.summerb.utils.jmx;

import java.lang.management.ManagementFactory;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

@SuppressWarnings("rawtypes")
public class MapSizeMXBeanImpl implements MapSizeMXBean {
	private static Logger log = Logger.getLogger(MapSizeMXBeanImpl.class);
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
