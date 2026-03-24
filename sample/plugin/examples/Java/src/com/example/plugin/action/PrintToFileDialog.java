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

import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXGraphicConversions;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXAction;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * PDF input parameters dialog
 */
public class PrintToFileDialog extends OkCancelDialog
{

	//	private static final int IMAGE_HOLDER_WIDTH = 1024;
	//	private static final int IMAGE_HOLDER_HEIGHT = 768;
	private static final int FIELD_WIDTH = 20;

	private IXApplicationContext appContext;
	private IXGraphicConversions.XPrintPDFContext printContext;
	private IXGraphicConversions.XDXFContext dxfContext;
	private IXGraphicConversions.XSVGContext svgContext;   //FEAT15661
	//	private IXGraphicConversionParameters.XPNGContext pngContext;
	private JTextField fileTxt = new JTextField(FIELD_WIDTH);
	private JTextField widthTxt;
	private JTextField heightTxt;
	private JFileChooser fileChooser = new JFileChooser();
	private JPanel paramsPanel;
	private JPanel scopePanel;
	private JComboBox fmtCombo;
	private JComboBox diagramCombo;

	public PrintToFileDialog(IXAction action, IXApplicationContext context)
	{
		super(action, context);
		appContext = context;

		// create the model
		printContext = new IXGraphicConversions.XPrintPDFContext();
		dxfContext = new IXGraphicConversions.XDXFContext();
		svgContext = new IXGraphicConversions.XSVGContext();
//		pngContext = new IXGraphicConversionParameters.XPNGContext();

		// create the view
		build();
		pack();
	}

