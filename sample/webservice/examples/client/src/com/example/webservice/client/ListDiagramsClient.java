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

/**
 * A concrete implementation for the CIS "ListDesignDiagrams" web service client.
 */
public class ListDiagramsClient extends AbstractClient
{
	public ListDiagramsClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "ListDesignDiagrams";
	}

	protected String getRequestPayload()
	{
		return "<wiringdesign name='Design1' projectname='example1'/>";
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void processResponse(Document responsePayload) throws Exception
	{
		// More design information
		Element design = responsePayload.getDocumentElement();
		logMsg("");
		logMsg("Design name: " + design.getAttribute("name"));
		logMsg("       short description: " + design.getAttribute("shortdesc"));
		logMsg("       revision: " + design.getAttribute("revision"));
		logMsg("       type: " + design.getAttribute("designtype"));
		logMsg("       id: " + design.getAttribute("id"));
		// Retrieve diagram nodes
		NodeList nodes = design.getElementsByTagName("diagram");
		int nbNodes = nodes.getLength();
		logMsg("");
		logMsg("Diagrams: " + nbNodes);
		for (int i=0; i<nbNodes; i++) {
			Element diagram = (Element) nodes.item(i);
			logMsg("> Diagram name: " + diagram.getAttribute("name"));
			logMsg("         type: " + diagram.getAttribute("type"));
			logMsg("         id: " + diagram.getAttribute("id"));
		}
	}
}
