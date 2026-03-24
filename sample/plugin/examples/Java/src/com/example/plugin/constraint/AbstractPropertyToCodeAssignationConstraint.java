/**
 * Copyright 2010 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.constraint;

import com.mentor.chs.api.IXAbstractPinList;
import com.mentor.chs.api.IXCavity;
import com.mentor.chs.api.IXCavityDetail;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXInsulationRun;
import com.mentor.chs.api.IXModuledObject;
import com.mentor.chs.api.IXMultiLocationComponent;
import com.mentor.chs.api.IXNode;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.IXOutputWindow;
import com.mentor.chs.plugin.constraint.IXModuleAssignationResult;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Base class for those constraints that convert properties into real module codes using a constraint.
 */
public abstract class AbstractPropertyToCodeAssignationConstraint
		extends BaseConstraint
{

	private Properties properties;

	private boolean changeMade;
	private String moduleCodeNames;
	private String moduleCodeSep;

	protected IXModuleAssignationResult<?> currentResult;

	protected AbstractPropertyToCodeAssignationConstraint(String n, String v, String d)
	{
		super(n, v, d);
	}

	private Properties loadProperties()
	{
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			InputStream inputStream = classLoader
					.getResourceAsStream("modulecodes.properties");
			properties = new Properties();
			if (inputStream != null) {
				properties.load(inputStream);
			}
			return properties;
		}
		catch (IOException ex) {

			ex.printStackTrace();
			return null;
		}
	}

	protected String getPropertyValue(String key, String def)
	{
		if (properties == null) {
			properties = loadProperties();
		}
		if (properties == null) {
			throw new RuntimeException("Could not load properties");
		}
		String value = (String) properties.get(key);
		if (value != null) {
			return value.trim();
		}
		return def;
	}

	/**
	 * The main method that does the work for this constraint.
	 *
	 * @param design - the design, we get the wires from this object
	 * @param objects - the object that were selected.
	 * @param result - the result object.
	 *
	 * @return true
	 */
	protected boolean execute(
			IXHarnessDesign design,
			Set<IXModuledObject> objects,
			IXModuleAssignationResult<?> result)
	{
		currentResult = result;

		properties = null;

		moduleCodeNames = getNameOfProperty();
		moduleCodeSep = getCodeNameSeparator();

		// Ensure we do all the wires too...
		Set<IXModuledObject> toWorkOn = new HashSet<IXModuledObject>();
		toWorkOn.addAll(objects);
		toWorkOn.addAll(design.getConnectivity().getWires());
		toWorkOn.addAll(design.getHarness().getWires());

		//result.addMessage("begin");

		changeMade = false;
		convertModuleCodes(result, toWorkOn);
		//result.addMessage("end");

		return changeMade;
	}

	protected abstract String getCodeNameSeparator();

	protected abstract String getNameOfProperty();

	private <X extends IXObject> void convertModuleCodes(IXModuleAssignationResult<?> result, Set<X> xObjs)
	{
		if (xObjs == null) {
			return;
		}
		for (X xObj : xObjs) {

			if (xObj instanceof IXModuledObject) {
				IXModuledObject moduledObject = (IXModuledObject) xObj;
				setModuleCode(result, moduledObject);
			}

			if (xObj instanceof IXAbstractPinList) {
				IXAbstractPinList xParentObj = (IXAbstractPinList) xObj;
				convertModuleCodes(result, xParentObj.getPins());
			}

			if (xObj instanceof IXInsulationRun) {
				IXInsulationRun xParentObj = (IXInsulationRun) xObj;
				convertModuleCodes(result, xParentObj.getInsulations());
			}
			else if (xObj instanceof IXConnector) {
				IXConnector xParentObj = (IXConnector) xObj;
				setModuleCode(result, xParentObj.getBackshell());
			}
			else if (xObj instanceof IXCavity) {
				IXCavity xParentObj = (IXCavity) xObj;
				convertModuleCodes(result, xParentObj.getAdditionalComponents());
				convertModuleCodes(result, xParentObj.getCavityDetails());
			}
			else if (xObj instanceof IXCavityDetail) {
				IXCavityDetail xParentObj = (IXCavityDetail) xObj;
				setModuleCode(result, xParentObj.getCavitySeal());
				setModuleCode(result, xParentObj.getTerminal());
			}
			else if (xObj instanceof IXNode) {
				IXNode xParentObj = (IXNode) xObj;
				convertModuleCodes(result, xParentObj.getAdditionalComponents());
			}
			else if (xObj instanceof IXMultiLocationComponent) {
				IXMultiLocationComponent xParentObj = (IXMultiLocationComponent) xObj;
				convertModuleCodes(result, xParentObj.getAdditionalComponents());
			}
			else if (xObj instanceof IXWire) {
				IXWire xParentObj = (IXWire) xObj;
				convertModuleCodes(result, xParentObj.getWireEnds());
			}
		}
	}

	private void setModuleCode(IXModuleAssignationResult<?> result, IXModuledObject moduledObject)
	{
		if (moduledObject == null) {
			return;
		}

		//result.addMessage("updating " + moduledObject.toHTML());

		setModuleCodes(moduledObject, getModuleCodes(moduledObject));

		changeMade = true;
	}

	private void setModuleCodes(IXModuledObject moduledObject, Set<String> codes)
	{
		if (currentResult != null) {
			for (String code : codes) {
				currentResult.addNewNonTechnicalModuleCodeToObject(moduledObject, code);
			}
		}
	}

	private Set<String> getModuleCodes(IXModuledObject moduledObject)
	{
		String value = moduledObject.getProperty(moduleCodeNames);
		if (value == null) {
			return Collections.emptySet();
		}
		Set<String> rval = new HashSet<String>();
		StringTokenizer st = new StringTokenizer(value, moduleCodeSep, false);
		while (st.hasMoreTokens()) {
			String moduleCodeName = st.nextToken().trim();
			if (moduleCodeName != null && !moduleCodeName.isEmpty()) {
				rval.add(moduleCodeName);
			}
		}
		return rval;
	}
}
