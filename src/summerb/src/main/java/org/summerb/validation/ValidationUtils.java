package org.summerb.validation;

import java.io.File;
import java.io.IOException;

import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

public class ValidationUtils {
	public static boolean isValidNotNullableUuid(String uuid) {
		return uuid != null && (0 < uuid.length() && uuid.length() <= 36);
	}

	public static boolean isValidNullableUuid(String uuid) {
		return uuid == null || (0 < uuid.length() && uuid.length() <= 36);
	}

	public static boolean isValidFilename(String fileName) {
		// TODO: Add validation check for that
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
