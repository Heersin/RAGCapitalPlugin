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
package com.example.plugin.action;

import com.mentor.chs.api.IXAbstractTopologyDesign;
import com.mentor.chs.api.IXConnectivity;
import com.mentor.chs.api.IXConnectivityObject;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXHarness;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXHarnessDiagram;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.IXZoneInfo;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXLogicAction;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This custom action  to select & show objects
 * <p/>
 */
public class ShowObjectsAction extends BaseAction implements IXLogicAction, IXIntegratorAction, IXHarnessAction
{

	private JList designlist;
	private JList connobjectlist = new JList();
	private JList diagobjectlist = new JList();
	private JList selobjectlist = new JList();
	private JCheckBox zoomSelected = new JCheckBox("Zoom Selected", true);
	private JCheckBox clearPrevSel = new JCheckBox("Clear Previous Selections", false);

	private IXApplicationContext applicationContext;
	private List<ListObj> selObjs = new ArrayList<ListObj>();

	public ShowObjectsAction()
	{
		super(
				"Select & Show Objects",
				"1.0",
				"This Action will open the diagrams and show the objects selected");
	}

	public Icon getSmallIcon()
	{
		return null;
	}

	public boolean isAvailable(IXApplicationContext context)
	{
		return (context.getCurrentProject() != null);
	}

