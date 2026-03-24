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

import com.example.plugin.BasePlugin;
import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXBundle;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXInsulation;
import com.mentor.chs.api.IXInsulationRun;
import com.mentor.chs.api.IXNode;
import com.mentor.chs.api.IXSplice;
import com.mentor.chs.api.IXWire;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AbstractHarnessProcessor extends BasePlugin
{

	protected AbstractHarnessProcessor(String n,
			String v,
			String d)
	{
		super(n, v, d);
	}

	protected Set<IXBundle> getAllBundles(IXDesign design)
	{
		Set<IXBundle> bundles = new LinkedHashSet<IXBundle>();
		for (IXNode node : getAllNodes(design)) {
			bundles.addAll(node.getBundles());
		}
		return bundles;
	}

	protected Set<IXInsulationRun> getAllInsulationRuns(IXDesign design)
	{
		Set<IXInsulationRun> insulationRuns = new LinkedHashSet<IXInsulationRun>();
		for (IXNode node : getAllNodes(design)) {
			insulationRuns.addAll(node.getInsulationRuns());
		}
		return insulationRuns;
	}

	protected Set<IXInsulation> getAllInsulations(IXDesign design)
	{
		Set<IXInsulation> insulations = new LinkedHashSet<IXInsulation>();
		for (IXInsulationRun insulationRun : getAllInsulationRuns(design)) {
			insulations.addAll(insulationRun.getInsulations());
		}
		return insulations;
	}

	protected List<Double> computeSpliceOffsets(IXBundle bundle)
	{
		List<Double> offsets = new ArrayList<Double>();
		offsets.add(0.0);
		for (IXNode node : bundle.getNodes()) {
			IXSplice splice = node.getSplice();
			if (splice != null) {
				offsets.add(Double.valueOf(splice.getAttribute("BundleOffset")));
			}
		}
		offsets.add(Double.valueOf(bundle.getAttribute(IXAttributes.Length)));
		return offsets;
	}

	protected Set<IXInsulationRun> getInsulationRunsOnBundle(IXBundle bundle, Set<IXInsulationRun> allInsulationRuns)
	{
		Set<IXInsulationRun> bundleRuns = new HashSet<IXInsulationRun>();

		for (IXInsulationRun run : allInsulationRuns) {
			Set<IXNode> runNodes = run.getNodes();

			boolean bundleNodeBelongsToRun = true;
			for (IXNode node : bundle.getNodes()) {
				if (!runNodes.contains(node)) {
					bundleNodeBelongsToRun = false;
					break;
				}
			}

			if (bundleNodeBelongsToRun) {
				bundleRuns.add(run);
			}
		}

		return bundleRuns;
	}

	private Set<IXNode> getAllNodes(IXDesign design)
	{
		Set<IXNode> nodes = new LinkedHashSet<IXNode>();
		for (IXWire wire : design.getConnectivity().getWires()) {
			nodes.addAll(wire.getNodes());
		}
		return nodes;
	}

//	protected List<IXBundle> gatherBundles(List<IXInsulationRun> insulationRuns, IXDesign design)
//	{
//		List<IXBundle> bundleList = new LinkedList<IXBundle>();
//
//		Set<IXWire> wires = design.getConnectivity().getWires();
//		List<IXNode> nodes = new LinkedList<IXNode>();
//
//		for (IXWire wire : wires) {
//			List<IXNode> _nodes = wire.getNodes();
//
//			for (IXNode node : _nodes) {
//				if (!nodes.contains(node)) {
//					nodes.add(node);
//				}
//			}
//		}
//
//		for (IXNode node : nodes) {
//			Set<IXBundle> bundles = node.getBundles();
//
//			for (IXBundle bundle : bundles) {
//				if (!bundleList.contains(bundle)) {
//					bundleList.add(bundle);
//				}
//			}
//
//			Set<IXInsulationRun> runs = node.getInsulationRuns();
//
//			for (IXInsulationRun run : runs) {
//				if (!insulationRuns.contains(run)) {
//					insulationRuns.add(run);
//				}
//			}
//		}
//
//		return bundleList;
//	}
}
