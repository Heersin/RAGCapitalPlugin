/**
 * Copyright 2009 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.library;

import com.mentor.chs.plugin.library.IXLibraryImportExportErrorReporter;
import com.mentor.chs.plugin.library.IXLibraryExportOptions;
import com.mentor.chs.plugin.library.IXLibraryXMLExport;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.List;
import java.util.ArrayList;


public class LibraryPluginFileBasedExampleXMLExport implements IXLibraryXMLExport
{
	File exportFile = null;
	public String getDescription()
	{
		return "Library Export XML Custom Plugin";
	}

	public String getName()
	{
		return "Library XML Export Plugin";
	}

	public String getVersion()
	{
		return "1.0"; 
	}

	public List<String> getLibraryExportFileExtensions()
	{
		 List<String> lstExtensions = new ArrayList<String>();
		 lstExtensions.add("xml");
		 return lstExtensions;
	}

	public String getLibraryExportFileExtensionDescription()
	{
		return "Library Custom XML Files(*.xml)";
	}

	public void exportLibrary(File tmpFile, File file, IXLibraryImportExportErrorReporter conversionErrorReporter,
			IXLibraryExportOptions exportOptions)
	{
		if (file == null) {
			conversionErrorReporter.error("Export File not found");
			return;
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(tmpFile);
			InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
			BufferedReader input = new BufferedReader(reader);
			FileOutputStream fileStream = new FileOutputStream(file);
			BufferedWriter dataStream = new BufferedWriter(new OutputStreamWriter(fileStream, "UTF-8"));
			String line;
			while ((line = input.readLine()) != null) {
			  dataStream.write(line);
			}
			dataStream.close();
			fileStream.close();
		}
		catch (FileNotFoundException e) {
			conversionErrorReporter.error("Custom Export Error" + e.getMessage());
		}
		catch (UnsupportedEncodingException e) {
			conversionErrorReporter.error("Custom Export Error" + e.getMessage());
		}
		catch (IOException e) {
			conversionErrorReporter.error("Custom Export Error" + e.getMessage());
		}
	}

}
