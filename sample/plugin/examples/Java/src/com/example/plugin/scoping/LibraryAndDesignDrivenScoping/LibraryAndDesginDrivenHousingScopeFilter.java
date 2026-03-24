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

package com.example.plugin.scoping.LibraryAndDesignDrivenScoping;

import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXLibraryCavityGroup;
import com.mentor.chs.api.IXLibraryComponentScope;
import com.mentor.chs.api.IXLibraryHousingDefinition;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibraryScope;
import com.mentor.chs.api.IXLibraryScopeCode;
import com.mentor.chs.plugin.IXComponentSelectionContext;
import com.mentor.chs.plugin.filter.IXHousingScopeSelectionFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LibraryAndDesginDrivenHousingScopeFilter implements IXHousingScopeSelectionFilter
{

	public Collection apply(Collection objs, IXComponentSelectionContext scopingContext)
	{
		Collection<IXLibraryHousingDefinition> retObjs = new HashSet<IXLibraryHousingDefinition>();
		if (scopingContext != null) {
			for (Object obj : objs) {
				if (obj instanceof IXLibraryHousingDefinition) {
					if (isLibrarySatisified((IXLibraryHousingDefinition) obj, scopingContext) &&
							isDesignSatisified((IXLibraryHousingDefinition) obj, scopingContext)) {
						retObjs.add((IXLibraryHousingDefinition) obj);
					}
				}
			}
		}
		return retObjs;
	}

	private boolean isLibrarySatisified(IXLibraryHousingDefinition houseDef, IXComponentSelectionContext scopingContext)
	{
		Set<IXLibraryScope> houseScopes;
		if (houseDef != null) {
			houseScopes = houseDef.getScopes();
		}
		else {
			return true;
		}

		Set<? extends IXLibraryComponentScope> referredObjCompScopes = Collections.emptySet();
		IXLibraryObject referredObj = houseDef.getSubComponent();
		if (referredObj != null) {
			referredObjCompScopes = referredObj.getComponentScopeCodes();
		}

		Set<? extends IXLibraryComponentScope> applCavGroupScopes = Collections.emptySet();
		IXLibraryCavityGroup cavGroup = houseDef.getAssociatedCavityGroup();
		if (cavGroup != null) {
			applCavGroupScopes = cavGroup.getComponentScopeCodes();
		}

		if (houseScopes.isEmpty() && referredObjCompScopes.isEmpty() && applCavGroupScopes.isEmpty()) {
			return true;
		}
		Collection<? extends IXLibraryScopeCode> designScopes = null;
		if (scopingContext.getDesign() != null) {
			designScopes = scopingContext.getDesign().getScopeScodes();
			if (designScopes == null && scopingContext.getProject() != null) {
				designScopes = scopingContext.getProject().getScopeScodes();
			}
		}
		if (designScopes == null) {
			return false;
		}
		else if (designScopes.isEmpty()) {
			return false;
		}

		for (IXLibraryScope houseScope : houseScopes) {
			if (!LibraryAndDesignDrivenScopeValidator
					.isPartScopeValid(houseScope.getScope(), designScopes, houseScope.getAttribute(
							IXAttributes.ScopeType))) {
				return false;
			}
		}
		for (IXLibraryComponentScope referredObjCompScope : referredObjCompScopes) {
			if (!LibraryAndDesignDrivenScopeValidator
					.isPartScopeValid(referredObjCompScope.getScope(), designScopes, referredObjCompScope.getAttribute(
							IXAttributes.ScopeType))) {
				return false;
			}
		}
		for (IXLibraryComponentScope cavGrpSCope : applCavGroupScopes) {
			if (!LibraryAndDesignDrivenScopeValidator
					.isPartScopeValid(cavGrpSCope.getScope(), designScopes, cavGrpSCope.getAttribute(
							IXAttributes.ScopeType))) {
				return false;
			}
		}
		return true;
	}

	private boolean isDesignSatisified(IXLibraryHousingDefinition houseDef, IXComponentSelectionContext scopingContext)
	{
		Collection<? extends IXLibraryScopeCode> designScopes = null;
		if (scopingContext.getDesign() != null) {
			designScopes = scopingContext.getDesign().getScopeScodes();
			if (designScopes == null && scopingContext.getProject() != null) {
				designScopes = scopingContext.getProject().getScopeScodes();
			}
		}
		if (designScopes == null) {
			return true;
		}
		else if (designScopes.isEmpty()) {
			return true;
		}

		Set<IXLibraryScope> houseScopes;
		if (houseDef != null) {
			houseScopes = houseDef.getScopes();
		}
		else {
			return true;
		}

		Set<? extends IXLibraryComponentScope> referredObjCompScopes = Collections.emptySet();
		IXLibraryObject referredObj = houseDef.getSubComponent();
		if (referredObj != null) {
			referredObjCompScopes = referredObj.getComponentScopeCodes();
		}

		Set<? extends IXLibraryComponentScope> applCavGroupScopes = Collections.emptySet();
		IXLibraryCavityGroup cavGroup = houseDef.getAssociatedCavityGroup();
		if (cavGroup != null) {
			applCavGroupScopes = cavGroup.getComponentScopeCodes();
		}

		if (houseScopes.isEmpty() && referredObjCompScopes.isEmpty() &&
				applCavGroupScopes.isEmpty()) { //&& applCavGroupScopes.isEmpty()) {
			return false;
		}
		if (!houseScopes.isEmpty()) {
			for (IXLibraryScopeCode designScope : designScopes) {
				if (!LibraryAndDesignDrivenScopeValidator.isDesignScopeValidwithHousing(designScope, houseScopes)) {
					return false;
				}
			}
		}
		if (!referredObjCompScopes.isEmpty()) {
			for (IXLibraryScopeCode designScope : designScopes) {
				if (!LibraryAndDesignDrivenScopeValidator
						.isDesignScopeValidwithPart(designScope, referredObjCompScopes)) {
					return false;
				}
			}
		}
		if (!applCavGroupScopes.isEmpty()) {
			for (IXLibraryScopeCode designScope : designScopes) {
				if (!LibraryAndDesignDrivenScopeValidator.isDesignScopeValidwithPart(designScope, applCavGroupScopes)) {
					return false;
				}
			}
		}
		return true;
	}
}
