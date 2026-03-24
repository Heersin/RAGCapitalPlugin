/**
 * Copyright 2013-2014 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example plugin.
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
package com.example.plugin.dataexchange;

import com.mentor.chs.api.dataexchange.IXDataExchange;
import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * An example to demonstrate how to implement the IXDataExchange. This example simply takes the input report content and
 * dumps them into a CSF format. It also save the CSV file to the user temp location.
 * <p/>
 * On success it shows the small dialog with success and the path of file. On failure the error message is displayed
 * along with the reason.
 */
public class ReportToCSVFileExchange implements IXDataExchange
{

	@Override public String getDescription()
	{
		return "Convert Report content To CSV File";
	}

	@Override public String getName()
	{
		return "ReportToCSVFileExchange";
	}

	@Override public String getVersion()
	{
		return "1.0";
	}

	@Override public void bridgeOut(InputStream dataTobeExchanged)
	{
		FileOutputStream fos = null;
		try {
			// create the Document from Inputstream to convert to CSV
			Document document = createDocument(dataTobeExchanged);
			// create the temp file
			final File tempFile = File.createTempFile("SampleReport", ".csv");
			fos = new FileOutputStream(tempFile);
			// Write the headers first in the CSV
			writeHeaders(fos, document);
			// Write the results now
			writeResults(fos, document);
			// report success status to the user
			reportSuccess(tempFile.getAbsolutePath());
		}
		catch (Exception e) {
			e.printStackTrace();
			// Report could not be converted to CSV format. Report error to the user.
			reportError(e);
		}
		finally {
			if (fos != null) {
				try {
					fos.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Reports the error dialog to the user along with the exception message
	 *
	 * @param e Exception thrown
	 */
	private void reportError(Exception e)
	{
		final String message = "Converting report to CSV file failed due to " + e.getMessage();
		JOptionPane.showMessageDialog(null, message, "Report To CSV File Exchange Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Report Success statut to the user along with the temp file location
	 *
	 * @param absolutePath Absolute path of the CSV file being created
	 */
	private void reportSuccess(String absolutePath)
	{
		final String message = "Report saved at " + absolutePath;
		JOptionPane.showMessageDialog(null, message, "Report To CSV File Exchange Successful",
				JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Parse the DOM elements and output the results into CSV format
	 *
	 * @param fos file output stream to write the CSV data
	 * @param document Document created from Input report content
	 *
	 * @throws IOException Exception if any parsing fails.
	 */
	private void writeResults(FileOutputStream fos, Document document) throws IOException
	{
		NodeList resultSets = document.getElementsByTagName("resultset");
		for (int index = 0; index < resultSets.getLength(); index++) {
			Node resultSet = resultSets.item(index);
			if (resultSet instanceof Element) {
				NodeList results = ((Element) resultSet).getElementsByTagName("result");
				for (int i = 0; i < results.getLength(); i++) {
					Node result = results.item(i);
					Node valueNode = result.getChildNodes().item(0);
					if (valueNode != null) {
						String value = valueNode.getNodeValue();
						if (value != null && !value.isEmpty()) {
							fos.write(value.getBytes());
						}
					}
					fos.write(',');
				}
			}
			fos.write('\n');
		}
	}

	/**
	 * Write the headers for the CSV file
	 *
	 * @param fos file output stream to write the CSV data
	 * @param document Document created from Input report content
	 *
	 * @throws IOException Exception if any parsing fails.
	 */
	private void writeHeaders(FileOutputStream fos, Document document) throws IOException
	{
		NodeList resultheaders = document.getElementsByTagName("resultsetheader");
		for (int index = 0; index < resultheaders.getLength(); index++) {
			Node header = resultheaders.item(index);
			NamedNodeMap attrs = header.getAttributes();
			Node attrNode = attrs.getNamedItem("title");
			String attrName = attrNode.getNodeValue();
			fos.write(attrName.getBytes());
			fos.write(',');
		}
		fos.write('\n');
	}

	/**
	 * Creates the DOM document from the given Inputstream
	 *
	 * @param stream Inputstream - contains the report content
	 *
	 * @return created DOM document
	 *
	 * @throws Exception if fails to create DOM document
	 */
	private Document createDocument(InputStream stream) throws Exception
	{
		org.apache.xerces.parsers.DOMParser parser;
		// Instantiate the parser and set various options.
		parser = new DOMParser();
		parser.setFeature("http://xml.org/sax/features/namespaces", true);

		// Parse the input file
		parser.parse(new InputSource(stream));
		// Return the DOM tree
		return parser.getDocument();
	}
}
