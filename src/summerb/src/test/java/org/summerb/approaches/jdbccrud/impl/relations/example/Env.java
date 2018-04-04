package org.summerb.approaches.jdbccrud.impl.relations.example;

import java.util.List;

public class Env extends EnvironmentRow {
	private static final long serialVersionUID = -4866790907868264162L;

	private List<Device> devices;

	public Env() {
	}

	public List<Device> getDevices() {
		return devices;
	}

	public void setDevices(List<Device> devices) {
		this.devices = devices;
	}
}
