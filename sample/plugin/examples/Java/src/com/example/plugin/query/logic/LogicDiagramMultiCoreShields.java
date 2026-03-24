package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramMultiCore;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramMultiCoreShields extends BaseCustomResultExpression
{

	public LogicDiagramMultiCoreShields()
	{
		super("LogicDiagramMultiCoreShields", "1.0", "Get List of Shields attched to a Multicore?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Multicore};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String multiCoreShiledNames = "";
		if (entity instanceof IXLogicDiagramMultiCore) {
			Set<IXLogicDiagramConductor> xShileds = ((IXLogicDiagramMultiCore) entity).getAllDirectlyConnectedShields();
			for (IXLogicDiagramConductor xShiled : xShileds) {
				String name = getObjectAttribute(xShiled, "Name");
				if (!multiCoreShiledNames.isEmpty()) {
					multiCoreShiledNames += " % ";
				}
				multiCoreShiledNames += name;
			}
		}
		LogExit(entity, multiCoreShiledNames);
		return multiCoreShiledNames;
	}
}
