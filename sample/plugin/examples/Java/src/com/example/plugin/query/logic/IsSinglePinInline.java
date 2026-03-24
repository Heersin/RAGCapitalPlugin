package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.HashSet;
import java.util.Set;

/**
 * This query checks if the given schematic object has a single pin instantiation
 */
public class IsSinglePinInline extends BaseCustomFilterExpression
{

	public IsSinglePinInline()
	{
		super("Single Pin Inline Query", "1.0", "Checks if the given Inline diagram object has a single pin");
	}

	public IXCustomExpression.Context[] getApplicableContexts()
	{
		return new IXCustomExpression.Context[]{IXCustomExpression.Context.Connector};
	}

	public boolean isSatisfiedBy(IXObject object)
	{
		if (object instanceof IXLogicDiagramPinList &&
				((IXDiagramObject) object).getConnectivity() instanceof IXConnector) {
			IXConnector con = (IXConnector) ((IXDiagramObject) object).getConnectivity();
			if (con != null && con.isInline()) {
				return ((IXLogicDiagramPinList) object).getPins().size() == 1;
			}
		}

		return false;
	}
}
