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

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXDevice;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class IsHomeQuery extends BaseCustomFilterExpression
{

	public IsHomeQuery()
	{
		super("IsHomeQuery", "1.0", "Checks the Home Condition of the Object.");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Device};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		LogEntry(obj);
		if (obj instanceof IXLogicDiagramPinList) {
			boolean res = ((IXLogicDiagramPinList) obj).getAttribute("Home").equals("true");
			LogExit(obj, ((Boolean) res).toString());
			return res;
		}
		if (obj instanceof IXDevice) {
			boolean res = obj.getAttribute("Name").equalsIgnoreCase("Dev1");
			LogExit(obj, ((Boolean) res).toString());
			return res;
		}
		LogExit(obj, "false");
		return false;
	}
}

