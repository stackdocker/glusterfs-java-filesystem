package org.springframework.integration.samples.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("swxa")
public class SwxaAppProperties {

	private String inifile;

	private int eccindex;

	public String getInifile() {
		return this.inifile;
	}

	public void setInifile(String inifile) {
		this.inifile = inifile;
	}

	public int getEccindex() {
		return this.eccindex;
	}

	public void setEccindex(int eccindex) {
		this.eccindex = eccindex;
	}

}