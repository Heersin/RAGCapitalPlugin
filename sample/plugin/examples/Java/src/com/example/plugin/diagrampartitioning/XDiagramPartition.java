package com.example.plugin.diagrampartitioning;

import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXConnectivityObject;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.IXOption;
import com.mentor.chs.api.IXOptionExpressionValidator;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXPropertySetter;
import com.mentor.chs.plugin.wiringdesigngenerator.IXDiagramPartition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class XDiagramPartition implements IXDiagramPartition
{

	private String m_diagramPartitionName;
	private List<IColumn> m_applicableColumns = Collections.emptyList();
	private List<IColumn> m_selectedColumns = Collections.emptyList();
	private IXApplicationContext m_applicationContext;

	public XDiagramPartition(String diagramPartitionName, List<IColumn> selectedColumns,
			List<IColumn> applicableColumns, IXApplicationContext applicationContext)
	{
		m_diagramPartitionName = diagramPartitionName;
		if (selectedColumns != null) {
			m_selectedColumns = selectedColumns;
		}
		if (applicableColumns != null) {
			m_applicableColumns = applicableColumns;
		}
		m_applicationContext = applicationContext;
	}

	public String getName()
	{
		return m_diagramPartitionName;
	}

	public boolean isApplicable(IXConnectivityObject connectivityObject)
	{
		IXOptionExpressionValidator optionExpressionValidator = m_applicationContext.createOptionExpressionValidator();
		if (optionExpressionValidator != null) {
			List<String> selectedColumnNames = getSelectedColumnNames();
			Set<String> allIncludedColumnNames = getIncludedOptions(selectedColumnNames);

			String optionExpression = connectivityObject.getAttribute(IXAttributes.OptionExpression);

			return optionExpressionValidator.validateExpression(optionExpression, allIncludedColumnNames);
		}
		else {
			throw new IllegalStateException(
					"Application context was not initialized. So, option expression validation cannot be performed.");
		}
	}

	private List<String> getSelectedColumnNames()
	{
		List<String> selectedColumnNames = new ArrayList<String>();
		for (IColumn column : m_selectedColumns) {
			selectedColumnNames.add(column.getName());
		}
		return selectedColumnNames;
	}

	private List<String> getApplicableColumnNames()
	{
		List<String> columnNames = new ArrayList<String>();
		for (IColumn column : m_applicableColumns) {
			columnNames.add(column.getName());
		}
		return columnNames;
	}

	public void getPropertiesForPartition(IXPropertySetter propertySetter)
	{
		propertySetter.addProperty("Selected Criteria", makeCommaSeparatedString(getSelectedColumnNames()));
		propertySetter.addProperty("Applicable Criteria", makeCommaSeparatedString(getApplicableColumnNames()));
	}

	private String makeCommaSeparatedString(List<String> values)
	{
		StringBuilder builder = new StringBuilder();
		Iterator<String> valueIter = values.iterator();
		while (valueIter.hasNext()) {
			builder.append(valueIter.next());
			if (valueIter.hasNext()) {
				builder.append(',');
			}
		}
		return builder.toString();
	}

	public IXLogicDiagram getPrototypeDiagramForPartition(IXLogicDiagram systemSelectedPrototype,
			Collection<IXLogicDiagram> candidatePrototypes)
	{
		return systemSelectedPrototype;
	}

	private Set<String> getIncludedOptions(Collection<String> applicableOptions)
	{
		IXProject project = m_applicationContext.getCurrentProject();
		if (project == null) {
			return new HashSet<String>();
		}

		Map<String, IXOption> nameToOptionMap = new HashMap<String, IXOption>();
		for (IXOption anOption : project.getOptions()) {
			nameToOptionMap.put(anOption.getName(), anOption);
		}

		Queue<String> tQueue = new LinkedList<String>(applicableOptions);
		Set<String> visited = new LinkedHashSet<String>();

		while (!tQueue.isEmpty()) {
			String next = tQueue.poll();
			if (!visited.add(next)) {
				continue;
			}
			IXOption nextOption = nameToOptionMap.get(next);
			if (nextOption == null) {
				continue;
			}
			for (IXOption xOption : nextOption.getInclusiveOptions()) {
				if (!visited.contains(xOption.getName())) {
					tQueue.offer(xOption.getName());
				}
			}
		}

		return visited;
	}
}
