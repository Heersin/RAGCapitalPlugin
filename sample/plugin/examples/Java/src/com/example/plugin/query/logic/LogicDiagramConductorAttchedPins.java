package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramConductorAttchedPins extends BaseCustomResultExpression
{

	public LogicDiagramConductorAttchedPins()
	{
		super("LogicDiagramConductorAttchedPins", "1.0", "Get List of Pins attched to a Conductor?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.LogicDiagramConductor};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String condPinNames = "";
		if (entity instanceof IXLogicDiagramConductor) {
			Set<IXLogicDiagramPin> xPins = ((IXLogicDiagramConductor) entity).getConnectedPins();
			for (IXLogicDiagramPin xPin : xPins) {
				String name = getObjectAttribute(xPin, "Name");
				if (!condPinNames.isEmpty()) {
					condPinNames += " % ";
				}
				condPinNames += name;
			}
		}
		LogExit(entity, condPinNames);
		return condPinNames;
	}
}
