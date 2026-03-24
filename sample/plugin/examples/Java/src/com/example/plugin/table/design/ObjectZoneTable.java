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

package com.example.plugin.table.design;

import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXZoneInfo;
import com.mentor.chs.plugin.table.IXLogicTable;
import com.mentor.chs.plugin.table.IXTableData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ObjectZoneTable implements IXLogicTable
{

	private IXLogicDesign design;
	private IXTableData m_xTableData;
	private String[][] data;

	public String getDescription()
	{
		return "Sample table plug-in for objects showing zone where it is located ";
	}

	public String getName()
	{
		return "Object Zone Table";
	}

	public String getVersion()
	{
		return "1.0";
	}

	public Class getTableContext()
	{
		return IXLogicDesign.class;
	}

	public void initialize(IXObject xObject)
	{
		try {
			if (xObject instanceof IXLogicDesign) {
				design = (IXLogicDesign) xObject;
			}
			m_xTableData = new ObjectZoneTable.XTableData();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IXTableData populate(IXObject xObject)
	{
		return m_xTableData;
	}

	private class RowInfo
	{

		public String column1;
		public String column2;
		public String column3;
		public String column4;
	}

	class XTableData implements IXTableData
	{

		XTableData()
		{
			List<IXObject> objs = new ArrayList<IXObject>();
			List<RowInfo> rows = new ArrayList<RowInfo>();
			objs.addAll(design.getConnectivity().getBlocks());
			objs.addAll(design.getConnectivity().getDevices());
			objs.addAll(design.getConnectivity().getSplices());
			objs.addAll(design.getConnectivity().getGrounds());
			objs.addAll(design.getConnectivity().getConnectors());
			objs.addAll(design.getConnectivity().getInterconnectDevices());

			for (IXObject obj : objs) {
				Set<IXDiagram> diags = design.getDiagrams();
				for (IXDiagram diag : diags) {
					Set<IXZoneInfo> zones = diag.getZoneInfos(obj);
					for (IXZoneInfo zone : zones) {
						if (zone != null) {
							RowInfo row = new RowInfo();
							row.column1 = obj.getAttribute("Name");
							row.column2 = obj.getAttribute("ShortDescription");
							row.column3 =
									new StringBuilder().append(zone.getRowName() == null ? "" : zone.getRowName()).
											append(zone.getColumnName() == null ? "" : zone.getColumnName()).toString();
							row.column4 = diag.getAttribute("Name");
							rows.add(row);
						}
					}
				}
			}
			data = new String[rows.size()][getColumnCount()];
			int i = 0;
			for (RowInfo row : rows) {
				data[i][0] = row.column1;
				data[i][1] = row.column2;
				data[i][2] = row.column3;
				data[i][3] = row.column4;
				i++;
			}
		}

		public int getColumnCount()
		{
			return 4;
		}

		public int getRowCount()
		{
			return data.length;
		}

		public int getHeaderCount()
		{
			return 1;
		}

		public Object getCellValueAt(int row, int column)
		{
			Object obj = "x";
			switch (column) {
				case 0:
				case 1:
				case 2:
				case 3:
					obj = data[row][column];
					break;
			}
			return obj;
		}

		public Object getHeaderValueAt(int rowIndex, int colIndex)
		{
			Object obj = null;
			switch (colIndex) {
				case 0:
					obj = "Object";
					break;
				case 1:
					obj = "Short Description";
					break;
				case 2:
					obj = "Zone";
					break;
				case 3:
					obj = "Diagram";
					break;
			}
			return obj;
		}
	}
}




