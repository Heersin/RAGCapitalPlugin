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
import com.mentor.chs.plugin.IXApplicationContext;

import javax.swing.JPanel;

/**
 */
public class BasePanel extends BasePlugin
{

	protected JPanel panel;

	/**
	 * Protected constructor used to ensure that the use of this class is via extension only.
	 *
	 * @param n - the name of the example custom action.
	 * @param v - the version of the example custom action.
	 * @param d - the description of the example custom action.
	 */
	protected BasePanel(String n, String v, String d)
	{
		super(n, v, d);
		panel = new JPanel();
		panel.setName("InspectorPanel - " + n);
	}

	/**
       * Unlike IXAction.isAvailable() - this method should not return a value that is dependent on the selection
       * set. Inspectors dynamically redisplay data based on the selection set so they should always be available
       * to accept different selections.
       * <p/>
       *
	 * @param context - the IXApplicationContext
	 *
	 * @return true
	 */
	public boolean isAvailable(IXApplicationContext context)
	{
		return true;
	}

	public void destroy()
	{
		panel = null;
		panel = new JPanel();
	}
}
