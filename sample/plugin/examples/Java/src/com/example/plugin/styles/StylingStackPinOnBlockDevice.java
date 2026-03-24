/*
 * Copyright 2015 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */
package com.example.plugin.styles;

import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXBlockPin;
import com.mentor.chs.api.IXLogicDiagramStackedPin;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.plugin.query.IXCustomResultExpression;

import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

/**
 * @author chandras on 06-10-2015.
 */
public class StylingStackPinOnBlockDevice implements IXCustomResultExpression
{

	@Override public Object evaluate(IXObject entity)
	{
		return doEvaluate(entity);
	}

	@Override public Object evaluate(IXObject entity, String executionContext)
	{
		return doEvaluate(entity);
	}

	private Object doEvaluate(IXObject entity)
	{
		StringBuilder result = new StringBuilder();
		if (entity instanceof IXLogicDiagramStackedPin) {
			IXLogicDiagramStackedPin stackedPin = (IXLogicDiagramStackedPin) entity;
			Set<String> associatedPinListNames = new TreeSet<String>();
			for (IXAbstractPin pin : stackedPin.getPins()) {
				if (pin instanceof IXBlockPin) {
					String blockPinName = pin.getAttribute(IXAttributes.Name);
					StringTokenizer tokenizer = new StringTokenizer(blockPinName, ":");
					associatedPinListNames.add(tokenizer.hasMoreElements() ? tokenizer.nextToken() : blockPinName);
				}
			}
			for (String pinListName : associatedPinListNames) {
				if (result.length() > 0) {
					result.append("\n");
				}
				result.append(pinListName);
			}
		}
		return result;
	}

	public String getDescription()
	{
		return "Computes the associated pinlists by the pins of a stackpin on a block";
	}

	public String getName()
	{
		return "Styling StackPin On BlockDevice Plugin";
	}

	public String getVersion()
	{
		return "1.0";
	}

	@Override public Context[] getApplicableContexts()
	{
		return new Context[0];
	}
}
