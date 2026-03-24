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

import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.query.IXQueryExpressionFactory;
import com.mentor.chs.api.query.IXResultExpression;
import com.mentor.chs.api.report.IXReportContext;
import com.mentor.chs.api.report.IXReportFactory;
import com.mentor.chs.api.report.IXReportModule;
import com.mentor.chs.api.report.IXReportTemplate;

import java.text.ParseException;

/**
 * This is a utility class for Report and DataExchange examples
 */
public class ReportExampleUtils
{

	private static String startQueryExpression = "<queryExpression>";
	private static String endQueryExpression = "</queryExpression>";

	/**
	 * Creates the Report Module
	 *
	 * @param context report Context
	 * @param moduleName Module name - This is optional and can be empty string
	 *
	 * @return created report module
	 */
	public static IXReportModule createReportModule(IXReportContext context, String moduleName)
	{
		return context.getReportFactory().createReportModule(moduleName);
	}

	/**
	 * Creates the Wire report template
	 *
	 * @param rf Report Factory
	 *
	 * @return created report template for Wire
	 */
	public static IXReportTemplate createWireReportTemplate(IXReportFactory rf)
	{
		return rf.createReportTemplate(new WireListResultExpression(), "Name");
	}

	/**
	 * Creates the Connector report template
	 *
	 * @param rf Report Factory
	 *
	 * @return created report template for Connector
	 */
	public static IXReportTemplate createConnectorReportTemplate(IXReportFactory rf)
	{
		return rf.createReportTemplate(new ConnectorListResultExpression(), "Name");
	}

	/**
	 * Wires List result expression used for getting all the names of the wires. This example class only outputs the name
	 * of the wires
	 */
	private static class WireListResultExpression implements IXResultExpression
	{

		@Override public String getName()
		{
			return "Wire List Result Expression";
		}

		@Override public Object evaluate(IXObject entity)
		{
			return evaluate(entity, "");
		}

		@Override public Object evaluate(IXObject entity, String executionContext)
		{
			if (entity instanceof IXWire) {
				return entity.getAttribute("Name");
			}
			return "";
		}
	}

	/**
	 * Connector List result expression used for getting all the names of the connectors. This example class only outputs
	 * the name of the connectors
	 */
	private static class ConnectorListResultExpression implements IXResultExpression
	{

		@Override public String getName()
		{
			return "Connector List Result Expression";
		}

		@Override public Object evaluate(IXObject entity)
		{
			return evaluate(entity, "");
		}

		@Override public Object evaluate(IXObject entity, String executionContext)
		{
			if (entity instanceof IXConnector) {
				return entity.getAttribute("Name");
			}
			return "";
		}
	}

	/**
	 * Adds the PartNumber report template to the passed on report module
	 *
	 * @param rf Report factory
	 * @param qef Query Expression factory
	 * @param module Module to which partnunber template needs to be added
	 *
	 * @throws ParseException if there are any parse exception
	 */
	public static void addPartNumberReportColumnToModule(IXReportFactory rf, IXQueryExpressionFactory qef,
			IXReportModule module) throws ParseException
	{
		final String partNumberAttrName = "PartNumber";
		final IXResultExpression pnResultExpression =
				qef.parseResultExpression(createQueryExpression(partNumberAttrName).toString());
		IXReportTemplate nestedReportcolumn = rf.createReportTemplate(new IXResultExpression()
		{
			@Override public String getName()
			{
				return "PartNumber ResultExpression";
			}

			public Object evaluate(IXObject entity)
			{
				return evaluate(entity, "");
			}

			public Object evaluate(IXObject entity, String executionContext)
			{
				return pnResultExpression.evaluate(entity, executionContext);
			}
		}, partNumberAttrName);
		module.addReportTemplate(nestedReportcolumn);
	}

	private static StringBuilder createQueryExpression(String attribute)
	{
		StringBuilder expression = new StringBuilder(startQueryExpression);
		expression.append("<attributeVariable name=\"");
		expression.append(attribute);
		expression.append("\"/>");
		expression.append(endQueryExpression);
		return expression;
	}
}
