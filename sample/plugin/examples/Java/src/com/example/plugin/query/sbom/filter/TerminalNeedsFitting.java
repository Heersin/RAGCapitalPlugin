/*
 * Copyright 2012 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */
package com.example.plugin.query.sbom.filter;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXTerminal;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.plugin.query.IXSBOMContextFilterExpression;
import com.mentor.chs.plugin.sbom.IXSBOMEvaluationContext;

import java.util.Collection;

/**
 * Example filter expression based on evaluating whether a given terminal object is being "brought together" with its single wire end
 * in the current sub-assembly. If so, return's true, otherwise false.
 */
public class TerminalNeedsFitting extends BaseCustomFilterExpression implements IXSBOMContextFilterExpression
{

	public TerminalNeedsFitting()
	{
		super("Fit Single Terminal", "1.0",
				"Determine if a terminal is being applied in the current sub-assembly");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.Terminal};
	}

	public boolean isSatisfiedBy(IXObject entity, IXSBOMEvaluationContext context)
	{
		if (context == null) {
			return false;
		}
		if (entity instanceof IXTerminal) {
			IXTerminal term = (IXTerminal) entity;
			// More than 1 wire? If so, it's not applicable.
			Collection<IXAbstractConductor> wires = term.getCavityDetail().getOwner().getConductors();
			if (wires.size() > 1) {
				return false;
			}

			// Get parent sub for terminal
			IXSubAssembly termOwner =
					context.getSignificantParent(context.getSBOMContext().getSubAssembly(term));
			// Get parent sub for wire
			IXSubAssembly wireOwner = context.getSignificantParent(context.getSBOMContext().getSubAssembly(
					wires.iterator().next()));

			Collection<? extends IXSubAssembly> children = context.getCurrentSubAssembly().getChildren();
			// If both are children of the current sub, it's OK
			return termOwner != wireOwner && children.contains(termOwner) && children.contains(wireOwner);
		}
		return false;
	}
}
