package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXObject;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class IsObjectHavingPropertyCustomAngle extends BaseCustomFilterExpression
{

	public IsObjectHavingPropertyCustomAngle()
	{
		super("IsObjectHavingPropertyCustomAngle", "1.0", "Checks if the Object has a property named CustomAngle.");
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		LogEntry(obj);
		boolean res = (obj.getProperty("CustomAngle") != null);
		LogExit(obj, ((Boolean) res).toString());
		return res;
	}
}
