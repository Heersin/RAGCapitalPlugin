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
package com.example.webservice.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.example.webservice.common.WebServiceUtils;

import java.util.Date;

/**
 * A web service that implements the "Logical Design Attributes Update" CHS hook
 */
public class DesignAttributesUpdateHookService extends AbstractService
{

	public Document invoke(Document requestPayload) throws Exception
	{
		// Extract and dump design information
		Element root = requestPayload.getDocumentElement();
		NodeList nodes = root.getElementsByTagName("designattributesdata");
		Element design = (Element) nodes.item(0);
		System.out.println();
		System.out.println("Design Attributes Update event:");
		System.out.println("  name               = " + design.getAttribute("name"));
		System.out.println("  revision           = " + design.getAttribute("revision"));
		System.out.println("  short description  = " + design.getAttribute("shortdescription"));
		System.out.println("  description        = " + design.getAttribute("description"));
		System.out.println("  type               = " + design.getAttribute("designtype"));
		System.out.println("  part number        = " + design.getAttribute("partnumber"));
		System.out.println("  release status     = " + design.getAttribute("releaselevel"));
		System.out.println("  abstraction        = " + design.getAttribute("designabstraction"));
		System.out.println("  domain             = " + design.getAttribute("domain"));
		System.out.println("  applicable options = " + design.getAttribute("applicableoptions"));

		nodes = root.getElementsByTagName("designattributescontext");
		Element context = (Element) nodes.item(0);
		System.out.println("Design Attributes Update context:");
		System.out.println("  trigger            = " + context.getAttribute("dialogueInvocationCause"));
		System.out.println("  mode               = " + context.getAttribute("webSericeInvocationCause"));

		// Add or modify "webservice validation" property
		Element property = null;
		nodes = design.getElementsByTagName("property");
		for (int i = 0; property == null && i < nodes.getLength(); i++) {
			Element prop = (Element) nodes.item(i);
			if ("webservice validation".equals(prop.getAttribute("name"))) {
				property = prop;
			}
		}
		if (property == null) {
			property = requestPayload
					.createElementNS("http://www.mentor.com/harness/Schema/bridgesdesignattributes", "property");
			property.setAttribute("name", "webservice validation");
			design.appendChild(property);
		}

		Date now = new Date();
		property.setAttribute("val", now.toString());

		return requestPayload;
	}
}
