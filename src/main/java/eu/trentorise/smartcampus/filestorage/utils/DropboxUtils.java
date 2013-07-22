package eu.trentorise.smartcampus.filestorage.utils;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;

public class DropboxUtils {
	private DropboxAPI<?> sourceClient;

	public DropboxUtils(AppKeyPair appToken, AccessTokenPair userToken,
			AccessType accessType) {
		WebAuthSession sourceSession = new WebAuthSession(appToken, accessType,
				userToken);
		sourceClient = new DropboxAPI<WebAuthSession>(sourceSession);
	}

}
