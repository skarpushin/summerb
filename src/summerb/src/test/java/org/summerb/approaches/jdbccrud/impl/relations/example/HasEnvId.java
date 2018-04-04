package org.summerb.approaches.jdbccrud.impl.relations.example;

public interface HasEnvId {
	public static final String FN_ENV_ID = "envId";

	long getEnvId();

	void setEnvId(long envId);
}
