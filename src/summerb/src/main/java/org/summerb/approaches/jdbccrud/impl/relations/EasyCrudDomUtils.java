package org.summerb.approaches.jdbccrud.impl.relations;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ObjectUtils;
import org.summerb.approaches.jdbccrud.api.dto.HasId;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataSet;
import org.summerb.approaches.jdbccrud.api.dto.datapackage.DataTable;
import org.summerb.approaches.jdbccrud.api.dto.relations.Ref;
import org.summerb.approaches.jdbccrud.api.dto.relations.RefQuantity;

import com.google.common.base.Preconditions;

public class EasyCrudDomUtils {
	/**
	 * Finds all referenced objects and creates a list of them
	 * 
	 * @param dataSet
	 *            data set where source and all possible targets are located
	 * @param src
	 *            id of the referencer
	 * @param ref
	 *            description of the reference
	 * @param rowDtoClass
	 *            DTO class, it's used to avoid cimpiler confusing between
	 *            TRowDto and TRetDto class, since latter one might be a
	 *            subclass of former one
	 * @param builder
	 *            function that can take id of an referencee and build new
	 *            instance. This will be added to the returned list
	 * @return list of referenced objects or empty list if none found. Changes
	 *         to a list will not affect the database
	 * 
	 *         TODO: THINK: Why not add this functionality?
	 */
	public static <TSrcId, TSrcDto extends HasId<TSrcId>, TId, TRowDto extends HasId<TId>, TRetDto extends TRowDto> List<TRetDto> buildReferencedObjectsList(
			DataSet dataSet, TSrcDto src, Ref ref, Class<TRowDto> rowDtoClass, Function<TRowDto, TRetDto> builder) {
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

			List<TRetDto> ret = new ArrayList<>();
			for (TRowDto row : target.getRows().values()) {
				Object targetValue = targetField.getReadMethod().invoke(row);
				if (ObjectUtils.nullSafeEquals(srcValue, targetValue)) {
					TRetDto rowToAdd = builder.apply(row);
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
