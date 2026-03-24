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

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

public class PartitionPanel extends JPanel
{

	private PartitionModel m_diagramPartitionModel = PartitionModel.EMPTY_MODEL;
	private PartitionTableUI m_tableUI;
	private static final String EMPTY_COLUMNS = "emptyColumns";
	private static final String FULL_COLUMNS = "fullColumns";
	private CardLayout m_cardLayout;
	private JPanel m_cardLayoutPanel;
	private ObjectBrowsePanel m_designBrowsePanel;
	private JTextField m_undistributedDiagramProp;

	public PartitionPanel(String borderTitle)
	{
		intialize();
		if (borderTitle != null) {
			setBorder(new TitledBorder(borderTitle));
		}
	}

	public void setDiagramPartitionModel(PartitionModel model)
	{
		m_diagramPartitionModel = model;
		m_designBrowsePanel.addElements(m_diagramPartitionModel.getModels());
		m_undistributedDiagramProp.setText(model.getUndistributedContentDiagramName());
	}

	private void intialize()
	{

		setLayout(new BorderLayout());

		List<PartitionModel.IDesignPartitionModel> designPartitions = m_diagramPartitionModel.getModels();

		ObjectBrowsePanel.IObjectSelectionListener objectSelectionListener =
				new ObjectBrowsePanel.IObjectSelectionListener()
				{
					public void objectSelected(ObjectBrowsePanel.ObjectSelectionEvent<?> event)
					{
						PartitionModel.IDesignPartitionModel newVal =
								(PartitionModel.IDesignPartitionModel) event.getSelectedObject();
						if (newVal == null) {
							return;
						}

						if (newVal.getColumnSpecifier().getColumns().isEmpty()) {
							m_cardLayout.show(m_cardLayoutPanel, EMPTY_COLUMNS);
						}
						else {
							m_tableUI.setObectTypeModel(newVal);
							m_cardLayout.show(m_cardLayoutPanel, FULL_COLUMNS);
						}
					}
				};
		m_designBrowsePanel =
				new ObjectBrowsePanel(designPartitions, new DesignPartitionRenderer(), objectSelectionListener,
						"Functional Design");

		add(m_designBrowsePanel, BorderLayout.NORTH);

		m_cardLayoutPanel = new JPanel();
		m_cardLayout = new CardLayout();
		m_cardLayoutPanel.setLayout(m_cardLayout);

		add(m_cardLayoutPanel, BorderLayout.CENTER);

		JPanel noColumnsPanel = new JPanel();
		noColumnsPanel.setBorder(new TitledBorder("Diagram Partitions"));
		JLabel textLabel = new JLabel();
		textLabel.setText("No options or variants available to create diagram partitions.");
		noColumnsPanel.add(textLabel);

		m_cardLayoutPanel.add(noColumnsPanel, EMPTY_COLUMNS);

		m_tableUI = new PartitionTableUI();

		JPanel tableAndNamePanel = new JPanel(new BorderLayout());
		tableAndNamePanel.setBorder(new TitledBorder("Diagram Partitions"));
//		Box verticalBox = Box.createVerticalBox();
		tableAndNamePanel.add(m_tableUI, BorderLayout.CENTER);
		tableAndNamePanel.add(createUndistributedContentDiagramNamePanel(), BorderLayout.SOUTH);
//		verticalBox.add(Box.createVerticalGlue());

//		tableAndNamePanel.add(verticalBox);

		m_cardLayoutPanel.add(tableAndNamePanel, FULL_COLUMNS);
		if (!designPartitions.isEmpty()) {
			m_designBrowsePanel.setSelected(designPartitions.get(0));
		}
	}

	private JPanel createUndistributedContentDiagramNamePanel()
	{
		String defaultName = m_diagramPartitionModel.getUndistributedContentDiagramName();
		if (defaultName == null || defaultName.isEmpty() || defaultName.trim().isEmpty()) {
			defaultName = "ContentToReview";
		}
		JPanel undistributedContentPanel = new JPanel(new GridBagLayout());

		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 0;
		JLabel label = new JLabel("Undistributed Content Diagram Name:");
		undistributedContentPanel.add(label, gc);
		gc.gridx = 1;
		gc.weightx = 1.0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.WEST;
		gc.insets = new Insets(5, 5, 5, 5);
		m_undistributedDiagramProp = new JTextField(defaultName, 50);
		undistributedContentPanel.add(m_undistributedDiagramProp, gc);

		m_undistributedDiagramProp.setName("pluginUndistributedDiagramName");
		m_undistributedDiagramProp.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override public void insertUpdate(DocumentEvent e)
			{
				setUndistributedContentDiagramName();
			}

			@Override public void removeUpdate(DocumentEvent e)
			{
				setUndistributedContentDiagramName();
			}

			@Override public void changedUpdate(DocumentEvent e)
			{
				setUndistributedContentDiagramName();
			}
		});

		return undistributedContentPanel;
	}

	private void setUndistributedContentDiagramName()
	{
		String newName = m_undistributedDiagramProp.getText();
		m_diagramPartitionModel.setUndistributedDiagramName(newName);
	}

	private static class DesignPartitionRenderer extends DefaultListCellRenderer
	{

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus)
		{
			Component cpt = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof PartitionModel.IDesignPartitionModel) {
				setText(((PartitionModel.IDesignPartitionModel) value).getDesignPartition().getName());
				setIcon(((PartitionModel.IDesignPartitionModel) value).getDesignPartition().getIcon());
			}
			return cpt;
		}
	}
}
