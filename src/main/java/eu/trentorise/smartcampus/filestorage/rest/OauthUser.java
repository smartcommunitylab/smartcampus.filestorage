package eu.trentorise.smartcampus.filestorage.rest;

import eu.trentorise.smartcampus.User;

public class OauthUser extends User {

	private static final long serialVersionUID = -3206202460029379201L;
	private String userToken;
	private String clientToken;

	public OauthUser(User user) {
		setId(user.getId());
		setSocialId(user.getSocialId());
		setName(user.getName());
		setSurname(user.getSurname());
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}

	public String getClientToken() {
		return clientToken;
	}

	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}

}
