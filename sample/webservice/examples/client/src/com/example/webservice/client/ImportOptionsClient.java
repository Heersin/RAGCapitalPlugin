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

/**
 * A concrete implementation for the CIS "ImportOptions" web service client.
 */
public class ImportOptionsClient extends AbstractClient
{
	public ImportOptionsClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "ImportOptions";
	}

	protected String getRequestPayload()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("<project name='example1'>");
		builder.append("<optionmgr id='_1'>");
		builder.append("<option id='_2' name='opt1' description='option 1 modified' isvariant='true'/>");
		builder.append("</optionmgr>");
		builder.append("</project>");
		return builder.toString();
	}

	protected boolean isResponseExcepted()
	{
		return false;
	}
}
