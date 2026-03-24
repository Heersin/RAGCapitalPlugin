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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.util.StringTokenizer;

import com.example.webservice.common.WebServiceUtils;

/**
 * A servlet that responds to web service requests
 */
public class ExampleServlet extends HttpServlet
{
    /**
	 * Handles posted request. See #doGet
	 * @param request The SOAP request
	 * @param response The SOAP response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
		IOException
	{
		doGet(request, response);
	}

    /**
	 * Handles web service SOAP requests.
	 * The web service name is extracted from the URL (http://<host>:<port>/<context>/<web service>.
	 * The XML request payload is extracted and passed to the web service.
	 * The XML payload is obtained from the service and wrapped into the SOAP response message.
	 * @param request The SOAP request
	 * @param response The SOAP response
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
		IOException
	{
		// Get the output stream to be written to
		OutputStream responseOut = response.getOutputStream();

		// get the URL and extract the service name from it.
		String requestURL = request.getRequestURI();
		String serviceName = "";

		// extract the service name
		StringTokenizer tokens = new StringTokenizer(requestURL, "/");
		while (tokens.hasMoreTokens()) {
			serviceName = tokens.nextToken();
		}

		// Extract the XML request payload from the incoming stream
		Document requestPayload = null;

		if (request.getContentLength() > 0) {
			response.setContentType("text/xml");
			BufferedReader reader = request.getReader();

			StreamSource source = new StreamSource(reader);

			try {
				// Create the message from the input request stream and extract the payload
				SOAPMessage message = WebServiceUtils.newBlankSOAPMessage();
				SOAPPart part = (SOAPPart) message.getSOAPPart();
				part.setContent(source);
				message.saveChanges();
				requestPayload = WebServiceUtils.extractXMLPayloadFromSOAPMessage(message);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// Retrieve the web service (class)
        Document responsePayload = null;

		if (requestPayload != null) {
			String serviceClassName = "com.example.webservice.server." + serviceName;
			AbstractService service = null;

			// Instantiate the web service
			try {
				Class serviceClass = Class.forName(serviceClassName);
				service = (AbstractService) serviceClass.newInstance();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			// Invoke the service
			if (service != null) {
				try {
					responsePayload = service.invoke(requestPayload);
				}
				catch (Exception e) {
					System.out.println("Web Service Exception:");
					e.printStackTrace();
				}
			}
		}

		// Send response if applies
		if (responsePayload != null) {
			try {
				response.setContentType("text/xml");
				// Wrap response payload in SOAP
				SOAPMessage message = WebServiceUtils.newBlankSOAPMessage();
				WebServiceUtils.insertXMLPayloadInSOAPMessage(responsePayload, message);
				message.writeTo(responseOut);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String getServletInfo()
	{
		return "Example Servlet for CHS Web Services documentation";
	}
}
