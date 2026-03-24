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

import com.mentor.chs.plugin.authentication.IXAuthenticationResponse;
import com.mentor.chs.plugin.authentication.IXLogin;

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
 * Sample plugin for authentication. Demonstrates how to delegate authentication to an LDAP server
 */
public class SimpleLDAPLogin implements IXLogin
{

	private static String errorMsg = "Default Error Message";
	private static String location = "dc=example,dc=com";
	private static String ldapProvider = "ldap://127.0.0.1/";
	private String m_userName = null;
	private static final byte[] keyValue = new byte[]{'T', 'h', 'e', 'C', 'l', 'i', 'e', 'n', 't','A','u',
				't','h','K', 'e', 'y'};
	private static final String LOG_MESSAGE="[Server Authentication Plugin] ";
	
	/**
	 * This method authenticates the given username and password strings
	 *
	 * @param username The user name string
	 * @param password The password string
	 *
	 * @return IXAuthenticationResponse type
	 */
	public IXAuthenticationResponse authenticate(final String username, final String password)
	{
		System.out.println(LOG_MESSAGE +
						"Server Authentication plugin received Ticket : " + password );
		// Set up environment for creating initial context
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapProvider);

		boolean isAuthentic = false;
		try {
			//Plugin Assumes usename and pass word are send as username@password in password filed as encrypted format
			String userNameAndPassword = decryptString(password);
			int userIndex = userNameAndPassword.indexOf("@");
			m_userName = userNameAndPassword.substring(0, userIndex);
			String m_password = userNameAndPassword.substring(userIndex + 1, userNameAndPassword.length());
			if (!m_userName.equalsIgnoreCase("system") && !m_password.equalsIgnoreCase("manager")) {

				System.out.println(LOG_MESSAGE + "Validating ticket with LDAP");
				// Authenticate with user and password
				env.put(Context.SECURITY_PRINCIPAL, "cn=" + m_userName + ',' + location);
				env.put(Context.SECURITY_CREDENTIALS, m_password);

				// Create the initial context and in doing so authenticate the user
				DirContext context = new InitialDirContext(env);
				// if we arrive at this point then we have successfully authenticated the user

				isAuthentic = true;
               	context.close();
				System.out.println(LOG_MESSAGE + "Successfully validated ticket with LDAP");
			}
			else{
				System.out.println(LOG_MESSAGE + "Its a super user so not validating with LDAP");
				isAuthentic = true;
			}
		}
		catch (AuthenticationException e) {
			// As we have arrived here then the authentication has failed
			System.out.println("failed to authenticate user: " + username + " with LDAP ");
			isAuthentic = false;
		}
		catch (NamingException e) {
			e.printStackTrace();
		}

		final boolean success = isAuthentic;

		// construct the response object and return
		return new IXAuthenticationResponse()
		{

			public String getErrorMessage()
			{
				return errorMsg;
			}

			public String getCHSUserName()
			{
				if (success) {
					System.out.println(LOG_MESSAGE + "Returning Capital User " + m_userName);
				}
				return m_userName;
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
		return "Login credentials are taken from an LDAP server while logging into CHS Application";
	}

	/**
	 * Return the name of the plugin
	 *
	 * @return String type - name of the plugin
	 */
	public String getName()
	{
		return "Authentication";
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

	public String decryptString(String str)
	{
		try {
			SecretKey key = new SecretKeySpec(keyValue, "AES");
			Cipher deCipher = Cipher.getInstance("AES");
			deCipher.init(Cipher.DECRYPT_MODE, key);
			byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
			byte[] utf8 = deCipher.doFinal(dec);
			return new String(utf8, "UTF-8");
		}
		catch (Exception e) {
			System.out.println("Failed in Decryption");
		}
		return null;
	}
}


