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
package org.summerb.webappboilerplate.security.ve;

import org.summerb.users.api.exceptions.InvalidPasswordException;
import org.summerb.validation.ValidationError;
import org.summerb.webappboilerplate.security.dto.LoginParams;

public class PasswordInvalidValidationError extends ValidationError {
	private static final long serialVersionUID = 5184851404690565907L;

	public PasswordInvalidValidationError() {
		super(InvalidPasswordException.ERROR_LOGIN_INVALID_PASSWORD, LoginParams.FN_PASSWORD);
	}
}
