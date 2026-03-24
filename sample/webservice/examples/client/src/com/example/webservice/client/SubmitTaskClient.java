package com.example.webservice.client;

import com.example.webservice.common.WebServiceUtils;

import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;

/**
 * A concrete implementation for the CIS "SubmitTask" web service client.
 */
public class SubmitTaskClient extends AbstractClient
{
	public SubmitTaskClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "SubmitTask";
	}

	protected String getRequestPayload()
	{
		return "<taskspec cron_expression='0 42 13 1/1 * ? *' instance_name='HPTask' name='HarnessProcessingTask'/>";
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void addRequestSOAPAttachments(SOAPMessage messageSOAP) throws Exception
	{
		// Check that the extra parameter has been provided on the command line
		if (clientParam == null) {
			throw new RuntimeException("!!Error: this service requires passing the task parameters file path as the last parameter on the command line.");
		}

		File parametersFile = new File(clientParam);
		if (!parametersFile.exists()) {
			throw new RuntimeException("!!Error: task paramaters file not found: " + clientParam);
		}

		WebServiceUtils.createXMLAttachment(messageSOAP, "FEMParams", new FileInputStream(parametersFile));
	}
}
