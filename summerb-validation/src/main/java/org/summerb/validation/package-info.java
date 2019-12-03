/*******************************************************************************
 * Copyright 2015-2019 Sergey Karpushin
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
/**
 * This package contains core design and impl for validation of input/data. It
 * is primary strong side compared to other approaches is super easy to use and
 * also transparency it gives -- validation related to particular entity is
 * contained in 1 place and easily readable and augmentable.
 * 
 * It doesn't operate with human-readble text, instead it operates upon message
 * codes (see {@link org.summerb.i18n.HasMessageCode}.
 * 
 * Also, it supports validation of hierarchical structures.
 * 
 * Start with an empty instance of
 * {@link org.summerb.validation.ValidationContext}, call out-of the box methods
 * or add validation errors manually using
 * {@link org.summerb.validation.ValidationContext#add(ValidationError)}. And
 * then call {@link org.summerb.validation.ValidationContext#throwIfHasErrors()}
 * to throw well-structured exception containing all errors collected.
 * 
 * In case needed to return to REST client, use
 * {@link org.summerb.validation.FieldValidationException#getErrorDescriptionObject()}
 * 
 * @author sergeyk
 *
 */
package org.summerb.validation;
