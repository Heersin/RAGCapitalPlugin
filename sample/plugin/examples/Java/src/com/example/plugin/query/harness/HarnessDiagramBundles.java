package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXHarnessDiagram;
import com.mentor.chs.api.IXHarnessDiagramBundle;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class HarnessDiagramBundles extends BaseCustomResultExpression
{

	public HarnessDiagramBundles()
	{
		super("HarnessDiagramBundles", "1.0", "Get list of the bundles on a Harness Diagram?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Diagram};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String bunldeNames = "";
		if (entity instanceof IXHarnessDiagram) {
			Set<IXHarnessDiagramBundle> xFixtures = ((IXHarnessDiagram) entity).getDiagramBundles();
			for (IXHarnessDiagramBundle xbundle : xFixtures) {
				String name = getObjectAttribute(xbundle, "Name");
				if (!bunldeNames.isEmpty()) {
					bunldeNames += " % ";
				}
				bunldeNames += name;
			}
		}
		LogExit(entity, bunldeNames);
		return bunldeNames;
	}
}
