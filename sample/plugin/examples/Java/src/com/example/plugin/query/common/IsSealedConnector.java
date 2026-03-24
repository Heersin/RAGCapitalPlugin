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
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class IsSealedConnector extends BaseCustomFilterExpression
{

	public IsSealedConnector()
	{
		super("IsSealedConnector", "1.0", "Checks Whether the conductor Object is connected to only 2 pins.");
	}

	public IXCustomExpression.Context[] getApplicableContexts()
	{
		return new IXCustomExpression.Context[]{IXCustomExpression.Context.Connector};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		LogEntry(obj);
		if (obj instanceof IXConnector) {
			IXConnector dev = (IXConnector) obj;
			String selaed = dev.getAttribute("PlugsRequired");
			if ((selaed != null) && selaed.equalsIgnoreCase("true")) {
				LogExit(obj, "true");
				return true;
			}
		}
		LogExit(obj, "false");
		return false;
	}
}

