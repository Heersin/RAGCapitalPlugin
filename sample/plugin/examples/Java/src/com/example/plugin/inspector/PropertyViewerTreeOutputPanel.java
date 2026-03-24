/**
 * Copyright 2007 Mentor Graphics Corporation. All Rights Reserved.
 * <p>
 * Recipients who obtain this code directly from Mentor Graphics use it solely
 * for internal purposes to serve as example plugin.
 * This code may not be used in a commercial distribution. Recipients may
 * duplicate the code provided that all notices are fully reproduced with
 * and remain in the code. No part of this code may be modified, reproduced,
 * translated, used, distributed, disclosed or provided to third parties
 * without the prior written consent of Mentor Graphics, except as expressly
 * authorized above.
 * <p>
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY
 * DISCLAIMS ANY WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * OR WARRANTY OF NON-INFRINGEMENT. IN NO EVENT SHALL MENTOR GRAPHICS OR ITS
 * LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT
 * OR ANY OTHER LEGAL THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * <p>
 */

package com.example.plugin.inspector;

import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.event.IXDesignChangeEvent;
import com.mentor.chs.api.event.IXDesignModificationEvent;
import com.mentor.chs.api.event.IXProjectChangeEvent;
import com.mentor.chs.api.event.IXSelectionChangeEvent;
import com.mentor.chs.api.event.IXWindowChangeEvent;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.event.IXDesignChangeListener;
import com.mentor.chs.plugin.event.IXDesignModificationListener;
import com.mentor.chs.plugin.event.IXProjectChangeListener;
import com.mentor.chs.plugin.event.IXWindowChangeListener;
import com.mentor.chs.plugin.action.IXAction;
import com.mentor.chs.plugin.action.IXHarnessAction;
import com.mentor.chs.plugin.action.IXIntegratorAction;
import com.mentor.chs.plugin.action.IXLogicAction;
import com.mentor.chs.plugin.action.IXTopologyAction;
import com.mentor.chs.plugin.output.IXOutputTabPanel;

import javax.swing.Icon;
import java.util.Set;

