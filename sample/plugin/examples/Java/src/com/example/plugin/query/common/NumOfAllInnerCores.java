package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXMulticore;
import com.mentor.chs.api.IXNet;
import com.mentor.chs.api.IXShield;
import com.mentor.chs.api.IXAbstractConductor;

import java.util.Set;
import java.util.HashSet;

public class NumOfAllInnerCores extends BaseCustomResultExpression
{

	public NumOfAllInnerCores()
	{
		super("NumOfAllInnerCores", "1.0", "Get the total number of inner cores inside a multicore including nested cores");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.Multicore};
	}

	public Object evaluate(IXObject entity)
	{
		if( entity instanceof IXMulticore) {
			IXMulticore mc = (IXMulticore)entity;
			Set<IXAbstractConductor> innerConductors = getAllInnerConductorsInHierarchy(mc);
			return innerConductors.size();
		}

		return 0;
	}

	private Set<IXAbstractConductor> getAllInnerConductorsInHierarchy(IXMulticore mc)
	{
		Set<IXAbstractConductor> conductors = new HashSet<IXAbstractConductor>();
		conductors.addAll(mc.getConductors());

		for (IXMulticore innermc : mc.getMulticores()) {
			conductors.addAll(getAllInnerConductorsInHierarchy(innermc));
		}

		IXShield shield = mc.getShield();
		if (shield != null) {
			conductors.add(shield);
		}
		return conductors;
	}
}