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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A concrete implementation for the CIS "ImportBuildLists" web service client.
 */

public class ImportBuildListsClient extends AbstractClient
{

	@Override protected String getWebServiceName()
	{
		return "ImportBuildLists";
	}

	@Override protected String getRequestPayload()
	{
		Path filePath = Paths.get("../doc/webservice/data/ImportBuildListsWebServiceSampleRequest.xml");
		String payload = "";
		try {
			byte[] fileAsBytes = Files.readAllBytes(filePath);
			payload = new String(fileAsBytes);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return payload;
	}

	@Override protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void processResponse(Document responsePayload)
	{
		NodeList nodes = responsePayload.getElementsByTagName("buildlist");
		int nbNodes = nodes.getLength();
		logMsg("");
		if (nbNodes > 0) {
			//there were some errors on Build List import
			logMsg("Build List(s) import failed due to the following errors:");
			for (int i = 0; i < nbNodes; i++) {
				Element buildList = (Element) nodes.item(i);
				String buildListName = buildList.getAttribute("name");
				String buildListType = buildList.getAttribute("type");
				String error = buildList.getAttribute("error");
				logMsg(">Import of Build List:" + buildListName + " of type:" + buildListType +
						" failed due to the error:" + error);
			}
		}
		else {
			logMsg("Build List(s) imported successfully.");
		}
	}
}
