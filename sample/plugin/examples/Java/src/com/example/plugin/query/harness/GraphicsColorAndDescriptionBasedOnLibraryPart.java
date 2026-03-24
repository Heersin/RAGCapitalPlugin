package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXBackshellTermination;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLibrariedObject;
import com.mentor.chs.api.IXLibraryColorCode;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXAbstractPin;

/**
 * Return the Color for the given object based on the home condition
 */
public class GraphicsColorAndDescriptionBasedOnLibraryPart extends BaseCustomResultExpression
{

	public GraphicsColorAndDescriptionBasedOnLibraryPart()
	{
		super("Graphics Color Code and Description based on Library Part", "1.0",
				"Return the Library Color Code and Description <ColorCode::ColorCodeDescription> if the object has a library Part attached");
	}

	public Object evaluate(IXObject entity)
	{
		IXObject cObject = entity;
		if (entity != null && entity instanceof IXDiagramObject) {
			cObject = ((IXDiagramObject) entity).getConnectivity();
		}

		if(cObject != null && cObject instanceof IXBackshellTermination) {
			cObject = ((IXAbstractPin) cObject).getOwner();
		}

		if (cObject != null && cObject instanceof IXLibrariedObject) {
			IXLibrariedObject librariedObject = (IXLibrariedObject) cObject;
			IXLibraryObject libObj = librariedObject.getLibraryObject();
			if (libObj != null) {
				IXLibraryColorCode colorCode = libObj.getColorCode();

            String str = colorCode.getAttribute("colorcode");
            str = str + "::" + colorCode.getAttribute("Description");
				return str;
			}
		}

		return "";
	}
}
