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

/**
 * A concrete implementation for the CIS "ExportEngineeringChangeOrders" web service client.
 */
public class ExportEngineeringChangeOrdersClient extends AbstractClient
{
	public ExportEngineeringChangeOrdersClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "ExportEngineeringChangeOrders";
	}

	protected String getRequestPayload()
	{
		// To retreive all the Engineering Change Orders present in the example3 project, uncomment the below request and comment others
		/*	
			return "<project name='example3'><engineeringchangeorder asattachment='false' /></project>";
		*/

		// To retreive all the Engineering Change Orders under  the given category, uncomment the below request and comment others
		/*	
			return "<project name='example3'><engineeringchangeorder asattachment='false'  category='TestCategory1'/></project>";
		*/
		
		// To retreive all the Engineering Change Orders under the given release level, uncomment the below request and comment others
		/*	
			return "<project name='example3'><engineeringchangeorder asattachment='false' releaselevelname='Draft' /></project>";
		*/
		
		// This request will return all the Engineering Change Orders under the category="TestCategory1" and having Draft release level
		return "<project name='example3'><engineeringchangeorder asattachment='false'  category='TestCategory' releaselevelname='Draft'/></project>";
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}
}
