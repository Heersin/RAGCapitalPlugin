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

import com.mentor.chs.api.IXConnectivity;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.IXSystemContext;
import com.mentor.chs.plugin.task.IXTask;
import com.mentor.chs.plugin.task.IXTaskContext;
import com.mentor.chs.plugin.task.IXTaskHTMLLogger;
import com.mentor.chs.plugin.task.IXTaskProgress;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DesignWirePropetyValidationTask implements IXTask
{

	public final String WIRE_PROPERTY_NAME = "Max_Current";
	public final String WIRE_PROPERTY_VALUE = "2 amp";

	public boolean run(IXTaskProgress progress, IXTaskHTMLLogger logger,
			InputStream inputStream, IXTaskContext taskContext, IXSystemContext systemContext)
	{
		DesignWirePropertyValidationTaskParameters parameters =
				new DesignWirePropertyValidationTaskParameters(inputStream);
		IXProject proj = systemContext.getProject(parameters.getProjectName());
		if(proj == null){
			logger.println(bold("Project " + parameters.getProjectName() + " not found"));
			return true;
		}
		List<IXHarnessDesign> harnessDesigns = getHarnessDesigns(proj);
		for (IXHarnessDesign harnessDes : harnessDesigns) {
			logger.println(bold("Checking Harness Design: " + harnessDes.getAttribute("Name")));
			IXConnectivity connectivity = harnessDes.getConnectivity();
			if (connectivity != null) {
				Set<IXWire> wires = connectivity.getWires();
				for (IXWire wire : wires) {
					String propvalue = wire.getProperty(WIRE_PROPERTY_NAME);
					IXAttributeSetter attributeSetter = wire.getAttributeSetter();
					if (propvalue == null || propvalue.isEmpty()) {
						logger.println(color("BLUE ",
								" Property " + WIRE_PROPERTY_NAME + " not found on Wire " + wire.toHTML()
										+ " and hence creating it with default value of 2 amp"));
						if (attributeSetter != null) {
							attributeSetter.addProperty(WIRE_PROPERTY_NAME, WIRE_PROPERTY_VALUE);
						}
					}
					else {
						if (!propvalue.equalsIgnoreCase(WIRE_PROPERTY_VALUE) && !propvalue.equalsIgnoreCase("3 amp") && !propvalue.equalsIgnoreCase("5 amp")) {
							logger.println(color("RED",
									" Property " + WIRE_PROPERTY_NAME + " on wire " + wire.toHTML() +
											" has value not equal to valid values (2 amp, 3 amp, 5 amp) and hence " +
											"setting default value of 2 amp"));
							if (attributeSetter != null) {
								attributeSetter.removeProperty(WIRE_PROPERTY_NAME);
								attributeSetter.addProperty(WIRE_PROPERTY_NAME, WIRE_PROPERTY_VALUE);
							}
						}
						else {
							logger.println(bold("Property " + WIRE_PROPERTY_NAME + " found for wire ") + wire.toHTML());
						}
					}
				}
			}
		}
		return true;
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

	public PDFTaskParameters getTaskParameters()
	{
		return new PDFTaskParameters();
	}

	private List<IXHarnessDesign> getHarnessDesigns(IXProject proj)
	{
		List<IXHarnessDesign> returnDesigns = new ArrayList<IXHarnessDesign>();
		for (IXDesign des : proj.getDesigns()) {
			if (des instanceof IXHarnessDesign) {
				returnDesigns.add((IXHarnessDesign) des);
			}
		}
		return returnDesigns;
	}

	public void abort(AbortStatus status)
	{
		
	}
	public String getDescription()
	{
		return "Validate Harness Design ";
	}

	public String getName()
	{
		return "DesignWirePropetyValidation";
	}

	public String getVersion()
	{
		return "1";
	}
}