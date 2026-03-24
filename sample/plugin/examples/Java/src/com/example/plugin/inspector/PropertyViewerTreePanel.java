/**
 * Copyright 2007 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.inspector;

import com.example.plugin.BasePlugin;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.api.event.IXSelectionChangeEvent;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.event.IXSelectionChangeListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 */
public class PropertyViewerTreePanel extends BasePanel implements IXSelectionChangeListener
{

	/**
	 * The main tree displaying the selected objects
	 */
	protected JTree tree;

	/**
	 * The Root node.  All selected objects nodes are children of this node
	 */
	protected DefaultMutableTreeNode root;

	/**
	 * The tree model which determins the behaviour of the data the tree represents
	 */
	protected DefaultTreeModel model;

	public PropertyViewerTreePanel(String n, String v, String d)
	{
		super(n, v, d);
	}

	public JPanel initialize(final IXApplicationContext applicationContext)
	{
		// Set the layout style for the new panel (constructed in BasePanel constructor)
		panel.setLayout(new BorderLayout());

		// Create a JTree.
		tree = new JTree();
		tree.setName("PropertyTreeName");

		// Create the root of the tree, the model for the root and add to the tree.
		root = new DefaultMutableTreeNode("Selected Objects");
		model = new DefaultTreeModel(root);
		tree.setModel(model);

		// Add the JTree to JScrollPane.
		JScrollPane pane = new JScrollPane(tree);
		pane.setName("TreeScrollPane");

		// Add the JScrollPane to the JDialog.
		panel.setBackground(Color.white);
		panel.add(pane, BorderLayout.CENTER);

		final JTextField txt = new JTextField();
		txt.setName("ActionURL");
		JLabel lbl = new JLabel("Action:");
		JButton btn = new JButton("Invoke");
		btn.setName("Invoke");
		JPanel actPane = new JPanel(new GridBagLayout());
		actPane.add(lbl, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(1, 0, 1, 0), 0, 0));
		actPane.add(txt, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
				, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(1, 0, 1, 0), 0, 0));
		actPane.add(btn, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
				, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(1, 0, 1, 0), 0, 0));
		btn.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				applicationContext.invokeAction(txt.getText(), e);
			}
		});
		panel.add(actPane, BorderLayout.SOUTH);

		// Return the completed panel.
		return panel;
	}

	public boolean update(IXApplicationContext applicationContext)
	{
		if (tree == null) {
			return true;
		}
//		// Send a message to the application to inform the user that the plugin is doing something.
//		applicationContext.getOutputWindow().println(getName(), "Updating Properties and Attributes of selection...");
		// Use the application context to update the tree this plugin displays
		updateTable(applicationContext);
		// Order a repaint since the tree information may have changed
		tree.repaint();
		// Send a message to the application to inform the user that the pluging has finished
//		applicationContext.getOutputWindow().println(getName(), "Update Complete.\n");
		// Update was successful
		return true;
	}

	protected void updateTable(IXApplicationContext applicationContext)
	{
		if (root == null) {
			return;
		}
		// We're going to completely rebuild the tree, so clear all it's current child nodes.
		root.removeAllChildren();

		// We are only interested in what is selected in the diagram, so loop around the selected objects
		for (IXObject xObject : applicationContext.getSelectedObjects()) {

			// Create an node that is a child of the root node that holds the name of the object.
			DefaultMutableTreeNode object = new DefaultMutableTreeNode(BasePlugin.getObjectName(xObject));
			root.add(object);

			// Send a message to the application that we have found an interesting object, and convert it to a hot
			// link so clicking on it will take you to the object
//			applicationContext.getOutputWindow().println(getName(), "   " + xObject.toHTML());

			// For each attribute add nodes that display the attribute name and value, then add them
			// as children of the 'object' node.
			for (IXValue value : xObject.getAttributes()) {
				if (!isEmpty(value.getValue())) {
					DefaultMutableTreeNode attribute =
							new DefaultMutableTreeNode(value.getName() + " = " + value.getValue());
					object.add(attribute);
				}
			}

			// For each property add nodes that display the property name and value, then add them
			// as children of the 'object' node.
			for (IXValue value : xObject.getProperties()) {
				if (!isEmpty(value.getValue())) {
					DefaultMutableTreeNode property =
							new DefaultMutableTreeNode(value.getName() + " = " + value.getValue());
					object.add(property);
				}
			}
		}

		// The data has changed, perform a reload on the model
		model.reload();

		// Fully expand every tree node so all the data is visible
		for (int i = 0; i < tree.getRowCount(); i++) {
			tree.expandRow(i);
		}
	}

	public void selectionChanged(IXApplicationContext context, IXSelectionChangeEvent xEvent)
	{
		update(context);
	}
}