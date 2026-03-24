package com.example.plugin.library;


import com.mentor.chs.api.IXLibrarySymbol;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.Collection;
import java.util.HashSet;

public class SymbolFilter implements IXObjectFilter<IXLibrarySymbol>
{

	@Override public Collection<IXLibrarySymbol> apply(Collection<IXLibrarySymbol> candidateObjects)
	{
		Collection<IXLibrarySymbol> filteredList = new HashSet<IXLibrarySymbol>();
		for (IXLibrarySymbol libObj : candidateObjects) {
			IXLibraryObject owner = libObj.getOwningPart();
			String partNumber = owner.getAttribute("partnumber");
			if ("TestDev".equalsIgnoreCase(partNumber)) {
				String defSymbol = libObj.getAttribute("DefaultSymbol");
				if (!("1".equalsIgnoreCase(defSymbol))) {
					filteredList.add(libObj);
				}
			}
			else{
				filteredList.add(libObj);
			}
		}                         
		return filteredList;
	}
}
