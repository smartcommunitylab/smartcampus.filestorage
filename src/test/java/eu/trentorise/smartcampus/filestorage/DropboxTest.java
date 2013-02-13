/**
 *    Copyright 2012-2013 Trento RISE
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.trentorise.smartcampus.filestorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.dropbox.client2.session.WebAuthSession;

import eu.trentorise.smartcampus.filestorage.utils.DropboxUtils;
import eu.trentorise.smartcampus.filestorage.utils.TestUtils;

public class DropboxTest {

	@Test
	public void listFiles() throws DropboxException {

		AppKeyPair app = new AppKeyPair(DropboxUtils.appkey,
				DropboxUtils.appsecret);

		AccessTokenPair token = new AccessTokenPair(DropboxUtils.userkey,
				DropboxUtils.usersecret);

		WebAuthSession sourceSession = new WebAuthSession(app,
				Session.AccessType.APP_FOLDER, token);
		DropboxAPI<?> sourceClient = new DropboxAPI<WebAuthSession>(
				sourceSession);

		Entry entries = sourceClient.metadata("/", 0, null, true, null);
		for (Entry e : entries.contents) {
			System.out.println(e.path);
		}

		sourceSession.unlink();
	}

	@Test
	public void upload() throws DropboxException, IOException {
		AppKeyPair app = new AppKeyPair(DropboxUtils.appkey,
				DropboxUtils.appsecret);

		AccessTokenPair token = new AccessTokenPair(DropboxUtils.userkey,
				DropboxUtils.usersecret);

		WebAuthSession sourceSession = new WebAuthSession(app,
				Session.AccessType.APP_FOLDER, token);
		DropboxAPI<?> sourceClient = new DropboxAPI<WebAuthSession>(
				sourceSession);

		File f = TestUtils.getSampleTextFile("sample file");
		byte[] content = FileUtils.readFileToByteArray(f);
		InputStream in = new ByteArrayInputStream(content);
		sourceClient.putFile("/test.txt", in, content.length, null, null);
		sourceClient.delete("/test.txt");
		sourceSession.unlink();
	}

	@Test
	public void update() throws IOException, DropboxException,
			URISyntaxException {
		AppKeyPair app = new AppKeyPair(DropboxUtils.appkey,
				DropboxUtils.appsecret);

		AccessTokenPair token = new AccessTokenPair(DropboxUtils.userkey,
				DropboxUtils.usersecret);

		WebAuthSession sourceSession = new WebAuthSession(app,
				Session.AccessType.APP_FOLDER, token);
		DropboxAPI<?> sourceClient = new DropboxAPI<WebAuthSession>(
				sourceSession);

		File f = TestUtils.getSampleTextFile("sample file updated");
		byte[] content = FileUtils.readFileToByteArray(f);
		InputStream in = new ByteArrayInputStream(content);
		sourceClient.putFileOverwrite("/test.txt", in, content.length, null);

	}
}
