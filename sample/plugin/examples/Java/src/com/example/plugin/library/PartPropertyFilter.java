package com.example.plugin.library;

import com.mentor.chs.api.IXValue;
import com.mentor.chs.api.IXLibraryIDCConnector;
import com.mentor.chs.plugin.filter.IXPropertyFilter;

import java.util.Collection;
import java.util.ArrayList;

public class PartPropertyFilter implements IXPropertyFilter<IXLibraryIDCConnector>
{

	@Override public Collection<IXValue> apply(IXLibraryIDCConnector libObj, Collection<IXValue> properties)
	{
		Collection<IXValue> returnValue = new ArrayList<IXValue>();
		for (IXValue property : properties) {
			String propname = property.getName();
			String propvalue = property.getValue();
			if ("A".equalsIgnoreCase(propname) && "ABC".equalsIgnoreCase(propvalue)) {
				continue;
			}
			returnValue.add(property);
		}
		return returnValue;
	}
}
