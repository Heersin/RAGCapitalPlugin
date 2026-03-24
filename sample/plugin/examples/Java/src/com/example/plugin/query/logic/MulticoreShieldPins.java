package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramMultiCore;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXObject;

import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: svsn Date: Nov 2, 2009 Time: 4:55:58 PM To change this template use File | Settings |
 * File Templates.
 */
public class MulticoreShieldPins extends BaseCustomResultExpression
{

	public MulticoreShieldPins()
	{
		super("GetMulticoreShieldPins", "1.0", "Get the shiled pin names  of a multicore");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.LogicDiagramMultiCore};
	}

	public Object evaluate(IXObject entity)
	{
		StringBuilder s = new StringBuilder("Shield Pins: ");
		if (entity instanceof IXLogicDiagramMultiCore) {
			IXLogicDiagramMultiCore diagramMulticore = (IXLogicDiagramMultiCore) entity;
			Set<IXLogicDiagramPin> shieldPins = diagramMulticore.getAllConnectedDiagramPins();
			boolean appendSpecial = shieldPins.size() > 1;
			for (IXLogicDiagramPin pin : shieldPins) {
				s.append(pin.getConnectivity().getAttribute("Name"));
				if (appendSpecial) {
					s.append('/');
				}
			}
		}

		return s.toString();
	}
}