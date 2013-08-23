package eu.trentorise.smartcampus.filestorage.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.services.StorageService;
import eu.trentorise.smartcampus.filestorage.utils.StorageUtils;
import eu.trentorise.smartcampus.resourceprovider.controller.SCController;
import eu.trentorise.smartcampus.resourceprovider.model.AuthServices;

@Controller
public class GetAccountController extends SCController {

	@Autowired
	StorageUtils storageUtils;

	@Autowired
	AuthServices authServices;

	static Map<String, String> cache = new HashMap<String, String>();

	@RequestMapping(method = RequestMethod.GET, value = "/getAccount/{appId}/{storageId}")
	public @ResponseBody
	String retrieveAccount(HttpServletResponse response,
			@PathVariable String storageId) throws IOException {

		// TODO getUser
		try {
			String k = UUID.randomUUID().toString();
			cache.put(k, getUserId());
			StorageService storageService = storageUtils
					.getStorageServiceByStorage(storageId);
			// response.sendRedirect(storageService.getAccountAuthUrl(storageId));
			return "http://localhost:8080/core.filestorage/authorize/" + k;
			// return storageService.getAccountAuthUrl(storageId);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/authorize/{k}")
	public void authorize(HttpSession session, HttpServletResponse response,
			@PathVariable String k) throws SmartcampusException,
			NotFoundException, IOException {
		String userId = cache.remove(k);
		session.setAttribute("userId", userId);
		// TODO storageId 5200baa144aec0a63a353887
		StorageService storageService = storageUtils
				.getStorageServiceByStorage("5200baa144aec0a63a353887");
		response.sendRedirect(storageService
				.getAccountAuthUrl("5200baa144aec0a63a353887"));
	}

	@Override
	protected AuthServices getAuthServices() {
		return authServices;
	}
}
