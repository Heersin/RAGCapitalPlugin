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
import com.mentor.chs.api.IXOption;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.wiringdesigngenerator.IXWiringDesignGeneratorContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartitioningData extends AbstractPartitioningData implements IPartitioningData
{

	private IApplicableColumnFinder m_columnFinder;

	public PartitioningData(IXWiringDesignGeneratorContext wiringDesignGeneratorContext,
			IApplicableColumnFinder columnFinder,
			IXProject project)
	{
		super(wiringDesignGeneratorContext);
		m_columnFinder = columnFinder;
		initApplicableColumns(project);
	}

	private void initApplicableColumns(IXProject project)
	{
		if (m_columnFinder == null) {
			return;
		}
		for (IXLogicDesign design : getFunctionalDesigns()) {
			List<IColumn> columns = m_columnFinder.getApplicableColumns(design, getProjectOptions(project));
			m_designToColumns.put(design, columns);
		}
	}

	@Override public boolean addApplicableColumn(IXLogicDesign design, String columnName, String groupName)
	{
		return false;
	}

	private Map<String, IXOption> getProjectOptions(IXProject project)
	{
		if (project == null) {
			return Collections.emptyMap();
		}

		Map<String, IXOption> projectOptions = new HashMap<String, IXOption>();
		for (IXOption xOption : project.getOptions()) {
			projectOptions.put(xOption.getName(), xOption);
		}
		return projectOptions;
	}
}
