/**
 * Copyright 2009 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.library;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.library.IXLibraryTabPage;
import com.mentor.chs.plugin.library.IXLibraryTabNotifier;
import javax.swing.JTable;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * This Custom Library Tab Page provides an example for editing library attributes. It demonstrates how to create a tab page
 * that changes attributes only when the [OK] button is pressed.
 */
public class LibraryCustomTabPage extends JPanel  implements IXLibraryTabPage
{
	private static IXLibraryTabNotifier tabNotifier = null;
	protected AttributeEditingTable table = null;
	protected JList propoertyList = null;
	private DefaultListModel dataModel = new DefaultListModel();
	private Map<String,Map<IXObject,Set<IXValue>>> nameVsObjectMap = new HashMap<String,Map<IXObject,Set<IXValue>>>();
	protected AttributeEditingTable childtable = null;
	private Map<String,String> attributesList = new HashMap<String,String>();
	private Map<String,String> childattributes = new HashMap<String,String>();

	public String getTabName()
	{
		return "Custom Tab Page";
	}
	@SuppressWarnings("MagicNumber")
	public LibraryCustomTabPage(){
		JPanel jPanel1 = new JPanel();
		GridBagLayout gridBagLayout1 = new GridBagLayout();
		jPanel1.setLayout(gridBagLayout1);

		AttributeTableModel model = new AttributeTableModel(attributesList);
		table = new AttributeEditingTable(model);
		JScrollPane sc = new JScrollPane();
		sc.getViewport().add(table);
		sc.setPreferredSize(new Dimension(300, 200));
		sc.setMinimumSize(new Dimension(300, 200));

		jPanel1.add(sc, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		propoertyList = new JList(dataModel);
		sc = new JScrollPane();
		sc.getViewport().add(propoertyList);
		sc.setPreferredSize(new Dimension(300, 200));
		sc.setMinimumSize(new Dimension(300, 200));

		jPanel1.add(sc, new GridBagConstraints(0, 1, 1, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		AttributeTableModel model1 = new AttributeTableModel(childattributes);
		childtable =  new AttributeEditingTable(model1);
		sc = new JScrollPane();
		sc.getViewport().add(childtable);
		sc.setPreferredSize(new Dimension(300, 200));
		sc.setMinimumSize(new Dimension(300, 200));

		jPanel1.add(sc, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
				, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));
		setLayout(new GridBagLayout());
		add(jPanel1, new GridBagConstraints(0, 0, 0, 0, 1.0, 1.0
				, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(10, 5, 0, 10), 0, 0));
		revalidate();
		repaint();

		propoertyList.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent e)
			{
				childtable.getDataModel().refresh(null);
				Object Value = propoertyList.getSelectedValue();
				Map<IXObject,Set<IXValue>> xAttributes = nameVsObjectMap.get(Value);
				  if(xAttributes == null){
					  return;
				  }
				   Collection<Set<IXValue>> attributes = xAttributes.values();
					if(!attributes.isEmpty()){
				   		childtable.getDataModel().refresh(attributes.iterator().next());
					}
					childtable.getDataModel().fireTableRowsUpdated(childtable.getSelectionModel().getMinSelectionIndex(),childtable.getSelectionModel().getMaxSelectionIndex());

			}

			public void mousePressed(MouseEvent e)
			{
			}

			public void mouseReleased(MouseEvent e)
			{
			}

			public void mouseEntered(MouseEvent e)
			{
			}

			public void mouseExited(MouseEvent e)
			{
			}
		});
		propoertyList.addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				childtable.getDataModel().refresh(null);
				Object Value = propoertyList.getSelectedValue();
				Map<IXObject, Set<IXValue>> xAttributes = nameVsObjectMap.get(Value);
				if (xAttributes == null) {
					return;
				}
				Collection<Set<IXValue>> attributes = xAttributes.values();
				if (!attributes.isEmpty()) {
					childtable.getDataModel().refresh(attributes.iterator().next());
				}
				childtable.revalidate();
				childtable.repaint();
			}
		});

	}
	/*
	refresh is done for the panle on part change
	 */
	public void refresh(IXApplicationContext libContext)
	{
		Set<IXObject> objs = libContext.getSelectedObjects();
		if (objs == null) {
			 table.getDataModel().refresh(null,dataModel,nameVsObjectMap);
			childtable.getDataModel().refresh(null);
			return;
		}
		if (!objs.isEmpty()) {
			IXObject selObject = objs.iterator().next();
			if (selObject instanceof IXLibraryObject) {
				 Object selval = propoertyList.getSelectedValue();
				 table.getDataModel().refresh((IXWriteableObject)selObject,dataModel,nameVsObjectMap);
				 childtable.getDataModel().refresh(null);
				if(selval != null) {
					if(nameVsObjectMap.get(selval.toString()) != null){
						propoertyList.setSelectedValue(selval,true);
					}
				}
				else{
					 propoertyList.setSelectedIndex(0);
				}
			}
		}
		else{
			 table.getDataModel().refresh(null,dataModel,nameVsObjectMap);
			 childtable.getDataModel().refresh(null);
		}
	}
	/*
	changes made on object attributes are applied
	 */
	public void update(IXApplicationContext libContext)
	{
		table.stopCellEditing();
		childtable.stopCellEditing();
		Set<IXObject> objs = libContext.getSelectedObjects();
		if (objs == null || objs.isEmpty()) {
			return;
		}
		if (!objs.isEmpty()) {
			IXObject selObject = objs.iterator().next();
			if (selObject instanceof IXLibraryObject) {
				IXAttributeSetter attSetter = ((IXWriteableObject) selObject).getAttributeSetter();
				assert attSetter != null;
				for (String attribute : attributesList.keySet()) {
					attSetter.addAttribute(attribute, attributesList.get(attribute));
				}
				attributesList.clear();
			}
		}

		IXObject selObject = objs.iterator().next();
		//now update the child objects
		Object Value = propoertyList.getSelectedValue();
		Map<IXObject, Set<IXValue>> xObj = nameVsObjectMap.get(Value);
		if(xObj == null){
			return;
		}
		Set<IXObject> xChildObjs = xObj.keySet();
		if (xChildObjs == null || xChildObjs.isEmpty()) {
			return;
		}
		if (xChildObjs.iterator().next() == null) {
			return;
		}
		IXObject xChildObject = xChildObjs.iterator().next();

		assert xChildObject != null;
		IXObject xContextChildObject  = getOrPopulateChildObject(selObject,Value.toString());
		assert xContextChildObject != null;
		if (xContextChildObject instanceof IXWriteableObject) {
			IXAttributeSetter attSetter = ((IXWriteableObject) xContextChildObject).getAttributeSetter();
			assert attSetter != null;
			for (String attribute : childattributes.keySet()) {
				attSetter.addAttribute(attribute, childattributes.get(attribute));
			}
			childattributes.clear();
		}
	}
   /*
   notifier is cached
    */
	@SuppressWarnings({"AssignmentToStaticFieldFromInstanceMethod"}) public void initialize(IXLibraryTabNotifier notifier)
	{
		tabNotifier = notifier;
	}

   	/**
	 * The JTable
	 */
	protected class AttributeEditingTable extends JTable
	{

		/**
		 * The table model.
		 */
		private AttributeTableModel tableModel;

		/**
		 * Constructor.
		 *
		 * @param model - the table model
		 */
		protected AttributeEditingTable(AttributeTableModel model)
		{
			super(model);
			tableModel = model;
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

		public AttributeTableModel getDataModel(){
			return tableModel;
		}

		}

			/**
	 * This class is a table of the attributes/properties of various IXObjects. This is not an editable table model.
	 */
	protected  class AttributeTableModel extends AbstractTableModel
	{
		private Map<String,String> attributes = null;
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
		 * @param dataModel DefaultListModel list data model
		 * @param nameVsObjectMap  Map<String,Map<IXObject,Set<IXValue>>> map of JList display value VS Map of IXObject with its attribute values
		 */
		protected void refresh(IXWriteableObject xWriteableObject,DefaultListModel dataModel, Map<String,Map<IXObject,Set<IXValue>>> nameVsObjectMap)
		{
			rowList.clear();
			dataModel.clear();
			nameVsObjectMap.clear();
			attributeSetter = null;
			if(xWriteableObject == null){
				return;
			}
			// Construct the table data.
//			attributeSetter = xWriteableObject.getAttributeSetter();

			for (IXValue value : xWriteableObject.getAttributes()) {
				List<String> currentRow = new ArrayList<String>();
				currentRow.add(value.getName());
				final String v = ensureNotNull(value.getValue());
				currentRow.add(v);
				currentRow.add(String.valueOf(v.length()));
				rowList.add(currentRow);
			}
			nameVsObjectMap.clear();
			dataModel.clear();
			getOrPopulateChildObject(xWriteableObject,null);
		    fireTableDataChanged();

		}

		protected void refresh(Set<IXValue> xAttributes)
		{
			rowList.clear();
			if(xAttributes == null){
				return;
			}
			// Construct the table data.
			for (IXValue value :xAttributes) {
				List<String> currentRow = new ArrayList<String>();
				currentRow.add(value.getName());
				final String v = ensureNotNull(value.getValue());
				currentRow.add(v);
				currentRow.add(String.valueOf(v.length()));
				rowList.add(currentRow);
			}
			fireTableDataChanged();
		}

		protected AttributeTableModel(Map<String,String> attributes)
		{
			// Use the possible attribute and property names as the column names
			// ensuring that the "NAME" attribute is the first in the list.
			columnList.add("Name");
			columnList.add("Value");
			columnList.add("Length");
			this.attributes = attributes;
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
			return false;
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
			attributes.put(attributeName,attributeValue);

			// Apply the change to the table cell
			rowList.get(rowIndex).clear();
			rowList.get(rowIndex).add(attributeName);
			rowList.get(rowIndex).add(attributeValue);
			rowList.get(rowIndex).add(String.valueOf(attributeValue.length()));

			fireTableCellUpdated(rowIndex, columnIndex);
			tabNotifier.edited();
		}
	}

	private IXObject getOrPopulateChildObject(IXObject xWriteableObject, String childObjecttoFind)
	{
		if (xWriteableObject instanceof IXLibraryObject) {

			for (Method clMeth : (xWriteableObject).getClass().getMethods()) {
				if (clMeth.getName().startsWith("get") && !clMeth.getName().equalsIgnoreCase("getProperties")) {
					Class<?> returnType = clMeth.getReturnType();
					Class<?>[] params = clMeth.getParameterTypes();

					if (returnType != null && params != null && params.length == 0) {
						try {
							Object obj = clMeth.invoke(xWriteableObject);
							if(obj instanceof IXObject){
								if (childObjecttoFind != null) {
									if (((IXObject)obj).toString().equalsIgnoreCase(childObjecttoFind)) {
										return ((IXObject)obj);
									}
									else {
										continue;
									}
								}
								Map<IXObject, Set<IXValue>> map = new HashMap<IXObject, Set<IXValue>>();
								map.put(((IXObject)obj), ((IXObject)obj).getAttributes());
								nameVsObjectMap.put(((IXObject)obj).toString(), map);
								dataModel.addElement(((IXObject)obj).toString());
							}
							else if(obj instanceof Collection){
								for(Object ixObj : (Collection)obj){
									if (ixObj instanceof IXObject) {
										if (childObjecttoFind != null) {
											if (((IXObject) ixObj).toString().equalsIgnoreCase(childObjecttoFind)) {
												return ((IXObject) ixObj);
											}
											else {
												continue;
											}
										}
										Map<IXObject, Set<IXValue>> map = new HashMap<IXObject, Set<IXValue>>();
										map.put(((IXObject) ixObj), ((IXObject) ixObj).getAttributes());
										nameVsObjectMap.put(((IXObject) ixObj).toString(), map);
										dataModel.addElement(((IXObject) ixObj).toString());
									}
								}
							}
						}
						catch (IllegalAccessException e) {
						  e.printStackTrace();
						}
						catch (InvocationTargetException e) {
							e.printStackTrace();
						}


					}
				}

			}

		}
		return null;
	}
}




