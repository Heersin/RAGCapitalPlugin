package com.example.plugin.library;


import com.mentor.chs.api.IXLibraryHousingDefinition;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibraryBackshellSeal;

import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.Collection;
import java.util.HashSet;

public class DemoDomainHousingFilter implements IXObjectFilter<IXLibraryHousingDefinition>
{

	@Override public Collection<IXLibraryHousingDefinition> apply(
			Collection<IXLibraryHousingDefinition> candidateObjects)
	{
		Collection<IXLibraryHousingDefinition> filteredList = new HashSet<IXLibraryHousingDefinition>();
		for (IXLibraryHousingDefinition libObj : candidateObjects) {
			IXLibraryObject subComponent = libObj.getSubComponent();
			String domainName = subComponent.getAttribute("DomainName");
			if ("secured".equalsIgnoreCase(domainName)) {
				continue;
			}
			if (subComponent instanceof IXLibraryBackshellSeal) {
				continue;
			}
			filteredList.add(libObj);
		}
		return filteredList;
	}
}