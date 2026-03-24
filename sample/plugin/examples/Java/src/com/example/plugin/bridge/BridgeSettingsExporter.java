package com.example.plugin.bridge;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class BridgeSettingsExporter
{
	public static final String CONFIGURATION_ROOT_NODE = "BridgesConfiguration";
	public static final List<String> m_KeyNodeClassPaths = Arrays.asList(
			"/chs/bridges/BridgesOptionSetter", "/chs/bridges/adaptors/CHSAdaptorFactory",
			"/chs/bridges/topo/AutoLinkCriteriaSetter",
			"/chs/bridges/harness/CHSImportHarnessOptions", "/chs/bridges/harness/CHSOrthogonalFlattener",
			"/chs/bridges/harness/CHS3DFlattener","/chs/bridges/harness/CHSUnfoldingFlattener",
			"/chs/bridges/harness/CHSExternalFlattener", "/chs/bridges/adaptors/kbl/KBLFormat",
			"/chs/bridges/adaptors/dsi/DSIFormat"
	);
	public static final List<String> m_ExcludedKeyNames = Arrays.asList(
			"ExportPostProcessor", "RepositoryFolderPath",
			"curHandshake","RetainLastWorkingHarnessStatus"
	);

	public static final List<String> m_IncludeKBLKeyNames = Arrays.asList(
			"KBLFormat.kbl.stretch.factor", "KBLFormat.module.code.type"
	);

	public static final List<String> m_IncludeDSIKeyNames = Arrays.asList(
			"ModuleCodeType", "FieldDelimiter",
			"CommentMarker","SectionSeparator", "Unit"
	);


	public static BridgeSettingsExporter getInstance()
	{
		return new BridgeSettingsExporter();
	}

	public static String getValidFilteredValue(String key, Preferences pref)
	{
		if (!m_ExcludedKeyNames.contains(key)) {
			String value = pref.get(key, "");
			if (value != null && !value.isEmpty()) {			
				return value;
			}
		}
		return null;
	}

	public static String getSimpleName(String classpath)
	{
		String fullPath = classpath;
		int dotInd = fullPath.lastIndexOf('/');
		return dotInd < 0 ? fullPath : fullPath.substring(dotInd + 1);
	}

	public static String getPackagePath(String classpath)
	{
		String fullPath = classpath;
		int dotInd = fullPath.lastIndexOf('/');
		return dotInd < 0 ? fullPath : fullPath.substring(0, dotInd);
	}

	private List<String> getKeySet(String classPath, Preferences pref)
	{
		List<String> keys = new ArrayList<String>();
		try {
			keys = Arrays.asList(pref.keys());
			if(classPath.equals(m_KeyNodeClassPaths.get(8)) && !m_IncludeKBLKeyNames.isEmpty())
			{
				keys = m_IncludeKBLKeyNames;
			}
			else if(classPath.equals(m_KeyNodeClassPaths.get(9)) && !m_IncludeDSIKeyNames.isEmpty())
			{
				keys = m_IncludeDSIKeyNames;
			}
		}
		catch (BackingStoreException e) {
			e.printStackTrace();
		}

		return keys;
	}

	public boolean export( OutputStreamWriter outWritter) throws XMLStreamException
	{
		XMLStreamWriter out = XMLOutputFactory.newInstance().createXMLStreamWriter(outWritter);
		out.writeStartDocument("UTF-8", "1.0");
		out.writeCharacters("\n");
		out.writeStartElement(CONFIGURATION_ROOT_NODE);

		for(String src: m_KeyNodeClassPaths)
		{
			Preferences pref = getPreferences(src);
			if (pref != null) {
				String className = getSimpleName(src);

				out.writeCharacters("\n");
				out.writeCharacters("\t");
				out.writeStartElement(className);
				out.writeCharacters("\n");

				List<String> keys = getKeySet(src, pref);
				for (String key : keys) {
					String value = getValidFilteredValue(key, pref);
					if (value != null) {
						out.writeCharacters("\t \t");
						out.writeStartElement("entry");
						out.writeAttribute(key, value);
						out.writeEndElement();
						out.writeCharacters("\n");
					}
				}
				out.writeCharacters("\t");
				out.writeEndElement();
			}
		}
		out.writeCharacters("\n");
		out.writeEndElement();
		out.writeEndDocument();
		out.flush();
		out.close();
		return true;
	}

	private Preferences getPreferences(String sClass)
	{
		StringBuilder srcClass = new StringBuilder();
		if(sClass.equals(m_KeyNodeClassPaths.get(8)))
		{
			srcClass.append(getPackagePath(sClass));
		}
		else if(sClass.equals(m_KeyNodeClassPaths.get(9)))
		{
			srcClass.append(getPackagePath(sClass));
			srcClass.append("/harness");
		}
		else{
			srcClass.append(sClass);
		}
		Preferences prefs = Preferences.userRoot().node(srcClass.toString());
		if(srcClass.toString().equals(m_KeyNodeClassPaths.get(0)) || srcClass.toString().equals(m_KeyNodeClassPaths.get(2))
			|| srcClass.toString().equals(m_KeyNodeClassPaths.get(3)))
		{
			return prefs.node("harnessdesign");
		}
		return prefs;
	}

	public boolean export(String targetFileName)
	{
		boolean exportStatus = false;
		if(!targetFileName.isEmpty()){
			OutputStreamWriter outWritter = null;
			try {
				//noinspection IOResourceOpenedButNotSafelyClosed
				outWritter = new OutputStreamWriter (new FileOutputStream(new File(targetFileName)), "UTF-8");
				exportStatus = export(outWritter);
			}
			catch (UnsupportedEncodingException ignore) {
			}
			catch (FileNotFoundException ignore) {
			}
			catch (XMLStreamException ignore) {

			}
			finally {
				if(outWritter != null)
				{
					try {
						outWritter.close();
					}
					catch (IOException ignore) {
					}
				}
			}
		}
		return exportStatus;
	}


	private BridgeSettingsExporter()
	{
	}
}
