package com.example.plugin.library;

import com.mentor.chs.plugin.filter.IXFilter;
import com.mentor.chs.plugin.library.IXLibraryExportFilter;
import com.mentor.chs.api.IXLibraryHousingDefinition;
import com.mentor.chs.api.IXLibraryObject;

import java.util.Collection;
import java.util.HashSet;


public class HousingDefinitionChildExportFilter implements IXLibraryExportFilter<IXFilter>
{
	@Override public IXFilter[] getEvaluators()
	{
		return new IXFilter[]
				{
						new HousingFilter()
						{
							@Override public Collection<IXLibraryHousingDefinition> apply(
									Collection<IXLibraryHousingDefinition> candidateObjects)
							{
								Collection<IXLibraryHousingDefinition> filteredList =
										new HashSet<IXLibraryHousingDefinition>();
								for (IXLibraryHousingDefinition libObj : candidateObjects) {

									IXLibraryObject owner = libObj.getOwningPart();
									String partNumber = owner.getAttribute("partnumber");
									if (partNumber.equalsIgnoreCase("T1")) {
										String subpart = libObj.getSubComponent().getAttribute("partnumber");
										if (subpart.equalsIgnoreCase("T2")) {
											continue;
										}
									}
									filteredList.add(libObj);
								}
								return filteredList;
							}
						},
				};
	}

	public String getDescription()
	{
		return "Housing Definition Child Export Filter";
	}

	public String getName()
	{
		return "HousingDefinitionChildExportFilter";
	}

	public String getVersion()
	{
		return "1.0";
	}

}
