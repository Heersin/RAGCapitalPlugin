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
import com.mentor.chs.api.IXLibraryComponentScope;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibraryScopeCode;
import com.mentor.chs.plugin.IXComponentSelectionContext;
import com.mentor.chs.plugin.filter.IXComponentScopeSelectionFilter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class LibraryAndDesginDrivenComponentScopeFilter implements IXComponentScopeSelectionFilter
{

	private boolean isLibrarySatisified(IXLibraryObject libObj, IXComponentSelectionContext scopingContext)
	{
		Set<? extends IXLibraryComponentScope> partScopes = libObj.getComponentScopeCodes();
		if (partScopes == null) {
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
		for (IXLibraryComponentScope partScope : partScopes) {
			if (!LibraryAndDesignDrivenScopeValidator.isPartScopeValid(partScope.getScope(), designScopes,
					partScope.getAttribute(IXAttributes.ScopeType))) {
				return false;
			}
		}
		return true;
	}

	private boolean isDesignSatisified(IXLibraryObject libObj, IXComponentSelectionContext scopingContext)
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

		Set<? extends IXLibraryComponentScope> partScopes = libObj.getComponentScopeCodes();
		if (partScopes == null) {
			return false;
		}
		for (IXLibraryScopeCode designScope : designScopes) {
			if (!LibraryAndDesignDrivenScopeValidator.isDesignScopeValidwithPart(designScope, partScopes)) {
				return false;
			}
		}
		return true;
	}

	public Collection<IXLibraryObject> apply(Collection objs, IXComponentSelectionContext scopingContext)
	{
		Collection<IXLibraryObject> retObjs = new HashSet<IXLibraryObject>();
		if (scopingContext != null) {
			for (Object obj : objs) {
				if (isLibrarySatisified((IXLibraryObject) obj, scopingContext) &&
						isDesignSatisified((IXLibraryObject) obj, scopingContext)) {
					retObjs.add((IXLibraryObject) obj);
				}
			}
		}
		return retObjs;
	}

	public String getDescription()
	{
		return "Computes the scope Applicability of part for the design";
	}

	public String getName()
	{
		return "Library & Design Driven Soping Plugin";
	}

	public String getVersion()
	{
		return "1.0";
	}
}

