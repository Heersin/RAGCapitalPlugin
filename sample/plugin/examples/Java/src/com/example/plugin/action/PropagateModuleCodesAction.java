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

package com.example.plugin.action;

import com.mentor.chs.api.IXAbstractPinList;
import com.mentor.chs.api.IXCavity;
import com.mentor.chs.api.IXCavityDetail;
import com.mentor.chs.api.IXConnectivity;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXFunctionalModuleCode;
import com.mentor.chs.api.IXHarness;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXInsulationRun;
import com.mentor.chs.api.IXModuleCodeSetter;
import com.mentor.chs.api.IXModuledDesign;
import com.mentor.chs.api.IXModuledObject;
import com.mentor.chs.api.IXMultiLocationComponent;
import com.mentor.chs.api.IXNode;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXProductionModuleCode;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXOutputWindow;
import com.mentor.chs.plugin.action.IXHarnessAction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Ramdomly distributes codes on objects in a design.
 */
public class PropagateModuleCodesAction extends BaseAction implements IXHarnessAction
{

	private List<IXFunctionalModuleCode> functionalModuleCodes;
	private List<IXProductionModuleCode> productionModuleCodes;

	private int functionalModuleCodeIndex = 0;
	private int productionModuleCodeIndex = 0;

	private boolean changeMade;

	private IXOutputWindow outputWindow;

	public PropagateModuleCodesAction()
	{
		super("Propagate Module Codes",
				"1.1",
				"This propagates the module codes from the project onto all the objects in a harness design.");
	}

	@Override public boolean isAvailable(IXApplicationContext context)
	{
		return true;
	}

	@Override public boolean isReadOnly()
	{
		return false;
	}

	@Override public boolean execute(IXApplicationContext applicationContext)
	{
		outputWindow = applicationContext.getOutputWindow();
		outputWindow.println(getName(), "begin");

		changeMade = false;

		final IXModuledDesign moduledDesign = (IXModuledDesign) applicationContext.getCurrentDesign();

		functionalModuleCodes =
				new ArrayList<IXFunctionalModuleCode>(moduledDesign.getApplicableFunctionalModuleCodes());
		productionModuleCodes =
				new ArrayList<IXProductionModuleCode>(moduledDesign.getApplicableProductionModuleCodes());

		functionalModuleCodeIndex = 0;
		productionModuleCodeIndex = 0;

		IXDesign design = applicationContext.getCurrentDesign();

		IXConnectivity connectivity = design.getConnectivity();
		setModuleCodes(connectivity.getAssemblies());
		setModuleCodes(connectivity.getHighways());
		setModuleCodes(connectivity.getConnectors());
		setModuleCodes(connectivity.getBlocks());
		setModuleCodes(connectivity.getDevices());
		setModuleCodes(connectivity.getGrounds());
		setModuleCodes(connectivity.getInterconnectDevices());
		setModuleCodes(connectivity.getInterconnects());
		setModuleCodes(connectivity.getMulticores());
		setModuleCodes(connectivity.getNets());
		setModuleCodes(connectivity.getShields());
		setModuleCodes(connectivity.getSplices());
		setModuleCodes(connectivity.getWires());

		if (design instanceof IXHarnessDesign) {
			IXHarnessDesign harnessDesign = (IXHarnessDesign) design;
			IXHarness harness = harnessDesign.getHarness();
			setModuleCodes(harness.getBreakoutTapes());
			setModuleCodes(harness.getBundles());
			setModuleCodes(harness.getClips());
			setModuleCodes(harness.getGrommets());
			setModuleCodes(harness.getConnectors());
			setModuleCodes(harness.getInsulationRuns());
			setModuleCodes(harness.getMultiLocationComponents());
			setModuleCodes(harness.getMulticores());
			setModuleCodes(harness.getNodes());
			setModuleCodes(harness.getOtherComponents());
			setModuleCodes(harness.getSplices());
			setModuleCodes(harness.getSpotTapes());
			setModuleCodes(harness.getWires());
		}

		outputWindow.println(getName(), "end");

		return changeMade;
	}

