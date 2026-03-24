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

package com.example.plugin.sbom;

import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXAbstractPinList;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.plugin.sbom.IXSBOMContext;

import java.util.Set;
import java.util.HashSet;

/**
 * A connector assembly operation which is erroneously implemented
 */
public class WrongConnectorAssemblyOperation extends AbstractOperation
{
	private static final int MAX_LINKED_CONNECTORS = 1;

    public WrongConnectorAssemblyOperation()
    {
        super("Example operation: Connector assembly - wrong implementation",
                "1.0",
                "Warning: makes the SBOM generation engine fail");
    }

	@Override
	public int getOrderHint()
	{
		return 710;
	}

	@Override
    public boolean runSelector(IXSubAssembly subAssembly, final IXSBOMContext context,
                               Set<IXSubAssembly> combinedObjects)
    {
        IXObject harnessObj = subAssembly.getHarnessObject();

		//
		// ========> Object type filtering:
		// - only look at connectors
        if (harnessObj instanceof IXConnector) {			
            IXConnector connector = (IXConnector) harnessObj;
			context.logMessage(">>> input fact: " + getObjectName(connector));
			Set<IXConnector> linkedConnectors = new HashSet<IXConnector>();
			retrieveLinkedConnectors(connector, context, linkedConnectors);
			linkedConnectors.remove(connector);

			// If all linked connectors are added to the combined objects,
			// then there is no problem because the same set of children will
			// be produced for each input connector in each linked group and the engine
			// will recognise each returned set is either the same or has no intersection
			// with any other set, and will gracefully eliminate duplicate sets.
			//
			// However, if the sets are limited to a given maximum size, then
			// this method will return different sets which have intersections.
			// Since only one parent can be produced for a given sub-assembly,
			// the engine cannot decide which of 2 intersecting (but different) sets
			// is to be selected as the set of children for the new parent to be created.
			//
			int count = 0;
			for (IXConnector linkedConnector : linkedConnectors) {
				context.logMessage("     linked connector: " + getObjectName(linkedConnector));
				combinedObjects.add(context.getOwnerRootSubAssembly(linkedConnector));
				if (++count >= MAX_LINKED_CONNECTORS) {
					// This produces the error of self-intersection in the engine
					break;
				}
			}
            return true;
        }

        return false;
    }

	private void retrieveLinkedConnectors(IXConnector connector, final IXSBOMContext context,
			Set<IXConnector> visited)
	{
		if (visited.add(connector)) {
			// Retrieve all wires and connectors at the other end
			for (IXAbstractPin pin : connector.getPins()) {
				for (IXAbstractConductor cond : pin.getConductors()) {
					for (IXAbstractPin otherPin : cond.getAbstractPins()) {
						if (otherPin != pin) {
							IXAbstractPinList owner = otherPin.getOwner();
							if (owner instanceof IXConnector) {
								retrieveLinkedConnectors((IXConnector) owner, context, visited);
							}
						}
					}
				}
			}
		}
	}
}