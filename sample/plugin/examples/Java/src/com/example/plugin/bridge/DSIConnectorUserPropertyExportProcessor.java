/**
 * Copyright 2014 Mentor Graphics Corporation. All Rights Reserved.
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
package com.example.plugin.bridge;

import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXHarnessPlugin;
import com.mentor.chs.plugin.IXIntegratorPlugin;
import com.mentor.chs.plugin.IXLogicPlugin;
import com.mentor.chs.plugin.IXTopologyPlugin;
import com.mentor.chs.plugin.changemanager.IXBridgeExportPostProcessor;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Sample plugin to demonstrate bridge export post processing.
 * <p/>
 * For each connector in the design, its user properties are written as a comment line in the exported DSI file
 */
public class DSIConnectorUserPropertyExportProcessor
		implements IXBridgeExportPostProcessor, IXHarnessPlugin, IXIntegratorPlugin, IXTopologyPlugin, IXLogicPlugin
{

	private static final String DSI = "DSI";
	private static final String COMMENT = "! ";
	private static final String SECTION = "%";
	private static final String UTF8 = "UTF8";
	private static final int COMPONENT_SECTION_COUNT = 5;

	/**
	 * Return the description of the plugin
	 *
	 * @return String type - description of the plugin
	 */
	public String getDescription()
	{
		return "For each connector, its user properties are written as comments in the exported DSI file";
	}

	/**
	 * Return the name of the plugin
	 *
	 * @return String type - name of the plugin
	 */
	public String getName()
	{
		return "DSI Connector User Property Export Processor";
	}

	/**
	 * Return the version of the plugin
	 *
	 * @return String type - version of the plugin
	 */
	public String getVersion()
	{
		return "1.0";
	}

	@Override
	public boolean isAdaptorFormatHandledByPlugin(String adaptorFormatDescription)
	{
		return adaptorFormatDescription.contains(DSI);
	}

	@Override
	public boolean process(String adaptorFormatDescription, InputStream adaptorData,
			OutputStream processedAdaptorData, IXApplicationContext applicationContext) throws Exception
	{
		Collection<String> commentLines = getConectorPropertiesAsCommentLines(applicationContext);
		StringBuilder modifiedData = createModifiedAdaptorDataString(adaptorData, commentLines);
		boolean modifiedAdaptorData = !commentLines.isEmpty();
		if (modifiedAdaptorData) {
			processOutputStream(modifiedData, processedAdaptorData);
		}
		return modifiedAdaptorData;
	}

	private void processOutputStream(StringBuilder modifiedData, OutputStream processedAdaptorData)
			throws IOException
	{
		DataOutputStream dos = new DataOutputStream(processedAdaptorData);
		try {
			dos.writeBytes(modifiedData.toString());
		}
		finally {
			dos.close();
		}
	}

	private StringBuilder createModifiedAdaptorDataString(InputStream adaptorData, Collection<String> commentLines)
			throws IOException
	{
		StringBuilder modifiedData = new StringBuilder();
		InputStreamReader reader = new InputStreamReader(adaptorData, UTF8);
		LineNumberReader lineReader = new LineNumberReader(reader);
		try {
			writeModifiedDataLines(commentLines, lineReader, modifiedData);
		}
		finally {
			lineReader.close();
			reader.close();
		}
		return modifiedData;
	}

	private void writeModifiedDataLines(Collection<String> commentLines, LineNumberReader lineReader,
			StringBuilder modifiedData) throws IOException
	{
		boolean propsWritten = false;
		int sectionCount = 0;
		for (String line = lineReader.readLine(); line != null; line = lineReader.readLine()) {
			if (line.startsWith(SECTION)) {
				sectionCount++;
			}
			modifiedData.append(line).append("\n");
			if (!propsWritten && sectionCount == COMPONENT_SECTION_COUNT) {
				modifiedData.append(COMMENT).append("\n");
				modifiedData.append(COMMENT).append(getName()).append(" - ").append(getDescription())
						.append("\n");
				for (String comment : commentLines) {
					modifiedData.append(comment).append("\n");
				}
				modifiedData.append(COMMENT).append("\n");
				propsWritten = true;
			}
		}
	}

	private Collection<String> getConectorPropertiesAsCommentLines(IXApplicationContext applicationContext)
	{
		Set<IXConnector> connectors = new HashSet<IXConnector>();
		if (applicationContext != null && applicationContext.getCurrentDesign() != null &&
				applicationContext.getCurrentDesign().getConnectivity() != null) {
			connectors = applicationContext.getCurrentDesign().getConnectivity().getConnectors();
		}
		Collection<String> commentLines = new ArrayList<String>();
		for (IXConnector connector : connectors) {
			Set<IXValue> properties = connector.getProperties();
			StringBuilder propsLine = new StringBuilder();
			if (!properties.isEmpty()) {
				propsLine.append(COMMENT).append(connector.getAttribute("Name")).append(" -> ");
			}
			for (IXValue prop : properties) {
				propsLine.append("\"").append(prop.getName())
						.append("\" = \"").append(prop.getValue()).append("\"; ");
			}
			commentLines.add(propsLine.toString());
		}
		return commentLines;
	}
}
