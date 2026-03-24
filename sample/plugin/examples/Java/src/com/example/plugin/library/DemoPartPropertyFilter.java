package com.example.plugin.library;

import com.mentor.chs.api.IXValue;
import com.mentor.chs.api.IXLibraryConnector;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.plugin.filter.IXPropertyFilter;

import java.util.Collection;
import java.util.ArrayList;

public class DemoPartPropertyFilter implements IXPropertyFilter<IXLibraryConnector>
{

	@Override public Collection<IXValue> apply(IXLibraryConnector libObj, Collection<IXValue> properties)
	{
		Collection<IXValue> returnValue = new ArrayList<IXValue>();
		for (IXValue property : properties) {
			String propname = property.getName();
			if (propname.startsWith("Mfg")) {
				IXLibraryObject coflibObj = libObj.getStoredLibraryObject();
				if (coflibObj == null) {
					returnValue.add(property);
					continue;
				}
				String propValue = coflibObj.getProperty(propname);
				if (propValue != null && !propValue.isEmpty()) {
					continue;
				}
				else{
					returnValue.add(property);
				}
			}
			else {
				returnValue.add(property);
			}
		}
		return returnValue;
	}
}