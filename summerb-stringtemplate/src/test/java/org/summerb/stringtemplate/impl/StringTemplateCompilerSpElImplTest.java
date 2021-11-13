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
package org.summerb.stringtemplate.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.summerb.stringtemplate.api.StringTemplate;

public class StringTemplateCompilerSpElImplTest {

	/**
	 * The object that is being tested.
	 * 
	 * @see ru.skarpushin.services.stringtemplate.impl.
	 *      StringTemplateCompilerSpElImpl
	 */
	private StringTemplateCompilerlImpl fixture = new StringTemplateCompilerlImpl();

	/**
	 * Run the StringTemplate compile(String) method test
	 */
	@Test
	public void testCompile() {
		String template = "Hello ${name}! I hope you are fine. Now you have ${amount} money!";
		StringTemplate stringTemplate = fixture.compile(template);
		String result = stringTemplate.applyTo(new DomainObjectExample("Bob", 1234));
		assertEquals("Hello Bob! I hope you are fine. Now you have 1234 money!", result);
	}

	public static class DomainObjectExample {
		private String name;
		private int amount;

		public DomainObjectExample(String name, int amount) {
			this.name = name;
			this.amount = amount;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}
	}
}
