package org.summerb.approaches.validation;

import java.io.File;
import java.io.IOException;

import org.springframework.util.StringUtils;
import org.summerb.approaches.jdbccrud.api.StringIdGenerator;
import org.summerb.approaches.jdbccrud.impl.StringIdGeneratorUuidImpl;

import com.google.common.base.Preconditions;

public class ValidationUtils {

	/**
	 * @deprecated use {@link StringIdGenerator} and it's default impl
	 *             {@link StringIdGeneratorUuidImpl} instead of this method
	 */
	@Deprecated
	public static boolean isValidNotNullableUuid(String uuid) {
		return uuid != null && (0 < uuid.length() && uuid.length() <= 36);
	}

	/**
	 * @deprecated use {@link StringIdGenerator} and it's default impl
	 *             {@link StringIdGeneratorUuidImpl} instead of this method
	 */
	@Deprecated
	public static boolean isValidNullableUuid(String uuid) {
		return uuid == null || (0 < uuid.length() && uuid.length() <= 36);
	}

	public static boolean isValidFilename(String fileName) {
		Preconditions.checkArgument(StringUtils.hasText(fileName), "File name must be provided");

		// Check if name contains path separator
		if (fileName.contains("\\") || fileName.contains("/")) {
			return false;
		}

		File f = new File(fileName);
		try {
			f.getCanonicalPath();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
