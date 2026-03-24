package com.example.plugin.diagrampartitioning;

import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.wiringdesigngenerator.IXWiringDesignGeneratorContext;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OptionBasedPartitionPluginSettings
{

	private static final String VERSION_STRING = "Version";
	private static final String VERSION_1_0 = "1.0";

	private static final String DIAGRAM_NAME_FOR_UNDISTRIBUTED_CONTENT_STRING = "DiagramNameForUndistributedContent";

	private static final String NAME_ATTRIBUTE = "Name";
	private static final String REVISION_ATTRIBUTE = "Revision";

	private static final String APPLICABLE_COLUMNS = "ApplicableColumns";

	private static final char SEPARATOR1 = '|';
	private static final char SEPARATOR2 = ':';
	private static final char ESCAPE_CHAR = '\\';

	private OptionBasedPartitionPluginSettings()
	{
	}

	/**
	 * Updates the partitioning data from the specified settings.
	 *
	 * @param partitioningData partitioning data to be updated
	 * @param settings settings
	 *
	 * @return settings of the non-selected functional designs
	 */
	public static Map<String, String> updatePartitioningDataFromSettings(IPartitioningData partitioningData,
			Map<String, String> settings)
	{
		OptionBasedPartitionPluginSettings pluginSettings = new OptionBasedPartitionPluginSettings();
		Map<String, String> nonSelectedDesignSettings = new LinkedHashMap<String, String>();
		pluginSettings.doConvertToPartitioningData(new LinkedHashMap<String, String>(settings), partitioningData,
				nonSelectedDesignSettings);
		return nonSelectedDesignSettings;
	}

	public static IPartitioningData convertToPartitioningData(Map<String, String> settings,
			IXWiringDesignGeneratorContext wiringDesignGeneratorContext)
	{
		OptionBasedPartitionPluginSettings pluginSettings = new OptionBasedPartitionPluginSettings();
		IPartitioningData partitioningData = new PartitioningDataFromSettings(wiringDesignGeneratorContext);
		pluginSettings.doConvertToPartitioningData(new LinkedHashMap<String, String>(settings), partitioningData, null);
		return partitioningData;
	}

	private boolean doConvertToPartitioningData(LinkedHashMap<String, String> settings,
			IPartitioningData partitioningData, Map<String, String> nonSelectedDesignSettings)
	{
		String version = pull(settings, VERSION_STRING);
		if (!VERSION_1_0.equals(version)) {
			return false;
		}

		String diagramNameForUndistributedContent = pull(settings, DIAGRAM_NAME_FOR_UNDISTRIBUTED_CONTENT_STRING);
		partitioningData.setDiagramNameForUndistributedContent(diagramNameForUndistributedContent);

		Set<String> keys = new LinkedHashSet<String>(settings.keySet());
		for (String key : keys) {

			String value = pull(settings, key);

			List<String> tokens = tokenize(SEPARATOR1, key);
			String lastToken = tokens.get(tokens.size() - 1);

			if (APPLICABLE_COLUMNS.equals(lastToken)) {
				String designName = tokens.get(0);
				String revision = tokens.get(1);
				IXLogicDesign design = getDesign(designName, revision, partitioningData);
				if (design != null) {
					List<String> columnStrings = tokenize(SEPARATOR1, value);
					for (String columnString : columnStrings) {
						List<String> columnAttributes = tokenize(SEPARATOR2, columnString);
						Iterator<String> columnAttrIter = columnAttributes.iterator();
						String columnName = columnAttrIter.hasNext() ? columnAttrIter.next() : null;
						if (columnName != null) {
							String groupName = columnAttrIter.hasNext() ? columnAttrIter.next() : null;
							partitioningData.addApplicableColumn(design, columnName, groupName);
						}
					}
				}
				else {
					if (nonSelectedDesignSettings != null) {
						nonSelectedDesignSettings.put(key, value);
					}
				}
			}
			else {
				// designName, designRevision, digramName, partitionName
				String designName = tokens.get(0);
				String revision = tokens.get(1);
				IXLogicDesign design = getDesign(designName, revision, partitioningData);
				if (design != null) {
					String diagramName = tokens.get(2);
					IXLogicDiagram diagram = getDiagram(design, diagramName);
					if (diagram != null) {
						String partitionName = tokens.get(3);
						IDiagramPartition partition = partitioningData.addDiagramPartition(diagram, partitionName);
						List<String> columnStrings = tokenize(SEPARATOR1, value);
						for (String columnString : columnStrings) {
							List<String> columnAttributes = tokenize(SEPARATOR2, columnString);
							Iterator<String> columnAttrIter = columnAttributes.iterator();
							String columnName = columnAttrIter.hasNext() ? columnAttrIter.next() : null;
							if (columnName != null) {
								String groupName = columnAttrIter.hasNext() ? columnAttrIter.next() : null;
								partition.addColumn(columnName, groupName);
							}
						}
					}
				}
				else {
					if (nonSelectedDesignSettings != null) {
						nonSelectedDesignSettings.put(key, value);
					}
				}
			}
		}

		return true;
	}

	/**
	 * Get the diagram of the specified name in the specified design.
	 *
	 * @param design design in which to find the diagram with the name diagramName
	 * @param diagramName name of the diagram
	 *
	 * @return diagram with the specified name found, otherwise <b>null</b>.
	 */
	private IXLogicDiagram getDiagram(IXLogicDesign design, String diagramName)
	{
		for (IXDiagram obj : design.getDiagrams()) {
			if (obj.getAttribute(IXAttributes.Name).equals(diagramName)) {
				return (IXLogicDiagram) obj;
			}
		}
		return null;
	}

	private IXLogicDesign getDesign(String designName, String revision, IPartitioningData partitioningData)
	{
		return partitioningData.getFunctionalDesign(designName, revision);
	}

	private List<String> tokenize(char separator, String key)
	{
		if (key == null) {
			return Collections.emptyList();
		}

		final List<String> tokens = new ArrayList<String>();
		boolean esc = false;
		final StringBuilder sb = new StringBuilder(1024);
		final CharacterIterator it = new StringCharacterIterator(key);
		for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			if (esc) {
				sb.append(c);
				esc = false;
			}
			else if (c == ESCAPE_CHAR) {
				esc = true;
			}
			else if (c == separator) {
				tokens.add(sb.toString());
				sb.delete(0, sb.length());
			}
			else {
				sb.append(c);
			}
		}
		if (sb.length() > 0) {
			tokens.add(sb.toString());
		}
		return tokens;
	}

	private String pull(LinkedHashMap<String, String> settings, String key)
	{
		String value = settings.get(key);
		settings.remove(key);
		return value;
	}

	public static Map<String, String> convertToSettings(IPartitioningData partitioningData)
	{
		LinkedHashMap<String, String> settings = new LinkedHashMap<String, String>();
		OptionBasedPartitionPluginSettings pluginSettings = new OptionBasedPartitionPluginSettings();
		pluginSettings.doConvertToSettings(partitioningData, settings);
		return settings;
	}

	private void doConvertToSettings(IPartitioningData partitioningData, Map<String, String> settings)
	{
		writeVersion(settings, VERSION_1_0);

		String diagramNameForUndistributedContent = partitioningData.getUndistributedContentDiagramName();
		writeDiagramNameForUndistributedContent(settings, diagramNameForUndistributedContent);

		Map<IXLogicDesign, List<IColumn>> applicableColumns = partitioningData.getApplicableColumns();
		Map<IXLogicDesign, Map<IXLogicDiagram, List<IDiagramPartition>>> designToPartitions =
				partitioningData.getDiagramPartitions();

		List<IXLogicDesign> allFunctionalDesigns = new ArrayList<IXLogicDesign>();
		for (IXLogicDesign functionalDesign : applicableColumns.keySet()) {
			if (!allFunctionalDesigns.contains(functionalDesign)) {
				allFunctionalDesigns.add(functionalDesign);
			}
		}
		for (IXLogicDesign functionalDesign : designToPartitions.keySet()) {
			if (!allFunctionalDesigns.contains(functionalDesign)) {
				allFunctionalDesigns.add(functionalDesign);
			}
		}
		allFunctionalDesigns = Collections.unmodifiableList(allFunctionalDesigns);

		for (IXLogicDesign functionalDesign : allFunctionalDesigns) {

			// write applicable columns
			writeApplicableColumns(settings, functionalDesign, applicableColumns.get(functionalDesign));

			// write diagram partitions
			writeDiagramPartitions(settings, functionalDesign, designToPartitions.get(functionalDesign));
		}
	}

	private void writeVersion(Map<String, String> settings, String version)
	{
		String key = getString(VERSION_STRING);
		String value = getString(version);

		addToSettings(settings, key, value);
	}

	private void writeDiagramNameForUndistributedContent(Map<String, String> settings,
			String diagramNameForUndistributedContent)
	{
		String key = getString(DIAGRAM_NAME_FOR_UNDISTRIBUTED_CONTENT_STRING);
		String value = getString(diagramNameForUndistributedContent);

		addToSettings(settings, key, value);
	}

	private void writeApplicableColumns(Map<String, String> settings, IXLogicDesign functionalDesign,
			List<IColumn> applicableColumns)
	{
		String designName = functionalDesign.getAttribute(NAME_ATTRIBUTE);
		String designRevision = functionalDesign.getAttribute(REVISION_ATTRIBUTE);

		String key = getString(SEPARATOR1, designName, designRevision, APPLICABLE_COLUMNS);
		String value = getColumnsAsString(applicableColumns);

		addToSettings(settings, key, value);
	}

	private void writeDiagramPartitions(Map<String, String> settings,
			IXLogicDesign functionalDesign, Map<IXLogicDiagram, List<IDiagramPartition>> diagramToPartitions)
	{
		if (diagramToPartitions == null) {
			return;
		}

		String designName = functionalDesign.getAttribute(NAME_ATTRIBUTE);
		String designRevision = functionalDesign.getAttribute(REVISION_ATTRIBUTE);

		for (IXLogicDiagram functionalDiagram : diagramToPartitions.keySet()) {

			String digramName = functionalDiagram.getAttribute(NAME_ATTRIBUTE);

			List<IDiagramPartition> partitions = diagramToPartitions.get(functionalDiagram);

			if (partitions != null) {
				for (IDiagramPartition partition : partitions) {
					String partitionName = partition.getName();

					String key = getString(SEPARATOR1, designName, designRevision, digramName, partitionName);
					String value = getColumnsAsString(partition.getColumns());

					addToSettings(settings, key, value);
				}
			}
		}
	}

	private String getColumnsAsString(List<IColumn> columns)
	{

		if (columns == null) {
			return getString();
		}

		String[] columnStrings = new String[columns.size()];

		for (int i = 0; i < columns.size(); i++) {
			IColumn column = columns.get(i);
			columnStrings[i] = getString(SEPARATOR2, column.getName(), column.getGroupName());
		}

		return getString(SEPARATOR1, columnStrings);
	}

	private String getString()
	{
		return getString(null);
	}

	private String getString(String item)
	{
		return getNotNullString(item);
	}

	private String getString(char separator, String... items)
	{
		StringBuilder stringBuilder = new StringBuilder();
		boolean addSeparator = false;
		for (String item : items) {

			if (addSeparator) {
				stringBuilder.append(separator);
			}
			addSeparator = true;

			stringBuilder.append(getNotNullString(item, separator));
		}

		return stringBuilder.toString();
	}

	private boolean addToSettings(Map<String, String> settings, String key, String value)
	{
		if (!isKeyValid(settings, key)) {
			return false;
		}

		settings.put(key, getNotNullString(value));

		return true;
	}

	private boolean isKeyValid(Map<String, String> settings, String key)
	{
		return key != null && !key.trim().isEmpty() && !settings.containsKey(key);
	}

	private String getNotNullString(String value)
	{
		return value != null ? value : "";
	}

	private String getNotNullString(String value, char separator)
	{

		if (value == null) {
			return "";
		}

		final StringBuilder sb = new StringBuilder(1024);
		final CharacterIterator it = new StringCharacterIterator(value);
		for (char c = it.first(); c != CharacterIterator.DONE; c = it.next()) {
			if (c == separator || c == ESCAPE_CHAR) {
				sb.append(ESCAPE_CHAR);
			}
			sb.append(c);
		}

		return sb.toString();
	}
}
