package com.example.plugin.diagrampartitioning;

import com.mentor.chs.api.wiringdesigngenerator.IXWiringDesignGeneratorContext;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.wiringdesigngenerator.IXDiagramPartitionConfigurer;
import com.mentor.chs.plugin.wiringdesigngenerator.IXDiagramPartitioner;
import com.mentor.chs.plugin.wiringdesigngenerator.IXDiagramPartitionerPlugin;

import java.util.Map;

public class OptionBasedPartitionPlugin implements IXDiagramPartitionerPlugin, IXApplicationContextListener

{

	private IXApplicationContext m_applicationContext;

	public String getDescription()
	{
		return "Option based partition plugin";
	}

	public String getName()
	{
		return "Option Based Partition Plugin";
	}

	public String getVersion()
	{
		return "1.0";
	}

	public void setApplicationContext(IXApplicationContext applicationContext)
	{
		m_applicationContext = applicationContext;
	}

	public IXDiagramPartitioner getDiagramPartitioner(Map<String, String> settings,
			IXWiringDesignGeneratorContext wiringDesignGeneratorContext)
	{
		return new DiagramPartitioner(settings, m_applicationContext, wiringDesignGeneratorContext);
	}

	public IXDiagramPartitionConfigurer getPartitionConfigurer(
			IXWiringDesignGeneratorContext wiringDesignGeneratorContext)
	{
		return new DiagramPartitionConfigurer(m_applicationContext, wiringDesignGeneratorContext);
	}
}
