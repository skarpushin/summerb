package org.summerb.easymail.impl;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Required;
import org.summerb.easymail.api.EmailMessageRepository;
import org.summerb.easymail.api.dto.EmailMessage;

public class EmailMessageRepositoryClasspathImpl implements EmailMessageRepository {
	private String pathPrefix;
	private Unmarshaller unmarshaller;

	@Override
	public EmailMessage get(String templateId, Locale locale) {
		try {
			InputStream is = getStream(templateId, locale);
			return parse(is);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to load message template: " + templateId, t);
		}
	}

	private InputStream getStream(String templateId, Locale locale) throws FileNotFoundException, URISyntaxException {
		String defaultFile = pathPrefix + templateId + ".xml";
		String localizedFile = null;
		InputStream is = null;
		if (Locale.ENGLISH.getLanguage() != locale.getLanguage()) {
			localizedFile = pathPrefix + templateId + "_" + locale.getLanguage() + ".xml";
			is = getResourceFile(localizedFile);
		}
		if (null == is) {
			is = getResourceFile(defaultFile);
		}
		if (null == is) {
			throw new FileNotFoundException("Email template not found: " + templateId);
		}
		return is;
	}

	private InputStream getResourceFile(String fileName) throws URISyntaxException {
		return getClass().getClassLoader().getResourceAsStream(fileName);
	}

	private EmailMessage parse(InputStream is) throws JAXBException {
		EmailMessage emailMessage = (EmailMessage) getUnmarshaller().unmarshal(is);
		return emailMessage;
	}

	private Unmarshaller getUnmarshaller() throws JAXBException {
		if (unmarshaller == null) {
			unmarshaller = createUnmarshaller();
		}
		return unmarshaller;
	}

	private static Unmarshaller createUnmarshaller() throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(EmailMessage.class);
		Unmarshaller ret = context.createUnmarshaller();
		return ret;
	}

	public String getPathPrefix() {
		return pathPrefix;
	}

	@Required
	public void setPathPrefix(String pathPrefix) {
		this.pathPrefix = pathPrefix;
	}
}
