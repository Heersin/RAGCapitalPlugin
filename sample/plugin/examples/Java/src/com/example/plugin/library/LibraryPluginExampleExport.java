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
import com.mentor.chs.plugin.library.IXLibraryObjectsExport;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibraryColorCode;
import com.mentor.chs.api.IXLibraryMaterialCode;
import com.mentor.chs.api.IXLibraryComponentTypeCode;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

public class LibraryPluginExampleExport implements IXLibraryObjectsExport
{
	File exportFile = null;
	public String getDescription()
	{
		return "Library Export Custom Plugin";
	}

	public String getName()
	{
		return "Library ABC Export Plugin";
	}

	public String getVersion()
	{
		return "1.0"; 
	}

	public List<String> getLibraryExportFileExtensions()
	{
		 List<String> lstExtensions = new ArrayList<String>();
		 lstExtensions.add("abc");
		 return lstExtensions;
	}

	public String getLibraryExportFileExtensionDescription()
	{
		return "Library ABC Files(*.abc)";
	}

	public void exportLibraryObjects(Set<IXObject> objects, File file,
			IXLibraryImportExportErrorReporter conversionErrorReporter,
			IXLibraryExportOptions exportOptions)
	{
			if(file == null){
				conversionErrorReporter.error("Export File not found");
			return;
		}
		FileOutputStream fileStream = null;
		try {
			fileStream = new FileOutputStream(file);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedWriter dataStream = null;
		try {
			dataStream = new BufferedWriter(new OutputStreamWriter(fileStream, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();  
		}
		StringBuilder  strContent = new StringBuilder();
		//	groupname # colorcode value # Material Code value # Type Code value # Part Number # partstatus#
		// unitofmeasure # cavityqt #  replacedby # latest # specification # striplength #userf1# userf2 #
		// userf3# userf4# userf5# weight# width # alternatepartnumber # ca_attach# depth# description# knockoff # length
		for(IXObject obj : objects){
			if(obj instanceof IXLibraryObject){
				conversionErrorReporter.log(new StringBuilder().append("Writing details about part number : ")
						.append(obj.getAttribute("PartNumber")).toString());
				IXLibraryColorCode colCode = ((IXLibraryObject)obj).getColorCode();
				IXLibraryMaterialCode materialCode = ((IXLibraryObject)obj).getMaterialCode();
				IXLibraryComponentTypeCode typeCode = ((IXLibraryObject)obj).getComponentTypeCode();
				String value = obj.getAttribute("GroupName");
				strContent.append(value).append('#');
				value = colCode.getAttribute("ColorCode");
				strContent.append(value).append('#');
				value = materialCode.getAttribute("MaterialCode");
				strContent.append(value).append('#');
				value = typeCode.getAttribute("TypeCode");
				strContent.append(value).append('#');
				value = obj.getAttribute("PartNumber");
				strContent.append(value).append('#');
				value = obj.getAttribute("Status");
				strContent.append(value).append('#');
				value = obj.getAttribute("UnitOfMeasure");
				strContent.append(value).append('#');
				value = obj.getAttribute("NumCavities");
				strContent.append(value).append('#');
				value = obj.getAttribute("ReplacedBy") == null ? "Test":obj.getAttribute("ReplacedBy");
				strContent.append(value).append('#');
				value = obj.getAttribute("RevisionStatus");
				strContent.append(value).append('#');
				value = obj.getAttribute("Specification") == null ? "Test":obj.getAttribute("Specification");
				strContent.append(value).append('#');
				value = obj.getAttribute("StripLength");
				strContent.append(value).append('#');
				value = obj.getAttribute("UserField1") == null ? "TestUser":obj.getAttribute("UserField1");
				strContent.append(value).append('#');
				value = obj.getAttribute("UserField2")  == null ? "TestUser":obj.getAttribute("UserField2");
				strContent.append(value).append('#');
				value = obj.getAttribute("UserField3")  == null ? "TestUser":obj.getAttribute("UserField3");
				strContent.append(value).append('#');
				value = obj.getAttribute("UserField4")  == null ? "TestUser":obj.getAttribute("UserField4");
				strContent.append(value).append('#');
				value = obj.getAttribute("UserField5") == null ? "TestUser":obj.getAttribute("UserField5");
				strContent.append(value).append('#');
				value = obj.getAttribute("Weight");
				strContent.append(value).append('#');
				value = obj.getAttribute("Width");
				strContent.append(value).append('#');
				value = obj.getAttribute("AlternatePartNumber")  == null ? "AlternatePart":obj.getAttribute("AlternatePartNumber");
				strContent.append(value).append('#');
				value = obj.getAttribute("AnalysisModel")  == null ? "AnalysisModel":obj.getAttribute("AnalysisModel");
				strContent.append(value).append('#');
				value = obj.getAttribute("Depth");
				strContent.append(value).append('#');
				value = obj.getAttribute("Description")   == null ? "Dummy Desc":obj.getAttribute("Description");
				strContent.append(value).append('#');
				value = obj.getAttribute("KnockOff");
				strContent.append(value).append('#');
				value = obj.getAttribute("Length");
				strContent.append(value);
				try {
					dataStream.write(strContent.toString());
					dataStream.newLine();
					strContent.delete(0,strContent.length());
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		try {
			dataStream.close();
			fileStream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

}
