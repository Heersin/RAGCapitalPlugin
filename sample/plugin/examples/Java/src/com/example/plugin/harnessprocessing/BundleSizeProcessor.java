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
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.IXDesignLogSeverityEnum;
import com.mentor.chs.plugin.harness.IXHarnessEngineering;
import com.mentor.chs.plugin.harness.IXHarnessEngineeringTypeEnum;
import com.mentor.chs.plugin.harness.IXHarnessProcessingOutput;

import java.util.List;
import java.util.Set;

/**
 * Example bundle size harness processor, using wire CSA and packing factor
 */
public class BundleSizeProcessor extends AbstractHarnessProcessor implements IXHarnessEngineering
{

	private static final double HCP_PACKING_FACTOR = 1.1026577908435840990226529966259;

	public BundleSizeProcessor()
	{
		super("Example bundle size harness processor",
				"1.0",
				"Sample bundle size harness processor implementation");
	}

	@Override
	public IXHarnessEngineeringTypeEnum getType()
	{
		return IXHarnessEngineeringTypeEnum.BundleNodeSizes;
	}


	@Override
	public boolean process(IXHarnessDesign design, IXHarnessProcessingOutput output)
	{
		output.log(IXDesignLogSeverityEnum.Information, getName() + " " + getVersion());

		Set<IXBundle> bundles = getAllBundles(design);
		output.log(IXDesignLogSeverityEnum.Information, String.format("Bundles: %d\n", bundles.size()));

		processBundles(output, bundles);

		return true;
	}

	private void processBundles(IXHarnessProcessingOutput output, Set<IXBundle> bundles)
	{
		for (IXBundle bundle : bundles) {
			// Retrieve the points (by offset from node) on the bundle between which the
			// bundle size can change (e.g. bundle size will generally change on each side of a splice)
			List<Double> offsets = computeSpliceOffsets(bundle);

			// Compute the maximum sum of wire CSA between each point
			double sumCSA = computeMaxCSA(output, bundle, offsets);
			double diameter = Math.sqrt(HCP_PACKING_FACTOR * 4 * sumCSA / Math.PI);

			setBundleDiameter(bundle, diameter);

			output.log(IXDesignLogSeverityEnum.Information,
					String.format("\tBundle Width: %f", diameter));
		}
	}

	private double computeMaxCSA(IXHarnessProcessingOutput output, IXBundle bundle, List<Double> offsets)
	{
		double maxSumCSA = 0;
		Double last = null;

		for (Double offset : offsets) {
			if (last != null) {
				double dOffset = (last + offset) / 2;
				Set<IXAbstractConductor> conductors = bundle.getConductorsAtOffset(dOffset);
				output.log(IXDesignLogSeverityEnum.Information,
						String.format("Offset %f, No. Cond. %d", dOffset, conductors.size()));

				double sumCSA = 0;
				for (IXAbstractConductor ixac : conductors) {
					sumCSA += Double.parseDouble(ixac.getAttribute(IXAttributes.WireCSA));
				}
				output.log(IXDesignLogSeverityEnum.Information,
						String.format("CSA: %f", sumCSA));

				if (sumCSA > maxSumCSA) {
					maxSumCSA = sumCSA;
				}
			}
			last = offset;
		}

		return maxSumCSA;
	}

	private boolean setBundleDiameter(IXBundle bun, double diameter)
	{
		IXAttributeSetter setter = bun.getAttributeSetter();

		if (setter == null) {
			return false;
		}

		setter.addAttribute(IXAttributes.Width, Double.toString(diameter));

		return true;
	}
}
