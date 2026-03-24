package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXAbstractPinList;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.lang.StringBuilder;

public class GetPinsAttachedToConductor extends BaseCustomResultExpression
{

	public GetPinsAttachedToConductor()
	{
		super("GetPinsAttachedToConductor", "1.0", "Get the pins attached to the given conductor");
	}

	public Context[] getApplicableContexts()
	{
		return new Context[]{Context.Conductor};
	}

	public Object evaluate(IXObject entity)
	{
		if( entity instanceof IXAbstractConductor) {
			IXAbstractConductor conductor = (IXAbstractConductor)entity;
			Set<IXAbstractPin> connectedPins = conductor.getAbstractPins();
			List<String> connectedPinLists = new ArrayList<String>();
			if( connectedPins != null){
				for(IXAbstractPin  pin : connectedPins){
					IXAbstractPinList pinList = pin.getOwner();
					if( pinList != null){
						StringBuilder text = new StringBuilder();
						text.append(pinList.getAttribute("name"));
						text.append("::");
						text.append(pin.getAttribute("name"));
						connectedPinLists .add(text.toString());
					}
				}
			}
			return connectedPinLists;
		}
		return null;
	}

}