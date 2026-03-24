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
import com.mentor.chs.api.IXAbstractPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWireEnd;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.plugin.query.IXSBOMContextFilterExpression;
import com.mentor.chs.plugin.sbom.IXSBOMEvaluationContext;

import java.util.Collection;

/**
 * Example filter expression based on evaluating whether a given wire end object is being inserted into a connector in
 * the current sub-assembly. If so, return's true, otherwise false.
 */
public class WireEndIsBeingInserted extends BaseCustomFilterExpression implements IXSBOMContextFilterExpression
{

	public WireEndIsBeingInserted()
	{
		super("Wire End Being Inserted", "1.0",
				"Determine if a wire end is being inserted in the current sub-assembly");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.WireEnd};
	}

	public boolean isSatisfiedBy(IXObject entity, IXSBOMEvaluationContext context)
	{
		if (context == null) {
			return false;
		}
		if (entity instanceof IXWireEnd) {
			IXWireEnd xWireEnd = (IXWireEnd) entity;
			// Get its connector
			IXAbstractPinList pinList = xWireEnd.getPin().getOwner();
			IXSubAssembly connectorOwner =
					context.getSignificantParent(context.getSBOMContext().getSubAssembly(pinList));
			IXSubAssembly wireEndOwner =
					context.getSignificantParent(context.getSBOMContext().getSubAssembly(xWireEnd));

			Collection<? extends IXSubAssembly> children = context.getCurrentSubAssembly().getChildren();
			// If both are children of the current sub, it's OK
			return connectorOwner != wireEndOwner && children.contains(connectorOwner) &&
					children.contains(wireEndOwner);
		}
		return false;
	}
}
