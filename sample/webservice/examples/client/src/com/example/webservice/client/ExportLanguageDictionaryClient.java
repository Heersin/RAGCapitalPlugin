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
package com.example.webservice.client;

import org.w3c.dom.Document;
import com.example.webservice.common.WebServiceUtils;

import javax.xml.soap.SOAPMessage;
import java.io.FileOutputStream;

/**
 * A concrete implementation for the CIS "ExportLanguageDictionary" web service client.
 */
public class ExportLanguageDictionaryClient extends AbstractClient
{
	public ExportLanguageDictionaryClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "ExportLanguageDictionary";
	}

	protected String getRequestPayload()
	{
		return "<languagemgr/>";
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected boolean hasResponseAttachments()
	{
		return true;
	}

	protected void processResponseAttachments(SOAPMessage messageSOAP) throws Exception
	{
		// Retrieve the attachment and persist it to a file
		// New format for design export: as gzip attachment
		String extenstion = ".csv";
		String fileName = OUTPUT_DIRECTORY + "/dictionary" + extenstion;
		FileOutputStream stream = new FileOutputStream(fileName);
		try {
			WebServiceUtils.extractDocumentFromSOAPAttachment(messageSOAP, stream);
			System.out.println("Dictionary data successfully written to " + fileName);
		}
		finally {
			stream.close();
		}
	}

	protected void processResponse(Document responsePayload) throws Exception
	{
		// Payload is contained in attachment which is processed separately
	}
}
