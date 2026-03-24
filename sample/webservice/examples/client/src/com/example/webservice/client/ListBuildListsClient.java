/*
 * Copyright 2014 Mentor Graphics Corporation. All Rights Reserved.
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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A concrete implementation for the CIS "ListBuildLists" web service client.
 */

public class ListBuildListsClient extends AbstractClient
{
	public ListBuildListsClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "ListBuildListsService";
	}

	protected String getRequestPayload()
	{
		return "<project name='example4'>" +
				"<buildlisttype>logic</buildlisttype>" +
				"<buildlisttype>harness</buildlisttype>" +
				"</project>";
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void processResponse(Document responsePayload) throws Exception
	{
		// Retrieve design nodes
		NodeList nodes = responsePayload.getElementsByTagName("buildlist");
		int nbNodes = nodes.getLength();
		logMsg("");
		logMsg("Build lists: " + nbNodes);
		for (int i=0; i<nbNodes; i++) {
			Element design = (Element) nodes.item(i);
			logMsg("> Build list name: " + design.getAttribute("name"));
			logMsg("         short description: " + design.getAttribute("description"));
			logMsg("         release level: " + design.getAttribute("releaselevel"));
			logMsg("         type: " + design.getAttribute("type"));
			logMsg("         id: " + design.getAttribute("id"));
			logMsg("         modified: " + design.getAttribute("modified"));
			logMsg("         effectivityvalue: " + design.getAttribute("effectivityvalue"));
			logMsg("         build list maintenance: " + design.getAttribute("buildlistmaintenance"));
			logMsg("         folder path: " + design.getAttribute("folderpath"));
		}
	}
}
