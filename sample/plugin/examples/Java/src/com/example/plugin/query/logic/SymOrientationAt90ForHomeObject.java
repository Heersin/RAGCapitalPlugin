package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 * Created by IntelliJ IDEA. User: chandras Date: Sep 3, 2009 Time: 2:25:35 PM To change this template use File |
 * Settings | File Templates.
 */
public class SymOrientationAt90ForHomeObject extends BaseCustomResultExpression
{

	public SymOrientationAt90ForHomeObject()
	{
		super("SymOrientationAt90ForHomeObject", "1.0", "Returns 90 degree orientation for Home Objects");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.LogicDiagramPin, Context.LogicDiagramPinList,
				Context.LogicDiagramConductor};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		if (entity instanceof IXLogicDiagramPinList || entity instanceof IXLogicDiagramPin ||
				entity instanceof IXLogicDiagramConductor) {
			String str = entity.getAttribute("Home");
			boolean res = Boolean.valueOf(str);
			String angle = res ? "90" : "0";
			LogExit(entity, angle);
			return angle;
		}
		LogExit(entity, "0");
		return "0";
	}
}
