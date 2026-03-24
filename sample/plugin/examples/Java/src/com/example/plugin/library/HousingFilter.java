package com.example.plugin.library;

import com.mentor.chs.api.IXLibraryHousingDefinition;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.Collection;
import java.util.HashSet;

public class HousingFilter implements IXObjectFilter<IXLibraryHousingDefinition>
{

	@Override public Collection<IXLibraryHousingDefinition> apply(
			Collection<IXLibraryHousingDefinition> candidateObjects)
	{
		Collection<IXLibraryHousingDefinition> filteredList = new HashSet<IXLibraryHousingDefinition>();
		for (IXLibraryHousingDefinition libObj : candidateObjects) {

			IXLibraryObject owner = libObj.getOwningPart();
			String partNumber = owner.getAttribute("partnumber");
			if (libObj.getAssociatedCavityGroup() != null
					&& "PN_Connector".equalsIgnoreCase(partNumber)) {
				continue;
			}
			String cavityName = libObj.getAttribute("Cavity");
			if (libObj.getAssociatedCavityGroup() != null
					&&
					"TestConnector".equalsIgnoreCase(partNumber) &&
					"1".equalsIgnoreCase(cavityName)) {
				continue;
			}
			filteredList.add(libObj);
		}
		return filteredList;
	}
}
