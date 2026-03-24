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

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GroupableTableHeaderUI extends BasicTableHeaderUI
{

	public void paint(Graphics g, JComponent c)
	{
		// DR 633941: Remove components added by previous repaints to avoid a memory leak.
		rendererPane.removeAll();

		Rectangle clipBounds = g.getClipBounds();
		if (header.getColumnModel() == null) {
			return;
		}
		((GroupableTableHeader) header).setColumnMargin();
		int column = 0;
		Dimension size = header.getSize();
		Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
		Map h = new HashMap();
//		int columnMargin = header.getColumnModel().getColumnMargin();

		Enumeration enumeration = header.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			cellRect.height = size.height;
			cellRect.y = 0;
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			Iterator cGroups = ((GroupableTableHeader) header).getColumnGroups(aColumn);
			if (cGroups != null) {
				int groupHeight = 0;
				while (cGroups.hasNext()) {
					ColumnGroup cGroup = (ColumnGroup) cGroups.next();
					Rectangle groupRect = (Rectangle) h.get(cGroup);
					if (groupRect == null) {
						groupRect = new Rectangle(cellRect);
						Dimension d = cGroup.getSize(header.getTable());
						groupRect.width = d.width;
						groupRect.height = d.height;
						h.put(cGroup, groupRect);
					}
					paintCell(g, groupRect, cGroup);
					groupHeight += groupRect.height;
					cellRect.height = size.height - groupHeight;
					cellRect.y = groupHeight;
				}
			}
			cellRect.width = aColumn.getWidth();// + columnMargin;
			if (cellRect.intersects(clipBounds)) {
				paintCell(g, cellRect, column);
			}
			cellRect.x += cellRect.width;
			column++;
		}
	}

	private void paintCell(Graphics g, Rectangle cellRect, int columnIndex)
	{
		// This is to be the renderer for the individual COLUMN headers, not the group header
		TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
		TableCellRenderer renderer = aColumn.getHeaderRenderer();
		if (renderer == null) {
			renderer = new GroupableTableHeaderCellRenderer();
		}

		Component component = renderer.getTableCellRendererComponent(
				header.getTable(), aColumn.getHeaderValue(), false, false, -1, columnIndex);

//		component.setFont(UIManager.getFont("Label.font"));

		rendererPane.add(component);
		rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
				cellRect.width, cellRect.height, true);
	}

	private void paintCell(Graphics g, Rectangle cellRect, ColumnGroup cGroup)
	{
		// This is the renderer for the GROUP header, not the individual columns
		TableCellRenderer renderer = cGroup.getHeaderRenderer();
		if (renderer == null) {
			renderer = header.getDefaultRenderer();
		}

		Component component = renderer.getTableCellRendererComponent(
				header.getTable(), cGroup.getHeaderValue(), false, false, -1, -1);

//		component.setFont(UIManager.getFont("Label.font"));

		rendererPane.add(component);
		rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y,
				cellRect.width, cellRect.height, true);
	}

	private int getHeaderHeight()
	{
		int height = 0;
		TableColumnModel columnModel = header.getColumnModel();
		for (int column = 0; column < columnModel.getColumnCount(); column++) {
			TableColumn aColumn = columnModel.getColumn(column);

			TableCellRenderer renderer = aColumn.getHeaderRenderer();
			if (renderer == null) {
				renderer = header.getDefaultRenderer();
			}

			Component comp = renderer.getTableCellRendererComponent(
					header.getTable(), aColumn.getHeaderValue(), false, false, -1, column);
			int cHeight = comp.getPreferredSize().height;
			Iterator enumerator = ((GroupableTableHeader) header).getColumnGroups(aColumn);
			if (enumerator != null) {
				while (enumerator.hasNext()) {
					ColumnGroup cGroup = (ColumnGroup) enumerator.next();
					cHeight += cGroup.getSize(header.getTable()).height;
				}
			}
			height = Math.max(height, cHeight);
		}
		return height;
	}

	private Dimension createHeaderSize(long width)
	{
		TableColumnModel columnModel = header.getColumnModel();
		width += columnModel.getColumnMargin() * columnModel.getColumnCount();  // simons - experiment with width
		if (width > Integer.MAX_VALUE) {
			width = Integer.MAX_VALUE;
		}
		return new Dimension((int) width, getHeaderHeight());
	}

	public Dimension getPreferredSize(JComponent c)
	{
		long width = 0;
		Enumeration enumeration = header.getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			width = width + aColumn.getPreferredWidth();
		}
		return createHeaderSize(width);
	}

	protected MouseInputListener createMouseInputListener()
	{
		return new MouseInputHandler((GroupableTableHeader) header);
	}

	public class MouseInputHandler extends BasicTableHeaderUI.MouseInputHandler
	{

		//	private Component dispatchComponent;
		protected GroupableTableHeader header;

		public MouseInputHandler(GroupableTableHeader header)
		{
			this.header = header;
		}

//		private void setDispatchComponent(MouseEvent e) {
//
//			Component editorComponent = header.getEditorComponent();
//			Point p = e.getPoint();
//			Point p2 = SwingUtilities.convertPoint(header, p, editorComponent);
//			dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent,
//					p2.x, p2.y);
//		}

//		private boolean repostEvent(MouseEvent e) {
//			if (dispatchComponent == null) {
//				return false;
//			}
//			MouseEvent e2 = SwingUtilities.convertMouseEvent(header, e, dispatchComponent);
//			dispatchComponent.dispatchEvent(e2);
//			return true;
//		}

		public void mousePressed(MouseEvent e)
		{
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}
			super.mousePressed(e);

//			if (header.getResizingColumn() == null) {
//				Point p = e.getPoint();
//				TableColumnModel columnModel = header.getColumnModel();
//				int index = columnModel.getColumnIndexAtX(p.x);
//				TableColumn col = columnModel.getColumn(index);
//				TableCellRenderer renderer = col.getHeaderRenderer();
//				m_eventHandler.mousePressed(e, renderer);
//			}
		}

		public void mouseReleased(MouseEvent e)
		{
			super.mouseReleased(e);
		}
	}
}

