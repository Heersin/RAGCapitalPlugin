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

import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXModuledObject;
import com.mentor.chs.api.IXProductionModuleCode;
import com.mentor.chs.plugin.constraint.IXModuleAssignationResult;
import com.mentor.chs.plugin.constraint.IXProductionModuleAssignationConstraint;

import java.util.Set;

/**
 * This plugin converts a property on an object in a harness design into real module codes.
 */
public class ProductionPropertyToCodeAssignationConstraint
		extends AbstractPropertyToCodeAssignationConstraint
		implements IXProductionModuleAssignationConstraint
{

	public ProductionPropertyToCodeAssignationConstraint()
	{
		super("ProductionPropertyToCodeAssignationConstraint",
				"1.1",
				"Converts a property string into a real module code");
	}

	@Override
	protected String getCodeNameSeparator()
	{
		return getPropertyValue("property.name.produciton.separator", ",");
	}

	@Override
	protected String getNameOfProperty()
	{
		return getPropertyValue("property.name.produciton", "ProdModules");
	}

	@Override public boolean assignModuleCodes(
			Set<IXModuledObject> objects,
			IXHarnessDesign design,
			IXModuleAssignationResult<IXProductionModuleCode> result)
	{
		execute(design, objects, result);
		return true;
	}
}
