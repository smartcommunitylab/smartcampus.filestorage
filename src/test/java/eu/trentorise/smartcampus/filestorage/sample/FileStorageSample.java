package eu.trentorise.smartcampus.filestorage.sample;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import eu.trentorise.smartcampus.filestorage.client.Filestorage;
import eu.trentorise.smartcampus.filestorage.client.FilestorageException;
import eu.trentorise.smartcampus.filestorage.client.model.Configuration;
import eu.trentorise.smartcampus.filestorage.client.model.Storage;
import eu.trentorise.smartcampus.filestorage.client.model.StorageType;

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
				"https://vas-dev.smartcampuslab.it/core.filestorage-dev",
				"expbuster-dev");
		final String APPID = "cf88e42f-6564-411e-a784-8dfd98543f10";
		// final String USERID = "3bd338e3-d90b-4b1f-9814-848962324acf";
		final String CLIENT_ID = "345597665704-dusds3ltrijt4su2q4fnde0budir4f2s.apps.googleusercontent.com";
		final String CLIENT_SECRET = "Z62RQWnF_BJ-kk8OJgCEEbBO";
		// final String CLIENT_ID =
		// "142946636439-hk0rj16jmhhnl4vt2stbi9s4fkqo4fsp.apps.googleusercontent.com";
		// final String CLIENT_SECRET = "p_FwiwiTYxSCnyQHtUN5YZuh";
		// final String REFRESH_TOKEN =
		// "1/jYGn1wWG4XBf3kD72LX3g-g3srbCd7uXuvcsTPoq3so";
		// Take the first storage of the app in db
		fs.deleteStorage(APPID);
		Storage s = fs.getStorage(APPID);

		if (s == null) {
			s = new Storage();
			s.setAppId("expbuster-dev");
			s.setName("google storage name");
			s.setStorageType(StorageType.GDRIVE);
			List<Configuration> confs = new ArrayList<Configuration>();
			confs.add(new Configuration("APP_KEY", CLIENT_ID));
			confs.add(new Configuration("APP_SECRET", CLIENT_SECRET));
			s.setConfigurations(confs);
			s = fs.createStorage(APPID, s);
		}
		logger.info("Storage found: " + s.getId() + " TYPE: "
				+ s.getStorageType());

		// // Take the first account of the user in db
		// Account account = fs.getAccountByUser(USERID);
		// if (account == null) {
		// account = new Account();
		// account.setId(new ObjectId().toString());
		// account.setAppId("test");
		// account.setStorageType(StorageType.GDRIVE);
		// account.setName("LocalTestAccount");
		// account.setUserId("1");
		// List<Configuration> confs = new ArrayList<Configuration>();
		// confs.add(new Configuration("REFRESH_TOKEN", REFRESH_TOKEN));
		// account.setConfigurations(confs);
		// account = fs.createAccountByApp(APPID, account);
		// }
		// System.out.println(account.getUserId());
		// System.out.println(s.getId());
		// logger.info("Account found: " + account.getId() + " NAME: "
		// + account.getName() + " TYPE: " + account.getStorageType());
		//
		// File logo1 = new File("C:\\local_storage\\logo1.jpg");
		// File pathToLogo1 = new File("C:\\local_storage\\" +
		// account.getAppId()
		// + "\\" + account.getUserId() + "\\logo1.jpg");
		// File logo2 = new File("C:\\local_storage\\logo2.jpg");
		// File pathToLogo2 = new File("C:\\local_storage\\" +
		// account.getAppId()
		// + "\\" + account.getUserId() + "\\logo2.jpg");
		// File logo3 = new File("C:\\local_storage\\logo3.jpg");
		// File pathToLogo3 = new File("C:\\local_storage\\" +
		// account.getAppId()
		// + "\\" + account.getUserId() + "\\logo3.jpg");
		// File logo4 = new File("C:\\local_storage\\logo4.jpg");
		// File logo6 = new File("C:\\local_storage\\logo6.png");
		// File video = new File("C:\\local_storage\\video.mp4");
		// File doc = new File("c:\\local_storage\\document.txt");
		// File largeFile = new File("C:\\local_storage\\bigfile.arc");
		// File largeImage = new File("C:\\local_storage\\large.jpg");
		// File tinyImage = new File("C:\\local_storage\\tinygif.gif");

		// InputStream is = new FileInputStream(tinyImage);
		//
		// Metadata meta = fs.storeResourceByApp(tinyImage, APPID,
		// account.getId(), false);
		// is.close();

		// InputStream is2 = new FileInputStream(logo3);
		// fs.updateResourceByUser(USERID, meta.getResourceId(), doc);
		// // is2.close();
		// OutputStream os = new FileOutputStream(
		// "c://local_storage//resource.jpeg");
		// fs.getResourceByApp(APPID, meta.getResourceId(), os);
		// os.close();
		// logger.info("Done");
		// OutputStream os2 = new FileOutputStream(
		// "c://local_storage//thumbnail.jpeg");
		// fs.getThumbnailByUser(USERID, meta.getResourceId(), os2);
		// os2.close();
		// fs.deleteResourceByApp(APPID, meta.getResourceId());
		// os.close();
		// // STORE AND UPDATE
		// Metadata metaLogo1 = null;
		// if (!pathToLogo3.exists()) {
		// InputStream is = new FileInputStream(logo1);
		// InputStream is2 = new FileInputStream(logo3);
		// metaLogo1 = fs.storeResourceByUser(logo1, is, USERID,
		// account.getId(), false);
		// is.close();
		// logger.info("Created file: " + metaLogo1.getName());
		// fs.updateResourceByUser(USERID, metaLogo1.getResourceId(), logo3,
		// is2);
		// is2.close();
		// logger.info(String.format("Updated %s with %s",
		// metaLogo1.getName(), logo3.getName()));
		// // THUMBNAIL CREATION
		// OutputStream os = new FileOutputStream(
		// "c:\\local_storage\\thumb_client.jpg");
		// fs.getThumbnailByUser(USERID, metaLogo1.getResourceId(), os);
		// os.close();
		//
		// } else {
		// List<Metadata> metadatas = fs.getAllResourceMetadataByApp(APPID, 0,
		// 100);
		// InputStream is = new FileInputStream(logo4);
		// for (int i = 0; i < metadatas.size(); i++) {
		// if (pathToLogo3.getName().equals(metadatas.get(i).getName())) {
		// fs.updateResourceByApp(APPID, metadatas.get(i)
		// .getResourceId(), logo4, is);
		// logger.info(String.format("Updated %s with %s", metadatas
		// .get(i).getName(), logo4.getName()));
		// }
		// }
		// }
		// // STORE, TOKEN AND DELETE
		// if (!pathToLogo2.exists()) {
		// InputStream is = new FileInputStream(logo2);
		// Metadata metaLogo2 = fs.storeResourceByApp(logo2, is, APPID,
		// account.getId(), false);
		// is.close();
		// logger.info("Created file: " + metaLogo2.getName());
		// Token token = fs.getResourceTokenByApp(APPID,
		// metaLogo2.getResourceId());
		// // THUMBNAIL CREATION
		// OutputStream os = new FileOutputStream(
		// "c:\\local_storage\\thumb_client.jpg");
		// fs.getThumbnailByUser(USERID, metaLogo2.getResourceId(), os);
		// os.close();
		// logger.info("Created token: " + token.getUrl());
		// fs.deleteResourceByUser(USERID, metaLogo2.getResourceId());
		// logger.info("Deleted file: " + metaLogo2.getName());
		// } else {
		// List<Metadata> metadatas = fs.getAllResourceMetadataByApp(APPID, 0,
		// 100);
		// for (int i = 0; i < metadatas.size(); i++) {
		// if (pathToLogo2.getName().equals(metadatas.get(i).getName())) {
		// Token token = fs.getResourceTokenByApp(APPID, metadatas
		// .get(i).getResourceId());
		// logger.info("Created token: " + token.getUrl());
		// fs.deleteResourceByApp(APPID, metadatas.get(i)
		// .getResourceId());
		// logger.info("Deleted file: " + metadatas.get(i).getName());
		// }
		// }
		// }

	}
}