	public boolean execute(IXApplicationContext context)
	{
		selObjs.clear();
		connobjectlist = new JList();
		designlist = new JList();
		diagobjectlist = new JList();
		selobjectlist = new JList();
		applicationContext = context;
		// Create the dialog using the name of the action as the title of the dialog (recommended).
		// You should use a modal 'JDialog' rather than a non-modal 'JDialog' or a normal 'JFrame'.
		final JDialog dialog = new JDialog(applicationContext.getParentFrame(), true);
		dialog.setTitle(getName());

		JPanel panel = new JPanel(new GridBagLayout());
		JLabel lbl = new JLabel("Designs..");

		int gridy = 0;
		int gridx = 0;
		panel.add(lbl, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 2, 0, 2), 0, 0));

		gridy++;
		JScrollPane scrollPane = new JScrollPane(designlist);
		panel.add(scrollPane, new GridBagConstraints(gridx, gridy, 2, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 2, 0, 2), 0, 0));

		gridy++;
		gridx = 0;
		lbl = new JLabel("Connectivity Objects..");
		panel.add(lbl, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 2), 0, 0));
		gridx++;
		lbl = new JLabel("Diagram Objects..");
		panel.add(lbl, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 2), 0, 0));

		gridy++;
		gridx = 0;
		scrollPane = new JScrollPane(connobjectlist);
		panel.add(scrollPane, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 1.0
				, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 2, 0, 2), 0, 0));
		gridx++;
		scrollPane = new JScrollPane(diagobjectlist);
		panel.add(scrollPane, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 1.0
				, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(5, 2, 0, 2), 0, 0));

		gridy++;
		gridx = 0;
		panel.add(new JPanel(), new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 1.0
				, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 2, 0, 2), 0, 0));

		gridx++;
		JButton btn = new JButton("Add");
		panel.add(btn, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
				, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 2, 0, 2), 0, 0));
		btn.addActionListener(new ActionListener()
		{

			private void addAll(List<ListObj> list, Object[] objs)
			{
				for (Object obj : objs) {
					if (!selObjs.contains(obj)) {
						list.add((ListObj) obj);
					}
				}
			}

			public void actionPerformed(ActionEvent e)
			{
				addAll(selObjs, connobjectlist.getSelectedValues());
				addAll(selObjs, diagobjectlist.getSelectedValues());
				selobjectlist.setListData(selObjs.toArray());
			}
		});
		gridy++;
		gridx = 0;
		lbl = new JLabel("Selected Objects..");
		panel.add(lbl, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 2), 0, 0));
		gridy++;
		gridx = 0;
		scrollPane = new JScrollPane(selobjectlist);
		panel.add(scrollPane, new GridBagConstraints(gridx, gridy, 2, 1, 1.0, 1.0
				, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5, 2, 0, 2), 0, 0));

		gridy++;
		gridx = 0;
		panel.add(zoomSelected, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 2, 0, 2), 0, 0));
		gridx++;
		panel.add(clearPrevSel, new GridBagConstraints(gridx, gridy, 1, 1, 1.0, 0.0
				, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 2, 0, 2), 0, 0));

		btn = new JButton("Show");
		btn.addActionListener(new ActionListener()
		{

			public void actionPerformed(ActionEvent e)
			{
				if (applicationContext == null) {
					return;
				}
				List<IXObject> objs = new ArrayList<IXObject>();
				for (ListObj obj : selObjs) {
					if (obj != null && !objs.contains(obj)) {
						applicationContext.show(obj.m_obj, zoomSelected.isSelected(), !clearPrevSel.isSelected());
						objs.add(obj.m_obj);
					}
				}
				if (selObjs.isEmpty()) {
					applicationContext.show(null, zoomSelected.isSelected(), !clearPrevSel.isSelected());
				}
				else {
					selObjs.clear();
					selobjectlist.setListData(selObjs.toArray());
				}
//				dialog.setVisible(false);
			}
		});

		gridy++;
		gridx = 0;
		panel.add(btn, new GridBagConstraints(gridx, gridy, 2, 1, 0.0, 0.0
				, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(5, 2, 0, 2), 0, 0));
		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(panel, BorderLayout.CENTER);

		IXProject prj = applicationContext.getCurrentProject();
		List<ListObj> list = getListObjects(prj.getDesigns());
		designlist.setListData(list.toArray());
		designlist.addListSelectionListener(new ListSelectionListener()
		{
			private void getHarnessListObjs(IXHarness harn, List<ListObj> list)
			{
				if (harn != null) {
					list.addAll(getListObjects(harn.getBundles()));
					list.addAll(getListObjects(harn.getInsulationRuns()));
					list.addAll(getListObjects(harn.getClips()));
					list.addAll(getListObjects(harn.getGrommets()));
					list.addAll(getListObjects(harn.getOtherComponents()));
					list.addAll(getListObjects(harn.getMultiLocationComponents()));
					list.addAll(getListObjects(harn.getSpotTapes()));
					list.addAll(getListObjects(harn.getNodes()));
				}
			}

			public void valueChanged(ListSelectionEvent e)
			{
				if (!e.getValueIsAdjusting() && designlist.getSelectedValue() != null) {
					IXDesign des = (IXDesign) ((ListObj) (designlist.getSelectedValue())).m_obj;
					//Adding Connectivity Objects
					IXConnectivity conn = des.getConnectivity();
					List<ListObj> list = getListObjects(conn.getDevices());
					list.addAll(getListObjects(conn.getBlocks()));
					list.addAll(getListObjects(conn.getInterconnects()));
					list.addAll(getListObjects(conn.getInterconnectDevices()));
					list.addAll(getListObjects(conn.getConnectors()));
					list.addAll(getListObjects(conn.getSplices()));
					list.addAll(getListObjects(conn.getWires()));
					list.addAll(getListObjects(conn.getMulticores()));
					list.addAll(getListObjects(conn.getNets()));
					list.addAll(getListObjects(conn.getAssemblies()));
					list.addAll(getListObjects(conn.getGrounds()));
					list.addAll(getListObjects(conn.getShields()));
					if (des instanceof IXHarnessDesign) {
						getHarnessListObjs(((IXHarnessDesign) des).getHarness(), list);
					}
					else if (des instanceof IXAbstractTopologyDesign) {
						list.addAll(getListObjects(((IXAbstractTopologyDesign) des).getHarnesses()));
						list.addAll(getListObjects(((IXAbstractTopologyDesign) des).getSlots()));
						list.addAll(getListObjects(((IXAbstractTopologyDesign) des).getSignals()));
						for (IXHarness harn : ((IXAbstractTopologyDesign) des).getHarnesses()) {
							getHarnessListObjs(harn, list);
						}
					}
					connobjectlist.setListData(list.toArray());

					//Adding Diagram Objects
					Set<IXDiagram> dgms = des.getDiagrams();
					List<ListObj> diagObjlist = getListObjects(dgms);
					for (IXDiagram diag : dgms) {
						if (diag instanceof IXHarnessDiagram) {
							diagObjlist.addAll(getListObjects(((IXHarnessDiagram) diag).getDiagramFixtures()));
							diagObjlist.addAll(getListObjects(((IXHarnessDiagram) diag).getDiagramDimensions()));
							diagObjlist.addAll(getListObjects(((IXHarnessDiagram) diag).getDiagramAxialDimensions()));
						}
						for (ListObj obj : list) {
							diagObjlist
									.addAll(getListObjects(diag.getDiagramObjects((IXConnectivityObject) obj.m_obj)));
						}
					}
					diagobjectlist.setListData(diagObjlist.toArray());
				}
			}
		});

		// Display the dialog.
		dialog.pack();
		centerWindow(dialog);
		dialog.setVisible(true);

		// We should only get here when the JDialog is closed.
		return true;
	}

	private List<ListObj> getListObjects(Set<? extends IXObject> objs)
	{
		List<ListObj> list = new ArrayList<ListObj>();
		for (IXObject obj : objs) {
			list.add(new ListObj(obj));
		}
		return list;
	}

	public static class ListObj
	{

		public IXObject m_obj;

		public ListObj(IXObject obj)
		{
			m_obj = obj;
		}

		public String toString()
		{
			if (m_obj instanceof IXDiagramObject) {
				String connObjName = getName(((IXDiagramObject) m_obj).getConnectivity());
				IXZoneInfo info = ((IXDiagramObject) m_obj).getZoneInfo();
				if (info != null) {
					connObjName = connObjName + " " + info.getRowName() + "_" + info.getColumnName();
					return connObjName;
				}
				Rectangle2D ext = ((IXDiagramObject) m_obj).getAbsoluteExtent();
				if (ext != null) {
					connObjName = connObjName + " " + ext.getX() + "_" + ext.getY();
					return connObjName;
				}
				return connObjName;
			}
			else if (m_obj instanceof IXDesign) {
				return getDesName((IXDesign) m_obj);
			}
			else {
				return getName(m_obj);
			}
		}

		private String getDesName(IXDesign des)
		{
			String desName = getName((des));
			String part = des.getAttribute("PartNumber");
			if (part != null) {
				desName = desName + ":" + part;
			}
			String rev = des.getAttribute("Revision");
			if (rev != null) {
				desName = desName + ":" + rev;
			}
			String shdes = des.getAttribute("ShortDescription");
			if (shdes != null) {
				desName = desName + ":" + shdes;
			}
			return desName;
		}

		private String getName(IXObject obj)
		{
			if (obj == null) {
				String name = m_obj.getAttribute("Name");
				if (name != null) {
					return name;
				}
				else {
					return m_obj.toString();
				}
			}
			String name = obj.getAttribute("Name");
			if (name == null) {
				return obj.toString();
			}
			return name;
		}
	}
}
