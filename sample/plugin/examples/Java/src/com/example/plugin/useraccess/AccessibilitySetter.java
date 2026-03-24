/**
 * Copyright 2015 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.useraccess;

import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.plugin.useraccess.IXUserAccessibilityResult;
import com.mentor.chs.plugin.useraccess.UserAccessibility;

import java.util.Collection;
import java.util.Set;

public class AccessibilitySetter
{

	public void setAccess(
			Collection<IXUserAccessibilityResult<? extends IXObject>> results)
	{
		if (results.isEmpty()) {
			return;
		}
		for (IXUserAccessibilityResult<? extends IXObject> access : results) {
			Set<IXValue> props = access.getObject().getProperties();
			String value = null;
			for (IXValue prop : props) {
				if ("UserAccess".equalsIgnoreCase(prop.getName())) {
					value = prop.getValue();
				}
			}
			if (value == null) {
				access.setAccessibility(UserAccessibility.WRITE);
				continue;
			}

			if ("Write".equalsIgnoreCase(value)) {
				access.setAccessibility(UserAccessibility.WRITE);
			}
			else if ("Read".equalsIgnoreCase(value)) {
				access.setAccessibility(UserAccessibility.READONLY);
                access.setMessage("Access is read only (defined by external system)");
			}
			else {
				access.setAccessibility(UserAccessibility.NOACCESS);
                access.setMessage("Access restricted by external system");
			}
		}
	}
}
