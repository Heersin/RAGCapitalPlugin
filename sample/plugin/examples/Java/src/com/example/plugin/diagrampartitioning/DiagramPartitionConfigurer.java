package com.example.plugin.diagrampartitioning;

import com.example.plugin.diagrampartitioning.ui.PartitionModel;
import com.example.plugin.diagrampartitioning.ui.PartitionPanel;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.wiringdesigngenerator.IXPartitionerConfigurationSettings;
import com.mentor.chs.api.wiringdesigngenerator.IXWiringDesignGeneratorContext;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.wiringdesigngenerator.IXDiagramPartitionConfigurer;

import javax.swing.JPanel;
import java.util.Map;

public class DiagramPartitionConfigurer implements IXDiagramPartitionConfigurer
{

	private IPartitioningData m_partitioningData;
	private PartitionModel m_partitionUIModel;
	private IXApplicationContext m_applicationContext;
	private Map<String, String> m_nonSelectedDesignSettings;
	private IXWiringDesignGeneratorContext m_wiringDesignGeneratorContext;

	public DiagramPartitionConfigurer(IXApplicationContext applicationContext,
			IXWiringDesignGeneratorContext wiringDesignGeneratorContext
	)
	{
		m_applicationContext = applicationContext;
		m_wiringDesignGeneratorContext = wiringDesignGeneratorContext;
	}

	public JPanel getPanel(Map<String, String> settings)
	{

		IApplicableColumnFinder columnFinder =
				getApplicableColumnFinder(m_wiringDesignGeneratorContext);
		IXProject project = m_applicationContext.getCurrentProject();
		m_partitioningData = new PartitioningData(m_wiringDesignGeneratorContext, columnFinder, project);
		m_nonSelectedDesignSettings =
				OptionBasedPartitionPluginSettings.updatePartitioningDataFromSettings(m_partitioningData, settings);
		PartitionPanel partitionPanel = new PartitionPanel("Configure Diagram Partitions");
		m_partitionUIModel = new PartitionModel(m_partitioningData);
		partitionPanel.setDiagramPartitionModel(m_partitionUIModel);
		return partitionPanel;
	}

	public void getSettings(IXPartitionerConfigurationSettings result)
	{
		if (m_partitioningData == null) {
			throw new IllegalStateException(
					"Partitioning data object is not initialized. This must be initialized in getPanel() which gets updated with the information");
		}

		m_partitionUIModel.updatePartitioningData(m_partitioningData);
		Map<String, String> settings = OptionBasedPartitionPluginSettings.convertToSettings(m_partitioningData);
		//adding non selected functional design settings so not to miss them
		settings.putAll(m_nonSelectedDesignSettings);
		result.setSettings(settings);
		result.setPersistSettings(true);
	}

	protected IApplicableColumnFinder getApplicableColumnFinder(
			IXWiringDesignGeneratorContext wiringDesignGeneratorContext)
	{
		return new OptionBasedColumnFinder(wiringDesignGeneratorContext, m_applicationContext);
	}
}
