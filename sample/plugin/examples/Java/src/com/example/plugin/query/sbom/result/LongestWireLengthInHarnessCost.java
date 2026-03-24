/*
 * Copyright 2012 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */
package com.example.plugin.query.sbom.result;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.plugin.query.IXSBOMContextResultExpression;
import com.mentor.chs.plugin.sbom.IXSBOMEvaluationContext;

/**
 * Example result expression based on evaluating the longest wire length in the current harness.
 */
public class LongestWireLengthInHarnessCost extends BaseCustomResultExpression implements IXSBOMContextResultExpression
{

	public LongestWireLengthInHarnessCost()
	{
		super("Longest Wire Length In Harness Cost", "1.1", "Longest Wire Length In Harness Cost");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.SubAssembly};
	}

	public Object evaluate(IXObject entity, IXSBOMEvaluationContext context)
	{
		if (context == null) {
			return false;
		}
		if (entity instanceof IXSubAssembly) {
			// Use helper method to get the cached value for the longest wire in the current SBOM's harness
			return context.getLongestWireLengthInHarness();
		}
		return false;
	}
}
