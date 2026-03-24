package com.example.plugin.inspector;

import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.IXDesign;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

/**
 * Copyright 2011 Mentor Graphics Corporation. All Rights Reserved.
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
public class DiagramStateHandler
{
	private Set<String> earlierOpenDiagrams = new HashSet<String>();
//	private String projectID;
	private Set<IDiagramStateListener> listeners = new HashSet<IDiagramStateListener>();
	private IXApplicationContext context;

	public DiagramStateHandler(IXApplicationContext cont){
		context = cont;
	}
	public void addListener(IDiagramStateListener listener){
		listeners.add(listener);
	}
	public void removeListener(IDiagramStateListener listener){
		listeners.remove(listener);
	}
	void diagramOpened(Set<IXDiagram> diagrams){
		for(IDiagramStateListener list : listeners){
			list.diagramOpened(context,diagrams);
		}
	}
	void diagramClosed(Set<IXDiagram> diagrams){
		for(IDiagramStateListener list : listeners){
			list.diagramClosed(context,diagrams);
		}
	}

	public interface IDiagramStateListener{
		void diagramOpened(IXApplicationContext context, Set<IXDiagram> diagrams);
		void diagramClosed(IXApplicationContext context, Set<IXDiagram> diagrams);
	}
	private Set<IXDiagram> getDiagrams(IXApplicationContext context,Set<String> diagUIDs){
		Map<String,IXDiagram> diagMap = new HashMap<String,IXDiagram>();
		IXProject project = context.getCurrentProject();
		for(IXDesign des : project.getDesigns()){
			for(IXDiagram diag : des.getDiagrams()){
				diagMap.put(diag.getID(),diag);
			}
		}
		Set<IXDiagram> diags = new HashSet<IXDiagram>();
		for(String id : diagUIDs){
			IXDiagram xDiag = diagMap.get(id);
			if(xDiag != null){
				diags.add(xDiag);
			}
		}
		return diags;
	}
	public void notfyEvent(){
		IXProject xProj = context.getCurrentProject();
		String newprojectID = xProj == null? null :context.getCurrentProject().getID();
//		if(newprojectID == null || projectID == null || !newprojectID.equals(projectID)){
//			earlierOpenDiagrams.clear();
//		}
//		projectID = newprojectID;
		if(newprojectID == null){
			return;
		}
		Set<? extends IXDiagram> openDiags = context.getOpenDiagrams();
		Set<String> openDiagStrs = new HashSet<String>();
		for(IXDiagram xDiag : openDiags){
			openDiagStrs.add(xDiag.getID());
		}
		Set<String> earlOpenDiags = new HashSet<String>(earlierOpenDiagrams);
		earlOpenDiags.removeAll(openDiagStrs);
		if(!earlOpenDiags.isEmpty()){
			diagramClosed(getDiagrams(context,earlOpenDiags));
		}
		else{
			Set<String> earlOpenDiags1 = new HashSet<String>(openDiagStrs);
			earlOpenDiags1.removeAll(earlierOpenDiagrams);
			if(!earlOpenDiags1.isEmpty()){
				diagramOpened(getDiagrams(context,earlOpenDiags1));
			}
		}
		earlierOpenDiagrams.clear();
		earlierOpenDiagrams.addAll(openDiagStrs);
	}
}
