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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ColumnSpecifier implements IColumnSpecifier
{

	private Map<String, IColumnGroup> m_nameToColumnGroups = new LinkedHashMap<String, IColumnGroup>();
	private List<IColumnGroup> m_columnGroups = new ArrayList<IColumnGroup>();

	private static final String NO_GROUP = "";

	public ColumnSpecifier(Collection<IColumn> columns)
	{
		if (columns == null || columns.isEmpty()) {
			return;
		}

		for (IColumn column : columns) {
			addColumn(column);
		}
	}

	public void addColumn(IColumn column)
	{
		if (column == null) {
			return;
		}

		String groupName = column.getGroupName();
		IColumnGroup columnGroup = getColumnGroup(groupName);
		columnGroup.addColumn(column);
	}

	private IColumnGroup getColumnGroup(String groupName)
	{
		String groupNameToUse = groupName;
		if (groupNameToUse == null || "".equals(groupNameToUse.trim())) {
			groupNameToUse = NO_GROUP;
		}
		IColumnGroup columnGroup = m_nameToColumnGroups.get(groupNameToUse);
		if (columnGroup == null) {
			columnGroup = createColumnGroup(groupNameToUse);
			m_nameToColumnGroups.put(groupNameToUse, columnGroup);
		}

		return columnGroup;
	}

	private ColumnGroup createColumnGroup(String groupName)
	{
		ColumnGroup columnGroup = new ColumnGroup(groupName);
		m_columnGroups.add(columnGroup);
		return columnGroup;
	}

	public List<IColumnGroup> getGroups()
	{
		List<IColumnGroup> grps = new ArrayList<IColumnGroup>(m_columnGroups);
		Collections.sort(grps, new Comparator<IColumnGroup>()
		{
			@Override public int compare(IColumnGroup o1, IColumnGroup o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		return grps;
	}

	public List<IColumn> getColumns()
	{
		Set<IColumn> allColumns = new LinkedHashSet<IColumn>();
		for (IColumnGroup columnGroup : m_columnGroups) {
			List<IColumn> columns = columnGroup.getColumns();
			assert Collections.disjoint(allColumns, columns);
			allColumns.addAll(columns);
		}

		return new ArrayList<IColumn>(allColumns);
	}

	private static class ColumnGroup implements IColumnGroup
	{

		private List<IColumn> m_columns = new ArrayList<IColumn>();
		private String m_groupName;

		private ColumnGroup(String groupName)
		{
			m_groupName = groupName;
		}

		public void addColumn(IColumn column)
		{
			if (m_columns.contains(column)) {
				return;
			}
			m_columns.add(column);
		}

		public List<IColumn> getColumns()
		{
			return m_columns;
		}

		public String getName()
		{
			return m_groupName;
		}
	}
}
