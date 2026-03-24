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

import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.plugin.IXApplicationContext;

import javax.swing.JScrollPane;

/**
 * This is the attribute editing dialog with [OK] and [CANCEL] buttons. Any chnages that were made to attributes will
 * only be done if the [OK] button is pressed.
 */
class AttributeEditingDialog extends OkCancelDialog
{

	/**
	 * The table attributes.
	 */
	protected AttributeEditingTable table;

	/**
	 * Constructor
	 *
	 * @param action - the action
	 * @param context - the context containing the selection.
	 */
	protected AttributeEditingDialog(ChangeAttributesAction action, IXApplicationContext context)
	{
		super(action, context);
		final IXWriteableObject writeableObject = action.getSelectedWriteableObject(context);

		AttributeEditingTable.AttributeTableModel model = new AttributeEditingTable.AttributeTableModel(writeableObject);
		table = new AttributeEditingTable(model);
		JScrollPane scrollPane = new JScrollPane(table);
		addContent(scrollPane);

		String title = getTitle() + " on " + writeableObject;
		if (table.isReadOnly()) {
			title += " (READ-ONLY)";
		}
		setTitle(title);
	}

	/**
	 * This method is overriden to ensure that if the [OK] button is pressed when an attribute is still being edited, that
	 * attribute change is still recogonized.
	 */
	protected void okButtonPressed()
	{
		table.stopCellEditing();
		super.okButtonPressed();
	}
}
