package org.summerb.utils.exceptions.translator;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ExceptionTranslatorDelegatingImpl implements ExceptionTranslator {
	private List<ExceptionTranslator> translators;
	private ExceptionUnwindingStrategy exceptionUnwindingStrategy = new ExceptionUnwindingStrategyImpl();

	public ExceptionTranslatorDelegatingImpl(List<ExceptionTranslator> translators) {
		this.translators = new LinkedList<>(translators);
	}

	@Override
	public String buildUserMessage(Throwable t, Locale locale) {
		if (t == null) {
			return "";
		}

		StringBuilder ret = new StringBuilder();

		Throwable cur = exceptionUnwindingStrategy.getNextMeaningfulExc(t);
		while (cur != null) {
			if (ret.length() > 0) {
				ret.append(" -> ");
			}

			boolean matchFound = false;
			for (ExceptionTranslator translator : translators) {
				String msg = translator.buildUserMessage(cur, locale);
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
			cur = exceptionUnwindingStrategy.getNextMeaningfulExc(cur.getCause());
		}
		return ret.toString();
	}

	public ExceptionUnwindingStrategy getExceptionUnwindingStrategy() {
		return exceptionUnwindingStrategy;
	}

	public void setExceptionUnwindingStrategy(ExceptionUnwindingStrategy exceptionUnwindingStrategy) {
		this.exceptionUnwindingStrategy = exceptionUnwindingStrategy;
	}
}
