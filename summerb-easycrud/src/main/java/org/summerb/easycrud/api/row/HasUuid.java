/*******************************************************************************
 * Copyright 2015-2025 Sergey Karpushin
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
package org.summerb.easycrud.api.row;

import org.summerb.easycrud.api.StringIdGenerator;
import org.summerb.easycrud.impl.dao.mysql.EasyCrudDaoSqlImpl;

/**
 * Impl this interface if your DTO's primary key is automatically generated String.
 *
 * <p>Name of this interface remains HasUuid for compatibility purposes, while actually you can
 * customize underlying impl and provide string of any format by setting instance of {@link
 * StringIdGenerator} to {@link EasyCrudDaoSqlImpl#setStringIdGenerator(StringIdGenerator)}
 *
 * @author sergey.karpushin
 */
public interface HasUuid extends HasId<String> {}
