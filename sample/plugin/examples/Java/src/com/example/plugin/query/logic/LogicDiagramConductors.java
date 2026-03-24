package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramObject;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramConductors extends BaseCustomResultExpression
{

	public LogicDiagramConductors()
	{
		super("LogicDiagramConductors", "1.0", "Get List of Conductors available on the diagram?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.LogicDiagramPinList};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String condNames = "";
		if (entity instanceof IXLogicDiagramPinList) {
			IXDiagram xDiagram = ((IXDiagramObject) entity).getDiagram();
			if (xDiagram != null && xDiagram instanceof IXLogicDiagram) {
				Set<IXLogicDiagramConductor> xConds = ((IXLogicDiagram) xDiagram).getDiagramConductors();
				for (IXLogicDiagramConductor xCond : xConds) {
					String name = getObjectAttribute(xCond, "Name");
					if (!condNames.isEmpty()) {
						condNames += " % ";
					}
					condNames += name;
				}
			}
		}
		LogExit(entity, condNames);
		return condNames;
	}
}
