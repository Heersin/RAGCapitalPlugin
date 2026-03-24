package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXDevice;

import java.util.Set;
import java.util.LinkedHashSet;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class IsObjectHavingPropertyCustomQuery extends BaseCustomFilterExpression
{

	public IsObjectHavingPropertyCustomQuery()
	{
		super("IsObjectHavingPropertyCustomQuery", "1.0", "Checks if the Object has a property named CustomQuery.");
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		LogEntry(obj);
		boolean res = (obj.getProperty("CustomQuery") != null);
		LogExit(obj, ((Boolean) res).toString());
		return res;
	}
}
