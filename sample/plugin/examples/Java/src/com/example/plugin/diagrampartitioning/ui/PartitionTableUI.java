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
import com.example.plugin.diagrampartitioning.IColumn;
import com.example.plugin.diagrampartitioning.IColumnGroup;
import com.example.plugin.diagrampartitioning.IColumnSpecifier;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

public class PartitionTableUI extends JPanel
{

	private static final int colorStripeGray = 0xF4F4F4;
	private static final Color STRIPE_GRAY = new Color(colorStripeGray);

	private DiagramPartitionTreeTableModel m_treeTableModel;
	private JTreeTable m_table;
	private static final int COLUMN_WIDTH = 300;
	private static final int TABLE_WIDTH = 500;
	private static final int TABLE_HEIGHT = 300;
	private JButton m_addButton;
	private JButton m_deleteButton;
	private JButton m_copyButton;
	private JButton m_selectAllButton;
	private JButton m_UnSelectAllButton;
	private NameGenerator m_nameGenerator;
	private DPController m_controller = new DPController();

	public PartitionTableUI()
	{
		m_nameGenerator = new NameGenerator();
		buildUI();
	}

	private void buildUI()
	{
		setLayout(new BorderLayout());

		m_table = new PartitionTreeTable(DiagramPartitionTreeTableModel.EMPTY_TREE_TABLE_MODEL);
		m_table.setName("diagramPartitionTable");
		m_table.setPreferredScrollableViewportSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));
		m_table.setRowHeight((int) (m_table.getRowHeight() * 1.2));
		m_table.setShowVerticalLines(true);
		m_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.add(getEditButtonPanel(), BorderLayout.WEST);

		JScrollPane scrollPane = new JScrollPane(m_table);
		scrollPane.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));

		add(buttonPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	private Component getSelectUnselectPanel()
	{
		m_selectAllButton = new JButton("Select All");
		m_selectAllButton.setName("SELECTALL");
		m_selectAllButton.setToolTipText("Selects all the columns for the selected diagram partitions");
		m_selectAllButton.addActionListener(m_controller);

		m_UnSelectAllButton = new JButton("Clear");
		m_UnSelectAllButton.setName("UNSELECTALL");
		m_UnSelectAllButton.setToolTipText("Clears all the columns for the selected diagram partitions");
		m_UnSelectAllButton.addActionListener(m_controller);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		m_selectAllButton.setEnabled(false);
		m_UnSelectAllButton.setEnabled(false);
		buttonPanel.add(m_selectAllButton, BorderLayout.WEST);
		buttonPanel.add(m_UnSelectAllButton, BorderLayout.CENTER);
		return buttonPanel;
	}

	private void setTreeSettings()
	{
		m_table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		m_table.getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		m_table.getTree().setName("diagramPartitionTree");
		m_table.getTree().setCellRenderer(new DPTreeCellRenderer());
		final TableCellEditor cellEditor = new DPTreeCellEditor(m_table, new TreeTableTextField());
		cellEditor.addCellEditorListener(m_controller);
		m_table.setDefaultEditor(ITreeTableModel.class, cellEditor);
		m_table.getTree().addTreeSelectionListener(m_controller);
		m_table.addKeyListener(new TableKeyListener());

		m_table.getTree().setInvokesStopCellEditing(true);
		m_table.getTree().setEditable(true);
		m_table.getTree().setRootVisible(false);
		m_table.getTree().setShowsRootHandles(true);
		TreeUtils.expandAll(m_table.getTree(), true);

		updateTreeContaingColumnWidth(m_table);
	}

	private JPanel getEditButtonPanel()
	{
		Icon addIcon = PartitionHelper.getImageIcon("btn_add.gif");
		m_addButton = addIcon != null ? new JButton(addIcon) : new JButton("ADD");
		m_addButton.setName("ADD");
		m_addButton.setToolTipText("Add diagram partition for the selected functional diagram");
		m_addButton.addActionListener(m_controller);
		m_addButton.setRolloverEnabled(true);

		Icon deleteIcon = PartitionHelper.getImageIcon("ico_delete_active.gif");
		m_deleteButton = deleteIcon != null ? new JButton(deleteIcon) : new JButton("DEL");
		m_deleteButton.setName("DELETE");
		m_deleteButton.setToolTipText("Removes the selected diagram partitions");
		m_deleteButton.addActionListener(m_controller);
		m_deleteButton.setRolloverEnabled(true);

		Icon copyIcon = PartitionHelper.getImageIcon("ico_copy_active.gif");
		m_copyButton = copyIcon != null ? new JButton(copyIcon) : new JButton("COPY");

		m_copyButton.setName("COPY");
		m_copyButton.setToolTipText("Copy diagram partition using the selected diagram partition");
		m_copyButton.addActionListener(m_controller);
		m_copyButton.setRolloverEnabled(true);

		m_addButton.setEnabled(false);
		m_deleteButton.setEnabled(false);
		m_copyButton.setEnabled(false);

		//Add the buttons to the toolbar
		JToolBar buttonsToolBar = new JToolBar("buttonsToolBar");
		buttonsToolBar.setFloatable(false);
		buttonsToolBar.setRollover(true);
		buttonsToolBar.setBorder(BorderFactory.createEmptyBorder());

		buttonsToolBar.add(m_addButton);
		buttonsToolBar.add(m_deleteButton);
		buttonsToolBar.add(m_copyButton);

		m_selectAllButton = new JButton("Select All");
		m_selectAllButton.setName("SELECTALL");
		m_selectAllButton.setToolTipText("Selects all the columns for the selected diagram partitions");
		m_selectAllButton.addActionListener(m_controller);
		m_selectAllButton.setRolloverEnabled(true);

		m_UnSelectAllButton = new JButton("Clear");
		m_UnSelectAllButton.setName("UNSELECTALL");
		m_UnSelectAllButton.setToolTipText("Clears all the columns for the selected diagram partitions");
		m_UnSelectAllButton.addActionListener(m_controller);
		m_UnSelectAllButton.setRolloverEnabled(true);

		m_selectAllButton.setEnabled(false);
		m_UnSelectAllButton.setEnabled(false);

		JPanel buttonPanel = new JPanel(new GridBagLayout());

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = 3;
		constraints.anchor = GridBagConstraints.WEST;

		buttonPanel.add(buttonsToolBar, constraints);

		constraints.gridx = 5;
		constraints.gridwidth = 2;
		buttonPanel.add(m_selectAllButton, constraints);

		constraints.gridx = 7;
		buttonPanel.add(m_UnSelectAllButton, constraints);

		return buttonPanel;
	}

	private void buildTableHeaderGroups(IColumnSpecifier columnSpecifier)
	{

		TableColumnModel colMdl = setColumnHeaderAndIdentifier(columnSpecifier);
		if (getTotalColumnWidth(m_table) < TABLE_WIDTH) {
			m_table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
		else {
			m_table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}

		GroupableTableHeader header = (GroupableTableHeader) m_table.getTableHeader();
		header.removeAllColumnGroups();
		header.setColumnModel(colMdl);

		ColumnGroup columnGroup = new ColumnGroup("");
		columnGroup.setHeaderRenderer(new GroupableTableHeaderCellRenderer());
		header.addColumnGroup(columnGroup);

		// First column is the tree
		TableColumn tableCol = colMdl.getColumn(0);
		columnGroup.add(tableCol);

		// Other columns are the options/properties as specified by the column specifier
		for (IColumnGroup group : columnSpecifier.getGroups()) {
			columnGroup = new ColumnGroup(group.getName());
			columnGroup.setHeaderRenderer(new GroupableTableHeaderCellRenderer());
			header.addColumnGroup(columnGroup);

			for (IColumn column : group.getColumns()) {
				tableCol = colMdl.getColumn(colMdl.getColumnIndex(column));
				columnGroup.add(tableCol);
			}
		}
	}

	private static int getTotalColumnWidth(JTreeTable table)
	{
		TableColumnModel model = table.getColumnModel();
		TableCellRenderer headerRenderer =
				table.getTableHeader().getDefaultRenderer();

		int totalColumnWidth = 0;
		for (int i = 0; i < model.getColumnCount(); i++) {
			TableColumn column = model.getColumn(i);

			int headerWidth;
			if (i != 0) {
				Component comp = headerRenderer.getTableCellRendererComponent(
						table, column.getHeaderValue(),
						false, false, 0, i);
				headerWidth = comp.getPreferredSize().width;
			}
			else {
				headerWidth = getPreferredTreeWidth(table);
			}

			totalColumnWidth += headerWidth;
		}
		return totalColumnWidth;
	}

	private int initColumnSizes()
	{
		TableColumnModel model = m_table.getColumnModel();
		TableCellRenderer headerRenderer =
				m_table.getTableHeader().getDefaultRenderer();

		int totalColumnWidth = 0;
		for (int i = 0; i < model.getColumnCount(); i++) {
			TableColumn column = model.getColumn(i);

			int headerWidth;
			if (i != 0) {
				Component comp = headerRenderer.getTableCellRendererComponent(
						m_table, column.getHeaderValue(),
						false, false, 0, i);
				headerWidth = comp.getPreferredSize().width;
			}
			else {
				headerWidth = getPreferredTreeWidth(m_table);
			}

			column.setPreferredWidth(headerWidth);
			totalColumnWidth += headerWidth;
		}
		return totalColumnWidth;
	}

	private static int getPreferredTreeWidth(JTreeTable table)
	{
		int columnIdx = 0;
		TableCellRenderer renderer = table.getDefaultRenderer(ITreeTableModel.class);
		int rowCount = table.getRowCount();

		int headerWidth = COLUMN_WIDTH;
		for (int rowIdx = 0; rowIdx < rowCount; rowIdx++) {
			Object value = table.getValueAt(rowIdx, columnIdx);
			Component comp = renderer.getTableCellRendererComponent(table, value, false, false, rowIdx, columnIdx);
			headerWidth = Math.max(comp.getPreferredSize().width, headerWidth);
		}
		return headerWidth;
	}

	private TableColumnModel setColumnHeaderAndIdentifier(IColumnSpecifier columnSpecifier)
	{
		TableColumnModel colMdl = m_table.getColumnModel();
		int columnCount = colMdl.getColumnCount();
		if (columnCount == columnSpecifier.getColumns().size() + 1) {
			for (int i = 0; i < columnCount; i++) {
				TableColumn column = colMdl.getColumn(i);
				if (i == 0) {
					column.setIdentifier("");
					column.setHeaderValue("");
				}
				else {
					IColumn identifier = columnSpecifier.getColumns().get(i - 1);
					column.setIdentifier(identifier);
					column.setHeaderValue(identifier.getName());
				}
			}
		}
		initColumnSizes();
		return colMdl;
	}

	public void setObectTypeModel(PartitionModel.IDesignPartitionModel designPartitionModel)
	{
		m_treeTableModel =
				new DiagramPartitionTreeTableModel(designPartitionModel.getDesignPartition(),
						designPartitionModel.getColumnSpecifier());
		m_treeTableModel.setTable(m_table);
		m_table.setTreeModel(m_treeTableModel);
		refreshButtons(null);
		buildTableHeaderGroups(designPartitionModel.getColumnSpecifier());
		setTreeSettings();
		updateView();
	}

	private void updateView()
	{
		((TreeTableModelAdapter) m_table.getModel()).fireAllTableCellUpdated();
		((AbstractTableModel) m_table.getModel()).fireTableDataChanged();
		m_table.getTree().repaint();
		TreeUtils.expandAll(m_table.getTree(), true);
		m_table.updateUI();
	}

	private static class PartitionTreeTable extends JTreeTable
	{

		private TableCellRenderer m_diagramColumnRenderer = new DiagramColumnCellRenderer();

		PartitionTreeTable(ITreeTableModel treeTableModel)
		{
			super(treeTableModel);
			getTableHeader().setReorderingAllowed(false);
		}

		public void setTreeModel(ITreeTableModel treeTableModel)
		{
			super.setTreeModel(treeTableModel);
		}

		private class DiagramColumnCellRenderer extends JLabel implements TableCellRenderer
		{

			private DiagramColumnCellRenderer()
			{
				setBackground(getBackground());
				setText("");
				setHorizontalAlignment(CENTER);
			}

			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus,
					int row, int column)
			{
				if (!isCellSelected(row, column)) {
					if (row % 2 == 0) {
						setBackground(STRIPE_GRAY);
					}
					else {
						setBackground(getBackground());
					}
				}
				return this;
			}
		}

		private boolean isColumnAgainstDiagramNode(int row, int column)
		{
			if (column != 0) {
				Object treeNode = getValueAt(row, 0);
				if (treeNode instanceof DefaultMutableTreeNode) {
					Object partition = ((DefaultMutableTreeNode) treeNode).getUserObject();
					return partition instanceof IDiagramPartitionGroup;
				}
			}
			return false;
		}

		@Override public boolean isCellEditable(int row, int column)
		{
			if (isColumnAgainstDiagramNode(row, column)) {
				return false;
			}

			return super.isCellEditable(row, column);
		}

		@Override public TableCellRenderer getCellRenderer(int row, int column)
		{
			TableCellRenderer defaultRenderer = super.getCellRenderer(row, column);
			if (isColumnAgainstDiagramNode(row, column)) {
				return m_diagramColumnRenderer;
			}
			return defaultRenderer;
		}

		public Component prepareRenderer(TableCellRenderer renderer, int row, int column)
		{
			JComponent c = (JComponent) super.prepareRenderer(renderer, row, column);

			if (!isCellSelected(row, column)) {
				if (row % 2 == 0) {
					c.setBackground(STRIPE_GRAY);
				}
				else {
					c.setBackground(getBackground());
				}
			}

			setFont(getFont());

			return c;
		}

		@Override protected JTableHeader createDefaultTableHeader()
		{
			return new GroupableTableHeader(columnModel);
		}
	}

//	@Override public void setEnabled(boolean enabled)
//	{
//		super.setEnabled(enabled);
//		for (chs.utilities.ui.property.IProperty property : getProperties()) {
//			if (property.isAttribute(INHERIT_ENABLED, true)) {
//				m_table.setEnabled(enabled);
//			}
//		}
//	}

	private static class DPTreeCellRenderer extends DefaultTreeCellRenderer
	{

		DPTreeCellRenderer()
		{
			setOpenIcon(null);
			setClosedIcon(null);
			setLeafIcon(null);
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus)
		{
			JComponent component =
					(JComponent) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			DiagramPartitionTreeTableModel.AbstractDPTreeNode node =
					(DiagramPartitionTreeTableModel.AbstractDPTreeNode) value;
			setIcon(node.getIcon());

			if (!sel) {
				if (row % 2 == 0) {
					setBackground(STRIPE_GRAY);
				}
				else {
					setBackground(getBackground());
				}
			}
			return component;
		}
	}

	private void refreshButtons(TreePath selectedPath)
	{
		if (selectedPath == null) {
			m_addButton.setEnabled(false);
			m_deleteButton.setEnabled(false);
			m_copyButton.setEnabled(false);
			m_selectAllButton.setEnabled(false);
			m_UnSelectAllButton.setEnabled(false);
		}
		else {

			boolean isMultiSelection = m_table.getTree().getSelectionCount() > 1;

			if (isMultiSelection) {
				m_addButton.setEnabled(false);
				boolean haveOnlyParitionCombinations = !hasDiagramNodeInSelections();
				m_deleteButton.setEnabled(haveOnlyParitionCombinations);
				m_selectAllButton.setEnabled(haveOnlyParitionCombinations);
				m_UnSelectAllButton.setEnabled(haveOnlyParitionCombinations);
				m_copyButton.setEnabled(false);
			}
			else {
				DiagramPartitionTreeTableModel.AbstractDPTreeNode node =
						(DiagramPartitionTreeTableModel.AbstractDPTreeNode) selectedPath.getLastPathComponent();
				Object o = node.getUserObject();
				if (o instanceof IDiagramPartitionGroup) {
					IDiagramPartitionGroup diagramPartitionGroup = (IDiagramPartitionGroup) o;
					m_addButton.setEnabled(true);
					m_deleteButton.setEnabled(!diagramPartitionGroup.getObjects().isEmpty());
					m_deleteButton.setToolTipText("Removes diagram partitions of the selected functional diagram");
					m_copyButton.setEnabled(false);
					m_selectAllButton.setEnabled(false);
					m_UnSelectAllButton.setEnabled(false);
				}
				else {
					m_addButton.setEnabled(false);
					m_deleteButton.setEnabled(true);
					m_deleteButton.setToolTipText("Removes the selected diagram partitions");
					m_copyButton.setEnabled(true);
					m_selectAllButton.setEnabled(true);
					m_UnSelectAllButton.setEnabled(true);
				}
			}
		}
	}

	private boolean hasDiagramNodeInSelections()
	{
		JTree tree = m_table.getTree();
		TreePath[] selectedPaths = tree.getSelectionPaths();
		if (selectedPaths != null) {
			for (TreePath selectedPath : selectedPaths) {
				Object object = selectedPath.getLastPathComponent();
				if (object instanceof DiagramPartitionTreeTableModel.DiagramPartitionGroupNode) {
					return true;
				}
			}
		}
		return false;
	}

	private class DPController implements TreeSelectionListener, ActionListener, CellEditorListener
	{

		public void valueChanged(TreeSelectionEvent e)
		{
			TreePath path = e.getNewLeadSelectionPath();

			refreshButtons(path);
		}

		public void actionPerformed(ActionEvent e)
		{
			TreePath selectedPath = m_table.getTree().getSelectionPath();
			if (selectedPath == null) {
				return;
			}

			Object source = e.getSource();
			if (source == m_addButton) {
				DiagramPartitionTreeTableModel.DiagramPartitionGroupNode node =
						(DiagramPartitionTreeTableModel.DiagramPartitionGroupNode) selectedPath.getLastPathComponent();
				node.createNode(m_nameGenerator.generateNodeName(node));
				updateView();
				m_table.getTree().expandPath(selectedPath);
				m_table.getTree().setSelectionPath(selectedPath);
			}
			else if (source == m_deleteButton) {
				TreePath[] selectedPaths = m_table.getTree().getSelectionPaths();
				if (selectedPaths != null) {
					DiagramPartitionTreeTableModel.AbstractDPTreeNode nodeToSelect = null;
					for (TreePath deleteNodePath : selectedPaths) {
						nodeToSelect = deleteNode(deleteNodePath);
					}
					updateView();
					if (nodeToSelect != null) {
						TreePath path = new TreePath(nodeToSelect.getPath());
						m_table.getTree().setSelectionPath(path);
						refreshButtons(path);
					}
					else {
						refreshButtons(null);
					}
				}
			}
			else if (source == m_copyButton) {
				DiagramPartitionTreeTableModel.DiagramPartitionNode currentNode =
						(DiagramPartitionTreeTableModel.DiagramPartitionNode) selectedPath.getLastPathComponent();

				DiagramPartitionTreeTableModel.DiagramPartitionGroupNode parentNode =
						(DiagramPartitionTreeTableModel.DiagramPartitionGroupNode) currentNode.getParent();

				DiagramPartitionTreeTableModel.AbstractDPTreeNode newNode =
						parentNode.copyNode(currentNode,
								m_nameGenerator.generateNodeName("Copy of " + currentNode.getName(), parentNode));

				updateView();
				TreePath newPathToSelect = new TreePath(newNode.getPath());
				m_table.getTree().expandPath(newPathToSelect);
				m_table.getTree().setSelectionPath(newPathToSelect);
			}
			else if (source == m_selectAllButton) {
				TreePath[] selectedPaths = m_table.getTree().getSelectionPaths();
				setValueonAllColumns(selectedPaths, true);
				m_table.updateUI();
			}
			else if (source == m_UnSelectAllButton) {
				TreePath[] selectedPaths = m_table.getTree().getSelectionPaths();
				setValueonAllColumns(selectedPaths, false);
				m_table.updateUI();
			}

			updateTreeContaingColumnWidth(m_table);
		}

		private DiagramPartitionTreeTableModel.AbstractDPTreeNode deleteNode(TreePath selectedPath)
		{
			DiagramPartitionTreeTableModel.AbstractDPTreeNode deleteOnNode =
					(DiagramPartitionTreeTableModel.AbstractDPTreeNode) selectedPath.getLastPathComponent();

			DiagramPartitionTreeTableModel.AbstractDPTreeNode nodeToSelect = null;
			if (deleteOnNode instanceof DiagramPartitionTreeTableModel.DiagramPartitionGroupNode) {
				nodeToSelect = deleteOnNode;
				((DiagramPartitionTreeTableModel.DiagramPartitionGroupNode) deleteOnNode).removeAllNodes();
			}
			else {
				DiagramPartitionTreeTableModel.DiagramPartitionGroupNode parentNode =
						(DiagramPartitionTreeTableModel.DiagramPartitionGroupNode) deleteOnNode.getParent();
				int childIndex = parentNode.getIndex(deleteOnNode);
				int numChild = parentNode.getChildCount();
				if (childIndex + 1 >= numChild) {
					childIndex--;
				}
				else {
					childIndex++;
				}

				if (childIndex >= 0) {
					nodeToSelect =
							(DiagramPartitionTreeTableModel.AbstractDPTreeNode) parentNode.getChildAt(childIndex);
				}

				parentNode.removeNode((DiagramPartitionTreeTableModel.DiagramPartitionNode) deleteOnNode);
			}
			return nodeToSelect;
		}

		private void setValueonAllColumns(TreePath[] selectedPaths, boolean select)
		{
			if (selectedPaths == null) {
				return;
			}

			for (TreePath selectedPath : selectedPaths) {
				if (selectedPath
						.getLastPathComponent() instanceof DiagramPartitionTreeTableModel.DiagramPartitionNode) {
					DiagramPartitionTreeTableModel.DiagramPartitionNode currentNode =
							(DiagramPartitionTreeTableModel.DiagramPartitionNode) selectedPath.getLastPathComponent();
					Object userObject = currentNode.getUserObject();
					if (userObject instanceof IPartitionCombination) {
						IPartitionCombination<IColumn> combination = (IPartitionCombination<IColumn>) userObject;
						IColumnSpecifier columnSpecifier = m_treeTableModel.getColumnSpecifier();
						if (columnSpecifier != null) {
							for (IColumn column : columnSpecifier.getColumns()) {
								combination.setValue(column, select);
							}
						}
					}
				}
			}
		}

		public void editingStopped(ChangeEvent e)
		{
			Object editedNode = m_table.getTree().getLastSelectedPathComponent();
			DiagramPartitionTreeTableModel.DiagramPartitionNode node = null;
			if (editedNode instanceof DiagramPartitionTreeTableModel.DiagramPartitionNode) {
				node = (DiagramPartitionTreeTableModel.DiagramPartitionNode) editedNode;
			}
			if (node == null) {
				editingCanceled(e);
				return;
			}

			DiagramPartitionTreeTableModel.DiagramPartitionGroupNode parentNode =
					(DiagramPartitionTreeTableModel.DiagramPartitionGroupNode) node.getParent();

			Object value = m_table.getDefaultEditor(ITreeTableModel.class).getCellEditorValue();
			if (value instanceof String) {
				String newName = ((String) value).trim();
				if (node.getName().equalsIgnoreCase(newName)) {
					editingCanceled(e);
				}
				else if (parentNode.isNameValid(newName)) {
					node.setName(newName);
					m_treeTableModel.updateNodeChildrenOrder(parentNode);
					m_table.getTree().setSelectionPath(new TreePath(node.getPath()));

					updateTreeContaingColumnWidth(m_table);
				}
				else {
					editingCanceled(e);
					String message = "Diagram partition with the name" + "\"" + newName + "\"" + "already exists.";
					JOptionPane.showMessageDialog(null, message, "Name already exists", JOptionPane.WARNING_MESSAGE);
				}
			}
		}

		public void editingCanceled(ChangeEvent e)
		{

		}
	}

	/**
	 * Update width of the first column of the table by analzing the values of the column.
	 *
	 * @param table table
	 */
	private static void updateTreeContaingColumnWidth(JTreeTable table)
	{

		int treeWidth = getPreferredTreeWidth(table);
		table.getColumnModel().getColumn(0).setPreferredWidth(treeWidth);

		if (getTotalColumnWidth(table) < TABLE_WIDTH) {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		}
		else {
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		}
	}

	private static class TreeTableTextField extends JTextField
	{

		private int offset;

		public void reshape(int x, int y, int w, int h)
		{
			int newX = Math.max(x, offset);
			super.reshape(newX, y, w - (newX - x), h);
		}

		public void setOffset(int offset)
		{
			this.offset = offset;
		}
	}

	private static class DPTreeCellEditor extends DefaultCellEditor
	{

		private JTreeTable m_dpTable;

		private DPTreeCellEditor(JTreeTable dpTable, JTextField textField)
		{
			super(textField);
			m_dpTable = dpTable;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c)
		{
			Object nvalue = value;
			if (value instanceof DiagramPartitionTreeTableModel.AbstractDPTreeNode) {
				nvalue = value.toString();
			}
			Component component = super.getTableCellEditorComponent(table, nvalue, isSelected, r, c);
			JTree t = m_dpTable.getTree();
			boolean rv = t.isRootVisible();
			int offsetRow = rv ? r : r - 1;
			Rectangle bounds = t.getRowBounds(offsetRow);
			int offset = bounds.x;
			TreeCellRenderer tcr = t.getCellRenderer();
			if (tcr instanceof DefaultTreeCellRenderer) {
				Object node = t.getPathForRow(offsetRow).getLastPathComponent();
				Icon icon;
				if (t.getModel().isLeaf(node)) {
					icon = ((DefaultTreeCellRenderer) tcr).getLeafIcon();
				}
				else if (t.isExpanded(offsetRow)) {
					icon = ((DefaultTreeCellRenderer) tcr).getOpenIcon();
				}
				else {
					icon = ((DefaultTreeCellRenderer) tcr).getClosedIcon();
				}
				if (icon != null) {
					offset += ((JLabel) tcr).getIconTextGap() + icon.getIconWidth();
				}
			}
			((TreeTableTextField) getComponent()).setOffset(offset);
			return component;
		}

		public boolean isCellEditable(EventObject event)
		{
			if (!super.isCellEditable(event)) {
				return false;
			}
			JTree tree = m_dpTable.getTree();
			TreeModel treeModel = tree.getModel();
			if (!(treeModel instanceof DiagramPartitionTreeTableModel)) {
				return false;
			}

			if (event instanceof KeyEvent) {
				if (((KeyEvent) event).getKeyCode() != KeyEvent.VK_F2) {
					return false;
				}
			}

			if (event == null || event.getSource() == m_dpTable) {
				TreePath path;
				if (event instanceof MouseEvent) {
					path = tree.getPathForLocation(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
					MouseEvent me = (MouseEvent) event;
					// If the modifiers are not 0 (or the left mouse button),
					// tree may try and toggle the selection, and table
					// will then try and toggle, resulting in the
					// selection remaining the same. To avoid this, we
					// only dispatch when the modifiers are 0 (or the left mouse
					// button).
					if (me.getModifiers() == 0 ||
							me.getModifiers() == InputEvent.BUTTON1_MASK) {
						MouseEvent newME = new MouseEvent
								(tree, me.getID(),
										me.getWhen(), me.getModifiers(),
										me.getX() - m_dpTable.getCellRect(0, 0, true).x,
										me.getY(), me.getClickCount(),
										me.isPopupTrigger());
						tree.dispatchEvent(newME);
					}
					return me.getClickCount() == 2 && isAllowEditingOfNode(path);
				}
				else {
					// KB event - use selection path
					path = tree.getLeadSelectionPath();
					return isAllowEditingOfNode(path);
				}
			}

			return false;
		}

		private boolean isAllowEditingOfNode(TreePath path)
		{
			if (path != null) {
				DiagramPartitionTreeTableModel.AbstractDPTreeNode selNode =
						(DiagramPartitionTreeTableModel.AbstractDPTreeNode) path.getLastPathComponent();
				if (selNode.getUserObject() instanceof IPartitionCombination) {
					// allow editing of node only for diagram partitions
					return true;
				}
			}
			return false;
		}
	}

	private static class NameGenerator
	{

		private static final String BASE_NAME = "Combination";

		private NameGenerator()
		{
		}

		public String generateNodeName(
				DiagramPartitionTreeTableModel.DiagramPartitionGroupNode diagramPartitionGroupNode)
		{
			return generateNodeName(BASE_NAME, diagramPartitionGroupNode);
		}

		public String generateNodeName(String initialString,
				DiagramPartitionTreeTableModel.DiagramPartitionGroupNode diagramPartitionGroupNode)
		{
			Set<String> existingNames = new HashSet<String>();
			Enumeration<DiagramPartitionTreeTableModel.DiagramPartitionNode> enumeration =
					diagramPartitionGroupNode.children();
			while (enumeration.hasMoreElements()) {
				Object obj = enumeration.nextElement();
				if (obj instanceof DiagramPartitionTreeTableModel.DiagramPartitionNode) {
					existingNames.add(((DiagramPartitionTreeTableModel.AbstractDPTreeNode) obj).getName());
				}
			}
			existingNames.add(BASE_NAME);
			return generateName(initialString, existingNames);
		}

		private int getNextIndex(Set<String> existingNames)
		{
			int index = 0;
			for (String existingName : existingNames) {
				if (existingName.startsWith(BASE_NAME)) {
					String numString = existingName.substring(BASE_NAME.length() - 1, existingName.length() - 1);
					if (numString.length() > 0) {
						try {
							index = Math.max(Integer.valueOf(numString), index);
						}
						catch (NumberFormatException exp) {
							//do nothing
						}
					}
				}
			}
			return index + 1;
		}

		public String generateName(String baseName, Set<String> existingNames)
		{
			if (!existingNames.contains(baseName)) {
				return baseName;
			}
			int index = getNextIndex(existingNames);
			String newName = baseName + index;
			while (true) {
				if (existingNames.contains(newName)) {
					index++;
					newName = baseName + index;
				}
				else {
					break;
				}
			}
			existingNames.add(newName);
			return newName;
		}
	}

	private static class TableKeyListener implements KeyListener
	{

		public void keyTyped(KeyEvent e)
		{
		}

		public void keyPressed(KeyEvent e)
		{

			Object source = e.getSource();
			if (source instanceof JTreeTable) {
				JTreeTable table = (JTreeTable) source;
				JTree tree = table.getTree();
				if (tree == null) {
					return;
				}

				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					TreePath path = tree.getSelectionPath();
					if (path != null) {
						Object obj = path.getLastPathComponent();
						if (obj instanceof DiagramPartitionTreeTableModel.DiagramPartitionNode) {
							DiagramPartitionTreeTableModel.DiagramPartitionNode node =
									(DiagramPartitionTreeTableModel.DiagramPartitionNode) obj;
							DiagramPartitionTreeTableModel.AbstractDPTreeNode diagramGroupNode =
									(DiagramPartitionTreeTableModel.AbstractDPTreeNode) node.getParent();
							TreePath parentPath = new TreePath(diagramGroupNode.getPath());
							tree.collapsePath(parentPath);
							tree.setSelectionPath(parentPath);
						}
						else if (obj instanceof DiagramPartitionTreeTableModel.DiagramPartitionGroupNode) {
							DiagramPartitionTreeTableModel.AbstractDPTreeNode diagramGroupNode =
									(DiagramPartitionTreeTableModel.AbstractDPTreeNode) obj;
							TreePath treePath = new TreePath(diagramGroupNode.getPath());
							if (tree.isExpanded(treePath)) {
								tree.collapsePath(treePath);
								tree.setSelectionPath(treePath);
							}
						}
						updateTreeContaingColumnWidth(table);
					}
				}
				else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					TreePath path = tree.getSelectionPath();
					if (path != null) {
						Object obj = path.getLastPathComponent();
						if (obj instanceof DiagramPartitionTreeTableModel.DiagramPartitionGroupNode) {
							tree.expandPath(path);
							tree.setSelectionPath(path);
						}
						updateTreeContaingColumnWidth(table);
					}
				}
			}
		}

		public void keyReleased(KeyEvent e)
		{
//			Object source = e.getSource();
//			if (source instanceof JTreeTable) {
//				JTreeTable table = (JTreeTable) source;
//				JTree tree = table.getTree();
//				if (tree == null) {
//					return;
//				}
//				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
//					TreePath path = tree.getSelectionPath();
//					if (path != null) {
//						m_deleteButton.doClick();
//					}
//				}
//			}
		}
	}
}

