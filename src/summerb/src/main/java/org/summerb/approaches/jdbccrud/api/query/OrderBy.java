package org.summerb.approaches.jdbccrud.api.query;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((direction == null) ? 0 : direction.hashCode());
		result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderBy other = (OrderBy) obj;
		if (direction == null) {
			if (other.direction != null)
				return false;
		} else if (!direction.equals(other.direction))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		} else if (!fieldName.equals(other.fieldName))
			return false;
		return true;
	}
}
