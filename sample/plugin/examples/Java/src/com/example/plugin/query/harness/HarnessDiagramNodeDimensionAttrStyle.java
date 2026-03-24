package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXHarnessDiagramNodeDimension;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class HarnessDiagramNodeDimensionAttrStyle extends BaseCustomResultExpression
{

	public HarnessDiagramNodeDimensionAttrStyle()
	{
		super("HarnessDiagramNodeDimensionAttrStyle", "1.0", "Checks the Harness Diagram NodeDimension Style?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.NodeDimension};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		if (entity instanceof IXHarnessDiagramNodeDimension) {
			String str = entity.getAttribute("DimensionStyle");
			LogExit(entity, str);
			return str;
		}
		LogExit(entity, "");
		return "";
	}
}
