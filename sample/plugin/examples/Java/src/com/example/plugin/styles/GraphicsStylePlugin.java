/**
 * Copyright 2008 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.styles;

import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXDevice;
import com.mentor.chs.api.IXNet;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXSlot;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.plugin.styles.IXGraphicsAttribute;
import com.mentor.chs.plugin.styles.IXGraphicsStyle;
import com.mentor.chs.plugin.styles.FILL_PATTERN_STYLE;

import java.awt.Color;

public class GraphicsStylePlugin implements IXGraphicsStyle
{

	public void updateGraphicsAttribute(IXObject object, IXGraphicsAttribute graphicsAttribute)
	{
		if (object instanceof IXDevice) {
			String CriticalValue = object.getProperty("Critical");
			graphicsAttribute.setLineThickness(2);
			graphicsAttribute.setLineStyle(IXGraphicsAttribute.LINE_STYLE.SOLID);
			if (CriticalValue != null) {
				graphicsAttribute.setPrimaryColor(Color.RED);
				graphicsAttribute.setFillPattern(FILL_PATTERN_STYLE.PATTERN_SOLID);
				graphicsAttribute.setFillForegroundColor(Color.ORANGE);
				graphicsAttribute.setFillBackgroundColor(Color.BLACK);
			}
			else {
				graphicsAttribute.setPrimaryColor(new Color(0, 0, 0));
				graphicsAttribute.setFillPattern(FILL_PATTERN_STYLE.PATTERN_NONE);
			}
		}
		else if (object instanceof IXConnector) {
			String CriticalValue = object.getProperty("Critical");
			graphicsAttribute.setLineThickness(2);
			graphicsAttribute.setLineStyle(IXGraphicsAttribute.LINE_STYLE.SOLID);
			if (CriticalValue != null) {
				graphicsAttribute.setPrimaryColor(Color.RED);
				graphicsAttribute.setFillPattern(FILL_PATTERN_STYLE.PATTERN_SOLID);
				graphicsAttribute.setFillForegroundColor(Color.ORANGE);
				graphicsAttribute.setFillBackgroundColor(Color.BLACK);
			}
			else {
				graphicsAttribute.setPrimaryColor(new Color(0, 0, 0));
				graphicsAttribute.setFillPattern(FILL_PATTERN_STYLE.PATTERN_NONE);
			}
		}
		else if (object instanceof IXNet) {
			String powerValue = object.getProperty("Power");
			String groundValue = object.getProperty("Ground");
			String signalValue = object.getProperty("Signal");
			graphicsAttribute.setLineThickness(2);
			graphicsAttribute.setLineStyle(IXGraphicsAttribute.LINE_STYLE.SOLID);
			if (powerValue != null) {
				graphicsAttribute.setPrimaryColor(Color.RED);
			}
			else if (groundValue != null) {
				graphicsAttribute.setPrimaryColor(Color.BLACK);
			}
			else if (signalValue != null) {
				graphicsAttribute.setPrimaryColor(Color.ORANGE);
			}
			else {
				graphicsAttribute.setPrimaryColor(new Color(0, 102, 102));
			}
		}
		else if (object instanceof IXWire) {
			String powerValue = object.getProperty("Power");
			String groundValue = object.getProperty("Ground");
			String signalValue = object.getProperty("Signal");
			graphicsAttribute.setLineThickness(2);
			graphicsAttribute.setLineStyle(IXGraphicsAttribute.LINE_STYLE.SOLID);
			if (powerValue != null) {
				graphicsAttribute.setPrimaryColor(Color.RED);
			}
			else if (groundValue != null) {
				graphicsAttribute.setPrimaryColor(Color.BLACK);
			}
			else if (signalValue != null) {
				graphicsAttribute.setPrimaryColor(Color.ORANGE);
			}
			else {
				graphicsAttribute.setPrimaryColor(new Color(0, 102, 102));
			}
		}
		else if (object instanceof IXSlot) {
			String CriticalValue = object.getProperty("Critical");
			graphicsAttribute.setLineThickness(2);
			graphicsAttribute.setLineStyle(IXGraphicsAttribute.LINE_STYLE.SOLID);
			if (CriticalValue != null) {
				graphicsAttribute.setPrimaryColor(Color.RED);
				graphicsAttribute.setFillPattern(FILL_PATTERN_STYLE.PATTERN_SOLID);
				graphicsAttribute.setFillForegroundColor(Color.ORANGE);
				graphicsAttribute.setFillBackgroundColor(Color.BLACK);
			}
			else {
				graphicsAttribute.setPrimaryColor(new Color(0, 0, 0));
				graphicsAttribute.setFillPattern(FILL_PATTERN_STYLE.PATTERN_NONE);
			}
		}
		else if (object instanceof IXAbstractPin) {
			graphicsAttribute.setPrimaryColor(Color.RED);
			graphicsAttribute.setFillPattern(FILL_PATTERN_STYLE.PATTERN_INHERIT);
			graphicsAttribute.setFillForegroundColor(Color.ORANGE);
			graphicsAttribute.setFillBackgroundColor(Color.BLACK);
		}

	}

	public String getDescription()
	{
		return "Sets colour of the Device, Connector, Net and Wire based on the existence of the property. Red for Power, Black for Ground, Orange for Signal";
	}

	public String getName()
	{
		return "Graphics Style Plugin";
	}

	public String getVersion()
	{
		return "1.0";
	}
}