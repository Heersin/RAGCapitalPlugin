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

import com.example.plugin.utility.DesignContentsDumpImpl;
import com.mentor.chs.api.IXBuildList;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXOutputWindow;
import com.mentor.chs.plugin.action.IXApplicationAction;

import javax.swing.Icon;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

/**
 * This custom action retrieve the design contens for all the designs in the current project.
 * <p>
 */
public class AllDesignsContentAction extends BaseAction implements IXApplicationAction
{

	public AllDesignsContentAction() {
		super(
				"Dump Design Contents",
				"1.0",
				"This Action will output contents of all the designs in current project");
	}
	public Icon getSmallIcon()
	{
		return null;
	}
	public boolean isAvailable(IXApplicationContext context)
	{
		return (!getSelectedDesigns(context).isEmpty()) ;
	}
	private Set<IXDesign> getSelectedDesigns(IXApplicationContext applicationContext){
		Set<IXDesign> designs = new HashSet<IXDesign>();
		designs.addAll(applicationContext.getSelectedObjects(IXDesign.class));
		for(IXProject proj : applicationContext.getSelectedObjects(IXProject.class)){
			designs.addAll(proj.getDesigns());
		}
		for(IXBuildList bld : applicationContext.getSelectedObjects(IXBuildList.class)){
			designs.addAll(bld.getDesigns());
		}
		return designs;
	}
	public boolean execute(IXApplicationContext applicationContext)
	{
		IXOutputWindow output = applicationContext.getOutputWindow();
		IXProject project = applicationContext.getCurrentProject();
		DesignContentsDumpImpl dumpImpl = new DesignContentsDumpImpl(output, project);

		//Open a file for logging all XObjects.
		String chsHome = System.getenv("chs_home");
		File logFile = null;
		if(chsHome!=null){
			logFile = new File(chsHome + File.separator + "temp" + File.separator + "BusinessObjects.log");
		}
		Writer fileWriter = null;
		if(logFile!=null){
			try {
				fileWriter = new FileWriter(logFile,false);
			}
			catch (IOException e) {
				e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
			}
		}

		dumpImpl.dumpDesignContents(fileWriter);


		return true;
	}
}
