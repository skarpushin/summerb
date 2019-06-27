package org.summerb.webappboilerplate.utils;

import java.io.IOException;
import java.io.InputStream;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

public class MimeTypeResolverImpl implements MimeTypeResolver {
	private static Logger log = Logger.getLogger(MimeTypeResolverImpl.class);

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
