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
import com.mentor.chs.api.IXTerminal;
import com.mentor.chs.api.IXAbstractPinList;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.sbom.IXSubAssembly;
import com.mentor.chs.plugin.sbom.IXSBOMContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Standard connector feeding operation.
 */
public class ConnectorFeedingOperation extends AbstractOperation
{
    public ConnectorFeedingOperation()
    {
        super("Example operation: Connector feeding",
                "1.0",
                "Insert terminated wires in connector cavities");
    }

	@Override
	public IXOperationType getType()
	{
		return OperationTypeImpl.CONNNECTOR_LOADING;
	}
	
	@Override
	public int getOrderHint()
	{
		return 500;
	}

	@Override
    public boolean runSelector(IXSubAssembly subAssembly, final IXSBOMContext context,
                               Set<IXSubAssembly> combinedObjects)
    {
        IXObject harnessObj = subAssembly.getHarnessObject();
        context.logMessage("Invoked Operation: " + getName() + "- harness obj = " + harnessObj);

		//
		// ========> Object type filtering:
		// - only look at connectors
        if (harnessObj instanceof IXConnector) {
            IXConnector connector = (IXConnector) harnessObj;
			Set<IXConnector> linkedConnectors = new HashSet<IXConnector>();
			retrieveLinkedUnprocessedConnectors(connector, context, linkedConnectors);
            final List<IXConnector> connectorNetwork = new ArrayList<IXConnector>(linkedConnectors);

            // Sort the list to ensure engine Stability design principle
            Comparator<IXConnector> connectorComparator = new Comparator<IXConnector>() {
                public int compare(IXConnector conn1, IXConnector conn2)
                {
                    // First order = number of pins with wires
                    int o1 = getWiredConnectorPins(conn1).size();
                    int o2 = getWiredConnectorPins(conn1).size();
                    if (o1 != o2) {
                        return o2 - o1; // from max to min
                    }

                    // Second order = connector name
                    return conn1.getAttribute("Name").compareTo(conn2.getAttribute("Name"));
                }
            };
            Collections.sort(connectorNetwork, connectorComparator);

            context.logMessage("  Sorted connectors: " + connectorNetwork.size());
            for (IXConnector conn : connectorNetwork) {
                context.logMessage("   - connector: " + conn.getAttribute("Name"));
            }

			//
			// ========> Harness filtering:
            // - Only select the connector if it is first in the list - i.e. ensure connectors
			//   are always processed in the same order to ensure stability
            boolean select = false;
            if (!connectorNetwork.isEmpty() && connector == connectorNetwork.get(0)) {
				//
                // ========> Harness combining: Include the wires
                for (IXAbstractPin pin : getWiredConnectorPins(connector)) {
                    for (IXAbstractConductor cond : pin.getConductors()) {
                        IXSubAssembly wireSub = context.getOwnerRootSubAssembly(cond);
						//
						// ========> Process filtering: make sure wire assembly contains terminals
						// i.e. wire has already gone through Terminating operation
                        Collection<IXTerminal> terminals = new HashSet<IXTerminal>();
						retrieveTerminalsFromSubAssembly(wireSub, terminals);
                        if (!terminals.isEmpty()) {
                            context.logMessage("      > add wire: " + cond.getAttribute("Name"));
                            combinedObjects.add(wireSub);
                            select = true;
                        }
                    }
                }
            }

            return select;
        }

        return false;
    }

	private void retrieveLinkedUnprocessedConnectors(IXConnector connector, final IXSBOMContext context,
			Set<IXConnector> visited)
	{
		//
		// ========> Process filtering: only include connectors which have not been processed by this operation
		// - i.e. connectors which do not belong to a sub-assembly containing wires
		IXSubAssembly fact = context.getOwnerRootSubAssembly(connector);
		Collection<IXWire> wires = new HashSet<IXWire>();
		retrieveWiresFromSubAssembly(fact, wires);
		if (!wires.isEmpty()) {
			return;
		}

		if (visited.add(connector)) {
			// Retrieve all wires and connectors at the other end
			for (IXAbstractPin pin : connector.getPins()) {
				for (IXAbstractConductor cond : pin.getConductors()) {
					for (IXAbstractPin otherPin : cond.getAbstractPins()) {
						if (otherPin != pin) {
							IXAbstractPinList owner = otherPin.getOwner();
							if (owner instanceof IXConnector) {
								retrieveLinkedUnprocessedConnectors((IXConnector) owner, context, visited);
							}
						}
					}
				}
			}
		}
	}

	private static Collection<IXAbstractPin> getWiredConnectorPins(IXConnector connector)
	{
		Collection<IXAbstractPin> pins = new ArrayList<IXAbstractPin>();

		for (IXAbstractPin pin : connector.getPins()) {
			if (!pin.getConductors().isEmpty()) {
				pins.add(pin);
			}
		}

		return pins;
	}

	private static void retrieveTerminalsFromSubAssembly(IXSubAssembly subAssy, Collection<IXTerminal> terminals)
	{
		IXObject harnessObject = subAssy.getHarnessObject();

		if (harnessObject instanceof IXTerminal) {
			terminals.add((IXTerminal) harnessObject);
		}

		for (IXSubAssembly child : subAssy.getChildren()) {
			retrieveTerminalsFromSubAssembly(child, terminals);
		}
	}

	private static void retrieveWiresFromSubAssembly(IXSubAssembly subAssy, Collection<IXWire> wires)
	{
		IXObject harnessObject = subAssy.getHarnessObject();

		if (harnessObject instanceof IXWire) {
			wires.add((IXWire) harnessObject);
		}

		for (IXSubAssembly child : subAssy.getChildren()) {
			retrieveWiresFromSubAssembly(child, wires);
		}
	}
}