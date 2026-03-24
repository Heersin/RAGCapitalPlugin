package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
public class IsHomeObjectQuery extends BaseCustomFilterExpression
{

	public IsHomeObjectQuery()
	{
		super("Home Object Query", "1.0", "Checks if the given diagram object is a Home Object");
	}

	public IXCustomExpression.Context[] getApplicableContexts()
	{
		return new IXCustomExpression.Context[]{IXCustomExpression.Context.LogicDiagramPin,
				IXCustomExpression.Context.LogicDiagramPinList, IXCustomExpression.Context.LogicDiagramConductor};
	}

	public boolean isSatisfiedBy(IXObject object)
	{
		if (object instanceof IXLogicDiagramPinList || object instanceof IXLogicDiagramPin ||
				object instanceof IXLogicDiagramConductor) {
			String s = object.getAttribute("Home");
			return s != null && "true".equalsIgnoreCase(s);
		}

		return false;
	}
}