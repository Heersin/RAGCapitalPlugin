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
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.IXDesignLogSeverityEnum;
import com.mentor.chs.plugin.harness.IXHarnessEngineering;
import com.mentor.chs.plugin.harness.IXHarnessEngineeringTypeEnum;
import com.mentor.chs.plugin.harness.IXHarnessProcessingOutput;

import java.util.ArrayList;
import java.util.List;

public class WireLengthProcessor extends AbstractHarnessProcessor implements IXHarnessEngineering
{

	public WireLengthProcessor()
	{
		super("Example wire length harness processor",
				"1.0",
				"Sample wire length harness processor implementation");
	}

	@Override
	public IXHarnessEngineeringTypeEnum getType()
	{
		return IXHarnessEngineeringTypeEnum.WireMulticoreLength;
	}

	@Override
	public boolean process(IXHarnessDesign design, IXHarnessProcessingOutput output)
	{
		List<IXWire> wires = new ArrayList<IXWire>(design.getConnectivity().getWires());
		double[] lens = new double[wires.size()];
		for (int idx = 0; idx < wires.size(); idx++) {
			lens[idx] = 0;
		}

		for (IXBundle bundle : getAllBundles(design)) {
			double len = Double.parseDouble(bundle.getAttribute(IXAttributes.Length));

			for (IXAbstractConductor wire : bundle.getConductorsAtOffset(len / 2)) {
				lens[wires.indexOf(wire)] += len;
			}
		}

		for (int idx = 0; idx < wires.size(); idx++) {
			IXAttributeSetter setter = wires.get(idx).getAttributeSetter();
			String lengthValue = Double.toString(lens[idx]);
			setter.addAttribute(IXAttributes.UnmodifiedLength, lengthValue);
			output.log(IXDesignLogSeverityEnum.Information,
					String.format("%s - %f", wires.get(idx).getAttribute(IXAttributes.Name), lens[idx]));
		}

		return true;
	}
}