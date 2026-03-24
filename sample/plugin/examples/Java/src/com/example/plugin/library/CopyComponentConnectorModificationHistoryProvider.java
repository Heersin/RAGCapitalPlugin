package com.example.plugin.library;



import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibraryConnector;
import com.mentor.chs.plugin.library.IXLibraryModificationHistoryProvider;

public class CopyComponentConnectorModificationHistoryProvider implements IXLibraryModificationHistoryProvider<IXLibraryConnector>
{

	@Override public String getModificationNote(IXLibraryConnector libObj)
	{
		IXLibraryObject cofObj = libObj.getStoredLibraryObject();
		String cavityCount = null;
		if(cofObj != null){
			cavityCount = cofObj.getAttribute("NumCavities");
		}
		String transcavityCount = libObj.getAttribute("NumCavities");
		String partNumber = libObj.getAttribute("partnumber");
		if(cavityCount != null && !cavityCount.equalsIgnoreCase(transcavityCount)){
			return "Copy Component : Part " + partNumber + " cavity count changed from " + cavityCount + " to " + transcavityCount;
		}
		return "Copy Component : Part " + partNumber + " copied";
	}
}