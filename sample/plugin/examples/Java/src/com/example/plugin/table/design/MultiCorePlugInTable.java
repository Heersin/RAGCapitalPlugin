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
package com.example.plugin.table.design;


import java.util.LinkedHashMap;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.table.IXHarnessTable;
import com.mentor.chs.plugin.table.IXTableData;
import com.mentor.chs.api.IXMulticore;

public class MultiCorePlugInTable implements IXHarnessTable 
{
	//..Plugin table for Multicore implements 6 Methods
	//  and 'class XTableData'

	
	
	MultiCoreProcessor multicoreProcessor = new MultiCoreProcessor();
	private LinkedHashMap<IXMulticore,MultiCoreDataClass> MulticoreDataMap ;
	IXMulticore[] MulticoreKeyArray ;	
	
	private IXTableData m_xTableData;
//	the composite design
	private IXHarnessDesign compositeDesign = null;
	
	public String getDescription()
	{
		return "Plugin Table for Multicores ";
	}

	public String getName()
	{
		return "Multicore_PluginTable";
	}

	public String getVersion()
	{
		return "1.0";
	}

	@SuppressWarnings("unchecked")
	
	public Class getTableContext()
	{
		return IXHarnessDesign.class;
	}

	public void initialize(IXObject xObject)
		{		
			if (xObject instanceof IXHarnessDesign) 
			{	
				compositeDesign = (IXHarnessDesign)xObject;
				multicoreProcessor.initData(compositeDesign);//to set up all multicore data		
				MulticoreDataMap 	= multicoreProcessor.getMulticoreDataMap();						
				MulticoreKeyArray 	= (IXMulticore[])MulticoreDataMap.keySet().toArray(
						new IXMulticore[MulticoreDataMap.size()]);			
			//to construct the 	table with data
			m_xTableData = new XTableData();
			}
		}
	
	public IXTableData populate(IXObject xObject)
	{
		return m_xTableData;
	}

	
	class XTableData implements IXTableData
	{
		public int getColumnCount()
		{
			return 10;//No of Columns
		}

		public int getRowCount()
		{
			int RowCount = 0;
			if (MulticoreDataMap.size() > 0)
			{				
				RowCount = MulticoreDataMap.size();
			}
			return RowCount;//No of Rows = No of Multicores		
		}

		public int getHeaderCount()
		{
			return 1;//No of headers 
		}

		public Object getCellValueAt(int row, int column)
		{
			
			Object obj = " ";
			if (MulticoreDataMap.size() >0)
			{
				IXMulticore thisMulticore = MulticoreKeyArray[row];
				
				switch (column) {
				
				case 0:
					 //"Multicore Name ";
					obj = MulticoreDataMap.get(thisMulticore).getMulticore_Name();
					break;
				case 1:
					// "Multicore Option ";
					obj = MulticoreDataMap.get(thisMulticore).getMulticore_Option();
					break;
				
				case 2://"Multicore Type "
					obj =  MulticoreDataMap.get(thisMulticore).getMulticore_Type();
					break;
				case 3://"Internal Part Number "
					obj =  MulticoreDataMap.get(thisMulticore).getInternal_Part_Number();
					break;
				
				case 4://"Outer Color"
					obj =  MulticoreDataMap.get(thisMulticore).getOuter_Color();
					break;
				case 5://"Outer Material "
					obj =  MulticoreDataMap.get(thisMulticore).getOuter_Material();
					break;
				case 6://"Outer Spec "
					obj =  MulticoreDataMap.get(thisMulticore).getOuter_Spec();
					break;
			
				case 7://"Pitch "						
					obj =  MulticoreDataMap.get(thisMulticore).getPitch();												
					break;
				case 8://"Multiplication factor"						
					obj =  MulticoreDataMap.get(thisMulticore).getMultiplication_factor();
					break;
				case 9:
					//" In-House"getIsInhouse;
					obj =  MulticoreDataMap.get(thisMulticore).getIsInhouse();
					break;
					// Can add more properties of a multicore   from here on with each case /column representing a multicore property .............
				default:
					obj = " ";
				    break;
				}//end of switch 
			}
			if (obj == null) {
				obj = " ";
			}
			return obj;
		}

		public Object getHeaderValueAt(int rowIndex,int colIndex)
		{
			Object obj = null;
			switch (colIndex) 
			{

				case 0:
					obj = "Multicore Name ";
					break;
					
				case 1:
					obj = "Multicore Option ";
					break;
				
				case 2:
					obj = "Multicore Type ";
					break;
				case 3:
					obj = "Internal Part Number ";
					break;
				
				case 4:
					obj = "Outer Color";
					break;
				case 5:
					obj = "Outer Material ";
					break;
				case 6:
					obj = "Outer Spec ";
					break;
			
				case 7:
					obj = "Pitch ";
					break;
				case 8:
					obj = "Multiplication factor";
					break;
				case 9:
					obj = " In-House";
					break;					
					
			}//end of switch 

			return obj;
		}//end  of getHeaderValueAt
	}//End of XTableData
  }
	


