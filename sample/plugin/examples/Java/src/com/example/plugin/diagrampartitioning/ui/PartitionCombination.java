/*
 * Copyright 2013 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */

package com.example.plugin.diagrampartitioning.ui;

import com.mentor.chs.api.IXObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PartitionCombination<T> implements IPartitionCombination<T>
{

	private String m_name;
	private IXObject m_sourceObject;
	protected Set<T> m_selectedColumns = new HashSet<T>();

	public PartitionCombination(IXObject sourceObject, String name)
	{
		m_sourceObject = sourceObject;
		m_name = name;
	}

	public PartitionCombination(String name)
	{
		m_name = name;
	}

	public PartitionCombination(String name, IPartitionCombination<T> partition)
	{
		m_name = name;
		for (T obj : partition.getSelectedColumns()) {
			setValue(obj, true);
		}
	}

	public String getName()
	{
		return m_name;
	}

	public void setValue(T columnName, boolean value)
	{
		if (value) {
			m_selectedColumns.add(columnName);
		}
		else {
			m_selectedColumns.remove(columnName);
		}
	}

	public boolean getValue(T columnName)
	{
		return m_selectedColumns.contains(columnName);
	}

	public IXObject getSourceObject()
	{
		return m_sourceObject;
	}

	public void setName(String newName)
	{
		m_name = newName;
	}

	public String toString()
	{
		return getName();
	}

	public List<T> getSelectedColumns()
	{
		return new ArrayList<T>(m_selectedColumns);
	}
}
