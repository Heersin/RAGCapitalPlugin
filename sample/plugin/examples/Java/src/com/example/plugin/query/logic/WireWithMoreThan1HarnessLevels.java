package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 *
 */
public class WireWithMoreThan1HarnessLevels extends BaseCustomFilterExpression
{

	public WireWithMoreThan1HarnessLevels()
	{
		super("WireHarness Levels = 1", "1.0", "Check if a wire has more than 1 harness levels");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Wire};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		if (obj instanceof IXWire) {
			IXWire wire = (IXWire) obj;
			if (wire.getHarnessLevels().size() == 1) {
				return true;
			}
		}
		return false;
	}
}
