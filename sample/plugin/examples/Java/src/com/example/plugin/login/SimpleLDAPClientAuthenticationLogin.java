/**
 * Copyright 2013 Mentor Graphics Corporation. All Rights Reserved.
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

import com.mentor.chs.plugin.authentication.IXClientAuthenticationResponse;
import com.mentor.chs.plugin.authentication.IXClientLogin;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.Hashtable;

/**
 * Sample plugin for client authentication. Demonstrates how to delegate authentication to an LDAP server
 */
public class SimpleLDAPClientAuthenticationLogin implements IXClientLogin
{

	private static String errorMsg = "Default Error Message";
	private static String location = "dc=example,dc=com";
	private static String ldapProvider = "ldap://127.0.0.1/";
	private String m_userName = null;
	private String m_password = null;
	private static final byte[] keyValue = new byte[]{'T', 'h', 'e', 'C', 'l', 'i', 'e', 'n', 't', 'A', 'u',
			't', 'h', 'K', 'e', 'y'};
	private boolean isAuthentic = false;
	private static final String LOG_MESSAGE="[Client Authentication Plugin] ";
	/**
	 * This method authenticates the given username and password strings
	 *
	 * @param username The user name string
	 * @param password The password string
	 *
	 * @return IXClientAuthenticationResponse type
	 */
	public IXClientAuthenticationResponse authenticate(final String username, final String password)
	{
		System.out.println(LOG_MESSAGE +
				"Client Authentication plugin received user name : " + username + " password: " + "****" +
						" from Capital Client Application");

		if (username.equalsIgnoreCase("system") && password.equalsIgnoreCase("manager")) {
			System.out.println(LOG_MESSAGE + "This is a super user. Hence not authenticating with LDAP ");
			m_userName = username;
			m_password = password;
			isAuthentic = true;
		}
		else {
			System.out.println(LOG_MESSAGE + "Validating user name and password with LDAP");
			// Set up environment for creating initial context
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, ldapProvider);
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			// Authenticate with user and password
			env.put(Context.SECURITY_PRINCIPAL, "cn=" + username + ',' + location);
			env.put(Context.SECURITY_CREDENTIALS, password);

			try {
				// Create the initial context and in doing so authenticate the user
				DirContext context = new InitialDirContext(env);
				// if we arrive at this point then we have successfully authenticated the user
				isAuthentic = true;
				m_userName = username;
				m_password = password;
				context.close();
				System.out.println(LOG_MESSAGE + "Successfully validated user name and password with LDAP");
			}
			catch (AuthenticationException e) {
				// As we have arrived here then the authentication has failed
				System.out.println(LOG_MESSAGE + "Failed to authenticate user: " + username + " with LDAP ");
				isAuthentic = false;
			}
			catch (NamingException e) {
				e.printStackTrace();
			}
		}

		final boolean success = isAuthentic;

		// construct the response object and return
		return new IXClientAuthenticationResponse()
		{

			@Override public String getIdentifier()
			{
				return " ";
			}

			@Override public String getTicket()
			{
				String ticket = encryptString(m_userName + "@" + m_password);
				if(isAuthentic){
					System.out.println(LOG_MESSAGE + "Returning ticket " + ticket);
				}
				return ticket;
			}

			public String getErrorMessage()
			{
				return errorMsg;
			}

			public boolean isAuthenticUser()
			{
				return success;
			}
		};
	}

	/**
	 * Return the description of the plugin
	 *
	 * @return String type - description of the plugin
	 */
	public String getDescription()
	{
		return "Client Login credentials are validated from an LDAP server and mapped authentication is returned";
	}

	/**
	 * Return the name of the plugin
	 *
	 * @return String type - name of the plugin
	 */
	public String getName()
	{
		return "Client Authentication";
	}

	/**
	 * Return the version of the plugin
	 *
	 * @return String type - version of the plugin
	 */
	public String getVersion()
	{
		return "1.0";
	}

	/**
	 * Encrypts username@password string which is returned via getTicket API
	 *
	 * @param str username@password string
	 *
	 * @return encrypted string
	 */
	public String encryptString(String str)
	{
		try {
			SecretKey key = new SecretKeySpec(keyValue, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] byteString = str.getBytes("UTF-8");
			byte[] encodedByte = cipher.doFinal(byteString);

			return new sun.misc.BASE64Encoder().encode(encodedByte);
		}
		catch (Exception e) {
			System.out.println("Failed in Encryption");
		}
		return null;
	}
}