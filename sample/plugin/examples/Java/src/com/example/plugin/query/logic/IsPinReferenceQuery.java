package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
 */
public class IsPinReferenceQuery extends BaseCustomFilterExpression
{

	public IsPinReferenceQuery()
	{
		super("Pin is a Reference", "1.0", "Checks if the given Pin is a reference");
	}

	public IXCustomExpression.Context[] getApplicableContexts()
	{
		return new IXCustomExpression.Context[]{IXCustomExpression.Context.LogicDiagramPin};
	}

	public boolean isSatisfiedBy(IXObject object)
	{
		if (object instanceof IXLogicDiagramPin) {
			String s = object.getAttribute("Reference");
			return s != null && "true".equalsIgnoreCase(s);
		}

		return false;
	}
}
