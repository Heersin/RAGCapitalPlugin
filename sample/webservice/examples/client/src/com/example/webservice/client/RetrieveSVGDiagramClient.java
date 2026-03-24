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
import com.example.webservice.common.WebServiceUtils;

/**
 * A concrete implementation for the CIS "SVGDiagrams" web service client.
 */
public class RetrieveSVGDiagramClient extends AbstractClient
{
	public RetrieveSVGDiagramClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "SVGDiagrams";
	}

	protected String getRequestPayload()
	{
		return "<wiringdesign><diagram id='UIDe69c0a-1152dcc767d-f528764d624db129b32c21fbca0cb8d6'/></wiringdesign>";
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void processResponse(Document responsePayload) throws Exception
	{
		// Save as SVG file in output directory
		String filePath = OUTPUT_DIRECTORY + "/RetrieveSVGDiagramClient.svg";
		WebServiceUtils.writeDOMDocumentToFile(responsePayload, filePath);
		System.out.println("SVG diagram successfully written to: " + filePath);
	}
}
