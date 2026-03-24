package com.example.plugin.action;

import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.api.IXValue;

import javax.swing.JTable;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

/**
	 * The JTable
 */
class AttributeEditingTable extends JTable
{

	/**
	 * The table model.
	 */
	private AttributeTableModel tableModel;

	/**
	 * The popup menu for the table.
	 */
	private JPopupMenu popupMenu;

	/**
	 * Constructor.
	 *
	 * @param model - the table model
	 */
	protected AttributeEditingTable(AttributeTableModel model)
	{
		super(model);
		tableModel = model;

		popupMenu = new JPopupMenu();
		buildPopupMenu();
		addMouseListener(new MousePopupListener());
	}

	/**
	 * Called to stop the cell editing when the [OK] button is pressed to ensure the last attribute change is recognized.
	 */
	public void stopCellEditing()
	{
		if (getCellEditor() != null) {
			getCellEditor().stopCellEditing();
		}
	}

	public boolean isReadOnly()
	{
		return tableModel.isReadOnly();
	}

	/**
	 * Build the popup menu that will insert values in the selected attribute.
	 */
	protected void buildPopupMenu()
	{

		/**
		 * Action called when a menu on the popup menu is pressed.
		 */
		ActionListener menuListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				final int sizeOfString = Integer.parseInt(e.getActionCommand());
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < sizeOfString; i++) {
					builder.append("0123456789");
				}
				tableModel.setValueAt(
						builder.toString(),
						getSelectedRow(),
						getSelectedColumn());
			}
		};

		popupMenu.removeAll();

		JMenuItem itemMenu = new JMenuItem("3");
		popupMenu.add(itemMenu);
		itemMenu.setHorizontalTextPosition(SwingConstants.RIGHT);
		itemMenu.addActionListener(menuListener);

		itemMenu = new JMenuItem("7");
		popupMenu.add(itemMenu);
		itemMenu.setHorizontalTextPosition(SwingConstants.RIGHT);
		itemMenu.addActionListener(menuListener);

		itemMenu = new JMenuItem("21");
		popupMenu.add(itemMenu);
		itemMenu.setHorizontalTextPosition(SwingConstants.RIGHT);
		itemMenu.addActionListener(menuListener);

		popupMenu.setLabel("Insert Chars");
		popupMenu.setBorder(new BevelBorder(BevelBorder.RAISED));
	}

	/**
	 * An inner class to check if the current mouse event is to open the popup menu.
	 */
	private class MousePopupListener extends MouseAdapter
	{

		public void mousePressed(MouseEvent e)
		{
			checkPopup(e);
		}

		public void mouseClicked(MouseEvent e)
		{
			checkPopup(e);
		}

		public void mouseReleased(MouseEvent e)
		{
			checkPopup(e);
		}

		private void checkPopup(MouseEvent e)
		{
			if (e.isPopupTrigger()) {
				popupMenu.show(AttributeEditingTable.this, e.getX(), e.getY());
			}
		}
	}

	/**
	 * This class is a table of the attributes/properties of various IXObjects. This is not an editable table model.
	 */
	protected static class AttributeTableModel extends AbstractTableModel
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
		 * The object used to store the changes that need to be applied.
		 */
		protected IXAttributeSetter attributeSetter;

		protected String ensureNotNull(String s)
		{
			if (s == null) {
				return "";
			}
			return s;
		}

		/**
		 * @param xWriteableObject - the currently selected object.
		 */
		protected AttributeTableModel(IXWriteableObject xWriteableObject)
		{
			this();
			addRows(xWriteableObject);

		}
		protected AttributeTableModel()
		{
			// Use the possible attribute and property names as the column names
			// ensuring that the "NAME" attribute is the first in the list.
			columnList.add("Name");
			columnList.add("Value");
			columnList.add("Length");

		}
		void addRows(IXWriteableObject xWriteableObject){
			// Construct the table data.
			attributeSetter = xWriteableObject.getAttributeSetter();
			for (IXValue value : xWriteableObject.getAttributes()) {
				List<String> currentRow = new ArrayList<String>();
				currentRow.add(value.getName());
				final String v = ensureNotNull(value.getValue());
				currentRow.add(v);
				currentRow.add(String.valueOf(v.length()));
				rowList.add(currentRow);
			}

		}
		public void setObject(IXWriteableObject xWriteableObject){
			rowList.clear();
			addRows(xWriteableObject);
			fireTableDataChanged();
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
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return rowList.get(rowIndex).get(columnIndex);
		}

		public boolean isReadOnly()
		{
			return (attributeSetter == null);
		}

		/* (non-Javadoc)
						 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
						 */
		public boolean isCellEditable(int rowIndex, int columnIndex)
		{
			if (isReadOnly()) {
				return false;
			}
			return (columnIndex == 1);
		}

		/* (non-Javadoc)
						 * @see javax.swing.table.AbstractTableModel#setValueAt(int, int)
						 */
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			// Apply the change to the IXAttributeSetter.
			final String attributeName = rowList.get(rowIndex).get(0);
			final String attributeValue = aValue.toString();
			attributeSetter.addAttribute(attributeName, attributeValue);

			// Apply the change to the table cell
			rowList.get(rowIndex).clear();
			rowList.get(rowIndex).add(attributeName);
			rowList.get(rowIndex).add(attributeValue);
			rowList.get(rowIndex).add(String.valueOf(attributeValue.length()));

			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
}
