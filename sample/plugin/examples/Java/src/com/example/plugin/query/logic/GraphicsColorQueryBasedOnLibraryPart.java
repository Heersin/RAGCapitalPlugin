package com.example.plugin.query.logic;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLibrariedObject;
import com.mentor.chs.api.IXLibraryColorCode;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXBackshellTermination;
import com.mentor.chs.api.IXAbstractPin;

/**
 * Return the Color for the given object based on the home condition
 */
public class GraphicsColorQueryBasedOnLibraryPart extends BaseCustomResultExpression
{

	public GraphicsColorQueryBasedOnLibraryPart()
	{
		super("Graphics Color based on Library Part", "1.0",
				"Return the Library Color Code if the object has a library Part attached");
	}

	public Object evaluate(IXObject entity)
	{
		IXObject cObject = entity;
		if (entity instanceof IXDiagramObject) {
			cObject = ((IXDiagramObject) entity).getConnectivity();
		}
		if (cObject != null && cObject instanceof IXBackshellTermination) {
			cObject = ((IXAbstractPin) cObject).getOwner();
		}
		if (cObject instanceof IXLibrariedObject) {
			IXLibrariedObject librariedObject = (IXLibrariedObject) cObject;
			IXLibraryObject libObj = librariedObject.getLibraryObject();
			if (libObj != null) {
				IXLibraryColorCode colorCode = libObj.getColorCode();

				return colorCode.getAttribute("colorcode");
			}
		}

		return "B";
	}
}