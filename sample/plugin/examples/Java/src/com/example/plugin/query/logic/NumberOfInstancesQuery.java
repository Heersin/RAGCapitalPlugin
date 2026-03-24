package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXConnectivityObject;

import java.util.Set;

/**
 * Return true if the number of instances of the given diagram object are moer than two
 */
public class NumberOfInstancesQuery extends BaseCustomFilterExpression
{

	public NumberOfInstancesQuery()
	{
		super("Number of Diagram Object Instances Query", "1.0",
				"Checks if the number of instances of the given diagram object are greater than 2");
	}

	public boolean isSatisfiedBy(IXObject obj)
	{
		if (obj instanceof IXDiagramObject) {
			IXDiagramObject diagramObject = (IXDiagramObject) obj;
			IXDiagram logicDiagram = diagramObject.getDiagram();
			assert logicDiagram != null;
			Set<IXDiagramObject> diagramObjs =
					logicDiagram.getDiagramObjects((IXConnectivityObject) diagramObject.getConnectivity());
			return diagramObjs.size() > 2;
		}

		return false;
	}
}
