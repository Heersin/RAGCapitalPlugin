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

package com.example.plugin.action;

import com.example.plugin.BasePlugin;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXLogicAction;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Displays the properties/attributes of the selected objects in a GUI dialog in TREE.
 */
public class PropertyViewerTreeAction extends BaseAction implements IXLogicAction, IXIntegratorAction, IXHarnessAction
{

	public PropertyViewerTreeAction()
	{
		super("Property Tree Viewer",
				"1.0",
				"Displays the properties of selected objects in a tree");
	}

	/**
	 * This 'execute' method will construct a JDialog that contains a tree of all the properties/attributes for the
	 * currently selected objects.
	 *
	 * @param context - the IXApplicationContext from which the currently selected objects can be obtained.
	 *
	 * @return true, as this Custom Action never fails :-)
	 */
	public boolean execute(IXApplicationContext context)
	{
		// Create the dialog using the name of the action as the title of the dialog (recommended).
		// You should use a modal 'JDialog' rather than a non-modal 'JDialog' or a normal 'JFrame'.
		JDialog dialog = new JDialog(context.getParentFrame(), true);
		dialog.setTitle(getName());

		// Create the root of the tree.
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Selected Objects");

		for (IXObject xObject : context.getSelectedObjects()) {

			// Create an node that is a child of the root node that holds the name of the object.
			DefaultMutableTreeNode object = new DefaultMutableTreeNode(BasePlugin.getObjectName(xObject));
			root.add(object);

			// Foreach attribute, add nodes that display the attribute name & value and add them
			// as children of the 'object' node.
			for (IXValue value : xObject.getAttributes()) {
				if (!isEmpty(value.getValue())) {
					DefaultMutableTreeNode attribute =
							new DefaultMutableTreeNode(value.getName() + " = " + value.getValue());
					object.add(attribute);
				}
			}

			// Foreach property, add nodes that display the property name & value and add them
			// as children of the 'object' node.
			for (IXValue value : xObject.getProperties()) {
				if (!isEmpty(value.getValue())) {
					DefaultMutableTreeNode property =
							new DefaultMutableTreeNode(value.getName() + " = " + value.getValue());
					object.add(property);
				}
			}
		}

		// Create the JTree.
		JTree tree = new JTree();
		tree.setModel(new DefaultTreeModel(root));

		// Add the JTree to JScrollPane.
		JScrollPane pane = new JScrollPane(tree);

		// Add the JScrollPane to the  JDialog.
		dialog.getContentPane().add(pane);

		// Display the JDialog.
		dialog.pack();
		centerWindow(dialog);
		dialog.setVisible(true);

		// We should only get here when the JDialog is closed.
		return true;
	}
}
