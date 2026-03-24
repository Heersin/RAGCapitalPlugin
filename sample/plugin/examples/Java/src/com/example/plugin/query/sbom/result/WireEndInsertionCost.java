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
import com.mentor.chs.api.IXAbstractPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWireEnd;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.plugin.query.IXSBOMContextResultExpression;
import com.mentor.chs.plugin.sbom.IXSBOMEvaluationContext;

import java.util.Collection;

/**
 * Example result expression based on evaluating whether a given wire end object is being inserted into a connector in
 * the current sub-assembly. If so, the cost is 1.5 * engineered wire length.
 */
public class WireEndInsertionCost extends BaseCustomResultExpression implements IXSBOMContextResultExpression
{

	private static final double CONST = 1.5;

	public WireEndInsertionCost()
	{
		super("Wire End Insertion Cost", "1.1", "Wire End Insertion Cost");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.WireEnd};
	}

	public Object evaluate(IXObject entity, IXSBOMEvaluationContext context)
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
			if (connectorOwner != wireEndOwner && children.contains(connectorOwner) &&
					children.contains(wireEndOwner)) {

				String length = xWireEnd.getConductor().getAttribute("modifiedlength");
				if (length != null && !length.trim().isEmpty()) {
					return CONST * Double.parseDouble(length);
				}
			}
			return null;
		}
		return false;
	}
}
