/**
 * Copyright 2012 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.task;

import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXGraphicConversions;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.plugin.IXSystemContext;
import com.mentor.chs.plugin.IXBaseContext;
import com.mentor.chs.plugin.task.IXTask;
import com.mentor.chs.plugin.task.IXTaskContext;
import com.mentor.chs.plugin.task.IXTaskHTMLLogger;
import com.mentor.chs.plugin.task.IXTaskProgress;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * This custom action retrieve the design contens for all the released design in the current project.
 * <p/>
 */

public class PDFGenerationTask implements IXTask
{

	public boolean run(IXTaskProgress progress, IXTaskHTMLLogger logger,
			InputStream inputStream, IXTaskContext taskContext, IXSystemContext systemContext)
	{
		PDFTaskParameters parameters = new PDFTaskParameters(inputStream);
		IXProject proj = systemContext.getProject(parameters.getProjectName());
		List<IXDesign> releaseDesigns = getReleasedDesigns(proj);
		try {
			OutputStream outStr = taskContext.createTaskAttachment("PDFPacket").getOutputStream();
			ourZipStream gzos = new ourZipStream(new BufferedOutputStream(outStr));
			generatePDFZip(releaseDesigns, new TaskProgressBar(progress),
					new TaskOutputWindow(logger), systemContext, parameters, gzos);
		}
		catch (IOException e) {
			logger.println(color("red","Failed with exception - " + e.getMessage()));
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void generatePDFZip(List<IXDesign> releaseDesigns, TaskProgressBar progress,
			TaskOutputWindow logger, IXBaseContext basecontext,
			PDFTaskParameters parameters, ourZipStream gzos) throws IOException
	{
		IXGraphicConversions.XPrintPDFContext context = getPDFContext(parameters);
		int totalDesignCount = releaseDesigns.size();
		int counter = 0;
		progress.setMinimumMaximum(0, totalDesignCount);
		for (IXDesign design : releaseDesigns) {
			String designName = design.getAttribute(IXAttributes.Name) + "_" + design.getAttribute(IXAttributes.Revision);
			logger.println(bold("Generating PDF for Design: " + design.toHTML()));
			++counter;
			String subDir = designName + File.separator;
			Set<IXDiagram> diagrams = design.getDiagrams();
			for (IXDiagram dia : diagrams) {
				String diagramName = subDir + dia.getAttribute("Name") + ".pdf";
				ZipEntry diaEntry = new ZipEntry(diagramName);
				gzos.putNextEntry(diaEntry);
				logger.println(italic("Generating PDF for diagram: " + dia.toHTML()));
				progress.progressText("Generating PDF for diagram: " + dia.getAttribute("Name"));
				progress.progressPercentage(counter);
				dia.getPDF(gzos, context.pdfContext);
				gzos.closeEntry();
			}
		}
		if(counter == 0){
			logger.println(color("red","No design to generate ..."));
			return;
		}
		gzos.privateClose();
		logger.println(color("green",bold("PDF Generation complete ")));
	}

	public static String color(String sColor, String sText)
	{
		return "<font color=" + sColor + '>' + sText + "</font>";
	}

	public static String bold(String sText)
	{
		return bold(true) + sText + bold(false);
	}

	public static String bold(boolean bEnable)
	{
		return bEnable ? "<b>" : "</b>";
	}

	public static String italic(String sText)
	{
		return italic(true) + sText + italic(false);
	}

	public static String italic(boolean bEnable)
	{
		return bEnable ? "<i>" : "</i>";
	}

	public static boolean transferData(InputStream istr, OutputStream ostr, int limit) throws IOException
	{
		int totalBytes = 0;
		boolean finished = false;
		byte[] decompBuff = new byte[1024];
		while (!finished) {
			int num = istr.read(decompBuff);
			if (num == -1) {
				finished = true;
			}
			else {
				boolean hitLimit = false;
				if (limit != -1 && totalBytes + num > limit) {
					num = limit - totalBytes;
					hitLimit = true;
				}
				totalBytes += num;
				ostr.write(decompBuff, 0, num);
				if (hitLimit) {
					return true;
				}
			}
		}
		return false;
	}

	public static class ourZipStream extends ZipOutputStream
	{

		public ourZipStream(OutputStream out)
		{
			super(out);
		}

		public void close()
		{
		}

		public void privateClose() throws IOException
		{
			super.close();
		}
	}

	public IXGraphicConversions.XPrintPDFContext getPDFContext(PDFTaskParameters parameters)
	{
		IXGraphicConversions.XPrintPDFContext printContext = new IXGraphicConversions.XPrintPDFContext();
		printContext.scope = IXGraphicConversions.XPrintScope.DESIGN;

		if (parameters.getOrientation() != null && !parameters.getOrientation().isEmpty()) {
			printContext.pdfContext.paperOrientation =
					IXGraphicConversions.XPDFPaperOrientationChoice.valueOf(parameters.getOrientation());
		}
		printContext.pdfContext.customPageHeightCm = new Float(parameters.getImageHeight());
		printContext.pdfContext.customPageWidthCm = new Float(parameters.getImageWidth());
		printContext.pdfContext.color = IXGraphicConversions.XPDFColorChoice.valueOf(parameters.getColor());
		return printContext;
	}

	public PDFTaskParameters getTaskParameters()
	{
		return new PDFTaskParameters();
	}

	private List<IXDesign> getReleasedDesigns(IXProject proj)
	{
		List<IXDesign> returnDesigns = new ArrayList<IXDesign>();
		for (IXDesign des : proj.getDesigns()) {
			String releaseLevel = des.getAttribute("ReleaseLevel");
			if (releaseLevel.equalsIgnoreCase("Released")) {
				returnDesigns.add(des);
			}
		}
		return returnDesigns;
	}

	public static byte[] readBytesFromStream(InputStream inputStream)
			throws IOException
	{
		byte[] result;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[32767];
		int bytesRead = 0;
		while (bytesRead > -1) {
			bytesRead = inputStream.read(buffer);
			if (bytesRead > 0) {
				baos.write(buffer, 0, bytesRead);
			}
		}
		result = baos.toByteArray();
		return result;
	}
	public void abort(AbortStatus status)
	{
		
	}
	public String getDescription()
	{
		return "Generate PDF for the design/project requested";
	}

	public String getName()
	{
		return "GeneratePDFTask";
	}

	public String getVersion()
	{
		return "1";
	}
}
