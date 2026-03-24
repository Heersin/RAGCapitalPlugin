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

import com.mentor.chs.api.IXBuildList;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXAction;
import com.mentor.chs.plugin.action.IXApplicationAction;

import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import java.awt.Cursor;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This custom action retrieve the design contens for all the designs in the current project.
 * <p/>
 */
public class PDFGeneratorAction implements IXApplicationAction
{

	public IXAction.Trigger[] getTriggers()
	{
		return new IXAction.Trigger[]{IXAction.Trigger.MainMenu, IXAction.Trigger.ContextMenu};
	}

	public boolean execute(final IXApplicationContext applicationContext)
	{
		Frame frame = applicationContext.getParentFrame();
		List<IXDesign> designs = getSelectedDesigns(applicationContext);
		if (designs.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "No designs to export");
			return true;
		}
		//A modal dialog shown to take the input from the user on parameters to control the PDF generation
		final PDFParameterInputDialog inputDlg = new PDFParameterInputDialog(this, applicationContext);
		inputDlg.setVisible(true);
		if (!inputDlg.wasSuccessful()) {
			return true;
		}
		//Once user provides the input annd press OK then diaog is disposed and input parameters are collected
		final PDFTaskParameters parameters = inputDlg.getParameters();

		//Setup the progress and logger
		final TaskProgressBar progress = new TaskProgressBar(new JProgressBar(), frame);
		generatePDF(applicationContext, progress, parameters, inputDlg.getOutputFile());
		progress.setVisible(true);
		inputDlg.dispose();
		return true;
	}

	public static void generatePDF(
			final IXApplicationContext applicationContext, final TaskProgressBar progress,
			final PDFTaskParameters parameters, final File output)

	{
		final TaskOutputWindow logger = new TaskOutputWindow(applicationContext.getOutputWindow(), "PDF Generator");
		final PDFGenerationTask task = new PDFGenerationTask();
		final Frame owner = applicationContext.getParentFrame();

		final SwingWorker runnable = new SwingWorker<String, Object>()
		{

			@Override
			public String doInBackground()
			{
				start(owner);
				try {
					PDFGenerationTask.ourZipStream gzos = new PDFGenerationTask.ourZipStream(new BufferedOutputStream(new FileOutputStream(output)));
					task.generatePDFZip(getSelectedDesigns(applicationContext),
									progress, logger, applicationContext, parameters, gzos);
					gzos.close();
				}
				catch (IOException e) {
					e.printStackTrace();
					logger.println(e.getMessage());
				}
				return null;
			}

			@Override
			protected void done()
			{
				finish(progress, owner);
			}
		};

		runnable.execute();
	}

	private static void start(Frame owner)
	{
		Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);
		owner.setCursor(busyCursor);
	}

	private static void finish(final TaskProgressBar progressBarDialog, Frame owner)
	{
		Cursor normalCursor = new Cursor(Cursor.DEFAULT_CURSOR);
		owner.setCursor(normalCursor);
		progressBarDialog.dispose();
	}

	public Integer getMnemonicKey()
	{
		return null;
	}

	public String getLongDescription()
	{
		return getDescription();
	}

	public boolean isReadOnly()
	{
		return true;
	}

	public String getDescription()
	{
		return "Action to generate PDFs for selected designs or all release designs";
	}

	public String getName()
	{
		return "PDF Generator Action";
	}

	public String getVersion()
	{
		return "1";
	}

	public Icon getSmallIcon()
	{
		return null;
	}

	public boolean isAvailable(IXApplicationContext context)
	{
		return (!getSelectedDesigns(context).isEmpty());
	}

	private static List<IXDesign> getSelectedDesigns(IXApplicationContext applicationContext)
	{
		List<IXDesign> designs = new ArrayList<IXDesign>();
		designs.addAll(applicationContext.getSelectedObjects(IXDesign.class));
		for (IXProject proj : applicationContext.getSelectedObjects(IXProject.class)) {
			designs.addAll(proj.getDesigns());
		}
		for (IXBuildList bld : applicationContext.getSelectedObjects(IXBuildList.class)) {
			designs.addAll(bld.getDesigns());
		}
		return designs;
	}
}