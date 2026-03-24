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
package com.example.plugin.bridge;

import chs.bridges.x2ml.BackShell;
import chs.bridges.x2ml.BackShellGroup;
import chs.bridges.x2ml.Connector;
import chs.bridges.x2ml.IWiringContainer;
import chs.bridges.x2ml.Member;
import chs.bridges.x2ml.Multicore;
import chs.bridges.x2ml.Pin;
import chs.bridges.x2ml.ShieldConductor;
import chs.bridges.x2ml.UserProperty;
import chs.bridges.x2ml.Wire;
import chs.bridges.x2ml.WiringDesign;
import chs.bridges.x2ml.X2MLElement;
import chs.bridges.x2ml.harness.ConnectorShell;
import chs.bridges.x2ml.harness.Harness;
import chs.bridges.x2ml.harness.HarnessConnector;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.changemanager.IXHarnessBridgeProcessor;
import com.mentor.chs.plugin.changemanager.IXIntegratorBridgeProcessor;
import com.mentor.chs.plugin.changemanager.IXLogicBridgeProcessor;
import com.mentor.chs.plugin.changemanager.IXTopologyBridgeProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Process the bridges wiring/harness export/import as per the settings in CustomMCADWiringProcessor.properties.
 */
public class CustomMCADWiringProcessor implements IXIntegratorBridgeProcessor, IXLogicBridgeProcessor,
		IXHarnessBridgeProcessor, IXTopologyBridgeProcessor
{

	private Properties m_property;
	private Map<String, String> m_defaultProps;

	private static final String Shield_as_wire = "Shield_as_wire";
	private static final String Shield_wire_identifier = "Shield_wire_identifier";
	private static final String shieldwire = "shieldwire";
	private static final String Remove_shield = "Remove_shield";
	private static final String Shield_removal_identifier = "Shield_removal_identifier";
	private static final String shieldsremoved = "shieldsremoved";

	private static final String Backshell_as_connector = "Backshell_as_connector";
	private static final String Backshell_connector_identifier = "Backshell_connector_identifier";
	private static final String backshellconnector = "backshellconnector";
	private static final String Backshell_container = "Backshell_container";
	private static final String backshellcontainer = "backshellcontainer";

	private static final String Flatten_out_nested_multicore = "Flatten_out_nested_multicore";
	private static final String Nested_multicore = "Nested_multicore";
	private static final String nestedmulticore = "nestedmulticore";
	private static final String Hierarchy_indicator = "Hierarchy_indicator";
	private static final String multicorenesting = "multicorenesting";
	private static final String Hierarchy_separator = "Hierarchy_separator";
	private static final String Hierarchy_separator_string = ">";

	/**
	 * Return the description of the plugin
	 *
	 * @return String type - description of the plugin
	 */
	public String getDescription()
	{
		return "- Process the Bridge wiring/harness export to" +
				"\n\t1. convert shields to wires or remove shields" +
				"\n\t2. convert backshells to connectors" +
				"\n\t3. flatten out nested multicores" +
				"\n- Process the Bridge wiring/harness import to" +
				"\n\t1. convert 'marked' wires (converted earlier during export) to shields" +
				"\n\t2. convert 'marked'connectors (converted earlier during export) to backshells" +
				"\n as per the settings in CustomMCADWiringProcessor.properties";
	}

	/**
	 * Return the name of the plugin
	 *
	 * @return String type - name of the plugin
	 */
	public String getName()
	{
		return "Custom MCAD Wiring Processor";
	}

	/**
	 * Return the version of the plugin
	 *
	 * @return String type - version of the plugin
	 */
	public String getVersion()
	{
		return "1.0";
	}

	private UserProperty getUserProperty(X2MLElement elem, String propName)
	{
		UserProperty markedProp = null;
		if ((elem != null) && (propName != null)) {
			Collection<UserProperty> props = elem.getUserProperties();
			for (UserProperty prop : props) {
				if (propName.equalsIgnoreCase(prop.getName())) {
					markedProp = prop;
					break;
				}
			}
		}
		return markedProp;
	}

	private ShieldConductor createShieldFromWire(Wire wire)
	{
		ShieldConductor shield = null;
		if (wire != null) {
			shield = new ShieldConductor(wire.getId(), wire.getName(), wire.getPartNumber(), wire.getLength());
			// copy other attributes
			shield.setColor(wire.getColor());
			shield.setOutsideDiameter(wire.getOutsideDiameter());
			shield.setCSA(wire.getCSA());
			shield.setDisplayName(wire.getDisplayName());
			shield.setCustomerPartNumber(wire.getCustomerPartNumber());
			shield.setSupplierPartNumber(wire.getSupplierPartNumber());
			shield.setECADId(wire.getECADId());
			shield.setLibraryTypeCode(wire.getLibraryTypeCode());
			shield.setMaterial(wire.getMaterial());
			shield.setModule(wire.getModule());
			shield.setOptionExpression(wire.getOptionExpression());
			shield.setShortDescription(wire.getShortDescription());
			shield.setSignal(wire.getSignal());
			shield.setWireSpec(wire.getWireSpec());
			shield.setHarnessName(wire.getHarnessName());
			// pins
			List<Pin> pins = wire.getPins();
			for (Pin pin : pins) {
				shield.addPin(pin);
			}
			// user properties
			Collection<UserProperty> props = wire.getUserProperties();
			for (UserProperty prop : props) {
				// don't add the 'marked' property!
				if (!getPropertyValue(Shield_wire_identifier).equalsIgnoreCase(prop.getName())) {
					shield.addUserProperty(prop);
				}
			}
		}
		return shield;
	}

	private void convertMarkedWiresToShields(IWiringContainer wiringCont)
	{
		// get 'marked' wires
		Collection wires = wiringCont.getElementsByType(Wire.getElementName());
		Collection<Wire> markedWires = new ArrayList<Wire>();
		for (Object elem : wires) {
			if (elem instanceof Wire) {
				Wire wire = (Wire) elem;
				UserProperty shieldProp = getUserProperty(wire, getPropertyValue(Shield_wire_identifier));
				if (shieldProp != null) {
					boolean marked = Boolean.parseBoolean(shieldProp.getValue());
					if (marked) {
						markedWires.add(wire);
					}
					// remove 'marked' properties!
					wire.removeUserProperty(shieldProp);
				}
			}
		}
		// convert 'marked' wires
		Collection<Wire> wiresToRemove = new ArrayList<Wire>();
		for (Wire wire : markedWires) {
			if (wiringCont instanceof Harness) {
				// set 'shield' attribute to true
				wire.setShield(true);
			}
			else if (wiringCont instanceof WiringDesign) {
				// convert to 'shieldconductor' element
				ShieldConductor shield = createShieldFromWire(wire);
				// add shield to the design
				wiringCont.addElement(shield);
				wiresToRemove.add(wire);
			}
		}
		// remove all the wires now
		if (wiringCont instanceof WiringDesign) {
			((WiringDesign) wiringCont).removeElements(wiresToRemove);
		}
		// update multicore members!
		if (wiringCont instanceof WiringDesign) {
			Collection multicores = wiringCont.getElementsByType(Multicore.getElementName());
			for (Object elem : multicores) {
				if (elem instanceof Multicore) {
					Multicore multicore = (Multicore) elem;
					Collection<Member> members = multicore.getMembers();
					for (Member member : members) {
						X2MLElement memberElem = wiringCont.getElement(member.getReference());
						if (markedWires.contains(memberElem)) {
							// change member type to shield
							member.setType(ShieldConductor.getElementName());
						}
					}
				}
			}
		}
	}

	private BackShell createBackshellFromConnector(Connector conn)
	{
		BackShell bs = null;
		if (conn != null) {
			bs = new BackShell(conn.getName(), conn.getDisplayName(), conn.getPartNumber());
			// copy other attributes
			bs.setId(conn.getId());
			bs.setShortDescription(conn.getShortDescription());
			bs.setCustomerPartNumber(conn.getCustomerPartNumber());
			bs.setSupplierPartNumber(conn.getSupplierPartNumber());
			bs.setECADId(conn.getECADId());
			bs.setLibraryTypeCode(conn.getLibraryTypeCode());
			if (conn instanceof HarnessConnector) {
				bs.setNode(((HarnessConnector) conn).getNode());
			}
			bs.setOptionExpression(conn.getOptionExpression());
			bs.setHarnessName(conn.getHarnessName());
			// pins
			List<Pin> terms = conn.getPins();
			for (Pin term : terms) {
				bs.addPin(term);
			}
			// user properties
			Collection<UserProperty> props = conn.getUserProperties();
			for (UserProperty prop : props) {
				// do not add 'marked' properties!
				if (!(getPropertyValue(Backshell_connector_identifier).equalsIgnoreCase(prop.getName()) ||
						getPropertyValue(Backshell_container).equalsIgnoreCase(prop.getName()))) {
					bs.addUserProperty(prop);
				}
			}
		}
		return bs;
	}

	private Connector getConnector(Collection conns, String name)
	{
		Connector matchingConn = null;
		if (conns != null && name != null) {
			for (Object elem : conns) {
				if (elem instanceof Connector) {
					Connector conn = (Connector) elem;
					if (name.equals(conn.getName())) {
						matchingConn = conn;
						break;
					}
				}
			}
		}
		return matchingConn;
	}

	private void convertMarkedConnectorsToBackshells(IWiringContainer wiringCont)
	{
		// get all connectors
		Collection conns = wiringCont.getElementsByType(Connector.getElementName());
		// 'marked' connectors
		Collection<Connector> markedConns = new ArrayList<Connector>();
		for (Object elem : conns) {
			if (elem instanceof Connector) {
				Connector conn = (Connector) elem;
				UserProperty backshellProp =
						getUserProperty(conn, getPropertyValue(Backshell_connector_identifier));
				if (backshellProp != null) {
					boolean marked = Boolean.parseBoolean(backshellProp.getValue());
					if (marked) {
						UserProperty containerProp = getUserProperty(conn, getPropertyValue(Backshell_container));
						if (containerProp != null) {
							String containerName = containerProp.getValue();
							// verify if the containing connector exists!
							Connector containerConn = getConnector(conns, containerName);
							if (containerConn != null) {
								// add as backshell
								BackShell bs = createBackshellFromConnector(conn);
								containerConn.getBackShellGroup().addBackShell(bs);
								markedConns.add(conn);
							}
						}
					}
				}
			}
		}
		// remove 'marked' connectors
		if (wiringCont instanceof WiringDesign) {
			((WiringDesign) wiringCont).removeElements(markedConns);
		}
		else if (wiringCont instanceof Harness) {
			((Harness) wiringCont).removeElements(markedConns);
		}
	}

	public void processBridgeIn(Object x2mlContainer, IXApplicationContext applicationContext)
	{
		// load properties file
		loadPropertyFile();

		if (x2mlContainer instanceof IWiringContainer) {
			IWiringContainer wiringCont = (IWiringContainer) x2mlContainer;
			// 'marked' wires to shields
			convertMarkedWiresToShields(wiringCont);
			//  'marked' connectors to backshells
			convertMarkedConnectorsToBackshells(wiringCont);
		}
	}

	private void loadPropertyFile()
	{
		if (m_property == null) {
			m_property = new Properties();
		}
		if (System.getenv() != null) {
			String chsHomePath = System.getenv().get("CHS_HOME");
			if (chsHomePath != null) {
				String propsPath = chsHomePath + File.separator + "plugins" + File.separator +
						"CustomMCADWiringProcessor.properties";
				File file = new File(propsPath);
				if (file.exists()) {
					try {
						FileInputStream fileStream = new FileInputStream(file);
						m_property.load(fileStream);
					}
					catch (FileNotFoundException e) {
						// Do nothing. Use default properties
					}
					catch (IOException e) {
						// Do nothing. Use default properties
					}
				}
			}
		}
		// load default properties
		loadDefaultProperties();
	}

	private void loadDefaultProperties()
	{
		if (m_defaultProps == null) {
			m_defaultProps = new HashMap<String, String>();
		}
		// Shield_as_wire=true
		m_defaultProps.put(Shield_as_wire, Boolean.TRUE.toString());
		// Shield_wire_identifier=shieldwire
		m_defaultProps.put(Shield_wire_identifier, shieldwire);
		// Remove_shield=true
		m_defaultProps.put(Remove_shield, Boolean.TRUE.toString());
		// Shield_removal_identifier=shieldsremoved
		m_defaultProps.put(Shield_removal_identifier, shieldsremoved);

		// Backshell_as_connector=true
		m_defaultProps.put(Backshell_as_connector, Boolean.TRUE.toString());
		// Backshell_connector_identifier=backshellconnector
		m_defaultProps.put(Backshell_connector_identifier, backshellconnector);
		// Backshell_container=backshellcontainer
		m_defaultProps.put(Backshell_container, backshellcontainer);

		// Flatten_out_nested_multicore=true
		m_defaultProps.put(Flatten_out_nested_multicore, Boolean.TRUE.toString());
		// Nested_multicore=nestedmulticore
		m_defaultProps.put(Nested_multicore, nestedmulticore);
		// Hierarchy_indicator=multicorenesting
		m_defaultProps.put(Hierarchy_indicator, multicorenesting);
		// Hierarchy_separator=>
		m_defaultProps.put(Hierarchy_separator, Hierarchy_separator_string);
	}

	private String getPropertyValue(String propName)
	{
		String value = null;
		if (m_property != null) {
			value = m_property.getProperty(propName);
			if (value == null || value.isEmpty()) {
				if (m_defaultProps != null) {
					value = m_defaultProps.get(propName);
				}
			}
		}
		return value;
	}

	private void removeShields(IWiringContainer wiringCont)
	{
		boolean remove = Boolean.parseBoolean(getPropertyValue(Remove_shield));
		if (remove) {
			Collection shields = wiringCont.getElementsByType(ShieldConductor.getElementName());
			if (wiringCont instanceof WiringDesign) {
				((WiringDesign) wiringCont).removeElements(shields);
			}
			else if (wiringCont instanceof Harness) {
				((Harness) wiringCont).removeElements(shields);
			}
			// remove references from all multicores
			// update multicore members!
			Collection multicores = wiringCont.getElementsByType(Multicore.getElementName());
			for (Object elem : multicores) {
				if (elem instanceof Multicore) {
					Multicore multicore = (Multicore) elem;
					Collection<Member> members = multicore.getMembers();
					Collection<Member> toRemove = new ArrayList<Member>();
					for (Member member : members) {
						if (ShieldConductor.getElementName().equals(member.getType())) {
							toRemove.add(member);
						}
					}
					// remove
					multicore.removeMembers(toRemove);
					// add 'marker' property
					if (!toRemove.isEmpty()) {
						UserProperty prop = new UserProperty(getPropertyValue(Shield_removal_identifier),
								Boolean.TRUE.toString());
						multicore.addUserProperty(prop);
					}
				}
			}
		}
	}

	private Wire createWireFromShield(ShieldConductor shield)
	{
		Wire wire = null;
		if (shield != null) {
			wire = new Wire(shield.getId(), shield.getName(), shield.getPartNumber(), shield.getLength(),
					shield.getColor(), shield.getOutsideDiameter(), shield.getCSA(), null);
			// copy other attributes
			wire.setDisplayName(shield.getDisplayName());
			wire.setCustomerPartNumber(shield.getCustomerPartNumber());
			wire.setSupplierPartNumber(shield.getSupplierPartNumber());
			wire.setECADId(shield.getECADId());
			wire.setLibraryTypeCode(shield.getLibraryTypeCode());
			wire.setMaterial(shield.getMaterial());
			wire.setModule(shield.getModule());
			wire.setOptionExpression(shield.getOptionExpression());
			wire.setShortDescription(shield.getShortDescription());
			wire.setSignal(shield.getSignal());
			wire.setWireSpec(shield.getWireSpec());
			wire.setHarnessName(shield.getHarnessName());
			// pins
			List<Pin> pins = shield.getPins();
			for (Pin pin : pins) {
				wire.addPin(pin);
			}
			// user properties
			Collection<UserProperty> props = shield.getUserProperties();
			for (UserProperty prop : props) {
				wire.addUserProperty(prop);
			}
			// add user property
			UserProperty prop = new UserProperty(getPropertyValue(Shield_wire_identifier), Boolean.TRUE.toString());
			wire.addUserProperty(prop);
		}
		return wire;
	}

	private void convertShieldsToWires(IWiringContainer wiringCont)
	{
		boolean convert = Boolean.parseBoolean(getPropertyValue(Shield_as_wire));
		if (convert) {
			// wires with "shield" attribute "true"
			/*
			Collection wires = wiringCont.getElementsByType(Wire.getElementName());
			for(Object elem : wires) {
				if(elem instanceof Wire) {
					Wire wire = (Wire) elem;
					if (wire.isShield()) {
						// shield! convert to wire!
						wire.setShield(false);
						// add user property
						UserProperty prop =
								new UserProperty(getPropertyValue(Shield_wire_identifier), Boolean.TRUE.toString());
						wire.addUserProperty(prop);
					}
				}
			}
			*/
			// gather all backshells
			Set<BackShell> backShells = new HashSet<BackShell>();
			Collection conns = wiringCont.getElementsByType(Connector.getElementName());
			// add all shells
			conns.addAll(wiringCont.getElementsByType(ConnectorShell.getElementName()));
			// new connectors
			Collection<Connector> newConns = new ArrayList<Connector>();
			for (Object elem : conns) {
				if (elem instanceof Connector) {
					Connector conn = (Connector) elem;
					BackShellGroup bsGrp = conn.getBackShellGroup();
					if (bsGrp != null) {
						List<BackShell> backshells = bsGrp.getBackShells();
						for (BackShell bs : backshells) {
							if (!bs.getPins().isEmpty()) {
								backShells.add(bs);
							}
						}
					}
				}
			}
			// shields
			Collection shields = wiringCont.getElementsByType(ShieldConductor.getElementName());
			Collection<ShieldConductor> shieldsToBeRemoved = new ArrayList<ShieldConductor>();
			for (Object elem : shields) {
				if (elem instanceof ShieldConductor) {
					ShieldConductor shield = (ShieldConductor) elem;
					boolean convertBSToConn = Boolean.parseBoolean(getPropertyValue(Backshell_as_connector));
					boolean connectedToBackShell = false;
					if (!convertBSToConn) {
						// check if this shield terminates in backshell!
						List<Pin> shieldPins = shield.getPins();
						for (Pin shieldPin : shieldPins) {
							// see if this is connected to backshell pin
							for (BackShell bs : backShells) {
								Pin bsPin = bs.searchPin(shieldPin.getId());
								if (bsPin != null) {
									connectedToBackShell = true;
									break;
								}
							}
							if (connectedToBackShell) {
								break;
							}
						}
					}
					if (!connectedToBackShell) {
						// create wire
						Wire wire = createWireFromShield(shield);
						// add wire to the design
						wiringCont.addElement(wire);
					}
					else {
						shieldsToBeRemoved.add(shield);
					}
				}
			}
			// remove all the shields now
			if (wiringCont instanceof WiringDesign) {
				((WiringDesign) wiringCont).removeElements(
						wiringCont.getElementsByType(ShieldConductor.getElementName()));
			}
			else if (wiringCont instanceof Harness) {
				((Harness) wiringCont).removeElements(wiringCont.getElementsByType(ShieldConductor.getElementName()));
			}
			// update multicore members!
			Collection multicores = wiringCont.getElementsByType(Multicore.getElementName());
			for (Object elem : multicores) {
				if (elem instanceof Multicore) {
					Multicore multicore = (Multicore) elem;
					Collection<Member> members = multicore.getMembers();
					Collection<Member> removableMembers = new ArrayList<Member>();
					for (Member member : members) {
						if (ShieldConductor.getElementName().equals(member.getType())) {
							// change to wire!
							member.setType(Wire.getElementName());
							// see if this is removable shield!
							for (ShieldConductor shield : shieldsToBeRemoved) {
								if (shield.getId().equals(member.getReference())) {
									removableMembers.add(member);
								}
							}
						}
					}
					// remove members
					multicore.removeMembers(removableMembers);
				}
			}
		}
	}

	private HarnessConnector createConnectorFromBackshell(BackShell bs, Connector parentConn)
	{
		HarnessConnector newConn = null;
		if (bs != null && parentConn != null) {
			newConn = new HarnessConnector(bs.getName(), bs.getDisplayName(), bs.getPartNumber());
			// copy other attributes
			newConn.setId(bs.getId());
			newConn.setShortDescription(bs.getShortDescription());
			newConn.setCustomerPartNumber(bs.getCustomerPartNumber());
			newConn.setSupplierPartNumber(bs.getSupplierPartNumber());
			newConn.setECADId(bs.getECADId());
			newConn.setLibraryTypeCode(bs.getLibraryTypeCode());
			newConn.setNode(bs.getNode());
			newConn.setOptionExpression(bs.getOptionExpression());
			if (bs.getContainer() instanceof X2MLElement) {
				newConn.setHarnessName(((X2MLElement) bs.getContainer()).getHarnessName());
			}
			// pins
			List<Pin> terms = bs.getPins();
			for (Pin term : terms) {
				newConn.addPin(term);
			}
			// user properties
			Collection<UserProperty> props = bs.getUserProperties();
			for (UserProperty prop : props) {
				newConn.addUserProperty(prop);
			}
			// add special user properties!
			UserProperty prop1 = new UserProperty(getPropertyValue(Backshell_connector_identifier),
					Boolean.TRUE.toString());
			UserProperty prop2 = new UserProperty(getPropertyValue(Backshell_container), parentConn.getName());
			newConn.addUserProperty(prop1);
			newConn.addUserProperty(prop2);
		}
		return newConn;
	}

	private void convertBackshellsToConnectors(IWiringContainer wiringCont)
	{
		boolean convert = Boolean.parseBoolean(getPropertyValue(Backshell_as_connector));
		if (convert) {
			// get all connectors
			Collection conns = wiringCont.getElementsByType(Connector.getElementName());
			// add all shells
			conns.addAll(wiringCont.getElementsByType(ConnectorShell.getElementName()));
			// new connectors
			Collection<Connector> newConns = new ArrayList<Connector>();
			for (Object elem : conns) {
				if (elem instanceof Connector) {
					Connector conn = (Connector) elem;
					BackShellGroup bsGrp = conn.getBackShellGroup();
					if (bsGrp != null) {
						List<BackShell> backshells = bsGrp.getBackShells();
						Collection<BackShell> backShellsToRemove = new ArrayList<BackShell>();
						for (BackShell bs : backshells) {
							List<Pin> pins = bs.getPins();
							if (pins != null && !pins.isEmpty()) {
								// convert to a connector
								HarnessConnector newConn = createConnectorFromBackshell(bs, conn);
								// todo - remove bs from conn
								backShellsToRemove.add(bs);
								// add newConn
								newConns.add(newConn);
							}
						}
						// remove backshells
						backshells.removeAll(backShellsToRemove);
					}
				}
			}
			// add all new connectors to container
			for (Connector conn : newConns) {
				wiringCont.addElement(conn);
			}
		}
	}

	private Collection<Member> flattenNestedMulticore(Multicore multicore, IWiringContainer wiringCont,
			String hierarchy)
	{
		Collection<Member> membersToAdd = new ArrayList<Member>();
		StringBuilder mulHierarchy = new StringBuilder(hierarchy);
		if (multicore != null) {
			String separator = getPropertyValue(Hierarchy_separator);
			String hierarchyStr = mulHierarchy.append(separator).append(multicore.getDisplayName()).toString();
			// special property to add!
			String hierarchyInd = getPropertyValue(Hierarchy_indicator);
			UserProperty prop = new UserProperty(hierarchyInd, hierarchyStr);

			Collection<Member> members = multicore.getMembers();
			Collection<Member> toRemove = new ArrayList<Member>();
			for (Member member : members) {
				// remove member from this multicore and add to top most
				toRemove.add(member);
				X2MLElement memberElem = wiringCont.getElement(member.getReference());
				if (memberElem instanceof Multicore) {
					membersToAdd.addAll(flattenNestedMulticore((Multicore) memberElem, wiringCont, hierarchyStr));
				}
				else {
					// add to top most
					membersToAdd.add(member);
					memberElem.addUserProperty(prop);
				}
			}
			// remove members
			multicore.removeMembers(toRemove);
		}
		return membersToAdd;
	}

	private void flattenOutMulticores(IWiringContainer wiringCont)
	{
		boolean convert = Boolean.parseBoolean(getPropertyValue(Flatten_out_nested_multicore));
		if (convert) {
			// get all multicores
			Collection multicores = wiringCont.getElementsByType(Multicore.getElementName());
			// collect all top level multicores
			Collection<Multicore> topMults = new ArrayList<Multicore>();
			for (Object elem : multicores) {
				if (elem instanceof Multicore) {
					Multicore multicore = (Multicore) elem;
					if (multicore.getParentMulticore() == null) {
						topMults.add(multicore);
					}
				}
			}
			// traverse all the top multicores
			for (Multicore topMult : topMults) {
				Collection<Member> members = topMult.getMembers();
				Collection<Member> membersToAdd = new ArrayList<Member>();
				Collection<Member> membersToRemove = new ArrayList<Member>();
				boolean nested = false;
				for (Member member : members) {
					X2MLElement memberElem = wiringCont.getElement(member.getReference());
					if (memberElem instanceof Multicore) {
						nested = true;
						membersToAdd.addAll(flattenNestedMulticore((Multicore) memberElem, wiringCont,
								topMult.getDisplayName()));
						membersToRemove.add(member);
					}
				}
				if (nested) {
					// add special user property!
					UserProperty prop = new UserProperty(getPropertyValue(Nested_multicore), Boolean.TRUE.toString());
					topMult.addUserProperty(prop);
				}
				// add all the flattened members
				for (Member member : membersToAdd) {
					topMult.addMember(member.getReference(), member.getType());
				}
				// remove all child multicores
				topMult.removeMembers(membersToRemove);
			}
			// remove all non-top multicores from wiring container
			Collection<Multicore> multicoresToRemove = new ArrayList<Multicore>();
			multicoresToRemove.addAll(multicores);
			multicoresToRemove.removeAll(topMults);
			if (wiringCont instanceof WiringDesign) {
				((WiringDesign) wiringCont).removeElements(multicoresToRemove);
			}
			else if (wiringCont instanceof Harness) {
				((Harness) wiringCont).removeElements(multicoresToRemove);
			}
		}
	}

	public void processBridgeOut(Object x2mlContainer, IXApplicationContext applicationContext)
	{
		// load properties file
		loadPropertyFile();

		if (x2mlContainer instanceof IWiringContainer) {
			IWiringContainer wiringCont = (IWiringContainer) x2mlContainer;
			// remove shields
			removeShields(wiringCont);
			// shields to wires
			convertShieldsToWires(wiringCont);
			// backshells to connectors
			convertBackshellsToConnectors(wiringCont);
			// flatten out multicores
			flattenOutMulticores(wiringCont);
		}
	}
}
