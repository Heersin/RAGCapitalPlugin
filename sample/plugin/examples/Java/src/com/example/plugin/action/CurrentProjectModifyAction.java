/**
 * Copyright 2007 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example plugin.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, except as expressly
 * authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */
package com.example.plugin.action;

import com.mentor.chs.api.IXConnectivity;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXHarness;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXHarnessRegister;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.action.IXApplicationAction;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;

/**
 * This custom application action modify project/design/diagram/harness resgister
 * <p/>
 */
public class CurrentProjectModifyAction extends BaseAction implements IXApplicationAction
{

	public CurrentProjectModifyAction()
	{
		super(
				"Current Project Modify Action",
				"1.0",
				"This Action will modify Project/Design/Diagram attributes & properties");
	}

	public Icon getSmallIcon()
	{
		return null;
	}

	public boolean isAvailable(IXApplicationContext context)
	{
		return !context.getSelectedObjects(IXProject.class).isEmpty();
	}

	public boolean execute(IXApplicationContext applicationContext)
	{
		applicationContext.getOutputWindow().println("Modifying Project/Design/Diagram attributes & properties");
		IXProject prj = applicationContext.getCurrentProject();
		modifyObject(prj);
		for (IXDesign des : prj.getDesigns()) {
			modifyObject(des);
			IXConnectivity conn = des.getConnectivity();
			List<IXObject> connObjs = new ArrayList<IXObject>();
			connObjs.addAll(conn.getConnectors());
			connObjs.addAll(conn.getAssemblies());
			connObjs.addAll(conn.getDevices());
			connObjs.addAll(conn.getBlocks());
			connObjs.addAll(conn.getGrounds());
			connObjs.addAll(conn.getHighways());
			connObjs.addAll(conn.getInterconnectDevices());
			connObjs.addAll(conn.getInterconnects());
			connObjs.addAll(conn.getMulticores());
			connObjs.addAll(conn.getNets());
			connObjs.addAll(conn.getShields());
			connObjs.addAll(conn.getWires());
			if (des instanceof IXHarnessDesign) {
				IXHarnessRegister reg = ((IXHarnessDesign) des).getHarness().getHarnessRegister();
				if (reg != null) {
					modifyObject(reg);
				}
				IXHarness harn = ((IXHarnessDesign) des).getHarness();
				connObjs.addAll(harn.getBreakoutTapes());
				connObjs.addAll(harn.getBundles());
				connObjs.addAll(harn.getClips());
				connObjs.addAll(harn.getGrommets());
				connObjs.addAll(harn.getInsulationRuns());
				connObjs.addAll(harn.getMultiLocationComponents());
				connObjs.addAll(harn.getNodes());
				connObjs.addAll(harn.getOtherComponents());
				connObjs.addAll(harn.getSpotTapes());
			}
			for (IXDiagram diag : des.getDiagrams()) {
				modifyObject(diag);
			}
			for (IXObject connObj : connObjs) {
				if (connObj instanceof IXWriteableObject) {
					modifyObject((IXWriteableObject) connObj);
				}
			}
		}
		return true;
	}

	private void modifyObject(IXWriteableObject obj)
	{
		IXAttributeSetter setter = obj.getAttributeSetter();
		for (IXValue val : obj.getAttributes()) {
			modifyAttr(val, setter);
		}
		setter.addProperty("AddP1", "1.0");
		setter.addProperty("AddP2", "2");
		setter.addProperty("AddP3", "Added");
		for (IXValue val : obj.getProperties()) {
			modifyProp(val, setter);
		}
		for (IXValue val : obj.getProperties()) {
			if (val.getName().startsWith("Del")) {
				setter.removeProperty(val.getName());
			}
		}
	}

	private void modifyAttr(IXValue val, IXAttributeSetter setter)
	{
		String valStr = val.getValue();
		try {
			final int valueInt = Integer.parseInt(valStr) + 1;
			setter.addAttribute(val.getName(), Integer.toString(valueInt));
		}
		catch (NumberFormatException exc) {
			try {
				final double valueInt = Double.parseDouble(valStr) + 1.1;
				setter.addAttribute(val.getName(), Double.toString(valueInt));
			}
			catch (NumberFormatException exc1) {
				setter.addAttribute(val.getName(), valStr + "_app");
			}
		}
	}

	private void modifyProp(IXValue val, IXAttributeSetter setter)
	{
		String valStr = val.getValue();
		try {
			final int valueInt = Integer.parseInt(valStr) + 1;
			setter.addProperty(val.getName(), Integer.toString(valueInt));
		}
		catch (NumberFormatException exc) {
			try {
				final double valueInt = Double.parseDouble(valStr) + 1.1;
				setter.addProperty(val.getName(), Double.toString(valueInt));
			}
			catch (NumberFormatException exc1) {
				setter.addProperty(val.getName(), valStr + "_app");
			}
		}
	}

	public boolean isReadOnly()
	{
		return false;
	}
}
