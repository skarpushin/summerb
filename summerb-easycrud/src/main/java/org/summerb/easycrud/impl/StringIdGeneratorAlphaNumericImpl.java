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
package org.summerb.easycrud.impl;

import java.util.Random;

import org.summerb.easycrud.api.StringIdGenerator;

/**
 * Simple impl, that generates random alphanumeric IDs
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
