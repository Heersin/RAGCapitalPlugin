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
package com.example.webservice.server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.example.webservice.common.WebServiceUtils;
import com.example.webservice.client.RetrieveSVGDiagramClient;

/**
 * A web service that implements the "Build List Release Status Change" Capital hook
 */
public class BuildListReleaseStatusChangeHookService extends AbstractService
{
	public Document invoke(Document requestPayload) throws Exception
	{
		// Extract and dump Build List  information
		Element buildlist = requestPayload.getDocumentElement();
		System.out.println("Build List Release Status Change event:");
		System.out.println("  Build List name  			= " +buildlist.getAttribute("buildlistname"));
		System.out.println("  type         				= " + buildlist.getAttribute("buildlisttype"));
		System.out.println("  Project Name 				= " + buildlist.getAttribute("projectname"));
		System.out.println("  Build List description 	= " + buildlist.getAttribute("desc"));
		System.out.println("  new status        		= " + buildlist.getAttribute("newstatus"));
		System.out.println("  old status        		= " + buildlist.getAttribute("oldstatus"));

		return null;
	}
}
