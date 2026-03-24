package com.example.plugin.library;


import com.mentor.chs.api.IXLibraryObject;


import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.Collection;
import java.util.HashSet;

public class DemoDomainPartFilter implements IXObjectFilter<IXLibraryObject>
{

	@Override public Collection<IXLibraryObject> apply(Collection<IXLibraryObject> candidateObjects)
	{
		Collection<IXLibraryObject> filteredList = new HashSet<IXLibraryObject>();
		for (IXLibraryObject libObj : candidateObjects) {
			String domainName = libObj.getAttribute("DomainName");
			if ("secured".equalsIgnoreCase(domainName)) {
				continue;
			}
			else {
				filteredList.add(libObj);
			}
		}
		return filteredList;
	}
}