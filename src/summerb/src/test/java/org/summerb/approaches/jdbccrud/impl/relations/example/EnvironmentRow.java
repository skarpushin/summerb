package org.summerb.approaches.jdbccrud.impl.relations.example;

public class EnvironmentRow extends RowBase {
	private static final long serialVersionUID = -8941389959899460385L;

	public static final String FN_NAME = "name";
	public static final int FN_NAME_SIZE = 40;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
