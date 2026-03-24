package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: svsn Date: Aug 20, 2009 Time: 10:40:24 AM To change this template use File | Settings
 * | File Templates.
 */
public class WireCSAGreateOneQuery extends BaseCustomFilterExpression
{

	public WireCSAGreateOneQuery()
	{
		super("WireCSA > 1.0", "1.0", "Checks if the wire CSA is greater than 1.0");
	}

	public IXCustomExpression.Context[] getApplicableContexts()
	{
		return new IXCustomExpression.Context[]{IXCustomExpression.Context.Wire};
	}

	public boolean isSatisfiedBy(IXObject object)
	{
		IXObject xCond = object;
		if (object instanceof IXLogicDiagramConductor) {
			xCond = ((IXDiagramObject) object).getConnectivity();
		}
		if (xCond instanceof IXWire) {
			String val = xCond.getAttribute("WireCSA");
			if (val != null) {
				try {
					double v = Double.parseDouble(val);
					if (v > 1.0) {
						return true;
					}
				}
				catch (NumberFormatException e) {
					return false;
				}
			}
		}
		return false;
	}
}
