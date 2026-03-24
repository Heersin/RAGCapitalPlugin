package com.example.webservice.client;

import com.example.webservice.common.WebServiceUtils;

import javax.xml.soap.SOAPMessage;
import java.io.File;
import java.io.FileInputStream;

/**
 * A concrete implementation for the CIS "SymbolLibraryImport" web service client.
 */
public class ImportSymbolClient extends AbstractClient
{
	public ImportSymbolClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		return "SymbolLibraryImport";
	}

	protected String getRequestPayload()
	{
		String compressedAtt = "compressed='" + Boolean.toString(isCompressed()) + "'";
		return "<import symboloverwrite='false' "+ compressedAtt + "/>";
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void addRequestSOAPAttachments(SOAPMessage messageSOAP) throws Exception
	{
		// Check that the extra parameter has been provided on the command line
		if (clientParam == null) {
			throw new RuntimeException("!!Error: this service requires passing the symbol library file path as the last parameter on the command line.");
		}

		File designFile = new File(clientParam);
		if (!designFile.exists()) {
			throw new RuntimeException("!!Error: symbol library file not found: " + clientParam);
		}

		if(isCompressed()) {
			WebServiceUtils.createGZIPAttachment(messageSOAP, "symbollibrary", new FileInputStream(designFile));
		}
		else {
		WebServiceUtils.createXMLAttachment(messageSOAP, "symbollibrary", new FileInputStream(designFile));
		}
	}

	private boolean isCompressed()
	{
		return false;
	}
}
