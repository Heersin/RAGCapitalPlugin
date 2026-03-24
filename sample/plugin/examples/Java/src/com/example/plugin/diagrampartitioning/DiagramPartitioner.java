package com.example.plugin.diagrampartitioning;

import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.wiringdesigngenerator.IXWiringDesignGeneratorContext;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.wiringdesigngenerator.IXDiagramPartition;
import com.mentor.chs.plugin.wiringdesigngenerator.IXDiagramPartitioner;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DiagramPartitioner implements IXDiagramPartitioner
{

	private IPartitioningData m_partitioningData;
	private IXApplicationContext m_applicationContext;

	public DiagramPartitioner(Map<String, String> settings, IXApplicationContext applicationContext,
			IXWiringDesignGeneratorContext wiringDesignGeneratorContext)
	{
		m_applicationContext = applicationContext;
		m_partitioningData =
				OptionBasedPartitionPluginSettings.convertToPartitioningData(settings, wiringDesignGeneratorContext);
	}

	public Set<IXDiagramPartition> getPartitions(IXLogicDiagram functionalDiagram)
	{
		Map<IXLogicDesign, Map<IXLogicDiagram, List<IDiagramPartition>>> designToPartitions =
				m_partitioningData.getDiagramPartitions();

		Set<IXDiagramPartition> xDiagramPartitions = new LinkedHashSet<IXDiagramPartition>();
		IXLogicDesign functionalDesign = (IXLogicDesign) functionalDiagram.getDesign();
		Map<IXLogicDiagram, List<IDiagramPartition>> diagramToPartitions = designToPartitions.get(functionalDesign);
		if (diagramToPartitions != null) {
			List<IDiagramPartition> partitions = diagramToPartitions.get(functionalDiagram);
			if (partitions != null) {
				Map<IXLogicDesign, List<IColumn>> applicableColumns = m_partitioningData.getApplicableColumns();
				List<IColumn> designApplicableColums = applicableColumns.get(functionalDesign);
				for (IDiagramPartition partition : partitions) {
					xDiagramPartitions
							.add(new XDiagramPartition(partition.getName(), partition.getColumns(),
									designApplicableColums, m_applicationContext));
				}
			}
		}

		return xDiagramPartitions;
	}

	public String getDiagramNameForUndistributedObjects()
	{
		return m_partitioningData.getUndistributedContentDiagramName();
	}
}
