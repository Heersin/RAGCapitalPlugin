package com.example.plugin.library;

import com.mentor.chs.api.IXLibraryDevicePin;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.plugin.filter.IXPropertyFilter;

import java.util.Collection;
import java.util.ArrayList;

public class DemoDevicePinPropertyFilter implements IXPropertyFilter<IXLibraryDevicePin>
{

	@Override public Collection<IXValue> apply(IXLibraryDevicePin candidateObject, Collection<IXValue> properties)
	{
		Collection<IXValue> returnValue = new ArrayList<IXValue>();
		for (IXValue property : properties) {
			String propertyName = property.getName();
			if ("MaxCurrent".equalsIgnoreCase(propertyName)) {
				continue;
			}
			if ("MinCurrent".equalsIgnoreCase(propertyName)) {
				continue;
			}
			returnValue.add(property);
		}
		return returnValue;
	}
}