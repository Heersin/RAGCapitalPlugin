package com.example.webservice.client;

import com.example.webservice.common.WebServiceUtils;

import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;

public class BatchBridgeInTaskClient extends SubmitTaskClient
{

	private static final String ATTACHMENT_TYPE_FEMPARAMS = "FEMParams";
	private static final String ATTACHMENT_TYPE_BRIDGECONFIG = "BRIDGE_CONFIG_FILE";
	private static final String ATTACHMENT_TYPE_IMPORT = "BRIDGE_IMPORT_FILE";

	public BatchBridgeInTaskClient() throws Exception
	{
	}

	protected String getRequestPayload()
	{
		return "<taskspec cron_expression='' instance_name='BridgeFEMTask' name='BatchBridgeInTask'/>";
	}

	protected void addRequestSOAPAttachments(SOAPMessage messageSOAP) throws Exception
	{
		// Check that the extra parameter has been provided on the command line
		if (clientParam == null) {
			super.addRequestSOAPAttachments(messageSOAP);
		}
		// parse the clientParam string to get various input types
		String attachmentsString = clientParam;
		String[] attachmentDefinitions = attachmentsString.split(";");
		int count = 0;
		for (String attachmentDef : attachmentDefinitions) {
			count++;
			String[] attachmentDefinition = attachmentDef.split(",");
			String attachmentType = ATTACHMENT_TYPE_FEMPARAMS;
			String attachmentPath = "";
			if (attachmentDefinition.length == 2) {
				attachmentType = attachmentDefinition[0];
				attachmentPath = attachmentDefinition[1];
			}
			else if (attachmentDefinition.length == 1) {
				attachmentPath = attachmentDefinition[0];
			}
			if (!ATTACHMENT_TYPE_FEMPARAMS.equals(attachmentType) &&
					!ATTACHMENT_TYPE_BRIDGECONFIG.equals(attachmentType) &&
					!ATTACHMENT_TYPE_IMPORT.equals(attachmentType)) {
				throw new RuntimeException(
						"!!Error: task input file is of unknown type: " + attachmentType);
			}
			File parametersFile = new File(attachmentPath);
			if (!parametersFile.exists()) {
				throw new RuntimeException(
						"!!Error: task input file of type " + attachmentType + " not found: " + attachmentPath);
			}
			WebServiceUtils.createXMLAttachment(messageSOAP, attachmentType, new FileInputStream(attachmentPath));
		}
		if (count < 3) {
			throw new RuntimeException(
					"!!Error: Not all input attachments are provided for the task");
		}
	}
}
