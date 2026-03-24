package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomFilterExpression;
import com.mentor.chs.api.IXConnectivityObject;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXDevice;
import com.mentor.chs.plugin.query.IXCustomExpression;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: svsn Date: Aug 20, 2009 Time: 10:40:24 AM To change this template use File | Settings
 * | File Templates.
 */
public class MoreThan2AttachedWireCSAGreateOneQuery extends BaseCustomFilterExpression
{

	public MoreThan2AttachedWireCSAGreateOneQuery()
	{
		super("MoreThan2AttachedWireCSAGreateOneQuery", "1.0",
				"Checks if there are more than 2 wires attached to this pinList which have its wire CSA greater than 1.0");
	}

	public IXCustomExpression.Context[] getApplicableContexts()
	{
		return new IXCustomExpression.Context[] {IXCustomExpression.Context.LogicDiagramPinList};
	}
	private int NumWireCSAgreaterThanOne(IXLogicDiagramPinList object)
	{
		int count = 0;
		if (object != null) {
			Set<IXLogicDiagramPin> pins = object.getPins();
			for (IXLogicDiagramPin pin : pins) {
				Set<IXLogicDiagramConductor> xconds = pin.getConductors();
				for (IXLogicDiagramConductor xSchemCond : xconds) {
					IXObject xCond = xSchemCond.getConnectivity();
					if (xCond instanceof IXWire) {
						String val = xCond.getAttribute("WireCSA");
						if (val != null) {
							try {
								double v = Double.parseDouble(val);
								if (v > 1.0) {
									count++;
								}
							}
							catch (NumberFormatException e) {
								return count;
							}
						}
					}
				}
			}
		}
		return count;
	}

	public boolean isSatisfiedBy(IXObject object)
	{
		int wireCSAgreaterThanOneCount = 0;
		if (object instanceof IXLogicDiagramPinList) {
			wireCSAgreaterThanOneCount += NumWireCSAgreaterThanOne((IXLogicDiagramPinList) object);
			IXObject conObj = ((IXLogicDiagramPinList)object).getConnectivity();
			if (conObj != null && conObj instanceof IXDevice) {
				Set<IXLogicDiagramPinList> xAttPinLists = ((IXLogicDiagramPinList) object).getAttachedPinListObjects();
				for (IXLogicDiagramPinList xAttPinList : xAttPinLists) {
					wireCSAgreaterThanOneCount += NumWireCSAgreaterThanOne(xAttPinList);
				}
			}
		}
		return (wireCSAgreaterThanOneCount > 2);
	}
}
