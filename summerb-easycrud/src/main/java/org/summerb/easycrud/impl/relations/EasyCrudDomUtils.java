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
package org.summerb.easycrud.impl.relations;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.summerb.easycrud.api.dto.HasId;
import org.summerb.easycrud.api.dto.datapackage.DataSet;
import org.summerb.easycrud.api.dto.datapackage.DataTable;
import org.summerb.easycrud.api.dto.relations.Ref;
import org.summerb.easycrud.api.dto.relations.RefQuantity;

import com.google.common.base.Preconditions;

public class EasyCrudDomUtils {
	/**
	 * Finds all referenced objects and creates a list of them
	 * 
	 * @param dataSet   data set where source and all possible targets are located
	 * @param src       id of the referencer
	 * @param ref       description of the reference
	 * @param rowClass  DTO class, it's used to avoid cimpiler confusing between
	 *                  TRowDto and TRetDto class, since latter one might be a
	 *                  subclass of former one
	 * @param builder   function that can take id of an referencee and build new
	 *                  instance. This will be added to the returned list
	 * 
	 * @param <TSrcId>  TSrcId
	 * @param <TSrcRow> TSrcRow
	 * @param <TId>     TId
	 * @param <TRowDto> TRowDto
	 * @param <TRetRow> TRetRow
	 * 
	 * @return list of referenced objects or empty list if none found. Changes to a
	 *         list will not affect the database
	 */
	public static <TSrcId, TSrcRow extends HasId<TSrcId>, TId, TRowDto extends HasId<TId>, TRetRow extends TRowDto> List<TRetRow> buildReferencedObjectsList(
			DataSet dataSet, TSrcRow src, Ref ref, Class<TRowDto> rowClass, Function<TRowDto, TRetRow> builder) {
		try {
			Preconditions.checkArgument(ref.getQuantity() != RefQuantity.Many2Many,
					"ManyToMany is not supported (yet) by this method");

			Object srcValue = findSrcFieldValue(src, ref.getFromField());

			@SuppressWarnings("unchecked")
			DataTable<TId, TRowDto> target = dataSet.get(ref.getToEntity());
			if (target.getRows().size() == 0) {
				return Collections.emptyList();
			}

			TRowDto targetExample = target.getRows().values().iterator().next();
			PropertyDescriptor targetField = BeanUtils.getPropertyDescriptor(targetExample.getClass(),
					ref.getToField());

			List<TRetRow> ret = new ArrayList<>();
			for (TRowDto row : target.getRows().values()) {
				Object targetValue = targetField.getReadMethod().invoke(row);
				if (ObjectUtils.nullSafeEquals(srcValue, targetValue)) {
					TRetRow rowToAdd = builder.apply(row);
					ret.add(rowToAdd);
				}
			}

			return ret;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to build ist of referenced (" + ref.getName() + ") objects for " + src,
					t);
		}
	}

	private static Object findSrcFieldValue(Object dto, String fieldName) {
		try {
			PropertyDescriptor property = BeanUtils.getPropertyDescriptor(dto.getClass(), fieldName);
			return property.getReadMethod().invoke(dto);
		} catch (Throwable t) {
			throw new RuntimeException("Failed to get source field (" + fieldName + ") value from object " + dto, t);
		}
	}
}
