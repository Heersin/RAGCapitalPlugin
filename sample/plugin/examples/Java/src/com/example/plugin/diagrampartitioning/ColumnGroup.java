/*
 * Copyright 2013 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */

package com.example.plugin.diagrampartitioning;

import com.example.plugin.diagrampartitioning.ui.GroupableTableHeaderCellRenderer;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ColumnGroup
{

	protected TableCellRenderer renderer;
	protected List<Object> groupElems;
	protected String text;
	protected int margin = 0;

	public ColumnGroup(String text)
	{
		this(null, text);
	}

	public ColumnGroup(TableCellRenderer renderer, String text)
	{
		if (renderer == null) {
			this.renderer = new GroupableTableHeaderCellRenderer();
		}
		else {
			this.renderer = renderer;
		}
		this.text = text;
		groupElems = new ArrayList<Object>();
	}

	/**
	 * @param obj TableColumn or ColumnGroup
	 */
	public void add(Object obj)
	{
		if (obj == null) {
			return;
		}
		groupElems.add(obj);
	}

	public List<Object> getColumnGroups(TableColumn c, List<Object> g)
	{
		g.add(this);
		if (groupElems.contains(c)) {
			return g;
		}
		for (Object groupElem : groupElems) {
			if (groupElem instanceof ColumnGroup) {
				List<Object> groups = ((ColumnGroup) groupElem).getColumnGroups(c, new ArrayList<Object>(g));
				if (groups != null) {
					return groups;
				}
			}
		}
		return null;
	}

	public TableCellRenderer getHeaderRenderer()
	{
		return renderer;
	}

	public void setHeaderRenderer(TableCellRenderer renderer)
	{
		if (renderer != null) {
			this.renderer = renderer;
		}
	}

	public Object getHeaderValue()
	{
		return text;
	}

	public Dimension getSize(JTable table)
	{
		Component comp = renderer.getTableCellRendererComponent(
				table, getHeaderValue(), false, false, -1, -1);
		Dimension preferredSize = comp.getPreferredSize();
		int width = 0;
		Iterator enumerator = groupElems.iterator();
		while (enumerator.hasNext()) {
			Object obj = enumerator.next();
			if (obj instanceof TableColumn) {
				TableColumn aColumn = (TableColumn) obj;
				width += aColumn.getWidth();
				//width += margin;
			}
			else {
				width += ((ColumnGroup) obj).getSize(table).width;
			}
		}
		//if(width < preferredSize.width)
		//	return preferredSize;
		return new Dimension(width, preferredSize.height);
	}

	public int getHeight(JTable table)
	{
		Component comp = renderer.getTableCellRendererComponent(table, getHeaderValue(), false, false, -1, -1);
		return comp.getPreferredSize().height;
	}

	public void setColumnMargin(int margin)
	{
		this.margin = margin;
		Iterator enumerator = groupElems.iterator();
		while (enumerator.hasNext()) {
			Object obj = enumerator.next();
			if (obj instanceof ColumnGroup) {
				((ColumnGroup) obj).setColumnMargin(margin);
			}
		}
	}
}
