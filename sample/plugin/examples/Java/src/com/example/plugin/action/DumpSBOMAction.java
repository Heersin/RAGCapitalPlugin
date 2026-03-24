/*
 * Copyright 2011 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */
package com.example.plugin.action;

import com.mentor.chs.plugin.action.IXSBOMAction;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXOutputWindow;
import com.mentor.chs.api.sbom.IXSBOM;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.api.IXHarnessDesign;

import java.util.Collection;

/**
 * A custom action which provides SBOM text display in the output window
 */
public class DumpSBOMAction extends BaseAction implements IXSBOMAction, IXHarnessAction
{

	public DumpSBOMAction()
	{
		super("Example Dump SBOM",
				"1.0",
				"Example custom action which displays SBOM text in output window");
	}

	public Trigger[] getTriggers()
	{
		return new Trigger[]{Trigger.MainMenu};
	}

	public boolean isAvailable(IXApplicationContext context)
	{
		return true;
	}

	public boolean execute(IXApplicationContext context)
	{
		IXOutputWindow log = context.getOutputWindow();
		if (log == null) {
			return false;
		}
		log.clear("Plugins");

		IXHarnessDesign design = (IXHarnessDesign) context.getCurrentDesign();
		IXSBOM currentSBOM = context.getCurrentSBOM();

		for (IXSBOM sbom : design.getSBOMs()) {

			// Dump SBOM Header information
			StringBuilder buff = new StringBuilder("====================== Dump SBOM: ");
			buff.append(sbom.getAttribute("Name"));
			log.println(buff.toString());
			buff = new StringBuilder("Input Pattern: ").append(sbom.getAttribute("GeneratorPatternName"));
			log.println(buff.toString());
			buff = new StringBuilder("Is Frozen: ").append(sbom.getAttribute("Frozen"));
			log.println(buff.toString());
			buff = new StringBuilder("Is Current: ").append(sbom == currentSBOM ? "true" : "false");
			log.println(buff.toString());

			// Dump sub-assemblies
			for (IXSubAssembly root : sbom.getRootSubAssemblies()) {
				dumpSubAssembly(root, log, 0);
			}
		}

		return false;
	}

	private static void dumpSubAssembly(IXSubAssembly sub, IXOutputWindow log, int depth)
	{
		// Start with depth prefix
		StringBuilder buff = new StringBuilder();
		for (int i = 0; i < depth; i++) {
			buff.append("+");
		}

		// Add sub-assembly info
		Collection<? extends IXSubAssembly> children = sub.getChildren();
		buff.append(" sub-assembly: ").append(sub.getAttribute("Name")).append(" - size: ").append(children.size());
		buff.append(" - operation: ").append(sub.getAttribute("GeneratorOperationName"));
		log.println(buff.toString());

		// Process children
		for (IXSubAssembly child : children) {
			dumpSubAssembly(child, log, depth + 1);
		}
	}
}