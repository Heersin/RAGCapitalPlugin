/*
 * Copyright 2012 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * Recipients who obtain this code directly from Mentor Graphics use it solely for internal purposes to serve as
 * example plugin. This code may not be used in a commercial distribution.
 * Recipients may duplicate the code provided that all notices are fully reproduced with and remain in the code.
 * No part of this code may be modified, reproduced, translated, used, distributed, disclosed or provided to
 * third parties without the prior written consent of Mentor Graphics, except as expressly authorized above.
 *
 * THE CODE IS MADE AVAILABLE "AS IS" WITHOUT WARRANTY OR SUPPORT OF ANY KIND.
 * MENTOR GRAPHICS OFFERS NO EXPRESS OR IMPLIED WARRANTIES AND SPECIFICALLY DISCLAIMS ANY WARRANTY OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR WARRANTY OF NON-INFRINGEMENT.
 * IN NO EVENT SHALL MENTOR GRAPHICS OR ITS LICENSORS BE LIABLE FOR DIRECT, INDIRECT, SPECIAL, INCIDENTAL,
 * OR CONSEQUENTIAL DAMAGES (INCLUDING LOST PROFITS OR SAVINGS) WHETHER BASED ON CONTRACT, TORT OR ANY OTHER LEGAL
 * THEORY, EVEN IF MENTOR GRAPHICS OR ITS LICENSORS HAVE BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */
package com.example.plugin.changepolicy;

import com.mentor.chs.plugin.IXCurrentContext;
import com.mentor.chs.plugin.changepolicy.IXChangePolicy;
import com.mentor.chs.plugin.changepolicy.IXChangePolicySelector;
import com.mentor.chs.plugin.user.IXDomain;
import com.mentor.chs.plugin.user.IXRole;
import com.mentor.chs.plugin.user.IXUser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChangePolicySelector implements IXChangePolicySelector
{
	public IXChangePolicy getSelectedChangePolicy(IXCurrentContext context, Map<String, ? extends IXChangePolicy> policies)
	{
		displayDetails(context);
		return choosePolicy(policies, context);
	}

	public Set<IXChangePolicy> getFilteredChangePolicies(IXCurrentContext context, Map<String, IXChangePolicy> policies)
	{
		displayDetails(context);
		return new HashSet<IXChangePolicy>(policies.values());
	}

	public String getDescription()
	{
		return "Select a Change Policy based on the current context";
	}

	public String getName()
	{
		return "Sample Change Policy Selector";
	}

	public String getVersion()
	{
		return "0.1";
	}

	/**
	 * Alphabatically sorts the given set of policies from 'policies' and returns the first entry in the sorted list.
	 *
	 * @param policies - The map of changes policies vs their name
	 * @param context - The application's current context.
	 *
	 * @return - Returns a change policy chosen from the set of policies from 'policies'
	 *
	 */
	private IXChangePolicy choosePolicy(Map<String, ? extends IXChangePolicy> policies, IXCurrentContext context)
	{
		IXChangePolicy retPolicy = null;
		if (policies == null || policies.isEmpty()) {
			System.out.println("No policies to choose from");
			return null;
		}
		List<IXChangePolicy> sorted = new ArrayList<IXChangePolicy>(policies.values());
		Collections.sort(sorted, new Comparator<IXChangePolicy>()
		{
			@Override public int compare(IXChangePolicy o1, IXChangePolicy o2)
			{
				return o1.getName().compareTo(o2.getName());
			}
		});
		System.out.println(" -- Choices --");

		retPolicy = sorted.get(0);
		for (IXChangePolicy cp : sorted) {
			System.out.println(cp.getName());
		}

		return retPolicy;
	}

	/**
	 * This method displays the context information sent from the application.
	 *
	 * @param context - the context obtained from the application.
	 *
	 */
	private void displayDetails(IXCurrentContext context)
	{
		System.out.println("Current Build List:  " + context.getActiveBuildList());
		System.out.println("Current Project:     " + context.getCurrentProject());
		System.out.println("Current Design:      " + context.getCurrentDesign());
		System.out.println("Current Diagram:     " + context.getCurrentDiagram());
		IXUser user = context.getCurrentUser();
		if (user != null) {
			System.out.println("Current User:        " + user);
			System.out.println("Current User [CP]:   " + user.getChangePolicy());
			for (IXDomain domain : user.getDomains()) {
				System.out.println("    Domain:   " + domain.getName());
			}
			for (IXRole role : user.getRoles()) {
				System.out.println("    Role:   " + role.getName());
			}
		}
	}
}
