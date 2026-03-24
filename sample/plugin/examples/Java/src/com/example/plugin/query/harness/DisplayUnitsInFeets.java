package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXHarnessDiagram;
import com.mentor.chs.api.IXHarnessDiagramAxialDimension;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: svsn Date: Aug 20, 2009 Time: 11:56:05 AM To change this template use File | Settings
 * | File Templates.
 */
public class DisplayUnitsInFeets extends BaseCustomResultExpression
{

	public DisplayUnitsInFeets()
	{
		super("Convert into feets", "1.0", "Converts the given value into feets");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[] {IXCustomExpression.Context.AxialDimension};
	}
	public Object evaluate(IXObject object)
	{
		if (object instanceof IXHarnessDiagramAxialDimension) {
			IXHarnessDiagram xHarnessDiagram =
					(IXHarnessDiagram) ((IXDiagramObject) object).getDiagram();
			assert xHarnessDiagram != null;
			double conV = xHarnessDiagram.getUnitDistance();

			String length = object.getAttribute("Length");

			try {
				double d = Double.parseDouble(length);
				// Truncate it to 2 decimals
				double inFeets = (d * conV) * 3.28084;
				double trucated = Math.floor(inFeets) + (Math.floor(((inFeets - Math.floor(inFeets)) * 100)) / 100);
				return trucated + "'";
			}
			catch (NumberFormatException e) {
				return "0.0";
			}
		}

		return "0.0";
	}
}
