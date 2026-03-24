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

import com.example.plugin.diagrampartitioning.AlphaNumComparator;
import com.example.plugin.diagrampartitioning.ColumnSpecifier;
import com.example.plugin.diagrampartitioning.IColumn;
import com.example.plugin.diagrampartitioning.IColumnSpecifier;
import com.example.plugin.diagrampartitioning.IDiagramPartition;
import com.example.plugin.diagrampartitioning.IPartitioningData;
import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.IXLogicDiagram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartitionModel
{

	public static final PartitionModel EMPTY_MODEL =
			new PartitionModel(Collections.<IXLogicDesign, IColumnSpecifier>emptyMap(), "UndistributedContentDiagram");
	private HashMap<IXLogicDesign, IDesignPartitionModel> m_designToPartition =
			new HashMap<IXLogicDesign, IDesignPartitionModel>();
	private String m_undistributedDiagramName = "UndistributedContentDiagram";
	private IPartitioningData m_partitioningData;

	private PartitionModel(Map<IXLogicDesign, IColumnSpecifier> designsToColumnSpecifierMap,
			String undistributedContentDiagramName)
	{
		buildDesignPartitions();
		setUndistributedDiagramName(undistributedContentDiagramName);
	}

	public PartitionModel(IPartitioningData partitioningData)
	{
		m_partitioningData = partitioningData;
		buildDesignPartitions();
		setUndistributedDiagramName(partitioningData.getUndistributedContentDiagramName());
	}

	private Map<IXLogicDesign, IColumnSpecifier> getDesignToColumnSpecifier(IPartitioningData partitioningData)
	{
		if (partitioningData == null) {
			return Collections.emptyMap();
		}

		Map<IXLogicDesign, IColumnSpecifier> designToColumnSpecifier = new HashMap<IXLogicDesign, IColumnSpecifier>();
		for (Map.Entry<IXLogicDesign, List<IColumn>> entry : partitioningData.getApplicableColumns()
				.entrySet()) {
			IXLogicDesign design = entry.getKey();
			List<IColumn> columns = entry.getValue();
			IColumnSpecifier columnSpecifier = new ColumnSpecifier(columns);
			designToColumnSpecifier.put(design, columnSpecifier);
		}
		return designToColumnSpecifier;
	}

	private void buildDesignPartitions()
	{
		Map<IXLogicDesign, IColumnSpecifier> designsToColumnSpecifierMap =
				getDesignToColumnSpecifier(m_partitioningData);

		m_designToPartition = new HashMap<IXLogicDesign, IDesignPartitionModel>(designsToColumnSpecifierMap.size());
		for (Map.Entry<IXLogicDesign, IColumnSpecifier> entry : designsToColumnSpecifierMap
				.entrySet()) {
			IXLogicDesign design = entry.getKey();
			IColumnSpecifier columnSpecifier = entry.getValue();

			IDesignDiagramsPartitionGroup designComposite =
					PartitionHelper.createDesignDiagramsPartitionGroup(design);
			for (IXDiagram diagram : design.getDiagrams()) {
				IDiagramPartitionGroup diagramComposite = PartitionHelper.createDiagramPartitionGroup(diagram);
				designComposite.add(diagramComposite);
				List<IDiagramPartition> xDiagramPartitions =
						m_partitioningData.getDiagramPartitions((IXLogicDiagram) diagram);
				for (IDiagramPartition xDiagramPartition : xDiagramPartitions) {
					IPartitionCombination<IColumn> partitionCombination =
							new PartitionCombination<IColumn>(xDiagramPartition.getName());
					diagramComposite.add(partitionCombination);
					for (IColumn xColumn : xDiagramPartition.getColumns()) {
						partitionCombination.setValue(xColumn, true);
					}
				}
			}

			IDesignPartitionModel model = new DesignPartitionModel(design, columnSpecifier, designComposite);
			m_designToPartition.put(design, model);
		}
	}

	public String getUndistributedContentDiagramName()
	{
		return m_undistributedDiagramName;
	}

	public void setUndistributedDiagramName(String newName)
	{
		if (newName != null && newName.length() > 0) {
			m_undistributedDiagramName = newName;
		}
	}

	public List<IDesignPartitionModel> getModels()
	{
		List<IDesignPartitionModel> designPartitions =
				new ArrayList<IDesignPartitionModel>(m_designToPartition.values());
		Collections.sort(designPartitions, new DesignPartitionModelComparator());
		return designPartitions;
	}

	public List<IPartitionCombination<IColumn>> getPartitions(IXLogicDiagram diagram, IXLogicDesign design)
	{
		List<IPartitionCombination<IColumn>> diagramPartitions = new ArrayList<IPartitionCombination<IColumn>>();
		IDesignPartitionModel designPartitionModel = m_designToPartition.get(design);
		for (IDiagramPartitionGroup diagramPartition : designPartitionModel.getDesignPartition().getObjects()) {
			if (diagramPartition.getSource() == diagram) {
				diagramPartitions.addAll(diagramPartition.getObjects());
				break;
			}
		}
		return diagramPartitions;
	}

	public void updatePartitioningData(IPartitioningData partitioningData)
	{

		for (IXLogicDesign design : partitioningData.getFunctionalDesigns()) {
			partitioningData.clearPartitions(design);

			for (IXDiagram obj : design.getDiagrams()) {
				IXLogicDiagram diagram = (IXLogicDiagram) obj;
				List<IPartitionCombination<IColumn>> combinations = getPartitions(diagram, design);
				for (IPartitionCombination<IColumn> combination : combinations) {
					IDiagramPartition diagramPartition =
							partitioningData.addDiagramPartition(diagram, combination.getName());
					for (IColumn selectedColumn : combination.getSelectedColumns()) {
						String groupName = selectedColumn.getGroupName();
						diagramPartition.addColumn(selectedColumn.getName(), groupName);
					}
				}
			}
		}
		partitioningData.setDiagramNameForUndistributedContent(getUndistributedContentDiagramName());
	}

	public interface IDesignPartitionModel
	{

		IColumnSpecifier getColumnSpecifier();

		IDesignDiagramsPartitionGroup getDesignPartition();

		IXLogicDesign getDesign();
	}

	private static class DesignPartitionModel implements IDesignPartitionModel
	{

		private IColumnSpecifier m_columnSpecifier;
		private IDesignDiagramsPartitionGroup m_designPartition;
		private IXLogicDesign m_design;

		private DesignPartitionModel(IXLogicDesign design, IColumnSpecifier columnSpecifier,
				IDesignDiagramsPartitionGroup designPartition)
		{
			m_design = design;
			m_columnSpecifier = columnSpecifier;
			m_designPartition = designPartition;
		}

		public IDesignDiagramsPartitionGroup getDesignPartition()
		{
			return m_designPartition;
		}

		public IColumnSpecifier getColumnSpecifier()
		{
			return m_columnSpecifier;
		}

		public IXLogicDesign getDesign()
		{
			return m_design;
		}

		public String toString()
		{
			return m_design.getAttribute(IXAttributes.Name);
		}
	}

	private static class DesignPartitionModelComparator implements Comparator<IDesignPartitionModel>
	{

		private final Comparator<String> m_comparator = new AlphaNumComparator<String>();

		@Override public int compare(IDesignPartitionModel o1, IDesignPartitionModel o2)
		{
			return m_comparator.compare(o1.getDesignPartition().getName(), o2.getDesignPartition().getName());
		}
	}
}
