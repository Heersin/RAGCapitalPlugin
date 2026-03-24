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
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class NumOfConductorsAttachedToPin extends BaseCustomResultExpression
{

	public NumOfConductorsAttachedToPin()
	{
		super("NumOfConductorsAttachedToPin", "1.0", "Returns the number of conductors attached to the pin.");
	}

	public Context[] getApplicableContexts()
	{
		return new IXCustomExpression.Context[]{IXCustomExpression.Context.Pin};
	}

	public Object evaluate(IXObject obj)
	{
		LogEntry(obj);
		if (obj instanceof IXLogicDiagramPin) {
			String val = ((Integer) (((IXLogicDiagramPin) obj).getConductors().size())).toString();
			LogExit(obj, val);
			return val;
		}
		LogExit(obj, "");
		return "";
	}
}

