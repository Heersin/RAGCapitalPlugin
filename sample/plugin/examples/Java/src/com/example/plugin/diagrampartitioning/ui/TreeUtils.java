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
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;

public class TreeUtils
{

	private TreeUtils()
	{
	}

	/**
	 * Checks if the specified keyevent is one that a tree may be interested in.
	 *
	 * @param e the event
	 *
	 * @return true if we are interested...
	 */
	public static boolean isTreeKeyEvent(KeyEvent e)
	{
		switch (e.getKeyCode()) {
			case KeyEvent.VK_DIVIDE:
			case KeyEvent.VK_SLASH:
			case KeyEvent.VK_MINUS:
			case KeyEvent.VK_SUBTRACT:
			case KeyEvent.VK_ASTERISK:
			case KeyEvent.VK_MULTIPLY:
			case KeyEvent.VK_PLUS:
			case KeyEvent.VK_ADD:
				return true;
			default:
				return false;
		}
	}

	// If expand is true, expands all nodes in the tree.
	// Otherwise, collapses all nodes in the tree.
	public static void expandAll(JTree tree, boolean expand)
	{
		TreeNode root = (TreeNode) tree.getModel().getRoot();

		// Traverse tree from root
		expandAll(tree, new TreePath(root), expand);
	}

	private static void expandAll(JTree tree, TreePath parent, boolean expand)
	{
		// Traverse children
		TreeNode node = (TreeNode) parent.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				TreeNode n = (TreeNode) e.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path, expand);
			}
		}

		// Expansion or collapse must be done bottom-up
		if (expand) {
			tree.expandPath(parent);
		}
		else {
			tree.collapsePath(parent);
		}
	}

	/**
	 * Get a list of all the currently expanded paths in the tree. The returned list may be modified by the caller.
	 *
	 * @param tree the tree
	 *
	 * @return an unmodifiable list of all currently expanded tree paths.
	 */
	public static List<TreePath> getExpandedPaths(JTree tree)
	{
		TreePath path = new TreePath(tree.getModel().getRoot());
		Enumeration<TreePath> descendants = tree.getExpandedDescendants(path);
		if (descendants == null) {
			return Collections.emptyList();
		}
		else {
			return Collections.unmodifiableList(Collections.list(descendants));
		}
	}

	/**
	 * Expand only the specified tree paths.
	 *
	 * @param tree the tree..
	 * @param expandedPaths A list of {@link TreePath}, such as that returned from {@link #getExpandedPaths}
	 */
	public static void expandPaths(JTree tree, List<TreePath> expandedPaths)
	{
		for (TreePath path : expandedPaths) {
			tree.expandPath(path);
		}
	}

	/**
	 * Creates a tree path as a list of Objects from the given Tree and Tree Node using the string list to indicate the
	 * tree path (by name)
	 *
	 * @param aTreeModel The tree to traverse
	 * @param aTreeModelNode The starting node to traverse from
	 * @param aStringList The tree path by name
	 * @param aTreePath The tree path by object
	 */
	public static void createTreePath(TreeModel aTreeModel, Object aTreeModelNode, List<String> aStringList,
			List<Object> aTreePath)
	{
		createTreePathOptionalExpand(null, aTreeModel, aTreeModelNode, aStringList, aTreePath);
	}

	/**
	 * Creates a tree path as a list of Objects from the given Tree and Tree Node using the string list to indicate the
	 * tree path (by name)
	 *
	 * @param aTree If non-null will expand the paths as they are created
	 * @param aTreeModel The tree model to traverse
	 * @param aTreeModelNode The starting node to traverse from
	 * @param aStringList The tree path by name
	 * @param aTreePath The tree path by object
	 */
	public static void createTreePathOptionalExpand(JTree aTree, TreeModel aTreeModel, Object aTreeModelNode,
			List<String> aStringList, List<Object> aTreePath)
	{
		if (aStringList.isEmpty() || aTreeModel == null || aTreeModelNode == null || aTreePath == null) {
			return;
		}
		String nodeString = aTreeModelNode.toString();
		if (nodeString != null && nodeString.equals(aStringList.get(0))) {
			aTreePath.add(aTreeModelNode);
			if (aTree != null) {
				aTree.expandPath(createTreePath(aTreePath));
			}
			aStringList.remove(0);
		}
		int numChildren = aTreeModel.getChildCount(aTreeModelNode);
		for (int index = 0; index < numChildren; ++index) {
			createTreePathOptionalExpand(aTree, aTreeModel, aTreeModel.getChild(aTreeModelNode, index), aStringList,
					aTreePath);
		}
	}

	/**
	 * Creates a TreePath for a given model using a List<String> to represent the path, searching for the path by name.
	 *
	 * @param aTreeModel The model to create a TreePath for
	 * @param aStringList The tree path as a List<String>, path defined by node name(string value)
	 *
	 * @return TreePath The Swing TreePath object
	 */
	public static TreePath createTreePath(TreeModel aTreeModel, List<String> aStringList)
	{
		return createTreePathOptionalExpand(null, aTreeModel, aStringList);
	}

	/**
	 * Creates a TreePath for a given model using a List<String> to represent the path, searching for the path by name.
	 *
	 * @param aTree If non-null, TreePaths will be expanded on the tree as they are created
	 * @param aTreeModel The model to create a TreePath for
	 * @param aStringList The tree path as a List<String>, path defined by node name(string value)
	 *
	 * @return TreePath The Swing TreePath object
	 */
	public static TreePath createTreePathOptionalExpand(JTree aTree, TreeModel aTreeModel,
			List<String> aStringList)
	{
		if (aTreeModel == null) {
			return null;
		}
		List<Object> treePath = new ArrayList<Object>();
		createTreePathOptionalExpand(aTree, aTreeModel, aTreeModel.getRoot(), aStringList, treePath);
		if (treePath.isEmpty()) {
			return null;
		}
		return createTreePath(treePath);
	}

	/**
	 * Creates a TreePath from a List<Object>
	 *
	 * @param aTreePath List of Object to create TreePath from
	 *
	 * @return TreePath The Swing TreePath object
	 */
	public static TreePath createTreePath(List<Object> aTreePath)
	{
		Object[] treePathArray = new Object[aTreePath.size()];
		treePathArray = aTreePath.toArray(treePathArray);
		return new TreePath(treePathArray);
	}

	/**
	 * Select nodes in the given JTree from a List<List<String>>, it's a list of tree paths in List<String> form.
	 *
	 * @param aTree The tree to select in
	 * @param aListList The tree paths in List<String> form
	 */

	public static void selectTreeFromListList(JTree aTree, List<List<String>> aListList)
	{
		Collection<TreePath> selectionPaths = convertListListToTreePaths(aTree, aListList);
		if (selectionPaths.isEmpty()) {
			return;
		}
		TreeSelectionModel treeSelectionModel = aTree.getSelectionModel();
		if (treeSelectionModel != null) {
			TreePath[] treePaths = new TreePath[selectionPaths.size()];
			treePaths = selectionPaths.toArray(treePaths);
			if (treePaths.length > 0) {
				treeSelectionModel.setSelectionPaths(treePaths);
				aTree.scrollPathToVisible(treePaths[treePaths.length - 1]);
			}
		}
	}

	/**
	 * Expand nodes in the given JTree from a List<List<String>>, it's a list of tree paths in List<String> form.
	 *
	 * @param aTree The tree to expand in
	 * @param aListList The tree paths in List<String> form
	 */
	public static void expandTreeFromListList(JTree aTree, List<List<String>> aListList)
	{
		// Convert and expand, ignore return value
		convertListListToTreePathsOptionalExpand(aTree, aListList, true);
	}

	/**
	 * Converts a list of list of strings to a List of TreePath
	 *
	 * @param aTree Tree to create paths for
	 * @param aListList Tree paths in string format
	 *
	 * @return List<TreePath> List of TreePaths created from aListList
	 */
	public static List<TreePath> convertListListToTreePaths(JTree aTree, List<List<String>> aListList)
	{
		return convertListListToTreePathsOptionalExpand(aTree, aListList, false);
	}

	/**
	 * Converts a list of list of strings to a List of TreePath, optionally expands the paths in the process needed for
	 * clients that want to restore an expansion state but where the nodes are created on demand!
	 *
	 * @param aTree Tree to create paths for
	 * @param aListList Tree paths in string format
	 * @param aExpand Flag to expand the paths as they are created
	 *
	 * @return List<TreePath> List of TreePaths created from aListList
	 */
	public static List<TreePath> convertListListToTreePathsOptionalExpand(JTree aTree, List<List<String>> aListList,
			boolean aExpand)
	{
		List<TreePath> treePaths = new ArrayList<TreePath>();
		if ((aTree == null) || (aListList == null)) {
			return treePaths;
		}
		for (List<String> stringList : aListList) {
			TreePath treePath = createTreePathOptionalExpand(aExpand ? aTree : null, aTree.getModel(), stringList);
			if (treePath != null) {
				treePaths.add(treePath);
			}
		}
		return treePaths;
	}

	public static DefaultMutableTreeNode getNodeForObject(DefaultMutableTreeNode rootNode, Object objectToFind,
			Comparator<Object> c)
	{
		Enumeration<?> nodesEnum = rootNode.depthFirstEnumeration();
		while (nodesEnum.hasMoreElements()) {
			DefaultMutableTreeNode bleh = (DefaultMutableTreeNode) nodesEnum.nextElement();
			if (c.compare(bleh, objectToFind) == 0) {
				return bleh;
			}
		}
		return null;
	}

	/**
	 * Get a TreePath for a MutableTreeNode. {@link #getPath(TreeNode node, TreeModel treeModel)} is a slightly more
	 * flexible method
	 *
	 * @param node the node of interest
	 *
	 * @return the path to the node
	 *
	 * @see chs.utilities.ui.tree.BaseJTree#getPath(TreeNode node)
	 * @see #getPath(TreeNode node, TreeModel treeModel)
	 */
	public static TreePath getPath(DefaultMutableTreeNode node)
	{
		return new TreePath(node.getPath());
	}

	/**
	 * Get the path corresponding to a node in the tree. If the node is a DefaultMutableTreeNode OR the model is a
	 * DefaultTreeModel this will work for any node in the tree.  Otherwise it will only work for the root node.
	 *
	 * @param node the node of interest
	 * @param treeModel the model of the tree we want to find.
	 *
	 * @return path to the node, or null if it's not in the tree
	 *
	 * @see #getPath(DefaultMutableTreeNode node)
	 */
	public static TreePath getPath(TreeNode node, TreeModel treeModel)
	{
		TreeNode[] path = null;
		if (node instanceof DefaultMutableTreeNode) {
			// the default mutable tree node can get it's path.
			DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode) node;
			path = dmtn.getPath();
		}
		else if (treeModel instanceof DefaultTreeModel) {
			// the default tree model can get the path of any node
			DefaultTreeModel model = (DefaultTreeModel) treeModel;
			path = model.getPathToRoot(node);
		}
		else if (node.equals(treeModel.getRoot())) {
			// only works for the root node.
			return new TreePath(node);
		}
		if (path == null) {
			return null;
		}
		return new TreePath(path);
	}

	/**
	 * Create a tree where the parent does not have vertical and horizonal lines to the children
	 *
	 * @param root the root node
	 *
	 * @return the tree
	 */
	public static JTree createJTreeWithoutLines(TreeNode root)
	{
		boolean oldPaintLines = UIManager.getBoolean("Tree.paintLines");
		try {
			UIManager.put("Tree.paintLines", false);
			return new JTree(root);
		}
		finally {
			UIManager.put("Tree.paintLines", oldPaintLines);
		}
	}
}
