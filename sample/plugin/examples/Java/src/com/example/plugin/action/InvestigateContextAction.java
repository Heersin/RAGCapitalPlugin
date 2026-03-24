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

import com.example.plugin.BasePlugin;
import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXAbstractPinList;
import com.mentor.chs.api.IXAssembly;
import com.mentor.chs.api.IXBlock;
import com.mentor.chs.api.IXCavity;
import com.mentor.chs.api.IXConnectivity;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXDevice;
import com.mentor.chs.api.IXDevicePin;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXGround;
import com.mentor.chs.api.IXHighway;
import com.mentor.chs.api.IXInterconnect;
import com.mentor.chs.api.IXInterconnectDevice;
import com.mentor.chs.api.IXLibrariedObject;
import com.mentor.chs.api.IXLibrary;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXModuleCode;
import com.mentor.chs.api.IXModuledObject;
import com.mentor.chs.api.IXMulticore;
import com.mentor.chs.api.IXNet;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.IXShield;
import com.mentor.chs.api.IXSplice;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXOutputWindow;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXLogicAction;

import java.util.Set;

/**
 * Displays information that can be obtained from the business objects returned from methods on
 * <code>IXApplicationContext</code>.
 */
public class InvestigateContextAction extends BaseAction implements IXLogicAction,
		IXIntegratorAction, IXHarnessAction