	private <X extends IXObject> void setModuleCodes(Set<X> xObjs)
	{
		if (xObjs == null) {
			return;
		}
		for (X xObj : xObjs) {

			if (xObj instanceof IXModuledObject) {
				IXModuledObject moduledObject = (IXModuledObject) xObj;
				setModuleCode(moduledObject);
			}

			if (xObj instanceof IXAbstractPinList) {
				IXAbstractPinList xParentObj = (IXAbstractPinList) xObj;
				setModuleCodes(xParentObj.getPins());
			}

			if (xObj instanceof IXInsulationRun) {
				IXInsulationRun xParentObj = (IXInsulationRun) xObj;
				setModuleCodes(xParentObj.getInsulations());
			}
			else if (xObj instanceof IXConnector) {
				IXConnector xParentObj = (IXConnector) xObj;
				setModuleCode(xParentObj.getBackshell());
			}
			else if (xObj instanceof IXCavity) {
				IXCavity xParentObj = (IXCavity) xObj;
				setModuleCodes(xParentObj.getAdditionalComponents());
				setModuleCodes(xParentObj.getCavityDetails());
			}
			else if (xObj instanceof IXCavityDetail) {
				IXCavityDetail xParentObj = (IXCavityDetail) xObj;
				setModuleCode(xParentObj.getCavitySeal());
				setModuleCode(xParentObj.getTerminal());
			}
			else if (xObj instanceof IXNode) {
				IXNode xParentObj = (IXNode) xObj;
				setModuleCodes(xParentObj.getAdditionalComponents());
			}
			else if (xObj instanceof IXMultiLocationComponent) {
				IXMultiLocationComponent xParentObj = (IXMultiLocationComponent) xObj;
				setModuleCodes(xParentObj.getAdditionalComponents());
			}
			else if (xObj instanceof IXWire) {
				IXWire xParentObj = (IXWire) xObj;
				setModuleCodes(xParentObj.getWireEnds());
			}
		}
	}

	private void setModuleCode(IXModuledObject moduledObject)
	{
		if (moduledObject == null) {
			return;
		}

		outputWindow.println(getName(), "updating " + moduledObject.toHTML());

		IXModuleCodeSetter moduleCodeSetter = moduledObject.getModuleCodeSetter();
		if (moduleCodeSetter != null) {
			if (!functionalModuleCodes.isEmpty()) {
				moduleCodeSetter.setUserFunctionalModuleCodes(getNextFunctionalModuleCodes(1));
				moduleCodeSetter.setGeneratedFunctionalModuleCodes(getNextFunctionalModuleCodes(2));
				changeMade = true;
			}
			if (!productionModuleCodes.isEmpty()) {
				moduleCodeSetter.setUserProductionModuleCodes(getNextProductionModuleCodes(2));
				moduleCodeSetter.setGeneratedProductionModuleCodes(getNextProductionModuleCodes(1));
				changeMade = true;
			}
		}
	}

	private Set<IXFunctionalModuleCode> getNextFunctionalModuleCodes(int size)
	{
		Set<IXFunctionalModuleCode> rval = new HashSet<IXFunctionalModuleCode>();
		for (int i = 0; i < size; i++) {
			rval.add(getNextFunctionalModuleCode());
		}
		return rval;
	}

	private IXFunctionalModuleCode getNextFunctionalModuleCode()
	{
		try {
			return functionalModuleCodes.get(functionalModuleCodeIndex);
		}
		finally {
			functionalModuleCodeIndex = (functionalModuleCodeIndex + 1) % functionalModuleCodes.size();
		}
	}

	private Set<IXProductionModuleCode> getNextProductionModuleCodes(int size)
	{
		Set<IXProductionModuleCode> rval = new HashSet<IXProductionModuleCode>();
		for (int i = 0; i < size; i++) {
			rval.add(getNextProductionModuleCode());
		}
		return rval;
	}

	private IXProductionModuleCode getNextProductionModuleCode()
	{
		try {
			return productionModuleCodes.get(productionModuleCodeIndex);
		}
		finally {
			productionModuleCodeIndex = (productionModuleCodeIndex + 1) % productionModuleCodes.size();
		}
	}
}
