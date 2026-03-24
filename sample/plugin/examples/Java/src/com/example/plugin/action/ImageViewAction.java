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
import com.mentor.chs.api.IXDiagram;

import javax.swing.Icon;
import javax.imageio.ImageIO;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

/**
 * This custom action create SVG and PNG of currently opened diagram.
 * <p>
 */
public class ImageViewAction extends BaseAction implements IXLogicAction, IXIntegratorAction, IXHarnessAction
{

	public ImageViewAction() {
		super(
				"Image View Action",
				"1.0",
				"This Action will enable viewing of SVG and PNG for current opened diagram");
	}
	public Icon getSmallIcon()
	{
		return null;
	}
	public boolean isAvailable(IXApplicationContext context)
	{
		return (context.getCurrentDiagram() != null);
	}

	public boolean execute(IXApplicationContext applicationContext)
	{
		applicationContext.getOutputWindow().println("SVG for the diagram stored at chs_home/temp/Diagram.svg");
		applicationContext.getOutputWindow().println("PNG for the diagram stored at chs_home/temp/Diagram.png");

		StringWriter strWriter = new StringWriter();
		BufferedImage buffImage = new BufferedImage(1024, 768, BufferedImage.TYPE_INT_RGB);

		FileWriter fileWriter = null;
		//Open a file for logging all XObjects.
		String chsHome = System.getenv("chs_home");

		try {

			File svgFile = null;
			String svgFilePath = "C:\\";
			String pngFilePath = "C:\\";
			File pngFile = null;
			if(chsHome!=null){
				svgFilePath = chsHome + File.separator + "temp" + File.separator + "Diagram.svg";
				pngFilePath = chsHome + File.separator + "temp" + File.separator + "Diagram.png";
				svgFile = new File(svgFilePath);
				pngFile = new File(pngFilePath);
			}
			if(svgFile!=null){
				fileWriter = new FileWriter(svgFile,false);
				IXDiagram currDiag = applicationContext.getCurrentDiagram();
				currDiag.getSVG(strWriter,null);
				fileWriter.write(strWriter.toString());
			}
			if(pngFile!=null){
				IXDiagram currDiag = applicationContext.getCurrentDiagram();
				currDiag.getPNG(buffImage,null);
				ImageIO.write(buffImage, "png", pngFile);
			}
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + svgFilePath);
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + pngFilePath);
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
				strWriter.close();

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
