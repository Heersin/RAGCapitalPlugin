package com.example.webservice.client;

import com.example.webservice.common.WebServiceUtils;

import javax.xml.soap.SOAPMessage;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * A sample web service client to submit a custom task, wait for its completion and then retrieve attachment from the task.
 */
public class ExecuteCustomTaskClient extends AbstractClient
{

	enum State{
		SubmittingTask,
		WaitingForTaskCompletion,
		RetrieveTaskAttachments
	}
	protected State state = State.SubmittingTask;
	protected String taskID;

	public ExecuteCustomTaskClient() throws Exception
	{
	}

	protected String getWebServiceName()
	{
		if(State.SubmittingTask == state){
			return "SubmitTask";
		}
		else{
			return "DescribeTask";
		}
	}

	protected String getRequestPayload()
	{
		if(State.SubmittingTask == state){
			return "<taskspec cron_expression='' instance_name='PDFGenTask' name='plugin:Java//com.example.plugin.task.PDFGenerationTask'/>";
		}
		else if(State.WaitingForTaskCompletion == state){
			return "<taskspec attachments='false' executions='false' id='" + taskID + "'/>";
		}
		else{
			return "<taskspec attachments='true' executions='false' id='" + taskID + "'/>";
		}
	}

	protected boolean isResponseExcepted()
	{
		return true;
	}
	protected boolean hasResponseAttachments()
	{
		if(State.RetrieveTaskAttachments == state){
			return true;
		}
		else {
			return false;
		}
	}

	protected void addRequestSOAPAttachments(SOAPMessage messageSOAP) throws Exception
	{
		if (clientParam == null) {
			throw new RuntimeException("!!Error: this service requires passing the task parameters file path as the last parameter on the command line.");
		}

		File parametersFile = new File(clientParam);
		if (!parametersFile.exists()) {
			throw new RuntimeException("!!Error: task paramaters file not found: " + clientParam);
		}
		if(State.SubmittingTask == state){
			WebServiceUtils.createXMLAttachment(messageSOAP, "FEMParams", new FileInputStream(parametersFile));
		}
	}
	protected void processResponse(Document responsePayload) throws Exception
	{
		if(State.SubmittingTask == state){
			taskID = responsePayload.getElementsByTagName("task").item(0).getAttributes().getNamedItem("id").getNodeValue();
			state = State.WaitingForTaskCompletion;
			System.out.println("Waiting for task completion...");
			Thread.sleep(5000);
			invoke();
		}
		else if(State.WaitingForTaskCompletion == state){
			Element tasks = responsePayload.getDocumentElement();
			String stat = ((Element)(tasks.getElementsByTagName("taskspec").item(0))).getAttribute("status");
			if(stat.equalsIgnoreCase("FAILED")){
				System.out.println("Task Failed");
			}
			else if(stat.equalsIgnoreCase("COMPLETED")){
				System.out.println("Task Completed");
				state = State.RetrieveTaskAttachments;
				invoke();
			}
			else{
				System.out.println("Waiting for task completion...");
				Thread.sleep(5000);
				invoke();
			}
		}
	}
	protected void processResponseAttachments(SOAPMessage messageSOAP) throws Exception
	{
		if(State.RetrieveTaskAttachments == state){
			File parametersFile = new File(clientParam);
			saveAttachments(messageSOAP, parametersFile.getParentFile().getAbsolutePath());
		}
	}
	public static void saveAttachments(SOAPMessage soapMessage, String folderPath)
			throws SOAPException, IOException
	{
		for (Iterator iter = soapMessage.getAttachments(); iter.hasNext(); ) {
			AttachmentPart attachment = (AttachmentPart) iter.next();
			String contenID = attachment.getMimeHeader("Content-ID")[0];
			InputStream data = (ByteArrayInputStream) attachment.getContent();
			if(contenID.contains("PDF")){
				String filepath = folderPath + File.separator + contenID + ".zip";
				FileOutputStream stream = new FileOutputStream(filepath);
				while (data.available() != 0) {
					byte[] buffer = new byte[1024];
					int readBytesCount = data.read(buffer, 0, 1024);
					stream.write(buffer, 0, readBytesCount);
				}
				stream.close();
				System.out.println("PDF zip attachment successfully written to : " + filepath);
			}
			else if(contenID.contains("html")){
				String filepath = folderPath + File.separator + contenID + ".html";
				FileOutputStream stream = new FileOutputStream(filepath);
				InputStream gzis = new GZIPInputStream(data);
				InputStreamReader streamReader = new InputStreamReader(gzis, "UTF8");
				char[] buffer = new char[32768];
				PrintWriter printWriterc = null;
				try {
					printWriterc = new PrintWriter(stream);
					int charsRead;
					do {
						charsRead = streamReader.read(buffer);
						if (charsRead > 0) {
							printWriterc.write(buffer, 0, charsRead);
						}
					} while (charsRead > -1);
					printWriterc.flush();
				}
				finally {
					if (printWriterc != null) {
						printWriterc.close();
					}
				}
				System.out.println("HTML output successfully written to : " + filepath);
			}
		}
	}
}