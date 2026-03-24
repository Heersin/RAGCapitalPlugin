/**
 * Copyright 2009 Mentor Graphics Corporation. All Rights Reserved.
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
package com.example.plugin.bridge;

import com.mentor.chs.plugin.changemanager.IXHarnessBridgeProcessor;
import com.mentor.chs.plugin.changemanager.IXIntegratorBridgeProcessor;
import com.mentor.chs.plugin.changemanager.IXLogicBridgeProcessor;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.api.IXLibraryColorCode;
import chs.bridges.x2ml.UserProperty;
import chs.bridges.x2ml.WiringDesign;
import chs.bridges.x2ml.Wire;
import chs.bridges.x2ml.harness.Harness;
import chs.bridges.adaptors.IAdaptorReporter;

import java.util.Collection;
import java.util.HashSet;

/**
 * Sample plugin for Bridge processing.
 */
public class BridgeProcessor implements IXHarnessBridgeProcessor, IXIntegratorBridgeProcessor, IXLogicBridgeProcessor,
		IXApplicationContextListener
{

	/**
	 * Application context
	 */
	private IXApplicationContext m_appContext;

	/**
	 * Return the description of the plugin
	 * @return String type - description of the plugin
	 */
	public String getDescription() {
		return "Demonstrate bridge processing. Color code translation between code name-description & add properties";
	}

	/**
	 * Return the name of the plugin
	 * @return String type - name of the plugin
	 */
	public String getName() {
		return "Bridge Processor Example";
	}

	/**
	 * Return the version of the plugin
	 * @return String type - version of the plugin
	 */
	public String getVersion() {
		return "1.0";
	}

	public void processBridgeIn(Object x2mlContainer, IXApplicationContext applicationContext)
	{
		Collection wireColl = null;
		if(x2mlContainer instanceof Harness){
			Harness incHarn = (Harness)x2mlContainer;
			wireColl = incHarn.getElementsByType(Wire.getElementName());
		}
		else if(x2mlContainer instanceof WiringDesign){
			WiringDesign des = (WiringDesign)x2mlContainer;
			wireColl = des.getElementsByType(Wire.getElementName());
		}
		for(Object elem : wireColl){
			if(elem instanceof Wire){
				Wire wire = (Wire) elem;
				String color = wire.getColor();
				if(color != null && !color.trim().isEmpty()){
					boolean found = false;
					for(IXLibraryColorCode libcolor : applicationContext.getLibrary().getColorCodes()){
					   if(libcolor.getAttribute("Description").equalsIgnoreCase(color)){
						   found = true;
						   wire.setColor(libcolor.getAttribute("ColorCode"));
						   break;
					   }
					}
					if (!found && m_appContext != null && m_appContext.getBridgeContext() != null) {
						IAdaptorReporter reporter =
								(IAdaptorReporter)m_appContext.getBridgeContext().getAdaptorReporter();
						if (reporter != null) {
							reporter.logMessage(IAdaptorReporter.WARNING,
									new StringBuilder().append("Color with description '").append(color)
											.append("' is not present in the library").toString(),
									"Add a valid color to the wire (This is reported by the Bridge processor plugin)");
						}
					}
					wire.addUserProperty(new UserProperty("ColorBeforeBridgeInTranslation",color));
				}
			}
		}
	}

	public void processBridgeOut(Object x2mlContainer, IXApplicationContext applicationContext)
	{
		Collection wireColl = null;
		if(x2mlContainer instanceof Harness){
			Harness incHarn = (Harness)x2mlContainer;
			wireColl = incHarn.getElementsByType(Wire.getElementName());
		}
		else if(x2mlContainer instanceof WiringDesign){
			WiringDesign des = (WiringDesign)x2mlContainer;
			wireColl = des.getElementsByType(Wire.getElementName());
		}
		Collection<String> reportedColors = new HashSet<String>();
		for(Object elem : wireColl){
			if(elem instanceof Wire){
				Wire wire = (Wire) elem;
				String color = wire.getColor();
				if(color != null && !color.trim().isEmpty()){
					for(IXLibraryColorCode libcolor : applicationContext.getLibrary().getColorCodes()){
					   if(libcolor.getAttribute("ColorCode").equalsIgnoreCase(color)){
						   wire.setColor(libcolor.getAttribute("Description"));
						   break;
					   }
					}
					if (m_appContext != null && m_appContext.getBridgeContext() != null &&
							!reportedColors.contains(color)) {
						IAdaptorReporter reporter =
								(IAdaptorReporter)m_appContext.getBridgeContext().getAdaptorReporter();
						if (reporter != null) {
							reporter.logMessage(IAdaptorReporter.WARNING,
									new StringBuilder().append("Color code '").append(color)
											.append("' is translated to '").append(wire.getColor())
											.append("\' by the bridge processor").toString(), null);
							reportedColors.add(color);
						}
					}
					wire.addUserProperty(new UserProperty("ColorBeforeBridgeOutTranslation",color));
				}
			}
		}
	}

	public void setApplicationContext(IXApplicationContext applicationContext)
	{
		m_appContext = applicationContext;
	}
}
