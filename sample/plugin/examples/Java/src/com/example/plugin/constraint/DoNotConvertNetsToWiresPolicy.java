/**
 * Copyright 2009 Mentor Graphics Corporation. All Rights Reserved.
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

import com.mentor.chs.api.IXMulticore;
import com.mentor.chs.api.IXNet;
import com.mentor.chs.plugin.constraint.IXNetToWireConstraint;

public class DoNotConvertNetsToWiresPolicy extends BaseConstraint implements IXNetToWireConstraint
{

	public DoNotConvertNetsToWiresPolicy()
	{
		super("Do Not Convert Nets to Wires Policy",
			  "1.0",
			  "Example constraint for Nets to Wires conversion: exclude multicores with 2 children or less, "
			  + "exclude nets with 1 pin connection or none.");
	}

	public boolean match(IXMulticore multicore)
	{
		int nbChildren = multicore.getConductors().size() + multicore.getMulticores().size();
		if (nbChildren <= 2) {
			// exclude from conversion if 2 children or less
			return true;
		}
		return false;
	}

	public boolean match(IXNet net)
	{
		int nbPins = net.getAbstractPins().size();
		if (nbPins <= 1) {
			// exclude from conversion if 1 pin or less
			return true;
		}
		return false;
	}
}
