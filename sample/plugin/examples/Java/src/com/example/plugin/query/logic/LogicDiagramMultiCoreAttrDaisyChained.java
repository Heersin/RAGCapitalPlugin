package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramMultiCore;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramMultiCoreAttrDaisyChained extends BaseCustomResultExpression
{

	public LogicDiagramMultiCoreAttrDaisyChained()
	{
		super("LogicDiagramMultiCoreAttrDaisyChained", "1.0", "Checks if the Multicore Object is DaisyChained?.");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Multicore};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		if (entity instanceof IXLogicDiagramMultiCore) {
			String str = entity.getAttribute("DaisyChained");
			LogExit(entity, str);
			return str;
		}
		LogExit(entity, "");
		return "";
	}
}
