package com.example.plugin.library;



import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibraryConnector;
import com.mentor.chs.api.IXLibraryNoteSetter;
import com.mentor.chs.plugin.library.IXLibraryModificationHistoryProvider;

public class ComponentMaintenanceConnectorModificationHistoryProvider implements IXLibraryModificationHistoryProvider<IXLibraryConnector>
{

	@Override public String getModificationNote(IXLibraryConnector libObj)
	{
		IXLibraryObject cofObj = libObj.getStoredLibraryObject();
		String cavityCount = null;
		if(cofObj != null){
			cavityCount = cofObj.getAttribute("NumCavities");
		}
		IXLibraryNoteSetter noteSetter = libObj.getLibraryNoteSetter();
		String currentNote = null;
		if (noteSetter != null && noteSetter.getCurrentNote() != null) {
			currentNote = noteSetter.getCurrentNote();
		}
		String transcavityCount = libObj.getAttribute("NumCavities");
		String partNumber = libObj.getAttribute("partnumber");
		if(cavityCount != null && !cavityCount.equalsIgnoreCase(transcavityCount)){
			String message = "Component Maintenance : Part " + partNumber + " cavity count changed from " + cavityCount + " to " + transcavityCount;
			if(currentNote  != null){
				return currentNote + " " + message;
			}
			else{
				return message;
			}

		}
		if (currentNote != null) {
			return currentNote + " " + "Component Maintenance : Part " + partNumber + " edited";
		}
		else {
			return "Component Maintenance : Part " + partNumber + " edited";
		}

	}
}