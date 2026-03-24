package com.example.plugin.library;



import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibraryDevice;
import com.mentor.chs.api.IXLibraryComponentTypeCode;
import com.mentor.chs.api.IXLibraryNoteSetter;
import com.mentor.chs.plugin.library.IXLibraryModificationHistoryProvider;

public class ComponentMaintenanceDeviceModificationHistoryProvider implements IXLibraryModificationHistoryProvider<IXLibraryDevice>
{

	@Override public String getModificationNote(IXLibraryDevice libObj)
	{
		IXLibraryObject cofObj = libObj.getStoredLibraryObject();
		IXLibraryComponentTypeCode typeCode = null;
		if(cofObj != null){
		   typeCode = cofObj.getComponentTypeCode();
		}
		IXLibraryNoteSetter noteSetter = libObj.getLibraryNoteSetter();
		String currentNote = null;
		if (noteSetter != null && noteSetter.getCurrentNote() != null) {
			currentNote = noteSetter.getCurrentNote();
		}
		IXLibraryComponentTypeCode transtypeCode = libObj.getComponentTypeCode();
		String partNumber = libObj.getAttribute("partnumber");
		String typeCodeName = null;
		if(typeCode != null){
			typeCodeName = typeCode.getAttribute("TypeCode");
		}
		String transtypeCodeName = transtypeCode.getAttribute("TypeCode");
		if(typeCodeName != null && !typeCodeName.equalsIgnoreCase(transtypeCodeName)){
			String message = "Component Maintenance : Part " + partNumber + " Type Code changed from " + typeCodeName + " to " + transtypeCodeName;
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