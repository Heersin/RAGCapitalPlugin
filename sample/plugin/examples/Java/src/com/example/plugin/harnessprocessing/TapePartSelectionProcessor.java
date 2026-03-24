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
import com.mentor.chs.api.IXBundle;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXInsulation;
import com.mentor.chs.api.IXInsulationRun;
import com.mentor.chs.api.IXLibraryCode;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.IXDesignLogSeverityEnum;
import com.mentor.chs.plugin.harness.IXHarnessEngineering;
import com.mentor.chs.plugin.harness.IXHarnessEngineeringTypeEnum;
import com.mentor.chs.plugin.harness.IXHarnessProcessingOutput;
import com.mentor.chs.plugin.harness.IXPreHarnessProcessor;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Harness processor to select spiral tape part number based on underlying bundle diameter
 */
public class TapePartSelectionProcessor extends AbstractHarnessProcessor
		implements IXPreHarnessProcessor, IXHarnessEngineering, IXApplicationContextListener
{

	private static final double[] BUNDLE_DIAMETER_RANGES = { 5, 7, 8, 10, 15, 20 };
	private static final double[] TAPE_WIDTHS = { 3, 6, 7, 8, 15, 20, 25 };
	private static final String[] TAPE_ATTRIBUTES = {
			IXAttributes.TypeCode,
			IXAttributes.MaterialCode,
			IXAttributes.ColorCode,
	};

	private IXApplicationContext context;

	public TapePartSelectionProcessor()
	{
		super("Example tape part selection harness processor",
				"1.0",
				"Sample tape part selection harness processor implementation");
	}

	public IXHarnessEngineeringTypeEnum getType()
	{
		return IXHarnessEngineeringTypeEnum.InsulationTotals;
	}

	public boolean process(IXHarnessDesign design, IXHarnessProcessingOutput output)
	{
		Set<IXInsulationRun> insulationRuns = getAllInsulationRuns(design);

		for (IXBundle bundle : getAllBundles(design)) {
			double targetTapeWidth = computeTapeWidth(getWidth(bundle));

			// Retrieve insulation runs on this bundle and process their insulation layers
			for (IXInsulationRun run : getInsulationRunsOnBundle(bundle, insulationRuns)) {
				for (IXInsulation insulation : run.getInsulations()) {
					processInsulation(insulation, targetTapeWidth, output);
				}
			}
		}

		return true;
	}

	private double computeTapeWidth(double bundleDiameter)
	{
		double tapeWidth = -1;
		for (int i=0; i<BUNDLE_DIAMETER_RANGES.length; i++) {
			tapeWidth = TAPE_WIDTHS[i];
			if (bundleDiameter <= BUNDLE_DIAMETER_RANGES[i]) {
				break;
			}
		}
		return tapeWidth;
	}

	private void processInsulation(IXInsulation insulation, double tapeWidth, IXHarnessProcessingOutput output)
	{
		String insulationType = insulation.getAttribute(IXAttributes.InsulationType);

		if ("Spiral Tape".equals(insulationType)) {
			// Choice of width for spiral tape depends on bundle diameter
			IXLibraryObject tapePart = findTapeWithMatchingWidth(insulation, tapeWidth);
			if (tapePart != null) {
				output.log(IXDesignLogSeverityEnum.Information, "Spiral Tape " +
						getName(insulation) + " - found width: " +
						tapePart.getAttribute(IXAttributes.TapeWidth) +
						" library part: " + tapePart.getAttribute(IXAttributes.PartNumber));
				insulation.getAttributeSetter().addAttribute(IXAttributes.PartNumber,
						tapePart.getAttribute(IXAttributes.PartNumber));
			}
		}
	}

	private IXLibraryObject findTapeWithMatchingWidth(IXInsulation insulation, final double tapeWidth)
	{
		SortedSet<IXLibraryObject> sortedParts = new TreeSet<IXLibraryObject>(new Comparator<IXLibraryObject>() {
			public int compare(IXLibraryObject o1, IXLibraryObject o2)
			{
				double diff1 = Math.abs(getWidth(o1) - tapeWidth);
				double diff2 = Math.abs(getWidth(o2) - tapeWidth);
				if (diff1 < diff2) {
					return -1;
				}
				else if (diff1 > diff2) {
					return 1;
				}
				return 0;
			}
		});

		for (IXLibraryObject tapePart : context.getLibrary().getLibraryObjects("Tape", "")) {
			// Input attributes (material, color, type code) should match
			if (matchingTapeAttributes(tapePart, insulation)) {
				sortedParts.add(tapePart);
			}
		}

		return sortedParts.isEmpty() ? null : sortedParts.iterator().next();
	}

	private static double getWidth(IXLibraryObject tape)
	{
		return getDouble(tape, IXAttributes.TapeWidth);
	}

	private static double getWidth(IXBundle bundle)
	{
		return getDouble(bundle, IXAttributes.Width);
	}

	private static double getDouble(IXObject object, String attributeName)
	{
		try {
			String value = object.getAttribute(attributeName);
			if (value != null) {
				return Double.valueOf(object.getAttribute(attributeName));
			}
		}
		catch (NumberFormatException ignore) {
		}
		return 0;
	}

	private boolean matchingTapeAttributes(IXLibraryObject tapePart, IXInsulation insulation)
	{
		boolean matching = true;

		for (String attrName : TAPE_ATTRIBUTES) {
			String partAttr = getLibraryObjectAttribute(tapePart, attrName);
			String objectAttr = insulation.getAttribute(attrName);
			if (partAttr != null) {
				matching = partAttr.equals(objectAttr);
			}
			else {
				matching = objectAttr == null;
			}
			if (!matching) {
				break;
			}
		}

		return matching;
	}

	private String getLibraryObjectAttribute(IXLibraryObject libraryObject, String attributeName)
	{
		IXLibraryCode code = null;

		if (IXAttributes.TypeCode.equals(attributeName)) {
			code = libraryObject.getComponentTypeCode();
		}
		else if (IXAttributes.ColorCode.equals(attributeName)) {
			code = libraryObject.getColorCode();
		}
		else if (IXAttributes.MaterialCode.equals(attributeName)) {
			code = libraryObject.getMaterialCode();
		}

		if (code != null) {
			return code.getAttribute(attributeName);
		}

		return libraryObject.getAttribute(attributeName);
	}

	private String getName(IXObject object)
	{
		return object.getAttribute(IXAttributes.Name);
	}

	public void setApplicationContext(IXApplicationContext applicationContext)
	{
		context = applicationContext;
	}
}
