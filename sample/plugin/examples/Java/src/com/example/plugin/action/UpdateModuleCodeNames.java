/**
 * Copyright 2012 Mentor Graphics Corporation. All Rights Reserved.
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

import com.mentor.chs.api.IXFunctionalModuleCode;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXModuleCode;
import com.mentor.chs.api.IXProductionModuleCode;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXHarnessAction;

import java.util.Set;

/**
 * Ramdomly distributes codes on objects in a design.
 */
public class UpdateModuleCodeNames extends BaseAction implements IXHarnessAction
{

	private boolean changeMade = false;

	public UpdateModuleCodeNames()
	{
		super("Update Module Code Names",
				"1.0",
				"Example custom action to update all module code names");
	}

	@Override public boolean isAvailable(IXApplicationContext context)
	{
		return context.getCurrentDesign() instanceof IXHarnessDesign;
	}

	@Override public boolean isReadOnly()
	{
		return false;
	}

	@Override public boolean execute(IXApplicationContext applicationContext)
	{
		// Get the current design & get all the functional and production module codes.
		IXHarnessDesign des = (IXHarnessDesign) applicationContext.getCurrentDesign();
		Set<IXFunctionalModuleCode> functionalModuleCodes = des.getFunctionalModuleCodes();
		Set<IXProductionModuleCode> productionModuleCodes = des.getProductionModuleCodes();

		// Update all functional module codes (append XX if not already present)
		for (IXFunctionalModuleCode fmc : functionalModuleCodes) {
			updateModuleCode(fmc, "XX", applicationContext);
		}

		// Update all production module codes (append YY if not already present)
		for (IXProductionModuleCode pmc : productionModuleCodes) {
			updateModuleCode(pmc, "YY", applicationContext);
		}
		return changeMade;
	}

	private void updateModuleCode(IXModuleCode moduleCode, String modifier, IXApplicationContext applicationContext)
	{
		String val = moduleCode.getAttribute("name");
		if (!val.toLowerCase().startsWith(modifier.toLowerCase())) {
			String newVal = modifier + val;
			moduleCode.getAttributeSetter().addAttribute("name", newVal);
			applicationContext.getOutputWindow().println("Updating module code " + val + " to " + newVal);
			changeMade = true;
		}
	}
}
