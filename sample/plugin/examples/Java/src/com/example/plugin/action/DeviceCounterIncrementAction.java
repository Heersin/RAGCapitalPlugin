/*
 * Copyright 2007 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.   
 */

package com.example.plugin.action;

import com.mentor.chs.api.IXAbstractDevice;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.IXOutputWindow;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXLogicAction;

import java.util.Set;

/**
 * Increments a 'counter' property on a selected device.
 * <p/>
 * This action will only be available if exactly one device is selected on a diagram in Capital Logic.
 *
 * @see ConnectorCounterIncrementAction
 */
public class DeviceCounterIncrementAction extends BaseAction implements IXLogicAction, IXIntegratorAction
{

	public DeviceCounterIncrementAction()
	{
		super("Device Counter Increment",
				"1.0",
				"Increments a property on an IXDevice object.");
	}

	/**
	 * @return false - this example action modifies properties/attributes on objects.
	 */
	public boolean isReadOnly()
	{
		return false;
	}

	/**
	 * This method will return the selected device. It will return null if the number of objects selected is not 1 and that
	 * selected objects is NOT a device.
	 * <p/>
	 * You can use a variation of this method if your own custom action needs an object of a particular type selected.
	 * <p/>
	 *
	 * @param context - the IXApplicationContext from which the selected objects can be obtained.
	 *
	 * @return the selected IXDevice
	 *
	 * @see ConnectorCounterIncrementAction for an example that requires a connector.
	 */
	protected IXAbstractDevice getSelectedDevice(IXApplicationContext context)
	{
		final Set<IXObject> selectedObjects = context.getSelectedObjects();
		if (selectedObjects.size() != 1) {
			return null;
		}
		for (IXObject selectedObject : selectedObjects) {
			if (selectedObject instanceof IXAbstractDevice) {
				return (IXAbstractDevice) selectedObject;
			}
		}
		return null;
	}

	/**
	 * This implementation of the 'IXAction.isAvailable' method will only return true if a single device is selected.
	 *
	 * @param context - the IXApplicationContext from which the selected objects can be obtained.
	 *
	 * @return true if a single device is selected.
	 */
	public boolean isAvailable(IXApplicationContext context)
	{
		return getSelectedDevice(context) != null;
	}

	/**
	 * This is the 'execute' method. This method will increment a counter on the selected device.
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

		// Get the selected device.
		final IXAbstractDevice selectedDevice = getSelectedDevice(context);
		if (selectedDevice == null) {
			outputWindow.println("ERROR: device not selected!");
			return false;
		}

		// Get the object that is used to record the changes to be applied to the device
		// if, and only if, this method returns true.
		// Note that this obtains a specific IXAttributeSetter object for the current 'selectedDevice'.
		// You should obtain a specific IXAttributeSetter for each of the objects you want to modify.
		// Moreover, you MUST return 'false' from 'IXAction.isReadOnly()' otherwise you will not get an
		// 'IXAttributeSetter' object.
		final IXAttributeSetter attributeSetter = selectedDevice.getAttributeSetter();
		if (attributeSetter == null) {
			outputWindow.println("ERROR: not in read/write mode. Can't change: " + selectedDevice);
			return false;
		}

		// Get the current value of the 'counter' property.
		final String propertyName = "counter";
		String valueStr = selectedDevice.getProperty(propertyName);

		if (valueStr == null) {
			// If the property does not exist already, then create & initialize it to 0
			attributeSetter.addProperty("counter", "0");

			// Note that at this point, no changes to the properties/attributes on 'selectedDevice'
			// have been made. The changes are being stored in the IXAttributeSetter to be applied
			// if and only if this method returns true.
			valueStr = selectedDevice.getProperty(propertyName);
			if (valueStr == null) {
				outputWindow.println("NOTE: no change made yet to: " + selectedDevice.toHTML());
			}
		}
		else {
			// If the property does exist, then increment its value.
			final int valueInt = Integer.parseInt(valueStr);
			attributeSetter.addProperty("counter", String.valueOf(valueInt + 1));

			// Note that at this point, no changes to the properties/attributes on 'selectedDevice'
			// have been made. The changes are being stored in the IXAttributeSetter to be applied
			// if and only if this method returns true.
			valueStr = selectedDevice.getProperty(propertyName);
			if (valueInt == Integer.parseInt(valueStr)) {
				outputWindow.println("NOTE: no change made yet to: " + selectedDevice.toHTML());
			}
		}

		// When we return true, the changes that are stored in all the IXAttributeSetter objects used
		// will be applied.
		outputWindow.println("NOTE: changes stored in IXAttributeSetter will be applied to " +
				selectedDevice.toHTML() + " as this method returned true.");
		return true;
	}
}
