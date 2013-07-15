package eu.trentorise.smartcampus.filestorage.rest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.filestorage.model.Operation;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.Token;
import eu.trentorise.smartcampus.filestorage.services.ACLService;

@Controller
public class PublicResourceController {

	@Autowired
	ACLService scAcl;

	@RequestMapping(method = RequestMethod.GET, value = "/publicresource/{appName}/{rid}")
	public @ResponseBody
	Token getSharedResource(HttpServletRequest request,
			@PathVariable String appName, @PathVariable String rid)
			throws SmartcampusException, SecurityException {

		return scAcl.getSessionToken(Operation.DOWNLOAD, null, rid, false);
	}
}
