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
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A concrete implementation for the CIS "BuildListReleaseStatusChange" web service client.
 */
public class BuildListReleaseStatusChangeClient extends AbstractClient
{
	private static final String PROJECT_NAME = "example4";

	// List of all the build list names which will be sent for release level change.
	private static final String[][] BUILD_LIST_NAMES = {
			{"logicBuildList", "Pending"}
	};

	public BuildListReleaseStatusChangeClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "BuildListReleaseLevelChange";
	}

	/*
	   Request pay load consists of all the build lists for which release status is to be changed of a certain project.
	   Request sample is as given below

	   <?xml version="1.0" encoding="UTF-8"?>
       <project name="SampleProject">
          <buildlist name="SampleBuildList01" releasestatus="Pending" type="Logic" />
          <buildlist name="SampleBuildList01" releasestatus="Intermediate" type="Harness" />
          <buildlist name="SampleBuildList01" releasestatus="Released" type="Logic"/>
        </project>

        The same can also be achieved by sending UIDs

       <?xml version="1.0" encoding="UTF-8"?>
       <project id="UID_XXX_01">
          <buildlist id="UID_XXX_02" releasestatus="Pending" type="Logic" />
          <buildlist id="UID_XXX_03" releasestatus="Intermediate" type="Logic" />
          <buildlist id="UID_XXX_04" releasestatus="Released" type="Logic" />
        </project>

	 */
	protected String getRequestPayload()
	{
		return "<project name='"+ PROJECT_NAME +"'>" +
					buildListReleaseLevelChangeRequestXML() +
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
			printBuildListAttributes(childNode);
		}
	}

	private void printBuildListAttributes(Node childNode)
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
	private String buildListReleaseLevelChangeRequestXML()
	{
		StringBuilder request = new StringBuilder();
		for (String[] entry : BUILD_LIST_NAMES) {
			request.append(formDesignXMLFor(entry[0], entry[1]));
		}
		return request.toString();
	}

	private String formDesignXMLFor(String name, String releaseStatus)
	{
		return "<buildlist name='" + name.trim() + "' releasestatus='" + releaseStatus + "' type='Logic' />\n";
	}
}