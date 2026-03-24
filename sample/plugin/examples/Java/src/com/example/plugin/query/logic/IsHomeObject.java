package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXLogicDiagramHighway;
import com.mentor.chs.plugin.query.IXCustomExpression;


/**
 * Implement this if customized query to be applied for stylesets.
 */
public class IsHomeObject extends BaseCustomFilterExpression
{

	public IsHomeObject()
	{
		super("IsHomeObject", "1.0", "Checks the Home Condition of the Object.");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.LogicDiagramPin, Context.LogicDiagramPinList,
				Context.LogicDiagramConductor, Context.LogicDiagramHighway};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		LogEntry(obj);
		if (obj instanceof IXLogicDiagramPinList || obj instanceof IXLogicDiagramPin ||
				obj instanceof IXLogicDiagramConductor || obj instanceof IXLogicDiagramHighway) {
			String str = obj.getAttribute("Home");
			boolean res = Boolean.valueOf(str);
			LogExit(obj, ((Boolean) res).toString());
			return res;
		}
		LogExit(obj, "false");
		return false;
	}
}
