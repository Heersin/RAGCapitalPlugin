package com.example.plugin.library;

import com.mentor.chs.api.IXLibraryDeviceFootprint;
import com.mentor.chs.api.IXLibraryDeviceFootprintPinMapping;
import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class FootPrintPinMapConnectorExportFilter implements IXObjectFilter<IXLibraryDeviceFootprint>
{

	@Override public Collection<IXLibraryDeviceFootprint> apply(Collection<IXLibraryDeviceFootprint> candidateObjects)
	{
		Collection<IXLibraryDeviceFootprint> returnList = new ArrayList<IXLibraryDeviceFootprint>();

		for (IXLibraryDeviceFootprint fp : candidateObjects) {
			boolean shouldAddFP = true;
			Set<IXLibraryDeviceFootprintPinMapping> pinMaps = fp.getFootprintPinMappings();
			for (IXLibraryDeviceFootprintPinMapping pinMap : pinMaps) {
				if ("J1".equalsIgnoreCase(pinMap.getAttribute("ConnectorName")) || "J2".equalsIgnoreCase(pinMap.getAttribute("ConnectorName"))) {
					shouldAddFP = false;
					break;
				}
			}
			if (shouldAddFP) {
				returnList.add(fp);
			}
		}
		return returnList;
	}
}