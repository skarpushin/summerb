package org.summerb.dbupgrade.utils;

import com.google.common.base.Preconditions;

/**
 * Neither standard java tokenizer nor apache text do not provide simple
 * capability to search for multi-char delim and also return delim themselves.
 * 
 * Initial impl that I've created was super easy to comprehend but was insanely
 * slow. I've tried to make this impl efficient, but as always, it makes it a
 * bit hard to comprehend.
 * 
 * Also, {@link String#substring(int, int)} is super inefficient since Java7, so
 * I have to work here with CharSequences instead so that original String is not
 * getting copied. This affects many aspects including client code which now
 * have to work with {@link SubString}. But performance of this impl is
 * obliterating compared to initial impl.
 * 
 * GC usage could be improved even more if we will not create new instances of
 * {@link SubString} but this will to much for client code I think.
 * 
 * @author sergeyk
 *
 */
public class StringTokenizer {
	private final String subject;
	private SubString[] delim;

	/**
	 * We have a copy of this for faster searching within subject string
	 */
	private String[] delimStr;

	/**
	 * Current position in the string
	 */
	private int pos = 0;

	/**
	 * Holds indexes of each known delimeter
	 */
	private int[] delimPos;

	private int delimLength;

	private int lastSelectedDelimeterIndex = -1;

	public StringTokenizer(String stringToTokenize, SubString... delimeters) {
		this.subject = stringToTokenize;
		this.delim = delimeters;
		this.delimLength = delimeters.length;
		this.delimStr = new String[delimLength];

		for (int i = 0; i < delimLength; i++) {
			delimStr[i] = new String(new StringBuilder(delim[i]).toString());
		}
	}

	/**
	 * @return true if initialization happened for the first time
	 */
	private boolean initDelimPos() {
		if (delimPos != null) {
			return false;
		}

		delimPos = new int[delimLength];
		for (int i = 0; i < delimLength; i++) {
			delimPos[i] = subject.indexOf(delimStr[i]);
		}

		// trim those that are not present in the string anymore
		int i = 0;
		while (delimLength > 0 && i < delimLength) {
			if (delimPos[i] == -1) {
				delimNoLongerPresent(i);
			} else {
				i++;
			}
		}
		
		if (delimLength > 0) {
			lastSelectedDelimeterIndex = indexOfNearestDelimeter(delimPos);
		}
		
		return true;
	}

	private void delimNoLongerPresent(int i) {
		Preconditions.checkState(delimLength > 0,
				"Algorithm error - how come we're trying to delete delimeter if delimeters array is empty?");

		if (delimLength == 1) {
			delimLength = 0;
			return;
		}

		delim[i] = delim[delimLength - 1];
		delimStr[i] = delimStr[delimLength - 1];
		delimPos[i] = delimPos[delimLength - 1];
		delimLength--;
	}

	/**
	 * Updates delimeter positions if needed
	 * 
	 * @return index of the delimeter in the delim list, or -1 if none applicable
	 */
	private int updateDelimPositions() {
		while (lastSelectedDelimeterIndex >= 0 && delimPos[lastSelectedDelimeterIndex] < pos) {
			if (pos + delim[lastSelectedDelimeterIndex].length() >= subject.length()) {
				// if this delimeter could not fit anymore in the string
				delimNoLongerPresent(lastSelectedDelimeterIndex);
			} else {
				delimPos[lastSelectedDelimeterIndex] = subject.indexOf(delimStr[lastSelectedDelimeterIndex], pos);
				if (delimPos[lastSelectedDelimeterIndex] == -1) {
					delimNoLongerPresent(lastSelectedDelimeterIndex);
				}
			}

			// Ok, now we assume that all delims are now updated and present, need to find
			// nearest
			if (delimLength == 0) {
				return -1;
			}

			lastSelectedDelimeterIndex = indexOfNearestDelimeter(delimPos);
		}
		return lastSelectedDelimeterIndex;
	}

	public int indexOfNearestDelimeter(int... array) {
		int ret = 0;
		for (int i = 1; i < delimLength; i++) {
			if (array[i] < array[ret]) {
				ret = i;
			}
		}
		return ret;
	}

	public SubString next() {
		if (initDelimPos() && delimLength == 0) {
			// right of the bed we see that there are no delimeters -- just return string
			// itself
			pos = subject.length();
			return new SubString(subject);
		}

		if (pos >= subject.length()) {
			return null;
		}

		int nextDelimeter = updateDelimPositions();
		if (nextDelimeter == -1) {
			SubString ret = new SubString(subject, pos);
			pos = subject.length();
			return ret;
		}

		if (delimPos[nextDelimeter] == pos) {
			pos += delim[nextDelimeter].length();
			return delim[nextDelimeter];
		}

		SubString ret = new SubString(subject, pos, delimPos[nextDelimeter]);
		pos = delimPos[nextDelimeter];
		return ret;
	}

	public static class SubString implements CharSequence {
		private int beginIndex;
		private int endIndex;
		private String string;

		public SubString(String subject) {
			this.string = subject;
			this.beginIndex = 0;
			this.endIndex = subject.length();
		}

		public SubString(String subject, int beginIndex, int endIndex) {
			this.string = subject;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
		}

		public SubString(String subject, int pos) {
			this.string = subject;
			this.beginIndex = pos;
			this.endIndex = string.length();
		}

		@Override
		public int length() {
			return endIndex - beginIndex;
		}

		@Override
		public char charAt(int index) {
			return string.charAt(beginIndex + index);
		}

		@Override
		public CharSequence subSequence(int start, int end) {
			return new SubString(string, beginIndex + start, beginIndex + end);
		}
	}
}
