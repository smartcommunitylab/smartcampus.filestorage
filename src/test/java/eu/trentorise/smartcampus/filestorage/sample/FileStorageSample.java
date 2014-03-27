package eu.trentorise.smartcampus.filestorage.sample;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
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
		final String APPID = "e2084d7b-e982-4649-8a92-cadb8046ddac";
		final String USERID = "5b6353e9-d148-4fc5-8082-7206b14b3fff";

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
		File logo3 = new File("C:\\local_storage\\logo3.jpg");
		File pathToLogo3 = new File("C:\\local_storage\\" + account.getUserId()
				+ "\\logo3.jpg");
		File logo4 = new File("C:\\local_storage\\logo4.jpg");
		File video = new File("C:\\local_storage\\video.mp4");
		File largeFile = new File("C:\\local_storage\\bigfile.arc");

		// STORE AND UPDATE

		if (!pathToLogo1.exists()) {
			InputStream is = new FileInputStream(logo1);
			InputStream is2 = new FileInputStream(logo3);

			Metadata metaLogo1 = fs.storeResourceByUser(logo1, is, USERID,
					account.getId(), false);
			logger.info("Created file: " + metaLogo1.getName());
			fs.updateResourceByUser(USERID, metaLogo1.getResourceId(), logo3,
					is2);
			logger.info(String.format("Updated %s with %s",
					metaLogo1.getName(), logo3.getName()));
		} else {
			List<Metadata> metadatas = fs.getAllResourceMetadataByApp(APPID, 0,
					100);
			InputStream is = new FileInputStream(logo4);
			for (int i = 0; i < metadatas.size(); i++) {
				if (pathToLogo1.getName().equals(metadatas.get(i).getName())) {
					fs.updateResourceByApp(APPID, metadatas.get(i)
							.getResourceId(), logo4, is);
					logger.info(String.format("Updated %s with %s", metadatas
							.get(i).getName(), logo4.getName()));
				}
			}
		}
		// STORE, TOKEN AND DELETE
		if (!pathToLogo2.exists()) {
			InputStream is = new FileInputStream(logo2);
			Metadata metaLogo2 = fs.storeResourceByApp(logo2, is, APPID,
					account.getId(), false);
			logger.info("Created file: " + metaLogo2.getName());
			Token token = fs.getResourceTokenByApp(APPID,
					metaLogo2.getResourceId());
			logger.info("Created token: " + token.getUrl());
			fs.deleteResourceByUser(USERID, metaLogo2.getResourceId());
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
