/**
 * Copyright 2006 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin;

import com.mentor.chs.api.IXObject;

/**
 * This is the parent class for all the example plugins.<br> It implements the basic methods that all plugins need to
 * implement.
 * <p/>
 * It also contains some helper methods that could be useful when implementing plugins.
 * <p/>
 *
 * @author Richard M. D. Joseph
 */
public class BasePlugin
{

	/**
	 * The name attribute
	 */
	protected static final String ATTRIBUTE_NAME = "NAME";

	/**
	 * The wire color attribute
	 */
	protected static final String ATTRIBUTE_WIRE_COLOR = "WireColor";

	/**
	 * The part number attribute
	 */
	protected static final String ATTRIBUTE_PARTNUMBER = "PartNumber";

	/**
	 * The slit attribute
	 */
	protected static final String ATTRIBUTE_SLIT = "Slit";

	/**
	 * The name of the plugin.
	 */
	protected final String name;

	/**
	 * The version string of the plugin.
	 */
	protected final String version;

	/**
	 * The decription of the plugin.
	 */
	protected final String description;

	/**
	 * Constructor.
	 *
	 * @param n - the name of the plugin.
	 * @param v - the version string of the plugin.
	 * @param d - the description of the plugin.
	 */
	protected BasePlugin(
			String n,
			String v,
			String d)
	{
		name = n;
		version = v;
		description = d;
	}

	/* (non-Javadoc)
		 * @see com.mentor.chs.plugin.IXPlugin#getDescription()
		 */
	public String getDescription()
	{
		return description;
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.IXPlugin#getName()
	 */
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.IXPlugin#getVersion()
	 */
	public String getVersion()
	{
		return version;
	}

	/**
	 * Get the name of the object.
	 * <p/>
	 * The name is obtained from an attribute on the IXObject. <br>
	 *
	 * @param xObject - the object to get the name from.
	 *
	 * @return the name of the object, null if not found.
	 */
	public static String getObjectName(IXObject xObject)
	{
		if (xObject == null) {
			return null;
		}
		return xObject.getAttribute(ATTRIBUTE_NAME);
	}

	/**
	 * Used to check if the given string is empty or null.
	 *
	 * @param str - the given string.
	 *
	 * @return true if the string is null or empty, false otherwise.
	 */
	protected boolean isEmpty(String str)
	{
		if (str == null) {
			return true;
		}
		return "".equals(str);
	}
}
