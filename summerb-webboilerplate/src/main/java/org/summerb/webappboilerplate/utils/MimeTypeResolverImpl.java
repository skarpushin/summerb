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
package org.summerb.webappboilerplate.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public class MimeTypeResolverImpl implements MimeTypeResolver {
	private static Logger log = LogManager.getLogger(MimeTypeResolverImpl.class);

	private FileTypeMap fileTypeMap;

	@Override
	public String resolveContentTypeByFileName(String fileName) {
		try {
			String extension = StringUtils.getFilenameExtension(fileName);
			if (!StringUtils.hasText(extension)) {
				return MediaType.APPLICATION_OCTET_STREAM_VALUE;
			}

			String mediaType = getFileTypeMap().getContentType(fileName);
			if (!StringUtils.hasText(mediaType)) {
				return MediaType.APPLICATION_OCTET_STREAM_VALUE;
			}

			MediaType parsedMediaType = MediaType.parseMediaType(mediaType);
			if (parsedMediaType == null) {
				return MediaType.APPLICATION_OCTET_STREAM_VALUE;
			}

			return parsedMediaType.toString();
		} catch (Throwable t) {
			log.warn("Failed to resolve content type for file name: " + fileName, t);
			return MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
	}

	public FileTypeMap getFileTypeMap() {
		if (fileTypeMap == null) {
			fileTypeMap = loadFileTypeMapFromContextSupportModule();
		}

		return fileTypeMap;
	}

	private FileTypeMap loadFileTypeMapFromContextSupportModule() {
		// see if we can find the extended mime.types from the context-support
		// module
		Resource mappingLocation = new ClassPathResource("org/springframework/mail/javamail/mime.types");
		if (mappingLocation.exists()) {
			InputStream inputStream = null;
			try {
				inputStream = mappingLocation.getInputStream();
				return new MimetypesFileTypeMap(inputStream);
			} catch (IOException ex) {
				// ignore
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException ex) {
						// ignore
					}
				}
			}
		}
		return FileTypeMap.getDefaultFileTypeMap();
	}
}
