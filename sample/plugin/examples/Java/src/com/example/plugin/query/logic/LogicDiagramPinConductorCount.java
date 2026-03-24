package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.IXLogicDiagramObject;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXObject;

import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class LogicDiagramPinConductorCount extends BaseCustomResultExpression
{

	public LogicDiagramPinConductorCount()
	{
		super("LogicDiagramPinConductorCount", "1.0", "Get the number of conductors attached to the pin and its relative location");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[] {Context.LogicDiagramPin};
	}

	public Object evaluate(IXObject obj)
	{
		LogEntry(obj);
		String result = "";
		if (obj instanceof IXLogicDiagramPin) {
			DecimalFormat df = new DecimalFormat("#.###");
			IXLogicDiagramPin xDiagramPin = (IXLogicDiagramPin) obj;
			int numCond = xDiagramPin.getConductors().size();
			Rectangle2D relativeExtent = xDiagramPin.getRelativeExtent();
			String locString = "[x=" + relativeExtent.getX() + ", y=" + relativeExtent.getY() + ", w=" +
					relativeExtent.getWidth() + ", h=" + relativeExtent.getHeight() + ']';
			double unitDistance = xDiagramPin.getDiagram().getUnitDistance();
			String locStringInMeters = "[x=" + df.format(relativeExtent.getX() * unitDistance) + ", y=" +
					df.format(relativeExtent.getY() * unitDistance) + ", w=" +
					df.format(relativeExtent.getWidth() * unitDistance) + ", h=" + df.format(relativeExtent.getHeight() * unitDistance) +
					']';
			result = "Num Conductors:" + numCond + "; Relative Location:" + locString + "; RL in meters:" +
					locStringInMeters;
		}
		LogExit(obj, result);
		return result;
	}
}
