package com.example.plugin.library;



import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibraryDevice;
import com.mentor.chs.api.IXLibraryComponentTypeCode;
import com.mentor.chs.plugin.library.IXLibraryModificationHistoryProvider;

public class CopyComponentDeviceModificationHistoryProvider implements IXLibraryModificationHistoryProvider<IXLibraryDevice>
{

	@Override public String getModificationNote(IXLibraryDevice libObj)
	{
		IXLibraryObject cofObj = libObj.getStoredLibraryObject();
		IXLibraryComponentTypeCode typeCode = null;
		if(cofObj != null){
		   typeCode = cofObj.getComponentTypeCode();
		}
		IXLibraryComponentTypeCode transtypeCode = libObj.getComponentTypeCode();
		String partNumber = libObj.getAttribute("partnumber");
		String typeCodeName = null;
		if(typeCode != null){
			typeCodeName = typeCode.getAttribute("TypeCode");
		}
		String transtypeCodeName = transtypeCode.getAttribute("TypeCode");
		if(typeCodeName != null && !typeCodeName.equalsIgnoreCase(transtypeCodeName)){
			return "Copy Component : Part " + partNumber + " Type Code changed from " + typeCodeName + " to " + transtypeCodeName;
		}
		return "Copy Component : Part " + partNumber + " copied";
	}
}