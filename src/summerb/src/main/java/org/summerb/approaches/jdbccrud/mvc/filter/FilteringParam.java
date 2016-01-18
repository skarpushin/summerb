package org.summerb.approaches.jdbccrud.mvc.filter;

public class FilteringParam {
	public static final String CMD_EQUALS = "equal";
	public static final String CMD_NOT_EQUALS = "not.equal";
	public static final String CMD_CONTAIN = "contain";
	public static final String CMD_NOT_CONTAIN = "not.contain";
	public static final String CMD_IN = "in";
	public static final String CMD_NOT_IN = "not.in";
	public static final String CMD_BETWEEN = "between";
	public static final String CMD_NOT_BETWEEN = "not.between";
	public static final String CMD_LESS = "less";
	public static final String CMD_LESS_OR_EQUAL = "less.or.equal";
	public static final String CMD_GREATER = "greater";
	public static final String CMD_GREATER_OR_EQUAL = "greater.or.equal";

	private String command;
	private String[] values;

	public static FilteringParam build(String command, String value) {
		FilteringParam ret = new FilteringParam();
		ret.setCommand(command);
		ret.setValues(new String[] { value });
		return ret;
	}

	public static FilteringParam build(String command, long value) {
		FilteringParam ret = new FilteringParam();
		ret.setCommand(command);
		ret.setValues(new String[] { String.valueOf(value) });
		return ret;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String[] getValues() {
		return values;
	}

	public void setValues(String[] values) {
		this.values = values;
	}
}
