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
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXLibraryWire;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.plugin.library.IXLibraryValidator;
import com.mentor.chs.plugin.library.IXValidationReporter;
import java.util.Set;



public class  LibraryCustomValidation implements IXLibraryValidator
{
   public LibraryCustomValidation(){

   }
	public void validate(Context context, IXValidationReporter reporter, IXObject... obj)
	{
		for(IXObject xObj : obj){
			if(xObj instanceof IXLibraryWire){
				Set<IXValue> attValues = xObj.getAttributes();
				for(IXValue val : attValues){
					if(val.getName().equalsIgnoreCase("OutsideDiameter")){
						String value = val.getValue();
						double d = Double.parseDouble(value);
						if(d > 999){
							reporter.report(xObj,IXValidationReporter.Severity.ERROR,"Outside diameter of wire should be less than 999");
						}
						if(d > 0 && d < 0.0001){
							reporter.report(xObj,IXValidationReporter.Severity.WARNING,"Outside diameter of wire should be more than 0.0001");
						}
					}
				}
			}
		}
	}

	public String getDescription()
	{
		return "Library Custom validation sample";
	}

	public String getName()
	{
		return "LibraryCustomValidation";
	}

	public String getVersion()
	{
		return "1.0";  
	}
}




