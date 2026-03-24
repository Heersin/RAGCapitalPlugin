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

import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.IXOutputWindow;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXLogicAction;

import java.util.Set;

/**
 * Increments a 'counter' property on a selected connector.
 * <p/>
 * This action will only be available if exactly one connector is selected on a diagram in Capital Logic.
 *
 * @see DeviceCounterIncrementAction
 */
public class ConnectorCounterIncrementAction extends BaseAction implements IXLogicAction, IXIntegratorAction
{

	public ConnectorCounterIncrementAction()
	{
		super("Connector Counter Increment",
				"1.0",
				"Increments a property on an IXConector object.");
	}

	/**
	 * @return false - this example action modifies properties/attributes on objects.
	 */
	public boolean isReadOnly()
	{
		return false;
	}

	/**
	 * This method will return the selected connector. It will return null if the number of objects selected is not 1 and
	 * that selected objects is NOT a connector.
	 * <p/>
	 * You can use a variation of this method if your own custom action needs an object of a particular type selected.
	 * <p/>
	 *
	 * @param context - the IXApplicationContext from which the selected objects can be obtained.
	 *
	 * @return the selected IXConnector
	 *
	 * @see DeviceCounterIncrementAction for an example that requires a device.
	 */
	protected IXConnector getSelectedConnector(IXApplicationContext context)
	{
		// Get the currently selected objects and ensure that only one
		// object is selected
		final Set<IXObject> selectedObjects = context.getSelectedObjects();
		if (selectedObjects.size() != 1) {
			return null;
		}

		// return the object that is selected if that object is
		// a connector.
		for (IXObject selectedObject : selectedObjects) {
			if (selectedObject instanceof IXConnector) {
				return (IXConnector) selectedObject;
			}
		}
		return null;
	}

	/**
	 * This implementation of the 'IXAction.isAvailable' method will only return true if a single conntector is selected.
	 *
	 * @param context - the IXApplicationContext from which the selected objects can be obtained.
	 *
	 * @return true if a single connector is selected.
	 */
	public boolean isAvailable(IXApplicationContext context)
	{
		return getSelectedConnector(context) != null;
	}

	/**
	 * This is the 'execute' method. This method will increment a counter on the selected connector.
	 *
	 * @param context - the IXApplicationContext from which the selected objects can be obtained.
	 *
	 * @return true if the changes stored in the IXAttributeSetter object should be applied, false otherwise (see code
	 *         comments).
	 */
	public boolean execute(IXApplicationContext context)
	{
		// Get the IXOutputWindow so you can log messages.
		final IXOutputWindow outputWindow = context.getOutputWindow();

		// Get the selected connector.
		final IXConnector selectedConnector = getSelectedConnector(context);
		if (selectedConnector == null) {
			outputWindow.println("ERROR: connector not selected!");
			return false;
		}

		// Get the object that is used to record the changes to be applied to the connector
		// if, and only if, this method returns true.
		// Note that this obtains a specific IXAttributeSetter object for the current 'selectedConnector'.
		// You should obtain a specific IXAttributeSetter for each of the objects you want to modify.
		// Moreover, you MUST return 'false' from 'IXAction.isReadOnly()' otherwise you will not get an
		// 'IXAttributeSetter' object.
		final IXAttributeSetter attributeSetter = selectedConnector.getAttributeSetter();
		if (attributeSetter == null) {
			outputWindow.println("ERROR: not in read/write mode. Can't change: " + selectedConnector);
			return false;
		}

		// Get the current value of the 'counter' property.
		final String propertyName = "counter";
		String valueStr = selectedConnector.getProperty(propertyName);

		if (valueStr == null) {
			// If the property does not exist already, then create & initialize it to 0
			attributeSetter.addProperty("counter", "0");

			// Note that at this point, no changes to the properties/attributes on 'selectedConnector'
			// have been made. The changes are being stored in the IXAttributeSetter to be applied
			// if and only if this method returns true.
			valueStr = selectedConnector.getProperty(propertyName);
			if (valueStr == null) {
				outputWindow.println("NOTE: no change made yet to: " + selectedConnector.toHTML());
			}
		}
		else {
			// If the property does exist, then increment its value.
			final int valueInt = Integer.parseInt(valueStr);
			attributeSetter.addProperty("counter", String.valueOf(valueInt + 1));

			// Note that at this point, no changes to the properties/attributes on 'selectedConnector'
			// have been made. The changes are being stored in the IXAttributeSetter to be applied
			// if and only if this method returns true.
			valueStr = selectedConnector.getProperty(propertyName);
			if (valueInt == Integer.parseInt(valueStr)) {
				outputWindow.println("NOTE: no change made yet to: " + selectedConnector.toHTML());
			}
		}

		// When we return true, the changes that are stored in all the IXAttributeSetter objects used
		// will be applied.
		outputWindow.println("NOTE: changes stored in IXAttributeSetter will be applied to " +
				selectedConnector.toHTML() + " as this method returned true.");
		return true;
	}
}
