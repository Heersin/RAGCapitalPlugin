package com.example.plugin.utility;

import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.plugin.IXOutputPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by IntelliJ IDEA. User: kjuthi Date: Oct 3, 2012 Time: 6:21:27 PM
 */
public class DesignContentsDumpImpl
{

	private IXOutputPrinter output;
	private IXProject project;

	public DesignContentsDumpImpl(IXOutputPrinter output, IXProject project)
	{
		this.output = output;
		this.project = project;
	}

	public void dumpDesignContents(Writer writer)
	{
		output.println(
				"Outputing design contents for all designs in current project, Logs also stored at chs_home/temp/BusinessObjects.log");

		try {
			writeContents(writer);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			//Close the string writer
			try {

				if (writer != null) {
					writer.close();
				}
				output.println("<hr></hr>");
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeContents(Writer writer)
			throws IOException
	{
		StringWriter strWriter = new StringWriter();
		writeNetLists(strWriter);
		StringReader strReader = new StringReader(strWriter.toString());
		BufferedReader buffReader = new BufferedReader(strReader);
		try {
			do {
				String line = buffReader.readLine();
				if (line == null) {
					break;
				}
				writeLine(writer, line);
			}
			while (true);
		}
		finally {
			buffReader.close();
			strReader.close();
			strWriter.close();
		}
	}

	private void writeLine(Writer writer, String line)
			throws IOException
	{
		output.println(line);
		if (writer != null) {
			writer.write(line + '\n');
		}
	}

	private void writeNetLists(StringWriter strWriter)
			throws IOException
	{
		ExtensibilityNetlistWriter harnessWriter = new ExtensibilityNetlistWriter(strWriter);
		for (IXDesign des : project.getDesigns()) {
			strWriter.write("Design dump for Design:" + des.getAttribute("Name") + '\n');
			harnessWriter.writeNetlist(des);
		}
	}
}
