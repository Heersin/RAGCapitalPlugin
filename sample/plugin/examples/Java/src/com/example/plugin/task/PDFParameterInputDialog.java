/**
 * Copyright 2012 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.task;

import com.example.plugin.action.OkCancelDialog;
import com.mentor.chs.api.IXGraphicConversions;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXAction;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class PDFParameterInputDialog extends OkCancelDialog
{

	private static final int FIELD_WIDTH = 20;

	private IXGraphicConversions.XPrintPDFContext printContext;
	private JTextField fileTxt = new JTextField(FIELD_WIDTH);
	private JTextField widthTxt;
	private JTextField heightTxt;
	private JFileChooser fileChooser = new JFileChooser();

	public PDFParameterInputDialog(IXAction action, IXApplicationContext context)
	{
		super(action, context);

		// create the model
		printContext = new IXGraphicConversions.XPrintPDFContext();
		// create the view
		build();
		setTitle(" Parameters for custom PDF generation ");
		pack();
	}

	private void build()
	{
		// Main panel
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;
		gbc.insets = new Insets(2, 2, 2, 2);

		// Labels
		JLabel lbl = new JLabel("File: ");
		panel.add(lbl, gbc);

		// Fields
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx++;
		gbc.gridy = 0;

		fileTxt.setEditable(false);
		panel.add(fileTxt, gbc);

		// Ellipsis buttons
		JButton btn = new JButton("...");
		btn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (fileChooser.showSaveDialog(PDFParameterInputDialog.this) == JFileChooser.APPROVE_OPTION) {
					fileTxt.setText(fileChooser.getSelectedFile().getPath());
				}
			}
		});
		gbc.gridx++;
		gbc.gridy = 0;
		gbc.gridwidth = 1;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0f;
		panel.add(btn, gbc);

		JPanel paramsPanel = newPDFParamsPanel();
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		panel.add(paramsPanel, gbc);

		addContent(panel);
	}

	private JPanel newPDFParamsPanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;

		// Labels
		JLabel lbl = new JLabel("Orientation: ");
		panel.add(lbl, gbc);

		lbl = new JLabel("Color: ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		lbl = new JLabel("Width (cm): ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		lbl = new JLabel("Height (cm): ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		// Fields
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx++;
		gbc.gridy = 0;

		JComboBox combo = new JComboBox(new OrientationModel());
		panel.add(combo, gbc);

		combo = new JComboBox(new ColorModel());
		gbc.gridy++;
		panel.add(combo, gbc);

		widthTxt = new JTextField();
		gbc.gridy++;
		panel.add(widthTxt, gbc);
		widthTxt.setText(Float.toString(printContext.pdfContext.customPageWidthCm));

		heightTxt = new JTextField();
		gbc.gridy++;
		panel.add(heightTxt, gbc);
		heightTxt.setText(Float.toString(printContext.pdfContext.customPageHeightCm));
		return panel;
	}

	protected void okButtonPressed()
	{
		super.okButtonPressed();
		// commit
		saveAdditionalChanges();
	}

	private void saveAdditionalChanges()
	{
		try {
			printContext.pdfContext.customPageWidthCm = Float.parseFloat(widthTxt.getText());
		}
		catch (NumberFormatException ignore) {
		}
		try {
			printContext.pdfContext.customPageHeightCm = Float.parseFloat(heightTxt.getText());
		}
		catch (NumberFormatException ignore) {
		}
	}

	public File getOutputFile()
	{
		return fileChooser.getSelectedFile();
	}

	private abstract static class GenericModel implements ComboBoxModel
	{

		public void addListDataListener(ListDataListener l)
		{
			//NO-OP
		}

		public void removeListDataListener(ListDataListener l)
		{
			//NO-OP
		}
	}

	private class OrientationModel extends GenericModel
	{

		public int getSize()
		{
			return IXGraphicConversions.XPDFPaperOrientationChoice.values().length;
		}

		public Object getSelectedItem()
		{
			return printContext.pdfContext.paperOrientation;
		}

		public Object getElementAt(int i)
		{
			return IXGraphicConversions.XPDFPaperOrientationChoice.values()[i];
		}

		public void setSelectedItem(Object sel)
		{
			printContext.pdfContext.paperOrientation = (IXGraphicConversions.XPDFPaperOrientationChoice) sel;
		}
	}

	private class ColorModel extends GenericModel
	{

		public int getSize()
		{
			return IXGraphicConversions.XPDFColorChoice.values().length;
		}

		public Object getSelectedItem()
		{
			return printContext.pdfContext.color;
		}

		public Object getElementAt(int i)
		{
			return IXGraphicConversions.XPDFColorChoice.values()[i];
		}

		public void setSelectedItem(Object sel)
		{
			printContext.pdfContext.color = (IXGraphicConversions.XPDFColorChoice) sel;
		}
	}

	public PDFTaskParameters getParameters()
	{
		PDFTaskParameters taskparam = new PDFTaskParameters();
		taskparam.setColor(printContext.pdfContext.color.toString());
		taskparam.setImageHeight(printContext.pdfContext.customPageHeightCm);
		taskparam.setImageWidth(printContext.pdfContext.customPageWidthCm);
		taskparam.setOrientation(printContext.pdfContext.paperOrientation.toString());
		return taskparam;
	}

	public String getOutPutZIPFilepath()
	{
		return fileTxt.getText();
	}
}
