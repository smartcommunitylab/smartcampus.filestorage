package eu.trentorise.smartcampus.filestorage.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AppAccount {
	private String id;
	private String appName;
	private String appAccountName;
	private StorageType storageType;
	private List<Configuration> configurations;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getAppAccountName() {
		return appAccountName;
	}

	public void setAppAccountName(String appAccountName) {
		this.appAccountName = appAccountName;
	}

	public StorageType getStorageType() {
		return storageType;
	}

	public void setStorageType(StorageType storageType) {
		this.storageType = storageType;
	}

	public List<Configuration> getConfigurations() {
		return configurations;
	}

	public void setConfigurations(List<Configuration> configurations) {
		this.configurations = configurations;
	}

}
