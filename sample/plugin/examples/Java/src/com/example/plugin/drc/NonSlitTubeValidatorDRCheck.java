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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.mentor.chs.api.IXInsulation;
import com.mentor.chs.api.IXInsulationRun;
import com.mentor.chs.api.IXNode;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXStructureNode;
import com.mentor.chs.plugin.drc.IXDRCViolationReporter;
import com.mentor.chs.plugin.drc.IXHarnessDRCheck;
import com.mentor.chs.plugin.drc.IXDRCheck.Severity;

/**
 * Design rule check to ensure that non slit tube insulations do not have a junction node.
 * <p>
 */
public class NonSlitTubeValidatorDRCheck extends BaseDRCheck implements IXHarnessDRCheck
{

	/**
	 *
	 */
	public NonSlitTubeValidatorDRCheck() {
		super(
				"Non Slit Tube Validator",
				"1.0",
				"Ensure that all non slit tube insulations have no junction node.",
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

		if(xObject instanceof IXInsulationRun) {

			IXInsulationRun xInsulationRun = (IXInsulationRun) xObject;

		    vReporter.report(Severity.Information, "____DEBUG: found Insulation Run <b>" + "{0}"+"</b>",xInsulationRun);

			for(IXInsulation xInsulation:xInsulationRun.getInsulations()){

				vReporter.report(Severity.Information, "____DEBUG: found Insulation <b>" + "{0}"+"</b>",xInsulation);

				//Check if this insulation is a tube before proceeding.
				String insulationType = xInsulation.getAttribute("InsulationType");

				if(insulationType!=null && insulationType.toLowerCase().indexOf("tube")>0)  {

					String slit = xInsulation.getAttribute(ATTRIBUTE_SLIT);

					//For all insulations that are don't have a slit.
					if(slit!=null && slit.equalsIgnoreCase("False")){

						//Get the nodes of the insulation run
						Set<IXNode> nodes = xInsulation.getInsulationRun().getNodes();
						for(IXNode node: nodes){
							//If there are junction node in a non slit tube, it is a failure of drc.
						 	if(node instanceof IXStructureNode){
								//If a structure node has three or more bundles, it is a junction node.
								if(node.getBundles().size()>2)
								{
						 			vReporter.report(Severity.Error, "Found a non slit tube {0} that has a junction node {1}.", xInsulation,node);
								}
						 	}
						}
			   		}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#end(com.mentor.chs.plugin.drc.IXDRCViolationReporter)
	 */
	public void end(IXDRCViolationReporter arg0) {
	}
}
