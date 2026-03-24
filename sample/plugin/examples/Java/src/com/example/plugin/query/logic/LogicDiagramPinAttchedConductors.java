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
public class LogicDiagramPinAttchedConductors extends BaseCustomResultExpression
{

	public LogicDiagramPinAttchedConductors()
	{
		super("LogicDiagramPinAttchedConductors", "1.0", "Get List of Conductors attched to a Pin?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.LogicDiagramPin};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String attCondNames = "";
		if (entity instanceof IXLogicDiagramPin) {
			Set<IXLogicDiagramConductor> xConds = ((IXLogicDiagramPin) entity).getConductors();
			for (IXLogicDiagramConductor xCond : xConds) {
				String name = getObjectAttribute(xCond, "Name");
				if (!attCondNames.isEmpty()) {
					attCondNames += " % ";
				}
				attCondNames += name;
			}
		}
		LogExit(entity, attCondNames);
		return attCondNames;
	}
}
