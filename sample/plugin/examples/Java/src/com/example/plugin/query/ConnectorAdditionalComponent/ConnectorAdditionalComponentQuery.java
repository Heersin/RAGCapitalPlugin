package com.example.plugin.query.ConnectorAdditionalComponent;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXAdditionalComponent;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 * Created with IntelliJ IDEA. User: lkatikal Date: 18/9/14 Time: 3:24 PM To change this template use File | Settings |
 * File Templates.
 */
public class ConnectorAdditionalComponentQuery  extends BaseCustomResultExpression
{
	public ConnectorAdditionalComponentQuery()
	{
		super("ConnectorWithAdditionalComponent", "1.0", "Connectors Additional Component Name");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.AdditionalComponent};
	}

	public Object evaluate(IXObject entity)
	{
		if (entity instanceof IXAdditionalComponent) {
			IXAdditionalComponent dObject = (IXAdditionalComponent) entity;
			return dObject.getAttribute("Name");
		}

		return "Not found";
	}

}
