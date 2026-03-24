/**
 * Copyright 2006 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.drc;

import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.drc.IXDRCViolationReporter;
import com.mentor.chs.plugin.drc.IXHarnessDRCheck;
import com.mentor.chs.plugin.drc.IXIntegratorDRCheck;
import com.mentor.chs.plugin.drc.IXLogicDRCheck;
import com.mentor.chs.plugin.drc.IXDRCheckAdvancedConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * DRC check to ensure that all the wires leading to a connector have different colors.
 * <p/>
 */
public class ConnectorWireColorValidatorDRCheck extends BaseDRCheck
		implements IXLogicDRCheck, IXIntegratorDRCheck, IXHarnessDRCheck,
		IXApplicationContextListener, IXDRCheckAdvancedConfiguration
{

	protected IXApplicationContext applicationContext;

	/**
	 *
	 */
	public ConnectorWireColorValidatorDRCheck()
	{
		super(
				"Connector Wire Color Validator",
				"1.1",
				"Ensure that all wires leading to a connector have different colors.",
				true,
				Severity.Error);
	}

	public void setApplicationContext(IXApplicationContext context)
	{
		applicationContext = context;
	}

	/**
	 * Get the wires connected to the specified connector
	 *
	 * @param xConnector
	 *
	 * @return
	 */
	protected Set<IXWire> getConnectedWires(IXConnector xConnector)
	{
		Set<IXWire> connectedWires = new HashSet<IXWire>();
		Set<IXAbstractPin> xPinSet = xConnector.getPins();
		for (IXAbstractPin xPin : xPinSet) {
			Set<IXAbstractConductor> xConductorSet = xPin.getConductors();
			for (IXAbstractConductor xConductor : xConductorSet) {
				if (xConductor instanceof IXWire) {
					connectedWires.add((IXWire) xConductor);
				}
			}
		}
		return connectedWires;
	}

	/* (non-Javadoc)
		 * @see com.mentor.chs.plugin.drc.IXDRCheck#begin(com.mentor.chs.plugin.drc.IXDRCViolationReporter)
		 */
	public void begin(IXDRCViolationReporter arg0)
	{
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#check(com.mentor.chs.plugin.drc.IXDRCViolationReporter, com.mentor.chs.api.IXObject)
	 */
	public void check(IXDRCViolationReporter vReporter, IXObject xObject)
	{

		if (xObject instanceof IXConnector) {

			IXConnector xConnector = (IXConnector) xObject;
			applicationContext.getOutputWindow()
					.println("____DEBUG: found connector <b>" + getObjectName(xConnector) + "</b>. " + xConnector);

			// 1) Get the wires that are connected to this connector
			Set<IXWire> xWireSet = getConnectedWires(xConnector);

			// 2) Build a map that, for each wire color, contains a list of the wires
			// leading to the connector that has that color.
			Map<String, List<IXWire>> wireColorMap = new HashMap<String, List<IXWire>>();
			for (IXWire xWire : xWireSet) {
				final String color = xWire.getAttribute(ATTRIBUTE_WIRE_COLOR);

				applicationContext.getOutputWindow().println("________DEBUG: color = <b>" + color +
						"</b> of wire <b> " + getObjectName(xWire) + "</b>" + xConnector);

				List<IXWire> list = wireColorMap.get(color);
				if (list == null) {
					list = new ArrayList<IXWire>();
					wireColorMap.put(color, list);
				}
				list.add(xWire);
			}

			// 3) Search the map for any color that has more than one
			// wire associated with it
			for (List<IXWire> list : wireColorMap.values()) {
				if (list.size() > 1) {
					// TODO: at this point we have a list of all the wires that
					// have the same color, we should list the wires in the message
					// and the wire color.
					vReporter.report(Severity.Error,
							"Found a connector {0} that has one or more wires (like {1} and {2}) with the same color. For more information, please click <A href=\"http://www.mentor.com\">here</A>.",
							xConnector, list.get(0), list.get(1));
					break;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#end(com.mentor.chs.plugin.drc.IXDRCViolationReporter)
	 */
	public void end(IXDRCViolationReporter arg0)
	{
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheckAdvancedConfiguration#getAvailability(RunningMode, String)
	 */
	public boolean getAvailability(RunningMode runningMode, String designAbstraction)
	{
		if (runningMode == RunningMode.ONSAVE) {	//The rule is not available to run on OnSave mode
			return false;
		}
		else if (runningMode == RunningMode.BACKGROUND && "Physical".equalsIgnoreCase(designAbstraction)) {
			// The rule is not available to run on background mode for the designs with design abstraction "Physical"
			return false;
		}
		else {
			return true;
		}
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheckAdvancedConfiguration#getSeverity(RunningMode, String)
	 */
	public Severity getSeverity(RunningMode runningMode, String designAbstraction)
	{
		if (runningMode == RunningMode.ONSAVE) {
			//The rule has severity Warning for OnSave running mode regardless of design abstraction
			return Severity.Warning;
		}
		else if (runningMode == RunningMode.BACKGROUND && "Physical".equalsIgnoreCase(designAbstraction)) {
			//The rule has severity Information for Background running mode and "Physical" design abstraction
			return Severity.Information;
		}
		else {	// The rule has severity Error for all other cases
			return Severity.Error;
		}
	}
}
