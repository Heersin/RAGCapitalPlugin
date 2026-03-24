/*
 * Copyright 2013 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */

package com.example.plugin.diagrampartitioning.ui;

import com.example.plugin.diagrampartitioning.ColumnGroup;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GroupableTableHeader extends JTableHeader
{

	private List columnGroups = null;
	private GroupableTableHeaderUI m_tableHeaderUI = new GroupableTableHeaderUI();
	private static final String uiClassID = "GroupableTableHeaderUI";

	public GroupableTableHeader(TableColumnModel model)
	{
		super(model);
		setUI(m_tableHeaderUI);
		setColumnModel(model);
		setReorderingAllowed(false);
	}

	public void setReorderingAllowed(boolean b)
	{
		reorderingAllowed = false;
	}

	public void addColumnGroup(ColumnGroup g)
	{
		if (columnGroups == null) {
			columnGroups = new ArrayList();
		}
		columnGroups.add(g);
	}

	public Iterator getColumnGroups(TableColumn col)
	{
		if (columnGroups == null) {
			return null;
		}
		Iterator iter = columnGroups.iterator();
		while (iter.hasNext()) {
			ColumnGroup cGroup = (ColumnGroup) iter.next();
			List v_ret = cGroup.getColumnGroups(col, new ArrayList());
			if (v_ret != null) {
				return v_ret.iterator();
			}
		}
		return null;
	}

	public void removeAllColumnGroups()
	{
		columnGroups = null;
	}

	public void setColumnMargin()
	{
		if (columnGroups == null) {
			return;
		}
		int columnMargin = getColumnModel().getColumnMargin();
		Iterator iter = columnGroups.iterator();
		while (iter.hasNext()) {
			ColumnGroup cGroup = (ColumnGroup) iter.next();
			cGroup.setColumnMargin(columnMargin);
		}
	}

	public String getUIClassID()
	{
		return uiClassID;
	}

	public void updateUI()
	{
		setUI(m_tableHeaderUI);
		resizeAndRepaint();
		invalidate();//PENDING
	}
}


