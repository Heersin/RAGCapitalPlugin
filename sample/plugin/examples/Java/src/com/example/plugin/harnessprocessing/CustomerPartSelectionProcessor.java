/**
 * Copyright 2014 Mentor Graphics Corporation. All Rights Reserved.
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
package com.example.plugin.harnessprocessing;

import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXInsulation;
import com.mentor.chs.api.IXLibraryCustomerPartNumber;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.IXDesignLogSeverityEnum;
import com.mentor.chs.plugin.harness.IXHarnessEngineering;
import com.mentor.chs.plugin.harness.IXHarnessEngineeringTypeEnum;
import com.mentor.chs.plugin.harness.IXHarnessProcessingOutput;

import java.util.Iterator;

public class CustomerPartSelectionProcessor extends AbstractHarnessProcessor
		implements IXHarnessEngineering, IXApplicationContextListener
{

	private IXApplicationContext context;

	public CustomerPartSelectionProcessor ()
	{
		super("Example customer part selection harness processor",
				"1.0",
				"Sample customer part selection harness processor implementation");
	}

	@Override
	public IXHarnessEngineeringTypeEnum getType()
	{
		return IXHarnessEngineeringTypeEnum.SelectCustPartNo;
	}

	@Override
	public boolean process(IXHarnessDesign design, IXHarnessProcessingOutput output)
	{
		for (IXConnector connector : design.getConnectivity().getConnectors()) {
			updateCustomerPartNumbers(output, connector);
		}

		for (IXWire wire : design.getConnectivity().getWires()) {
			updateCustomerPartNumbers(output, wire);
		}

		for (IXInsulation insulation : getAllInsulations(design)) {
			updateCustomerPartNumbers(output, insulation);
		}

		return true;
	}

	private void updateCustomerPartNumbers(IXHarnessProcessingOutput output, IXWriteableObject object)
	{
		String cust = object.getAttribute(IXAttributes.CustomerPartNumber);

		if (cust == null) {
			output.log(IXDesignLogSeverityEnum.Information, "Invalid Attribute");
			return;
		}

		if (!cust.isEmpty()) {
			return;
		}

		String ptn = object.getAttribute(IXAttributes.PartNumber);
		IXAttributeSetter setter = object.getAttributeSetter();

		// Assign the first customer part number we find for this part
		IXLibraryObject libraryObject = context.getLibrary().getLibraryObject(ptn);
		Iterator<IXLibraryCustomerPartNumber> iterator = libraryObject.getCustomerPartNumbers().iterator();
		if (iterator.hasNext()) {
			IXLibraryCustomerPartNumber customerPartNumber = iterator.next();

			setter.addAttribute(IXAttributes.CustomerPartNumber,
					customerPartNumber.getAttribute(IXAttributes.CustomerPartNumber));

			output.log(IXDesignLogSeverityEnum.Information,
					customerPartNumber.getAttribute(IXAttributes.CustomerPartNumber) + " Selected for " + ptn);
		}
		else {
			output.log(IXDesignLogSeverityEnum.Information,
					"No Customer Part Numbers Found for " + ptn);
		}
	}

	@Override
	public void setApplicationContext(IXApplicationContext applicationContext)
	{
		context = applicationContext;
	}
}
