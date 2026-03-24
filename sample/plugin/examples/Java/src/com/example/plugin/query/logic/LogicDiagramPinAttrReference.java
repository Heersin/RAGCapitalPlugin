package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramPinAttrReference extends BaseCustomResultExpression
{

	public LogicDiagramPinAttrReference()
	{
		super("LogicDiagramPinAttrReference", "1.0", "Checks if Object is a ReferencePin.");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.LogicDiagramPin};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		if (entity instanceof IXLogicDiagramPin) {
			String str = entity.getAttribute("Reference");
			LogExit(entity, str);
			return str;
		}
		LogExit(entity, "");
		return "";
	}
}
