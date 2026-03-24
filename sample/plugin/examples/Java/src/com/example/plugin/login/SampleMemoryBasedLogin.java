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

import java.util.HashMap;
import java.util.Map;

/**
 * Sample plugin for authentication.
 * Login credentials are taken from memory while logging into CHS Application.
 */
public class SampleMemoryBasedLogin implements IXLogin {

	private Map<String, String> users;
	private String errorMsg = "Default Error Message";

	/**
	 * The constructor initializes the map with user credentials
	 */
	public SampleMemoryBasedLogin() {
		users = new HashMap<String, String>();
		users.put("strythall", "strythall");
		users.put("rkeane", "hardman");
	}

	/**
	 * This method authenticates the given username and password strings
	 * @param username The user name string
	 * @param password The password string
	 * @return IXAuthenticationResponse type
	 */
	public IXAuthenticationResponse authenticate(final String username, final String password) {
		boolean isAuthentic = false;

		// get the password from memory and compare
		String pwd = users.get(username);
		if (pwd != null) {
			if (pwd.equals(password)){
				 isAuthentic = true;
			}
		}
		final boolean success = isAuthentic;

		// construct the response object and return
		return new IXAuthenticationResponse() {

			public String getErrorMessage() {
				return errorMsg;
			}

			public String getCHSUserName() {
				return username;
			}

			public boolean isAuthenticUser() {
				return success;
			}
		};
	}

	/**
	 * Return the description of the plugin
	 * @return String type - description of the plugin
	 */
	public String getDescription() {
		return "Login credentials are taken from memory while logging into CHS Application";
	}

	/**
	 * Return the name of the plugin
	 * @return String type - name of the plugin
	 */
	public String getName() {
		return "Authentication";
	}

	/**
	 * Return the version of the plugin
	 * @return String type - version of the plugin
	 */
	public String getVersion() {
		return "1.0";
	}
}
