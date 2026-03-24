package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXConnectivityObject;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLogicDiagramMultiCore;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: svsn Date: Nov 2, 2009 Time: 4:55:58 PM To change this template use File | Settings |
 * File Templates.
 */
public class IsMulticoreDaisyChained extends BaseCustomFilterExpression
{

	public IsMulticoreDaisyChained()
	{
		super("IsMulticoreDaisyChained", "1.0", "Checks if any of the multicore indicators is daisy chained in  the diagram");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.LogicDiagramMultiCore};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		if (obj instanceof IXLogicDiagramMultiCore) {
			String isDaisyChain = obj.getAttribute("DaisyChained");
			if ("true".equalsIgnoreCase(isDaisyChain)) {
				return true;
			}

			// Check the other indicators in this diagram
			IXDiagram xDiagram = ((IXDiagramObject) obj).getDiagram();
			Set<IXDiagramObject> diagrmObjs =
					xDiagram.getDiagramObjects((IXConnectivityObject) ((IXDiagramObject) obj).getConnectivity());
			if (diagrmObjs.size() < 1) {
				return false;
			}
			for (IXDiagramObject d : diagrmObjs) {
				if (d != obj && "true".equalsIgnoreCase(d.getAttribute("DaisyChained"))) {
					return true;
				}
			}
		}

		return false;
	}
}