	public FormatInfo getSelectedFormatInfo()
	{
		return (FormatInfo) fmtCombo.getSelectedItem();
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

		lbl = new JLabel("Format: ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		// Fields
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx++;
		gbc.gridy = 0;

		fileTxt.setEditable(false);
		panel.add(fileTxt, gbc);

		FormatModel formatModel = new FormatModel();
		fmtCombo = new JComboBox(formatModel);
		fmtCombo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				// set up parameters panels to be showed
				JComboBox combo = (JComboBox) e.getSource();
				FormatInfo info = (FormatInfo) combo.getSelectedItem();

				CardLayout layout = (CardLayout) paramsPanel.getLayout();
				layout.show(paramsPanel, info.getFormatParametersPanelName());
				layout = (CardLayout) scopePanel.getLayout();
				layout.show(scopePanel, info.getScopePanelName());
			}
		});
		gbc.gridwidth = 2;
		gbc.gridy++;
		panel.add(fmtCombo, gbc);

		// Ellipsis buttons
		JButton btn = new JButton("...");
		btn.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				if (fileChooser.showSaveDialog(PrintToFileDialog.this) == JFileChooser.APPROVE_OPTION) {
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

		paramsPanel = newFormatParametersPanel();
		gbc.insets = new Insets(0, 0, 0, 0);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.gridwidth = 3;
		panel.add(paramsPanel, gbc);

		scopePanel = newScopePanel();
		gbc.gridy++;
		panel.add(scopePanel, gbc);

		addContent(panel);
	}

	private JPanel newFormatParametersPanel()
	{
		JPanel panel = new JPanel(new CardLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Parameters"));
		Set<String> panelNames = new HashSet<String>();
		FormatModel model = (FormatModel) fmtCombo.getModel();

		for (int i = 0; i < model.getSize(); i++) {
			FormatInfo info = (FormatInfo) model.getElementAt(i);
			String cardName = info.getFormatParametersPanelName();
			if (!panelNames.contains(cardName)) {
				JPanel card = info.newFormatParametersPanel();
				panel.add(cardName, card);
				panelNames.add(cardName);
			}
		}

		return panel;
	}

	private JPanel newScopePanel()
	{
		JPanel panel = new JPanel(new CardLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Scope"));
		Set<String> panelNames = new HashSet<String>();
		FormatModel model = (FormatModel) fmtCombo.getModel();

		for (int i = 0; i < model.getSize(); i++) {
			FormatInfo info = (FormatInfo) model.getElementAt(i);
			String cardName = info.getScopePanelName();
			if (!panelNames.contains(cardName)) {
				JPanel card = info.newScopePanel();
				panel.add(cardName, card);
				panelNames.add(cardName);
			}
		}

		return panel;
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

		lbl = new JLabel("Area: ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		lbl = new JLabel("Size: ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		lbl = new JLabel("Width (cm): ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		lbl = new JLabel("Height (cm): ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		lbl = new JLabel("Dash Multiplier : ");
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

		combo = new JComboBox(new AreaModel());
		gbc.gridy++;
		panel.add(combo, gbc);

		combo = new JComboBox(new SizeModel());
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

	private JPanel newDXFParamsPanel()
	{
		JPanel panel = new JPanel();

		JCheckBox check = new JCheckBox("Include Extended Font Information");
		check.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JCheckBox check = (JCheckBox) evt.getSource();
				dxfContext.includeExtendedFontInformation = check.isSelected();
			}
		});
		panel.add(check);

		return panel;
	}

	//FEAT15661
	private JPanel newSVGParamsPanel()
	{
		JPanel panel = new JPanel();
		ButtonGroup modeRBgrp = new ButtonGroup();
		JRadioButton old_Plugin = new JRadioButton("Old Plugin", null, true);
		old_Plugin.setActionCommand(IXGraphicConversions.GenerateLegacySVG.USE_SYSTEM_PREFERENCE.name());
		JRadioButton legacy_SVG = new JRadioButton("Legacy SVG", null, false);
		legacy_SVG.setActionCommand(IXGraphicConversions.GenerateLegacySVG.TRUE.name());
		JRadioButton new_SVG = new JRadioButton("New SVG", null, false);
		new_SVG.setActionCommand(IXGraphicConversions.GenerateLegacySVG.FALSE.name());
		modeRBgrp.add(old_Plugin);
		modeRBgrp.add(legacy_SVG);
		modeRBgrp.add(new_SVG);
		RadioListener myListener = new RadioListener();
		old_Plugin.addActionListener(myListener);
		legacy_SVG.addActionListener(myListener);
		new_SVG.addActionListener(myListener);

		panel.add(old_Plugin);
		panel.add(legacy_SVG);
		panel.add(new_SVG);

		return panel;
	}

	class RadioListener implements ActionListener
	{

		public void actionPerformed(ActionEvent e)
		{
			if (!(e.getActionCommand()
					.equalsIgnoreCase(IXGraphicConversions.GenerateLegacySVG.USE_SYSTEM_PREFERENCE.name()))) {
				svgContext.m_generateLegacySVG = IXGraphicConversions.GenerateLegacySVG.valueOf(e.getActionCommand());
			}
		}
	}

	private JPanel newPDFScopePanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;

		// Labels
		JLabel lbl = new JLabel("Scope: ");
		panel.add(lbl, gbc);

		lbl = new JLabel("Revisions: ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		lbl = new JLabel("Derivatives: ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		// Fields
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx++;
		gbc.gridy = 0;

		JComboBox combo = new JComboBox(new ScopeModel());
		panel.add(combo, gbc);

		combo = new JComboBox(new RevisionModel());
		gbc.gridy++;
		panel.add(combo, gbc);

		combo = new JComboBox(new DerivativeModel());
		gbc.gridy++;
		panel.add(combo, gbc);

		return panel;
	}

	private JPanel newDiagramScopePanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;

		// Labels
		JLabel lbl = new JLabel("Design: ");
		panel.add(lbl, gbc);

		lbl = new JLabel("Diagram: ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		// Fields
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx++;
		gbc.gridy = 0;

		JComboBox combo = new JComboBox(new DesignModel());
		combo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JComboBox combo = (JComboBox) evt.getSource();
				DesignWrapper dw = (DesignWrapper) combo.getSelectedItem();
				((DiagramModel) diagramCombo.getModel()).setDesign(dw.getDesign());
			}
		});
		panel.add(combo, gbc);

		diagramCombo = new JComboBox(new DiagramModel());
		gbc.gridy++;
		panel.add(diagramCombo, gbc);

		combo.setSelectedIndex(0);

		return panel;
	}

	//FEAT15661
	private JPanel newSVGScopePanel()
	{
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.LINE_START;

		// Labels
		JLabel lbl = new JLabel("Design: ");
		panel.add(lbl, gbc);

		lbl = new JLabel("Diagram: ");
		gbc.gridy++;
		panel.add(lbl, gbc);

		// Fields
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.gridx++;
		gbc.gridy = 0;

		JComboBox combo = new JComboBox(new DesignModel());
		combo.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				JComboBox combo = (JComboBox) evt.getSource();
				DesignWrapper dw = (DesignWrapper) combo.getSelectedItem();
				((DiagramModel) diagramCombo.getModel()).setDesign(dw.getDesign());
			}
		});
		panel.add(combo, gbc);

		diagramCombo = new JComboBox(new DiagramModel());
		gbc.gridy++;
		panel.add(diagramCombo, gbc);

		combo.setSelectedIndex(0);

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

	private class ScopeModel extends GenericModel
	{

		public int getSize()
		{
			return IXGraphicConversions.XPrintScope.values().length;
		}

		public Object getSelectedItem()
		{
			return printContext.scope;
		}

		public Object getElementAt(int i)
		{
			return IXGraphicConversions.XPrintScope.values()[i];
		}

		public void setSelectedItem(Object sel)
		{
			printContext.scope = (IXGraphicConversions.XPrintScope) sel;
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

	private class AreaModel extends GenericModel
	{

		public int getSize()
		{
			return IXGraphicConversions.XPDFPrintableAreaChoice.values().length;
		}

		public Object getSelectedItem()
		{
			return printContext.pdfContext.printableArea;
		}

		public Object getElementAt(int i)
		{
			return IXGraphicConversions.XPDFPrintableAreaChoice.values()[i];
		}

		public void setSelectedItem(Object sel)
		{
			printContext.pdfContext.printableArea = (IXGraphicConversions.XPDFPrintableAreaChoice) sel;
		}
	}

	private class RevisionModel extends GenericModel
	{

		public int getSize()
		{
			return IXGraphicConversions.XPrintRevisionSelection.values().length;
		}

		public Object getSelectedItem()
		{
			return printContext.revisionSelection;
		}

		public Object getElementAt(int i)
		{
			return IXGraphicConversions.XPrintRevisionSelection.values()[i];
		}

		public void setSelectedItem(Object sel)
		{
			printContext.revisionSelection = (IXGraphicConversions.XPrintRevisionSelection) sel;
		}
	}

	private class DerivativeModel extends GenericModel
	{

		public int getSize()
		{
			return IXGraphicConversions.XPrintDerivativeSelection.values().length;
		}

		public Object getSelectedItem()
		{
			return printContext.derivativeSelection;
		}

		public Object getElementAt(int i)
		{
			return IXGraphicConversions.XPrintDerivativeSelection.values()[i];
		}

		public void setSelectedItem(Object sel)
		{
			printContext.derivativeSelection = (IXGraphicConversions.XPrintDerivativeSelection) sel;
		}
	}

	private class SizeModel extends GenericModel
	{

		public int getSize()
		{
			return IXGraphicConversions.XPDFPageSizeChoice.values().length;
		}

		public Object getSelectedItem()
		{
			return printContext.pdfContext.pageSize;
		}

		public Object getElementAt(int i)
		{
			return IXGraphicConversions.XPDFPageSizeChoice.values()[i];
		}

		public void setSelectedItem(Object sel)
		{
			printContext.pdfContext.pageSize = (IXGraphicConversions.XPDFPageSizeChoice) sel;
		}
	}

	public abstract static class FormatInfo
	{

		public abstract void print(OutputStream out) throws Exception;

		protected abstract String getName();

		protected abstract String getFormatParametersPanelName();

		protected abstract JPanel newFormatParametersPanel();

		protected abstract String getScopePanelName();

		protected abstract JPanel newScopePanel();

		public String toString()
		{
			return getName();
		}
	}

	private class PDFProjectFormatInfo extends FormatInfo
	{

		public void print(OutputStream out) throws Exception
		{
			appContext.getPrintServices().printToPDF(printContext, out);
		}

		protected String getName()
		{
			return "PDF";
		}

		protected String getFormatParametersPanelName()
		{
			return "PDF";
		}

		protected String getScopePanelName()
		{
			return "PROJECT";
		}

		protected JPanel newFormatParametersPanel()
		{
			return newPDFParamsPanel();
		}

		protected JPanel newScopePanel()
		{
			return newPDFScopePanel();
		}
	}

	private class PDFDiagramFormatInfo extends FormatInfo
	{

		public void print(OutputStream out) throws Exception
		{
			DiagramWrapper diagObj = (DiagramWrapper) diagramCombo.getSelectedItem();
			if (diagObj.getDiagram() != null) {
				diagObj.getDiagram().getPDF(out, printContext.pdfContext);
			}
		}

		protected String getName()
		{
			return "PDF Diagram";
		}

		protected String getFormatParametersPanelName()
		{
			return "PDF";
		}

		protected String getScopePanelName()
		{
			return "DIAGRAM";
		}

		protected JPanel newFormatParametersPanel()
		{
			return newPDFParamsPanel();
		}

		protected JPanel newScopePanel()
		{
			return newDiagramScopePanel();
		}
	}

	private class DXFFormatInfo extends FormatInfo
	{

		public void print(OutputStream out) throws Exception
		{
			DiagramWrapper sel = (DiagramWrapper) diagramCombo.getSelectedItem();
			if (sel != null) {
				sel.getDiagram().getDXF(out, dxfContext);
			}
		}

		protected String getName()
		{
			return "DXF Diagram";
		}

		protected String getFormatParametersPanelName()
		{
			return "DXF";
		}

		protected String getScopePanelName()
		{
			return "DIAGRAM";
		}

		protected JPanel newFormatParametersPanel()
		{
			return newDXFParamsPanel();
		}

		protected JPanel newScopePanel()
		{
			return newDiagramScopePanel();
		}
	}

	//FEAT15661 START
	private class SVGFormatInfo extends FormatInfo
	{

		public void print(OutputStream out) throws Exception
		{
			DiagramWrapper sel = (DiagramWrapper) diagramCombo.getSelectedItem();
			if (sel != null) {
				sel.getDiagram().getSVG(out, svgContext);
			}
		}

		protected String getName()
		{
			return "SVG Diagram";
		}

		protected String getFormatParametersPanelName()
		{
			return "SVG";
		}

		protected String getScopePanelName()
		{
			return "DIAGRAM";
		}

		protected JPanel newFormatParametersPanel()
		{
			return newSVGParamsPanel();
		}

		protected JPanel newScopePanel()
		{
			return newSVGScopePanel();
		}
	}

	// todo: WIP
//	private class PNGFormatInfo extends FormatInfo
//	{
//		public void print(OutputStream out) throws Exception
//		{
//			DiagramWrapper sel = (DiagramWrapper) diagramCombo.getSelectedItem();
//			if (sel != null) {
//				BufferedImage img = new BufferedImage(IMAGE_HOLDER_WIDTH,
//				IMAGE_HOLDER_HEIGHT , BufferedImage.TYPE_INT_RGB);
//				sel.getDiagram().getPNG(img, pngContext);
//				ImageIO.write(img, "png", out);
//			}
//		}
//
//		protected String getName()
//		{
//			return "PNG Diagram";
//		}
//
//		protected String getFormatParametersPanelName()
//		{
//			return "PNG";
//		}
//
//		protected String getScopePanelName()
//		{
//			return "DIAGRAM";
//		}
//
//		protected JPanel newFormatParametersPanel()
//		{
//			return new JPanel();
//		}
//
//		protected JPanel newScopePanel()
//		{
//			return newDiagramScopePanel();
//		}
//	}

	private FormatInfo[] formats = new FormatInfo[]{
			new PDFProjectFormatInfo()
			, new PDFDiagramFormatInfo()
			, new DXFFormatInfo()
			, new SVGFormatInfo()
//			, new PNGFormatInfo() // todo WIP
	};

	private class FormatModel extends GenericModel
	{

		private int selFormatInd = 0;

		public int getSize()
		{
			return formats.length;
		}

		public Object getSelectedItem()
		{
			return formats[selFormatInd];
		}

		public Object getElementAt(int i)
		{
			return formats[i];
		}

		public void setSelectedItem(Object sel)
		{
			for (int i = 0; i < formats.length; i++) {
				if (sel == formats[i]) {
					selFormatInd = i;
					break;
				}
			}
		}
	}

	private static class DesignWrapper implements Comparable<DesignWrapper>
	{

		private final IXDesign design;

		private final String str;

		DesignWrapper(IXDesign design)
		{
			this.design = design;
			str = toString(design);
		}

		@Override public int compareTo(DesignWrapper o)
		{
			return toString().compareTo(o.toString());
		}

		private String toString(IXDesign d)
		{
			if (d == null) {
				return " ";
			}

			return d.getAttribute("name") + ':' + d.getAttribute("revision") + ':'
					+ d.getAttribute("shortDescription");
		}

		public String toString()
		{
			return str;
		}

		protected IXDesign getDesign()
		{
			return design;
		}
	}

	private static class DiagramWrapper
	{

		private IXDiagram diagram;

		DiagramWrapper(IXDiagram diagram)
		{
			this.diagram = diagram;
		}

		public String toString()
		{
			if (diagram == null) {
				return " ";
			}

			return diagram.getAttribute("name");
		}

		protected IXDiagram getDiagram()
		{
			return diagram;
		}
	}

	private class DesignModel extends GenericModel
	{

		private List<DesignWrapper> designs;
		private int indSel = 0;

		private DesignModel()
		{
			designs = new ArrayList<DesignWrapper>();
			Set<IXDesign> projectDesigns = appContext.getCurrentProject().getDesigns();
			for (IXDesign design : projectDesigns) {
				designs.add(new DesignWrapper(design));
			}
			Collections.sort(designs);
		}

		public int getSize()
		{
			return designs.size();
		}

		public Object getSelectedItem()
		{
			return designs.isEmpty() ? null : designs.get(indSel);
		}

		public Object getElementAt(int i)
		{
			return designs.get(i);
		}

		public void setSelectedItem(Object sel)
		{
			for (int i = 0; i < designs.size(); i++) {
				if (sel == designs.get(i)) {
					indSel = i;
					break;
				}
			}
		}
	}

	private static class DiagramModel extends GenericModel
	{

		private List<DiagramWrapper> diagrams;
		private int indSel = 0;

		private DiagramModel()
		{
			setDesign(null);
		}

		private void setDesign(IXDesign design)
		{
			if (design == null || design.getDiagrams().isEmpty()) {
				diagrams = new ArrayList<DiagramWrapper>();
				diagrams.add(new DiagramWrapper(null));
			}
			else {
				diagrams = new ArrayList<DiagramWrapper>();
				for (IXDiagram diagram : design.getDiagrams()) {
					diagrams.add(new DiagramWrapper(diagram));
				}
			}
			indSel = 0;
		}

		public int getSize()
		{
			return diagrams.size();
		}

		public Object getSelectedItem()
		{
			return diagrams.get(indSel);
		}

		public Object getElementAt(int i)
		{
			return diagrams.get(i);
		}

		public void setSelectedItem(Object sel)
		{
			for (int i = 0; i < diagrams.size(); i++) {
				if (sel == diagrams.get(i)) {
					indSel = i;
					break;
				}
			}
		}
	}
}
