/*
 * Copyright 2004-2008 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */
package com.example.plugin.query.common;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXBackshellTermination;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLibrariedObject;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXAbstractPin;

/**
 * Implement this if customized query to be applied for stylesets.
 */
public class GetLibraryPartDescription extends BaseCustomResultExpression
{

	public GetLibraryPartDescription()
	{
		super("GetLibraryPartDescription", "1.0", "Returns the Library Part Description of Libraried Objects.");
	}

	public Object evaluate(IXObject entity)
	{
		LogEntry(entity);
		IXObject obj = entity;
		if (entity != null && entity instanceof IXDiagramObject) {
			obj = ((IXDiagramObject) entity).getConnectivity();
		}
		if (obj != null && obj instanceof IXBackshellTermination) {
			obj = ((IXAbstractPin) obj).getOwner();
		}
		String retVal = "";
		if (obj != null && obj instanceof IXLibrariedObject) {
			IXLibrariedObject librariedObj = (IXLibrariedObject) obj;
			IXLibraryObject libObj = librariedObj.getLibraryObject();
			if (libObj != null) {
				String desc = libObj.getAttribute("Description");
				retVal = (desc != null && !desc.isEmpty()) ? desc : "";
			}
		}
		LogExit(obj, retVal);
		return retVal;
	}
}

