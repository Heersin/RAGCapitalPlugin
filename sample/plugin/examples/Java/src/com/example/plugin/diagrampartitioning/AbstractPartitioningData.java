package com.example.plugin.diagrampartitioning;

import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.wiringdesigngenerator.IXWiringDesignGeneratorContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractPartitioningData implements IPartitioningData
{

	private Map<IXLogicDesign, Map<IXLogicDiagram, List<IDiagramPartition>>> m_designToDiagramPartitions =
			new HashMap<IXLogicDesign, Map<IXLogicDiagram, List<IDiagramPartition>>>();
	protected Map<IXLogicDesign, List<IColumn>> m_designToColumns = new HashMap<IXLogicDesign, List<IColumn>>();
	private IXWiringDesignGeneratorContext m_context;

	private String m_undistributedContentDiagramName;

	public AbstractPartitioningData(IXWiringDesignGeneratorContext wiringDesignGeneratorContext)
	{
		m_context = wiringDesignGeneratorContext;
	}

	@Override public Collection<IXLogicDesign> getFunctionalDesigns()
	{
		return new ArrayList<IXLogicDesign>(m_context.getFunctionalDesigns());
	}

	@Override public Map<IXLogicDesign, Map<IXLogicDiagram, List<IDiagramPartition>>> getDiagramPartitions()
	{
		return m_designToDiagramPartitions;
	}

	@Override public Map<IXLogicDesign, List<IColumn>> getApplicableColumns()
	{
		return m_designToColumns;
	}

	@Override public String getUndistributedContentDiagramName()
	{
		return m_undistributedContentDiagramName;
	}

	@Override public void setDiagramNameForUndistributedContent(String diagramNameForUndistributedContent)
	{
		if (diagramNameForUndistributedContent != null && !diagramNameForUndistributedContent.isEmpty()) {
			m_undistributedContentDiagramName = diagramNameForUndistributedContent;
		}
	}

	@Override public IColumn getColumn(String columnName, String groupName, IXLogicDesign design)
	{
		if (columnName == null || columnName.trim().isEmpty()) {
			return null;
		}

		List<IColumn> columns = m_designToColumns.get(design);
		if (columns != null) {
			for (IColumn column : columns) {
				if (columnName.equals(column.getName())) {
					String currentColGrpName = column.getGroupName();
					if (groupName == null || groupName.trim().isEmpty()) {
						if (currentColGrpName == null || currentColGrpName.trim().isEmpty()) {
							return column;
						}
					}
					else {
						if (currentColGrpName != null && !currentColGrpName.trim().isEmpty() &&
								groupName.equals(currentColGrpName)) {
							return column;
						}
					}
				}
			}
		}
		return null;
	}

	@Override public IDiagramPartition addDiagramPartition(IXLogicDiagram diagram, String partitionName)
	{
		IXLogicDesign design = (IXLogicDesign) diagram.getDesign();
		Map<IXLogicDiagram, List<IDiagramPartition>> designPartitions = getDesignPartitions(design);
		IDiagramPartition partition = null;
		if (designPartitions != null) {
			partition = new DiagramPartition(partitionName, diagram, this);
			List<IDiagramPartition> diagramPartitions = designPartitions.get(diagram);
			if (diagramPartitions == null) {
				diagramPartitions = new ArrayList<IDiagramPartition>();
				designPartitions.put(diagram, diagramPartitions);
			}
			diagramPartitions.add(partition);
		}
		return partition;
	}

	@Override public List<IDiagramPartition> getDiagramPartitions(IXLogicDiagram diagram)
	{
		if (diagram == null) {
			return Collections.emptyList();
		}

		IXLogicDesign design = (IXLogicDesign) diagram.getDesign();
		Map<IXLogicDiagram, List<IDiagramPartition>> designPartitions = getDesignPartitions(design);
		if (designPartitions != null) {
			List<IDiagramPartition> diagramPartitions = designPartitions.get(diagram);
			return (diagramPartitions == null) ? Collections.<IDiagramPartition>emptyList() :
					new ArrayList<IDiagramPartition>(diagramPartitions);
		}
		return Collections.emptyList();
	}

	private Map<IXLogicDiagram, List<IDiagramPartition>> getDesignPartitions(IXLogicDesign design)
	{
		if (design != null && !getFunctionalDesigns().contains(design)) {
			return null;
		}
		Map<IXLogicDiagram, List<IDiagramPartition>> diagramPartitions = m_designToDiagramPartitions.get(design);
		if (diagramPartitions == null) {
			diagramPartitions = new HashMap<IXLogicDiagram, List<IDiagramPartition>>();
			m_designToDiagramPartitions.put(design, diagramPartitions);
		}
		return diagramPartitions;
	}

	@Override public void clearPartitions(IXLogicDesign logicDesign)
	{
		Map<IXLogicDiagram, List<IDiagramPartition>> partitions = getDesignPartitions(logicDesign);
		if (partitions != null) {
			partitions.clear();
		}
	}

	@Override public IXLogicDesign getFunctionalDesign(String name, String revision)
	{
		if (name == null || name.trim().isEmpty()) {
			return null;
		}

		for (IXLogicDesign design : getFunctionalDesigns()) {
			if (name.equals(design.getAttribute(IXAttributes.Name))) {
				String designRevision = design.getAttribute(IXAttributes.Revision);
				if (revision == null || revision.trim().isEmpty()) {
					if (designRevision == null || designRevision.isEmpty()) {
						return design;
					}
				}
				else {
					if (revision.equals(design.getAttribute(IXAttributes.Revision))) {
						return design;
					}
				}
			}
		}
		return null;
	}
}
