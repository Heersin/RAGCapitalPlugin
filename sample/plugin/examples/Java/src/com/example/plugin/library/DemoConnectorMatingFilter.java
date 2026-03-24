package com.example.plugin.library;

import com.mentor.chs.api.IXLibraryBaseConnector;
import com.mentor.chs.api.IXLibraryConnectorMating;
import com.mentor.chs.plugin.filter.IXObjectFilter;

import java.util.ArrayList;
import java.util.Collection;

public class DemoConnectorMatingFilter implements IXObjectFilter<IXLibraryConnectorMating>
{

	@Override public Collection<IXLibraryConnectorMating> apply(Collection<IXLibraryConnectorMating> candidateObjects)
	{
		Collection<IXLibraryConnectorMating> returnList = new ArrayList<IXLibraryConnectorMating>();
		for (IXLibraryConnectorMating connMate : candidateObjects) {
			IXLibraryBaseConnector owningPart = connMate.getOwningPart();
			IXLibraryBaseConnector matedConn = connMate.getMatedConnector(owningPart);
			String owingpartNumber = owningPart.getAttribute("PartNumber");
			String matedPartNumber = matedConn.getAttribute("PartNumber");
			if ("DummyOwingConn".equalsIgnoreCase(owingpartNumber) &&
					"DummyMatedConn".equalsIgnoreCase(matedPartNumber)) {
				continue;
			}
			returnList.add(connMate);
		}
		return returnList;
	}
}