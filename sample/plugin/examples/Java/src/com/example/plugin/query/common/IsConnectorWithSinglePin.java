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
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class IsConnectorWithSinglePin extends BaseCustomFilterExpression
{

	public IsConnectorWithSinglePin()
	{
		super("IsConnectorWithSinglePin", "1.0", "Checks if Connector Object is having single pin?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Connector};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{

		LogEntry(obj);

		IXConnector xConn = null;
		if (obj instanceof IXLogicDiagramPinList &&
				((IXDiagramObject) obj).getConnectivity() instanceof IXConnector) {
			xConn = (IXConnector) ((IXDiagramObject) obj).getConnectivity();
		}
		else if (obj instanceof IXConnector) {
			xConn = (IXConnector) obj;
		}

		if (xConn != null) {
			Set<IXAbstractPin> pins = xConn.getPins();
			boolean res = (pins.size() == 1);
			LogExit(obj, ((Boolean) res).toString());
			return res;
		}
		LogExit(obj, "false");
		return false;
	}
}
