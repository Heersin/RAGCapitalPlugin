package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXHarnessDiagramFixture;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class HarnessDiagramFixtureAttrOnElbow extends BaseCustomResultExpression
{

	public HarnessDiagramFixtureAttrOnElbow()
	{
		super("HarnessDiagramFixtureAttrOnElbow", "1.0", "Checks if the Harness Diagram Fixture is on Elbow?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Fixture};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		if (entity instanceof IXHarnessDiagramFixture) {
			String str = entity.getAttribute("OnElbow");
			LogExit(entity, str);
			return str;
		}
		LogExit(entity, "");
		return "";
	}
}
