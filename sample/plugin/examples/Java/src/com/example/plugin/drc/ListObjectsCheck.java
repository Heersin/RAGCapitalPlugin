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
package com.example.plugin.drc;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.plugin.utility.ExtensibilityNetlistWriter;

import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.drc.IXDRCViolationReporter;
import com.mentor.chs.plugin.drc.IXLogicDRCheck;
import com.mentor.chs.plugin.drc.IXHarnessDRCheck;
import com.mentor.chs.plugin.drc.IXIntegratorDRCheck;
import com.mentor.chs.plugin.drc.IXLogicBuildListDRCheck;
import com.mentor.chs.plugin.drc.IXDRCheck.Severity;

/**
 * This design rule check list all the objects supplied to DRC check method.
 * <p>
 */
public class ListObjectsCheck extends BaseDRCheck implements IXLogicDRCheck, IXHarnessDRCheck, IXIntegratorDRCheck, IXLogicBuildListDRCheck
{

	protected List<IXObject> m_xObjects = new ArrayList<IXObject>();

	public ListObjectsCheck() {
		super(
				"List Objects Check",
				"1.0",
				"This DR Check will list all objects passed to DRC by DRC Engine.",
				true,
				Severity.Information);
	}



	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#begin(com.mentor.chs.plugin.drc.IXDRCViolationReporter)
	 */
	public void begin(IXDRCViolationReporter reporter) {
		reporter.report(Severity.Information, "Lists all objects passed to DRC, Logs also stored at chs_home/temp/BusiessObjects.log");


	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#check(com.mentor.chs.plugin.drc.IXDRCViolationReporter, com.mentor.chs.api.IXObject)
	 */
	public void check(IXDRCViolationReporter reporter, IXObject object) {

		reporter.report(Severity.Information,ExtensibilityNetlistWriter.getObjectType(object) + " : {0}, Name : " + object.getAttribute("Name"), object);
		 //Add the XObject to the set.
		 m_xObjects.add(object);
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#end(com.mentor.chs.plugin.drc.IXDRCViolationReporter)
	 */
	public void end(IXDRCViolationReporter reporter) {

		//Iterate through all the harnesses and output the parameters.
		FileWriter fileWriter = null;
		try {
				String chsHome = System.getenv("chs_home");
				if(chsHome!=null){
					File logicLogFile = new File(chsHome + File.separator + "temp" + File.separator + "LogicXObjects.log");
					fileWriter = new FileWriter(logicLogFile,false);
				}

				//Output all XObjects
				for(IXObject obj : m_xObjects){
					if(fileWriter!=null){
						fileWriter.write(ExtensibilityNetlistWriter.getObjectType(obj) + " : " + obj.getAttribute("Name") + '\n');
				   }
				}

		}catch(IOException ioe) {
			 ioe.printStackTrace();
		}finally{
           	 try{
				 if(fileWriter!=null){
				 	fileWriter.close();
				 }
			 }catch(IOException e){
					e.printStackTrace();
		 	 }
		}

		//Clear the set for subsequent test runs.
		m_xObjects.clear();
	}


 }
