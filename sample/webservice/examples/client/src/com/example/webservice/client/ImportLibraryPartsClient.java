/*
 * Copyright 2007 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example Java web services.
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
package com.example.webservice.client;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;

import com.example.webservice.common.WebServiceUtils;

/**
 * A concrete implementation for the CIS "ImportLibraryParts" web service client.
 */
public class ImportLibraryPartsClient extends AbstractClient
{
	public ImportLibraryPartsClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "ImportLibraryParts";
	}

	protected String getRequestPayload()
	{
		// Retrieve the library data file (previously exported from Capital Library)
		// We assume the program is started from the sample directory $CHS_HOME/WebServiceExamples
		String filePath = "../doc/webservice/data/library_web_service_import_example2.xml";
		StringBuffer xmlString = new StringBuffer();
		BufferedReader in = null;
		try {
			in = new BufferedReader(
					new InputStreamReader(new FileInputStream((filePath)), "UTF8"));
			StringBuffer sb = new StringBuffer();
			String str;
			boolean firstLine = true;
			while ((str = in.readLine()) != null) {
				if (firstLine) {
					str = str.substring(str.indexOf("?>") + 1);
					firstLine = false;
				}
				str = str.replaceAll(" & ", "&amp;");
				sb.append(str);
			}
			xmlString.append("<importservice>");
			xmlString.append("<import content='xml' overwritecodedesc='false' overwritepart='false' scopeimport='false' overwritemode='truncate' >");

			xmlString.append(sb.toString());

			xmlString.append("</import>");
			xmlString.append("</importservice>");
		}
		catch(FileNotFoundException e) {
			throw new IllegalArgumentException("Please provide a valid file path", e);
		}
		catch(IOException e) {
			throw new RuntimeException("Error while reading file", e);
		}
		finally {
			if (in != null) {
				try {
					in.close();
				}
				catch (IOException ignore) {
				}
			}
		}

		return xmlString.toString();
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void processResponse(Document responsePayload) throws Exception
	{
		NodeList nodes = responsePayload.getElementsByTagName("importfeedback");
		System.out.println("Import status: " + ((Element) nodes.item(0)).getAttribute("status"));
	}
}
