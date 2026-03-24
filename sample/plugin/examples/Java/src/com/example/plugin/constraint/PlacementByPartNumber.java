/**
 * Copyright 2006 Mentor Graphics Corporation. All Rights Reserved.
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

import com.mentor.chs.api.IXDevice;
import com.mentor.chs.api.IXSlot;
import com.mentor.chs.plugin.constraint.IXDoDontResult;
import com.mentor.chs.plugin.constraint.IXPlacementConstraint;
import java.util.*;

public class PlacementByPartNumber extends BaseConstraint implements IXPlacementConstraint {

	Map<String, String> partsMap = null;
	
	public PlacementByPartNumber() {
		super("Placement By Part Number", 
			  "1.0",
			  "Do placement rule based on device part number matching slot name.");
		init();
	}
	
	public void init() {
		// construct map of parts to slot locations
		partsMap = new HashMap<String, String>();
		
		partsMap.put("123", "DEV1");
		partsMap.put("456", "DEV2");
		partsMap.put("789", "DEV3");
	}
	
	public boolean match(IXDevice device, IXSlot slot, IXDoDontResult result) {
		final String deviceNum = device.getAttribute(ATTRIBUTE_PARTNUMBER);
		final String slotName = partsMap.get(deviceNum);

		if (slotName == null) 
		{
			return false;
		}
		return slotName.equalsIgnoreCase(slot.getAttribute(ATTRIBUTE_NAME));
	}
}
