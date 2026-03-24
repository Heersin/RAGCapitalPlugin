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
import org.w3c.dom.NodeList;
import com.example.webservice.client.ExportDesignClient;
import com.example.webservice.common.WebServiceUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A web service that processes a design release status update event
 */
public class DesignWireListReporterService extends AbstractService
{
	public Document invoke(Document requestPayload) throws Exception
	{
		// Extract and dump design information
		Element design = requestPayload.getDocumentElement();
		System.out.println("Design attributes:");
		System.out.println("  name              = " + design.getAttribute("name"));
		System.out.println("  revision          = " + design.getAttribute("revision"));
		System.out.println("  short description = " + design.getAttribute("shortdesc"));
		System.out.println("  type              = " + design.getAttribute("designtype"));
		System.out.println("  id                = " + design.getAttribute("id"));
		System.out.println("  project           = " + design.getAttribute("projectname"));

		// Round trip to CHS web service: export the design and extract wire list file from it		
		final String request = WebServiceUtils.writeDOMDocumentToString(requestPayload);
		final String designName = design.getAttribute("name");
		final String projectName = design.getAttribute("projectname");
		ExportDesignClient client = new ExportDesignClient() {
			protected String getRequestPayload()
			{
				return request;
			}

			protected void processResponse(Document responsePayload) throws Exception
			{
				// Create output file
				File reportFile = new File(OUTPUT_DIRECTORY + '/' + projectName + '-' + designName + "-wires.csv");
				PrintWriter writer = null;

				try {
					writer = new PrintWriter(new FileOutputStream(reportFile));
					// Title row
					List<String> row = new ArrayList<String>();
					row.add("Wire Name");
					row.add("From Device");
					row.add("From Pin");
					row.add("To Device");
					row.add("To Pin");
					writeRow(writer, row);

					// Extract connectivity
					NodeList nodes = responsePayload.getElementsByTagName("connectivity");
					Element connectivity = (Element) nodes.item(0);
					nodes = connectivity.getElementsByTagName("wire");
					for (int i=0; i<nodes.getLength(); i++) {
						writeWire(writer, row, connectivity, (Element) nodes.item(i));
					}
				}
				finally {
					if (writer != null) {
						writer.close();
					}
				}
				System.out.println("Wire report written to file: " + reportFile.getPath());
			}

			private void writeRow(PrintWriter writer, List<String> row)
			{
				for (int i=0; i<row.size(); i++) {
					if (i > 0) {
						writer.print(",");
					}
					writer.print(row.get(i));
				}
				writer.println();
				row.clear();
			}

			private void writeWire(PrintWriter writer, List<String> row, Element connectivity, Element wire)
			{
				row.add(wire.getAttribute("name"));
				// Scan connections
				NodeList nodes = wire.getElementsByTagName("connection");
				for (int i=0; i<nodes.getLength() && i<2; i++) {
					Element connection = (Element) nodes.item(i);
					dumpConnection(row, connectivity, connection);
				}
				writeRow(writer, row);
			}

			private void dumpConnection(List<String> row, Element connectivity, Element connection)
			{
				// Search pin's parent device
				String pinId = connection.getAttribute("pinref");
				Element device = findDeviceFromPin(pinId, connectivity, "device");
				if (device == null) {
					device = findDeviceFromPin(pinId, connectivity, "connector");
				}
				if (device == null) {
					device = findDeviceFromPin(pinId, connectivity, "splice");
				}
				if (device != null) {
					row.add(device.getAttribute("name"));
					Element pin = findDevicePin(pinId, device);
					row.add(pin.getAttribute("name"));
				}
				else {
					row.add("?");
					row.add("?");
				}
			}

			private Element findDeviceFromPin(String pinId, Element connectivity, String deviceType)
			{
				Element device = null;
				NodeList nodes = connectivity.getElementsByTagName(deviceType);
				for (int i=0; device==null && i<nodes.getLength(); i++) {
					Element dev = (Element) nodes.item(i);
					Element pin = findDevicePin(pinId, dev);
					if (pin != null) {
						device = dev;
					}
				}
				return device;
			}

			private Element findDevicePin(String pinId, Element device)
			{
				Element pin = null;
				NodeList nodes = device.getElementsByTagName("pin");
				for (int i=0; pin==null && i<nodes.getLength(); i++) {
					Element p = (Element) nodes.item(i);
					if (pinId.equals(p.getAttribute("id"))) {
						pin = p;
					}
				}
				return pin;
			}
		};
		client.invoke(false);

		return null;
	}
}
