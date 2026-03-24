/*
 * Copyright 2009 Mentor Graphics Corporation. All Rights Reserved.
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

import com.example.webservice.common.WebServiceUtils;

import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;

/**
 * A concrete implementation for the CIS "ImportDesign" web service client.
 */
public class ImportDesignClient extends AbstractClient
{
	public ImportDesignClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "ImportDesign";
	}

	protected String getRequestPayload()
	{
		return "<import designprojectname='example1'/>";
	}

	protected boolean isResponseExcepted()
	{
		return false;
	}
	
	protected void addRequestSOAPAttachments(SOAPMessage messageSOAP) throws Exception
	{
		// Check that the extra parameter has been provided on the command line
		if (clientParam == null) {
			throw new RuntimeException("!!Error: this service requires passing the design file path as the last parameter on the command line.");
		}

		File designFile = new File(clientParam);
		if (!designFile.exists()) {
			throw new RuntimeException("!!Error: design file not found: " + clientParam);
		}

		WebServiceUtils.createGZIPAttachment(messageSOAP, "design", new FileInputStream(designFile));
	}
}
