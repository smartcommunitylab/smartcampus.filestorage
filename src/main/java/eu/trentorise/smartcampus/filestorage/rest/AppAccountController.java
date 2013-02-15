package eu.trentorise.smartcampus.filestorage.rest;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.filestorage.managers.AppAccountManager;
import eu.trentorise.smartcampus.filestorage.model.AlreadyStoredException;
import eu.trentorise.smartcampus.filestorage.model.AppAccount;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;

@Controller
public class AppAccountController extends RestController {

	private static final Logger logger = Logger
			.getLogger(AppAccountController.class);

	@Autowired
	private AppAccountManager appAccountManager;

	@RequestMapping(method = RequestMethod.POST, value = "/appaccount")
	public @ResponseBody
	AppAccount create(HttpServletRequest request,
			@RequestBody AppAccount appAccount) throws SmartcampusException,
			AlreadyStoredException {

		return appAccountManager.save(appAccount);
	}

	@RequestMapping(method = RequestMethod.PUT, value = "/appaccount/{appName}")
	public @ResponseBody
	AppAccount update(HttpServletRequest request,
			@RequestBody AppAccount appAccount, @PathVariable String appName)
			throws SmartcampusException, NotFoundException {
		return appAccountManager.update(appAccount);
	}

	@RequestMapping(method = RequestMethod.DELETE, value = "/appaccount/{appName}/{appAccountId}")
	public @ResponseBody
	boolean delete(HttpServletRequest request, @PathVariable String appName,
			@PathVariable String appAccountId) throws SmartcampusException {
		appAccountManager.delete(appAccountId);
		return true;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/appaccount/{appName}")
	public @ResponseBody
	List<AppAccount> getAppAccounts(HttpServletRequest request,
			@PathVariable String appName) throws SmartcampusException {

		return appAccountManager.getAppAccounts(appName);
	}

}
