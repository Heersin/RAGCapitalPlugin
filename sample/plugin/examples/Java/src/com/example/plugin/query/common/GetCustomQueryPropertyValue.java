package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXObject;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class GetCustomQueryPropertyValue extends BaseCustomResultExpression
{

	public GetCustomQueryPropertyValue()
	{
		super("GetCustomQueryPropertyValue", "1.0", "Returns the value of property named CustomQuery.");
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String val = entity.getProperty("CustomQuery");
		LogExit(entity, val);
		return (val != null) ? val : "<Null>";
	}
}
