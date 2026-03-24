/**
 * Copyright 2014 Mentor Graphics Corporation. All Rights Reserved.
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
package com.example.plugin.harnessprocessing;

import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.plugin.harness.IXHarnessProcessingOutput;
import com.mentor.chs.plugin.harness.IXPostHarnessProcessor;

import java.util.Collection;

public class PropertyBasedComponentNameProcessor extends AbstractHarnessProcessor implements IXPostHarnessProcessor
{

	public static final String COMPONENT_NAME = "Name";
	public static final String INCOMING_OBJECT_NAME = "IncomingObjectName";
	private IXHarnessDesign harnessDesign;
	public PropertyBasedComponentNameProcessor()
	{
		super("Component Name Processor",
				"1.0",
				"Renames the component based on the value for the property: IncomingObjectName");
	}

	@Override public boolean process(IXHarnessDesign design, IXHarnessProcessingOutput output)
	{
		harnessDesign = design;
		try {
			processConnectors();
			processRingTerminals();
			processSplices();
			processWires();
			processMulticores();

			processClips();
			processGrommets();
			processOtherComponents();
			processBreakoutTapes();
			processSpotTapes();
			processMultiLocationComponents();
			processInsulationRuns();
		}
		finally {
			harnessDesign = design;
		}
		return true;
	}

	private void processSplices()
	{
		processComponents(harnessDesign.getHarness().getSplices());
	}

	private void processClips()
	{
		processComponents(harnessDesign.getHarness().getClips());
	}

	private void processGrommets()
	{
		processComponents(harnessDesign.getHarness().getGrommets());
	}

	private void processConnectors()
	{
		processComponents(harnessDesign.getHarness().getConnectors());
	}

	private void processRingTerminals()
	{
		processComponents(harnessDesign.getHarness().getRingTerminals());
	}

	private void processWires()
	{
		processComponents(harnessDesign.getHarness().getWires());
	}

	private void processMulticores()
	{
		processComponents(harnessDesign.getHarness().getMulticores());
	}

	private void processOtherComponents()
	{
		processComponents(harnessDesign.getHarness().getOtherComponents());
	}

	private void processSpotTapes()
	{
		processComponents(harnessDesign.getHarness().getSpotTapes());
	}

	private void processBreakoutTapes()
	{
		processComponents(harnessDesign.getHarness().getBreakoutTapes());
	}

	private void processMultiLocationComponents()
	{
		processComponents(harnessDesign.getHarness().getMultiLocationComponents());
	}

	private void processInsulationRuns()
	{
		processComponents(harnessDesign.getHarness().getInsulationRuns());
	}

	private String getPropertyBasedName(IXObject component)
	{
		return component.getProperty(INCOMING_OBJECT_NAME);
	}

	private void processComponents(Collection<? extends IXWriteableObject> componentsToProcess)
	{
		for (IXWriteableObject component : componentsToProcess) {
			String customName = getPropertyBasedName(component);
			if (customName != null) {
				component.getAttributeSetter().addAttribute(IXAttributes.Name, customName);
			}
		}
	}
}
