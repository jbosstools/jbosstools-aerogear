package org.jboss.tools.aerogear.hybrid.core.plugin;

public class CordovaPluginVersion extends CordovaPluginInfo {
	private String versionNumber;
	private String distributionTarball;
	private String distributionSHASum;
	private String license;
	

	public String getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
	}

	public String getDistributionTarball() {
		return distributionTarball;
	}

	public void setDistributionTarball(String distributionTarball) {
		this.distributionTarball = distributionTarball;
	}

	public String getDistributionSHASum() {
		return distributionSHASum;
	}

	public void setDistributionSHASum(String distributionSHASum) {
		this.distributionSHASum = distributionSHASum;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}
	
}
