package com.example.plugin.bridge;

import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.action.IXHarnessAction;

import javax.swing.Icon;
import java.io.File;

public class ExportBridgeConfigurationsAction implements IXHarnessAction
{

	public String getName()
	{
		return "Export Bridge Registry Settings";
	}

	public String getDescription()
	{
		return "Exports Bridge Registry settings to a file";
	}

	public String getLongDescription()
	{
		return "Exports Bridge Registry settings to a file, which may be used as an input attachment of type " +
				"'BRIDGE_CONFIG_FILE' for submitting a BatchBridgeInTask";
	}

	public String getVersion()
	{
		return "1.0";
	}

	public Trigger[] getTriggers()
	{
		return new Trigger[]{Trigger.MainMenu, Trigger.ContextMenu};
	}

	public Icon getSmallIcon()
	{
		return null;
	}

	public Integer getMnemonicKey()
	{
		return null;
	}

	public boolean isReadOnly()
	{
		return true;
	}

	public boolean isAvailable(IXApplicationContext context)
	{
		return true;
	}

	public boolean execute(IXApplicationContext context)
	{
		boolean status = false;
		// use "chs_home" or any string which points to CAPITAL_HOME directory
		String chsHome = System.getenv("chs_home");
		if (chsHome != null) {
			String filePath = chsHome + File.separator + "temp" + File.separator + "BridgesConfigurations.xml";
			context.getOutputWindow()
					.println("Exporting all bridges registry settings to : " + filePath);
			status = BridgeSettingsExporter.getInstance().export(filePath);
			if (!status) {
				context.getOutputWindow().println("File :" + filePath + " could not be written");
			}
		}
		else {
			context.getOutputWindow().println("chs_home not configured");
		}
		return status;
	}
}
