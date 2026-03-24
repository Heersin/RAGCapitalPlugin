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

import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXLogicAction;

import java.util.Set;

/**
 * This Custom action provides an alternate template for a mechanism for editing attributes. It demonstrates how to
 * create a dialog that changes attributes only when the [OK] button is pressed.
 */
public abstract class ChangeAttributesAction extends BaseAction
		implements IXLogicAction, IXIntegratorAction, IXHarnessAction
{

	/**
	 * Protected constructor used to ensure that the use of this class is via extension only.
	 *
	 * @param n - the name of the example custom action.
	 * @param v - the version of the example custom action.
	 * @param d - the description of the example custom action.
	 */
	protected ChangeAttributesAction(String n, String v, String d)
	{
		super(n, v, d);
	}

	/**
	 * @return false - this example action modifies properties/attributes on objects.
	 */
	public boolean isReadOnly()
	{
		return false;
	}

	/**
	 * This gets the selected object that will be worked on.
	 *
	 * @param context - the context that contains the current selections.
	 *
	 * @return the object that should be worked on.
	 */
	public IXWriteableObject getSelectedWriteableObject(IXApplicationContext context)
	{
		Set<IXObject> selectedObjects = context.getSelectedObjects(IXObject.class);
		for (IXObject selectedObject : selectedObjects) {
			if (isObjectAcceptable(selectedObject)) {
				return (IXWriteableObject) selectedObject;
			}
		}
		return null;
	}

	protected abstract boolean isObjectAcceptable(IXObject selectedObject);

	/**
	 * @param context - the IXApplicationContext
	 *
	 * @return true, when objects are selected so that example custom actions will only be available when objects are
	 * selected (unless this method is overriden).
	 */
	public boolean isAvailable(IXApplicationContext context)
	{
		return getSelectedWriteableObject(context) != null;
	}

	/**
	 * This 'execute' method will construct a JDialog that contains a table of all the properties/attributes for the
	 * currently selected objects.
	 *
	 * @param applicationContext - the IXApplicationContext from which the currently selected objects can be obtained.
	 *
	 * @return true, as this Custom Action never fails :-)
	 */
	public boolean execute(IXApplicationContext applicationContext)
	{
		// Create the dialog using the name of the action as the title of the dialog (recommended).
		// You should use a modal 'JDialog' rather than a non-modal 'JDialog' or a normal 'JFrame'.
		AttributeEditingDialog dialog = new AttributeEditingDialog(this, applicationContext);

		// Display the dialog.
		dialog.displayDialog();

		// We should only get here when the JDialog is closed.
		return dialog.wasSuccessful();
	}
}
