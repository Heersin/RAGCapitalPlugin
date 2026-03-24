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

import com.mentor.chs.api.IXHarnessDiagramBundle;
import com.mentor.chs.api.IXObject;

/**
 * This Custom action provides an alternate mechanism for editing graphical attributes on harness Bundle. It
 * demonstrates how to create a dialog that changes attributes only when the [OK] button is pressed.
 */
public class ChangeHarnessBundleGraphicalAttributesAction extends ChangeAttributesAction
{

	/**
	 * Constructor.
	 */
	public ChangeHarnessBundleGraphicalAttributesAction()
	{
		super("Change harness bundle graphical attributes",
				"1.0",
				"Change harness bundle graphical attributes");
	}

	@Override protected boolean isObjectAcceptable(IXObject selectedObject)
	{
		return selectedObject instanceof IXHarnessDiagramBundle;
	}
}

