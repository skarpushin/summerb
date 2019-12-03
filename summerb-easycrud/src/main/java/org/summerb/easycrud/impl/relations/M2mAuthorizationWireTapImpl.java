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
package org.summerb.easycrud.impl.relations;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.ObjectUtils;
import org.summerb.easycrud.api.EasyCrudService;
import org.summerb.easycrud.api.EasyCrudWireTap;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.relations.ManyToManyDto;
import org.summerb.easycrud.impl.wireTaps.EasyCrudWireTapNoOpImpl;
import org.summerb.security.api.exceptions.NotAuthorizedException;
import org.summerb.validation.FieldValidationException;

import com.google.common.base.Preconditions;

/**
 * This impl will delegate calls to service responsible for referencers. It
 * might get resource consuming if referencer authorization wireTap requires
 * full dto for authorization checks.
 * 
 * Whether rely on caching or provide your custom impl to optimize performance.
 * 
 * @author sergeyk
 *
 * @param <TId1>
 * @param <TId2>
 */
public class M2mAuthorizationWireTapImpl<TId1, TId2> extends EasyCrudWireTapNoOpImpl<Long, ManyToManyDto<TId1, TId2>>
		implements InitializingBean {
	private EasyCrudWireTap<TId1, HasId<TId1>> referencerAuthorizationWireTap;
	private EasyCrudService<TId1, HasId<TId1>> referencerService;
	private boolean referencerAuthRequiresFullDto;
	private Class<HasId<TId1>> referencerClass;

	@Override
	public void afterPropertiesSet() throws Exception {
		referencerAuthRequiresFullDto = referencerAuthorizationWireTap.requiresFullDto();
		if (!referencerAuthRequiresFullDto) {
			referencerClass = referencerService.getDtoClass();
		}
	}

	@Override
	public boolean requiresFullDto() {
		return true;
	}

	@Override
	public boolean requiresOnCreate() throws FieldValidationException, NotAuthorizedException {
		return referencerAuthorizationWireTap.requiresOnUpdate();
	}

	@Override
	public void beforeCreate(ManyToManyDto<TId1, TId2> dto) throws NotAuthorizedException, FieldValidationException {
		HasId<TId1> referencer;
		if (referencerAuthRequiresFullDto) {
			referencer = referencerService.findById(dto.getSrc());
			Preconditions.checkState(referencer != null,
					referencerService.getEntityTypeMessageCode() + " identified by " + dto.getSrc() + " wasn't found");
		} else {
			referencer = buildDtoWithId(dto.getSrc());
		}
		referencerAuthorizationWireTap.beforeUpdate(referencer, referencer);
	}

	private HasId<TId1> buildDtoWithId(TId1 id) {
		try {
			HasId<TId1> ret = referencerClass.newInstance();
			ret.setId(id);
			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to build example dto", t);
		}
	}

	@Override
	public boolean requiresOnUpdate() throws NotAuthorizedException, FieldValidationException {
		return referencerAuthorizationWireTap.requiresOnUpdate();
	}

	@Override
	public void beforeUpdate(ManyToManyDto<TId1, TId2> from, ManyToManyDto<TId1, TId2> to)
			throws FieldValidationException, NotAuthorizedException {
		Preconditions.checkArgument(ObjectUtils.nullSafeEquals(from.getSrc(), to.getSrc()),
				"Referencer is not supposed to be changed");
		HasId<TId1> referencer;
		if (referencerAuthRequiresFullDto) {
			referencer = referencerService.findById(to.getSrc());
			Preconditions.checkState(referencer != null,
					referencerService.getEntityTypeMessageCode() + " identified by " + from.getSrc() + " wasn't found");
		} else {
			referencer = buildDtoWithId(to.getSrc());
		}
		referencerAuthorizationWireTap.beforeUpdate(referencer, referencer);
	}

	@Override
	public boolean requiresOnDelete() throws FieldValidationException, NotAuthorizedException {
		return referencerAuthorizationWireTap.requiresOnUpdate();
	}

	@Override
	public void beforeDelete(ManyToManyDto<TId1, TId2> dto) throws NotAuthorizedException, FieldValidationException {
		HasId<TId1> referencer;
		if (referencerAuthRequiresFullDto) {
			referencer = referencerService.findById(dto.getSrc());
			Preconditions.checkState(referencer != null,
					referencerService.getEntityTypeMessageCode() + " identified by " + dto.getSrc() + " wasn't found");
		} else {
			referencer = buildDtoWithId(dto.getSrc());
		}
		referencerAuthorizationWireTap.beforeUpdate(referencer, referencer);
	}

	@Override
	public boolean requiresOnRead() throws NotAuthorizedException, FieldValidationException {
		return referencerAuthorizationWireTap.requiresOnRead();
	}

	@Override
	public void afterRead(ManyToManyDto<TId1, TId2> dto) throws FieldValidationException, NotAuthorizedException {
		HasId<TId1> referencer;
		if (referencerAuthRequiresFullDto) {
			referencer = referencerService.findById(dto.getSrc());
		} else {
			referencer = buildDtoWithId(dto.getSrc());
		}
		referencerAuthorizationWireTap.afterRead(referencer);
	}

	public EasyCrudWireTap<TId1, HasId<TId1>> getReferencerAuthorizationWireTap() {
		return referencerAuthorizationWireTap;
	}

	@Required
	public void setReferencerAuthorizationWireTap(EasyCrudWireTap<TId1, HasId<TId1>> referencerAuthorizationWireTap) {
		this.referencerAuthorizationWireTap = referencerAuthorizationWireTap;
	}

	public EasyCrudService<TId1, HasId<TId1>> getReferencerService() {
		return referencerService;
	}

	@Required
	public void setReferencerService(EasyCrudService<TId1, HasId<TId1>> referencerService) {
		this.referencerService = referencerService;
	}

}
