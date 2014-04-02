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

package eu.trentorise.smartcampus.filestorage.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

public class SCExceptionResolver extends DefaultHandlerExceptionResolver {

	private static final Logger logger = Logger
			.getLogger(SCExceptionResolver.class);

	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {

		if (ex instanceof SecurityException) {
			try {
				return resolveSecurityException(response, ex);
			} catch (IOException e) {
				logger.error("Exception resolving SecurityExcetion");
			}
		}

		return super.doResolveException(request, response, handler, ex);
	}

	private ModelAndView resolveSecurityException(HttpServletResponse response,
			Exception exception) throws IOException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
				exception.getMessage());
		return new ModelAndView();
	}
}
