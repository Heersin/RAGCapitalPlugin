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
import com.mentor.chs.plugin.IXOutputWindow;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXLogicAction;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Displays the properties/attributes of the selected objects in a GUI dialog in TABLE.
 */
public class PropertyViewerTableAction extends BaseAction implements IXLogicAction, IXIntegratorAction, IXHarnessAction
{

	public PropertyViewerTableAction()
	{
		super("Property Table Viewer",
				"1.0",
				"Displays the properties of selected objects in a table");
	}

	/**
	 * This 'execute' method will construct a JDialog that contains a table of all the properties/attributes for the
	 * currently selected objects.
	 *
	 * @param context - the IXApplicationContext from which the currently selected objects can be obtained.
	 *
	 * @return true, as this Custom Action never fails :-)
	 */
	public boolean execute(IXApplicationContext context)
	{
		// Create the dialog using the name of the action as the title of the dialog (recommended).
		// You should use a modal 'JDialog' rather than a non-modal 'JDialog' or a normal 'JFrame'.
		JDialog dialog = new JDialog(context.getParentFrame(), true);
		dialog.setTitle(getName());

		// Create the JTable using 'PropertyTableModel' as the model.
		JTable table = new JTable(new PropertyTableModel(context.getOutputWindow(), context.getSelectedObjects()));
		JScrollPane scrollPane = new JScrollPane(table);
		dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);

		// Display the dialog.
		dialog.pack();
		centerWindow(dialog);
		dialog.setVisible(true);

		// We should only get here when the JDialog is closed.
		return true;
	}

	/**
	 * This class is a table of the attributes/properties of various IXObjects. This is not an editable table model.
	 */
	protected static class PropertyTableModel extends AbstractTableModel
	{

		/**
		 * This is the table information.
		 */
		protected List<List<String>> rowList = new ArrayList<List<String>>();

		/**
		 * This is the information on the columns for the table.
		 */
		protected List<String> columnList = new ArrayList<String>();

		/**
		 * @param outputWindow - the IXOutputWindow used to display messages
		 * @param selectionSet - the currently selected object.
		 */
		protected PropertyTableModel(IXOutputWindow outputWindow, Set<IXObject> selectionSet)
		{
			// Find out all the possible attribute and property names used in the
			// currently selected objects.
			Set<String> possibleAttributePropertyNames = new TreeSet<String>();
			for (IXObject xObject : selectionSet) {
				for (IXValue value : xObject.getAttributes()) {
					possibleAttributePropertyNames.add(value.getName());
				}
				for (IXValue value : xObject.getProperties()) {
					possibleAttributePropertyNames.add(value.getName());
				}
			}

			// Use the possible attribute and property names as the column names
			// ensuring that the "NAME" attribute is the first in the list.
			possibleAttributePropertyNames.remove("NAME");
			columnList.add("NAME");
			columnList.addAll(possibleAttributePropertyNames);

			// Construct the table data.
			for (IXObject xObject : selectionSet) {
				List<String> currentRow = new ArrayList<String>();
				for (String column : columnList) {
					final String a = xObject.getAttribute(column);
					final String p = xObject.getProperty(column);
					if (p == null && a != null) {
						// Use the attribute value if this was an attribute.
						currentRow.add(a);
					}
					else if (a == null && p != null) {
						// Use the property value if this was a property.
						currentRow.add(p);
					}
					else {
						// As this is neither a property nor an attribute on this object so
						// we just insert a blank string.
						currentRow.add("");
					}
				}
				rowList.add(currentRow);
			}

			// Display the dimensions of the table...for debuging.
			outputWindow.println("columnList.size()     = " + columnList.size());
			outputWindow.println("rowList.size()        = " + rowList.size());
			outputWindow.println("rowList.get(0).size() = " + rowList.get(0).size());
		}

		/* (non-Javadoc)
				 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
				 */
		public String getColumnName(int column)
		{
			return columnList.get(column);
		}

		/* (non-Javadoc)
						 * @see javax.swing.table.AbstractTableModel#getRowCount()
						 */
		public int getRowCount()
		{
			return rowList.size();
		}

		/* (non-Javadoc)
						 * @see javax.swing.table.AbstractTableModel#getColumnCount()
						 */
		public int getColumnCount()
		{
			return columnList.size();
		}

		/* (non-Javadoc)
						 * @see javax.swing.table.AbstractTableModel#getValueAt(int, int)
						 */
		public Object getValueAt(int row, int col)
		{
			return rowList.get(row).get(col);
		}

		/* (non-Javadoc)
						 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
						 */
		public boolean isCellEditable(int row, int column)
		{
			// This is not an editable table.
			return false;
		}

		/* (non-Javadoc)
						 * @see javax.swing.table.AbstractTableModel#setValueAt(int, int)
						 */
		public void setValueAt(Object value, int row, int col)
		{
			// This is not an editable table so we do not need this method.
			// rowList.get(row).set(col, value);
			fireTableCellUpdated(row, col);
		}
	}
}
