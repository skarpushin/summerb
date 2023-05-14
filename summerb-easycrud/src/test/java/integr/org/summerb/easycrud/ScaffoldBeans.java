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
package integr.org.summerb.easycrud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.scaffold.api.EasyCrudScaffold;

import com.google.common.eventbus.EventBus;

@Configuration
public class ScaffoldBeans {
	@Autowired
	private EasyCrudScaffold easyCrudScaffold;

	@Autowired
	private EasyCrudWireTap<?, ?> easyCrudWireTapEventBus;

	// table1:
	// table2: forms_test_2

	@Bean
	public TestDto1Service testDto1Service() {
		return easyCrudScaffold.fromService(TestDto1Service.class, TestDto1.class.getCanonicalName(), "forms_test_1");
	}

	@Bean
	public EasyCrudService<String, TestDto1> testDto1ServiceEb(EventBus eventBus) {
		return easyCrudScaffold.fromRowClass(TestDto1.class, TestDto1.class.getCanonicalName(), "forms_test_1",
				easyCrudWireTapEventBus);
	}

	@Bean
	public EasyCrudService<Long, TestDto2> testDto2Service() {
		return easyCrudScaffold.fromRowClass(TestDto2.class, TestDto2.class.getCanonicalName(), "forms_test_2");
	}
}
