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

package com.example.plugin.drc;

import java.util.Set;

import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.drc.IXDRCViolationReporter;
import com.mentor.chs.plugin.drc.IXIntegratorDRCheck;

public class InlineConnectorSignalCheck extends BaseDRCheck implements IXIntegratorDRCheck 
{

	/**
	 * 
	 */
	public InlineConnectorSignalCheck() {
		super(
				"Inline Connector Signal", 
				"1.0", 
				"Inline Connector Signal Check", 
				true, 
				Severity.Error);
	}
	
	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#begin(com.mentor.chs.plugin.drc.IXDRCViolationReporter)
	 */
	public void begin(IXDRCViolationReporter arg0) {
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#check(com.mentor.chs.plugin.drc.IXDRCViolationReporter, com.mentor.chs.api.IXObject)
	 */
	public void check(IXDRCViolationReporter vReporter, IXObject xObject) {

		if(xObject instanceof IXConnector) {
			
			final IXConnector xConnector = (IXConnector) xObject;

			final Set<IXConnector> matedConnectors = xConnector.getMatedConnectors();

		}

	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#end(com.mentor.chs.plugin.drc.IXDRCViolationReporter)
	 */
	public void end(IXDRCViolationReporter arg0) {
	}



}
