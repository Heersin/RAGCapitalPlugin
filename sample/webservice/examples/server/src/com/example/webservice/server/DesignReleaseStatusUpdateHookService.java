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
import com.example.webservice.common.WebServiceUtils;
import com.example.webservice.client.RetrieveSVGDiagramClient;

/**
 * A web service that implements the "Release Level Change" CHS hook
 */
public class DesignReleaseStatusUpdateHookService extends AbstractService
{
	public Document invoke(Document requestPayload) throws Exception
	{
		// Extract and dump design information
		Element design = requestPayload.getDocumentElement();
		System.out.println("Design Release Status Update event:");
		System.out.println("  name              = " + design.getAttribute("name"));
		System.out.println("  revision          = " + design.getAttribute("revision"));
		System.out.println("  short description = " + design.getAttribute("shortdesc"));
		System.out.println("  type              = " + design.getAttribute("designtype"));
		System.out.println("  id                = " + design.getAttribute("id"));
		System.out.println("  project           = " + design.getAttribute("projectname"));
		System.out.println("  new status        = " + design.getAttribute("releasestatus"));
		System.out.println("  old status        = " + design.getAttribute("oldstatus"));

		// Only perform the action for a specific transition
		if ("complete".equalsIgnoreCase(design.getAttribute("releasestatus")) &&
				"intermediate".equalsIgnoreCase(design.getAttribute("oldstatus"))) {
			// Round trip to CHS web service: retrieve an SVG diagram and persist it to a file
			final String request = WebServiceUtils.writeDOMDocumentToString(requestPayload);
			final String designName = design.getAttribute("name");
			final String projectName = design.getAttribute("projectname");
			RetrieveSVGDiagramClient client = new RetrieveSVGDiagramClient() {
				protected String getRequestPayload()
				{
					return request;
				}

				protected void processResponse(Document responsePayload) throws Exception
				{
					// Save as SVG file in output directory
					String filePath = OUTPUT_DIRECTORY + '/' + projectName + '-' + designName + "-diagram.svg";
					WebServiceUtils.writeDOMDocumentToFile(responsePayload, filePath);
					System.out.println("SVG diagram successfully written to: " + filePath);
				}
			};
			client.invoke(true);
		}
		else {
			System.out.println("Release status transition ignored");
		}

		return null;
	}
}
