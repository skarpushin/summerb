package org.summerb.approaches.jdbccrud.impl.relations.example;

public class DeviceRow extends RowBase implements HasEnvId {
	private static final long serialVersionUID = -8271872562969504597L;

	public static final String FN_IDENTIFIER = "identifier";
	public static final int FN_IDENTIFIER_SIZE = 64;

	public static final String FN_NAME = "name";
	public static final int FN_NAME_SIZE = 45;

	public static final String FN_SERIAL_NUMBER = "serialNumber";
	public static final int FN_SERIAL_NUMBER_SIZE = 36;

	private long envId;
	private String identifier;
	private String name;
	private String serialNumber;

	@Override
	public long getEnvId() {
		return envId;
	}

	@Override
	public void setEnvId(long envId) {
		this.envId = envId;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
}
