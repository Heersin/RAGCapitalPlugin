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
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.IXDesignLogSeverityEnum;
import com.mentor.chs.plugin.harness.IXHarnessEngineering;
import com.mentor.chs.plugin.harness.IXHarnessEngineeringTypeEnum;
import com.mentor.chs.plugin.harness.IXHarnessProcessingOutput;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class InsulationTotalsProcessor extends AbstractHarnessProcessor
		implements IXApplicationContextListener, IXHarnessEngineering
{

	private IXApplicationContext appContext;

	public InsulationTotalsProcessor()
	{
		super("Example insulation totals harness processor",
				"1.0",
				"Sample insulation totals harness processor implementation");
	}

	@Override
	public IXHarnessEngineeringTypeEnum getType()
	{
		return IXHarnessEngineeringTypeEnum.InsulationTotals;
	}

	@Override
	public boolean process(IXHarnessDesign design, IXHarnessProcessingOutput output)
	{
		output.log(IXDesignLogSeverityEnum.Information, getName() + " " + getVersion());

		Set<IXBundle> bundles = getAllBundles(design);
		Set<IXInsulationRun> insulationRuns = getAllInsulationRuns(design);

		output.log(IXDesignLogSeverityEnum.Information, String.format("Bundles: %d\nInsulation runs: %d",
				bundles.size(), insulationRuns.size()));

		processBundles(output, bundles, insulationRuns);

		return true;
	}

	private void processBundles(IXHarnessProcessingOutput output, Set<IXBundle> bundles,
			Set<IXInsulationRun> insulationRuns)
	{
		for (IXBundle bundle : bundles) {
			double diameter = Double.parseDouble(bundle.getAttribute("Width"));

			double ins_thickness = getBundleThicknessWithInsulation(output, insulationRuns, bundle, diameter);

			setBundleDiameterWithInsulation(bundle, ins_thickness);

			output.log(IXDesignLogSeverityEnum.Information,
					String.format("\tBundle Width: %f", diameter));
		}
	}

	private double getBundleThicknessWithInsulation(IXHarnessProcessingOutput output,
			Set<IXInsulationRun> insulationRuns, IXBundle bundle, double diameter)
	{
		int appl_ins = 0;
		double insulationThickness = diameter;

		for (IXInsulationRun run : getInsulationRunsOnBundle(bundle, insulationRuns)) {

			for (IXInsulation insulation : run.getInsulations()) {
				String itype = insulation.getAttribute(IXAttributes.InsulationType);

				if ("Overlap Tape".equals(itype)) {
					double dist = 100.0 / Double.parseDouble(
							insulation.getAttribute(IXAttributes.DistanceBetweenItemsPercentage));
					double overlap_layers = Math.ceil(dist);

					insulationThickness += 2 * overlap_layers *
							Double.parseDouble(insulation.getAttribute(IXAttributes.Thickness));

					double L = Double.parseDouble(bundle.getAttribute(IXAttributes.Length));
					double G = Double.parseDouble(insulation.getAttribute(IXAttributes.DistanceBetweenItems));
					double D = insulationThickness;
					double C = (L / G) * Math.sqrt(Math.pow(G, 2) + Math.pow(Math.PI * D, 2));

					System.out.println(
							String.format("Bundle Length:%f, Gap Size:%f, Bundle Width:%f, Tape Length: %f",
									L, G, D, C));

					IXAttributeSetter setter = insulation.getAttributeSetter();
					setter.addAttribute(IXAttributes.UnmodifiedLength, Double.toString(C));

					appl_ins++;
				}
				if ("Spiral Tape".equals(itype)) {
					insulationThickness += 2 * Double.parseDouble(insulation.getAttribute(IXAttributes.Thickness));

					double L = Double.parseDouble(bundle.getAttribute(IXAttributes.Length));
					double G = Double.parseDouble(insulation.getAttribute(IXAttributes.DistanceBetweenItems));
					double D = insulationThickness;
					double C = (L / G) * Math.sqrt(Math.pow(G, 2) + Math.pow(Math.PI * D, 2));

					System.out.println(
							String.format("Bundle Length:%f, Gap Size:%f, Bundle Width:%f, Tape Length: %f",
									L, G, D, C));

					IXAttributeSetter setter = insulation.getAttributeSetter();
					setter.addAttribute(IXAttributes.UnmodifiedLength, Double.toString(C));

					appl_ins++;
				}
				if ("Space Tape".equals(itype)) {
					insulationThickness += 2 * Double.parseDouble(insulation.getAttribute(IXAttributes.Thickness))
							* Double.parseDouble(insulation.getAttribute(IXAttributes.NumberOfOverlaps));

					double C = 2 * Math.PI * insulationThickness *
							Double.parseDouble(insulation.getAttribute(IXAttributes.NumberOfOverlaps));

					IXAttributeSetter setter = insulation.getAttributeSetter();
					setter.addAttribute(IXAttributes.UnmodifiedLength, Double.toString(C));

					appl_ins++;
				}
				if ("Selected Tube".equals(itype)) {
					IXAttributeSetter setter = insulation.getAttributeSetter();

					Set<IXLibraryObject> tubes = appContext.getLibrary().getLibraryObjects("TUBE", "");
					List<IXLibraryObject> potential_tubes = new LinkedList<IXLibraryObject>();

					for (IXLibraryObject tube : tubes) {
						if (tube.getAttribute(IXAttributes.BoreSize) != null &&
								tube.getAttribute(IXAttributes.WallThickness) != null) {
							if (Double.parseDouble(tube.getAttribute(IXAttributes.BoreSize)) >
									insulationThickness + diameter) {
								potential_tubes.add(tube);
							}
						}
					}

					double min_size = Double.POSITIVE_INFINITY;
					IXLibraryObject ixlo = null;

					for (IXLibraryObject tube : potential_tubes) {
						if (Double.parseDouble(tube.getAttribute(IXAttributes.BoreSize)) < min_size) {
							min_size = Double.parseDouble(tube.getAttribute(IXAttributes.BoreSize));
							ixlo = tube;
						}
					}

					if (ixlo != null) {
						setter.addAttribute(IXAttributes.Width, ixlo.getAttribute(IXAttributes.BoreSize));
						setter.addAttribute(IXAttributes.Thickness, ixlo.getAttribute(IXAttributes.WallThickness));
						setter.addAttribute(IXAttributes.PartNumber, ixlo.getAttribute(IXAttributes.PartNumber));
						setter.addAttribute(IXAttributes.InsulatedLength, bundle.getAttribute(IXAttributes.Length));

						insulationThickness = Double.parseDouble(ixlo.getAttribute(IXAttributes.BoreSize)) +
								2 * Double.parseDouble(ixlo.getAttribute(IXAttributes.WallThickness));

						appl_ins++;
					}
				}
			}
		}

		output.log(IXDesignLogSeverityEnum.Information,
				String.format("Bundle:%s :: Insulations %d", bundle.getAttribute(IXAttributes.Name), appl_ins));

		return insulationThickness;
	}

	private boolean setBundleDiameterWithInsulation(IXBundle bun, double diameter)
	{
		IXAttributeSetter setter = bun.getAttributeSetter();

		if (setter == null) {
			return false;
		}

		setter.addAttribute(IXAttributes.WidthWithInsulations, Double.toString(diameter));
		setter.addAttribute(IXAttributes.MaxBundleWidth, Double.toString(diameter));

		return true;
	}

	@Override
	public void setApplicationContext(IXApplicationContext applicationContext)
	{
		appContext = applicationContext;
	}
}
