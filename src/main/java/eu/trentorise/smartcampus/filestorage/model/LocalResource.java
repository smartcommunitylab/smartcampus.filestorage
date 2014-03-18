package eu.trentorise.smartcampus.filestorage.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

// Used to get access to a resource in the local storage

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class LocalResource {
	/**
	 * Id of the LocalResource
	 */
	private String id;
	/**
	 * Date of the token expiration
	 */
	private Long date;
	/**
	 * Url of the resource
	 */
	private String url;
	/**
	 * Id of the resource
	 */
	private String rid;

	public String getResourceId() {
		return rid;
	}

	public void setResourceId(String rid) {
		this.rid = rid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
