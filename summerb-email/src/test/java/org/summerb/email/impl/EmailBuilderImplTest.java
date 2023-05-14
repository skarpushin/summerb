/*******************************************************************************
 * Copyright 2015-2023 Sergey Karpushin
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
package org.summerb.email.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.summerb.email.api.dto.EmailMessage;
import org.summerb.email.api.dto.EmailTemplateParams;
import org.summerb.email.api.dto.EmailTemplateParamsFactory;
import org.summerb.stringtemplate.api.StringTemplateFactory;
import org.summerb.stringtemplate.impl.StringTemplateFactorySpElImpl;

public class EmailBuilderImplTest {

	StringTemplateFactory stringTemplateFactory = new StringTemplateFactorySpElImpl();

	@Test
	public void testBuildEmail_1() throws Exception {
		EmailMessageBuilderImpl fixture = EmailBuilderImplFactory.createEmailBuilderImplUS();
		EmailTemplateParams emailTemplateParams = EmailTemplateParamsFactory.createEmailTemplateParams();

		EmailMessage result = fixture.buildEmail("fromAddress", "toAddress", emailTemplateParams);

		assertNotNull(result);
		assertEquals("From: actualSender", result.getFromName());
		assertEquals("To: actualRecipient", result.getToName());
		assertEquals("Subject: bodyId", result.getSubject());
		assertEquals("Body: bodyMsg", result.getBody());
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
}
