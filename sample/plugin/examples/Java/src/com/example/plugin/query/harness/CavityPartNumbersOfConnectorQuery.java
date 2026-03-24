package com.example.plugin.query.harness;

import com.example.plugin.query.BaseCustomResultExpression;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXAdditionalComponent;
import com.mentor.chs.api.IXCavity;
import com.mentor.chs.api.IXCavityDetail;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXObject;

/**
 * Created by IntelliJ IDEA. User: chandras Date: Nov 16, 2009 Time: 5:11:06 PM To change this template use File |
 * Settings | File Templates.
 */

public class CavityPartNumbersOfConnectorQuery extends BaseCustomResultExpression
{

	public CavityPartNumbersOfConnectorQuery()
	{
		super("CavityPartNumbersOfConnectorQuery", "1.0", "Get list of PartNumbers of all the cavities of a connector");
	}

	private String getCavityValue(IXCavity cavity)
	{
		String partNumbers = "";
		if (!cavity.getConductors().isEmpty()) {
			for (IXCavityDetail xCavDetails : cavity.getCavityDetails()) {
				String xPartNumber = xCavDetails.getAttribute("SealPartNumber");
				if (!partNumbers.isEmpty()) {
					partNumbers += ", ";
				}
				partNumbers += xPartNumber;
			}
		}
		else {
			for (IXAdditionalComponent xAddComps : cavity.getAdditionalComponents()) {
				String grpName = "";
				IXLibraryObject xLibObj = xAddComps.getLibraryObject();
				if (xLibObj != null) {
					grpName = xLibObj.getAttribute("GroupName");
				}
				if ("Cavity Plug".equals(grpName) || "Backshell Plug".equals(grpName)) {
					String xPartNumber = xAddComps.getAttribute("PartNumber");
					if (!partNumbers.isEmpty()) {
						partNumbers += ", ";
					}
					partNumbers += xPartNumber;
				}
			}
		}
		return (cavity.getAttribute("Name") + ":< " + partNumbers + " >");
	}

	public Object evaluate(IXObject object)
	{
		LogEntry(object);
		IXObject conObj = object;
		if (object instanceof IXDiagramObject) {
			conObj = ((IXDiagramObject) object).getConnectivity();
		}
		String retObject = "";
		if (conObj instanceof IXConnector) {
			IXConnector xConn = (IXConnector) conObj;
			for (IXAbstractPin xCavity : xConn.getPins()) {
				if (xCavity instanceof IXCavity) {
					if (!retObject.isEmpty()) {
						retObject += "\n";
					}
					retObject += getCavityValue((IXCavity) xCavity);
				}
			}
		}

		String retVal = retObject.isEmpty() ? "" : retObject;
		LogExit(object, retVal);
		return retVal;
	}
}