public class PropertyViewerTreeOutputPanel extends PropertyViewerTreePanel implements IXOutputTabPanel, IXLogicAction,
		IXHarnessAction, IXTopologyAction, IXIntegratorAction, IXDesignChangeListener, IXDesignModificationListener,
		IXProjectChangeListener, IXWindowChangeListener,DiagramStateHandler.IDiagramStateListener
{
	private DiagramStateHandler stateHandler;
	private boolean recording = false;

	public Icon getSmallIcon()
	{
		return null;
	}

	/**
	 * @return returns null so example custom actions will not have a Mnemonic Key by default unless this method is
	 *         overriden.
	 */
	public Integer getMnemonicKey()
	{
		return null;
	}

	/**
	 * @return returns MainMenu & ContextMenu so example custom actions will appear on the main menu and the custom menu
	 *         (unless this method is overriden).
	 */
	public Trigger[] getTriggers()
	{
		return new Trigger[]{IXAction.Trigger.MainMenu, IXAction.Trigger.ContextMenu};
	}

	/**
	 * @param context - the IXApplicationContext
	 *
	 * @return true, when objects are selected so that example custom actions will only be available when objects are
	 *         selected (unless this method is overriden).
	 */
	public boolean isAvailable(IXApplicationContext context)
	{
		return true;
	}

	/**
	 * @return the description that appears when the example custom action is activated will be the same as the description
	 *         of the plugin.
	 */
	public String getLongDescription()
	{
		return getDescription();
	}

	public boolean isReadOnly()
	{
		return true;
	}

	public boolean execute(IXApplicationContext applicationContext)
	{
		initializeHandler(applicationContext);
		return true;
	}
	private void initializeHandler(IXApplicationContext applicationContext){
		if(!recording){
			stateHandler = new DiagramStateHandler(applicationContext);
			stateHandler.addListener(this);
			stateHandler.notfyEvent();
			applicationContext.getOutputWindow().getTabPanel("Property Viewer Tree", this);
			applicationContext.getOutputWindow().println("Event Recorder", "Event Recorder to record events");
			recording = true;
		}

	}
	public PropertyViewerTreeOutputPanel()
	{
		super("Create Output Panels",						// Plugin Name
				"1.1",													// Plugin Version
				"Displays the properties of selected objects in a tree in output panal and  "); // Plugin Description
	}

	public void clear()
	{
		root.removeAllChildren();
		tree.updateUI();
	}

	private String getNameLink(IXObject obj)
	{
		String html = obj.toHTML();
		if (html == null || html.isEmpty()) {
			html = obj.toString();
		}
		return html;
	}

	private String getEventMsg(Object xEvent)
	{
		StringBuilder strBuild = new StringBuilder("Event Type - ");
		if (xEvent instanceof IXSelectionChangeEvent) {
			strBuild.append("Selection Change,");
			if (((IXSelectionChangeEvent) xEvent).isSelect()) {
				strBuild.append("Selected objects are ");
			}
			else {
				strBuild.append("Deselected objects are ");
			}
			for (IXObject obj : ((IXSelectionChangeEvent) xEvent).getChangeSet()) {
				strBuild.append(getNameLink(obj)).append(",");
			}
		}
		else if (xEvent instanceof IXProjectChangeEvent) {
			strBuild.append("Project Change");
		}
		else if (xEvent instanceof IXDesignChangeEvent) {
			strBuild.append("Design Change");
		}
		else if (xEvent instanceof IXDesignModificationEvent) {
			strBuild.append("Design Modify, ");
			boolean isFirst = true;
			for (IXObject obj : ((IXDesignModificationEvent) xEvent).getModifiedObjects()) {
				if (isFirst) {
					strBuild.append("Modified Objects - ");
					isFirst = false;
				}
				strBuild.append(getNameLink(obj));
				strBuild.append(",");
			}
			isFirst = true;
			for (IXObject obj : ((IXDesignModificationEvent) xEvent).getNewObjects()) {
				if (isFirst) {
					strBuild.append("New Objects - ");
					isFirst = false;
				}
				strBuild.append(getNameLink(obj));
				strBuild.append(",");
			}
			isFirst = true;
			for (String obj : ((IXDesignModificationEvent) xEvent).getDeletedObjects()) {
				if (isFirst) {
					strBuild.append("Deleted Object UIDs - ");
					isFirst = false;
				}
				strBuild.append(obj);
				strBuild.append(",");
			}
		}
		else if (xEvent instanceof IXWindowChangeEvent) {
			strBuild.append("Window Change");
		}

		return strBuild.toString();
	}

	public void selectionChanged(IXApplicationContext context, IXSelectionChangeEvent xEvent)
	{
//		if(context.getOutputWindow().getTabPanel("Event Recorder",null) != null){
		if (recording) {
			context.getOutputWindow().println("Event Recorder", getEventMsg(xEvent));
		}
		update(context);
	}

	public void designChanged(IXApplicationContext context, IXDesignChangeEvent xEvent)
	{
		if (recording) {
			context.getOutputWindow().println("Event Recorder", getEventMsg(xEvent));
			stateHandler.notfyEvent();
		}
	}

	public void designModified(IXApplicationContext context, IXDesignModificationEvent xEvent)
	{
		if (recording) {
			context.getOutputWindow().println("Event Recorder", getEventMsg(xEvent));
		}
	}

	public void projectChanged(IXApplicationContext context, IXProjectChangeEvent xEvent)
	{
		if (recording) {
			context.getOutputWindow().println("Event Recorder", getEventMsg(xEvent));
			stateHandler.notfyEvent();
		}
	}
	private String getDiagramDisplayName(IXDiagram diag){
		StringBuffer strBuff = new StringBuffer();
		IXDesign design = diag.getDesign();
		strBuff.append(design.getAttribute(IXAttributes.Name));
		strBuff.append(":");
		strBuff.append(design.getAttribute(IXAttributes.Revision));
		strBuff.append(":");
		strBuff.append(diag.getAttribute(IXAttributes.Name));
		return strBuff.toString();
	}
	public void diagramOpened(IXApplicationContext context, Set<IXDiagram> diagrams){
		for(IXDiagram diag : diagrams){
			context.getOutputWindow().println("Event Recorder", "<html><b><FONT COLOR=BLUE>Diagram Opened - " + getDiagramDisplayName(diag) + "</FONT></b></html>");
		}
	}
	public void diagramClosed(IXApplicationContext context, Set<IXDiagram> diagrams){
		for(IXDiagram diag : diagrams){
			context.getOutputWindow().println("Event Recorder", "<html><b><FONT COLOR=GREEN>Diagram Closed - " + getDiagramDisplayName(diag) + "</FONT></b></html>");
		}
	}

	public void windowChanged(IXApplicationContext context, IXWindowChangeEvent xEvent)
	{
		if (recording) {
			context.getOutputWindow().println("Event Recorder", getEventMsg(xEvent));
			stateHandler.notfyEvent();
		}
	}
}