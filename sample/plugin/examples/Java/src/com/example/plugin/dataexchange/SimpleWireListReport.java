/**
 * Copyright 2013-2014 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example plugin.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, exceNpt as expressly
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

import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.query.IXFilterExpression;
import com.mentor.chs.api.query.IXQuery;
import com.mentor.chs.api.query.IXQueryFactory;
import com.mentor.chs.api.report.IXReport;
import com.mentor.chs.api.report.IXReportContext;
import com.mentor.chs.api.report.IXReportCreator;
import com.mentor.chs.api.report.IXReportFactory;
import com.mentor.chs.api.report.IXReportModule;
import com.mentor.chs.api.report.IXReportTemplate;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXAvailability;

import java.text.ParseException;

/**
 * An example of wire name list report. This example demonstrates how the report containing all the wire names can be
 * generated.
 */
public class SimpleWireListReport implements IXReportCreator, IXAvailability
{

	@Override public String getDescription()
	{
		return "Sample plugin for wire list export to csv file";
	}

	@Override public String getName()
	{
		return "SimpleWireListReport";
	}

	@Override public String getVersion()
	{
		return "1.0";
	}

	@Override public IXReport createReport(IXReportContext context)
	{
		// get the Report Factory
		IXReportFactory rf = context.getReportFactory();
		// get the Query Factory
		IXQueryFactory qf = context.getQueryFactory();
		// create the Report
		IXReport report = rf.createReport(getName());
		// create the report module for wire
		IXReportModule module = ReportExampleUtils.createReportModule(context, "WireList");
		// create the query for wires
		IXFilterExpression filterExp = null;
		IXQuery wireQuery = null;
		try {
			wireQuery = qf.createQuery(IXWire.class, "Wire", filterExp);
		}
		catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		// add the wire query to the report module
		if (wireQuery != null) {
			module.addQuery(wireQuery, "WireQuery");
		}
		// create the report template for wires and add it to the report module
		IXReportTemplate template = ReportExampleUtils.createWireReportTemplate(rf);
		module.addReportTemplate(template);
		try {
			ReportExampleUtils.addPartNumberReportColumnToModule(rf, context.getQueryExpressionFactory(), module);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		report.addModule(module);
		return report;
	}

	@Override public boolean isAvailable(IXApplicationContext context)
	{
		// An example how to restrict the report to only specific design type.
		// In this case it is restricted to Logic Designs and hence will not be applicable to other designs
		// Capital will not show this report option for other design types.
		IXDesign selectedDesign = context.getCurrentDesign();
		return selectedDesign instanceof IXLogicDesign;
	}
}
