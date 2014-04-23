package eu.trentorise.smartcampus.filestorage.rest;

import javax.servlet.http.HttpServletRequest;

import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;

public class FSController extends SCController {

	@Override
	protected AuthServices getAuthServices() {
		return null;
	}

	protected String getAuthToken(HttpServletRequest request) {
		String token = request.getHeader("Authorization");
		return token != null ? token.substring("Bearer ".length()) : null;
	}
}
