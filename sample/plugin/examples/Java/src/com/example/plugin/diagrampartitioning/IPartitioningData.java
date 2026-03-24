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

import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.IXLogicDiagram;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface IPartitioningData
{

	/**
	 * Returns list of applicable columns of functional designs
	 *
	 * @return list of applicable columns of functional designs
	 */
	Map<IXLogicDesign, List<IColumn>> getApplicableColumns();

	Map<IXLogicDesign, Map<IXLogicDiagram, List<IDiagramPartition>>> getDiagramPartitions();

	String getUndistributedContentDiagramName();

	void setDiagramNameForUndistributedContent(String diagramNameForUndistributedContent);

	IColumn getColumn(String columnName, String groupName, IXLogicDesign design);

	boolean addApplicableColumn(IXLogicDesign design, String columnName, String groupName);

	IDiagramPartition addDiagramPartition(IXLogicDiagram diagram, String partitionName);

	List<IDiagramPartition> getDiagramPartitions(IXLogicDiagram diagram);

	Collection<IXLogicDesign> getFunctionalDesigns();

	void clearPartitions(IXLogicDesign logicDesign);

	/**
	 * Returns the functional design with name as <code>name</code>.
	 *
	 * @param name name of the required design
	 * @param revision revision of the design
	 *
	 * @return design with name as <code>name</code> found, otherwise <b>null</b>
	 */
	IXLogicDesign getFunctionalDesign(String name, String revision);
}
