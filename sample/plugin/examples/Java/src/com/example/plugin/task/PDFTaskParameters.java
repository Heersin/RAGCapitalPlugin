/**
 * Copyright 2012 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.task;

import java.io.InputStream;

/**
 * it looks like this <?xml version="1.0" encoding="UTF-8"?> <CustomTaskParameters> <Param name="projectname"
 * value="Quick Start - Aerospace Interactive"/> <Param name="imagewidth" value="21.0"/> <Param name="imageheight"
 * value="29.7"/> <Param name="orientation" value="LANDSCAPE"/> <Param name="color" value="BLACK_AND_WHITE"/>
 * <p/>
 * </CustomTaskParameters>
 * we can create a xml file like above and use it in submit task as a parameter file for PDFGenerationTask
 */
public class PDFTaskParameters extends TaskNameValueParameters
{

	public PDFTaskParameters()
	{

	}

	public PDFTaskParameters(InputStream reader)
	{
		super(reader);
	}

	public String getProjectName()
	{
		return getStringParameter("projectname");
	}

	public void setProjectName(String name)
	{
		setStringParameter("projectname", name);
	}

	public double getImageWidth()
	{
		return getDoubleParameter("imagewidth");
	}

	public void setImageWidth(double imageWidth)
	{
		setDoubleParameter("imagewidth", imageWidth);
	}

	public double getImageHeight()
	{
		return getDoubleParameter("imageheight");
	}

	public void setImageHeight(double imageHeight)
	{
		setDoubleParameter("imageheight", imageHeight);
	}

	public String getOrientation()
	{
		return getStringParameter("orientation");
	}

	public void setOrientation(String pagesize)
	{
		setStringParameter("orientation", pagesize);
	}

	public String getColor()
	{
		return getStringParameter("color");
	}

	public void setColor(String color)
	{
		setStringParameter("color", color);
	}
}
