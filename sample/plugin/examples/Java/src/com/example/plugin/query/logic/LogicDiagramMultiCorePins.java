package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramMultiCore;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramMultiCorePins extends BaseCustomResultExpression
{

	public LogicDiagramMultiCorePins()
	{
		super("LogicDiagramMultiCorePins", "1.0", "Get List of Pins attched to a Multicore?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Multicore};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String multiCorePinNames = "";
		if (entity instanceof IXLogicDiagramMultiCore) {
			Set<IXLogicDiagramPin> xPins = ((IXLogicDiagramMultiCore) entity).getAllConnectedDiagramPins();
			for (IXLogicDiagramPin xPin : xPins) {
				String name = getObjectAttribute(xPin, "Name");
				if (!multiCorePinNames.isEmpty()) {
					multiCorePinNames += " % ";
				}
				multiCorePinNames += name;
			}
		}
		LogExit(entity, multiCorePinNames);
		return multiCorePinNames;
	}
}
