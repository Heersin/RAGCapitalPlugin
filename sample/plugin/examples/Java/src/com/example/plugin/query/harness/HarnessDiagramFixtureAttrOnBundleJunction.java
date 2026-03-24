package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXHarnessDiagramFixture;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class HarnessDiagramFixtureAttrOnBundleJunction extends BaseCustomResultExpression
{

	public HarnessDiagramFixtureAttrOnBundleJunction()
	{
		super("HarnessDiagramFixtureAttrOnBundleJunction", "1.0",
				"Checks if the Harness Diagram Fixture is on BundleJunction?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Fixture};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		if (entity instanceof IXHarnessDiagramFixture) {
			String str = entity.getAttribute("OnBundleJunction");
			LogExit(entity, str);
			return str;
		}
		LogExit(entity, "");
		return "";
	}
}
