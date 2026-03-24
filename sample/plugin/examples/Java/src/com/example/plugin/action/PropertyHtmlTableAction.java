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
import com.mentor.chs.api.IXValue;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXLogicAction;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Displays the properties/attributes of the selected objects in a HTML TABLE in the output window of the Capital Logic,
 * Capital Integrator, Capital Design and Capital HarnessXC applications.
 */
public class PropertyHtmlTableAction extends BaseAction implements IXLogicAction, IXIntegratorAction, IXHarnessAction
{

	public PropertyHtmlTableAction()
	{
		super("Property HTML Table",
				"1.0",
				"Displays the properties of selected objects in a table in the output window");
	}

	/**
	 * @return the 'H' in 'Property HTML Table' will be the mnemonic key used.
	 */
	public Integer getMnemonicKey()
	{
		return (int) 'H';
	}

	/**
	 * This 'execute' method will display the properties/attributes of selected objects in a HTML table in the output
	 * window.
	 * <p/>
	 * Note that we are passing in the name of this plugin as a parameter to the methods on IXOutputWindow.
	 * <p/>
	 * As a result, in the OutputWindow, we should see a new tab called 'Property HTML Table' (the name of this plugin)
	 * appear and the HTML table will be displayed in that tab.
	 * <p/>
	 *
	 * @param context - the IXApplicationContext from which the selected objects can be obtained.
	 *
	 * @return true
	 */
	public boolean execute(IXApplicationContext context)
	{
		// Create the PropertyTable object from the selected objects.
		final PropertyTable propertyTable = new PropertyTable(context.getSelectedObjects());

		// Ensure that the last results displayed in the output window is cleared.
		context.getOutputWindow().clear(getName());

		// To display HTML, we MUST ensure that the start & end TAGs for this HTML constructs used
		// are sent to the OutputWindow in a single 'println'. This will ensure that the HTML
		// tags are handled correctly.
		context.getOutputWindow().println(getName(), propertyTable.toHTML());

		// Here is an example that creates a link to an external WebPage. 
		context.getOutputWindow().println(getName(),
				"To open your browser on the Mentor WebPage, please click <A href=\"http://www.mentor.com\">here</A>");

		// This action was successful so we return true.
		return true;
	}

	/**
	 * This class is a table of the attributes/properties of various IXObjects.
	 */
	protected static class PropertyTable
	{

		/**
		 * This is the name of the attribute that can be used to get the name of an IXObject.
		 */
		protected static final String nameAttrib = "NAME";

		/**
		 * This is the table information. All information in the table is stored as strings.
		 */
		protected List<List<String>> rowList = new ArrayList<List<String>>();

		/**
		 * The columns for the table.
		 */
		protected List<String> columnList = new ArrayList<String>();

		/**
		 * Get the value to be stored in the table for the given IXObject and the specified 'column'.
		 *
		 * @param xObject - the
		 * @param column - the column (or attribute/property name).
		 *
		 * @return the string (could contain HTML tags).
		 */
		protected String getValue(IXObject xObject, String column)
		{
			final String a = xObject.getAttribute(column);
			if (a != null) {
				// Attributes like name will have a clickable HTML link that will select the object
				// when clicked.
				return xObject.toHTML(a);
			}

			final String p = xObject.getProperty(column);
			if (p != null) {
				// Properties will not have clickable HTML links.
				return p;
			}

			// We return a blank string if the specified column does not apply to the IXObject provided.
			return "";
		}

		/**
		 * @param objectSet - the set of objects for which the table should be built.
		 */
		protected PropertyTable(Set<IXObject> objectSet)
		{
			// Collect the unique set of all the possible attribute and property names.
			final Set<String> possibleAttributePropertyNames = new TreeSet<String>();
			for (IXObject xObject : objectSet) {
				for (IXValue value : xObject.getAttributes()) {
					possibleAttributePropertyNames.add(value.getName());
				}
				for (IXValue value : xObject.getProperties()) {
					possibleAttributePropertyNames.add(value.getName());
				}
			}

			// Add the possible attribute and property name as columns for the table
			// ensuring that the name of the property/attribute is in the first column.
			possibleAttributePropertyNames.remove(nameAttrib);
			columnList.add(nameAttrib);
			columnList.addAll(possibleAttributePropertyNames);

			// Construct the table, inserting the correct value in the right column.
			for (IXObject xObject : objectSet) {
				List<String> currentRow = new ArrayList<String>();
				for (String column : columnList) {
					currentRow.add(getValue(xObject, column));
				}
				rowList.add(currentRow);
			}
		}

		/**
		 * String a string that is the HTML representation of the table information.
		 *
		 * @return the string with the appropriate HTML tags.
		 */
		protected String toHTML()
		{
			final StringWriter html = new StringWriter();
			PrintWriter htmlPage = null;
			try {
				htmlPage = new PrintWriter(html);

				// Begin the table.
				htmlPage.println(
						"<TABLE BORDER=\"1\" CELLPADDING=\"2\"  CELLSPACING=\"0\" BORDERCOLOR=\"#000000\" BORDERCOLORDARK=\"#000000\" BORDERCOLORLIGHT=\"#000000\" WIDTH=\"100%\">");

				// Insert the first row containing all the column data.
				htmlPage.println("<TR BGCOLOR=\"#CCCCFF\">");
				for (String column : columnList) {
					htmlPage.println("<TD VALIGN=\"TOP\">");
					htmlPage.println("<FONT SIZE=\"-1\">");
					htmlPage.println("<B>");
					htmlPage.println(column);
					htmlPage.println("</B>");
					htmlPage.println("</FONT>");
					htmlPage.println("</TD>");
				}
				htmlPage.println("</TR>");

				// Insert the rows containing the data.
				for (List<String> row : rowList) {
					htmlPage.println("<TR>");
					for (String item : row) {
						htmlPage.println("<TD VALIGN=\"TOP\">");
						htmlPage.println("<FONT SIZE=\"-2\">");
						htmlPage.println(item);
						htmlPage.println("</FONT>");
						htmlPage.println("</TD>");
					}
					htmlPage.println("</TR>");
				}

				// End the table.
				htmlPage.println("</TABLE>");
			}
			finally {
				if (htmlPage != null) {
					htmlPage.close();
				}
			}

			return html.getBuffer().toString();
		}
	}
}
