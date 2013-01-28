package eu.trentorise.smartcampus.filestorage.utils;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import eu.trentorise.smartcampus.filestorage.managers.UserAccountManager;
import eu.trentorise.smartcampus.filestorage.model.NotFoundException;
import eu.trentorise.smartcampus.filestorage.model.SmartcampusException;
import eu.trentorise.smartcampus.filestorage.model.UserAccount;
import eu.trentorise.smartcampus.filestorage.services.StorageService;
import eu.trentorise.smartcampus.filestorage.services.impl.DropboxStorage;

@Service
public class StorageUtils {

	private static final Logger logger = Logger.getLogger(StorageUtils.class);

	@Autowired
	UserAccountManager accountManager;

	public StorageService getStorageService(String accountId)
			throws SmartcampusException {
		BeanFactory beanFactory = new ClassPathXmlApplicationContext(
				"spring/SpringAppDispatcher-servlet.xml");
		UserAccount account;
		try {
			account = accountManager.findById(accountId);
		} catch (NotFoundException e) {
			logger.error(String.format("Account %s doesn't exist", accountId));
			throw new SmartcampusException("Account doesn't exist");
		}
		StorageService service = null;
		switch (account.getStorage()) {
		case DROPBOX:
			logger.info(String.format(
					"Requested storageService for account %s, type %s",
					accountId, "DROPBOX"));
			service = beanFactory.getBean(DropboxStorage.class);
			break;
		default:
			throw new SmartcampusException("Storage type not supported");
		}

		return service;
	}

}
