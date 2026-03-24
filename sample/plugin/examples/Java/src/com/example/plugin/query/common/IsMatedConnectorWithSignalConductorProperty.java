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
import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXDevice;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class IsMatedConnectorWithSignalConductorProperty extends BaseCustomFilterExpression
{

	public IsMatedConnectorWithSignalConductorProperty()
	{
		super("IsMatedConnectorWithSignalConductorProperty", "1.0",
				"Checks Whether the device/connector has mated Connector with a pin attchaed to a conductor with signal property?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.Connector, Context.Device};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		LogEntry(obj);
		if (obj instanceof IXDevice) {
			IXDevice dev = (IXDevice) obj;
			Set<IXConnector> conns = dev.getMatedConnectors();
			for (IXConnector con : conns) {
				Set<IXAbstractPin> pins = con.getPins();
				for (IXAbstractPin pin : pins) {
					Set<IXAbstractConductor> conds = pin.getConductors();
					for (IXAbstractConductor cond : conds) {
						String sigProp = cond.getProperty("Signal");
						if ((sigProp != null) && !sigProp.isEmpty()) {
							LogExit(obj, "true");
							return true;
						}
					}
				}
			}
		}
		LogExit(obj, "false");
		return false;
	}
}

