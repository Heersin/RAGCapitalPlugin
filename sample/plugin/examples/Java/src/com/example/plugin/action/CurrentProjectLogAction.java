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
 */package com.example.plugin.action;

import com.mentor.chs.plugin.action.IXLogicAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.api.IXDiagram;
import com.example.plugin.utility.ExtensibilityNetlistWriter;

import javax.swing.Icon;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

/**
 * This custom action log all the direct project contents
 * <p>
 */
public class CurrentProjectLogAction extends BaseAction implements IXLogicAction, IXIntegratorAction, IXHarnessAction
{

	public CurrentProjectLogAction() {
		super(
				"Current Project Log Action",
				"1.0",
				"This Action will output Current Project contents");
	}
	public Icon getSmallIcon()
	{
		return null;
	}
	public boolean isAvailable(IXApplicationContext context)
	{
		return (context.getCurrentProject() != null);
	}

	public boolean execute(IXApplicationContext applicationContext)
	{
		applicationContext.getOutputWindow().println("Open Diagrams are :-");
		for(IXDiagram diag : applicationContext.getOpenDiagrams()){
			 applicationContext.getOutputWindow().println(diag.toString());
		}

		applicationContext.getOutputWindow().println("Outputing all parameters for current project, Logs also stored at chs_home/temp/BusiessObjects.log");

		StringWriter strWriter = new StringWriter();
		StringReader strReader = null;
		BufferedReader buffReader = null;
		FileWriter fileWriter = null;
		//Open a file for logging all XObjects.
		String chsHome = System.getenv("chs_home");

		try {
			ExtensibilityNetlistWriter harnessWriter = new ExtensibilityNetlistWriter(strWriter);
			harnessWriter.writeNetlist(applicationContext.getCurrentProject());

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
