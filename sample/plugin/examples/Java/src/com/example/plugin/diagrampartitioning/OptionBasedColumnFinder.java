package com.example.plugin.diagrampartitioning;

import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXBlock;
import com.mentor.chs.api.IXDevice;
import com.mentor.chs.api.IXGround;
import com.mentor.chs.api.IXIntegratorDesign;
import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.IXNet;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXOption;
import com.mentor.chs.api.IXOptionExpressionParser;
import com.mentor.chs.api.IXOptionFolder;
import com.mentor.chs.api.IXSignal;
import com.mentor.chs.api.IXVehicleConfiguration;
import com.mentor.chs.api.IXVehicleModel;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.wiringdesigngenerator.IXWiringDesignGeneratorContext;
import com.mentor.chs.plugin.IXApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OptionBasedColumnFinder implements IApplicableColumnFinder
{

	private IXWiringDesignGeneratorContext m_context;
	private IXApplicationContext m_applicationContext;
	private Map<IXAbstractConductor, Set<IXWire>> m_signalSetMap = null;

	public OptionBasedColumnFinder(IXWiringDesignGeneratorContext context, IXApplicationContext applicationContext)
	{
		m_context = context;
		m_applicationContext = applicationContext;
	}

	public List<IColumn> getApplicableColumns(IXLogicDesign functionalDesign, Map<String, IXOption> nameToOptionMap)
	{
		return getApplicableColumns(m_context, functionalDesign, nameToOptionMap);
	}

	private List<IColumn> getApplicableColumns(IXWiringDesignGeneratorContext context,
			IXLogicDesign functionalDesign,
			Map<String, IXOption> nameToOptionMap)
	{
		IXIntegratorDesign planeDesign = context.getPlaneDesign();

		Map<IXAbstractConductor, Set<IXWire>> signalSetMap = getSignalToWiresMap(planeDesign);

		Set<IXOption> configOptions = getConfigurationOptions(context);

		Set<IXOption> designOptions = new HashSet<IXOption>();

		//Get applicable options from the design
		designOptions.addAll(functionalDesign.getOptions());

		//Get the options set on the devices, nets, and shields & multicores: only these are the objects present on the functional diagram.
		designOptions.addAll(getDesignContentOptions(functionalDesign, signalSetMap, nameToOptionMap));

		if (!configOptions.isEmpty()) {
			designOptions.retainAll(configOptions);
		}

		List<IXOption> options = new ArrayList<IXOption>(designOptions);

		Collections.sort(options, new Comparator<IXOption>()
		{
			public int compare(IXOption o1, IXOption o2)
			{
				IXOptionFolder folder1 = o1.getFolder();
				IXOptionFolder folder2 = o2.getFolder();

				String folder1Name = folder1 == null ? "" : folder1.getAttribute(IXAttributes.Name);
				String folder2Name = folder2 == null ? "" : folder2.getAttribute(IXAttributes.Name);

				int folder1NameComparison = folder1Name.compareTo(folder2Name);
				if (folder1NameComparison != 0) {
					return folder1NameComparison;
				}

				String name1 = o1.getName();
				String name2 = o2.getName();

				return name1.compareTo(name2);
			}
		});

		List<IColumn> columns = new ArrayList<IColumn>(options.size());

		for (IXOption option : options) {
			String optionName = option.getName();
			IXOptionFolder folder = option.getFolder();
			String groupName = null;
			if (folder != null) {
				groupName = option.getFolder().getAttribute(IXAttributes.Name);
			}

			IColumn column = new Column(optionName, groupName);
			columns.add(column);
		}
		return columns;
	}

	private Map<IXAbstractConductor, Set<IXWire>> getSignalToWiresMap(IXIntegratorDesign planeDesign)
	{

		if (m_signalSetMap == null) {
			m_signalSetMap = new HashMap<IXAbstractConductor, Set<IXWire>>();
			Set<IXSignal> signals = planeDesign.getSignals();
			for (IXSignal signal : signals) {
				Set<IXWire> wires = signal.getWires();
				for (IXAbstractConductor funIxAbstractConductor : signal.getFunctionalConductors()) {
					m_signalSetMap.put(funIxAbstractConductor, wires);
				}
			}
		}
		return m_signalSetMap;
	}

	private Set<IXObject> getDesignContent(IXLogicDesign functionalDesign,
			Map<IXAbstractConductor, Set<IXWire>> signals)
	{
		Set<IXObject> objects = new HashSet<IXObject>();

		Set<? extends IXBlock> blocks = functionalDesign.getConnectivity().getBlocks();
		objects.addAll(blocks);

		Set<IXDevice> devices = functionalDesign.getConnectivity().getDevices();
		objects.addAll(devices);

		for (IXDevice xDevice : devices) {
			objects.addAll(xDevice.getPins());
		}

		Set<IXGround> grounds = functionalDesign.getConnectivity().getGrounds();
		objects.addAll(grounds);

		for (IXGround ground : grounds) {
			objects.addAll(ground.getPins());
		}

		objects.addAll(functionalDesign.getConnectivity().getNets());

		objects.addAll(functionalDesign.getConnectivity().getMulticores());

		objects.addAll(functionalDesign.getConnectivity().getShields());

		for (IXNet net : functionalDesign.getConnectivity().getNets()) {
			Set<IXWire> wires = signals.get(net);
			if (wires != null) {
				objects.addAll(wires);
			}
		}

		return objects;
	}

	private Set<IXOption> getConfigurationOptions(IXWiringDesignGeneratorContext context)
	{
		Set<IXOption> configOptions = new HashSet<IXOption>();
		for (IXVehicleConfiguration vehicleConfiguration : context.getConfigurations()) {
			IXVehicleModel vehicleModel = vehicleConfiguration.getVehicleModel();
			if (vehicleModel != null) {
				configOptions.addAll(vehicleModel.getSupportedVariants());
				configOptions.addAll(vehicleModel.getSupportedOptions());
				configOptions.addAll(vehicleModel.getIncludedOptions());
			}
		}
		return configOptions;
	}

	private Set<IXOption> getDesignContentOptions(IXLogicDesign functionalDesign,
			Map<IXAbstractConductor, Set<IXWire>> signals,
			Map<String, IXOption> nameToOptionMap)
	{
		Set<IXOption> designContentOptions = new LinkedHashSet<IXOption>();
		Set<IXObject> objects = getDesignContent(functionalDesign, signals);

		IXOptionExpressionParser parser = m_applicationContext.createOptionExpressionParser();

		for (IXObject object : objects) {
			for (String name : parser.getOptionsInExpression(object.getAttribute(IXAttributes.OptionExpression))) {
				IXOption option = nameToOptionMap.get(name);
				if (option != null) {
					designContentOptions.add(option);
				}
			}
		}

		return designContentOptions;
	}
}
