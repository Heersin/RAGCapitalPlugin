/**
 * Copyright 2008 Mentor Graphics Corporation. All Rights Reserved.
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
package com.example.plugin.reporter.processors;

import com.example.plugin.BasePlugin;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXDevicePin;
import com.mentor.chs.plugin.reporter.IXReportProcessorPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomReportProcessor extends BasePlugin implements IXReportProcessorPlugin
{


	public CustomReportProcessor()
	{
		super("Connectivity skipping Fuses", "1","Gets connectivity between pinlists jumping over fuses");
	}

	// This example shows how a plugin can customize Connectivity between PinLists report processor (which a standard report processor (IXReportProcessor)
	// available to JSP pages. This plugin instructs the processor that reaches a pin while navigating connectivity
	// to proceed to other pins of the pinlist if the name of the pinlist contains the string "FUSE".
	public List<IXAbstractPin> getConnectedPin(IXAbstractPin pin) {

		if( pin instanceof IXDevicePin) {
			if( pin.getOwner().getAttribute("Name").contains("FUSE")) {
				List<IXAbstractPin> list = new ArrayList<IXAbstractPin>(pin.getOwner().getPins());
				list.remove(pin);
				return list;
			}
		}
		return Collections.emptyList();
	}
}
