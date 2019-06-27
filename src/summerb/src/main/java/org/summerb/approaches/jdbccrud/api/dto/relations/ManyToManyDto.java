package org.summerb.approaches.jdbccrud.api.dto.relations;

import org.summerb.approaches.jdbccrud.api.dto.HasAutoincrementId;
import org.summerb.utils.DtoBase;

/**
 * DTO used to describe m2m table. Suites only very simple cases. In case m2m
 * table need to contain custom fields it's better to construct your own DTO
 * instead of trying to subclass this one.
 * 
 * @author sergeyk
 *
 * @param <T1Id>
 *            type of referencer id (who references)
 * @param <T2Id>
 *            type of referencee id (who is being referenced)
 */
public class ManyToManyDto<T1Id, T2Id> implements DtoBase, HasAutoincrementId {
	private static final long serialVersionUID = 2609297133758985L;

	public static final String FN_SRC = "src";
	public static final String FN_DST = "dst";

	private Long id;
	private T1Id src;
	private T2Id dst;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public T1Id getSrc() {
		return src;
	}

	public void setSrc(T1Id a) {
		this.src = a;
	}

	public T2Id getDst() {
		return dst;
	}

	public void setDst(T2Id b) {
		this.dst = b;
	}
}
