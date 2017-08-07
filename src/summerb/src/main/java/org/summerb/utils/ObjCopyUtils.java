package org.summerb.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Preconditions;

public class ObjCopyUtils {
	/**
	 * Get property values from source object (using getters) and assign to
	 * destination (using setters)
	 * 
	 * @param src
	 *            source object
	 * @param dst
	 *            target object. source class expected to be assignable from
	 *            destination
	 */
	public static <TSrc, TDst extends TSrc> void assignFields(TSrc src, TDst dst) {
		Preconditions.checkArgument(src != null, "Src must not be null");
		Preconditions.checkArgument(dst != null, "Dst must not be null");
		Preconditions.checkArgument(src.getClass().isAssignableFrom(dst.getClass()),
				"source class expected to be assignable from destination");

		try {
			PropertyDescriptor[] srcProps = BeanUtils.getPropertyDescriptors(src.getClass());
			for (PropertyDescriptor pd : srcProps) {
				Method writeMethod = pd.getWriteMethod();
				if (writeMethod == null) {
					continue;
				}
				
				Object value = pd.getReadMethod().invoke(src);
				writeMethod.invoke(dst, value);
			}
		} catch (Throwable t) {
			throw new RuntimeException("Failed to copy properties from " + src + " to " + dst, t);
		}
	}
}
