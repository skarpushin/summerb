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

// TODO: Make all private fields protected for increased extensibility

// TODO: Replace Spring XML config with Spring Java config

// TODO: Switch to constructor dependencies injection - as opposite of @Autowired and @Required

// TODO: Remove redundant exceptions throws (like NAE and FVE)

// TODO: Rework validator classes design to use new ValidationContextFactory

// TODO: Modify auth classes to also have boolean methods to check if user have permissions

// TODO: Introduce standard validator that can use Jakarta annotations - must be also extendible so
// user could augment it with manual validations

// TODO: Rework Query to use method references

// TODO: Introduce another AuthStrategy option PerId (on the contrary per row) - so that only ID of
// object is needed (also will be then utilized for ACL logic)

/**
 * SummerB offers an ready-to-use and easily extensible/customizable design and impl for working
 * with simple CRUD repositories.
 *
 * <p>It's called EasyCrud.
 *
 * <p>It provides building blocks to quickly bootstrap backend aspect of CRUD repository. It takes
 * into account many things including i18n, pagination, validation, security
 *
 * <p>Extensive description of this package is given here:
 * https://github.com/skarpushin/summerb/wiki/Easy-CRUD
 *
 * @author sergeyk
 */
package org.summerb.easycrud;
