/*
 * Copyright 2004-2008 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.   
 */
package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class NumOfPinsAttachedToConductor extends BaseCustomResultExpression
{

	public NumOfPinsAttachedToConductor()
	{
		super("NumOfPinsAttachedToConductor", "1.0", "Returns the number of pins attached to the conductor.");
	}

	public Context[] getApplicableContexts()
	{
		return new IXCustomExpression.Context[]{IXCustomExpression.Context.Conductor};
	}

	public Object evaluate(IXObject obj)
	{
		LogEntry(obj);
		if (obj instanceof IXLogicDiagramConductor) {
			String val = ((Integer) (((IXLogicDiagramConductor) obj).getConnectedPins().size())).toString();
			LogExit(obj, val);
			return val;
		}
		LogExit(obj, "");
		return "";
	}
}

