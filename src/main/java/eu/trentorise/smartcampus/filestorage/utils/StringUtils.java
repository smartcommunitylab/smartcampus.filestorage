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
package eu.trentorise.smartcampus.filestorage.utils;

import javax.servlet.http.HttpServletRequest;

public class StringUtils {

	public static boolean isNullOrEmpty(String input, boolean emptyIfBlank) {
		return input == null
				|| ((emptyIfBlank) ? input.trim().length() == 0 : input
						.length() == 0);
	}

	/**
	 * Given HTTP request, reconstruct the app root url
	 * @param request
	 * @return
	 */
	public static String appURL(HttpServletRequest request) {
		return request.getRequestURL().substring(0, request.getRequestURL().indexOf(request.getContextPath())+request.getContextPath().length());
	}
}
