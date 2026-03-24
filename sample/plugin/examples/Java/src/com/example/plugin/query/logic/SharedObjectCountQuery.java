package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXBackshellTermination;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXMulticore;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXShield;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 *
 */
public class SharedObjectCountQuery extends BaseCustomFilterExpression
{

	public SharedObjectCountQuery()
	{
		super("MultiCore Active Checker", "1.0", "Multi core is active if its shield is connected to a backshell");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Multicore};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		IXObject cObject = obj;
		if (obj instanceof IXDiagramObject) {
			cObject = ((IXDiagramObject) obj).getConnectivity();
		}
		if (cObject instanceof IXMulticore) {
			IXMulticore multicore = (IXMulticore) cObject;
			IXShield shield = multicore.getShield();
			if (shield != null) {
				for (IXAbstractPin pin : shield.getAbstractPins()) {
					if (pin instanceof IXBackshellTermination) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
