/**
 * Copyright 2011 Mentor Graphics Corporation. All Rights Reserved.
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

import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.drc.IXDRCViolationReporter;
import com.mentor.chs.plugin.drc.IXDRCheckAdvancedConfiguration;
import com.mentor.chs.plugin.drc.IXSBOMDRCheck;

/**
 * DRC check to ...
 * <p/>
 */
public class SBOMExampleDRCheck extends BaseDRCheck
		implements IXSBOMDRCheck,
		IXApplicationContextListener, IXDRCheckAdvancedConfiguration
{

	protected IXApplicationContext applicationContext;

	/**
	 *
	 */
	public SBOMExampleDRCheck()
	{
		super("SBOM Example Design Rule Check",
				"1.1",
				"...",
				true,
				Severity.Error);
	}

	public void setApplicationContext(IXApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	public void begin(IXDRCViolationReporter reporter)
	{
	}

	public void check(IXDRCViolationReporter reporter, IXObject object)
	{
		String objName = getObjectName(object);
		applicationContext.getOutputWindow()
				.println("____DEBUG: found sub <b>" + objName + "</b>. " + object);

		if (object instanceof IXSubAssembly) {
			if (objName!=null && !objName.startsWith("SUB")) {
				reporter.report(Severity.Error, "Name does not start with 'SUB': {0}", object);
			}
		}
	}

	public void end(IXDRCViolationReporter reporter)
	{
	}

	public boolean getAvailability(RunningMode runningMode, String designAbstraction)
	{
		return true;
	}

	public Severity getSeverity(RunningMode runningMode, String designAbstraction)
	{
		return Severity.Error;
	}
}