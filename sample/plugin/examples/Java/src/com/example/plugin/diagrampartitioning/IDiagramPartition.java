/*
 * Copyright 2010 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.   
 */
package com.example.plugin.diagrampartitioning;

import java.util.List;

public interface IDiagramPartition
{

	String getName();

	List<IColumn> getColumns();

	IColumn addColumn(String columnName, String groupName);
}
