/**
 * Copyright 2010 Mentor Graphics Corporation. All Rights Reserved.
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
package com.example.plugin.action;

import com.mentor.chs.plugin.action.IXApplicationAction;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.IXBuildList;
import com.example.plugin.utility.DesignModificationImpl;

import javax.swing.Icon;
import java.util.Set;
import java.util.HashSet;

/**
 * This custom action retrieve the design contens for all the designs  in the current project and modify connectivity objects and diagram objects.
 * <p>
 */
public class AllDesignsContentModifyAction extends BaseAction implements IXApplicationAction
{

	public AllDesignsContentModifyAction() {
		super(
				"Modify Objects in the designs",
				"1.0",
				"This Action will modify the design contents in selected context");
	}
	public Icon getSmallIcon()
	{
		return null;
	}
	public boolean isAvailable(IXApplicationContext context)
	{
		return (!getSelectedDesigns(context).isEmpty()) ;
	}

	private Set<IXDesign> getSelectedDesigns(IXApplicationContext applicationContext){
		Set<IXDesign> designs = new HashSet<IXDesign>();
		designs.addAll(applicationContext.getSelectedObjects(IXDesign.class));
		for(IXProject proj : applicationContext.getSelectedObjects(IXProject.class)){
			designs.addAll(proj.getDesigns());
		}
		for(IXBuildList bld : applicationContext.getSelectedObjects(IXBuildList.class)){
			designs.addAll(bld.getDesigns());
		}
		if(designs.isEmpty()){
			IXProject proj = applicationContext.getCurrentProject();
			if(proj != null){
			designs.addAll(proj.getDesigns());
			}
		}
		return designs;
	}
	public boolean execute(IXApplicationContext applicationContext)
	{
		Set<IXDesign> selectedDesigns = getSelectedDesigns(applicationContext);
		DesignModificationImpl impl = new DesignModificationImpl(selectedDesigns);
		impl.modifyDesignObjects();
		return true;
	}
}
