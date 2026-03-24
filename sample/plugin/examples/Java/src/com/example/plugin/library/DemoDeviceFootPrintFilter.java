package com.example.plugin.library;

import com.mentor.chs.api.IXLibraryDeviceFootprint;
import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.ArrayList;
import java.util.Collection;

public class DemoDeviceFootPrintFilter implements IXObjectFilter<IXLibraryDeviceFootprint>
{

	@Override public Collection<IXLibraryDeviceFootprint> apply(Collection<IXLibraryDeviceFootprint> candidateObjects)
	{
		Collection<IXLibraryDeviceFootprint> returnList = new ArrayList<IXLibraryDeviceFootprint>();
		for (IXLibraryDeviceFootprint fp : candidateObjects) {
			String fpName = fp.getAttribute("FootprintName");
			if (!"DummyFP".equalsIgnoreCase(fpName)) {
				returnList.add(fp);
			}
		}
		return returnList;
	}
}