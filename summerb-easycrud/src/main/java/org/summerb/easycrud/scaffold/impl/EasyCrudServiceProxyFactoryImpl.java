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
package org.summerb.easycrud.scaffold.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.summerb.easycrud.scaffold.api.EasyCrudServiceProxyFactory;
import org.summerb.easycrud.scaffold.api.ScaffoldedMethodFactory;

public class EasyCrudServiceProxyFactoryImpl implements EasyCrudServiceProxyFactory {
	private ScaffoldedMethodFactory scaffoldedMethodFactory;

	@Override
	public <TService> TService createImpl(Class<TService> serviceInterface) {
		return EasyCrudServiceScaffoldedImpl.createImpl(serviceInterface, scaffoldedMethodFactory);
	}

	public ScaffoldedMethodFactory getScaffoldQueryImplFactory() {
		return scaffoldedMethodFactory;
	}

	@Autowired(required = false)
	public void setScaffoldQueryImplFactory(ScaffoldedMethodFactory scaffoldedMethodFactory) {
		this.scaffoldedMethodFactory = scaffoldedMethodFactory;
	}
}
