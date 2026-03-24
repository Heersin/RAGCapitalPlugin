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

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public abstract class AbstractTreeTableModel implements ITreeTableModel
{

	protected TreeNode root;
	protected EventListenerList listenerList = new EventListenerList();

	protected AbstractTreeTableModel(TreeNode root)
	{
		this.root = root;
	}

	//
	// Default implmentations for methods in the TreeModel interface.
	//
	public Object getRoot()
	{
		return root;
	}

	public boolean isLeaf(Object node)
	{
		return getChildCount(node) == 0;
	}

	public void valueForPathChanged(TreePath path, Object newValue)
	{
		//MutableTreeNode   aNode = (MutableTreeNode)path.getLastPathComponent();
		//aNode.setUserObject(newValue);
		fireTreeNodesChanged(this, path.getPath(), null, null);
	}

	// This is not called in the JTree's default mode: use a naive implementation.
	public int getIndexOfChild(Object parent, Object child)
	{
		for (int i = 0; i < getChildCount(parent); i++) {
			if (getChild(parent, i).equals(child)) {
				return i;
			}
		}
		return -1;
	}

	public void addTreeModelListener(TreeModelListener l)
	{
		listenerList.add(TreeModelListener.class, l);
	}

	public void removeTreeModelListener(TreeModelListener l)
	{
		listenerList.remove(TreeModelListener.class, l);
	}

	/*
	 * Notify all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 * @see EventListenerList
	 */
	protected void fireTreeNodesChanged(Object source, Object[] path,
			int[] childIndices,
			Object[] children)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesChanged(e);
			}
		}
	}

	/*
	 * Notify all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 * @see EventListenerList
	 */
	protected void fireTreeNodesInserted(Object source, Object[] path,
			int[] childIndices,
			Object[] children)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesInserted(e);
			}
		}
	}

	/*
	 * Notify all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 * @see EventListenerList
	 */
	protected void fireTreeNodesRemoved(Object source, Object[] path,
			int[] childIndices,
			Object[] children)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;

		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeNodesRemoved(e);
			}
		}
	}

	/*
	 * Notify all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created using the parameters passed into
	 * the fire method.
	 * @see EventListenerList
	 */
	protected void fireTreeStructureChanged(Object source, Object[] path,
			int[] childIndices,
			Object[] children)
	{
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		TreeModelEvent e = null;
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == TreeModelListener.class) {
				// Lazily create the event:
				if (e == null) {
					e = new TreeModelEvent(source, path, childIndices, children);
				}
				((TreeModelListener) listeners[i + 1]).treeStructureChanged(e);
			}
		}
	}

	//
	// Default impelmentations for methods in the ITreeTableModel interface.
	//

	public Class getColumnClass(int column)
	{
		return ITreeTableModel.class;
	}

	/**
	 * By default, make the column with the Tree in it the only editable one. Making this column editable causes the JTable
	 * to forward mouse and keyboard events in the Tree column to the underlying JTree.
	 */
	public boolean isCellEditable(Object node, int column)
	{
		return getColumnClass(column) == ITreeTableModel.class;
	}

	public void setValueAt(Object aValue, Object node, int column)
	{
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChild(java.lang.Object, int)
	 */
	public Object getChild(Object parent, int index)
	{
		return ((TreeNode) parent).getChildAt(index);
	}

	/**
	 * @see javax.swing.tree.TreeModel#getChildCount(java.lang.Object)
	 */
	public int getChildCount(Object parent)
	{
		return ((TreeNode) parent).getChildCount();
	}

	/**
	 * @see chs.utilities.ui.table.ITreeTableModel#renameNode(java.lang.Object, java.lang.String)
	 */
	public void renameNode(Object node, String name)
	{
		// Read Only tree, can't rename nodes!
	}

	// Left to be implemented in the subclass:

	/*
	 *   public int getColumnCount()
	 *   public String getColumnName(Object node, int column)
	 *   public Object getValueAt(Object node, int column)
	 */

	/**
	 * Builds the parents of node up to and including the root node, where the original node is the last element in the
	 * returned array. The length of the returned array gives the node's depth in the tree.
	 *
	 * @param aNode the TreeNode to get the path for
	 */
	public TreeNode[] getPathToRoot(TreeNode aNode)
	{
		return getPathToRoot(aNode, 0);
	}

	/**
	 * Builds the parents of node up to and including the root node, where the original node is the last element in the
	 * returned array. The length of the returned array gives the node's depth in the tree.
	 *
	 * @param aNode the TreeNode to get the path for
	 * @param depth an int giving the number of steps already taken towards the root (on recursive calls), used to size the
	 * returned array
	 *
	 * @return an array of TreeNodes giving the path from the root to the specified node
	 */
	protected TreeNode[] getPathToRoot(TreeNode aNode, int depth)
	{
		TreeNode[] retNodes;
		// This method recurses, traversing towards the root in order
		// size the array. On the way back, it fills in the nodes,
		// starting from the root and working back to the original node.

		/* Check for null, in case someone passed in a null node, or
				   they passed in an element that isn't rooted at root. */
		if (aNode == null) {
			if (depth == 0) {
				return null;
			}
			else {
				retNodes = new TreeNode[depth];
			}
		}
		else {
			depth++;
			if (aNode == root) {
				retNodes = new TreeNode[depth];
			}
			else {
				retNodes = getPathToRoot(aNode.getParent(), depth);
			}
			retNodes[retNodes.length - depth] = aNode;
		}
		return retNodes;
	}

	public void cleanup()
	{
		root = null;
		listenerList = null;
	}
}
