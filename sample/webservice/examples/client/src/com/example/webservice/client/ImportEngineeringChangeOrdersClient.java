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
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;

import com.example.webservice.common.WebServiceUtils;

/**
 * A concrete implementation for the CIS "ImportEngineeringChangeOrders" web service client.
 */
public class ImportEngineeringChangeOrdersClient extends AbstractClient
{
	public ImportEngineeringChangeOrdersClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "ImportEngineeringChangeOrders";
	}

	protected String getRequestPayload()
	{
		// We assume the program is started from the sample directory $CHS_HOME/WebServiceExamples
		String filePath = "../doc/webservice/data/eco_web_service_import_example1.xml";
		FileReader reader = null;
		PrintWriter writer = null;
		ByteArrayOutputStream byteArrayStream = null;
		try {
			reader = new FileReader(filePath);
			byteArrayStream = new ByteArrayOutputStream();
			writer = new PrintWriter(byteArrayStream);

			for (int cc; (cc = reader.read()) != -1; ) {
				writer.print((char) cc);
			}

		}
		catch(FileNotFoundException e) {
			throw new IllegalArgumentException("Please provide a valid file path", e);
		}
		catch(IOException e) {
			throw new RuntimeException("Error while reading file", e);
		}
		finally {
			if (reader != null) {
				try {
					reader.close();
					writer.close();
				}
				catch (IOException ignore) {
				}
			}
		}

		return byteArrayStream.toString();
	}

	protected boolean isResponseExcepted()
	{
		return false;
	}
}
