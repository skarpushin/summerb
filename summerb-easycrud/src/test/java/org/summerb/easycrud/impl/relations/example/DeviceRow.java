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
package org.summerb.easycrud.impl.relations.example;

public class DeviceRow extends RowBase implements HasEnvId {
	private static final long serialVersionUID = -8271872562969504597L;

	public static final String FN_IDENTIFIER = "identifier";
	public static final int FN_IDENTIFIER_SIZE = 64;

	public static final String FN_NAME = "name";
	public static final int FN_NAME_SIZE = 45;

	public static final String FN_SERIAL_NUMBER = "serialNumber";
	public static final int FN_SERIAL_NUMBER_SIZE = 36;

	private long envId;
	private String identifier;
	private String name;
	private String serialNumber;

	@Override
	public long getEnvId() {
		return envId;
	}

	@Override
	public void setEnvId(long envId) {
		this.envId = envId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
}
