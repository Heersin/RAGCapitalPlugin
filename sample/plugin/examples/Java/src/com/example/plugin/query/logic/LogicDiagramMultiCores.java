package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.IXLogicDiagramMultiCore;
import com.mentor.chs.api.IXLogicDiagramObject;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramMultiCores extends BaseCustomResultExpression
{

	public LogicDiagramMultiCores()
	{
		super("LogicDiagramMultiCores", "1.0", "Get List of Multicores available on the diagram?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.LogicDiagramPinList};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String multiCoreNames = "";
		if (entity instanceof IXLogicDiagramPinList) {
			IXDiagram xDiagram = ((IXDiagramObject) entity).getDiagram();
			if (xDiagram != null && xDiagram instanceof IXLogicDiagram) {
				Set<IXLogicDiagramMultiCore> xMCs = ((IXLogicDiagram) xDiagram).getDiagramMulticores();
				for (IXLogicDiagramMultiCore xMC : xMCs) {
					String name = getObjectAttribute(xMC, "Name");
					if (!multiCoreNames.isEmpty()) {
						multiCoreNames += " % ";
					}
					multiCoreNames += name;
				}
			}
		}
		LogExit(entity, multiCoreNames);
		return multiCoreNames;
	}
}
