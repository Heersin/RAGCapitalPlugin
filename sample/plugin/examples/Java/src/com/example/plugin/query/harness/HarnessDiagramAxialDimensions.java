package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXHarnessDiagram;
import com.mentor.chs.api.IXHarnessDiagramAxialDimension;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class HarnessDiagramAxialDimensions extends BaseCustomResultExpression
{

	public HarnessDiagramAxialDimensions()
	{
		super("HarnessDiagramAxialDimensions", "1.0", "Get list of the Axial Dimensions on a Harness Diagram?");
	}

public Context[] getApplicableContexts()
	{
		return new Context[] {IXCustomExpression.Context.Diagram};
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		String axialDimensionNames = "";
		if (entity instanceof IXHarnessDiagram) {
			Set<IXHarnessDiagramAxialDimension> xAxialDimensions = ((IXHarnessDiagram) entity).getDiagramAxialDimensions();
			for (IXHarnessDiagramAxialDimension xAxialDimension : xAxialDimensions) {
				String name = getObjectAttribute(xAxialDimension, "Name");
				if (!axialDimensionNames.isEmpty()) {
					axialDimensionNames += " % ";
				}
				axialDimensionNames += name;
			}
		}
		LogExit(entity, axialDimensionNames);
		return axialDimensionNames;
	}
}
