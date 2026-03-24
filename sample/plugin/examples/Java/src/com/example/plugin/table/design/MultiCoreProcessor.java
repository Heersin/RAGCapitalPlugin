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
import java.util.Set;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXMulticore;


public class MultiCoreProcessor {
	
	private Set<IXMulticore> mainMultiCoreSet = null;
	
	private LinkedHashMap<IXMulticore,MultiCoreDataClass> Multicore_Data_Map 
			= new LinkedHashMap<IXMulticore,MultiCoreDataClass>() ;

	public void initData(IXHarnessDesign compositeHarn)
	{
		Multicore_Data_Map.clear();
//		Retrieves the parent level multicores.
		mainMultiCoreSet = compositeHarn.getHarness().getMulticores() ;
		
		for (IXMulticore curr_multicore : mainMultiCoreSet)
			{
				setMulticoreData(curr_multicore,curr_multicore);
				setInnerCoreData(curr_multicore);
			}
	}

	public void setInnerCoreData(IXMulticore this_multicore)
	{	
		
		if ( this_multicore.getMulticores().size()>0)
		{
			for (IXMulticore x_mc : this_multicore.getMulticores())
			{
				setMulticoreData(x_mc,this_multicore);
				setInnerCoreData(x_mc);
				
			}
		}
		
	}
	
	public void setMulticoreData(IXMulticore this_multicore,IXMulticore parent_multicore)
	{
		MultiCoreDataClass thisMulticoreObj = new MultiCoreDataClass(this_multicore,parent_multicore);
		try
		{
		//Adding the data to main MAP
			Multicore_Data_Map.put(this_multicore, thisMulticoreObj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}//End of setMulticoreData Method


	public LinkedHashMap<IXMulticore,MultiCoreDataClass> getMulticoreDataMap()
	{
		return Multicore_Data_Map;
	}

}
