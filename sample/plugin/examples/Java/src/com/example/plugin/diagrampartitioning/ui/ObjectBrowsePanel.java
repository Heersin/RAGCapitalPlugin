/*
 * Copyright 2013 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */

package com.example.plugin.diagrampartitioning.ui;

import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;

/**
 * Panel containing the combo box of entries and buttons (Back/Next) to traverse the list in the combo box.
 */
public class ObjectBrowsePanel extends JPanel
{

	private JComboBox m_comboBox;
	private JButton m_backButton;
	private JButton m_forwardButton;
	private IObjectSelectionListener m_objectSelectionListener;
	private static final float PANEL_X_AXIS_ALIGNMENT = 0.5f;

	public ObjectBrowsePanel(Collection<?> objects, ListCellRenderer objectRenderer,
			IObjectSelectionListener objectSelectionListener, String borderTitle)
	{
		buildUI(objectRenderer);
		addElements(objects);
		m_objectSelectionListener = objectSelectionListener;
		if (borderTitle != null) {
			setBorder(new TitledBorder(borderTitle));
		}
	}

	public void setSelected(Object object)
	{
		DefaultComboBoxModel model = (DefaultComboBoxModel) m_comboBox.getModel();
		int index = model.getIndexOf(object);
		if (index >= 0) {
			m_comboBox.setSelectedIndex(index);
		}
	}

	/**
	 * Removes all the existing elements and adds the new specified elements to the combo box model
	 *
	 * @param objects elements to be added to the combo box model
	 */
	public void addElements(Collection<?> objects)
	{
		DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) m_comboBox.getModel();
		comboBoxModel.removeAllElements();
		for (Object object : objects) {
			comboBoxModel.addElement(object);
		}
		if (!objects.isEmpty()) {
			m_comboBox.setSelectedIndex(0);
		}
	}

	private void buildUI(ListCellRenderer renderer)
	{

		m_comboBox = new JComboBox();
		m_comboBox.setName("pluginObjectComboBox");
		m_comboBox.setRenderer(renderer);
		m_comboBox.setModel(new DefaultComboBoxModel());
		ActionListener comboBoxListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				Object object = m_comboBox.getSelectedItem();
				refreshButtons(m_comboBox.getSelectedIndex());
				if (m_objectSelectionListener != null) {
					ObjectSelectionEvent<Object> event = new ObjectSelectionEvent<Object>(m_comboBox, object);
					m_objectSelectionListener.objectSelected(event);
				}
			}
		};
		m_comboBox.addActionListener(comboBoxListener);

		m_comboBox.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_LEFT) {
					int i = m_comboBox.getSelectedIndex();
					i--;
					if (i >= 0 && i < m_comboBox.getItemCount()) {
						m_comboBox.setSelectedIndex(i);
					}
					return;
				}

				if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
					int i = m_comboBox.getSelectedIndex();
					i++;
					if (i >= 0 && i < m_comboBox.getItemCount()) {
						m_comboBox.setSelectedIndex(i);
					}
					return;
				}

				Character c = e.getKeyChar();
				String keyS = new String(new char[]{c});
				boolean foundMatch = false;
				int i = 0;
				for (i = 0; i < m_comboBox.getItemCount(); i++) {
					Object o = m_comboBox.getItemAt(i);
					if (o != null && o.toString().substring(0, 1).equalsIgnoreCase(keyS)) {
						foundMatch = true;
						break;
					}
				}
				if (foundMatch && i >= 0 && i < m_comboBox.getItemCount()) {
					Object o = m_comboBox.getItemAt(i);
					m_comboBox.setSelectedItem(o);
				}
			}
		});

		Icon backIcon = PartitionHelper.getImageIcon("ico_back_active.gif");
		m_backButton = backIcon != null ? new JButton(backIcon) : new JButton("BACK");
		m_backButton.setName("pluginBackButton");
		m_backButton.setToolTipText("Previous Design");

		Icon forwardIcon = PartitionHelper.getImageIcon("ico_forward_active.gif");
		m_forwardButton = forwardIcon != null ? new JButton(forwardIcon) : new JButton("NEXT");
		m_forwardButton.setName("pluginForwardButton");
		m_forwardButton.setToolTipText("Next Design");
		m_forwardButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int currentIndex = m_comboBox.getSelectedIndex();
				int numElements = m_comboBox.getModel().getSize();
				int nextSelectedIndex = -1;
				if (currentIndex < numElements) {
					nextSelectedIndex = currentIndex + 1;
					m_comboBox.setSelectedIndex(nextSelectedIndex);
				}
				refreshButtons(nextSelectedIndex);
			}
		});

		m_backButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				int currentIndex = m_comboBox.getSelectedIndex();
				int nextSelectedIndex = -1;
				if (currentIndex > 0) {
					nextSelectedIndex = currentIndex - 1;
					m_comboBox.setSelectedIndex(nextSelectedIndex);
				}
				refreshButtons(nextSelectedIndex);
			}
		});
		refreshButtons(-1);

		JToolBar backToolBar = new JToolBar(SwingConstants.HORIZONTAL);
		backToolBar.setName("backToolBar");
		backToolBar.setRollover(true);
		backToolBar.setFloatable(false);
		backToolBar.add(m_backButton);

		JToolBar forwardToolBar = new JToolBar(SwingConstants.HORIZONTAL);
		forwardToolBar.setName("forwardToolBar");
		forwardToolBar.setRollover(true);
		forwardToolBar.setFloatable(false);
		forwardToolBar.add(m_forwardButton);

		setLayout(new GridBagLayout());
		setAlignmentX(PANEL_X_AXIS_ALIGNMENT);
		setName("ObjectBrowsePanel.panel");

		GridBagConstraints plgGbc = new GridBagConstraints();
		plgGbc.insets = new Insets(3, 3, 3, 3);
		plgGbc.anchor = GridBagConstraints.WEST;
		plgGbc.fill = GridBagConstraints.HORIZONTAL;
		plgGbc.gridy = 0;

		plgGbc.gridx = 0;
		plgGbc.gridwidth = 1;
		plgGbc.weightx = 0.0D;
		add(backToolBar, plgGbc);

		plgGbc.gridx = 1;
		plgGbc.gridwidth = 2;
		plgGbc.weightx = 1.0D;
		add(m_comboBox, plgGbc);

		plgGbc.gridx = 3;
		plgGbc.gridwidth = 1;
		plgGbc.weightx = 0.0D;
		add(forwardToolBar, plgGbc);
	}

	private void refreshButtons(int nextSelectedIndex)
	{
		int numElements = m_comboBox.getModel().getSize();
		m_forwardButton.setEnabled(nextSelectedIndex >= 0 && nextSelectedIndex < (numElements - 1));
		m_backButton.setEnabled(nextSelectedIndex > 0);
	}

	public interface IObjectSelectionListener extends EventListener
	{

		void objectSelected(ObjectSelectionEvent<?> event);
	}

	public static class ObjectSelectionEvent<T> extends EventObject
	{

		private T m_objectSelected;

		public ObjectSelectionEvent(Object source, T objectSelected)
		{
			super(source);
			m_objectSelected = objectSelected;
		}

		public T getSelectedObject()
		{
			return m_objectSelected;
		}
	}
}
