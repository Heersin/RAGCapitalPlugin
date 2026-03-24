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

import com.mentor.chs.plugin.action.IXLogicAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.api.IXLibrary;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXOptionExpressionValidator;
import com.mentor.chs.api.IXOption;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXLibrarySymbol;
import com.mentor.chs.api.IXSymbol;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXConnectivityObject;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXDiagramInstance;
import com.example.plugin.utility.ExtensibilityNetlistWriter;

import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.imageio.ImageIO;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * This custom action log all the library details.
 * <p>
 */
public class LibraryLogAction extends BaseAction implements IXLogicAction, IXIntegratorAction, IXHarnessAction
{
	private String m_partNumber = "";
	private String m_optionExpression = "";
	private boolean m_codesLog = false;
	private boolean m_imageLog = false;
	private boolean m_usageWithExtend = false;

	public LibraryLogAction() {
		super(
				"LibraryLog & OptionExpressionValidate",
				"1.0",
				"This Action will output Library contents and validate input option expresion");
	}
	public Icon getSmallIcon()
	{
		return null;
	}
	public boolean isAvailable(IXApplicationContext context)
	{
		return (context.getLibrary() != null);
	}
	private void showInputDialog(Frame parentFrame){
		JPanel inputPanel = new JPanel();
		inputPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		inputPanel.setLayout(new GridBagLayout());
		JLabel optExpLabel = new JLabel();
		optExpLabel.setText("OptionExpression to be validated: ");
		final JTextField optExpText = new JTextField();
		optExpText.setText(m_optionExpression);
		optExpText.setColumns(15);
		JLabel partNumLabel = new JLabel();
		partNumLabel.setText("PartNumber to be searched: ");
		final JTextField partNumText = new JTextField();
		partNumText.setText(m_partNumber);
		partNumText.setColumns(15);
		GridBagConstraints gbConst = new GridBagConstraints();
					gbConst.fill = GridBagConstraints.BOTH;
					gbConst.gridx = 0;
					gbConst.gridy = 0;
					gbConst.weightx = 0;
					gbConst.weighty = 0;
		inputPanel.add(optExpLabel, gbConst);
		gbConst.gridy = 1;
		inputPanel.add(partNumLabel, gbConst);
		gbConst.gridx = 1;
		gbConst.gridy = 0;
		gbConst.weightx = 1.;
		gbConst.weighty = 1.;
		inputPanel.add(optExpText, gbConst);
		gbConst.gridy = 1;
		inputPanel.add(partNumText, gbConst);
		gbConst.gridy = 2;
		final JCheckBox chBox = new JCheckBox("Log All Library Codes",m_codesLog);
		inputPanel.add(chBox, gbConst);
		gbConst.gridy = 3;
		final JCheckBox chBoxGraphics = new JCheckBox("Vew Symbols as PNG & SVG",m_imageLog);
		inputPanel.add(chBoxGraphics, gbConst);
		gbConst.gridy = 4;
		final JCheckBox chBoxUsages = new JCheckBox("Show Usages with Extend Info",m_usageWithExtend);
		inputPanel.add(chBoxUsages, gbConst);
		final JDialog inputDlg = new JDialog(parentFrame,"Input test data",true);

		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		JButton okButton = new JButton();
		okButton.setText("OK");
		okButton.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e)
			{
				m_partNumber = partNumText.getText();
				m_optionExpression = optExpText.getText();
				m_codesLog = chBox.isSelected();
				m_imageLog = chBoxGraphics.isSelected();
				m_usageWithExtend = chBoxUsages.isSelected();
				inputDlg.dispose();
			}
		});
		buttonPanel.add(okButton,BorderLayout.EAST);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(inputPanel,BorderLayout.NORTH);
		mainPanel.add(buttonPanel,BorderLayout.SOUTH);
		mainPanel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		inputDlg.add(mainPanel);
		inputDlg.pack();

		if (parentFrame != null) {
			int x = parentFrame.getX() + (parentFrame.getWidth() - inputDlg.getWidth()) / 2;
			int y = parentFrame.getY() + (parentFrame.getHeight() - inputDlg.getHeight()) / 2;
			inputDlg.setLocation(x, y);
		}

		inputDlg.setVisible(true);
	}
	public boolean execute(IXApplicationContext applicationContext)
	{
		applicationContext.getOutputWindow().println("Outputing all parameters for library and query library for part, Logs also stored at chs_home/temp/BusiessObjects.log");

		StringWriter strWriter = new StringWriter();
		StringReader strReader = null;
		BufferedReader buffReader = null;
		FileWriter fileWriter = null;
		//Open a file for logging all XObjects.
		String chsHome = System.getenv("chs_home");

		showInputDialog(applicationContext.getParentFrame());

		try {
			ExtensibilityNetlistWriter harnessWriter = new ExtensibilityNetlistWriter(strWriter);
			IXLibrary lib = applicationContext.getLibrary();
			if(m_codesLog){
				harnessWriter.writeLibrary(applicationContext.getLibrary());
			}
			Set<IXObject> objs = applicationContext.getSelectedObjects();
			for(IXObject obj : objs){
				if(obj instanceof IXConnectivityObject){
					strWriter.write("Associated Diagram Objects for " + obj.toString() + '\n');
					for(IXDiagramInstance diagObj : ((IXConnectivityObject)obj).getInstances()){
						strWriter.write(">>>>" + diagObj.getDiagram().getDesign().toString() + ':' +
								diagObj.getDiagram().toString());
						if(diagObj.getZoneInfo() != null){
							strWriter.write(' ' + diagObj.getZoneInfo().getRowName()
									+'_'+ diagObj.getZoneInfo().getColumnName());
						}
						if(m_usageWithExtend){
							IXDiagramObject realDiagObj = diagObj.getDiagramObject();
							strWriter.write(" Abs X,Y,W,H ("+realDiagObj.getAbsoluteExtent().getX()+','+realDiagObj.getAbsoluteExtent().getY()
									+ realDiagObj.getAbsoluteExtent().getWidth() + realDiagObj.getAbsoluteExtent().getHeight()+')');
							strWriter.write(" Rel X,Y,W,H ("+realDiagObj.getRelativeExtent().getX()+','+realDiagObj.getRelativeExtent().getY()
									+realDiagObj.getRelativeExtent().getWidth()+','+realDiagObj.getRelativeExtent().getHeight()+')');
							IXObject connObj = diagObj.getConnectivity();
							strWriter.write(" Conn Obj - " + connObj.toString());
						}
						strWriter.write('\n');
					}
				}
			}
			if(m_partNumber != null && m_partNumber.trim().length() > 0){
				String[] partNums = m_partNumber.split(",");
				for(String partNum : partNums){
					strWriter.write("Retrieve library part with input part number:" + partNum + " and logging them" +'\n');
					IXLibraryObject libObj = lib.getLibraryObject(partNum);
					if(libObj == null){
						strWriter.write("Part:" + partNum + " not found in Library" +'\n');
						continue;
					}
					harnessWriter.writeNetlist(libObj);
					if(m_imageLog){
						for(IXLibrarySymbol libSymb : libObj.getLibrarySymbols()){
							IXSymbol symbol = libSymb.getSymbol();
							if(symbol == null){
								continue;
							}
							String symbName = symbol.getAttribute("Name");

							StringWriter strWriterSVG = new StringWriter();
							BufferedImage buffImage = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_RGB);

							FileWriter fileWriterSVG = null;
							FileWriter fileWriterPNG = null;
							try {

								File svgFile = null;
								String svgFilePath = "C:\\";
								String pngFilePath = "C:\\";
								File pngFile = null;
								if(chsHome!=null){
									svgFilePath = chsHome + File.separator + "temp" + File.separator + partNum + "_" + symbName + ".svg";
									pngFilePath = chsHome + File.separator + "temp" + File.separator + partNum + "_" + symbName + ".png";
									svgFile = new File(svgFilePath);
									pngFile = new File(pngFilePath);
								}
								if(svgFile!=null){
									fileWriterSVG = new FileWriter(svgFile,false);
									symbol.getSVG(strWriterSVG,null);
									String svgStr = strWriterSVG.toString();
									fileWriterSVG.write(svgStr);
								}
								if(pngFile!=null){
									fileWriterPNG = new FileWriter(pngFile,false);
									symbol.getPNG(buffImage,null);
									ImageIO.write(buffImage, "png", pngFile);
								}
								strWriter.write("SVG for symbol " + symbName + " created at "+ svgFilePath +", please click <A href=\"file://" + svgFilePath + "\">here</A> to see" +'\n');
								strWriter.write("PNG for symbol " + symbName + " created at "+ pngFilePath +", please click <A href=\"file://" + pngFilePath + "\">here</A> to see" +'\n');
	//							applicationContext.getOutputWindow().println(getName(),
	//									"To open your browser on the Mentor WebPage, please click <A href=\"http://www.mentor.com\">here</A>");
					//			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + svgFilePath);
					//			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + pngFilePath);
							 //Enable the flag so that we don't print this again
							}catch(IOException ioe) {
								ioe.printStackTrace();
							}
							catch(Exception e) {
								e.printStackTrace();
							}
							finally{
								//Close the string writer
								try{
									strWriterSVG.close();

									if(fileWriterSVG!=null){
										fileWriterSVG.close();
									}
									if(fileWriterPNG!=null){
										fileWriterPNG.close();
									}
									applicationContext.getOutputWindow().println("<hr></hr>");
								}catch(IOException e){
									e.printStackTrace();
								}
							}
						}
					}
				}
			}

			if(m_optionExpression != null && m_optionExpression.length() > 0){
				strWriter.write("Validate input OptionExpression:" + m_optionExpression +'\n');
				IXOptionExpressionValidator optExpValidator = applicationContext.createOptionExpressionValidator();
				IXDesign currDes = applicationContext.getCurrentDesign();
				Set<IXOption> options;
				if(currDes instanceof IXHarnessDesign){
				   options = currDes.getOptions();
				}
				else{
					options = applicationContext.getCurrentProject().getOptions();
				}
				List<String> optionNames = new ArrayList<String>();
				for(IXOption opt : options){
					optionNames.add(opt.getName());
				}
				if(optExpValidator.validateExpression(m_optionExpression, optionNames)){
					strWriter.write("ValidateExpression successful" + '\n');
				}
				else{
					strWriter.write("ValidateExpression returned false, Error Message:" +optExpValidator.getErrorMessage()+", ErrorPosition:" + optExpValidator.getErrorPosition() + '\n');
				}
				if(optExpValidator.isExpressionApplicable(m_optionExpression, optionNames)){
					strWriter.write("isExpressionApplicable successful" + '\n');
				}
				else{
					strWriter.write("isExpressionApplicable returned false, Error Message:" +optExpValidator.getErrorMessage()+", ErrorPosition:" + optExpValidator.getErrorPosition() + '\n');
				}
			}
			File logFile = null;
			if(chsHome!=null){
				logFile = new File(chsHome + File.separator + "temp" + File.separator + "BusiessObjects.log");
			}
			  //Create a string reader and wrap it with buffered reader
			 strReader = new StringReader(strWriter.toString());
			 buffReader = new BufferedReader(strReader);
			 if(logFile!=null){
				 fileWriter = new FileWriter(logFile,false);
			 }
			String line;
			 while((line=buffReader.readLine())!=null){
				 applicationContext.getOutputWindow().println(line);
				 if(fileWriter!=null){
					  fileWriter.write(line + '\n');
				}

			 }
		 //Enable the flag so that we don't print this again
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}finally{
			//Close the string writer
			try{
				strWriter.close();

				if(strReader!=null){
					strReader.close();
				}

				if(buffReader!=null){
					buffReader.close();
				}
				if(fileWriter!=null){
					fileWriter.close();
				}
				applicationContext.getOutputWindow().println("<hr></hr>");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return true;
	}
}
