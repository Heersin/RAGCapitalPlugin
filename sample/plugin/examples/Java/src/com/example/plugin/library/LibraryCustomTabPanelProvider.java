/**
 * Copyright 2009 Mentor Graphics Corporation. All Rights Reserved.
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

package com.example.plugin.library;
import com.mentor.chs.plugin.library.IXLibraryTabPanel;
import com.mentor.chs.plugin.library.IXLibraryTabPanelProvider;
import com.mentor.chs.plugin.library.IXLibraryTabPage;

import java.util.Set;
import java.util.HashSet;
import java.util.Collections;

/**
 * This will be called by Library to find out the suitable tab name and tab page
 * there could be multiple implementation of this interface and all of them will be called
 * to get the external tab pages and panels
 */

public class LibraryCustomTabPanelProvider implements IXLibraryTabPanelProvider
{

	public Set<IXLibraryTabPanel> getTabPanels(LibraryGroupName groupName, LibraryTabName tabName)
	{
		if (groupName ==  LibraryGroupName.WIRE && tabName == LibraryTabName.LibraryExtraAttributesView) {
			Set<IXLibraryTabPanel> tabPanels = new HashSet<IXLibraryTabPanel>();
			IXLibraryTabPanel tabPanel = new LibraryCustomTabPanel();
			tabPanels.add(tabPanel);
			return tabPanels;
		}
		else if (groupName ==  LibraryGroupName.DEVICE && tabName == LibraryTabName.LibraryExtraAttributesView) {
			Set<IXLibraryTabPanel> tabPanels = new HashSet<IXLibraryTabPanel>();
			IXLibraryTabPanel tabPanel = new LibraryCustomDevicePinPanel();
			tabPanels.add(tabPanel);
			return tabPanels;
		}
		else if (groupName ==  LibraryGroupName.CLIP && tabName == LibraryTabName.LibraryExtraAttributesView) {
			Set<IXLibraryTabPanel> tabPanels = new HashSet<IXLibraryTabPanel>();
			IXLibraryTabPanel tabPanel = new LibraryModificationHistoryPanel();
			tabPanels.add(tabPanel);
			return tabPanels;
		}
		return Collections.emptySet();
	}

	public Set<IXLibraryTabPage> getTabPages(LibraryGroupName groupName)
	{
		Set<IXLibraryTabPage> tabPages = new HashSet<IXLibraryTabPage>();
		IXLibraryTabPage tabPage = new LibraryCustomTabPage();
		tabPages.add(tabPage);
		return tabPages;
	}

	public String getDescription()
	{
		return "Custom Tab panel Provider";
	}

	public String getName()
	{
		return "tabpanelProvider";
	}

	public String getVersion()
	{
		return "1.0";
	}
}




