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
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class NumOfPinsQuery extends BaseCustomResultExpression
{

	public NumOfPinsQuery()
	{
		super("NumOfPinsQuery", "1.0", "Returns the number of Pins on the Diagram PinList.");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.LogicDiagramPinList};
	}

	public Object evaluate(IXObject obj)
	{
		LogEntry(obj);
		if (obj instanceof IXLogicDiagramPinList) {
			String val = ((Integer) (((IXLogicDiagramPinList) obj).getPins().size())).toString();
			LogExit(obj, val);
			return val;
		}
		LogExit(obj, "");
		return "";
	}
}

