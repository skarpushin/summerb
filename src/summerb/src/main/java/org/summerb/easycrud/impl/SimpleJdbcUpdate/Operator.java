package org.summerb.easycrud.impl.SimpleJdbcUpdate;

public enum Operator {
	EQUALS("="), LESS_THAN("<"), GREATER_THAN(">");

	private String op;

	private Operator(final String op) {
		this.op = op;
	}

	@Override
	public String toString() {
		return op;
	}
}
