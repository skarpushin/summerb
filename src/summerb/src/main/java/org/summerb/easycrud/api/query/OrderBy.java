package org.summerb.easycrud.api.query;

/**
 * 
 * @author sergey.karpushin
 *
 */
public class OrderBy {
	private static final String ORDER_DESC = "DESC";
	private static final String ORDER_ASC = "ASC";

	private String direction;
	private String fieldName;

	public static OrderBy Asc(String fieldName) {
		OrderBy ret = new OrderBy();
		ret.fieldName = fieldName;
		ret.direction = ORDER_ASC;
		return ret;
	}

	public static OrderBy Desc(String fieldName) {
		OrderBy ret = new OrderBy();
		ret.fieldName = fieldName;
		ret.direction = ORDER_DESC;
		return ret;
	}

	public String getDirection() {
		return direction;
	}

	public boolean isAsc() {
		return ORDER_ASC.equals(direction);
	}

	public String getFieldName() {
		return fieldName;
	}
}
