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

public class Column implements IColumn
{

	private String m_name;
	private String m_groupName;

	public Column(String name, String groupName)
	{
		m_name = name;
		m_groupName = groupName;
	}

	public String getName()
	{
		return m_name;
	}

	public String getGroupName()
	{
		return m_groupName;
	}

	public void setGroupName(String groupName)
	{
		m_groupName = groupName;
	}
}

