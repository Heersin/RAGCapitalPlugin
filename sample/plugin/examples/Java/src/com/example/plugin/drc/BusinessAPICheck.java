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

import com.mentor.chs.plugin.drc.IXHarnessDRCheck;
import com.mentor.chs.plugin.drc.IXDRCViolationReporter;
import com.mentor.chs.plugin.drc.IXLogicDRCheck;
import com.mentor.chs.plugin.drc.IXIntegratorDRCheck;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.api.IXObject;
import com.example.plugin.utility.ExtensibilityNetlistWriter;

import java.io.StringWriter;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This design rule check log all the objects and its details in current design. Most of the interfaces and
 * methods on them are used and the attributes/properties/relations are logged.
 * <p>
 */
public class BusinessAPICheck extends BaseDRCheck implements IXHarnessDRCheck, IXLogicDRCheck, IXIntegratorDRCheck, IXApplicationContextListener
{
	private IXApplicationContext m_applicationContext;

	public BusinessAPICheck() {
		super(
				"Business API DR Check",
				"1.0",
				"This DRC will output all Business objects for QA testing",
				true,
				Severity.Information);
	}



	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#begin(com.mentor.chs.plugin.drc.IXDRCViolationReporter)
	 */
	public void begin(IXDRCViolationReporter reporter) {

	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#check(com.mentor.chs.plugin.drc.IXDRCViolationReporter, com.mentor.chs.api.IXObject)
	 */
	public void check(IXDRCViolationReporter reporter, IXObject object) {

		//This DRC should be completed during one execution and output of all parameters
	}

	/* (non-Javadoc)
	 * @see com.mentor.chs.plugin.drc.IXDRCheck#end(com.mentor.chs.plugin.drc.IXDRCViolationReporter)
	 */
	public void end(IXDRCViolationReporter reporter) {

		reporter.report(Severity.Information, "DEBUG: Outputing all parameters.");
		m_applicationContext.getOutputWindow().println("Outputing all parameters, Logs also stored at chs_home/temp/BusiessObjects.log");

		StringWriter strWriter = new StringWriter();
		StringReader strReader = null;
		BufferedReader buffReader = null;
		FileWriter fileWriter = null;
		//Open a file for logging all XObjects.
		String chsHome = System.getenv("chs_home");

		try {
			ExtensibilityNetlistWriter harnessWriter = new ExtensibilityNetlistWriter(strWriter);
			harnessWriter.writeNetlist(m_applicationContext.getCurrentDesign());

			File logFile = null;
			if(chsHome!=null){
				logFile = new File(chsHome + File.separator + "temp" + File.separator + "BusiessObjects.log");
			}
			  //Create a string reader and wrap it with buffered reader
			 strReader = new StringReader(strWriter.toString());
			 buffReader = new BufferedReader(strReader);
			 reporter.report(Severity.Information, "Logs also stored at chs_home/temp/BusiessObjects.log");
			 if(logFile!=null){
				 fileWriter = new FileWriter(logFile,false);
			 }
			String line;
			 while((line=buffReader.readLine())!=null){
				 m_applicationContext.getOutputWindow().println(line);
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
				m_applicationContext.getOutputWindow().println("<hr></hr>");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	public void setApplicationContext(IXApplicationContext applicationContext)
	{
		m_applicationContext = applicationContext;
	}
}
