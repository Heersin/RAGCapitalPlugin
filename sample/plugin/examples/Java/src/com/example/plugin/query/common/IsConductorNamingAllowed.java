package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.plugin.query.IXCustomExpression;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXMulticore;

import java.util.Set;

public class IsConductorNamingAllowed extends BaseCustomFilterExpression
{

	public IsConductorNamingAllowed()
	{
		super("IsConductorNamingAllowed", "1.0",
		 "Returns true for multicores and wires provided the wires are not a part of multicore");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.Multicore,Context.Wire};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		if (obj instanceof IXWire) {
			IXMulticore mc = ((IXWire)obj).getMulticore();
			return (mc == null);
		}
		else if( obj instanceof IXMulticore) {
			IXMulticore mc = (IXMulticore)obj;
			return (mc.getParentMulticore() == null);
		}

		return false;
	}
}
