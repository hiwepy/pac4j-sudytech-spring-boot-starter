/*
 * Copyright (c) 2018, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.pac4j.spring.boot.sudytech.credentials.extractor;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.pac4j.core.context.ContextHelper;
import org.pac4j.core.context.JEEContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.spring.boot.sudytech.credentials.SudytechWxCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SudytechWxParameterExtractor implements CredentialsExtractor<SudytechWxCredentials> {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private boolean supportGetRequest = true;
	private boolean supportPostRequest;
	private String charset = StandardCharsets.UTF_8.name();

	public SudytechWxParameterExtractor() {
		this(false, true, StandardCharsets.UTF_8.name());
	}

	public SudytechWxParameterExtractor(String charset) {
		this(false, true, charset);
	}

	public SudytechWxParameterExtractor(boolean supportGetRequest, boolean supportPostRequest, String charset) {
		this.supportGetRequest = supportGetRequest;
		this.supportPostRequest = supportPostRequest;
		this.charset = charset;
	}

	@Override
	public Optional<SudytechWxCredentials> extract(WebContext context) {

		logger.debug("supportGetRequest: {}", this.supportGetRequest);
		logger.debug("supportPostRequest: {}", this.supportPostRequest);

		if (ContextHelper.isGet(context) && !supportGetRequest) {
			throw new CredentialsException("GET requests not supported");
		} else if (ContextHelper.isPost(context) && !supportPostRequest) {
			throw new CredentialsException("POST requests not supported");
		}

		JEEContext jeeContext = (JEEContext) context;

		HttpServletRequest _request = jeeContext.getNativeRequest();
		HttpSession httpSess = _request.getSession();
		String loginName = (String) httpSess.getAttribute("mids_loginName");

		String openid = _request.getParameter("mids.openid");

		logger.debug("loignName : {}", loginName);
		logger.debug("wxOpenId : {}", openid);

		return Optional.of(new SudytechWxCredentials(loginName, openid));

	}

	@Override
	public String toString() {
		return CommonHelper.toNiceString(this.getClass(), "supportGetRequest", supportGetRequest, "supportPostRequest",
				supportPostRequest, "charset", charset);
	}

	public boolean isSupportGetRequest() {
		return supportGetRequest;
	}

	public void setSupportGetRequest(boolean supportGetRequest) {
		this.supportGetRequest = supportGetRequest;
	}

	public boolean isSupportPostRequest() {
		return supportPostRequest;
	}

	public void setSupportPostRequest(boolean supportPostRequest) {
		this.supportPostRequest = supportPostRequest;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

}
