package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXHarnessDiagram;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class HarnessDiagramAttrBundleOnFullScaleWidth extends BaseCustomResultExpression
{

	public HarnessDiagramAttrBundleOnFullScaleWidth()
	{
		super("HarnessDiagramAttrBundleOnFullScaleWidth", "1.0",
				"Checks the Harness Diagram is with BundleOnFullScaleWidth?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Diagram};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		if (entity instanceof IXHarnessDiagram) {
			String str = entity.getAttribute("BundleWidthFullScale");
			LogExit(entity, str);
			return str;
		}
		LogExit(entity, "");
		return "";
	}
}
