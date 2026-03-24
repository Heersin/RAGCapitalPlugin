package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramMultiCore;
import com.mentor.chs.api.IXLogicDiagramObject;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;

import java.util.Set;

/**
 * Return the Color for the given object based on the home condition
 */
public class LogicDiagramObjectQuery extends BaseCustomResultExpression
{

	public LogicDiagramObjectQuery()
	{
		super("Diagram Object Instances", "1.0", "Return Number of Instances of pinlists/conductors");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[0];
	}

	public Object evaluate(IXObject entity)
	{
		IXLogicDiagram xLogicDiagram = null;

		if (entity instanceof IXLogicDiagram) {
			xLogicDiagram = (IXLogicDiagram) entity;
		}
		else if (entity instanceof IXLogicDiagramObject) {
			xLogicDiagram = (IXLogicDiagram) ((IXDiagramObject) entity).getDiagram();
		}

		if (xLogicDiagram != null) {
			Set<IXLogicDiagramPinList> pinLists = xLogicDiagram.getDiagramPinLists();
			String s = "";
			if (!pinLists.isEmpty()) {
				s = "Number of Diagram PinLists = " + pinLists.size();
			}
			Set<IXLogicDiagramConductor> conductors = xLogicDiagram.getDiagramConductors();
			if (!conductors.isEmpty()) {
				s += '\n' + "Number of Diagram Conductors = " + conductors.size();
			}

			Set<IXLogicDiagramMultiCore> multicores = xLogicDiagram.getDiagramMulticores();
			if (!multicores.isEmpty()) {
				s += "\n Number of Diagram Multicores = " + multicores.size();
			}

			return s;
		}

		return "";
	}
}