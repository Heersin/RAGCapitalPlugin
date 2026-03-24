package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramPinListAttchedPinLists extends BaseCustomResultExpression
{

	public LogicDiagramPinListAttchedPinLists()
	{
		super("LogicDiagramPinListAttchedPinLists", "1.0", "Get List of PinLists attached to a PinList?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.LogicDiagramPinList};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String attPinListNames = "";
		if (entity instanceof IXLogicDiagramPinList) {
			Set<IXLogicDiagramPinList> xAttPinLists = ((IXLogicDiagramPinList) entity).getAttachedPinListObjects();
			for (IXLogicDiagramPinList xAttPinList : xAttPinLists) {
				String name = getObjectAttribute(xAttPinList, "Name");
				if (!attPinListNames.isEmpty()) {
					attPinListNames += " % ";
				}
				attPinListNames += name;
			}
		}
		LogExit(entity, attPinListNames);
		return attPinListNames;
	}
}
