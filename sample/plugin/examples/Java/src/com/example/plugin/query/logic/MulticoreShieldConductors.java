package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramMultiCore;
import com.mentor.chs.api.IXObject;

import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: svsn Date: Nov 2, 2009 Time: 4:55:58 PM To change this template use File | Settings |
 * File Templates.
 */
public class MulticoreShieldConductors extends BaseCustomResultExpression
{

	public MulticoreShieldConductors()
	{
		super("GetMulticoreShieldConductors", "1.0", "Get the shiled conductor names  of a multicore");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.LogicDiagramMultiCore};
	}

	public Object evaluate(IXObject entity)
	{
		StringBuilder s = new StringBuilder("Shield Conds: ");
		if (entity instanceof IXLogicDiagramMultiCore) {
			IXLogicDiagramMultiCore diagramMulticore = (IXLogicDiagramMultiCore) entity;
			Set<IXLogicDiagramConductor> shieldConds = diagramMulticore.getAllDirectlyConnectedShields();
			boolean appendSpecial = shieldConds.size() > 1;
			for (IXLogicDiagramConductor pin : shieldConds) {
				s.append(pin.getConnectivity().getAttribute("Name"));
				if (appendSpecial) {
					s.append('/');
				}
			}
		}

		return s.toString();
	}
}