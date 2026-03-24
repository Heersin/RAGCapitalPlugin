/**
 * Copyright 2008 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.autolink;

import com.mentor.chs.plugin.changemanager.IXHarnessXCAutoLink;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXHarness;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXGrommet;
import com.mentor.chs.api.IXClip;
import com.mentor.chs.api.IXOtherComponent;
import com.mentor.chs.api.IXInsulationRun;
import com.mentor.chs.api.IXInsulation;
import com.mentor.chs.api.IXBundle;
import com.mentor.chs.api.IXSplice;
import com.mentor.chs.api.IXSpotTape;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXMulticore;
import com.mentor.chs.api.IXRingTerminal;
import chs.bridges.x2ml.harness.Fixing;
import chs.bridges.x2ml.harness.Protection;
import chs.bridges.x2ml.harness.Bundle;
import chs.bridges.x2ml.IWiringContainer;
import chs.bridges.x2ml.Connector;
import chs.bridges.x2ml.X2MLElement;
import chs.bridges.x2ml.UserProperty;
import chs.bridges.x2ml.Splice;
import chs.bridges.x2ml.Wire;
import chs.bridges.x2ml.Multicore;

import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashSet;

/**
 * Sample plugin for Integrator autolink and HarnessXC autolink.
 * Demonstrates how to delegate Change Manager autolink action
 */
public class HarnessXCAutoLink implements IXHarnessXCAutoLink
{
	private static final String AUTOLINK_PROP_NAME = "AUTOLINK";
	/**
	 * Return the description of the plugin
	 * @return String type - description of the plugin
	 */
	public String getDescription() {
		return "Finds the autolinkable object for a selected object in Harness XC Change Manager";
	}

	/**
	 * Return the name of the plugin
	 * @return String type - name of the plugin
	 */
	public String getName() {
		return "HarnessXCAutoLink";
	}

	/**
	 * Return the version of the plugin
	 * @return String type - version of the plugin
	 */
	public String getVersion() {
		return "1.0";
	}

	/**
	 * Given an x2ml element and an IXHarness, find the design object in IXHarness,
	 * which is to be linked to the x2ml element
	 * @param x2mlElem x2ml element which is to be linked
	 * @param harness IXHarness of the design
	 * @return IXObject instance
	 */
	public IXObject getLinkableDesignObject(Object x2mlElem, IXHarness harness)
	{
		if(harness != null && x2mlElem instanceof X2MLElement) {
			Collection<UserProperty> userProps = ((X2MLElement)x2mlElem).getUserProperties();
			String autoLinkVal = null;
			for(UserProperty prop : userProps) {
				if(prop.getName().equalsIgnoreCase(AUTOLINK_PROP_NAME)) {
					autoLinkVal = prop.getValue();
					break;
				}
			}
			if(autoLinkVal != null && !"".equals(autoLinkVal.trim())) {
				Set desObjs = null;
				if(x2mlElem instanceof Bundle) {
					desObjs = harness.getBundles();
				} else if(x2mlElem instanceof Splice) {
					desObjs = harness.getSplices();
				} else if(x2mlElem instanceof Fixing) {
					desObjs = new HashSet();
					desObjs.addAll(harness.getClips());
					desObjs.addAll(harness.getGrommets());
					desObjs.addAll(harness.getOtherComponents());
				} else if(x2mlElem instanceof Protection) {
					Set<IXInsulationRun> insRuns = harness.getInsulationRuns();
					desObjs = new HashSet();
					for(IXInsulationRun insRun : insRuns) {
						desObjs.addAll(insRun.getInsulations());
					}
					desObjs.addAll(harness.getSpotTapes());
				} else if(x2mlElem instanceof Connector) {
					if(((Connector)x2mlElem).isRingTerminal())
					{
						desObjs = harness.getRingTerminals();
					}
					else{
						desObjs = harness.getConnectors();
					}
				} else if(x2mlElem instanceof Wire) {
					desObjs = harness.getWires();
				} else if(x2mlElem instanceof Multicore) {
					desObjs = harness.getMulticores();
				}

				IXObject reqObj = null;
				if(desObjs != null) {
					Iterator iter = desObjs.iterator();
					while(iter.hasNext()) {
						Object obj = iter.next();
						if(obj instanceof IXObject &&
												autoLinkVal.equals(((IXObject)obj).getProperty(AUTOLINK_PROP_NAME))) {
							if(reqObj == null) {
								reqObj = (IXObject)obj;
							} else {
								return null;
							}
						}
					}
				}
				return reqObj;
			}
		}
		return null;
	}

	/**
	 * Given a design object and an x2ml harness, find the x2ml element in the x2ml harness,
	 * which is to be linked to the design object
	 * @param designObject IXObject
	 * @param harness x2ml harness
	 * @return x2ml element
	 */
	public Object getLinkableExternalObject(IXObject designObject, Object harness)
	{
		X2MLElement reqObj = null;
		if(harness instanceof IWiringContainer && designObject instanceof IXObject) {
			String autoLinkVal = designObject.getProperty(AUTOLINK_PROP_NAME);
			if(autoLinkVal != null && !"".equals(autoLinkVal.trim())) {
				Collection harObjs = null;
				if(designObject instanceof IXBundle) {
					harObjs = ((IWiringContainer)harness).getElementsByType(Bundle.getElementName());
				} else if(designObject instanceof IXSplice) {
					harObjs = ((IWiringContainer)harness).getElementsByType(Splice.getElementName());
				} else if(designObject instanceof IXClip || designObject instanceof IXGrommet ||
																			designObject instanceof IXOtherComponent) {
					harObjs = ((IWiringContainer)harness).getElementsByType(Fixing.getElementName());
				} else if(designObject instanceof IXInsulation || designObject instanceof IXSpotTape) {
					harObjs = ((IWiringContainer)harness).getElementsByType(Protection.getElementName());
				} else if(designObject instanceof IXConnector || designObject instanceof IXRingTerminal) {
					harObjs = ((IWiringContainer)harness).getElementsByType(Connector.getElementName());
				} else if(designObject instanceof IXWire) {
					harObjs = ((IWiringContainer)harness).getElementsByType(Wire.getElementName());
				} else if(designObject instanceof IXMulticore) {
					harObjs = ((IWiringContainer)harness).getElementsByType(Multicore.getElementName());
				}
				if(harObjs != null) {
					Iterator iter = harObjs.iterator();
					while(iter.hasNext()) {
						Object obj = iter.next();
						if(obj instanceof X2MLElement) {
							Collection<UserProperty> props = ((X2MLElement)obj).getUserProperties();
							for(UserProperty prop : props) {
								if(AUTOLINK_PROP_NAME.equalsIgnoreCase(prop.getName()) &&
																				autoLinkVal.equals(prop.getValue())) {
									if(reqObj == null) {
										reqObj = (X2MLElement)obj;
										break;
									} else {
										// ambiguous match!
										return null;
									}
								}
							}
						}
					}
				}
			}
		}
		return reqObj;
	}

	/**
	 * Whether a given object is handled by the plugin for autolink or not
	 * @param object x2ml element or IXObject
	 * @return boolean whether or not autolinkable by the plugin
	 */
	public boolean isAutoLinkableByPlugin(Object object)
	{
		return (object instanceof IXObject || object instanceof X2MLElement);
	}
}


