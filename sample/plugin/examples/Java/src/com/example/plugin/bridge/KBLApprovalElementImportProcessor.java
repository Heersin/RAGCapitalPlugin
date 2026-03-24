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

import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXHarnessPlugin;
import com.mentor.chs.plugin.IXIntegratorPlugin;
import com.mentor.chs.plugin.IXLogicPlugin;
import com.mentor.chs.plugin.IXTopologyPlugin;
import com.mentor.chs.plugin.changemanager.IXBridgeImportPreProcessor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample plugin to demonstrate bridge import pre processing.
 * <p/>
 * For each Approval element in a KBL file, a user property is created on the Harness with the Approval name as the user
 * property name and Approval type as the user property value
 */
public class KBLApprovalElementImportProcessor
		implements IXBridgeImportPreProcessor, IXHarnessPlugin, IXIntegratorPlugin, IXTopologyPlugin, IXLogicPlugin
{

	private static final String KBL_ROOT = "kbl:KBL_container";
	private static final String APPROVAL = "Approval";
	private static final String NAME = "Name";
	private static final String TYPE_OF_APPROVAL = "Type_of_approval";
	private static final String HARNESS = "Harness";
	private static final String PROCESSING_INFORMATION = "Processing_information";
	private static final String ID = "id";
	private static final String INSTRUCTION_TYPE = "Instruction_type";
	private static final String INSTRUCTION_VALUE = "Instruction_value";
	private static final String KBL = "KBL";

	/**
	 * Return the description of the plugin
	 *
	 * @return String type - description of the plugin
	 */
	public String getDescription()
	{
		return "For each Approval element in KBL, a user property is created on the Harness for reference in Capital";
	}

	/**
	 * Return the name of the plugin
	 *
	 * @return String type - name of the plugin
	 */
	public String getName()
	{
		return "KBL Approval Element Import Processor";
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
		return adaptorFormatDescription.contains(KBL);
	}

	@Override
	public boolean process(String adaptorFormatDescription, InputStream adaptorData,
			OutputStream processedAdaptorData, IXApplicationContext applicationContext) throws Exception
	{
		// create document builder
		DocumentBuilder builder = createDocumentBuilder();
		// create the DOM from input stream
		Document domdoc = getDOMDocument(adaptorData, builder);
		// add user properties from Approval elements in KBL
		boolean dataModified = addApprovalElementsAsUserProperties(domdoc);
		// modify the stream by adding user properties
		if (dataModified) {
			processOutputStream(domdoc, processedAdaptorData);
		}
		return dataModified;
	}

	private void processOutputStream(Document domdoc, OutputStream processedAdaptorData) throws TransformerException
	{
		Source xmlSource = new DOMSource(domdoc);
		Result outputTarget = new StreamResult(processedAdaptorData);
		TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
	}

	private boolean addApprovalElementsAsUserProperties(Document domdoc)
	{
		if (domdoc != null) {
			Element documentElement = domdoc.getDocumentElement();
			if (documentElement != null && KBL_ROOT.equals(documentElement.getNodeName())) {
				Map<String, String> approvals = getApprovalElements(documentElement);
				return addUserPropertiesToHarness(documentElement, approvals);
			}
		}
		return false;
	}

	private boolean addUserPropertiesToHarness(Element documentElement, Map<String, String> approvals)
	{
		boolean modified = false;
		NodeList harnessList = documentElement.getElementsByTagName(HARNESS);
		if (harnessList.getLength() > 0) {
			Node harness = harnessList.item(0);
			Document ownerDocument = harness.getOwnerDocument();

			int i = 0;
			for (String approvalName : approvals.keySet()) {
				i++;
				Element procIns = ownerDocument.createElement(PROCESSING_INFORMATION);
				procIns.setAttribute(ID, APPROVAL + "_" + i);
				harness.appendChild(procIns);

				Element insType = ownerDocument.createElement(INSTRUCTION_TYPE);
				insType.setTextContent(approvalName);
				procIns.appendChild(insType);

				Element insValue = ownerDocument.createElement(INSTRUCTION_VALUE);
				String approvalValue = approvals.get(approvalName);
				insValue.setTextContent(approvalValue);
				procIns.appendChild(insValue);

				System.out.println("Log from " + getName() + " - Added user property with name = \"" + approvalName +
						"\" and value = \"" + approvalValue + "\"");
			}
			modified = i > 0;
		}
		return modified;
	}

	private Map<String, String> getApprovalElements(Element documentElement)
	{
		NodeList approvalList = documentElement.getElementsByTagName(APPROVAL);
		Map<String, String> approvals = new HashMap<String, String>();
		for (int i = 0; i < approvalList.getLength(); i++) {
			Node approval = approvalList.item(i);
			NodeList childNodes = approval.getChildNodes();
			String approvalName = null;
			String approvalType = null;
			for (int j = 0; j < childNodes.getLength(); j++) {
				Node child = childNodes.item(j);
				if (child.getNodeName().equals(NAME)) {
					approvalName = child.getTextContent();
				}
				else if (child.getNodeName().equals(TYPE_OF_APPROVAL)) {
					approvalType = child.getTextContent();
				}
			}
			if (approvalName != null && approvalType != null) {
				approvals.put(approvalName, approvalType);
			}
		}
		return approvals;
	}

	private Document getDOMDocument(InputStream adaptorInput, DocumentBuilder builder) throws IOException, SAXException
	{
		Document domdoc = null;
		if (builder != null) {
			domdoc = builder.parse(adaptorInput);
		}
		return domdoc;
	}

	private DocumentBuilder createDocumentBuilder() throws ParserConfigurationException
	{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		return builder;
	}
}
