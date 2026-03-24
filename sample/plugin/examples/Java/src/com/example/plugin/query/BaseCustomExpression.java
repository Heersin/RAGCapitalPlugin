package com.example.plugin.query;

import com.example.plugin.BasePlugin;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXObject;

/**
 * This is the parent class for all the example query plugins.<br> It implements the basic methods that all plugins need
 * to implement.
 * <p/>
 * It also contains some helper methods that could be useful when implementing plugins.
 * <p/>
 *
 * @author Chandra Shekhar Singh
 */
public abstract class BaseCustomExpression extends BasePlugin
{

	/**
	 * Constructor.
	 *
	 * @param n - the name of the plugin.
	 * @param v - the version string of the plugin.
	 * @param d - the description of the plugin.
	 */
	protected BaseCustomExpression(
			String n,
			String v,
			String d)
	{
		super(n, v, d);
	}

	/**
	 * Get the name of the object.
	 * <p/>
	 * The name is obtained from an attribute on the IXObject. <br>
	 *
	 * @param xObject - the object to get the name from.
	 * @param attrName: name of the attribute to get
	 *
	 * @return the name of the object, null if not found.
	 */
	public String getObjectAttribute(IXObject xObject, String attrName)
	{
		String val = null;
		if (xObject == null) {
			return val;
		}
		if (xObject instanceof IXDiagramObject) {
			val = xObject.getAttribute(attrName);
			if (val == null) {
				IXObject cObject = ((IXDiagramObject) xObject).getConnectivity();
				if (cObject != null) {
					val = cObject.getAttribute(attrName);
				}
			}
		}
		else {
			val = xObject.getAttribute(attrName);
		}
		return val;
	}

	public void LogEntry(IXObject obj)
	{
		//the below code is only used for debuging purpose so commenting it out now.
		//System.out.println("Entered in Query - " + getName() + " - for object : " + getObjectAttribute(obj, "Name"));
	}

	public void LogExit(IXObject obj, String value)
	{
		//the below code is only used for debuging purpose so commenting it out now.
		//System.out.println("Exit with value : " + value + " from Query - " + getName() + " - for object : " +
		//		getObjectAttribute(obj, "Name"));
	}
}
