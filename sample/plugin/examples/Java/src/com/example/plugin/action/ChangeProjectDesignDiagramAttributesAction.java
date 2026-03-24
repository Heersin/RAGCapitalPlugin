/**
 * Copyright 2010 Mentor Graphics Corporation. All Rights Reserved.
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
import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXHarnessRegister;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXLogicAction;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.util.List;
import java.util.ArrayList;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This Custom action provides an alternate mechanism for editing design/diagram/project attributes. It demonstrates how to create a dialog
 * that changes attributes only when the [OK] button is pressed.
 */
public class ChangeProjectDesignDiagramAttributesAction extends BaseAction implements IXLogicAction, IXIntegratorAction, IXHarnessAction
{

	/**
	 * Constructor.
	 */
	public ChangeProjectDesignDiagramAttributesAction()
	{
		super("Change Project/Design/Diagram Attributes",
				"1.0",
				"Displays an editable table of the attribute & their values. Also provide mechanism to add/modify/remove properties");
	}

	/**
	 * @return false - this example action modifies properties/attributes on objects.
	 */
	public boolean isReadOnly()
	{
		return false;
	}


	/**
	 * @param context - the IXApplicationContext
	 *
	 * @return true, when objects are selected so that example custom actions will only be available when objects are
	 *         selected (unless this method is overriden).
	 */
	public boolean isAvailable(IXApplicationContext context)
	{
		return true;
	}

	/**
	 * This 'execute' method will construct a JDialog that contains a table of all the properties/attributes for the
	 * currently selected objects.
	 *
	 * @param applicationContext - the IXApplicationContext from which the currently selected objects can be obtained.
	 *
	 * @return true, as this Custom Action never fails :-)
	 */
	public boolean execute(IXApplicationContext applicationContext)
	{
		// Create the dialog using the name of the action as the title of the dialog (recommended).
		// You should use a modal 'JDialog' rather than a non-modal 'JDialog' or a normal 'JFrame'.
		AttributeEditingDialog dialog = new AttributeEditingDialog(this, applicationContext);

		// Display the dialog.
		dialog.displayDialog();

		// We should only get here when the JDialog is closed.
		return dialog.wasSuccessful();
	}

	/**
	 * This is the attribute editing dialog with [OK] and [CANCEL] buttons. Any chnages that were made to attributes will
	 * only be done if the [OK] button is pressed.
	 */
	protected static class AttributeEditingDialog extends OkCancelDialog
	{
		private JButton addProp = new JButton("Add Props");
		private JButton remProp = new JButton("Remove Props");
		private JButton modProp = new JButton("Modify Props");

		private JList designlist;
		private List<ShowObjectsAction.ListObj> selObjs = new ArrayList<ShowObjectsAction.ListObj>();
		/**
		 * The table attributes.
		 */
		protected AttributeEditingTable table;

		/**
		 * Constructor
		 *
		 * @param action - the action
		 * @param context - the context containing the selection.
		 */
		protected AttributeEditingDialog(ChangeProjectDesignDiagramAttributesAction action, IXApplicationContext context)
		{
			super(action, context);


			selObjs.clear();
			designlist = new JList();

			addProp.setToolTipText("Adds properties P1,P2,P3,Del1,Del2,Del3");
			modProp.setToolTipText("Modify existing properties");
			remProp.setToolTipText("Remove properties which starts with Del");
			JPanel panel = new JPanel(new GridBagLayout());
			JLabel lbl = new JLabel("Designs..");

			int gridy=0;
			int gridx=0;
			panel.add(lbl, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
					, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 2, 0, 2), 0, 0));

