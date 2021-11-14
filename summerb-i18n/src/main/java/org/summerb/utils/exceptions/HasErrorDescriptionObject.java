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
package org.summerb.utils.exceptions;

import java.io.Serializable;

import org.summerb.utils.DtoBase;

/**
 * Exception might want to implement this interface if there is an object that
 * describes the exception. It has to be something that {@link Serializable} as
 * intention is to send it over network
 * 
 * @author sergeyk
 *
 */
public interface HasErrorDescriptionObject<T extends DtoBase> {
	T getErrorDescriptionObject();
}
