package com.example.webservice.client;

import com.example.webservice.common.WebServiceUtils;

import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;

/**
 * A concrete implementation for the CIS "ImportLanguageDictionary" web service client.
 */
public class ImportLanguageDictionaryClient extends AbstractClient
{
	public ImportLanguageDictionaryClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "ImportLanguageDictionary";
	}

	protected String getRequestPayload()
	{
		return "<dictionary/>";
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void addRequestSOAPAttachments(SOAPMessage messageSOAP) throws Exception
	{
		// Check that the extra parameter has been provided on the command line
		if (clientParam == null) {
			throw new RuntimeException("!!Error: this service requires passing the language dictionary file path as the last parameter on the command line.");
		}

		File designFile = new File(clientParam);
		if (!designFile.exists()) {
			throw new RuntimeException("!!Error: language dictionary file not found: " + clientParam);
		}

		// we do not close the input stream as it will be closed when the soap message is sent..
		WebServiceUtils.createGZIPAttachment(messageSOAP, clientParam, new FileInputStream(clientParam));
	}
}
