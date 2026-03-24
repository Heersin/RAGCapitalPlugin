package com.example.webservice.client;

import com.example.webservice.common.WebServiceUtils;
import org.w3c.dom.Document;

import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA. User: nsontine Date: 31/7/14 Time: 4:05 PM To change this template use File | Settings |
 * File Templates.
 */
public class ExportBuildListClient extends AbstractClient
{

	@Override protected String getWebServiceName()
	{
		return "ExportBuildListService";
	}

	@Override protected String getRequestPayload()
	{
		return "<project name='example4'><buildlist asattachment='false' name='logicBuildList' type='logic'/></project>";
	}

	@Override protected boolean isResponseExcepted()
	{
		return true;
	}

	protected void processResponse(Document responsePayload) throws IOException, TransformerException
	{
		// Save as XML file in output directory
		String filePath = OUTPUT_DIRECTORY + "/example4-logicBuildList.xml";
		WebServiceUtils.writeDOMDocumentToFile(responsePayload, filePath);
		System.out.println("BuildList xml successfully written to: " + filePath);
	}
}
