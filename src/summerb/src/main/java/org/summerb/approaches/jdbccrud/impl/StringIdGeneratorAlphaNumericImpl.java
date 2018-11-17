package org.summerb.approaches.jdbccrud.impl;

import java.util.Random;
import java.util.UUID;

import org.summerb.approaches.jdbccrud.api.StringIdGenerator;

/**
 * Simple impl based on {@link UUID} class.
 * 
 * @author sergeyk
 *
 */
public class StringIdGeneratorAlphaNumericImpl implements StringIdGenerator {
	private int length = 8;

	/**
	 * NOTE: "O" and "l" are removed to make it more human-readable
	 */
	private String alphabet = "ABCDEFGHIJKLMNPQRSTUVWXYZ0123456789abcdefghijkmnopqrstuvwxyz";

	private static final Random random = new Random();

	public StringIdGeneratorAlphaNumericImpl() {
	}

	public StringIdGeneratorAlphaNumericImpl(int length) {
		super();
		this.length = length;
	}

	@Override
	public String generateNewId(Object optionalDto) {
		StringBuilder ret = new StringBuilder();
		while (ret.length() < 8) {
			int next = random.nextInt(alphabet.length());
			ret.append(alphabet.charAt(next));
		}
		return ret.toString();
	}

	@Override
	public boolean isValidId(String id) {
		return id != null && id.length() == length && checkChars(id);
	}

	private boolean checkChars(String id) {
		for (int i = 0; i < id.length(); i++) {
			if (!alphabet.contains(id.substring(i, i + 1))) {
				return false;
			}
		}
		return true;
	}

	public int getLength() {
		return length;
	}

	/**
	 * The length of the generated ID. 8 is considered string enough and this is a
	 * default value
	 */
	public void setLength(int length) {
		this.length = length;
	}

	public String getAlphabet() {
		return alphabet;
	}

	public void setAlphabet(String alphabet) {
		this.alphabet = alphabet;
	}
}
