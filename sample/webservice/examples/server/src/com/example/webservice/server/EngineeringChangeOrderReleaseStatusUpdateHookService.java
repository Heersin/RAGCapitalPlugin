/*
 * Copyright 2011 Mentor Graphics Corporation. All Rights Reserved.
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
import com.example.webservice.common.WebServiceUtils;
import com.example.webservice.client.RetrieveSVGDiagramClient;

/**
 * A web service that implements the "Engineering Change Order Release Level Change" Capital hook
 */
public class EngineeringChangeOrderReleaseStatusUpdateHookService extends AbstractService
{
	public Document invoke(Document requestPayload) throws Exception
	{
		// Extract and dump Engineering Change Order  information
		Element eco = requestPayload.getDocumentElement();
		System.out.println("Engineering Change Order Release Status Update event:");
		System.out.println("  name              = " + eco.getAttribute("name"));
		System.out.println("  category          = " + eco.getAttribute("category"));
		System.out.println("  short description = " + eco.getAttribute("shortdesc"));
		System.out.println("  release notes 	= " + eco.getAttribute("releasenotes"));
		System.out.println("  new status        = " + eco.getAttribute("newstatus"));
		System.out.println("  old status        = " + eco.getAttribute("oldstatus"));

		return null;
	}
}
