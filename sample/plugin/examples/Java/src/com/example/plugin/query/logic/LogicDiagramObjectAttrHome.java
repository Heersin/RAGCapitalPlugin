package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramObjectAttrHome extends BaseCustomResultExpression
{

	public LogicDiagramObjectAttrHome()
	{
		super("LogicDiagramObjectAttrHome", "1.0", "Checks the Home Condition of the Object.");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.LogicDiagramPin, Context.LogicDiagramPinList, Context.LogicDiagramConductor};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		if (entity instanceof IXLogicDiagramPinList ||
				entity instanceof IXLogicDiagramPin || entity instanceof IXLogicDiagramConductor) {
			String str = entity.getAttribute("Home");
			LogExit(entity, str);
			return str;
		}
		LogExit(entity, "");
		return "";
	}
}
