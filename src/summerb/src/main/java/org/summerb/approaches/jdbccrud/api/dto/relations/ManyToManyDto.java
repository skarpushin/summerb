package org.summerb.approaches.jdbccrud.api.dto.relations;

import org.summerb.approaches.jdbccrud.api.dto.HasAutoincrementId;
import org.summerb.approaches.jdbccrud.common.DtoBase;

public class ManyToManyDto<T1Id, T2Id> extends DtoBase implements HasAutoincrementId {
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
