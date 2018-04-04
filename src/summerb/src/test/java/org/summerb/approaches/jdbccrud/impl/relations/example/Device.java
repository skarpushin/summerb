package org.summerb.approaches.jdbccrud.impl.relations.example;

public class Device extends DeviceRow {
	private static final long serialVersionUID = -8296540588721148743L;

	private Env env;

	public Device() {
	}

	public Env getEnv() {
		return env;
	}

	public void setEnv(Env nev) {
		this.env = nev;
	}
}
