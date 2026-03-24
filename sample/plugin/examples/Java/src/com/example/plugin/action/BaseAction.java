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
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXAction;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Window;
import java.net.URL;

/**
 * Base class for actions used as examples.
 *
 * @author Richard M D Joseph.
 */
public class BaseAction extends BasePlugin
{

	/**
	 * The icon used by all the example custom actions.
	 */
	private static Icon smallIcon = null;

	/**
	 * Protected constructor used to ensure that the use of this class is via extension only.
	 *
	 * @param n - the name of the example custom action.
	 * @param v - the version of the example custom action.
	 * @param d - the description of the example custom action.
	 */
	protected BaseAction(String n, String v, String d)
	{
		super(n, v, d);
	}

	/**
	 * @return returns null so example custom actions will not have a Mnemonic Key by default unless this method is
	 *         overriden.
	 */
	public Integer getMnemonicKey()
	{
		return null;
	}

	/**
	 * @return returns MainMenu & ContextMenu so example custom actions will appear on the main menu and the custom menu
	 *         (unless this method is overriden).
	 */
	public IXAction.Trigger[] getTriggers()
	{
		return new IXAction.Trigger[]{IXAction.Trigger.MainMenu, IXAction.Trigger.ContextMenu};
	}

	/**
	 * @param context - the IXApplicationContext
	 *
	 * @return true, when objects are selected so that example custom actions will only be available when objects are
	 *         selected (unless this method is overriden).
	 */
	public boolean isAvailable(IXApplicationContext context)
	{
		return !context.getSelectedObjects().isEmpty();
	}

	/**
	 * @return the description that appears when the example custom action is activated will be the same as the description
	 *         of the plugin.
	 */
	public String getLongDescription()
	{
		return getDescription();
	}

	/**
	 * @return the icon to use for the plugin. This will be the same icon for all the example custom actions.
	 */
	public Icon getSmallIcon()
	{
		if (smallIcon != null) {
			return smallIcon;
		}

		final URL iconURL = getClass().getResource("icon1.gif");
		if (iconURL != null) {
			smallIcon = new ImageIcon(iconURL);
		}
		else {
			System.err.println("Could not load image :-(");
		}

		return smallIcon;
	}

	/**
	 * @return true - by default, all the example actions are read-only (they read information from the design but do not
	 *         modify any properties/attributes.
	 */
	public boolean isReadOnly()
	{
		return true;
	}

	private static Point getCenterLocation(Window frame, Window dialog)
	{

		Point frameLoc;
		Dimension frameSize = frame.getSize();
		try {
			frameLoc = frame.getLocationOnScreen();
		}
		catch (IllegalComponentStateException e) {
			frameLoc = new Point(0, 0);
		}
		Dimension dialogSize = dialog.getSize();

		//
		// Get the mid point, then work out where this dialog should go.
		// Note, This won't take into account times when the dialog is larger
		// than the screen, mind you, if it *is* then you've got other problems.
		//
		// As per the User Experience style guide, determine the y location based
		// on the Golden Mean, which will position the dialog slightly higher than
		// the center of the frame.
		int mx = Math.max(10, (frameSize.width - dialogSize.width) / 2);
		int my = Math.max(10, ((int) (frameSize.height / 1.618) - dialogSize.height) / 2);
		frameLoc.setLocation(frameLoc.x + mx, frameLoc.y + my);
		return frameLoc;
	}

	/**
	 * @param dialog Normally will be JDialog, or a subclass such as OKCancelDialog, but may be any Window subclass
	 */
	public static void centerWindow(Window dialog)
	{
		Point frameLoc = getCenterLocation(dialog.getOwner(), dialog);
		dialog.setLocation(frameLoc.x, frameLoc.y);
	}
}
