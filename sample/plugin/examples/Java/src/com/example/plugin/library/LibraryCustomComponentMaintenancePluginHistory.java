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

package com.example.plugin.library;


import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.plugin.library.IXLibraryComponentMaintenanceModifierPlugin;
import com.mentor.chs.plugin.modifier.IXModifier;

/**
 * This Custom Automated Plugin filter  provides an example for providing automated modification history during edit process of library.
 */
public class LibraryCustomComponentMaintenancePluginHistory implements IXLibraryComponentMaintenanceModifierPlugin<IXLibraryObject>
{

	@Override public IXModifier[] getModifiers()
	{
		return new IXModifier[]{new ComponentMaintenanceDeviceModificationHistoryProvider(),new ComponentMaintenanceConnectorModificationHistoryProvider()};
	}

	@Override public String getDescription()
	{
		return "Automated Modification Note for Component Maintenance";
	}

	@Override public String getName()
	{
		return "LibraryCustomComponentMaintenancePluginHistory";
	}

	@Override public String getVersion()
	{
		return "1.0";
	}
}