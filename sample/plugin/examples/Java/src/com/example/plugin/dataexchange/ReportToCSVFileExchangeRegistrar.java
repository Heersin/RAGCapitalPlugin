/**
 * Copyright 2013-2014 Mentor Graphics Corporation. All Rights Reserved.
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
package com.example.plugin.dataexchange;

import com.mentor.chs.api.dataexchange.IXDataExchangeRegistrar;
import com.mentor.chs.api.dataexchange.IXDataExchangeRegistrarResult;

/**
 * This is an example implementation for IXDataExchangeRegistrar.This example demonstrates how the IXDataExchange and
 * the IXReportCreator can be registered.
 * <p/>
 * Capital will use the registered classes and plugins to automatically create new actions in the tool.
 * <p/>
 * Also these registered classed will be invoked for generating the report and exchanging the report content.
 */
public class ReportToCSVFileExchangeRegistrar implements IXDataExchangeRegistrar
{

	@Override public void register(IXDataExchangeRegistrarResult result)
	{
		if (result != null) {
			// add the WireList Report and the CSV File Exchanger
			result.add("WireList CSV Export", ReportToCSVFileExchange.class, SimpleWireListReport.class);
			// add the WireList Report and the CSV File Exchanger
			result.add("Connector CSV Export", ReportToCSVFileExchange.class, SimpleConnectorListReport.class);
		}
	}

	@Override public String getDescription()
	{
		return "Sample Registrar for exporting report data to CSV file";
	}

	@Override public String getName()
	{
		return "CSV File Exchange";
	}

	@Override public String getVersion()
	{
		return "1.0";
	}
}
