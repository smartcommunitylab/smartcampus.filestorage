package eu.trentorise.smartcampus.filestorage.model;

// Used to get access to a resource in the local storage
public class LocalResource {
	/**
	 * Id of the LocalResource
	 */
	private String id;
	/**
	 * Date of the tocken expiration
	 */
	private Long date;
	/**
	 * Url of the resource
	 */
	private String url;

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