{

	public InvestigateContextAction()
	{
		super("Investigate Context",
				"1.0",
				"Displays the information being passed in via the context.");
	}

	/**
	 * @param context - the IXApplicationContext
	 *
	 * @return true, so this menu entry will be available in all situations.
	 */
	public boolean isAvailable(IXApplicationContext context)
	{
		return true;
	}

	/**
	 * @return true - by default, all the example actions are read-only (they read information from the design but do not
	 *         modify any properties/attributes.
	 */
	public boolean isReadOnly()
	{
		return false;
	}

	/**
	 * This support method displays the name of the object as HTML. Note the use of the 'IXObject.toHTML()' method. This
	 * will insert an HREF link that when clicked on will select that object if it is present on the current diagram.
	 *
	 * @param xObject - the object to create the HTML for.
	 *
	 * @return the HTML for that object.
	 */
	protected String htmlObjectName(IXObject xObject)
	{
		final String rval = BasePlugin.getObjectName(xObject);
		if (rval != null) {
			return "<FONT color=\"blue\">" + xObject.toHTML() + "</FONT>";
		}
		return "<FONT color=\"red\">NO NAME ATTRIBUTE?</FONT>";
	}

	/**
	 * This method is used to check that you can write to an object. To change the attributes/properties of an object, you
	 * need an <code>IXAttributeSetter</code> object.
	 *
	 * @param context - the 'context' containing the current design in CHS (the design that has focus in the GUI).
	 * @param design - the current design being investigated.
	 * @param xObject - the object to check.
	 *
	 * @return the string indicating if we can change the attributes/properties of this object
	 *
	 * @see ConnectorCounterIncrementAction
	 * @see DeviceCounterIncrementAction
	 */
	protected String checkIfWriteable(
			IXApplicationContext context,
			IXDesign design,
			IXWriteableObject xObject)
	{
		return checkIfWriteable(context, design, "attributes/properties", (xObject.getAttributeSetter() == null));
	}

	/**
	 * This method is used to check that you can write to an object. To change the attributes/properties of an object, you
	 * need an <code>IXAttributeSetter</code> object.
	 *
	 * @param context - the 'context' containing the current design in CHS (the design that has focus in the GUI).
	 * @param design - the current design being investigated.
	 * @param aspect - the aspect of the object that could be changed.
	 * @param readOnly - is this apsect currently read-only?
	 *
	 * @return the string indicating if we can change the attributes/properties of this object
	 */
	protected String checkIfWriteable(
			IXApplicationContext context,
			IXDesign design,
			String aspect,
			boolean readOnly)
	{
		if (context.getCurrentDesign() == design) {
			if (readOnly) {
				return "<FONT color=\"red\"> but " + aspect +
						" of this object <b>can't</b> be changed. <b>ERROR: THIS IS WRONG!!!<b/></FONT>";
			}
			else {
				return "<FONT color=\"green\"> and " + aspect +
						" can be changed (object is in the current design).</FONT>";
			}
		}
		else {
			if (readOnly) {
				return " but " + aspect +
						" of this object <b>can't</b> be changed (object is <b>not</b> in the current design).";
			}
			else {
				return "<FONT color=\"red\"> and " + aspect +
						" can be changed on this object as it is in the current design. <b>ERROR: THIS IS WRONG!!!<b/></FONT>";
			}
		}
	}

	/**
	 * This method explores an object that could be a libraried object (an object that could have a library part attached).
	 * If a library part is attached, information about the library part will be displayed.
	 *
	 * @param context - the 'context' containing the output window used to display the results.
	 * @param design - the design being investigated.
	 * @param possibleLibrariedObject - the obj being investigated.
	 */
	protected void investigateLibraryObject(IXApplicationContext context, IXDesign design,
			IXObject possibleLibrariedObject)
	{
		final IXOutputWindow outputWindow = context.getOutputWindow();
		if (possibleLibrariedObject instanceof IXLibrariedObject) {
			final IXLibrariedObject librariedObject = (IXLibrariedObject) possibleLibrariedObject;

			// Just because an object is IXLibrariedObject, does not mean there is actually a library part attached.
			final IXLibraryObject libraryObject = librariedObject.getLibraryObject();
			if (libraryObject != null) {
				if (libraryObject.getColorCode() != null) {
					outputWindow.println(getName(),
							"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>IXLibraryObject</b> called: " +
									htmlObjectName(possibleLibrariedObject) + " color: " +
									libraryObject.getColorCode().toHTML());
				}
				if (libraryObject.getMaterialCode() != null) {
					outputWindow.println(getName(),
							"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>IXLibraryObject</b> called: " +
									htmlObjectName(possibleLibrariedObject) + " material: " +
									libraryObject.getMaterialCode().toHTML());
				}
				if (libraryObject.getComponentTypeCode() != null) {
					outputWindow.println(getName(),
							"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>IXLibraryObject</b> called: " +
									htmlObjectName(possibleLibrariedObject) + " type: " +
									libraryObject.getComponentTypeCode().toHTML());
				}
			}
		}
	}

	/**
	 * This method explores 'xObj' extracting module code information if it has it.
	 *
	 * @param context - the 'context' containing the output window used to display the results.
	 * @param design - the design being investigated.
	 * @param xObj - the pinList being investigated.
	 */
	protected void investigateModuledObject(IXApplicationContext context, IXDesign design, IXObject xObj)
	{
		if (xObj instanceof IXModuledObject) {
			final IXOutputWindow outputWindow = context.getOutputWindow();
			IXModuledObject moduledObject = (IXModuledObject) xObj;

			outputWindow.println(getName(),
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>this is also an IXModuledObject</b> called: " +
							htmlObjectName(moduledObject) + ' ' +
							checkIfModuleCodesWriteable(context, design, moduledObject));

			investigateModuleCodes(context, "UserFunctionalModuleCodes", moduledObject.getUserFunctionalModuleCodes());
			investigateModuleCodes(context, "GeneratedFunctionalModuleCodes",
					moduledObject.getGeneratedFunctionalModuleCodes());
			investigateModuleCodes(context, "UserProductionModuleCodes", moduledObject.getUserProductionModuleCodes());
			investigateModuleCodes(context, "GeneratedProductionModuleCodes",
					moduledObject.getGeneratedProductionModuleCodes());
		}
	}

	private String checkIfModuleCodesWriteable(IXApplicationContext context, IXDesign design,
			IXModuledObject moduledObject)
	{
		return checkIfWriteable(context, design, "module codes", (moduledObject.getModuleCodeSetter() == null));
	}

	private <MC extends IXModuleCode> void investigateModuleCodes(
			IXApplicationContext context,
			String title,
			Set<MC> codes)
	{
		final IXOutputWindow outputWindow = context.getOutputWindow();
		for (MC code : codes) {
			outputWindow.println(getName(),
					"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>" + title + "</b> called: " + htmlObjectName(code));
		}
	}

	/**
	 * This method explores the pin-information that can be obtained from the IXAbstractPinList object.
	 *
	 * @param context - the 'context' containing the output window used to display the results.
	 * @param design - the design being investigated.
	 * @param pinList - the pinList being investigated.
	 */
	protected void investigatePinList(IXApplicationContext context, IXDesign design, IXAbstractPinList pinList)
	{
		final IXOutputWindow outputWindow = context.getOutputWindow();
		for (IXAbstractPin pin : pinList.getPins()) {
			if (pin instanceof IXCavity) {
				IXCavity cavity = (IXCavity) pin;
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>IXCavity</b> called: " + htmlObjectName(cavity) + ' ' +
								checkIfWriteable(context, design, pin));
				for (IXAbstractPin connPin : cavity.getConnectedPins()) {
					if (connPin instanceof IXDevicePin) {
						IXDevicePin devicePin = (IXDevicePin) connPin;
						outputWindow.println(getName(),
								"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>IXDevicePin</b> called: " +
										htmlObjectName(devicePin) + ' ' +
										checkIfWriteable(context, design, pin));
					}
					else if (connPin instanceof IXCavity) {
						IXCavity matedCavity = (IXCavity) connPin;
						outputWindow.println(getName(),
								"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; mated <b>IXCavity</b> called: " +
										htmlObjectName(matedCavity) + ' ' +
										checkIfWriteable(context, design, pin));
						for (IXAbstractPin mateConnPin : matedCavity.getConnectedPins()) {
							if (mateConnPin instanceof IXDevicePin) {
								IXDevicePin devicePin = (IXDevicePin) mateConnPin;
								outputWindow.println(getName(),
										"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>IXDevicePin</b> called: " +
												htmlObjectName(devicePin) + ' ' +
												checkIfWriteable(context, design, pin));
							}
						}
					}
				}
			}
			else {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>IXAbstractPin</b> called: " + htmlObjectName(pin) +
								' ' +
								checkIfWriteable(context, design, pin));
			}
			for (IXAbstractConductor conductor : pin.getConductors()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>IXAbstractConductor</b> called: " +
								htmlObjectName(conductor) + ' ' +
								checkIfWriteable(context, design, pin));
			}
		}
	}

	/**
	 * This method explores the information that can be obtained from the IXDesign object.
	 *
	 * @param context - the 'context' containing the output window used to display the results.
	 * @param design - the design being investigated.
	 */
	protected void investigateDesign(IXApplicationContext context, IXDesign design)
	{
		String currentDesignString = "";
		if (context.getCurrentDesign() == design) {
			currentDesignString = " and this is the <b>current design</b>.";
		}

		final IXOutputWindow outputWindow = context.getOutputWindow();
		outputWindow.println(getName(),
				"&nbsp;&nbsp;<b>IXDesign</b> called: " + htmlObjectName(design) + currentDesignString);

		// Here we see if we can obtain the IXDiagram objects from the current design.
		final Set<IXDiagram> diagrams = design.getDiagrams();
		for (IXDiagram diagram : diagrams) {
			outputWindow.println(getName(),
					"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXDiagram</b> called: " + htmlObjectName(diagram));
		}

		boolean found = false;
		final IXConnectivity connectivity = design.getConnectivity();
		if (connectivity != null) {

			// Here we see if we can obtain the IXAssembly objects from the current design.
			for (IXAssembly assembly : connectivity.getAssemblies()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXAssembly</b> called: " + htmlObjectName(assembly) + ' ' +
								checkIfWriteable(context, design, assembly));
				investigateLibraryObject(context, design, assembly);
				investigateModuledObject(context, design, assembly);
				found = true;
			}

			// Here we see if we can obtain the IXConnector objects from the current design.
			for (IXConnector connector : connectivity.getConnectors()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXConnector</b> called: " + htmlObjectName(connector) + ' ' +
								checkIfWriteable(context, design, connector));
				investigatePinList(context, design, connector);
				investigateLibraryObject(context, design, connector);
				investigateModuledObject(context, design, connector);
				found = true;
			}

			// Here we see if we can obtain the IXDevice objects from the current design.
			for (IXDevice device : connectivity.getDevices()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXDevice</b> called: " + htmlObjectName(device) + ' ' +
								checkIfWriteable(context, design, device));
				investigatePinList(context, design, device);
				investigateLibraryObject(context, design, device);
				investigateModuledObject(context, design, device);
				found = true;
			}

			// Here we see if we can obtain the IXBlock objects from the current design.
			for (IXBlock block : connectivity.getBlocks()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXBlock</b> called: " + htmlObjectName(block) + ' ' +
								checkIfWriteable(context, design, block));
				investigatePinList(context, design, block);
				investigateLibraryObject(context, design, block);
				investigateModuledObject(context, design, block);
				found = true;
			}

			// Here we see if we can obtain the IXGround objects from the current design.
			for (IXGround ground : connectivity.getGrounds()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXGround</b> called: " + htmlObjectName(ground) + ' ' +
								checkIfWriteable(context, design, ground));
				investigatePinList(context, design, ground);
				investigateLibraryObject(context, design, ground);
				investigateModuledObject(context, design, ground);
				found = true;
			}

			// Here we see if we can obtain the IXInterconnectDevice objects from the current design.
			for (IXInterconnectDevice interconnectDevice : connectivity.getInterconnectDevices()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXInterconnectDevice</b> called: " +
								htmlObjectName(interconnectDevice) + ' ' +
								checkIfWriteable(context, design, interconnectDevice));
				investigatePinList(context, design, interconnectDevice);
				investigateLibraryObject(context, design, interconnectDevice);
				investigateModuledObject(context, design, interconnectDevice);
				found = true;
			}

			// Here we see if we can obtain the IXInterconnect objects from the current design.
			for (IXInterconnect interconnect : connectivity.getInterconnects()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXInterconnect</b> called: " + htmlObjectName(interconnect) + ' ' +
								checkIfWriteable(context, design, interconnect));
				investigateLibraryObject(context, design, interconnect);
				investigateModuledObject(context, design, interconnect);
				found = true;
			}

			// Here we see if we can obtain the IXMulticore objects from the current design.
			for (IXMulticore multicore : connectivity.getMulticores()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXMulticore</b> called: " + htmlObjectName(multicore) + ' ' +
								checkIfWriteable(context, design, multicore));
				investigateLibraryObject(context, design, multicore);
				investigateModuledObject(context, design, multicore);
				found = true;
			}

			// Here we see if we can obtain the IXNet objects from the current design.
			for (IXNet net : connectivity.getNets()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXNet</b> called: " + htmlObjectName(net) + ' ' +
								checkIfWriteable(context, design, net));
				investigateLibraryObject(context, design, net);
				investigateModuledObject(context, design, net);
				found = true;
			}

			// Here we see if we can obtain the IXShield objects from the current design.
			for (IXShield shield : connectivity.getShields()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXShield</b> called: " + htmlObjectName(shield) + ' ' +
								checkIfWriteable(context, design, shield));
				investigateLibraryObject(context, design, shield);
				investigateModuledObject(context, design, shield);
				found = true;
			}

			// Here we see if we can obtain the IXSplice objects from the current design.
			for (IXSplice splice : connectivity.getSplices()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXSplice</b> called: " + htmlObjectName(splice) + ' ' +
								checkIfWriteable(context, design, splice));
				investigatePinList(context, design, splice);
				investigateLibraryObject(context, design, splice);
				investigateModuledObject(context, design, splice);
				found = true;
			}

			// Here we see if we can obtain the IXWire objects from the current design.
			for (IXWire wire : connectivity.getWires()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXWire</b> called: " + htmlObjectName(wire) + ' ' +
								checkIfWriteable(context, design, wire));
				investigateLibraryObject(context, design, wire);
				investigateModuledObject(context, design, wire);
				found = true;
			}

			// Here we see if we can obtain the IXHighway objects from the current design.
			for (IXHighway highway : connectivity.getHighways()) {
				outputWindow.println(getName(),
						"&nbsp;&nbsp;&nbsp;&nbsp;<b>IXHighway</b> called: " + htmlObjectName(highway) + ' ' +
								checkIfWriteable(context, design, highway));
				investigateLibraryObject(context, design, highway);
				investigateModuledObject(context, design, highway);
				found = true;
			}
		}

		if (!found) {
			outputWindow.println(getName(),
					"&nbsp;&nbsp;<FONT color=\"red\"><b>ERROR: No IXConnectivity?</b></FONT>");
		}
	}

	/**
	 * This method will display the information that can be obtained from the 'IXApplicationContext' object.
	 *
	 * @param context - the IXApplicationContext from which various objects can be obtained.
	 *
	 * @return true, even if we have not modified any properties/attributes on any objects.
	 */
	public boolean execute(IXApplicationContext context)
	{
		// Here we get the IXOutputWindow. We can use this to display debug messages or the output of your action.
		// HTML tags can be used in the IXOutputWindow.
		// Note that we are passing in the name of this plugin as a parameter to the methods on IXOutputWindow.
		// As a result, in the OutputWindow, we should see a new tab called 'Investigate Context' (the name of
		// this plugin) appear and the information we are printing should go to that tab. You do not have to use the
		// name of the plugin as the name of the tab in the OutputWindow. Moreover, you can write to as many
		// OutputWindow tabs as you like.
		final IXOutputWindow outputWindow = context.getOutputWindow();
		outputWindow.clear(getName());

		// Here we see if we can get the current project.
		// If we can we, will see if we can iterate over those
		// designs within the project that have been loaded.
		final IXProject currentProject = context.getCurrentProject();
		if (currentProject != null) {
			outputWindow.println(getName(),
					"<b>IXProject</b> called: " + htmlObjectName(currentProject));

			final Set<IXDesign> designs = currentProject.getDesigns();
			for (IXDesign design : designs) {
				investigateDesign(context, design);
			}
		}
		else {
			outputWindow.println(getName(),
					"<FONT color=\"red\"><b>ERROR: context.getCurrentProject() == null</b></FONT>");
		}

		// Here check that we can get the current design.
		final IXDesign currentDesign = context.getCurrentDesign();
		if (currentDesign == null) {
			outputWindow.println(getName(),
					"<FONT color=\"red\"><b>ERROR: We should be able to get the current IXDesign object.</b></FONT>");
		}

		// Here we see if we can get the current diagram that has focus.
		final IXDiagram currentDiagram = context.getCurrentDiagram();
		if (currentDiagram == null) {
			outputWindow.println(getName(),
					"<FONT color=\"red\"><b>ERROR: We should be able to get the current IXDiagram object.</b></FONT>");
		}

		// Here we see if we can get the current library.
		final IXLibrary currentLibrary = context.getLibrary();
		if (currentLibrary == null) {
			outputWindow.println(getName(),
					"<FONT color=\"red\"><b>ERROR: We should be able to get an IXLibrary object.</b></FONT>");
		}

		// We have not modified any objects, but we return true anyway to indicate the success of this action.
		return true;
	}
}
