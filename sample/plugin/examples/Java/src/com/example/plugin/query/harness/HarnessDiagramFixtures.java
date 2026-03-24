package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXHarnessDiagram;
import com.mentor.chs.api.IXHarnessDiagramFixture;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class HarnessDiagramFixtures extends BaseCustomResultExpression
{

	public HarnessDiagramFixtures()
	{
		super("HarnessDiagramFixtures", "1.0", "Get list of the fixtures on a Harness Diagram?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Diagram};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String fixtureNames = "";
		if (entity instanceof IXHarnessDiagram) {
			Set<IXHarnessDiagramFixture> xFixtures = ((IXHarnessDiagram) entity).getDiagramFixtures();
			for (IXHarnessDiagramFixture xFixture : xFixtures) {
				String name = getObjectAttribute(xFixture, "Name");
				if (!fixtureNames.isEmpty()) {
					fixtureNames += " % ";
				}
				fixtureNames += name;
			}
		}
		LogExit(entity, fixtureNames);
		return fixtureNames;
	}
}
