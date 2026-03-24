package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.IXLogicDiagramObject;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramPinLists extends BaseCustomResultExpression
{

	public LogicDiagramPinLists()
	{
		super("LogicDiagramPinLists", "1.0", "Get List of PinLists available on the diagram?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Diagram};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String pinlists = "";
		if (entity instanceof IXLogicDiagramPinList) {
			IXDiagram xDiagram = ((IXDiagramObject) entity).getDiagram();
			if (xDiagram != null && xDiagram instanceof IXLogicDiagram) {
				Set<IXLogicDiagramPinList> xPLs = ((IXLogicDiagram) xDiagram).getDiagramPinLists();
				for (IXLogicDiagramPinList xPL : xPLs) {
					String name = getObjectAttribute(xPL, "Name");
					if (!pinlists.isEmpty()) {
						pinlists += " % ";
					}
					pinlists += name;
				}
			}
		}
		LogExit(entity, pinlists);
		return pinlists;
	}
}
