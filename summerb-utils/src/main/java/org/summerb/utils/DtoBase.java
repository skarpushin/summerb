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
package org.summerb.utils;

import java.io.Serializable;

/**
 * Special interface used to mark your DTOs.
 * 
 * It might help to correctly handle JSON serialization and it's also required
 * by other parts of the library, like EntityChangedEvent
 * 
 * It's also useful for enforcing which classes are allowed to be deserialized
 * when client determines which class to use when deserializing DTO. I.e. see
 * EntityChangedEventAdapter
 * 
 * @author sergeyk
 *
 */
public interface DtoBase extends Serializable {

}
