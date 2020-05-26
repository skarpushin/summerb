package org.summerb.dbupgrade.utils;

import java.util.Arrays;
import java.util.List;

import org.summerb.utils.Pair;

/**
 * Nor standard java tokenizer nor apache text do not provide simple capability
 * to search for multi-char delimeters and also return delimeters themselves.
 * 
 * @author sergeyk
 *
 */
public class StringTokenizer {
	private String subject;
	private List<String> delimeters;

	public StringTokenizer(String stringToTokenize, String... delimeters) {
		this.subject = stringToTokenize;
		this.delimeters = Arrays.asList(delimeters);
	}

	// NOTE: This is rather barbarian way of working with strings, I'm just too
	// tired now to work on optimized version
	public String next() {
		if (subject.length() == 0) {
			return null;
		}

		int pos = 0;
		Pair<String, Integer> nextDelimeter = delimeters.stream().map(x -> Pair.of(x, subject.indexOf(x)))
				.filter(p -> p.getValue() >= 0).min((a, b) -> a.getValue() - b.getValue()).orElse(null);
		if (nextDelimeter == null) {
			String ret = subject.substring(pos);
			pos = subject.length();
			return ret;
		}

		if (nextDelimeter.getValue() == 0) {
			pos += nextDelimeter.getKey().length();
			subject = subject.substring(pos);
			return nextDelimeter.getKey();
		}

		String ret = subject.substring(pos, nextDelimeter.getValue());
		pos += ret.length();
		subject = subject.substring(pos);
		return ret;
	}
}
