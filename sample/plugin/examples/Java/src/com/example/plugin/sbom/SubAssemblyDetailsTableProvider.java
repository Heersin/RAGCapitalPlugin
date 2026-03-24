/**
 * Copyright 2015 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely for internal purposes to serve as example
 * plugin. This code may not be used in a commercial distribution. Recipients may duplicate the
 * code provided that all notices are fully reproduced with and remain in the code. No part of this code may be
 * modified, reproduced, translated, used, distributed, disclosed or provided to third parties without the prior written
 * consent of Mentor Graphics, except as expressly authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND. MENTOR GRAPHICS OFFERS NO EXPRESS OR
 * IMPLIED WARRANTIES AND SPECIFICALLY DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS LICENSORS BE LIABLE FOR DIRECT, INDIRECT,
 * SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT OR
 * ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGES.
 * <p>
 */

package com.example.plugin.sbom;

import com.example.plugin.BasePlugin;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.sbom.IXSBOM;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.api.workbook.IXSubAssemblyWorkbookContext;
import com.mentor.chs.api.workbook.data.IXCellData;
import com.mentor.chs.api.workbook.data.IXTableData;
import com.mentor.chs.plugin.workbook.provider.IXWorkbookProvider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

/**
 * Example of a workbook table provider.
 */
public class SubAssemblyDetailsTableProvider extends BasePlugin implements IXWorkbookProvider<IXTableData>
{

	public SubAssemblyDetailsTableProvider()
	{
		super("Sub-Assembly Details Table Provider", "1.0",
				"A table provider that presents information about the sub-assembly being processed in a workbook page");
	}

	@Override public IXTableData generate(IXSubAssemblyWorkbookContext context, Map<String, String> paramsMap)
	{
		IXHarnessDesign design = context.getCurrentHarnessDesign();
		final Collection<? extends IXSubAssembly> currentSubAssemblies = context.getCurrentSubAssemblies();
		IXSubAssembly sub = currentSubAssemblies.size() == 1 ? currentSubAssemblies.iterator().next() : null;
		IXSBOM sbom = context.getCurrentSBOM();
		final int opIndex = context.getCurrentOperationIndex();
		String opName = context.getOperationNames().get(opIndex);

		IXTableData table = context.getProviderDataFactory().createTableData();
		table.setColumnCount(3);

		table.addRow(row(context, "Design:", design.getAttribute("Name"), "Date Generated:", now()));
		table.addRow(row(context, "SBOM:", sbom.getAttribute("Name"), "Author:", sbom.getAttribute("CreatedBy")));
		table.addRow(row(context, "Sub Assembly:", sub == null ? "" : sub.getAttribute("Name"), "Operation:", opName));

		return table;
	}

	private Collection<? extends IXCellData> row(IXSubAssemblyWorkbookContext context, String... values)
	{
		Collection<IXCellData> cells = new LinkedList<>();
		for (String value : values) {
			IXCellData cell = context.getProviderDataFactory().createTableCellData();
			cell.setColumnSpan(1);
			cell.setValue(value);
			cells.add(cell);
		}
		return cells;
	}

	private String now()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cal.getTime());
	}

	@Override public Class<IXTableData> getGenerateDataType()
	{
		return IXTableData.class;
	}
}
