/*
 * Copyright 2009 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.   
 */
package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXObject;

public class HarnessConstantOffset extends BaseCustomResultExpression
{

	public HarnessConstantOffset()
	{
		super("Constant Offset", "1.1",
				"Adds a constant (for the harness) property which captures the node dimension value for the harness");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.NodeDimension, Context.AxialDimension};
	}

	public Object evaluate(IXObject entity)
	{
		String value = null;
		if (entity instanceof IXDiagramObject) {
			IXDiagramObject dObject = (IXDiagramObject) entity;
			value = dObject.getDiagram().getProperty("Constant Offset");
			IXObject object = dObject.getConnectivity();
			String dimensionValue = object.getProperty("Constant Offset");
			value = dimensionValue == null ? value : dimensionValue;
		}
		return value == null ? "+5" : value;
	}
}
