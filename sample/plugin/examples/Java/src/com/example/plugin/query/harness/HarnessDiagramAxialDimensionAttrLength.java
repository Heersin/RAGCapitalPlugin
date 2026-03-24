package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXHarnessDiagramAxialDimension;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class HarnessDiagramAxialDimensionAttrLength extends BaseCustomResultExpression
{

	public HarnessDiagramAxialDimensionAttrLength()
	{
		super("HarnessDiagramAxialDimensionAttrLength", "1.0", "Checks the Harness Diagram AxialDimension Length?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.AxialDimension};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		if (entity instanceof IXHarnessDiagramAxialDimension) {
			String str = entity.getAttribute("Length");
			LogExit(entity, str);
			return str;
		}
		LogExit(entity, "");
		return "";
	}
}
