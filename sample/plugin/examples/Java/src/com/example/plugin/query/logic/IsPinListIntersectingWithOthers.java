package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;

import java.awt.geom.Rectangle2D;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class IsPinListIntersectingWithOthers extends BaseCustomFilterExpression
{

	public IsPinListIntersectingWithOthers()
	{
		super("IsPinListIntersectingWithOthers", "1.0", "Checks whether a pinlist extent intersects with other pinlist.");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[] {Context.LogicDiagramPinList};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		LogEntry(obj);
		boolean result = false;
		if (obj instanceof IXLogicDiagramPinList) {
			IXLogicDiagramPinList xDiagramPinList = (IXLogicDiagramPinList) obj;
			IXDiagram xDiagram = xDiagramPinList.getDiagram();
			if (xDiagram instanceof IXLogicDiagram) {
				IXLogicDiagram xLogicDiagram = (IXLogicDiagram) xDiagram;
				Set<IXLogicDiagramPinList> xDiagramPinLists = xLogicDiagram.getDiagramPinLists();
				Rectangle2D absoluteExtent1 = xDiagramPinList.getAbsoluteExtent();
				for (IXLogicDiagramPinList diagramPinList : xDiagramPinLists) {
					if (diagramPinList.getID().equalsIgnoreCase(xDiagramPinList.getID())) {
						continue;
					}
					Rectangle2D absoluteExtent2 = diagramPinList.getAbsoluteExtent();
					if (absoluteExtent1.intersects(absoluteExtent2)) {
						result = true;
						break;
					}
				}
			}
		}
		LogExit(obj, Boolean.valueOf(result).toString());
		return result;
	}

}
