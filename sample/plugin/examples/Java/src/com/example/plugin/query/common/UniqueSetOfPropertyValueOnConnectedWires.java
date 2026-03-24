package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXAbstractPinList;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: svsn Date: Aug 20, 2009 Time: 10:40:24 AM To change this template use File | Settings
 * | File Templates.
 */
public class UniqueSetOfPropertyValueOnConnectedWires extends BaseCustomResultExpression
{

	public UniqueSetOfPropertyValueOnConnectedWires()
	{
		super("UniqueSetOfPropertyValueOnConnectedWires", "1.0",
				"Get the list of unique value of property 'System Name' on the wires attached to this pinList");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.LogicDiagramPinList, Context.Connector};
	}

	public Object evaluate(IXObject object)
	{
		IXObject connObj = object;
		if (object instanceof IXDiagramObject) {
			connObj = ((IXDiagramObject) object).getConnectivity();
		}

		String retVal = "";
		if (connObj instanceof IXAbstractPinList) {
			Set<String> valueSet = new HashSet<String>();
			Set<IXAbstractPin> pins = ((IXAbstractPinList) connObj).getPins();
			for (IXAbstractPin pin : pins) {
				Set<IXAbstractConductor> xconds = pin.getConductors();
				for (IXAbstractConductor xSchemCond : xconds) {
					String val = xSchemCond.getProperty("System");
					if (val != null) {
						valueSet.add(val);
					}
				}
			}

			List<String> valueList = new ArrayList<String>();
			valueList.addAll(valueSet);
			Collections.sort(valueList);
			for (String str : valueList) {
				if (!retVal.isEmpty()) {
					retVal += "\n";
				}
				retVal += str;
			}
		}
		return retVal;
	}
}
