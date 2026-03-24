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

import com.mentor.chs.plugin.IXOutputWindow;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXLogicAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.api.IXObject;

import javax.swing.Icon;

/**
 * This template can be used if you want to create a new Custom Action that only
 * displays information to the OutputWindow and does not open a GUI of its own.
 * <p/>
 * To use, you should:
 * <ol>
 * <li>copy the template to a new package</li>
 * <li>rename the class</li>
 * <li>add the package statement</li>
 * <li>remove implements IXLogicAction, IXIntegratorAction or IXHarnessAction depending on what application this action is for</li>
 * <li>rewrite the methods accordingly</li>
 * </ol>
 */
public class TemplateConsoleAction implements IXLogicAction, IXIntegratorAction, IXHarnessAction
{

	/**
	 * We should always have a public-default constructor for any plugin.
	 */
	public TemplateConsoleAction()
	{
	}

	/**
	 * @return this will be the long description for the custom action.
	 */
	public String getLongDescription()
	{
		return "this is the long description for: " + getClass().getName();
	}

	/**
	 * @return the mnemonic, null if not mnemonic is required.
	 */
	public Integer getMnemonicKey()
	{
		// Return null if you do not want a 'mnemonic'
		// Otherwise return (char) '<char>'
		return null;
	}

	/**
	 * Get the small icon that is accociated with this action. This icon will be displayed for the action.
	 * <p/>
	 *
	 * @return the icon, null if no icon is required.
	 */
	public Icon getSmallIcon()
	{
		// Return null if you do not want an icon,
		// otherwise return a 16x16 icon.
		return null;
	}

	/**
	 * @return true
	 */
	public boolean isReadOnly() 
	{
		// If this action only requires read only access to the design, return true.
		// This will ensure that this action is available even if the design is locked (opened read-only).
		return true;
	}

	/**
	 * @param applicationContext - the application context.
	 *
	 * @return true/false
	 */
	public boolean isAvailable(IXApplicationContext applicationContext)
	{
		// If your action is only required if a particular type of objects is selected
		// you can insert the code to do that here. Otherwise, you can just return true
		// if your action should always be available.
		return true;
	}

	/**
	 * Get the point at which this action should be triggered.
	 * <p/>
	 *
	 * @return the trigger point. This method should never return NULL.
	 */
	public Trigger [] getTriggers()
	{
		// If you do not want your menu to be on the context ment, 
		// you should remove Trigger.ContextMenu from the array. 
		return new Trigger [] { Trigger.MainMenu, Trigger.ContextMenu };
	}

	/**
	 * @param applicationContext - the application context
	 *
	 * @return true/false.
	 */
	public boolean execute(IXApplicationContext applicationContext)
	{
		// Get the IXOutputWindow so you can log messages.
		IXOutputWindow outputWindow = applicationContext.getOutputWindow();

		// Implement your action here... (note the 'optional' use of HTML tags)
		outputWindow.println("<b>Hello World!</b>");

		// Remember to return true unless something has gone wrong.
		return true;
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.IXPlugin#getDescription()
	 */
	public String getDescription()
	{
		return "This is a Template Java based Custom Action";
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.IXPlugin#getName()
	 */
	public String getName()
	{
		return "Template Console Action";
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.IXPlugin#getVersion()
	 */
	public String getVersion()
	{
		return "0.1";
	}
}
