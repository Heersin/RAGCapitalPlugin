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

public class DoNotPlaceBySlotName extends BaseConstraint implements IXPlacementConstraint {

	public DoNotPlaceBySlotName() 
	{
		super("Do Not Place Device by Slot Name",
			  "1.0",
			  "Basic constraint to forbid placement of devices into slots based on matching names");
	}
	
	public boolean match(IXDevice device, IXSlot slot, IXDoDontResult result) {
		result.setValue(IXDoDontResult.DoDont.DONT);
		final String slotName = slot.getAttribute(ATTRIBUTE_NAME);
		
		if(slotName != null && slotName.equalsIgnoreCase(device.getAttribute(ATTRIBUTE_NAME))) {
				return true;
		}
		return false;
	}

}
