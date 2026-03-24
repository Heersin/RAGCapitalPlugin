package com.example.plugin.library;

import com.mentor.chs.api.IXLibraryDevicePin;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.plugin.filter.IXPropertyFilter;

import java.util.Collection;
import java.util.ArrayList;

public class DevicePinPropertyFilter implements IXPropertyFilter<IXLibraryDevicePin>
{

	@Override public Collection<IXValue> apply(IXLibraryDevicePin libObj, Collection<IXValue> properties)
	{
		Collection<IXValue> returnValue = new ArrayList<IXValue>();
		for (IXValue property : properties) {
			IXLibraryObject libraryObj = libObj.getOwningPart();
			String partNumber = libraryObj.getAttribute("partnumber");
			if ("PN_Device".equalsIgnoreCase(partNumber)) {
				String cavityName = libObj.getAttribute("Name");
				if ("3".equalsIgnoreCase(cavityName)) {
					continue;
				}
			}
			returnValue.add(property);
		}
		return returnValue;
	}
}
