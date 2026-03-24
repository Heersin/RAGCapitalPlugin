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

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class TaskNameValueParameters extends HashMap<String, String>
{

	private static final String PARAMETER_TYPE = "Param";

	public TaskNameValueParameters()
	{
	}

	public TaskNameValueParameters(InputStream reader)
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(reader);
			NodeList list = doc.getElementsByTagName(PARAMETER_TYPE);
			int length = list.getLength();
			for (int i = 0; i < length; i++) {
				Node node = list.item(i);
				String attributeName = node.getAttributes().item(0).getNodeValue();
				String attributeValue = node.getAttributes().item(1).getNodeValue();
				put(attributeName, attributeValue);
			}
		}
		catch (ParserConfigurationException e) {
			throw new IllegalArgumentException(e);
		}
		catch (SAXException e) {
			throw new IllegalArgumentException(e);
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public String getStringParameter(String parameterName)
	{
		return get(parameterName);
	}

	public void setStringParameter(String parameterName, String value)
	{
		put(parameterName, value);
	}

	public Double getDoubleParameter(String parameterName)
	{
		String value = get(parameterName);
		try {
			return Double.parseDouble(value);
		}
		catch (NumberFormatException ignore) {

		}
		return null;
	}

	public Integer getIntegerParameter(String parameterName)
	{
		String value = get(parameterName);
		try {
			return Integer.parseInt(value);
		}
		catch (NumberFormatException ignore) {

		}
		return null;
	}

	public void setDoubleParameter(String parameterName, Double value)
	{
		setStringParameter(parameterName, String.valueOf(value));
	}

	public void setIntegerParameter(String parameterName, Integer value)
	{
		setStringParameter(parameterName, String.valueOf(value));
	}

	public void setBooleanParameter(String parameterName, Boolean value)
	{
		setStringParameter(parameterName, String.valueOf(value));
	}

	public Boolean getBooleanParameter(String parameterName)
	{
		String value = get(parameterName);
		return Boolean.valueOf(value);
	}
}
