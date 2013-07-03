package eu.trentorise.smartcampus.filestorage.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "storages")
@XmlAccessorType(XmlAccessType.FIELD)
public class ListStorage {

	@XmlElement(name = "storage")
	private List<Storage> storages;

	public List<Storage> getStorages() {
		return storages;
	}

	public void setStorages(List<Storage> storages) {
		this.storages = storages;
	}

}
