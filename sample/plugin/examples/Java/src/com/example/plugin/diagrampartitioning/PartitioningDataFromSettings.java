/*
 * Copyright 2013 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */

package com.example.plugin.diagrampartitioning;

import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.wiringdesigngenerator.IXWiringDesignGeneratorContext;

import java.util.ArrayList;
import java.util.List;

public class PartitioningDataFromSettings extends AbstractPartitioningData implements IPartitioningData
{

	public PartitioningDataFromSettings(IXWiringDesignGeneratorContext wiringDesignGeneratorContext)
	{
		super(wiringDesignGeneratorContext);
	}

	@Override public boolean addApplicableColumn(IXLogicDesign design, String columnName, String groupName)
	{
		IColumn column = getColumn(columnName, groupName, design);
		if (column == null) {
			column = new Column(columnName, groupName);
			List<IColumn> applicableColumns = m_designToColumns.get(design);
			if (applicableColumns == null) {
				applicableColumns = new ArrayList<IColumn>();
				m_designToColumns.put(design, applicableColumns);
			}
			applicableColumns.add(column);
		}
		return true;
	}
}
