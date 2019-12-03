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
package org.summerb.easycrud.api;

import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.impl.EasyCrudServicePluggableImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapDelegatingImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapEventBusImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapPerRowAuthImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapTableAuthImpl;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapValidationImpl;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

import com.google.common.eventbus.EventBus;

/**
 * This interface defines "wire tap" for all common CRUD service methods
 * implemented by {@link EasyCrudServicePluggableImpl}.
 * 
 * <p>
 * 
 * You can easily tap into the workflow by implementing methods of this
 * interface and injecting it into {@link EasyCrudServicePluggableImpl}.
 * 
 * <p>
 * 
 * Consider extending {@link EasyCrudWireTapNoOpImpl} class if you don't need to
 * implement all methods.
 * 
 * <p>
 * 
 * In most cases you'd want to inject multiple wireTaps, in such case use
 * {@link EasyCrudWireTapDelegatingImpl} which will delegate to list of wireTaps
 * each time operation invoked.
 * 
 * <p>
 * 
 * Also note that EasyCrud contains default implementations for common tasks:
 * 
 * <p>
 * 
 * Validation impl of {@link EasyCrudValidationStrategy} can be injected using
 * {@link EasyCrudWireTapValidationImpl}.
 * 
 * <p>
 * 
 * Per-Table authorization {@link EasyCrudTableAuthStrategy} can be injected
 * using {@link EasyCrudWireTapTableAuthImpl}.
 * 
 * <p>
 * 
 * Per-Row authorization {@link EasyCrudPerRowAuthStrategy} can be injected
 * using {@link EasyCrudWireTapPerRowAuthImpl}.
 * 
 * <p>
 * 
 * {@link EventBus}-based events can be injected using
 * {@link EasyCrudWireTapEventBusImpl}.
 * 
 * @author sergeyk
 */
public interface EasyCrudWireTap<TId, TDto extends HasId<TId>> {
	/**
	 * @return False if all methods will work OK with just ID field. True if all
	 *         dto's must be fully filled befor invoking method of this interface.
	 *         In case False is returned then {@link EasyCrudService} will perform
	 *         batch operations when applicable instead of iterating elements
	 *         one-by-one
	 */
	boolean requiresFullDto();

	boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException;

	void beforeCreate(TDto dto) throws NotAuthorizedException, FieldValidationException;

	void afterCreate(TDto dto) throws FieldValidationException, NotAuthorizedException;

	boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException;

	void beforeUpdate(TDto from, TDto to) throws FieldValidationException, NotAuthorizedException;

	void afterUpdate(TDto from, TDto to) throws NotAuthorizedException, FieldValidationException;

	boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException;

	void beforeDelete(TDto dto) throws NotAuthorizedException, FieldValidationException;

	void afterDelete(TDto dto) throws FieldValidationException, NotAuthorizedException;

	boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException;

	void afterRead(TDto dto) throws FieldValidationException, NotAuthorizedException;
}
