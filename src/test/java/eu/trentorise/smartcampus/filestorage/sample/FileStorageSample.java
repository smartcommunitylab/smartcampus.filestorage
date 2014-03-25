package eu.trentorise.smartcampus.filestorage.sample;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import eu.trentorise.smartcampus.filestorage.client.Filestorage;
import eu.trentorise.smartcampus.filestorage.client.FilestorageException;
import eu.trentorise.smartcampus.filestorage.client.model.Account;
import eu.trentorise.smartcampus.filestorage.client.model.Metadata;
import eu.trentorise.smartcampus.filestorage.client.model.Storage;
import eu.trentorise.smartcampus.filestorage.client.model.StorageType;
import eu.trentorise.smartcampus.filestorage.client.model.Token;

public class FileStorageSample {
	private static final Logger logger = Logger
			.getLogger(FileStorageSample.class);

	/**
	 * @param args
	 * @throws FilestorageException
	 * @throws SecurityException
	 */

	public static void main(String[] args) throws SecurityException,
			FilestorageException, Exception {
		Filestorage fs = new Filestorage(
				"http://localhost:8080/core.filestorage", "test");
		final String APPID = "93d0aada-a476-4f1a-8f77-1f05e97cec1c";
		final String USERID = "423dabd7-cd68-4e90-bd6d-bf086b4dc5dc";

		// Take the first storage of the app in db
		Storage s = fs.getStorage(APPID);
		if (s == null) {
			s = new Storage();
			s.setAppId("test");
			s.setName("local storage name");
			s.setStorageType(StorageType.LOCAL);
			s = fs.createStorage(APPID, s);
		}
		logger.info("Storage found: " + s.getId() + " TYPE: "
				+ s.getStorageType());

		// Take the first account of the user in db
		Account account = fs.getAccountByUser(USERID);
		if (account == null) {
			account = new Account();
			account.setId(new ObjectId().toString());
			account.setAppId("test");
			account.setStorageType(StorageType.LOCAL);
			account.setName("LocalTestAccount");
			account.setUserId("1");
			account = fs.createAccountByApp(APPID, account);
		}
		logger.info("Account found: " + account.getId() + " NAME: "
				+ account.getName() + " TYPE: " + account.getStorageType());

		File logo1 = new File("C:\\local_storage\\logo1.jpg");
		File pathToLogo1 = new File("C:\\local_storage\\" + account.getUserId()
				+ "\\logo1.jpg");
		File logo2 = new File("C:\\local_storage\\logo2.jpg");
		File pathToLogo2 = new File("C:\\local_storage\\" + account.getUserId()
				+ "\\logo2.jpg");
		File logo6 = new File("C:\\local_storage\\logo6.png");
		File video = new File("C:\\local_storage\\video.mp4");
		// STORE, TOKEN AND DELETE
		if (!pathToLogo2.exists()) {
			Metadata metaLogo2 = fs.storeResourceByApp(logo2, APPID,
					account.getId(), false);
			logger.info("Created file: " + metaLogo2.getName());
			Token token = fs.getResourceTokenByApp(APPID,
					metaLogo2.getResourceId());
			logger.info("Created token: " + token.getUrl());
			fs.deleteResourceByApp(APPID, metaLogo2.getResourceId());
			logger.info("Deleted file: " + metaLogo2.getName());
		} else {
			List<Metadata> metadatas = fs.getAllResourceMetadataByApp(APPID, 0,
					100);
			for (int i = 0; i < metadatas.size(); i++) {
				if (pathToLogo2.getName().equals(metadatas.get(i).getName())) {
					Token token = fs.getResourceTokenByApp(APPID, metadatas
							.get(i).getResourceId());
					logger.info("Created token: " + token.getUrl());
					fs.deleteResourceByApp(APPID, metadatas.get(i)
							.getResourceId());
					logger.info("Deleted file: " + metadatas.get(i).getName());
				}
			}
		}

	}
}
