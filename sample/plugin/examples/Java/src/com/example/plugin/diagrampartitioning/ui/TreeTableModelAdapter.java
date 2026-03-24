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

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

public class TreeTableModelAdapter extends AbstractTableModel
{

	private JTree m_tree;
	private ITreeTableModel m_treeTableObjectModel;

	public TreeTableModelAdapter(ITreeTableModel treeTableObjectModel, JTree tree)
	{
		m_tree = tree;
		m_treeTableObjectModel = treeTableObjectModel;

		m_tree.addTreeExpansionListener(new TreeExpansionListener()
		{
			// Don't use fireTableRowsInserted() here;
			// the selection model would get updated twice.
			public void treeExpanded(TreeExpansionEvent event)
			{
				fireTableDataChanged();
			}

			public void treeCollapsed(TreeExpansionEvent event)
			{
				fireTableDataChanged();
			}
		});
		m_treeTableObjectModel.addTreeModelListener(new TreeModelListener()
		{
			public void treeNodesChanged(TreeModelEvent e)
			{
				int row = m_tree.getRowForPath(e.getTreePath());
				if (isValidRowToFireEvent(row)) {
					fireTableRowsUpdated(row, row);
				}
			}

			public void treeNodesInserted(TreeModelEvent e)
			{
				fireTableDataChanged();
			}

			public void treeNodesRemoved(TreeModelEvent e)
			{
				fireTableDataChanged();
			}

			public void treeStructureChanged(TreeModelEvent e)
			{
				fireTableDataChanged();
			}
		});
	}

	protected boolean isValidRowToFireEvent(int row)
	{
		return true;
	}

	// Wrappers, implementing TableModel interface.

	public int getColumnCount()
	{
		return m_treeTableObjectModel.getColumnCount();
	}

	public String getColumnName(int column)
	{
		return m_treeTableObjectModel.getColumnIdentifier(column).toString();
	}

	public Class getColumnClass(int column)
	{
		return m_treeTableObjectModel.getColumnClass(column);
	}

	public int getRowCount()
	{
		if (m_tree == null) {
			return 0;
		}
		return m_tree.getRowCount();
	}

	// not work if node collapse
	public Object nodeForRow(int row)
	{
		if (m_tree == null) {
			return null;
		}
		TreePath treePath = m_tree.getPathForRow(row);
		if (treePath == null) {
			throw new RuntimeException("Unexpected exception: can't get treepath for row " + row);
		}

		return treePath.getLastPathComponent();
	}

	public int rowForNode(Object node)
	{
		Object root = m_treeTableObjectModel.getRoot();
		int row = -1;
		for (int i = 0; i < m_treeTableObjectModel.getChildCount(root); i++) { // loop top level nodes
			row++;
			Object levelNode = m_treeTableObjectModel.getChild(root, i);
			if (node == levelNode) {
				return row;
			}
			for (int j = 0; j < m_treeTableObjectModel.getChildCount(levelNode); j++) {
				row++;
				if (node == m_treeTableObjectModel.getChild(levelNode, j)) {
					return row;
				}
			}
		}
		return -1;
	}

	public void fireAllTableCellUpdated()
	{
		Object root = m_treeTableObjectModel.getRoot();
		int row = -1;
		for (int i = 0; i < m_treeTableObjectModel.getChildCount(root); i++) { // loop top level nodes
			row++;
			fireTableCellUpdated(row, 1);
			Object levelNode = (Object) m_treeTableObjectModel.getChild(root, i);
			for (int j = 0; j < m_treeTableObjectModel.getChildCount(levelNode); j++) {
				row++;
				fireTableCellUpdated(row, 1);
			}
		}
	}

	public Object getValueAt(int row, int column)
	{
		return m_treeTableObjectModel.getValueAt(nodeForRow(row), column);
	}

	public boolean isCellEditable(int row, int column)
	{
		return m_treeTableObjectModel.isCellEditable(nodeForRow(row), column);
	}

	public void setValueAt(Object value, int row, int column)
	{
		if (row >= 0 && row < getRowCount()) {
			m_treeTableObjectModel.setValueAt(value, nodeForRow(row), column);
		}
	}

	public void cleanup()
	{
		m_tree = null;
		m_treeTableObjectModel = null;
	}
}
