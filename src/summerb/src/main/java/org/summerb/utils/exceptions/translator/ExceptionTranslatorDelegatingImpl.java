package org.summerb.utils.exceptions.translator;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

public class ExceptionTranslatorDelegatingImpl implements ExceptionTranslator {
	private List<ExceptionTranslator> translators;

	public ExceptionTranslatorDelegatingImpl(List<ExceptionTranslator> translators) {
		this.translators = new LinkedList<>(translators);
	}

	@Override
	public String buildUserMessage(Throwable t, MessageSource messageSource, Locale locale) {
		if (t == null) {
			return "";
		}
		
		StringBuilder ret = new StringBuilder();

		Throwable cur = t;
		while (cur != null) {
			if (ret.length() > 0) {
				ret.append(" -> ");
			}

			boolean matchFound = false;
			for (ExceptionTranslator translator : translators) {
				String msg = translator.buildUserMessage(cur, messageSource, locale);
				if (msg == null) {
					continue;
				}
				
				ret.append(msg);
				matchFound = true;
				break;
			}
			if (!matchFound) {
				ret.append(t.getMessage());
			}

			if (cur == cur.getCause()) {
				break;
			}
			cur = cur.getCause();
		}
		return ret.toString();
	}
}
