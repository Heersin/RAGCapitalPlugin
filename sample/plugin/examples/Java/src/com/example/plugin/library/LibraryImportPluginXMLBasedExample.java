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
import com.mentor.chs.plugin.library.IXLibraryXMLImport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class LibraryImportPluginXMLBasedExample implements IXLibraryXMLImport
{
	private static Map<String,String> groupnameVsTag = new HashMap<String,String>();
	static{
		groupnameVsTag.put("Assembly","assemblypart");
		groupnameVsTag.put("Backshell","backshellpart");
		groupnameVsTag.put("Backshell Plug","backshellplugpart");
		groupnameVsTag.put("Backshell Seal","backshellsealpart");
		groupnameVsTag.put("Cavity Group","cavitygrouppart");
		groupnameVsTag.put("Cavity Plug","cavityplugpart");
		groupnameVsTag.put("Cavity Seal","cavitysealpart");
		groupnameVsTag.put("Clip","clippart");
		groupnameVsTag.put("Connector","connectorpart");
		groupnameVsTag.put("Connector Seal","connectorsealpart");
		groupnameVsTag.put("Device","devicepart");
		groupnameVsTag.put("Fixture","fixturepart");
		groupnameVsTag.put("Grommet","grommetpart");
		groupnameVsTag.put("Heat Shrink Sleeve","heatshrinksleevepart");
		groupnameVsTag.put("IDC Connector","idcconnectorpart");
		groupnameVsTag.put("In-House Assembly","inhouseassemblypart");
		groupnameVsTag.put("Multicore Wire","multicorepart");
		groupnameVsTag.put("Other","otherpart");
		groupnameVsTag.put("Solder Sleeve","soldersleevepart");
		groupnameVsTag.put("Splice","splicepart");
		groupnameVsTag.put("Tape","tapepart");
		groupnameVsTag.put("Terminal","terminalpart");
		groupnameVsTag.put("Tube","tubepart");
		groupnameVsTag.put("Ultrasonic Weld","ultrasonicweldpart");
		groupnameVsTag.put("Wire","wirepart");

	}

	public List<String> getLibraryImportFileExtensions()
	{
		 List<String> lstExtensions = new ArrayList<String>();
		 lstExtensions.add("abc");
		 return lstExtensions;

	}

	public String getLibraryImportFileExtensionDescription()
	{
		return "Library File Based Import ABC Files";
	}
   	// file format is  (all mandatory)
	//	groupname # colorcode value # Material Code value # Type Code value # Part Number # partstatus#
	//  unitofmeasure # cavityqt # replacedby # latest # specification # striplength #userf1# userf2 #
	//  userf3# userf4# userf5# weight# width# alternatepartnumber # ca_attach# depth# description# knockoff # length

	@SuppressWarnings({"StringContatenationInLoop", "MagicNumber"}) public Object importLibrary(File slectedLibraryfile, IXLibraryImportExportErrorReporter conversionErrorReporter)
	{
		try {
			StringBuilder strContent = new StringBuilder(1024);
			strContent.append("<?xml version=\"1.0\"?><chssystem ExportDate=\"12/28/2009\" ExportTime=\"14:58:14 IST\" DateFormat=\"MM/dd/yyyy\" NumberFormat=\"HH:mm:ss \" SchemaValidation=\"true\" ExportVersion=\"2010.1.1 [Build 0] - 18 December 2009 (14:23 IST)\"  xmlns=\"http://www.mentor.com/harness/Schema/LibrarySchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.mentor.com/harness/Schema/LibrarySchema file:/H:/chs_home/dtd/LibrarySchema.xsd\" XMLVersion = \"1.6\">");
			FileInputStream fis = new FileInputStream(slectedLibraryfile);
			InputStreamReader reader = new InputStreamReader(fis, "UTF-8");
			BufferedReader input = new BufferedReader(reader);
			Map<String,String> colcodeMap = new HashMap<String,String> ();
			Map<String,String> matcodeMap = new HashMap<String,String> ();
			Map<String,String> typecodeMap = new HashMap<String,String> ();
			String line;
			int colindex = 0;
			int matindex = 100;
			int typeindex = 200;
			int partindex = 300;
			boolean bIsDataAvailable = false;
			FileOutputStream fileStream = null;
			BufferedWriter dataStream = null;
			File tempUpg = null;
			while((line = input.readLine()) != null ){
				bIsDataAvailable = true;
				String[] contents = line.split("#");
				if (colcodeMap.get(contents[1]) == null) {
					strContent.append("<librarycolor" + ' ' + "librarycolor_id=" + '\"' + '_').append(colindex)
							.append("\"" + ' ' + "colorcode=" + '\"').append(contents[1])
							.append("\"" + ' ' + "description=" + '\"'  + contents[1] + '_' + "NA" + '\"' + ' ');
					strContent.append("/>");
					colcodeMap.put(contents[1],String.valueOf(colindex));
				}
				if (matcodeMap.get(contents[2]) == null) {
					strContent.append("<librarymaterial" + ' ' + "librarymaterial_id=" + '\"' + '_').append(matindex)
							.append("\"" + ' ' + "materialcode=" + '\"').append(contents[2])
							.append("\"" + ' ' + "description=" + '\"'  + contents[2] + '_' + "NA" + '\"' + ' ');
					strContent.append("/>");
					matcodeMap.put(contents[2], String.valueOf(matindex));
				}
				if (typecodeMap.get(contents[3]) == null) {
					strContent.append("<librarycomponenttype" + ' ' + "librarycomponenttype_id=" + '\"' + '_')
							.append(typeindex).append("\"" + ' ' + "typecode=" + '\"').append(contents[3])
							.append("\"" + ' ' + "description=" + '\"'  + contents[3] + '_' + "NA" + '\"' + ' ');
					strContent.append("/>");
					typecodeMap.put(contents[3], String.valueOf(typeindex));
				}
				String tag = groupnameVsTag.get(contents[0]);
				strContent.append('<').append(tag).append(' ');

				strContent.append("groupname=" + '\"').append(contents[0]).append("\"" + ' ');
				 conversionErrorReporter.log("Writing details about part number : " + contents[4]);
				strContent.append("partnumber=" + '\"').append(contents[4]).append("\"" + ' ');
				strContent.append("partstatus=" + '\"').append(contents[5]).append("\"" + ' ');
				strContent.append("unitofmeasure=" + '\"').append(contents[6]).append("\"" + ' ');
				strContent.append("cavityqt=" + '\"').append(contents[7]).append("\"" + ' ');
				strContent.append("replacedby=" + '\"').append(contents[8]).append("\"" + ' ');
				strContent.append("latest=" + '\"').append(contents[9]).append("\"" + ' ');
				strContent.append("specification=" + '\"').append(contents[10]).append("\"" + ' ');
				strContent.append("striplength=" + '\"').append(contents[11]).append("\"" + ' ');
				strContent.append("userf1=" + '\"').append(contents[12]).append("\"" + ' ');
				strContent.append("userf2=" + '\"').append(contents[13]).append("\"" + ' ');
				strContent.append("userf3=" + '\"').append(contents[14]).append("\"" + ' ');
				strContent.append("userf4=" + '\"').append(contents[15]).append("\"" + ' ');
				strContent.append("userf5=" + '\"').append(contents[16]).append("\"" + ' ');
				strContent.append("weight=" + '\"').append(contents[17]).append("\"" + ' ');
				strContent.append("width=" + '\"').append(contents[18]).append("\"" + ' ');
				strContent.append("alternatepartnumber=" + '\"').append(contents[19]).append("\"" + ' ');
				strContent.append("ca_attach=" + '\"').append(contents[20]).append("\"" + ' ');
				strContent.append("depth=" + '\"').append(contents[21]).append("\"" + ' ');
				strContent.append("description=" + '\"').append(contents[22]).append("\"" + ' ');
				strContent.append("knockoff=" + '\"').append(contents[23]).append("\"" + ' ');
				strContent.append("length=" + '\"').append(contents[24]).append("\"" + ' ');
				strContent.append("libraryobject_id=" + '\"' + '_').append(partindex).append("\"" + ' ');
				strContent.append("librarycolor_id=" + '\"' + '_').append(colcodeMap.get(contents[1]))
						.append("\"" + ' ');
				strContent.append("librarycomponenttype_id=" + '\"' + '_').append(typecodeMap.get(contents[3]))
						.append("\"" + ' ');
				strContent.append("librarymaterial_id=" + '\"' + '_').append(matcodeMap.get(contents[2]))
						.append("\"" + ' ');
				strContent.append('>');
				strContent.append("</").append(tag).append('>');

				++colindex;
				++typeindex;
				++matindex;
				++partindex;
				String filePath = "C:\\";
				tempUpg = File.createTempFile("upgrade_", ".xml", new File(filePath));
				tempUpg.deleteOnExit();
				fileStream = new FileOutputStream(tempUpg);
				dataStream = new BufferedWriter(new OutputStreamWriter(fileStream, "UTF-8"));
				dataStream.write(strContent.toString());


			}
			if(!bIsDataAvailable){
				conversionErrorReporter.error("Empty File Supplied");
			}
			if (dataStream != null) {
				dataStream.write("</chssystem>");
				dataStream.close();
				fileStream.close();
			}
			return tempUpg;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			conversionErrorReporter.error("No file found");

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		conversionErrorReporter.error("Pluggin Conversion failed");
		return null;
	}

	public String getDescription()
	{
		return "Library Import FileBased Example";
	}

	public String getName()
	{
		return "Library Import FileBased Example";
	}

	public String getVersion()
	{
		return "1.0"; 
	}
}
