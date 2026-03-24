package com.example.plugin.query.ConnectorAdditionalComponent;

import com.example.plugin.query.BaseCustomExpression;
import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXAdditionalComponent;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXNode;
import com.mentor.chs.api.IXObject;

/**
 * Created with IntelliJ IDEA. User: lkatikal Date: 18/9/14 Time: 4:43 PM To change this template use File | Settings |
 * File Templates.
 */
public class ConnectorAdditionalComponentIsSatisfiedBy extends BaseCustomFilterExpression
{

	public ConnectorAdditionalComponentIsSatisfiedBy()
	{
		super("ConnectorAdditionalComponentIsSatisfiedBy", "1.0", "Connector AdditionalComponent IsSatisfiedBy");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.AdditionalComponent};
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		if(obj instanceof IXAdditionalComponent)
		{
			String propertyOnAOwningConnector = "";
			IXObject owner = ((IXAdditionalComponent) obj).getOwner();
			if(owner instanceof IXNode)
			{
				IXConnector connector = ((IXNode)owner).getConnector();
				propertyOnAOwningConnector = connector.getProperty("P1");
			}

			String attributeOnAddlComponent = ((IXAdditionalComponent)obj).getAttribute("Name");

			if(propertyOnAOwningConnector != null && propertyOnAOwningConnector.contains("p1") && attributeOnAddlComponent != null && attributeOnAddlComponent.contains("name"))
			{
				return true;
			}
		}

		return false;
	}
}
