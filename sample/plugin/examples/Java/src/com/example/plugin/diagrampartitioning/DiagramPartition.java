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
import com.mentor.chs.api.IXLogicDiagram;

import java.util.ArrayList;
import java.util.List;

public class DiagramPartition implements IDiagramPartition
{

	private String m_name;
	private IPartitioningData m_context;
	private List<IColumn> m_columns = new ArrayList<IColumn>();
	private IXLogicDiagram m_diagram;

	public DiagramPartition(String name, IXLogicDiagram diagram, IPartitioningData context)
	{
		m_name = name;
		m_diagram = diagram;
		m_context = context;
	}

	public String getName()
	{
		return m_name;
	}

	public void setName(String name)
	{
		m_name = name;
	}

	public IXLogicDiagram getDiagram()
	{
		return m_diagram;
	}

	public List<IColumn> getColumns()
	{
		return m_columns;
	}

	public IColumn addColumn(String columnName, String groupName)
	{
		IXLogicDesign design = getFunctionalDesign();
		IColumn column = m_context.getColumn(columnName, groupName, design);
		if (column != null) {
			m_columns.add(column);
		}
		return column;
	}

	public IXLogicDesign getFunctionalDesign()
	{
		IXLogicDiagram diagram = getDiagram();
		if (diagram != null) {
			return (IXLogicDesign) diagram.getDesign();
		}
		return null;
	}

	public IColumn removeColumn(String columnName)
	{
		IColumn columnTobeRemoved = null;
		for (IColumn column : m_columns) {
			if (columnName.equals(column.getName())) {
				columnTobeRemoved = column;
				break;
			}
		}
		if (columnTobeRemoved != null) {
			m_columns.remove(columnTobeRemoved);
		}
		return columnTobeRemoved;
	}

	public void clearColumns()
	{
		m_columns.clear();
	}
}
