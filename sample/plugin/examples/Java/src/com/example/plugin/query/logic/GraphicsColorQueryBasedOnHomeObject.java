package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXObject;

/**
 * Return the Color for the given object based on the home condition
 */
public class GraphicsColorQueryBasedOnHomeObject extends BaseCustomResultExpression
{

	public GraphicsColorQueryBasedOnHomeObject()
	{
		super("Graphics Color based on Home Condition", "1.0", "Return different color based on Home Object condition");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.LogicDiagramPin, Context.LogicDiagramPinList, Context.LogicDiagramConductor};
	}

	public Object evaluate(IXObject entity)
	{
		if (entity instanceof IXDiagramObject) {
			String val = entity.getAttribute("Home");
			if (val != null && "true".equalsIgnoreCase(val)) {
				return "LG";
			}
		}
		return "B";
	}
}