			gridy++;
			JScrollPane scrollPane = new JScrollPane(designlist);
			panel.add(scrollPane, new GridBagConstraints(gridx, gridy, 3, 1, 1.0, 1.0
					, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 2, 0, 2), 0, 0));

			gridy++;
			gridx=0;
			panel.add(lbl, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
					, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 2), 0, 0));

			AttributeEditingTable.AttributeTableModel model = new AttributeEditingTable.AttributeTableModel();
			table = new AttributeEditingTable(model);
			scrollPane = new JScrollPane(table);
			gridy++;
			gridx=0;
			panel.add(scrollPane, new GridBagConstraints(gridx, gridy, 3, 1, 1.0, 1.0
					, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 2, 0, 2), 0, 0));

			gridy++;
			gridx=0;
			panel.add(addProp, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
					, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 2), 0, 0));
			gridx++;
			panel.add(modProp, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
					, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 2), 0, 0));
			gridx++;
			panel.add(remProp, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
					, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 2), 0, 0));
			addContent(panel);

			addProp.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					if(designlist.getSelectedValue() == null){
						return;
					}
					IXWriteableObject obj = (IXWriteableObject) ((ListObj)(designlist.getSelectedValue())).m_obj;
					IXAttributeSetter setter = obj.getAttributeSetter();
					if(setter == null){
						return;
					}
					setter.addProperty("P1","1.0");
					setter.addProperty("P2","2");
					setter.addProperty("P3","Added");
					setter.addProperty("Del1","1.0");
					setter.addProperty("Del2","2");
					setter.addProperty("Del3","Added");
				}
			});
			modProp.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					if(designlist.getSelectedValue() == null){
						return;
					}
					IXWriteableObject obj = (IXWriteableObject) ((ListObj)(designlist.getSelectedValue())).m_obj;
					IXAttributeSetter setter = obj.getAttributeSetter();
					if(setter == null){
						return;
					}
					for(IXValue val : obj.getProperties()){
						String valStr = val.getValue();
						try{
							final int valueInt = Integer.parseInt(valStr) +1;
							setter.addProperty(val.getName(), Integer.toString(valueInt));
						}
						catch(NumberFormatException exc){
							try{
								final double valueInt = Double.parseDouble(valStr) + 1.1;
								setter.addProperty(val.getName(), Double.toString(valueInt));
							}
							catch(NumberFormatException exc1){
								setter.addProperty(val.getName(), valStr + "_app");
							}
						}
					}
				}
			});
			remProp.addActionListener(new ActionListener(){

				public void actionPerformed(ActionEvent e)
				{
					if(designlist.getSelectedValue() == null){
						return;
					}
					IXWriteableObject obj = (IXWriteableObject) ((ListObj)(designlist.getSelectedValue())).m_obj;
					IXAttributeSetter setter = obj.getAttributeSetter();
					if(setter == null){
						return;
					}
					for(IXValue val : obj.getProperties()){
						if(val.getName().startsWith("Del")){
							setter.removeProperty(val.getName());
						}
					}
				}
			});
			List<IXObject> list = new ArrayList<IXObject>();
			IXProject prj = context.getCurrentProject();
			list.add(prj);
			for(IXDesign des : prj.getDesigns()){
				list.add(des);
				if(des instanceof IXHarnessDesign){
					IXHarnessRegister reg = ((IXHarnessDesign)des).getHarness().getHarnessRegister();
					if(reg != null){
						list.add(reg);
					}
				}
				for(IXDiagram diag: des.getDiagrams()){
					list.add(diag);
				}
			}
			List<ListObj> listObj = getListObjects(list);
			designlist.setListData(listObj.toArray());
			designlist.addListSelectionListener(new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent e)
				{
					if(!e.getValueIsAdjusting() && designlist.getSelectedValue() != null){
						IXWriteableObject obj = (IXWriteableObject) ((ListObj)(designlist.getSelectedValue())).m_obj;
						AttributeEditingTable.AttributeTableModel mod = (AttributeEditingTable.AttributeTableModel) table.getModel();
						mod.setObject(obj);
					}
				}
			});
		}

		/**
		 * This method is overriden to ensure that if the [OK] button is pressed when an attribute is still being edited, that
		 * attribute change is still recogonized.
		 */
		protected void okButtonPressed()
		{
			table.stopCellEditing();
			super.okButtonPressed();
		}
		private List<ListObj> getListObjects(List<? extends IXObject> objs){
			List<ListObj> list = new ArrayList<ListObj>();
			for(IXObject obj : objs){
				list.add(new ListObj(obj));
			}
			return list;
		}
		public static class ListObj{
			public IXObject m_obj;
			public ListObj(IXObject obj){
				m_obj = obj;
			}
			private String getDesName(IXDesign des){
				String desName = getName((des));
				String part = des.getAttribute("PartNumber");
				if(part != null){
					desName = desName +  ":" + part;
				}
				String rev = des.getAttribute("Revision");
				if(rev != null){
					desName = desName +  ":" + rev;
				}
				String shdes = des.getAttribute("ShortDescription");
				if(shdes != null){
					desName = desName +  ":" + shdes;
				}
				return desName;
			}
			public String toString(){
				if(m_obj instanceof IXDesign){
					return getDesName((IXDesign)m_obj);
				}
				if(m_obj instanceof IXDiagram){
					String diagName = getDesName(((IXDiagram)m_obj).getDesign());
					return diagName + "-" + getName(m_obj);
				}
				else{
					return getName(m_obj);
				}
			}
			private String getName(IXObject obj){
				if(obj == null){
					String name = m_obj.getAttribute("Name");
					if(name != null){
						return name;
					}
					else{
						return m_obj.toString();
					}
				}
				String name = obj.getAttribute("Name");
				if(name == null){
					return obj.toString();
				}
				return name;
			}
		}
	}
}