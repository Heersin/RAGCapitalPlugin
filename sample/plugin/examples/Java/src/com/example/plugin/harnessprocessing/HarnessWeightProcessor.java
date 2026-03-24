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

import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXBundle;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXInsulation;
import com.mentor.chs.api.IXLibrariedObject;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.harness.IXHarnessEngineering;
import com.mentor.chs.plugin.harness.IXHarnessEngineeringTypeEnum;
import com.mentor.chs.plugin.harness.IXHarnessProcessingOutput;


public class HarnessWeightProcessor extends AbstractHarnessProcessor
		implements IXHarnessEngineering, IXApplicationContextListener
{

	private IXApplicationContext appContext;

	public HarnessWeightProcessor()
	{
		super("Example harness weight processor",
				"1.0",
				"Sample harness weight processor implementation");
	}

	@Override
	public IXHarnessEngineeringTypeEnum getType()
	{
		return IXHarnessEngineeringTypeEnum.HarnessWeight;
	}

	@Override
	public boolean process(IXHarnessDesign design, IXHarnessProcessingOutput output)
	{
		double weight = 0;

		// Weigh connectors
		for (IXConnector conn : design.getConnectivity().getConnectors()) {
			weight += computeObjectWeight(conn);
		}

		// Weigh Wires
		for (IXBundle bundle : getAllBundles(design)) {
			double length = Double.parseDouble(bundle.getAttribute(IXAttributes.Length)) / 1000;
			for (IXAbstractConductor conductor : bundle.getConductorsAtOffset(length / 2)) {
				weight += computeObjectWeight(conductor);
			}
		}

		// Weigh Insulation
		for (IXInsulation insulation : getAllInsulations(design)) {
			System.out.print(insulation.getAttribute(IXAttributes.InsulationType) + ": ");
			System.out.print(insulation.getAttribute(IXAttributes.PartNumber) + ": ");

			IXLibraryObject object = appContext.getLibrary().getLibraryObject(
					insulation.getAttribute(IXAttributes.PartNumber));
			if (object != null) {
				double kgm = Double.parseDouble(object.getAttribute(IXAttributes.Weight));
				double len = 0;

				if ("Spiral Tape".equals(insulation.getAttribute(IXAttributes.InsulationType)) ||
						"Overlap Tape".equals(insulation.getAttribute(IXAttributes.InsulationType)) ||
						"Space Tape".equals(insulation.getAttribute(IXAttributes.InsulationType))) {
					len = Double.parseDouble(insulation.getAttribute(IXAttributes.UnmodifiedLength)) / 1000;
				}
				if ("Selected Tube".equals(insulation.getAttribute(IXAttributes.InsulationType))) {
					len = Double.parseDouble(insulation.getAttribute(IXAttributes.InsulatedLength)) / 1000;
				}

				weight += len * kgm;
				System.out.println(String.format("%f, <%f, %f>", len * kgm, len, kgm));
			}
		}

		System.out.println(weight);

		return true;
	}

	private double computeObjectWeight(IXLibrariedObject object)
	{
		IXLibraryObject libObj = object.getLibraryObject();
		if (libObj != null) {
			String value = libObj.getAttribute(IXAttributes.Weight);
			try {
				return Double.parseDouble(value);
			}
			catch (NumberFormatException ignore) {
			}
		}
		return 0;
	}

	@Override
	public void setApplicationContext(IXApplicationContext applicationContext)
	{
		appContext = applicationContext;
	}
}
