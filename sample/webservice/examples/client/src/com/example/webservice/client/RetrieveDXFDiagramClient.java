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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Node;

import java.io.OutputStreamWriter;
import java.io.FileOutputStream;

/**
 * A concrete implementation for the CIS "DXFDiagrams" web service client.
 */
public class RetrieveDXFDiagramClient extends AbstractClient
{
	public RetrieveDXFDiagramClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "DXFDiagrams";
	}

	protected String getRequestPayload()
	{
		return "<wiringdesign name='Design1' projectname='example1'/>";
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void processResponse(Document responsePayload) throws Exception
	{
		// Extract diagram elements
		Element design = responsePayload.getDocumentElement();
		NodeList nodes = design.getElementsByTagName("diagram");
		int nbFiles = 0;

		for (int i = 0; i < nodes.getLength(); i++) {
			// Get diagram attribute to create file name
			Element diagram = (Element) nodes.item(i);

			// Retrieve DXF as CDATA
			NodeList children = diagram.getChildNodes();
			int nbChildren = children == null ? 0 : children.getLength();
			CDATASection cdata = null;

			for (int j = 0; cdata == null && j < nbChildren; j++) {
				Node node = children.item(j);
				if (node.getNodeType() == Node.CDATA_SECTION_NODE) {
					cdata = (CDATASection) node;
				}
			}

			String dxf = cdata==null ? null : cdata.getData();

			if (dxf != null) {
				// Create file and dump DXF content
				// Name the file after the id of the diagram as it is always unique
				// and compatible with a file name
				String diagId = diagram.getAttribute("id");
				String fileName = diagId + ".dxf";
				String filePathName = OUTPUT_DIRECTORY + '/' + fileName;
				OutputStreamWriter writer = null;
				try {
					System.out.println("Writing DXF file: " + filePathName);
					writer = new OutputStreamWriter(new FileOutputStream(filePathName), "UTF8");
					writer.write(dxf);
					nbFiles++;
				}
				finally {
					if (writer != null) {
						writer.close();
					}
				}
			}
		}

		System.out.println("Number of DXF diagrams generated: " + nbFiles);
	}
}
