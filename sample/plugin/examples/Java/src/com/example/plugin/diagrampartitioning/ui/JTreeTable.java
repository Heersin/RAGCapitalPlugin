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

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;

public class JTreeTable extends JTable
{

	protected TreeTableCellRenderer m_tree;
	protected ITreeTableModel m_treeTableModel;
	protected TreeTableModelAdapter m_treeTableModelAdapter;
	protected TreeTableTreeSelectionModel m_treeTableTreeSelectionModel;
	private static final float[] dash1 = {1.0f, 1.2f};
	private static final BasicStroke dashed = new BasicStroke(1.0f,
			BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash1, 0.0f);

	public JTreeTable(ITreeTableModel treeTableModel)
	{
		super();
		setTreeModel(treeTableModel);
		setDefaultEditor(ITreeTableModel.class, new TreeTableCellEditor());
		setShowGrid(false);
		//setIntercellSpacing(new Dimension(0, 0));		// don't do this by default, if so we can't show the grid.
	}

	public void paint(Graphics g)
	{
		super.paint(g);
		//figure bounds of cell and over-draw if tree column has selected node
		int row = getSelectedRow();
		if (row != -1) {
			int col = getSelectedColumn();
			if (col != -1 && getColumnClass(col) == TreeModel.class) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setPaint(Color.ORANGE);
				g2.setStroke(dashed);
				Rectangle r = getCellRect(row, col, false);
				g2.draw(new Rectangle2D.Double(r.x, r.y, r.width - 1, r.height - 1));
			}
		}
	}

	protected void createTreeTableCellRenderer(ITreeTableModel treeTableModel)
	{
		// Create the tree. It will be used as a renderer and editor.
		m_tree = new TreeTableCellRenderer(treeTableModel);
	}

	protected TreeTableModelAdapter createTableModelAdapter(ITreeTableModel treeTableModel,
			TreeTableCellRenderer tree)
	{
		return new TreeTableModelAdapter(treeTableModel, tree);
	}

	public void setTreeModel(ITreeTableModel treeTableModel)
	{
		m_treeTableModel = treeTableModel;

		createTreeTableCellRenderer(treeTableModel);

		// Install a tableModel representing the visible rows in the tree.
		m_treeTableModelAdapter = createTableModelAdapter(m_treeTableModel, m_tree);
		super.setModel(m_treeTableModelAdapter);

		// Force the JTable and JTree to share their list selection models.
		m_treeTableTreeSelectionModel = new TreeTableTreeSelectionModel()
		{
			// Extend the implementation of the constructor, as if:
			/* public this() */ {
				setSelectionModel(listSelectionModel);
			}
		};

		m_tree.setSelectionModel(m_treeTableTreeSelectionModel);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getSelectionModel().addListSelectionListener(this);

		// Make the tree and table row heights the same.
		m_tree.setRowHeight(getRowHeight());
		// Install a selection listener to the table so the tree's selection is in sync if selection changes
		// have been made via columns other than the Tree column
		getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			private boolean inCall = false;	 // avoid recursion/stack overflow

			public void valueChanged(ListSelectionEvent e)
			{
				if (inCall || e.getValueIsAdjusting() || m_treeTableTreeSelectionModel.isModifyingSelection()) {
					return;
				}
				inCall = true;
				int[] rows = getSelectedRows();
				if (rows != null) {
					m_tree.setSelectionRows(rows);
				}
				inCall = false;
			}
		});

		// Install a key listener on the table to forward certain keyboard events to the tree
		addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if (getSelectedColumn() != -1 && getColumnClass(getSelectedColumn()) == TreeModel.class &&
						TreeUtils.isTreeKeyEvent(e)) {
					int row = getSelectionModel().getMinSelectionIndex();
					int col = getSelectedColumn();
					m_tree.dispatchEvent(e);
					getSelectionModel().setSelectionInterval(row, row);
					columnModel.getSelectionModel().setSelectionInterval(col, col);
				}
			}

			public void keyReleased(KeyEvent e)
			{
			}

			public void keyTyped(KeyEvent e)
			{
			}
		});

		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeModel.class, m_tree);
		setDefaultRenderer(ITreeTableModel.class, m_tree);
	}

	public JTree getTree()
	{
		return m_tree;
	}

	/**
	 * make sure the column is wide enough 
	 *
	 * @param column Column number to set
	 */
	public void setColumnWidth(int column)
	{
		Object columnName = m_treeTableModel.getColumnIdentifier(column);
		TableColumn col = getColumn(columnName);
		int width = col.getWidth();

		Component hdrComp = getTableHeader().getDefaultRenderer()
				.getTableCellRendererComponent(this, columnName, true, true, -1, column);

		int hdrWidth = hdrComp.getPreferredSize().width;
		if (hdrWidth > width) {
			width = hdrWidth;
		}

		TableCellRenderer cellRenderer = getDefaultRenderer(getColumnClass(column));
		Object root = m_tree.getModel().getRoot();

		for (int row = m_treeTableModel.getChildCount(root) - 1; row > 0; row--) {
			Component cellComp = cellRenderer
					.getTableCellRendererComponent(this, m_treeTableModel.getChild(root, row), true, true, row, column);
			int rowWid = cellComp.getPreferredSize().width;
			if (rowWid > width) {
				width = rowWid;
			}
		}
		col.setPreferredWidth(width);
	}

	/*
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to paint
	 * the renderers and editors and overriding setBounds() below is not the
	 * right thing to do for an editor. Returning -1 for the editing row in
	 * this case, ensures the editor is never painted.
	 */
	public int getEditingRow()
	{
		return (getColumnClass(editingColumn) == ITreeTableModel.class) ? -1 : editingRow;
	}

	public void cleanup()
	{
		setDefaultRenderer(TreeModel.class, null);
		setDefaultRenderer(ITreeTableModel.class, null);

		m_tree = null;
		m_treeTableModel = null;
		if (m_treeTableModelAdapter != null) {
			//
			// Disconnect then cleanup
			//
			TreeTableModelAdapter ttma = m_treeTableModelAdapter;
			m_treeTableModelAdapter = null;
			ttma.cleanup();
		}
		m_treeTableTreeSelectionModel = null;
	}

	//
	// The renderer used to display the tree nodes, a JTree.
	//
	public class TreeTableCellRenderer extends JTree implements TableCellRenderer
	{

		protected int visibleRow = -1;

		public TreeTableCellRenderer(TreeModel model)
		{
			super(model);
		}

		public void setBounds(int x, int y, int w, int h)
		{
			super.setBounds(x, 0, w, JTreeTable.this.getHeight());
		}

		public void paint(Graphics g)
		{
			if (visibleRow >= 0) {	   // this may avoid paintImmediately() related problems - DR dts0100248140
				g.translate(0, -visibleRow * getRowHeight());
				visibleRow = -1;
			}
			else {
				// in this case we might blat over the table grid, but it's hard to avoid.  Hence non-working code commented out
				//if(getShowHorizontalLines()) {
				//	Dimension sp = getIntercellSpacing();
				//	if (sp.height > 0) {
				//		Rectangle r = g.getClipBounds();
				//		g.setClip(r.x,  r.y + sp.height/2 + 2, r.width, r.height - sp.height/2 - 2);
				//	}
				//}
			}
			super.paint(g);
		}

		public String getToolTipText(MouseEvent event)
		{
			if (event != null) {
				Point p = event.getPoint();
				p.y += visibleRow * getRowHeight();  // adjust the Y coord to get the correct tree node for the tooltip
				return super.getToolTipText(
						new MouseEvent(event.getComponent(), event.getID(), event.getWhen(), event.getModifiers(),
								p.x, p.y, event.getClickCount(), event.isPopupTrigger()));
			}
			return null;
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column)
		{
			if (isSelected) {
				setBackground(table.getSelectionBackground());
			}
			else {
				setBackground(table.getBackground());
			}

			visibleRow = row;
			return this;
		}
	}

	//
	// The editor used to interact with tree nodes, a JTree.
	//
	public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor
	{

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c)
		{
			return m_tree;
		}
	}

	/**
	 * Class to helper with synchronizing the JTree and JTable selections. Stack overflow has to be prevented by not
	 * updating tree selection due to tree selection update! This is needed because the JTree and the JTable are sharing
	 * the same DefaultListSelectionModel
	 */
	protected static class TreeTableTreeSelectionModel extends DefaultTreeSelectionModel
	{

		private int mSelectionModifyCount = 0;

		/**
		 * @return boolean True iff the tree selection is in the process of being modified
		 */
		private boolean isModifyingSelection()
		{
			return mSelectionModifyCount > 0;
		}

		/**
		 * @see javax.swing.tree.DefaultTreeSelectionModel#setSelectionPath(javax.swing.tree.TreePath)
		 */
		public void setSelectionPath(TreePath path)
		{
			mSelectionModifyCount++;
			super.setSelectionPath(path);
			mSelectionModifyCount--;
		}

		/**
		 * @see javax.swing.tree.DefaultTreeSelectionModel#setSelectionPaths(javax.swing.tree.TreePath[])
		 */
		public void setSelectionPaths(TreePath[] pPaths)
		{
			mSelectionModifyCount++;
			super.setSelectionPaths(pPaths);
			mSelectionModifyCount--;
		}

		/**
		 * @see javax.swing.tree.DefaultTreeSelectionModel#addSelectionPath(javax.swing.tree.TreePath)
		 */
		public void addSelectionPath(TreePath path)
		{
			mSelectionModifyCount++;
			super.addSelectionPath(path);
			mSelectionModifyCount--;
		}

		/**
		 * @see javax.swing.tree.DefaultTreeSelectionModel#addSelectionPaths(javax.swing.tree.TreePath[])
		 */
		public void addSelectionPaths(TreePath[] paths)
		{
			mSelectionModifyCount++;
			super.addSelectionPaths(paths);
			mSelectionModifyCount--;
		}

		/**
		 * @see javax.swing.tree.DefaultTreeSelectionModel#removeSelectionPath(javax.swing.tree.TreePath)
		 */
		public void removeSelectionPath(TreePath path)
		{
			mSelectionModifyCount++;
			super.removeSelectionPath(path);
			mSelectionModifyCount--;
		}

		/**
		 * @see javax.swing.tree.DefaultTreeSelectionModel#removeSelectionPaths(javax.swing.tree.TreePath[])
		 */
		public void removeSelectionPaths(TreePath[] paths)
		{
			mSelectionModifyCount++;
			super.removeSelectionPaths(paths);
			mSelectionModifyCount--;
		}

		/**
		 * @see javax.swing.tree.DefaultTreeSelectionModel#clearSelection()
		 */
		public void clearSelection()
		{
			mSelectionModifyCount++;
			super.clearSelection();
			mSelectionModifyCount--;
		}

		/**
		 * @see javax.swing.tree.DefaultTreeSelectionModel#resetRowSelection()
		 */
		public void resetRowSelection()
		{
			mSelectionModifyCount++;
			super.resetRowSelection();
			mSelectionModifyCount--;
		}
	}
}
