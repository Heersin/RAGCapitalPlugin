package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXHarnessDiagramBundle;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class HarnessDiagramBundleAttrBundleLengthOverridden extends BaseCustomResultExpression
{

	public HarnessDiagramBundleAttrBundleLengthOverridden()
	{
		super("HarnessDiagramBundleAttrBundleLengthOverridden", "1.0",
				"Checks if the Harness Diagram Bunlde is with BundleLengthOveridden?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Bundle};
	}

	public Object evaluate(IXObject obj)
	{
		LogEntry(obj);
		if (obj instanceof IXHarnessDiagramBundle) {
			String str = obj.getAttribute("BundleLengthOverridden");
			LogExit(obj, str);
			return str;
		}
		LogExit(obj, "");
		return "";
	}
}
