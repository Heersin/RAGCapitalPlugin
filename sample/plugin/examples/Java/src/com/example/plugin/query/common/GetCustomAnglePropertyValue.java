package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXObject;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class GetCustomAnglePropertyValue extends BaseCustomResultExpression
{

	public GetCustomAnglePropertyValue()
	{
		super("GetCustomAnglePropertyValue", "1.0", "Returns the value of property named CustomAngle.");
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String val = entity.getProperty("CustomAngle");
		LogExit(entity, val);
		return (val != null) ? val : "<Null>";
	}
}
