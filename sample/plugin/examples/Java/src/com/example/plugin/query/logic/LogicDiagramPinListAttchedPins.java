package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramPinListAttchedPins extends BaseCustomResultExpression
{

	public LogicDiagramPinListAttchedPins()
	{
		super("LogicDiagramPinListAttchedPins", "1.0", "Get List of Pins attached to a PinList?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.LogicDiagramPinList};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String attPinNames = "";
		if (entity instanceof IXLogicDiagramPinList) {
			Set<IXLogicDiagramPin> xAttPins = ((IXLogicDiagramPinList) entity).getPins();
			for (IXLogicDiagramPin xAttPin : xAttPins) {
				String name = getObjectAttribute(xAttPin, "Name");
				if (!attPinNames.isEmpty()) {
					attPinNames += " % ";
				}
				attPinNames += name;
			}
		}
		LogExit(entity, attPinNames);
		return attPinNames;
	}
}
