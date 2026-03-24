/*
 * Copyright 2013 Mentor Graphics Corporation. All Rights Reserved.
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
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;

/**
 * A concrete implementation for the CIS "ReleaseMultipleDesigns" web service client.
 */
public class ReleaseMultipleDesignsClient extends AbstractClient
{
	private static final String PROJECT_NAME = "ReleaseMultipleDesignsProject";
	private static final String RELEASE_STATUS = "Intermediate";

	// List of all the design Ids which will be sent for release level change.
	private static final String[][] DESIGN_IDS = {
			{"UID9fcbb0-13ec0a4fe11-fc9f50cf388b27655868d0e106be46fe", RELEASE_STATUS},
			{"UID9fcbb0-13ec0a4fe1d-fc9f50cf388b27655868d0e106be46fe", RELEASE_STATUS},
			{"UID9fcbb0-13ec0a4fe05-fc9f50cf388b27655868d0e106be46fe", RELEASE_STATUS}
	};

	public ReleaseMultipleDesignsClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "ReleaseMultipleDesigns";
	}

	protected String getRequestPayload()
	{
		return "<project name='"+ PROJECT_NAME +"'>" +
					designXML() +
				"</project>";
	}

	/**
	 * Parses the response and prints all the errors reported( if any )
	 */
	@Override protected void processResponse(Document responsePayload) throws Exception
	{
		super.processResponse(responsePayload);
		printErrorsIfAny(responsePayload);
	}

	private void printErrorsIfAny(Document responsePayload)
	{
		Element rootNode = responsePayload.getDocumentElement();
		// If the project node( root node) has any child elements in the response, it indicates failures for those design elements.
		if(rootNode.getChildNodes().getLength() > 0) {
			printFailures(rootNode.getChildNodes());
		}
		else {
			System.out.println("No errors reported");
		}
	}

	private void printFailures(NodeList childNodes)
	{
		System.out.println("Failures...");
		int childCount = childNodes.getLength();
	    for(int aChild = 0; aChild < childCount; aChild++) {
			Node childNode = childNodes.item(aChild);
			printDesignAttributes(childNode);
		}
	}

	private void printDesignAttributes(Node childNode)
	{
		// get all the attributes 
		NamedNodeMap attMap = childNode.getAttributes();
		
		// print the design id 
		Node idAtt = attMap.getNamedItem("id");
		if (idAtt != null) {
			System.out.println(idAtt.getNodeName() + ": " + idAtt.getNodeValue());
		}

		// print the design name
		Node nameAtt = attMap.getNamedItem("name");
		if (nameAtt != null) {
			System.out.println(nameAtt.getNodeName() + ": " + nameAtt.getNodeValue());
		}

		// print the errors
		Node errorAtt = attMap.getNamedItem("error");
		if (errorAtt != null) {
			System.out.println(errorAtt.getNodeName() + ": " + errorAtt.getNodeValue());
		}
	}

	/**
	 * Returns true since the response is expected in this case.
	 */
	protected boolean isResponseExcepted()
	{
		return true;
	}

	
	/**
	 * Prepares the wiringdesign request element for multiple designs.
	 */
	private String designXML()
	{
		StringBuilder designXML = new StringBuilder();
		for (String[] entry : DESIGN_IDS) {
			designXML.append(formDesignXMLFor(entry[0], entry[1]));
		}
		return designXML.toString();
	}

	private String formDesignXMLFor(String id, String releaseStatus)
	{
		return "<wiringdesign id='" + id.trim() + "' releasestatus='" + releaseStatus + "'/>\n";
	}
}