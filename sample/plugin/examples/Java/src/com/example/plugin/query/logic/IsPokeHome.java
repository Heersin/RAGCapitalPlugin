/*
 * Copyright 2004-2008 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */
package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXDevice;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class IsPokeHome extends BaseCustomFilterExpression
{

	public IsPokeHome()
	{
		super("IsPokeHome", "1.0", "Checks the Poke Home Condition of the Object.");
	}

	public IXCustomExpression.Context[] getApplicableContexts()
	{
		return new IXCustomExpression.Context[]{IXCustomExpression.Context.Device};
	}


	public boolean isSatisfiedBy(IXObject obj)
	{
		LogEntry(obj);
		if (obj instanceof IXDevice) {
			String str = obj.getAttribute("PokeHome");
			boolean res = (str != null && !str.isEmpty());
			LogExit(obj, ((Boolean) res).toString());
			return res;
		}
		LogExit(obj, "false");
		return false;
	}
}

