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

import com.example.plugin.diagrampartitioning.IColumn;
import com.example.plugin.diagrampartitioning.IColumnSpecifier;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class DiagramPartitionTreeTableModel extends AbstractTreeTableModel
{

	private IDesignDiagramsPartitionGroup m_designPartition;
	private IColumnSpecifier m_columnSpecifier;
	private JTreeTable m_table;
	private static final Comparator<AbstractDPTreeNode> NODE_COMPARATOR =
			new Comparator<AbstractDPTreeNode>()
			{
				@Override public int compare(AbstractDPTreeNode o1,
						AbstractDPTreeNode o2)
				{
					return o1.toString().compareTo(o2.toString());
				}
			};
	public static final ITreeTableModel EMPTY_TREE_TABLE_MODEL = new EmptyTreeTableModel();

	protected DiagramPartitionTreeTableModel(IDesignDiagramsPartitionGroup designPartition,
			IColumnSpecifier columnSpecifier)
	{
		super(new RootNode(null, "root"));
		m_designPartition = designPartition;
		m_columnSpecifier = columnSpecifier;
		createModel();
	}

	public void setTable(JTreeTable table)
	{
		m_table = table;
	}

	private void createModel()
	{
		if (root.getChildCount() > 0) {
			((DefaultMutableTreeNode) root).removeAllChildren();
		}

		for (IDiagramPartitionGroup diagramPartitionGroup : m_designPartition.getObjects()) {
			AbstractDPTreeNode diagramNode =
					new DiagramPartitionGroupNode(diagramPartitionGroup, diagramPartitionGroup.getName(),
							m_columnSpecifier);
			((DefaultMutableTreeNode) root).add(diagramNode);

			for (IPartitionCombination<IColumn> combination : diagramPartitionGroup.getObjects()) {
				AbstractDPTreeNode node = new DiagramPartitionNode(combination, combination.getName());
				diagramNode.add(node);
			}
		}
	}

	public IColumnSpecifier getColumnSpecifier()
	{
		return m_columnSpecifier;
	}

	public int getColumnCount()
	{
		// Added 1 to accomodate the first column that shows the names of the diagrams
		return m_columnSpecifier.getColumns().size() + 1;
	}

	public Object getColumnIdentifier(int column)
	{
		if (column == 0) {
			// No name for the the first column that shows the names of the diagrams
			return " ";
		}
		return m_columnSpecifier.getColumns().get(column - 1);
	}

	public Class<? extends Object> getColumnClass(int column)
	{
		return (column != 0) ? Boolean.class : ITreeTableModel.class;
	}

	public boolean isCellEditable(Object node, int column)
	{
		return true;
	}

	public void setValueAt(Object aValue, Object node, int column)
	{
		if (column == 0) {
			// first column has the tree... no need to set the value in the partition combination. Should be taken care by the listeners of the editor
			return;
		}
		DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) node;
		Object userObject = treenode.getUserObject();
		if (userObject instanceof IPartitionCombination) {
			IPartitionCombination<IColumn> partition = (IPartitionCombination<IColumn>) userObject;
			partition.setValue((IColumn) getColumnIdentifier(column), (Boolean) aValue);
			TreeTableModelAdapter tblMdl = ((TreeTableModelAdapter) m_table.getModel());
			int row = ((TreeTableModelAdapter) m_table.getModel()).rowForNode(node);
			tblMdl.fireTableCellUpdated(row, column);
			tblMdl.fireTableDataChanged();
		}
	}

	public Object getValueAt(Object node, int column)
	{
		DefaultMutableTreeNode treenode = (DefaultMutableTreeNode) node;

		if (column == 0) {
			return treenode;
		}
		if (treenode.getUserObject() instanceof IPartitionCombination) {
			IPartitionCombination<IColumn> partition = (IPartitionCombination<IColumn>) treenode.getUserObject();
			return partition.getValue((IColumn) getColumnIdentifier(column));
		}
		return false;
	}

	public void updateNodeChildrenOrder(AbstractDPTreeNode node)
	{
		Collection<AbstractDPTreeNode> childs = new ArrayList<AbstractDPTreeNode>();
		int numChildren = node.getChildCount();
		int[] indexes = new int[numChildren];

		for (int i = 0; i < numChildren; i++) {
			childs.add((AbstractDPTreeNode) node.getChildAt(i));
			indexes[i] = i;
		}
		List<TreePath> expandedPaths = TreeUtils.getExpandedPaths(m_table.getTree());

		node.removeAllChildren();
		fireTreeNodesRemoved(node, node.getPath(), indexes, childs.toArray());

		for (AbstractDPTreeNode child : childs) {
			node.add(child);
		}
		fireTreeNodesInserted(node, node.getPath(), indexes, childs.toArray());

		TreeUtils.expandPaths(m_table.getTree(), expandedPaths);
	}

	public abstract static class AbstractDPTreeNode extends DefaultMutableTreeNode
	{

		private String m_nodename;
		private Comparator m_comparator = null;

		protected AbstractDPTreeNode(Object object, String nodename)
		{
			super(object);
			m_nodename = nodename;
			m_comparator = NODE_COMPARATOR;
		}

		public String toString()
		{
			return m_nodename;
		}

		public String getName()
		{
			return m_nodename;
		}

		protected void setName(String nodename)
		{
			m_nodename = nodename;
		}

		protected Icon getIcon()
		{
//			return IconUtils.getTransparentIcon();
			return null;
		}

		public void add(MutableTreeNode node)
		{
			if (m_comparator == null) {
				super.add(node);
			}
			else {
				boolean placed = false;
				int i = 0;
				while (i < getChildCount()) {
					if (m_comparator.compare(node, super.getChildAt(i)) < 0) {
						super.insert(node, i);
						placed = true;
						break;
					}
					i++;
				}
				if (!placed) {
					super.insert(node, i);
				}
			}
		}
	}

	private static class RootNode extends AbstractDPTreeNode
	{

		private RootNode(Object object, String nodename)
		{
			super(object, nodename);
		}
	}

	public static class DiagramPartitionNode extends AbstractDPTreeNode
	{

		public DiagramPartitionNode(Object object, String nodename)
		{
			super(object, nodename);
		}

		protected void setName(String nodename)
		{
			super.setName(nodename);
			IPartitionCombination<IColumn> partition = (IPartitionCombination<IColumn>) getUserObject();
			partition.setName(nodename);
		}
	}

	public static class DiagramPartitionGroupNode extends AbstractDPTreeNode
	{

		private IColumnSpecifier m_columnSpecifier;

		public DiagramPartitionGroupNode(Object object, String nodename,
				IColumnSpecifier columnSpecifier)
		{
			super(object, nodename);
			m_columnSpecifier = columnSpecifier;
		}

		protected Icon getIcon()
		{
			return PartitionHelper.getImageIcon("ico_diagram.gif");
		}

		public DiagramPartitionNode copyNode(DiagramPartitionNode partitionNode, String name)
		{
			assert getIndex(partitionNode) >= 0 : "The node should be a child node";

			IPartitionCombination<IColumn> partition = (IPartitionCombination<IColumn>) partitionNode.getUserObject();

			IDiagramPartitionGroup diagramPartitionGroup = (IDiagramPartitionGroup) getUserObject();

			IPartitionCombination<IColumn> newPartition = new PartitionCombination<IColumn>(name, partition);
			diagramPartitionGroup.add(newPartition);

			DiagramPartitionNode newNode = new DiagramPartitionNode(newPartition, newPartition.getName());
			add(newNode);

			return newNode;
		}

		/**
		 * Create diagram partition node with the specified name under this diagram partition group node. By default, all the
		 * columns are selected.
		 *
		 * @param name name of the diagram partition
		 */
		public void createNode(String name)
		{
			IDiagramPartitionGroup diagramPartitionGroup = (IDiagramPartitionGroup) getUserObject();
			IPartitionCombination<IColumn> newPartition = new PartitionCombination<IColumn>(name);
			for (IColumn column : m_columnSpecifier.getColumns()) {
				newPartition.setValue(column, true);
			}
			diagramPartitionGroup.add(newPartition);
			DiagramPartitionNode newNode = new DiagramPartitionNode(newPartition, newPartition.getName());
			add(newNode);
		}

		public void removeNode(DiagramPartitionNode node)
		{
			IDiagramPartitionGroup diagramPartitionGroup = (IDiagramPartitionGroup) getUserObject();
			diagramPartitionGroup.remove((IPartitionCombination<IColumn>) node.getUserObject());
			remove(node);
		}

		public void removeAllNodes()
		{
			IDiagramPartitionGroup diagramPartitionGroup = (IDiagramPartitionGroup) getUserObject();
			diagramPartitionGroup.removeAll();
			removeAllChildren();
		}

		public boolean isNameValid(String newName)
		{
			int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				DiagramPartitionNode childNode = (DiagramPartitionNode) getChildAt(i);
				if (childNode.toString().equalsIgnoreCase(newName)) {
					return false;
				}
			}
//			return newName.trim().length() > 0 && newName.length() <= CHSConstants.MAX_NAME_LENGTH;
			return newName.trim().length() > 0 && newName.length() <= 255;
		}
	}

	private static class EmptyTreeTableModel extends AbstractTreeTableModel
	{

		private EmptyTreeTableModel()
		{
			super(new RootNode(null, "root"));
		}

		public int getColumnCount()
		{
			return 0;
		}

		public Object getColumnIdentifier(int column)
		{
			return null;
		}

		@Override public Object getValueAt(Object node, int column)
		{
			return null;
		}
	}
}
