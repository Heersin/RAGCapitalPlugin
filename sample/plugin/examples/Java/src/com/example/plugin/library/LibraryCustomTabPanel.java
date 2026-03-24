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

import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibrary;
import com.mentor.chs.api.IXLibraryUserProperty;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXAttributeSetter;
import com.mentor.chs.plugin.library.IXLibraryTabNotifier;
import com.mentor.chs.plugin.library.IXLibraryTabPanel;

import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.util.Set;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

/**
 * This Custom Library Tab Panel provides an example for editing library attributes. It demonstrates how to create a tab
 * panel that changes attributes only when the [OK] button is pressed. All the custom panels must inherit from JPanel
 * and implement  IXLibraryTabPanel
 */
public class LibraryCustomTabPanel extends JPanel implements IXLibraryTabPanel
{

	private static IXLibraryTabNotifier tabNotifier = null;
	private JLabel userProp1 = new JLabel("Min Voltage");
	private JLabel userProp2 = new JLabel("Max Voltage");
	private JLabel userProp3 = new JLabel("Max Current");
	private JComboBox userProp1Combo = new JComboBox();
	private JComboBox userProp2Combo = new JComboBox();
	private JTextField userProp3Text = new JTextField();

	
	public LibraryCustomTabPanel()
	{
		userProp1Combo.setName("userProp1Combo");
		userProp2Combo.setName("userProp2Combo");
		userProp3Text.setName("userProp3Text");
		JPanel jPanel1 = new JPanel();
		GridBagLayout gridBagLayout1 = new GridBagLayout();
		jPanel1.setLayout(gridBagLayout1);

		userProp1Combo.addItem("NA");
		userProp1Combo.addItem("0.05");
		userProp1Combo.addItem("0.25");
		userProp1Combo.addItem("0.35");

		userProp2Combo.addItem("NA");
		userProp2Combo.addItem("0.5");
		userProp2Combo.addItem("0.6");
		userProp2Combo.addItem("0.7");

		userProp1Combo.addItemListener(new ItemListener()
		{

			public void itemStateChanged(ItemEvent e)
			{
				if (tabNotifier != null) {
					tabNotifier.edited();
				}
			}
		});
		userProp2Combo.addItemListener(new ItemListener()
		{

			public void itemStateChanged(ItemEvent e)
			{
				if (tabNotifier != null) {
					tabNotifier.edited();
				}
			}
		});

		userProp3Text.addKeyListener(new KeyListener()
		{

			public void keyTyped(KeyEvent e)
			{
				if (tabNotifier != null) {
					tabNotifier.edited();
				}
			}

			public void keyPressed(KeyEvent e)
			{

			}

			public void keyReleased(KeyEvent e)
			{

			}
		});

		jPanel1.add(userProp1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
		jPanel1.add(userProp1Combo, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 0), 0, 0));

		jPanel1.add(userProp2, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
				, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		jPanel1.add(userProp2Combo, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0
				, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		jPanel1.add(userProp3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
				, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 5, 0, 0), 0, 0));

		jPanel1.add(userProp3Text, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0
				, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL, new Insets(10, 5, 0, 0), 0, 0));

		setLayout(new GridBagLayout());
		add(jPanel1, new GridBagConstraints(0, 0, 0, 0, 1.0, 1.0
				, GridBagConstraints.EAST, GridBagConstraints.BOTH, new Insets(0, 5, 0, 10), 0, 0));
		revalidate();
		repaint();
	}

	/*
		  col span is two
	   */
	public int getColSpan()
	{
		return 1;
	}

	public int getRowSpan()
	{
		return 2;
	}

	public void refresh(IXApplicationContext libContext)
	{
	   Set<IXObject> objs = libContext.getSelectedObjects();
		if (objs == null || objs.isEmpty()) {
			return;
		}
		if (!objs.isEmpty()) {
			IXObject selObject = objs.iterator().next();
			if (selObject instanceof IXLibraryObject) {

				String value = selObject.getProperty("Min Voltage");
				if(value != null){
					 userProp1Combo.setSelectedItem(value);
				}
				value = selObject.getProperty("Max Voltage");
				if(value != null){
					 userProp2Combo.setSelectedItem(value);
				}
			    value = selObject.getProperty("Max Current");
				if(value != null){
					 userProp3Text.setText(value);
				}
			}
		}
	}

	/*
	 * notifier is passed as a parameter during initilize of the tab panel.
	 * Custom tab panel needs to call edited() on notifier when ever there is a data modification made
	 * here we are adding user property on selected IXLibraryObject. Its creates properties for the part
	*/
	public void update(IXApplicationContext libContext)
	{
		Set<IXObject> objs = libContext.getSelectedObjects();
		if (objs == null || objs.isEmpty()) {
			return;
		}
		if (!objs.isEmpty()) {
			IXObject selObject = objs.iterator().next();
			if (selObject instanceof IXLibraryObject) {
				IXAttributeSetter attSetter = ((IXWriteableObject) selObject).getAttributeSetter();
				assert attSetter != null;
				if (userProp1Combo.getSelectedItem() != null) {
					if(!userProp1Combo.getSelectedItem().toString().equalsIgnoreCase("NA")){
						attSetter.addProperty("Min Voltage", userProp1Combo.getSelectedItem().toString());
					}
					else{
						attSetter.removeProperty("Min Voltage");
					}
				}
				if (userProp2Combo.getSelectedItem() != null) {
					if (!userProp2Combo.getSelectedItem().toString().equalsIgnoreCase("NA")) {
						attSetter.addProperty("Max Voltage", userProp2Combo.getSelectedItem().toString());
					}
					else{
						attSetter.removeProperty("Max Voltage");
					}
				}
				if (!userProp3Text.getText().isEmpty()) {
					attSetter.addProperty("Max Current", userProp3Text.getText());
				}
				else{
					attSetter.removeProperty("Max Current");
				}
				tabNotifier.edited();
			}
		}
	}

	/*
		tab notifier is cached
		 */
	public void initialize(IXLibraryTabNotifier notifier)
	{
		tabNotifier = notifier;
	}
}




