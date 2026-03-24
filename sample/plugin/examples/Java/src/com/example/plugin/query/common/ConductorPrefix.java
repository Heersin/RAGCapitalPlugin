package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXMulticore;
import com.mentor.chs.api.IXNet;
import com.mentor.chs.api.IXShield;

import java.util.Set;

public class ConductorPrefix extends BaseCustomResultExpression
{

	public ConductorPrefix()
	{
		super("ConductorPrefix", "1.0", "Get the prefix to be used for any conductor");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.Multicore,Context.Wire, Context.NetConductor, Context.ShieldConductor};
	}

	public Object evaluate(IXObject entity)
	{
		if (entity instanceof IXWire) {
			return "Wire";
		}
		else if( entity instanceof IXMulticore) {
			return "MC";
		}
		else if( entity instanceof IXNet) {
			return "Net";
		}
		else if( entity instanceof IXShield) {
			return "Shield";
		}

		return null;
	}
}