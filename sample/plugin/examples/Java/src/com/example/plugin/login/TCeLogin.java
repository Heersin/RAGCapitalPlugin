/**
 * Copyright 2007 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example Java or Java Script plugins.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, except as expressly 
 * authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */
package com.example.plugin.login;

import com.mentor.chs.plugin.authentication.IXLogin;
import com.mentor.chs.plugin.authentication.IXAuthenticationResponse;
import com.example.plugin.BasePlugin;
import com.example.plugin.login.aiws.AIWebServicePortType;
import com.example.plugin.login.aiws.AIWebServiceLocator;
import com.example.plugin.login.aiws.Identification;

import java.util.Properties;
import java.net.URL;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.axis.client.Stub;

/**
 * Sample plugin for TCe authentication.
 * Login credentials are checked with TCe before logging into CHS Application.
 */
public class TCeLogin extends BasePlugin implements IXLogin {
	private AIWebServicePortType m_webservice;
	private static final String AISERVICE_CONTEXT = "/tc/aiws/aiwebservice";
	private static final int AILOGIN_TIMEOUT = 300 * 60 * 1000;
	private String m_url = "http://127.0.0.1:7001";

	public TCeLogin() {
		super(
				"TCe Login",
				"1",
				"TCe Login credentials are taken while logging into CHS Application.");
		Properties property = new Properties();
		String chsHome = System.getenv("chs_home");
		File file = new File(chsHome + File.separator + "plugins" + File.separator + "TCESignon.properties");
		if( file.exists()){
			try{
				FileInputStream fileStream = new FileInputStream(file);
				property.load( fileStream);
			}
			catch(FileNotFoundException e){
			}
			catch(IOException e){
			}
			String url = property.getProperty("url");
			if(url != null && url.length() > 0){
				m_url = url;
			}
		}
		m_url += AISERVICE_CONTEXT;
	}

	public IXAuthenticationResponse authenticate(String username, String password) {
		String errorMesg=null;
		if (m_webservice == null) {
			AIWebServiceLocator locator = new AIWebServiceLocator();
			try {
				m_webservice = locator.getAIWebService(new URL(m_url));
				Stub stub = (Stub) m_webservice;
				stub.setUsername(username);
				stub.setPassword(password);
				stub._setProperty("javax.xml.rpc.session.maintain", Boolean.TRUE);
				stub.setTimeout(AILOGIN_TIMEOUT);
			}
			catch (Exception e) {
				e.printStackTrace();
				errorMesg = e.getMessage();
			}
		}
		try {
			if(m_webservice != null){
				Identification sessionId = m_webservice.login("NotUsed", username, password);
				if (sessionId == null) {
					errorMesg = "Login Unsuccessful for user : " + username;
				}
			}
		} catch (Exception e) {
			m_webservice = null;
			e.printStackTrace();
			errorMesg = e.getMessage();
		}
		final String chsUserName = username;
		final boolean isAuthentic = (errorMesg == null);
		final String authErrorMsg = errorMesg;

		return new IXAuthenticationResponse() {

			public String getErrorMessage() {
				return authErrorMsg;
			}

			public String getCHSUserName() {
				return chsUserName;
			}

			public boolean isAuthenticUser() {
				return isAuthentic;
			}
		};
	}
}
