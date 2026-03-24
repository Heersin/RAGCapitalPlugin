package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXHarnessDiagram;
import com.mentor.chs.api.IXHarnessDiagramNodeDimension;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class HarnessDiagramNodeDimensions extends BaseCustomResultExpression
{

	public HarnessDiagramNodeDimensions()
	{
		super("HarnessDiagramNodeDimensions", "1.0", "Get list of the Node Dimensions on a Harness Diagram?");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{IXCustomExpression.Context.Diagram};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String nodeDimensionNames = "";
		if (entity instanceof IXHarnessDiagram) {
			Set<IXHarnessDiagramNodeDimension> xNodeDimensions = ((IXHarnessDiagram) entity).getDiagramDimensions();
			for (IXHarnessDiagramNodeDimension xNodeDimension : xNodeDimensions) {
				String name = getObjectAttribute(xNodeDimension, "Name");
				if (!nodeDimensionNames.isEmpty()) {
					nodeDimensionNames += " % ";
				}
				nodeDimensionNames += name;
			}
		}
		LogExit(entity, nodeDimensionNames);
		return nodeDimensionNames;
	}
}
