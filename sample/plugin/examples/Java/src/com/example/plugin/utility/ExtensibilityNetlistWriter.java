/*
 * Copyright 2007-2014 Mentor Graphics Corporation
 * All Rights Reserved
 *
 * THIS WORK CONTAINS TRADE SECRET AND PROPRIETARY
 * INFORMATION WHICH IS THE PROPERTY OF MENTOR
 * GRAPHICS CORPORATION OR ITS LICENSORS AND IS
 * SUBJECT TO LICENSE TERMS.
 */

package com.example.plugin.utility;

import com.mentor.chs.api.IXAbstractConductor;
import com.mentor.chs.api.IXAbstractConnector;
import com.mentor.chs.api.IXAbstractDevice;
import com.mentor.chs.api.IXAbstractPin;
import com.mentor.chs.api.IXAbstractPinList;
import com.mentor.chs.api.IXAbstractTopologyDesign;
import com.mentor.chs.api.IXAdditionalComponent;
import com.mentor.chs.api.IXAssembly;
import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXBackshell;
import com.mentor.chs.api.IXBackshellTermination;
import com.mentor.chs.api.IXBlock;
import com.mentor.chs.api.IXBreakoutTape;
import com.mentor.chs.api.IXBuildList;
import com.mentor.chs.api.IXBundle;
import com.mentor.chs.api.IXBundleRegion;
import com.mentor.chs.api.IXCavity;
import com.mentor.chs.api.IXCavityDetail;
import com.mentor.chs.api.IXCavitySeal;
import com.mentor.chs.api.IXClip;
import com.mentor.chs.api.IXConfiguration;
import com.mentor.chs.api.IXConnectivity;
import com.mentor.chs.api.IXConnector;
import com.mentor.chs.api.IXConnectorPinMap;
import com.mentor.chs.api.IXConnectorRefinement;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXDevice;
import com.mentor.chs.api.IXDeviceConnector;
import com.mentor.chs.api.IXDevicePin;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXEngineeringChangeOrder;
import com.mentor.chs.api.IXEngineeringChangeOrderAssociatable;
import com.mentor.chs.api.IXFixture;
import com.mentor.chs.api.IXFunctionalModuleCode;
import com.mentor.chs.api.IXGraphicGroup;
import com.mentor.chs.api.IXGraphicObject;
import com.mentor.chs.api.IXGrommet;
import com.mentor.chs.api.IXGround;
import com.mentor.chs.api.IXHarness;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXHarnessDiagram;
import com.mentor.chs.api.IXHarnessDiagramAxialDimension;
import com.mentor.chs.api.IXHarnessDiagramBundle;
import com.mentor.chs.api.IXHarnessDiagramFixture;
import com.mentor.chs.api.IXHarnessDiagramNodeDimension;
import com.mentor.chs.api.IXHarnessLevel;
import com.mentor.chs.api.IXHarnessRegister;
import com.mentor.chs.api.IXHighway;
import com.mentor.chs.api.IXHole;
import com.mentor.chs.api.IXInsulation;
import com.mentor.chs.api.IXInsulationRun;
import com.mentor.chs.api.IXIntegratorDesign;
import com.mentor.chs.api.IXInterconnect;
import com.mentor.chs.api.IXInterconnectDevice;
import com.mentor.chs.api.IXInternalLink;
import com.mentor.chs.api.IXInternalPin;
import com.mentor.chs.api.IXInternalPosition;
import com.mentor.chs.api.IXInternalPositionedObject;
import com.mentor.chs.api.IXInternalPositionsContainer;
import com.mentor.chs.api.IXLibrariedObject;
import com.mentor.chs.api.IXLibrary;
import com.mentor.chs.api.IXLibraryAssembly;
import com.mentor.chs.api.IXLibraryAssemblyDetails;
import com.mentor.chs.api.IXLibraryBackshell;
import com.mentor.chs.api.IXLibraryBackshellPlug;
import com.mentor.chs.api.IXLibraryBackshellSeal;
import com.mentor.chs.api.IXLibraryBaseAssembly;
import com.mentor.chs.api.IXLibraryBaseConnector;
import com.mentor.chs.api.IXLibraryCavity;
import com.mentor.chs.api.IXLibraryCavityContainer;
import com.mentor.chs.api.IXLibraryCavityGroup;
import com.mentor.chs.api.IXLibraryCavityGroupDetails;
import com.mentor.chs.api.IXLibraryCavityPlug;
import com.mentor.chs.api.IXLibraryCavitySeal;
import com.mentor.chs.api.IXLibraryClip;
import com.mentor.chs.api.IXLibraryColorCode;
import com.mentor.chs.api.IXLibraryComponentScope;
import com.mentor.chs.api.IXLibraryComponentTypeCode;
import com.mentor.chs.api.IXLibraryConnector;
import com.mentor.chs.api.IXLibraryConnectorMating;
import com.mentor.chs.api.IXLibraryConnectorMatingMapping;
import com.mentor.chs.api.IXLibraryConnectorSeal;
import com.mentor.chs.api.IXLibraryCustomerOrganisation;
import com.mentor.chs.api.IXLibraryCustomerPartNumber;
import com.mentor.chs.api.IXLibraryDevice;
import com.mentor.chs.api.IXLibraryDeviceFootprint;
import com.mentor.chs.api.IXLibraryDeviceFootprintPinMapping;
import com.mentor.chs.api.IXLibraryDevicePin;
import com.mentor.chs.api.IXLibraryDressedRoute;
import com.mentor.chs.api.IXLibraryFixture;
import com.mentor.chs.api.IXLibraryFixtureSelection;
import com.mentor.chs.api.IXLibraryGrommet;
import com.mentor.chs.api.IXLibraryHeatshrinkSleeve;
import com.mentor.chs.api.IXLibraryHeatshrinkSleeveSelection;
import com.mentor.chs.api.IXLibraryHousingDefinition;
import com.mentor.chs.api.IXLibraryIDCConnector;
import com.mentor.chs.api.IXLibraryInHouseAssembly;
import com.mentor.chs.api.IXLibraryInnerCore;
import com.mentor.chs.api.IXLibraryManufacturingOrganisation;
import com.mentor.chs.api.IXLibraryMaterialCode;
import com.mentor.chs.api.IXLibraryMultiWireCore;
import com.mentor.chs.api.IXLibraryMulticore;
import com.mentor.chs.api.IXLibraryMultipleTerminationsConfiguration;
import com.mentor.chs.api.IXLibraryMultipleWireFitsCavityConfiguration;
import com.mentor.chs.api.IXLibraryObject;
import com.mentor.chs.api.IXLibraryOther;
import com.mentor.chs.api.IXLibraryRevisionGroup;
import com.mentor.chs.api.IXLibraryRingTerminalGroup;
import com.mentor.chs.api.IXLibraryRingTerminalGroupDetail;
import com.mentor.chs.api.IXLibraryScope;
import com.mentor.chs.api.IXLibraryScopeCode;
import com.mentor.chs.api.IXLibrarySingleWireCore;
import com.mentor.chs.api.IXLibrarySolderSleeve;
import com.mentor.chs.api.IXLibrarySolderSleeveSelection;
import com.mentor.chs.api.IXLibrarySplice;
import com.mentor.chs.api.IXLibrarySpliceSelection;
import com.mentor.chs.api.IXLibrarySupplierOrganisation;
import com.mentor.chs.api.IXLibrarySupplierPartNumber;
import com.mentor.chs.api.IXLibrarySymbol;
import com.mentor.chs.api.IXLibraryTape;
import com.mentor.chs.api.IXLibraryTapeSelection;
import com.mentor.chs.api.IXLibraryTerminal;
import com.mentor.chs.api.IXLibraryTermination;
import com.mentor.chs.api.IXLibraryTerminationsProvider;
import com.mentor.chs.api.IXLibraryTube;
import com.mentor.chs.api.IXLibraryUltrasonicWeld;
import com.mentor.chs.api.IXLibraryUltrasonicWeldSelection;
import com.mentor.chs.api.IXLibraryUserProperty;
import com.mentor.chs.api.IXLibraryWire;
import com.mentor.chs.api.IXLibraryWireFitsCavity;
import com.mentor.chs.api.IXLibraryWireFitsCavityProvider;
import com.mentor.chs.api.IXLibraryWireGroup;
import com.mentor.chs.api.IXLibraryWireInsulationThickness;
import com.mentor.chs.api.IXLibraryWirePitch;
import com.mentor.chs.api.IXLibraryWireSpec;
import com.mentor.chs.api.IXLogicDesign;
import com.mentor.chs.api.IXLogicDiagram;
import com.mentor.chs.api.IXLogicDiagramConductor;
import com.mentor.chs.api.IXLogicDiagramDaisyChain;
import com.mentor.chs.api.IXLogicDiagramMultiCore;
import com.mentor.chs.api.IXLogicDiagramPin;
import com.mentor.chs.api.IXLogicDiagramPinList;
import com.mentor.chs.api.IXMarker;
import com.mentor.chs.api.IXMultiLocationComponent;
import com.mentor.chs.api.IXMulticore;
import com.mentor.chs.api.IXNameInfo;
import com.mentor.chs.api.IXNet;
import com.mentor.chs.api.IXNode;
import com.mentor.chs.api.IXNodeComponent;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXObjectTypeInfo;
import com.mentor.chs.api.IXOption;
import com.mentor.chs.api.IXOptionFolder;
import com.mentor.chs.api.IXOtherComponent;
import com.mentor.chs.api.IXOverbraid;
import com.mentor.chs.api.IXProductionModuleCode;
import com.mentor.chs.api.IXProject;
import com.mentor.chs.api.IXPropertyInfo;
import com.mentor.chs.api.IXPropertyInfoRange;
import com.mentor.chs.api.IXReferenceNode;
import com.mentor.chs.api.IXRefinedConnector;
import com.mentor.chs.api.IXRingTerminal;
import com.mentor.chs.api.IXShield;
import com.mentor.chs.api.IXSignal;
import com.mentor.chs.api.IXSlot;
import com.mentor.chs.api.IXSplice;
import com.mentor.chs.api.IXSplicePin;
import com.mentor.chs.api.IXSpotTape;
import com.mentor.chs.api.IXStructureNode;
import com.mentor.chs.api.IXSymbol;
import com.mentor.chs.api.IXTerminal;
import com.mentor.chs.api.IXTopologyDesign;
import com.mentor.chs.api.IXValue;
import com.mentor.chs.api.IXVariance;
import com.mentor.chs.api.IXVariantReferenceNodePosition;
import com.mentor.chs.api.IXVehicleConfiguration;
import com.mentor.chs.api.IXVehicleModel;
import com.mentor.chs.api.IXWire;
import com.mentor.chs.api.IXWireEnd;
import com.mentor.chs.api.IXZoneInfo;

import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

//import com.mentor.chs.api.IXDrillPoint;

/**
 * This class is a logger class and outputs all possible relations/attributes/properties for a given object(s).
 */
public class ExtensibilityNetlistWriter
{

	protected static final String m_CustomerDateAttribute = "Customer Date";
	protected static final String m_InternalDateAttribute = "Internal Date";

	protected static final String m_Harness = "Harness ";
	protected static final String m_HarnessLevel = "Harness Level";
	protected static final String m_Bundle = "Bundle";
	protected static final String m_Connector = "Connector";
	protected static final String m_ReferenceNode = "Reference Node";
	protected static final String m_StructureNode = "Structure Node";
	protected static final String m_Splice = "Splice";
	protected static final String m_Pin = "Cavity";
	protected static final String m_DevicePin = "DevicePin";
	protected static final String m_SplicePin = "SplicePin";
	protected static final String m_BackshellTermination = "BackshellTermination";
	protected static final String m_Multicore = "Multicore";
	protected static final String m_Overbraid = "Overbraid";
	protected static final String m_Wire = "Wire";
	protected static final String m_HarnessRegister = "Harness Register";
	protected static final String m_customerDate = "Customer Date";
	protected static final String m_internalDate = "Internal Date";
	protected static final String m_WireEnd = "Wire End";
	protected static final String m_Conductor = "Conductor";
	protected static final String m_Backshell = "Backshell";
	protected static final String m_AdditionalComponent = "Additional Component";
	protected static final String m_InsulationRun = "Insulation Run";
	protected static final String m_Insulation = "Insulation";
	protected static final String m_Clip = "Clip";
	protected static final String m_Grommet = "Grommet";
	protected static final String m_OtherComponent = "Other Component";
	protected static final String m_SpotTape = "Spot Tape";
	protected static final String m_BreakoutTape = "Breakout Tape";
	protected static final String m_MLC = "Multi Location Component";
	protected static final String m_BundleRegion = "Bundle Region";
	protected static final String m_DeviceConnector = "Device Connector";
	protected static final String m_CavityDetail = "Cavity Detail";
	protected static final String m_Terminal = "Terminal";
	protected static final String m_Seal = "Seal";
	protected static final String m_Device = "Device";
	protected static final String m_Slot = "Slot";
	protected static final String m_SlotType = "Slot Type";
	protected static final String m_Hole = "Hole";
	protected static final String m_Signal = "Signal";
	protected static final String m_Shield = "Shield";
	protected static final String m_Net = "Net";
	protected static final String m_Interconnect = "Interconnect";
	protected static final String m_Assembly = "Assembly";
	protected static final String m_LogicDesign = "Logic Design";
	protected static final String m_IntegratorDesign = "Integrator Design";
	protected static final String m_TopologyDesign = "Topology Design";
	protected static final String m_HarnessDesign = "Harness Design";
	protected static final String m_Project = "Project";
	protected static final String m_BuildList = "BuildList";
	protected static final String m_Diagram = "Diagram";
	protected static final String m_Ground = "Ground";
	protected static final String m_Option = "Option";
	protected static final String m_InterconnectDevice = "Interconnect Device";
	protected static final String m_Marker = "Marker";
	protected static final String m_VehicleModel = "Vehicle Model";
	protected static final String m_VehicleConfiguration = "Vehicle Configuration";
	protected static final String m_Configuration = "Configuration";
	protected static final String m_OptionFolder = "Option Folder";
	protected static final String m_ObjectTypeInfo = "Object Type Info";
	protected static final String m_NameInfo = "Name Info";
	protected static final String m_PropertyInfo = "Property Info";
	protected static final String m_PropertyInfoRange = "Property Info Range";
	protected static final String m_Fixture = "Fixture";
	protected static final String m_VariantReferenceNodePosition = "Variant Reference Node Position";

	protected static final String m_ID = "ID";
	protected static final String m_BaseID = "Base ID";
	protected static final String m_ParentID = "Parent ID";
	protected static final String m_Name = "Name";
	protected static final String m_Description = "Description";
	protected static final String m_ShortDescription = "Short " + m_Description;
	protected static final String m_PropertyType = "Property Type";
	protected static final String m_AutoAssign = "Auto Assign";
	protected static final String m_DefaultValue = "Default Value";
	protected static final String m_StartValue = "Start Value";
	protected static final String m_EndValue = "End Value";

	protected static final String m_attributeKey = "ATTR ";
	protected static final String m_propertyKey = "PROP ";
	protected static final String m_harnessLevelKey = "HARNESS_LEVEL";
	protected static final String m_activeHarnessLevelKey = "ACTIVE_" + m_harnessLevelKey;
	protected static final String m_optionKey = "OPT";
	protected static final String m_connectorKey = "CONNECTOR";
	protected static final String m_bundleKey = "BUN";
	protected static final String m_varianceKey = "VAR";
	protected static final String m_NodeKey = "NODE";
	protected static final String m_MLCKey = "MLC_LOC";
	protected static final String m_LibraryAssembly = "LibraryAssembly";
	protected static final String m_LibraryAssemblyDetails = "Library Assembly Details";
	protected static final String m_LibraryBackshell = "Library Backshell";
	protected static final String m_LibraryBackshellPlug = "Library Backshell Plug";
	protected static final String m_LibraryBackshellSeal = "Library Backshell Seal";
	protected static final String m_LibraryCavity = "Library Cavity";
	protected static final String m_LibraryCavityContainer = "Library Cavity Container";
	protected static final String m_LibraryCavityGroup = "Library Cavity Group";
	protected static final String m_LibraryCavityGroupDetails = "Library Cavity Group Details";
	protected static final String m_LibraryCavityPlug = "Library Cavity Plug";
	protected static final String m_LibraryCavitySeal = "Library Cavity Seal";
	protected static final String m_LibraryClip = "Library Clip";
	protected static final String m_LibraryColorCode = "Library Color Code";
	protected static final String m_LibraryComponentTypeCode = "Library Component Type Code";
	protected static final String m_LibraryConnector = "Library Connector";
	protected static final String m_LibraryConnectorMating = "Library Connector Mating";
	protected static final String m_LibraryConnectorMatingMapping = "Library Connector Mating Mapping";
	protected static final String m_LibraryConnectorSeal = "Library Connector Seal";
	protected static final String m_LibraryCustomerOrganisation = "Library Customer Organisation";
	protected static final String m_LibraryCustomerPartNumber = "Library Customer PartNumber";
	protected static final String m_LibraryDevice = "Library Device";
	protected static final String m_LibraryDeviceFootprint = "Library Device Footprint";
	protected static final String m_LibraryDeviceFootprintPinMapping = "Library DeviceFootprint Pin Mapping";
	protected static final String m_LibraryDevicePin = "Library Device Pin";
	protected static final String m_LibraryDressedRoute = "Library Dressed Route";
	protected static final String m_LibraryGrommet = "Library Grommet";
	protected static final String m_LibraryHeatshrinkSleeve = "Library Heatshrink Sleeve";
	protected static final String m_LibraryHeatshrinkSleeveSelection = "Library Heatshrink Sleeve Selection";
	protected static final String m_LibraryHousingDefinition = "Library Housing Definition";
	protected static final String m_LibraryIDCConnector = "Library IDC Connector";
	protected static final String m_LibraryInHouseAssembly = "Library InHouse Assembly";
	protected static final String m_LibraryManufacturingOrganisation = "Library Manufacturing Organisation";
	protected static final String m_LibraryMaterialCode = "Library Material Code";
	protected static final String m_LibraryMulticore = "Library Multicore";
	protected static final String m_LibraryMultipleTerminationsConfiguration =
			"Library Multiple Terminations Configuration";
	protected static final String m_LibraryMultipleWireFitsCavityConfiguration =
			"Library Multiple WireFitsCavity Configuration";
	protected static final String m_LibraryMultiWireCore = "Library MultiWireCore";
	protected static final String m_LibraryOther = "Library Other";
	protected static final String m_LibraryRevisionGroup = "Library Revision Group";
	protected static final String m_LibraryScope = "Library Scope";
	protected static final String m_LibraryScopeCode = "Library Scope Code";
	protected static final String m_LibraryComponentScope = "Library Component Scope";
	protected static final String m_LibrarySingleWireCore = "Library SingleWireCore";
	protected static final String m_LibrarySolderSleeve = "Library Solder Sleeve";
	protected static final String m_LibrarySolderSleeveSelection = "Library Solder Sleeve Selection";
	protected static final String m_LibrarySplice = "Library Splice";
	protected static final String m_LibrarySpliceSelection = "Library Splice Selection";
	protected static final String m_LibrarySupplierOrganisation = "Library Supplier Organisation";
	protected static final String m_LibrarySupplierPartNumber = "Library Supplier PartNumber";
	protected static final String m_LibrarySymbol = "Library Symbol";
	protected static final String m_LibraryTape = "Library Tape";
	protected static final String m_LibraryTapeSelection = "Library Tape Selection";
	protected static final String m_LibraryTerminal = "Library Terminal";
	protected static final String m_LibraryTermination = "Library Termination";
	protected static final String m_LibraryTube = "Library Tube";
	protected static final String m_LibraryUltrasonicWeld = "Library Ultrasonic Weld";
	protected static final String m_LibraryUltrasonicWeldSelection = "Library Ultrasonic Weld Selection";
	protected static final String m_LibraryUserProperty = "Library UserProperty";
	protected static final String m_LibraryWire = "Library Wire";
	protected static final String m_LibraryWireFitsCavity = "Library WireFitsCavity";
	protected static final String m_LibraryWireGroup = "Library Wire Group";
	protected static final String m_LibraryWireInsulationThickness = "Library WireInsulation Thickness";
	protected static final String m_LibraryWirePitch = "Library Wire Pitch";
	protected static final String m_LibraryWireSpec = "Library Wire Spec";
	protected static final String m_LibraryFixture = "Library Fixture";
	protected static final String m_LibraryFixtureSelection = "Library Fixture Selection";
	protected static final String m_LibraryRingTerminal = "Library Ring Terminal Group";
	protected static final String m_LibraryRingTerminalDetails = "Library Ring Terminal Group Details";
	protected static final String m_Symbol = "Symbol";
	//name for the XDiagramObjects.
	protected static final String m_LogicDiagramPin = "Logic Diagram Pin";
	protected static final String m_LogicDiagramPinList = "Logic Diagram PinList";
	protected static final String m_LogicDiagramConductor = "Logic Diagram Conductor";
	protected static final String m_LogicDiagramMultiCore = "Logic Diagram MultiCore";
	protected static final String m_HarnessDiagramAxialDimension = "Harness Diagram AxialDimension";
	protected static final String m_HarnessDiagramBundle = "Harness Diagram Bundle";
	protected static final String m_HarnessDiagramFixture = "Harness Diagram Fixture";
	//	protected static final String m_DrillPoint = "Drill Point";
	protected static final String m_HarnessDiagramNodeDimension = "Harness Diagram NodeDimension";
	protected static final String m_LogicDiagramInternalLink = "Internal Link";
	protected static final String m_LogicDiagramInternalPin = "Internal Pin";
	protected static final String DIMENSION_VALUE_ATTR = "DIMENSIONVALUE";
	protected static final String m_baseIdKey = "BASE_ID";

	//Engineering Change Orders
	protected static final String m_EngineeringChangeOrder = "EngineeringChangeOrder";

	protected Writer m_writer;

	protected XObjectComparator m_comparator = new XObjectComparator();

	// When this class is used for Unit Tests there are quite a few bugs exposed, e.g - If a Wire connects
	// to two pins of the same name with the same parent object type this class only writes out one pin
	// we really ought to make sure this class always works properly
	private boolean m_aggressiveSorting = false;

	protected XObjectComparator m_fullSortComparator = new XObjectComparator("Name", true, true);
	// This comparator will also be used whem m_aggressiveSorting is set to true
	protected Comparator<IXWireEnd> m_wireEndComparator = new Comparator<IXWireEnd>()
	{
		public int compare(IXWireEnd o1, IXWireEnd o2)
		{
			return getComparator().compare(o1.getPin(), o2.getPin());
		}
	};
	protected XDimensionObjectComparator m_dimensionComparator = new XDimensionObjectComparator();

	protected int m_lastColumnIndex;
	private boolean m_isWriteBaseIds = false;

	public static class ExtensibilityAttributeSkipHelper
	{

		private static ExtensibilityAttributeSkipHelper m_instance =
				new ExtensibilityAttributeSkipHelper();

		private Map<Class<? extends IXObject>, Set<String>> m_ignoredAttributes =
				new HashMap<Class<? extends IXObject>, Set<String>>();

		public static ExtensibilityAttributeSkipHelper getInstance()
		{
			return m_instance;
		}

		protected ExtensibilityAttributeSkipHelper()
		{
			//unnecessary to update hundreds of refs in harness tests. ignore these two attrs.
			Set<String> attributesToIgnore = new HashSet<String>();
			//attributesToIgnore.add(getStorageValue(IXAttributes.ModularParent));
			//attributesToIgnore.add(getStorageValue(IXAttributes.ModularChild));
			m_ignoredAttributes.put(IXConnector.class, attributesToIgnore);

			// Node & Bundle need to ignore NumberOfWires and NumberOfMulticores
			attributesToIgnore = new HashSet<String>();
			attributesToIgnore.add(getStorageValue(IXAttributes.NumberOfWires));
			attributesToIgnore.add(getStorageValue(IXAttributes.NumberOfMulticores));
			m_ignoredAttributes.put(IXNode.class, attributesToIgnore);

			attributesToIgnore = new HashSet<String>();
			attributesToIgnore.add(getStorageValue(IXAttributes.NumberOfWires));
			attributesToIgnore.add(getStorageValue(IXAttributes.NumberOfMulticores));
			m_ignoredAttributes.put(IXBundle.class, attributesToIgnore);

			attributesToIgnore = new HashSet<String>();
			attributesToIgnore.add(getStorageValue(IXAttributes.BOMID));
			attributesToIgnore.add(getStorageValue(IXAttributes.IncludeOnBOM));
			attributesToIgnore.add(getStorageValue(IXAttributes.GeneratedFunctionalModuleCodesExpression));
			attributesToIgnore.add(getStorageValue(IXAttributes.GeneratedProductionModuleCodesExpression));
			attributesToIgnore.add(getStorageValue(IXAttributes.UserProductionModuleCodesExpression));
			attributesToIgnore.add(getStorageValue(IXAttributes.UserFunctionalModuleCodesExpression));
			m_ignoredAttributes.put(IXDevice.class, attributesToIgnore);
		}

		private String getStorageValue(String val)
		{
			return val.toUpperCase();
		}

		public boolean isAttributeSkipped(IXObject object, IXValue attr)
		{
			String lookupVal = getStorageValue(attr.getName());
			for (Map.Entry<Class<? extends IXObject>, Set<String>> entry : m_ignoredAttributes.entrySet()) {
				if (entry.getKey().isAssignableFrom(object.getClass()) && entry.getValue().contains(lookupVal)) {
					return true;
				}
			}
			return false;
		}
	}

	public ExtensibilityNetlistWriter(Writer writer)
	{
		m_writer = writer;
		m_lastColumnIndex = 0;
	}

	public ExtensibilityNetlistWriter(Writer writer, boolean isWriteBaseIds)
	{
		m_writer = writer;
		m_lastColumnIndex = 0;
		m_isWriteBaseIds = isWriteBaseIds;
	}

	/**
	 * If called with true this does additional object sorting such that results a consistent dependent on type and name
	 * and not on arbritary order of objects in datamodel collections.
	 * <p/>
	 *
	 * @param aggressiveSorting If true sort aggresively
	 */
	public void setAggressiveSorting(boolean aggressiveSorting)
	{
		m_aggressiveSorting = aggressiveSorting;
	}

	protected void writeHarness(IXHarness harness) throws IOException
	{
		String indexStr = getIndexString();
		m_writer.write(indexStr + m_Harness + '\n');
		m_lastColumnIndex++;
		//Write the attributes and properties of the harness
		writeAttributesAndProperties(harness);

		m_writer.write(indexStr + "Harness Levels in the harness:" + '\n' + '\n');
		writeHarnessLevels(harness.getHarnessLevels());

		m_writer.write(indexStr + "Active Harness Levels in the harness:" + '\n' + '\n');
		logName(harness.getActiveHarnessLevels());

		m_writer.write("\n" + indexStr + "Bundles in the harness:" + '\n' + '\n');
		writeBundles(harness.getBundles());

		m_writer.write("\n" + indexStr + "Connectors in the harness:" + '\n' + '\n');
		logName(harness.getConnectors());

		//Added an empty check just to avoid the updates to golden files for several unit tests
		if (!harness.getRingTerminals().isEmpty()) {
			m_writer.write("\n" + indexStr + "Ring Terminals in the harness:" + '\n' + '\n');
			logName(harness.getRingTerminals());
		}

		m_writer.write("\n" + indexStr + "Splices in the harness:" + '\n' + '\n');
		logName(harness.getSplices());

		m_writer.write("\n" + indexStr + "Multicores in the harness:" + '\n' + '\n');
		logName(harness.getMulticores());

		m_writer.write("\n" + indexStr + "Wires in the harness:" + '\n' + '\n');
		logName(harness.getWires());

		m_writer.write("\n" + indexStr + "Harness Register in the harness:" + '\n' + '\n');
		IXHarnessRegister register = harness.getHarnessRegister();
		if (register != null) {
			writeHarnessRegister(register);
		}

		m_writer.write("\n" + indexStr + "Options in the harness:" + '\n' + '\n');
		logName(harness.getApplicableOptions());

		m_writer.write("\n" + indexStr + "Insulation runs in the harness:" + '\n' + '\n');
		writeInsulationRuns(harness.getInsulationRuns());

		m_writer.write("\n" + indexStr + "Clips in the harness:" + '\n' + '\n');
		writeClips(harness.getClips());

		m_writer.write("\n" + indexStr + "Grommets in the harness:" + '\n' + '\n');
		writeGrommets(harness.getGrommets());

		m_writer.write("\n" + indexStr + "Other components in the harness:" + '\n' + '\n');
		writeOtherComponents(harness.getOtherComponents());

		m_writer.write("\n" + indexStr + "Spot tapes in the harness:" + '\n' + '\n');
		writeSpotTapes(harness.getSpotTapes());

		m_writer.write("\n" + indexStr + "Breakout tapes in the harness:" + '\n' + '\n');
		writeBreakoutTapes(harness.getBreakoutTapes());

		m_writer.write("\n" + indexStr + "Multilocation components in the harness:" + '\n' + '\n');
		writeMLCs(harness.getMultiLocationComponents());

		m_writer.write("\n" + indexStr + "Nodes in the harness:" + '\n' + '\n');
		writeNodes(harness.getNodes());

		m_writer.write("\n" + indexStr + "Connectivity Objects in the harness:" + '\n' + '\n');
		writeConnectivityObjectNames(harness.getConnectivity());
		m_lastColumnIndex--;
	}

	protected void writeConnectivity(IXConnectivity conn) throws IOException
	{
		if (conn == null) {
			return;
		}
		String indexStr = getIndexString();

		m_lastColumnIndex++;
		m_writer.write("\n" + indexStr + "Connectors:" + '\n');
		writeConnectors(conn.getConnectors());

		m_writer.write("\n" + indexStr + "Splices:" + '\n');
		writeSplices(conn.getSplices());

		//Added this empty check just to avoid golden updates for large number of tests
		if (!conn.getRingTerminals().isEmpty()) {
			m_writer.write("\n" + indexStr + "Ring Terminals:" + '\n');
			writeRingTerminals(conn.getRingTerminals());
		}

		m_writer.write("\n" + indexStr + "Multicores:" + '\n');
		writeMulticores(conn.getMulticores());

		m_writer.write("\n" + indexStr + "Wires:" + '\n');
		writeConductors(conn.getWires());

		m_writer.write("\n" + indexStr + "Interconnects:" + '\n');
		writeConductors(conn.getInterconnects());

		m_writer.write("\n" + indexStr + "Interconnect Devices:" + '\n');
		writeInterconnectDevices(conn.getInterconnectDevices());

		m_writer.write("\n" + indexStr + "Nets:" + '\n');
		writeConductors(conn.getNets());

		m_writer.write("\n" + indexStr + "Assemblies:" + '\n');
		writeAssemblies(conn.getAssemblies());

		Set<? extends IXBlock> blocks = conn.getBlocks();
		if (!blocks.isEmpty()) {
			//there are more than 500 references to be updated
			//if we dump empty set of blocks. so will be dumping
			// them only for non-empty block sets.
			m_writer.write("\n" + indexStr + "Blocks" + '\n');
			writeBlocks(blocks);
		}

		m_writer.write("\n" + indexStr + "Devices" + '\n');
		writeDevices(conn.getDevices());

		m_writer.write("\n" + indexStr + "Grounds:" + '\n');
		writeDevices(conn.getGrounds());

		m_writer.write("\n" + indexStr + "Shields:" + '\n');
		writeConductors(conn.getShields());
		m_lastColumnIndex--;
	}

	public void writeConnectivityObjectNames(IXConnectivity conn) throws IOException
	{
		if (conn == null) {
			return;
		}
		String indexStr = getIndexString();
		m_lastColumnIndex++;

		m_writer.write("\n" + indexStr + "Connectors:" + '\n');
		logName(conn.getConnectors());

		m_writer.write("\n" + indexStr + "Splices:" + '\n');
		logName(conn.getSplices());

		m_writer.write("\n" + indexStr + "Multicores:" + '\n');
		logName(conn.getMulticores());

		m_writer.write("\n" + indexStr + "Wires:" + '\n');
		logName(conn.getWires());

		m_writer.write("\n" + indexStr + "Interconnects:" + '\n');
		logName(conn.getInterconnects());

		m_writer.write("\n" + indexStr + "Interconnect Devices:" + '\n');
		logName(conn.getInterconnectDevices());

		m_writer.write("\n" + indexStr + "Nets:" + '\n');
		logName(conn.getNets());

		m_writer.write("\n" + indexStr + "Assemblies:" + '\n');
		logName(conn.getAssemblies());

		m_writer.write("\n" + indexStr + "Grounds:" + '\n');
		logName(conn.getGrounds());

		m_writer.write("\n" + indexStr + "Shields:" + '\n');
		logName(conn.getShields());
		m_lastColumnIndex--;
	}

	protected void writeMLCs(Set<IXMultiLocationComponent> mlcs) throws IOException
	{
		Set<IXMultiLocationComponent> sortedMLCs = new TreeSet<IXMultiLocationComponent>(getComparator());
		sortedMLCs.addAll(mlcs);

		for (IXMultiLocationComponent mlc : sortedMLCs) {
			writeMLC(mlc);
		}
	}

	protected void writeMLC(IXMultiLocationComponent mlc) throws IOException
	{
		logObjectType(mlc);
		m_lastColumnIndex++;
		//Write all the attributes and properties of the MLC
		writeAttributesAndProperties(mlc);

		//Get all the locations of MLC and write it
		Set<IXObject> oldLocs = mlc.getLocations();
		Set<IXObject> locs = new LinkedHashSet<IXObject>(oldLocs.size());
		//get the wire end pin/pinlist for WMLC for comparision as WireEnd doesn't have name etc. for uniqueness comparison - DR 908048
		for (IXObject loc : oldLocs) {
			if (loc instanceof IXWireEnd) {    //wire MLC?
				locs.add(((IXWireEnd) loc).getConductor());
				IXAbstractPin pin = ((IXWireEnd) loc).getPin();
				if (pin != null) {
					locs.add(pin.getOwner());
					locs.add(pin);
				}
			}
			else {
				locs.add(loc);
			}
		}
		logName(locs);
		logName(mlc.getHarness());
		writePositionContatiner(mlc);
		m_lastColumnIndex--;
	}

	protected void writeBreakoutTapes(Set<IXBreakoutTape> breakoutTapes) throws IOException
	{

		//Create a sorted set before proceeding further.
		Set<IXBreakoutTape> sortedBreakoutTapes = new TreeSet<IXBreakoutTape>(getComparator());
		sortedBreakoutTapes.addAll(breakoutTapes);

		for (IXBreakoutTape breakoutTape : sortedBreakoutTapes) {
			writeBreakoutTape(breakoutTape);
		}
	}

	protected void writeBreakoutTape(IXBreakoutTape breakoutTape) throws IOException
	{
		logObjectType(breakoutTape);
		m_lastColumnIndex++;

		//Write all the attributes and properties
		writeAttributesAndProperties(breakoutTape);

		//Get the node on which this breakout tape is applied
		logName(breakoutTape.getNode());
		logName(breakoutTape.getHarness());
		m_lastColumnIndex--;
	}

	protected void writeSpotTapes(Set<IXSpotTape> spotTapes) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXSpotTape> sortedSpotTapes = new TreeSet<IXSpotTape>(getComparator());
		sortedSpotTapes.addAll(spotTapes);

		for (IXSpotTape spotTape : sortedSpotTapes) {
			writeSpotTape(spotTape);
		}
	}

	protected void writeSpotTape(IXSpotTape spotTape) throws IOException
	{
		logObjectType(spotTape);
		m_lastColumnIndex++;

		//Write all the attributes and properties of spot tape
		writeAttributesAndProperties(spotTape);

		//Get the node on which this spot tape is applied
		logName(spotTape.getNode());
		logName(spotTape.getHarness());
		m_lastColumnIndex--;
	}

	protected void writeOtherComponents(Set<IXOtherComponent> otherComponents) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXOtherComponent> sortedOtherComponents = new TreeSet<IXOtherComponent>(getComparator());
		sortedOtherComponents.addAll(otherComponents);

		for (IXOtherComponent otherComponent : sortedOtherComponents) {
			writeNodeComponent(otherComponent);
		}
	}

	protected void writeNodeComponent(IXNodeComponent nodeComponent) throws IOException
	{

		logObjectType(nodeComponent);

		m_lastColumnIndex++;

		//Write the attributes and properties of other component
		writeAttributesAndProperties(nodeComponent);

		//Write the node on which this other component is placed
		logName(nodeComponent.getNode());
		logName(nodeComponent.getHarness());
		writePositionContatiner(nodeComponent);
		m_lastColumnIndex--;
	}

	protected void writeGrommets(Set<IXGrommet> grommets) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXGrommet> sortedGrommets = new TreeSet<IXGrommet>(getComparator());
		sortedGrommets.addAll(grommets);

		for (IXGrommet grommet : sortedGrommets) {
			writeNodeComponent(grommet);
		}
	}

	protected void writeClips(Set<IXClip> clips) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXClip> sortedClips = new TreeSet<IXClip>(getComparator());
		sortedClips.addAll(clips);

		for (IXClip clip : sortedClips) {
			writeNodeComponent(clip);
		}
	}

	protected void writeOptions(Set<IXOption> options) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXOption> sortedOpts = new TreeSet<IXOption>(getComparator());
		sortedOpts.addAll(options);
		Set<IXOptionFolder> optFolders = new TreeSet<IXOptionFolder>(getComparator());
		for (IXOption opt : sortedOpts) {
			writeOption(opt);
			IXOptionFolder optionFolder = opt.getFolder();
			if (optionFolder != null) {
				optFolders.add(optionFolder);
			}
		}
		writeOptionFolders(optFolders);
	}

	protected void writeOptionFolders(Set<IXOptionFolder> optFolders) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXOptionFolder> sortedOptFolders = new TreeSet<IXOptionFolder>(getComparator());
		sortedOptFolders.addAll(optFolders);

		for (IXOptionFolder optFolder : sortedOptFolders) {
			writeOptionFolder(optFolder);
			IXOptionFolder parent = optFolder.getParent();
			if (parent != null) {
				writeOptionFolder(parent);
			}
		}
	}

	protected void writeConfigurations(Set<IXConfiguration> configs) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXConfiguration> sortedConfigs = new TreeSet<IXConfiguration>(getComparator());
		sortedConfigs.addAll(configs);

		for (IXConfiguration config : sortedConfigs) {
			writeConfiguration(config);
		}
	}

	protected void writeObjectTypeInfos(Set<IXObjectTypeInfo> otInfos) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXObjectTypeInfo> sortedInfos = new TreeSet<IXObjectTypeInfo>(getComparator());
		sortedInfos.addAll(otInfos);

		for (IXObjectTypeInfo info : sortedInfos) {
			writeObjectTypeInfo(info);
		}
	}

	protected void writeNameInfos(Set<IXNameInfo> infos) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXNameInfo> sortedInfos = new TreeSet<IXNameInfo>(getComparator());
		sortedInfos.addAll(infos);

		for (IXNameInfo info : sortedInfos) {
			writeNameInfo(info);
		}
	}

	protected void writePropertyInfos(Set<IXPropertyInfo> infos) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXPropertyInfo> sortedInfos = new TreeSet<IXPropertyInfo>(getComparator());
		sortedInfos.addAll(infos);

		for (IXPropertyInfo info : sortedInfos) {
			writePropertyInfo(info);
		}
	}

	protected void writePropertyInfoRanges(Set<IXPropertyInfoRange> ranges) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXPropertyInfoRange> sortedRanges = new TreeSet<IXPropertyInfoRange>(getComparator());
		sortedRanges.addAll(ranges);

		for (IXPropertyInfoRange range : sortedRanges) {
			writePropertyInfoRange(range);
		}
	}

	protected void writeDiagrams(Set<IXDiagram> dgms) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXDiagram> sortedDgms = new TreeSet<IXDiagram>(getComparator());
		sortedDgms.addAll(dgms);

		for (IXDiagram dgm : sortedDgms) {
			writeDiagram(dgm);
		}
	}

	protected void writeAssemblies(Set<IXAssembly> assemblies) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXAssembly> sortedassemblies = new TreeSet<IXAssembly>(getComparator());
		sortedassemblies.addAll(assemblies);
		//	m_lastColumnIndex ++;
		for (IXAssembly assembly : sortedassemblies) {
			writeAssembly(assembly);
			m_writer.write('\n');
		}
		//	m_lastColumnIndex --;
	}

	protected void writeHarnesses(Set<IXHarness> harnesses) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXHarness> sortedHarnesses = new TreeSet<IXHarness>(getComparator());
		sortedHarnesses.addAll(harnesses);

		for (IXHarness harn : sortedHarnesses) {
			writeHarness(harn);
		}
	}

	protected void writeSlots(Set<IXSlot> slots) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXSlot> sortedSlots = new TreeSet<IXSlot>(getComparator());
		sortedSlots.addAll(slots);

		for (IXSlot slot : sortedSlots) {
			writeSlot(slot);
		}
	}

	protected void writeSignals(Set<IXSignal> signals) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXSignal> sortedSignals = new TreeSet<IXSignal>(getComparator());
		sortedSignals.addAll(signals);

		for (IXSignal sign : sortedSignals) {
			writeSignal(sign);
		}
	}

	protected void writeVehicleModels(Set<IXVehicleModel> models) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXVehicleModel> sortedModels = new TreeSet<IXVehicleModel>(getComparator());
		sortedModels.addAll(models);

		for (IXVehicleModel model : sortedModels) {
			writeVehicleModel(model);
		}
	}

	protected void writeVehicleConfigurations(Set<IXVehicleConfiguration> configs) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXVehicleConfiguration> sortedConfigs = new TreeSet<IXVehicleConfiguration>(getComparator());
		sortedConfigs.addAll(configs);

		for (IXVehicleConfiguration config : sortedConfigs) {
			writeVehicleConfiguration(config);
		}
	}

	protected void writeBuildLists(Set<IXBuildList> blds) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXBuildList> sortedBlds = new TreeSet<IXBuildList>(getComparator());
		sortedBlds.addAll(blds);

		for (IXBuildList bld : sortedBlds) {
			writeBuildList(bld);
		}
	}

	protected void writeInsulationRuns(Set<IXInsulationRun> insulationRuns) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXInsulationRun> sortedInsulationRuns = new TreeSet<IXInsulationRun>(getComparator());
		sortedInsulationRuns.addAll(insulationRuns);

		for (IXInsulationRun insulationRun : sortedInsulationRuns) {
			writeInsulationRun(insulationRun);
		}
	}

	protected void writeInsulationRun(IXInsulationRun insulationRun) throws IOException
	{

		logObjectType(insulationRun);
		m_lastColumnIndex++;
		//Write all the attributes and properties of insulation run.
		writeAttributesAndProperties(insulationRun);

		//Get and write all nodes through which this insulation run passes
		logName(insulationRun.getNodes());
		logName(insulationRun.getHarness());

		//Get all the insulations in this insulation run
		Set<IXInsulation> insulations = insulationRun.getInsulations();

		if (insulations != null && !insulations.isEmpty()) {
			writeInsulations(insulations);
		}
		m_lastColumnIndex--;
	}

	protected void writeInsulations(Set<IXInsulation> insulations) throws IOException
	{

		//Create a sorted set before proceeding further.
		Set<IXInsulation> sortedInsulations = new TreeSet<IXInsulation>(getComparator());
		sortedInsulations.addAll(insulations);

		for (IXInsulation insulation : sortedInsulations) {
			writeInsulation(insulation);
		}
	}

	protected void writeInsulation(IXInsulation insulation) throws IOException
	{

		logObjectType(insulation);
		m_lastColumnIndex++;

		//Write all the attributes and properties of this insulation
		writeAttributesAndProperties(insulation);
		logName(insulation.getHarness());
		m_lastColumnIndex--;
	}

	protected void writeHarnessRegister(IXHarnessRegister register) throws IOException
	{
		logName(register);
		//Write all the attributes and properties of harness register
		writeAttributesAndProperties(register);
		// write customer date and internal date
		String indexStr = getIndexString();
		String date = "";
		String fmtString = "dd/MM/yyyy";
		DateFormat format = new SimpleDateFormat(fmtString);
		if (register.getCustomerDate() != null) {
			date = format.format(register.getCustomerDate());
		}
		date = convertDate(date);
		m_writer.write(indexStr + m_attributeKey + m_customerDate + '=' + date + '\n');
		if (register.getInternalDate() != null) {
			date = format.format(register.getInternalDate());
		}
		date = convertDate(date);
		m_writer.write(indexStr + m_attributeKey + m_internalDate + '=' + date + '\n');
	}

	protected void writeConductors(Set<? extends IXAbstractConductor> conds) throws IOException
	{
		if (conds.isEmpty()) {
			return;
		}

		//Create a sorted set before proceeding further.
		Set<IXAbstractConductor> sortedConds = new TreeSet<IXAbstractConductor>(getComparator());
		sortedConds.addAll(conds);
		//m_lastColumnIndex ++;
		for (IXAbstractConductor cond : sortedConds) {
			writeConductor(cond);
			m_writer.write('\n');
		}
		//m_lastColumnIndex --;
	}

	protected void writeWireEnds(Set<IXWireEnd> wireEnds) throws IOException
	{
		Set<IXWireEnd> sortedWireEnds =
				new TreeSet<IXWireEnd>(m_aggressiveSorting ? m_wireEndComparator : getComparator());
		sortedWireEnds.addAll(wireEnds);

		for (IXWireEnd wireEnd : sortedWireEnds) {
			writeWireEnd(wireEnd);
		}
	}

	protected void writeMarkers(Set<IXMarker> markers) throws IOException
	{
		Set<IXMarker> sortedMarkers = new TreeSet<IXMarker>(new XObjectComparator("Position"));
		sortedMarkers.addAll(markers);

		for (IXMarker wireMarker : sortedMarkers) {
			writeMarker(wireMarker);
		}
	}

	protected void writeMarker(IXMarker marker) throws IOException
	{

		logObjectType(marker);
		m_lastColumnIndex++;

		//Write the attributes and properties of wire marker
		writeAttributesAndProperties(marker);
		m_lastColumnIndex--;
	}

	protected void writeWireEnd(IXWireEnd wireEnd) throws IOException
	{

		logObjectType(wireEnd);
		m_lastColumnIndex++;

		//Write the attributes and properties of wire end
		writeAttributesAndProperties(wireEnd);

		//Get and write the information of the pin on which wire end terminates
		logName(wireEnd.getPin());

		//Get and write the conductor associated with this wire end
		logName(wireEnd.getConductor());

		//Get all the additional components and write it
		Set<IXAdditionalComponent> additionalComponents = wireEnd.getAdditionalComponents();
		if (additionalComponents != null && !additionalComponents.isEmpty()) {
			writeAdditionalComponents(additionalComponents);
		}
		m_lastColumnIndex--;
	}

	protected void writeAdditionalComponents(Set<IXAdditionalComponent> additionalComponents) throws IOException
	{
		for (IXAdditionalComponent component : additionalComponents) {
			writeAdditionalComponent(component);
		}
	}

	protected void writeAdditionalComponent(IXAdditionalComponent component) throws IOException
	{

		logObjectType(component);
		m_lastColumnIndex++;
		//Write the attributes and properties of the additional component
		writeAttributesAndProperties(component);
		logName(component.getOwner());
		writePositionContatiner(component);
		m_lastColumnIndex--;
	}

	protected void writePins(Set<? extends IXAbstractPin> pins) throws IOException
	{
		if (pins != null && !pins.isEmpty()) {
			Set<IXAbstractPin> sortedPins = new TreeSet<IXAbstractPin>(getComparator());
			sortedPins.addAll(pins);
			m_lastColumnIndex++;
			for (IXAbstractPin pin1 : sortedPins) {
				writePin(pin1);
			}
			m_lastColumnIndex--;
		}
	}

	protected void writePin(IXAbstractPin pin) throws IOException
	{

		logObjectType(pin);
		//Write the attributes and properties of this pin.
		m_lastColumnIndex++;
		writeAttributesAndProperties(pin);
		logName(pin.getConductors());
		logName(pin.getSignals());
		logName(pin.getOwner());
		logName(pin.getInternalLinks());

		String indexStr = getIndexString();
		//This should be an instance of IXCavity
		if (pin instanceof IXCavity) {
			//Get and write the additional components part of this cavity
			Set<IXAdditionalComponent> additionalComponents = ((IXCavity) pin).getAdditionalComponents();
			if (additionalComponents != null && !additionalComponents.isEmpty()) {
				writeAdditionalComponents(additionalComponents);
			}
			//Get and write the cavitydetails for this cavity
			Set<IXCavityDetail> cavityDetails = ((IXCavity) pin).getCavityDetails();
			if (cavityDetails != null && !cavityDetails.isEmpty()) {
				writeCavityDetails(cavityDetails);
			}
			Set<IXAbstractPin> funcPins = ((IXCavity) pin).getFunctionalPins();
			if (funcPins != null && !funcPins.isEmpty()) {
				m_writer.write(indexStr + "Functional Pins" + '\n');
				writePins(funcPins);
			}
		}
		else if (pin instanceof IXDevicePin) {
			logName(((IXDevicePin) pin).getCavity());
			Set<IXAbstractPin> funcPins = ((IXDevicePin) pin).getFunctionalPins();
			if (funcPins != null && !funcPins.isEmpty()) {
				m_writer.write(indexStr + "Functional Pins" + '\n');
				writePins(funcPins);
			}

			Set<IXDevicePin> connDevPins = ((IXDevicePin) pin).getConnectedDevicePins();
			if (connDevPins != null && !connDevPins.isEmpty()) {
				m_writer.write(indexStr + "Connected Device Pins" + '\n');
				writeConnectedPins(connDevPins);
			}
		}
		m_lastColumnIndex--;
	}

	protected void writeConnectedPins(Set<IXDevicePin> pins) throws IOException
	{
		m_lastColumnIndex++;
		logName(pins);
		m_lastColumnIndex--;
	}

	protected void writeCavityDetails(Set<IXCavityDetail> cavityDetails) throws IOException
	{
		Set<IXCavityDetail> sortedCavityDetails = new TreeSet<IXCavityDetail>(getComparator());
		sortedCavityDetails.addAll(cavityDetails);

		for (IXCavityDetail cavityDetail : sortedCavityDetails) {
			writeCavityDetail(cavityDetail);
		}
	}

	protected void writeCavityDetail(IXCavityDetail cavityDetail) throws IOException
	{

		logObjectType(cavityDetail);
		m_lastColumnIndex++;
		//Write the attributes and properties of this cavity detail
		writeAttributesAndProperties(cavityDetail);

		logName(cavityDetail.getOwner());
		IXTerminal terminal = cavityDetail.getTerminal();
		if (terminal != null) {
			writeTerminal(terminal);
		}
		IXCavitySeal seal = cavityDetail.getCavitySeal();
		if (seal != null) {
			writeSeal(seal);
		}
		m_lastColumnIndex--;
	}

	protected void writeTerminal(IXTerminal terminal) throws IOException
	{

		logObjectType(terminal);
		m_lastColumnIndex++;
		//Write the attributes and properties of this Terminal
		writeAttributesAndProperties(terminal);
		m_lastColumnIndex--;
	}

	protected void writeSeal(IXCavitySeal seal) throws IOException
	{

		logObjectType(seal);
		m_lastColumnIndex++;
		//Write the attributes and properties of this Seal
		writeAttributesAndProperties(seal);
		m_lastColumnIndex--;
	}

	protected void writeConductor(IXAbstractConductor conductor) throws IOException
	{

		logObjectType(conductor);
		m_lastColumnIndex++;
		//Write the attributes and properties of this conductor
		writeAttributesAndProperties(conductor);

		//Get the multicore to which this conductor belongs and write that
		logName(conductor.getMulticore());
		logNameNoSort(conductor.getAbstractPins());

		if (conductor instanceof IXWire) {
			IXWire wire = (IXWire) conductor;
			writeSplices(wire.getCenterstripSplices());
			//Get and write the wire ends
			writeWireEnds(wire.getWireEnds());
			writeMarkers(wire.getWireMarkers());
			logName(wire.getHarnessLevels());
			logName(wire.getSignal());
			logName(wire.getHarness());

			String indexStr = getIndexString();
			m_writer.write(indexStr + "Wire Path" + '\n');
			m_lastColumnIndex++;
			logName(wire.getNodes());
			m_lastColumnIndex--;
			indexStr = getIndexString();
			m_writer.write(indexStr + "Wire Through Nodes" + '\n');
			m_lastColumnIndex++;
			logName(wire.getThroughNodes());
			m_lastColumnIndex--;
			indexStr = getIndexString();
			m_writer.write(indexStr + "Wire Not Through Nodes" + '\n');
			m_lastColumnIndex++;
			logName(wire.getNotThroughNodes());
			m_lastColumnIndex--;
		}
		else if (conductor instanceof IXShield) {
			logName(((IXShield) conductor).getHarnessLevels());
			logName(((IXShield) conductor).getSignal());
			logName(((IXShield) conductor).getHarness());
			writeVariances(((IXShield) conductor).attributeVariances());
			writeVariances(((IXShield) conductor).propertyVariances());
		}
		else if (conductor instanceof IXNet) {
			writeVariances(((IXNet) conductor).attributeVariances());
			writeVariances(((IXNet) conductor).propertyVariances());
		}
		m_lastColumnIndex--;
	}

	protected void writeMulticore(IXMulticore multicore) throws IOException
	{

		logObjectType(multicore);
		m_lastColumnIndex++;
		//Write the attributes and prooperties of the multicore
		writeAttributesAndProperties(multicore);
		logName(multicore.getConductors());
		logName(multicore.getShield());
		logName(multicore.getHarnessLevels());

		Set<IXMulticore> innerCores = multicore.getMulticores();
		Set<IXMulticore> sortedMulticores = new TreeSet<IXMulticore>(getComparator());
		sortedMulticores.addAll(innerCores);
		for (IXMulticore core : sortedMulticores) {
			writeMulticore(core);
		}
		// terminations for overbraid
		if (multicore.isOverbraid()) {
			logName(((IXOverbraid) multicore).getTerminations());
		}
		logName(multicore.getHarness());
		m_lastColumnIndex--;
	}

	protected void writeMulticores(Set<IXMulticore> multicores) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXMulticore> sortedMulticores = new TreeSet<IXMulticore>(getComparator());
		sortedMulticores.addAll(multicores);
		for (IXMulticore mc : sortedMulticores) {
			writeMulticore(mc);
			m_writer.write('\n');
		}
	}

	protected void writeSplices(Set<IXSplice> splices) throws IOException
	{
		Set<IXSplice> sortedsplices = new TreeSet<IXSplice>(getComparator());
		sortedsplices.addAll(splices);
		for (IXSplice splice : sortedsplices) {
			writeSplice(splice);
			m_writer.write('\n');
		}
	}

	protected void writeSplice(IXSplice splice) throws IOException
	{
		writePinList(splice);
		m_lastColumnIndex = m_lastColumnIndex + 1;
		Set<IXWire> wires = splice.getCenterstripWires();
		logName(wires);
		logName(splice.getNode());
		logName(splice.getSignal());
		logName(splice.getSlot());
		logName(splice.getHarnessLevels());
		logName(splice.getHarness());
		writePositionContatiner(splice);

		String indexStr = getIndexString();
		//Capture whether the connector is inline or not
		boolean isCenterStripped = splice.isCenterstrip();
		m_writer.write(indexStr + "Centerstrip " + isCenterStripped + '\n');
		if (isCenterStripped && wires.size() > 1) {
			LogInfo("Num Center Strip Wires: " + wires.size());
		}
		m_lastColumnIndex = m_lastColumnIndex - 1;
	}

	protected void writeConnectors(Set<IXConnector> connectors) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXConnector> sortedConnectors = new TreeSet<IXConnector>(getComparator());
		sortedConnectors.addAll(connectors);
		//m_lastColumnIndex ++;
		for (IXConnector connector : sortedConnectors) {
			writeConnector(connector);
			m_writer.write('\n');
		}
		//m_lastColumnIndex --;
	}

	protected void writeRingTerminals(Set<? extends IXRingTerminal> ringterminals) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXRingTerminal> sortedRingTerminals = new TreeSet<IXRingTerminal>(getComparator());
		sortedRingTerminals.addAll(ringterminals);
		//m_lastColumnIndex ++;
		for (IXRingTerminal ringterminal : sortedRingTerminals) {
			writeRingTerminal(ringterminal);
			m_writer.write('\n');
		}
		//m_lastColumnIndex --;
	}

	protected void writeBundles(Set<IXBundle> bundles) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXBundle> sortedBundles = new TreeSet<IXBundle>(getComparator());
		sortedBundles.addAll(bundles);

		for (IXBundle bundle : sortedBundles) {
			writeBundle(bundle);
			m_writer.write('\n');
		}
	}

	protected void writeBundle(IXBundle bundle) throws IOException
	{
		logObjectType(bundle);
		m_lastColumnIndex++;
		//Write all the attribute and properties of the bundle
		writeAttributesAndProperties(bundle);

		//Get the nodes of this bundle and write to a file.
		logName(bundle.getNodes());

		//Get the bundle regions in the bundle and write that
		Set<IXBundleRegion> bundleRegions = bundle.getBundleRegions();

		if (bundleRegions != null && !bundleRegions.isEmpty()) {
			Set<IXBundleRegion> sortedBundleRegions = new TreeSet<IXBundleRegion>(getComparator());
			sortedBundleRegions.addAll(bundleRegions);
			for (IXBundleRegion bundleRegion : sortedBundleRegions) {
				writeBundleRegion(bundleRegion);
			}
		}

		//Get all the connectors in the bundle and write that.
		logName(bundle.getConnectors());
		logName(bundle.getHarness());

		String indexStr = getIndexString();
		double maxComplexityDiameter = bundle.getMaxComplexityDiameter();
		m_writer.write(indexStr + m_bundleKey + " Max complexity diameter " + maxComplexityDiameter + " \n");

		//Get the attribute variances and write it
		writeVariances(bundle.attributeVariances());

		m_writer.write(indexStr + "Conductors at middle of the bundle" + '\n');
		logName(bundle.getConductorsAtOffset(Double.parseDouble(bundle.getAttribute("Length")) * 0.5));

		m_lastColumnIndex--;
	}

	protected void logObjectType(IXObject object) throws IOException
	{
		String indexStr = getIndexString();
		m_writer.write(indexStr + getObjectType(object) + '\n');
	}

	public static String getObjectType(IXObject object)
	{
		if (object instanceof IXLogicDiagramPin) {
			return m_LogicDiagramPin;
		}
		else if (object instanceof IXLogicDiagramPinList) {
			return m_LogicDiagramPinList;
		}
		else if (object instanceof IXLogicDiagramConductor) {
			return m_LogicDiagramConductor;
		}
		else if (object instanceof IXLogicDiagramMultiCore) {
			return m_LogicDiagramMultiCore;
		}
		else if (object instanceof IXHarnessDiagramAxialDimension) {
			return m_HarnessDiagramAxialDimension;
		}
		else if (object instanceof IXHarnessDiagramBundle) {
			return m_HarnessDiagramBundle;
		}
		else if (object instanceof IXHarnessDiagramFixture) {
			return m_HarnessDiagramFixture;
		}
//		else if (object instanceof IXDrillPoint) {
//			return m_DrillPoint;
//		}
		else if (object instanceof IXHarnessDiagramNodeDimension) {
			return m_HarnessDiagramNodeDimension;
		}
		else if (object instanceof IXHarness) {
			return m_Harness;
		}
		else if (object instanceof IXHarnessLevel) {
			return m_HarnessLevel;
		}
		else if (object instanceof IXBundle) {
			return m_Bundle;
		}
		else if (object instanceof IXConnector) {
			return m_Connector;
		}
		else if (object instanceof IXReferenceNode) {
			return m_ReferenceNode;
		}
		else if (object instanceof IXStructureNode) {
			return m_StructureNode;
		}
		else if (object instanceof IXSplice) {
			return m_Splice;
		}
		else if (object instanceof IXCavity) {
			return m_Pin;
		}
		else if (object instanceof IXDevicePin) {
			return m_DevicePin;
		}
		else if (object instanceof IXSplicePin) {
			return m_SplicePin;
		}
		else if (object instanceof IXBackshellTermination) {
			return m_BackshellTermination;
		}
		else if (object instanceof IXOverbraid) {
			return m_Overbraid;
		}
		else if (object instanceof IXMulticore) {
			return m_Multicore;
		}
		else if (object instanceof IXWire) {
			return m_Wire;
		}
		else if (object instanceof IXHarnessRegister) {
			return m_HarnessRegister;
		}
		else if (object instanceof IXWireEnd) {
			return m_WireEnd;
		}
		else if (object instanceof IXMarker) {
			return m_Marker;
		}
		else if (object instanceof IXBackshell) {
			return m_Backshell;
		}
		else if (object instanceof IXAdditionalComponent) {
			return m_AdditionalComponent;
		}
		else if (object instanceof IXInsulationRun) {
			return m_InsulationRun;
		}
		else if (object instanceof IXInsulation) {
			return m_Insulation;
		}
		else if (object instanceof IXClip) {
			return m_Clip;
		}
		else if (object instanceof IXGrommet) {
			return m_Grommet;
		}
		else if (object instanceof IXOtherComponent) {
			return m_OtherComponent;
		}
		else if (object instanceof IXSpotTape) {
			return m_SpotTape;
		}
		else if (object instanceof IXBreakoutTape) {
			return m_BreakoutTape;
		}
		else if (object instanceof IXMultiLocationComponent) {
			return m_MLC;
		}
		else if (object instanceof IXBundleRegion) {
			return m_BundleRegion;
		}
		else if (object instanceof IXDeviceConnector) {
			return m_DeviceConnector;
		}
		else if (object instanceof IXCavityDetail) {
			return m_CavityDetail;
		}
		else if (object instanceof IXTerminal) {
			return m_Terminal;
		}
		else if (object instanceof IXCavitySeal) {
			return m_Seal;
		}
		else if (object instanceof IXGround) {
			return m_Ground;
		}
		else if (object instanceof IXDevice) {
			return m_Device;
		}
		else if (object instanceof IXInterconnectDevice) {
			return m_InterconnectDevice;
		}
		else if (object instanceof IXSlot) {
			return m_Slot;
		}
		else if (object instanceof IXHole) {
			return m_Hole;
		}
		else if (object instanceof IXSignal) {
			return m_Signal;
		}
		else if (object instanceof IXShield) {
			return m_Shield;
		}
		else if (object instanceof IXNet) {
			return m_Net;
		}
		else if (object instanceof IXInterconnect) {
			return m_Interconnect;
		}
		else if (object instanceof IXAssembly) {
			return m_Assembly;
		}
		else if (object instanceof IXLogicDesign) {
			return m_LogicDesign;
		}
		else if (object instanceof IXIntegratorDesign) {
			return m_IntegratorDesign;
		}
		else if (object instanceof IXTopologyDesign) {
			return m_TopologyDesign;
		}
		else if (object instanceof IXHarnessDesign) {
			return m_HarnessDesign;
		}
		else if (object instanceof IXProject) {
			return m_Project;
		}
		else if (object instanceof IXBuildList) {
			return m_BuildList;
		}
		else if (object instanceof IXDiagram) {
			return m_Diagram;
		}
		else if (object instanceof IXOption) {
			return m_Option;
		}
		else if (object instanceof IXVehicleModel) {
			return m_VehicleModel;
		}
		else if (object instanceof IXVehicleConfiguration) {
			return m_VehicleConfiguration;
		}
		else if (object instanceof IXConfiguration) {
			return m_Configuration;
		}
		else if (object instanceof IXOptionFolder) {
			return m_OptionFolder;
		}
		else if (object instanceof IXFixture) {
			return m_Fixture;
		}
		else if (object instanceof IXLibraryAssembly) {
			return m_LibraryAssembly;
		}
		else if (object instanceof IXLibraryAssemblyDetails) {
			return m_LibraryAssemblyDetails;
		}
		else if (object instanceof IXLibraryBackshell) {
			return m_LibraryBackshell;
		}
		else if (object instanceof IXLibraryBackshellPlug) {
			return m_LibraryBackshellPlug;
		}
		else if (object instanceof IXLibraryBackshellSeal) {
			return m_LibraryBackshellSeal;
		}
		else if (object instanceof IXLibraryDevicePin) {
			return m_LibraryDevicePin;
		}
		else if (object instanceof IXLibraryCavity) {
			return m_LibraryCavity;
		}
		else if (object instanceof IXLibraryCavityGroup) {
			return m_LibraryCavityGroup;
		}
		else if (object instanceof IXLibraryCavityGroupDetails) {
			return m_LibraryCavityGroupDetails;
		}
		else if (object instanceof IXLibraryRingTerminalGroup) {
			return m_LibraryRingTerminal;
		}
		else if (object instanceof IXLibraryRingTerminalGroupDetail) {
			return m_LibraryRingTerminalDetails;
		}
		else if (object instanceof IXLibraryCavityPlug) {
			return m_LibraryCavityPlug;
		}
		else if (object instanceof IXLibraryCavitySeal) {
			return m_LibraryCavitySeal;
		}
		else if (object instanceof IXLibraryClip) {
			return m_LibraryClip;
		}
		else if (object instanceof IXLibraryColorCode) {
			return m_LibraryColorCode;
		}
		else if (object instanceof IXLibraryComponentTypeCode) {
			return m_LibraryComponentTypeCode;
		}
		else if (object instanceof IXLibraryConnector) {
			return m_LibraryConnector;
		}
		else if (object instanceof IXLibraryConnectorMating) {
			return m_LibraryConnectorMating;
		}
		else if (object instanceof IXLibraryConnectorMatingMapping) {
			return m_LibraryConnectorMatingMapping;
		}
		else if (object instanceof IXLibraryConnectorSeal) {
			return m_LibraryConnectorSeal;
		}
		else if (object instanceof IXLibraryCustomerOrganisation) {
			return m_LibraryCustomerOrganisation;
		}
		else if (object instanceof IXLibraryCustomerPartNumber) {
			return m_LibraryCustomerPartNumber;
		}
		else if (object instanceof IXLibraryDevice) {
			return m_LibraryDevice;
		}
		else if (object instanceof IXLibraryDeviceFootprint) {
			return m_LibraryDeviceFootprint;
		}
		else if (object instanceof IXLibraryDeviceFootprintPinMapping) {
			return m_LibraryDeviceFootprintPinMapping;
		}
		else if (object instanceof IXLibraryDressedRoute) {
			return m_LibraryDressedRoute;
		}
		else if (object instanceof IXLibraryGrommet) {
			return m_LibraryGrommet;
		}
		else if (object instanceof IXLibraryHeatshrinkSleeve) {
			return m_LibraryHeatshrinkSleeve;
		}
		else if (object instanceof IXLibraryHeatshrinkSleeveSelection) {
			return m_LibraryHeatshrinkSleeveSelection;
		}
		else if (object instanceof IXLibraryHousingDefinition) {
			return m_LibraryHousingDefinition;
		}
		else if (object instanceof IXLibraryIDCConnector) {
			return m_LibraryIDCConnector;
		}
		else if (object instanceof IXLibraryInHouseAssembly) {
			return m_LibraryInHouseAssembly;
		}
		else if (object instanceof IXLibraryManufacturingOrganisation) {
			return m_LibraryManufacturingOrganisation;
		}
		else if (object instanceof IXLibraryMaterialCode) {
			return m_LibraryMaterialCode;
		}
		else if (object instanceof IXLibraryMulticore) {
			return m_LibraryMulticore;
		}
		else if (object instanceof IXLibraryMultipleTerminationsConfiguration) {
			return m_LibraryMultipleTerminationsConfiguration;
		}
		else if (object instanceof IXLibraryMultipleWireFitsCavityConfiguration) {
			return m_LibraryMultipleWireFitsCavityConfiguration;
		}
		else if (object instanceof IXLibraryMultiWireCore) {
			return m_LibraryMultiWireCore;
		}
		else if (object instanceof IXLibraryOther) {
			return m_LibraryOther;
		}
		else if (object instanceof IXLibraryRevisionGroup) {
			return m_LibraryRevisionGroup;
		}
		else if (object instanceof IXLibraryScope) {
			return m_LibraryScope;
		}
		else if (object instanceof IXLibrarySingleWireCore) {
			return m_LibrarySingleWireCore;
		}
		else if (object instanceof IXLibrarySolderSleeve) {
			return m_LibrarySolderSleeve;
		}
		else if (object instanceof IXLibrarySolderSleeveSelection) {
			return m_LibrarySolderSleeveSelection;
		}
		else if (object instanceof IXLibrarySplice) {
			return m_LibrarySplice;
		}
		else if (object instanceof IXLibrarySpliceSelection) {
			return m_LibrarySpliceSelection;
		}
		else if (object instanceof IXLibrarySupplierOrganisation) {
			return m_LibrarySupplierOrganisation;
		}
		else if (object instanceof IXLibrarySupplierPartNumber) {
			return m_LibrarySupplierPartNumber;
		}
		else if (object instanceof IXLibrarySymbol) {
			return m_LibrarySymbol;
		}
		else if (object instanceof IXLibraryTape) {
			return m_LibraryTape;
		}
		else if (object instanceof IXLibraryTapeSelection) {
			return m_LibraryTapeSelection;
		}
		else if (object instanceof IXLibraryTerminal) {
			return m_LibraryTerminal;
		}
		else if (object instanceof IXLibraryTermination) {
			return m_LibraryTermination;
		}
		else if (object instanceof IXLibraryTube) {
			return m_LibraryTube;
		}
		else if (object instanceof IXLibraryUltrasonicWeld) {
			return m_LibraryUltrasonicWeld;
		}
		else if (object instanceof IXLibraryUltrasonicWeldSelection) {
			return m_LibraryUltrasonicWeldSelection;
		}
		else if (object instanceof IXLibraryUserProperty) {
			return m_LibraryUserProperty;
		}
		else if (object instanceof IXLibraryWire) {
			return m_LibraryWire;
		}
		else if (object instanceof IXLibraryWireFitsCavity) {
			return m_LibraryWireFitsCavity;
		}
		else if (object instanceof IXLibraryWireGroup) {
			return m_LibraryWireGroup;
		}
		else if (object instanceof IXLibraryWireInsulationThickness) {
			return m_LibraryWireInsulationThickness;
		}
		else if (object instanceof IXLibraryWirePitch) {
			return m_LibraryWirePitch;
		}
		else if (object instanceof IXLibraryWireSpec) {
			return m_LibraryWireSpec;
		}
		else if (object instanceof IXSymbol) {
			return m_Symbol;
		}
		else if (object instanceof IXLibraryFixture) {
			return m_LibraryFixture;
		}
		else if (object instanceof IXLibraryFixtureSelection) {
			return m_LibraryFixtureSelection;
		}
		else if (object instanceof IXInternalLink) {
			return m_LogicDiagramInternalLink;
		}
		else if (object instanceof IXInternalPin) {
			return m_LogicDiagramInternalPin;
		}
		else if (object instanceof IXVariantReferenceNodePosition) {
			return m_VariantReferenceNodePosition;
		}
		else if (object instanceof IXLibraryScopeCode) {
			return m_LibraryScopeCode;
		}
		else if (object instanceof IXLibraryComponentScope) {
			return m_LibraryComponentScope;
		}
		else if (object instanceof IXEngineeringChangeOrder) {
			return m_EngineeringChangeOrder;
		}
		return "Object";
	}

	public void logName(IXObject obj) throws IOException
	{
		if (obj != null) {
			String indexStr = getIndexString();
			String name = obj.getAttribute("Name");
			if (name == null || name.trim().length() == 0) {
				String val = obj.toString();
				if (val == null || val.contains("UID")) {
					val = "";
				}
				m_writer.write(indexStr + val + '\n');
			}
			else {
				if (name.startsWith("UID")) {
					name = "";
				}
				m_writer.write(indexStr + getObjectType(obj) + ':' + name + '\n');
			}
		}
	}

	public void logName(String prefix, IXObject obj) throws IOException
	{
		String indexStr = getIndexString();
		if (obj != null) {
			String name = obj.getAttribute("Name");
			if (name == null || name.trim().length() == 0) {
				String val = obj.toString();
				if (val == null || val.contains("UID")) {
					val = "";
				}
				m_writer.write(indexStr + prefix + val + '\n');
			}
			else {
				if (name.startsWith("UID")) {
					name = "";
				}
				m_writer.write(indexStr + prefix + name + '\n');
			}
		}
		else {
			m_writer.write(indexStr + prefix + '\n');
		}
	}

	public void logName(List<? extends IXObject> objs) throws IOException
	{
		if (objs != null && !objs.isEmpty()) {
			// A list is already sorted, leave the order intact.
			for (IXObject obj : objs) {
				String indexStr = getIndexString();
				if (obj != null) {
					String name = obj.getAttribute("Name");
					if (name == null || name.trim().length() == 0) {
						String val = obj.toString();
						if (val == null || val.contains("UID")) {
							val = "";
						}
						m_writer.write(indexStr + val + '\n');
					}
					else {
						if (name.startsWith("UID")) {
							name = "";
						}
						m_writer.write(indexStr + getObjectType(obj) + ':' + name + '\n');
					}
				}
				else {
					m_writer.write(indexStr + "null" + '\n');
				}
			}
		}
	}

	public void logName(Set<? extends IXObject> objs) throws IOException
	{
		if (objs != null && !objs.isEmpty()) {
			//Create a sorted set from it.
			Set<IXObject> sorteObjs = new TreeSet<IXObject>(getComparator());
			sorteObjs.addAll(objs);
			String indexStr = getIndexString();
			for (IXObject obj : sorteObjs) {
				if (obj == null) {
					continue;
				}
				String name = obj.getAttribute("Name");
				if (name == null || name.trim().length() == 0) {
					String val = obj.toString();
					if (val == null || val.contains("UID")) {
						val = "";
					}
					m_writer.write(indexStr + val + '\n');
				}
				else {
					if (name.startsWith("UID")) {
						name = "";
					}
					m_writer.write(indexStr + getObjectType(obj) + ':' + name + '\n');
				}
			}
		}
	}

	public void logNameAndProperties(Set<? extends IXObject> objs) throws IOException
	{
		if (objs != null && !objs.isEmpty()) {
			//Create a sorted set from it.
			Set<IXObject> sorteObjs = new TreeSet<IXObject>(m_comparator);
			sorteObjs.addAll(objs);
			for (IXObject obj : sorteObjs) {
				if (obj == null) {
					continue;
				}
				logName(obj);
				m_lastColumnIndex++;
				writeAttributesAndProperties(obj);
				m_lastColumnIndex--;
			}
		}
	}

	public void logBundleName(Set<IXBundle> objs) throws IOException
	{
		if (objs != null && !objs.isEmpty()) {
			//Create a sorted set from it.
			Set<IXObject> sorteObjs = new TreeSet<IXObject>(getComparator());
			sorteObjs.addAll(objs);
			String indexStr = getIndexString();
			for (IXObject obj : sorteObjs) {
				if (obj == null) {
					continue;
				}
				String name = obj.getAttribute("Name");
				if (name == null || name.trim().length() == 0) {
					String val = obj.toString();
					if (val == null || val.contains("UID")) {
						val = "";
					}
					m_writer.write(indexStr + val + '\n');
				}
				else {
					if (name.startsWith("UID")) {
						name = "";
					}
					m_writer.write(indexStr + getObjectType(obj) + ':' + name + '\n');
				}
			}
		}
	}

	public void logNameNoSort(Set<? extends IXObject> objs) throws IOException
	{
		if (m_aggressiveSorting) {
			// Always sort if we are doing aggressive sorting
			logName(objs);
			return;
		}
		if (objs != null && !objs.isEmpty()) {
			String indexStr = getIndexString();
			for (IXObject obj : objs) {
				String name = obj.getAttribute("Name");
				if (name == null || name.trim().length() == 0) {
					String val = obj.toString();
					if (val == null || val.contains("UID")) {
						val = "";
					}
					m_writer.write(indexStr + val + '\n');
				}
				else {
					if (name.startsWith("UID")) {
						name = "";
					}
					m_writer.write(indexStr + getObjectType(obj) + ':' + name + '\n');
				}
			}
		}
	}

	protected void writeVariances(Collection<IXVariance> variances) throws IOException
	{
		if (variances != null) {
			for (IXVariance variance : variances) {
				writeVariance(variance);
			}
		}
	}

	protected void writeVariance(IXVariance variance) throws IOException
	{
		String indexStr = getIndexString();
		m_writer.write(indexStr + m_varianceKey + " Name " + variance.getName() + '\n');
		m_writer.write(indexStr + m_varianceKey + " OptionExpression " + variance.getOptionExpression() + '\n');
		m_writer.write(indexStr + m_varianceKey + " Value " + variance.getValue() + '\n');
	}

	protected void writeAbstractConnector(IXAbstractConnector pinList) throws IOException
	{
		writePinList(pinList);
		m_lastColumnIndex++;
		logName(pinList.getDevice());
		logName(pinList.getMatedConnectors());
		m_lastColumnIndex--;
	}

	protected void writePosition(IXInternalPosition pos) throws IOException
	{
		logObjectType(pos);
		//Write the attributes and properties of this pos.
		m_lastColumnIndex++;
		writeAttributesAndProperties(pos);
		logName((IXObject) pos.getPositionContainer());
		List<IXObject> objs = new ArrayList<IXObject>();
		for (IXInternalPositionedObject posObj : pos.getPositionedObjects()) {
			objs.add((IXObject) posObj);
		}
		logName(objs);
		m_lastColumnIndex--;
	}

	protected void writePositionContatiner(IXInternalPositionsContainer cont) throws IOException
	{
		if (cont == null) {
			return;
		}
		Collection<IXInternalPosition> poss = cont.getPositions();
		if (poss != null && !poss.isEmpty()) {
			Set<IXInternalPosition> sortedPoss = new TreeSet<IXInternalPosition>(getComparator());
			sortedPoss.addAll(poss);
			m_lastColumnIndex++;
			for (IXInternalPosition pos : sortedPoss) {
				writePosition(pos);
			}
			m_lastColumnIndex--;
		}
	}

	protected void writeConnector(IXConnector pinList) throws IOException
	{
		writeAbstractConnector(pinList);
		m_lastColumnIndex = m_lastColumnIndex + 1;
		writePinList(pinList.getBackshell());
		logBundleName(pinList.getBundles());
		logName(pinList.getNodes());
		logName(pinList.getHarnesses());
		//Get the mated device connector, if any
		IXDeviceConnector deviceConnector = pinList.getMatedDeviceConnector();
		if (deviceConnector != null) {
			writeAbstractConnector(deviceConnector);
		}
		writePositionContatiner(pinList);
		String indexStr = getIndexString();
		m_lastColumnIndex++;
		//Capture whether the connector is inline or not
		m_writer.write(indexStr + m_connectorKey + " Inline " + pinList.isInline() + '\n');
		writeConnectorRefinement(pinList.getConnectorRefinement());
		if (pinList.getConnectorRefinement() != null) {
			Set<IXConnector> sortedConns = new TreeSet<IXConnector>(getComparator());
			sortedConns.addAll(pinList.getConnectorRefinement().getRefinedConnectors());
			for (IXConnector refinedConn : sortedConns) {
				if (refinedConn instanceof IXRefinedConnector) {
					m_writer.write(indexStr + "Refined Connector Parent - " + '\n');
					logName(((IXRefinedConnector) refinedConn).getParent());
					m_writer.write(indexStr + "Refined Connector Length - " +
							((IXRefinedConnector) refinedConn).getRefinedLength() + " "
							+ '\n');
				}
			}
		}
		m_writer.write(indexStr + "Primary Position Connector - " + '\n');
		logName(pinList.getPrimaryPositionConnector());
		m_writer.write(indexStr + "Variant Position Connectors" + '\n');
		logName(pinList.getVariantPositionConnectors());
		m_lastColumnIndex = m_lastColumnIndex - 2;
	}

	protected void writeRingTerminal(IXRingTerminal pinList) throws IOException
	{
		writeAbstractConnector(pinList);
		m_lastColumnIndex = m_lastColumnIndex + 1;
		logBundleName(pinList.getBundles());
		logName(pinList.getNodes());
		logName(pinList.getHarnesses());
		//Get the mated device connector, if any
		IXDeviceConnector deviceConnector = pinList.getMatedDeviceConnector();
		if (deviceConnector != null) {
			writeAbstractConnector(deviceConnector);
		}

		String indexStr = getIndexString();
		m_lastColumnIndex++;
		m_writer.write(indexStr + "Primary Position Ring Terminal - " + '\n');
		logName(pinList.getPrimaryPositionRingTerminal());
		m_writer.write(indexStr + "Variant Position Ring Terminals" + '\n');
		logName(pinList.getVariantPositionRingTerminals());
		m_lastColumnIndex = m_lastColumnIndex - 2;
	}

	public void writeConnectorPinMap(IXConnectorPinMap connPinMap) throws IOException
	{
		logName(connPinMap.getHarnessLevels());
		Set<Map.Entry<IXCavity, IXCavity>> cavityMapEntries = connPinMap.getAllCavityAssociations();
		for (Map.Entry<IXCavity, IXCavity> mapEntry : cavityMapEntries) {
			String indexStr = getIndexString();
			IXCavity genCavity = mapEntry.getKey();
			String genCavName;
			if (genCavity == null) {
				genCavName = "NULL";
			}
			else {
				genCavName = genCavity.getAttribute("Name");
			}
			IXCavity refCavity = mapEntry.getValue();
			String refCavName;
			if (refCavity == null) {
				refCavName = "NULL";
			}
			else {
				refCavName = refCavity.getAttribute("Name");
			}
			if (genCavity != null && refCavity != null) {
				if (connPinMap.getAssociatedCavity(genCavity) != refCavity) {
					m_writer.write(
							indexStr + "ERROR : Associated Cavity Returned different Cavity than expected" + '\n');
				}
			}

			m_writer.write(
					indexStr + "[Generalized Cavity]" + genCavName + " --- " + refCavName + "[Refined Cavity]" + '\n');
		}
		for (Map.Entry<IXCavity, IXCavity> mapEntry : cavityMapEntries) {
			String indexStr = getIndexString();
			IXCavity refinedCavity = mapEntry.getValue();
			String refinedCavityName;
			if (refinedCavity == null) {
				refinedCavityName = "NULL";
			}
			else {
				refinedCavityName = refinedCavity.getAttribute("Name");
			}
			IXCavity genCavity = mapEntry.getKey();
			String genCavName;
			if (genCavity == null) {
				genCavName = "NULL";
			}
			else {
				genCavName = genCavity.getAttribute("Name");
			}
			if (refinedCavity != null && genCavity != null) {
				if (connPinMap.getAssociatedCavity(refinedCavity) != genCavity) {
					m_writer.write(
							indexStr + "ERROR : Associated Cavity Returned different Cavity than expected" + '\n');
				}
			}

			m_writer.write(
					indexStr + "[Refined Cavity]" + refinedCavityName + " --- " + genCavName + "[Generalized Cavity]" +
							'\n');
		}
	}

	public void writeConnectorRefinement(IXConnectorRefinement connRef) throws IOException
	{
		if (connRef != null) {
			Set<IXConnector> refConns = connRef.getRefinedConnectors();
			if (!refConns.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Refined Connectors:" + '\n');
				Set<IXConnector> sortedConnectors = new TreeSet<IXConnector>(getComparator());
				sortedConnectors.addAll(refConns);
				for (IXConnector connector : sortedConnectors) {
					writeConnector(connector);
					m_writer.write(indexStr + "Refined Length:" + connRef.getRefinedLength(connector) + '\n');
				}
				m_writer.write(indexStr + "Connector Pin Maps:" + '\n');
				for (IXConnectorPinMap pinMap : connRef.getConnectorPinMaps()) {
					writeConnectorPinMap(pinMap);
					for (IXHarnessLevel lvl : pinMap.getHarnessLevels()) {
						if (connRef.getConnectorPinMap(lvl) != pinMap) {
							m_writer.write(
									indexStr + "ERROR : PinMap for Level Returned a different PinMap than expected" +
											'\n');
						}
					}
				}
			}
		}
	}

	protected void writePinList(IXAbstractPinList pinList) throws IOException
	{
		if (pinList == null) {
			return;
		}
		logObjectType(pinList);
		m_lastColumnIndex++;
		//Write the attributes and properties of the conductor
		writeAttributesAndProperties(pinList);
		//Get and write all pins in the connector
		writePins(pinList.getPins());
		m_lastColumnIndex--;
	}

	protected void writeNodes(Set<IXNode> nodes) throws IOException
	{
		Set<IXNode> sortedNodes = new TreeSet<IXNode>(getComparator());
		sortedNodes.addAll(nodes);
		for (IXNode node : sortedNodes) {
			writeNode(node);
		}
	}

	protected void writeBackshell(IXBackshell backshell) throws IOException
	{
		writePinList(backshell);
		logName(backshell.getOwner());
	}

	protected void writeBundleRegion(IXBundleRegion bundleRegion) throws IOException
	{

		logObjectType(bundleRegion);
		m_lastColumnIndex++;
		//Write the attributes and properties of a bundle region
		writeAttributesAndProperties(bundleRegion);
		logName(bundleRegion.getBundle());
		logName(bundleRegion.getNodes());
		m_lastColumnIndex--;
	}

	protected void writeNode(IXNode node) throws IOException
	{

		logObjectType(node);
		m_lastColumnIndex++;
		//Write the attributes and properties of the node
		writeAttributesAndProperties(node);

		String indexStr = getIndexString();
		//If this node is a reference node, then get information about it's anchor structure node
		if (node instanceof IXReferenceNode) {
			logName(((IXReferenceNode) node).getAnchorNode());
			//Get the offset of this reference from it's anchor node
			double offset = ((IXReferenceNode) node).getAnchorNodeOffset();
			m_writer.write(indexStr + m_NodeKey + " Node Offset " + Double.toString(offset) + '\n');

			//Write the variant positions defined on the reference node
			writeVariantReferenceNodePositions((IXReferenceNode) node);
		}

		//Get and write the additional components associated with this node
		logName(node.getAdditionalComponents());
		logName(node.getNodeComponents());
		logName(node.getSplice());
		logName(node.getConnector());
		logName(node.getBundleRegions());
		logBundleName(node.getBundles());
		m_lastColumnIndex--;
	}

	protected void writeHarnessLevels(Set<IXHarnessLevel> harnessLevels) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXHarnessLevel> sortedHarnessLevels = new TreeSet<IXHarnessLevel>(getComparator());
		sortedHarnessLevels.addAll(harnessLevels);
		//Iterate through all harness levels and write individual harness level
		for (IXHarnessLevel harnessLevel : sortedHarnessLevels) {
			writeHarnessLevel(harnessLevel);
		}
	}

	protected void writeAbstractDevice(IXAbstractDevice device) throws IOException
	{
		writePinList(device);
		//Get the mated connectors in the device
		logName(device.getMatedConnectors());
		logName(device.getMatedRingTerminals());
		logName(device.getDeviceConnectors());
	}

	protected void writeDevices(Set<? extends IXDevice> devs) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXDevice> sortedDevs = new TreeSet<IXDevice>(getComparator());
		sortedDevs.addAll(devs);
		//	m_lastColumnIndex ++;
		for (IXDevice dev : sortedDevs) {
			writeDevice(dev);
			m_writer.write('\n');
		}
		//	m_lastColumnIndex --;
	}

	protected void writeBlocks(Set<? extends IXBlock> blocks) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXBlock> sortedDevs = new TreeSet<IXBlock>(getComparator());
		sortedDevs.addAll(blocks);
		//	m_lastColumnIndex ++;
		for (IXBlock blk : sortedDevs) {
			writeBlock(blk);
			m_writer.write('\n');
		}
		//	m_lastColumnIndex --;
	}

	protected void writeInterconnectDevices(Set<IXInterconnectDevice> devs) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXInterconnectDevice> sortedDevs = new TreeSet<IXInterconnectDevice>(getComparator());
		sortedDevs.addAll(devs);
		//m_lastColumnIndex ++;
		for (IXInterconnectDevice dev : sortedDevs) {
			writeAbstractDevice(dev);
			m_writer.write('\n');
		}
		//m_lastColumnIndex --;
	}

	protected void writeDevice(IXDevice device) throws IOException
	{
		writeAbstractDevice(device);
		logName(device.getSlot());
		m_lastColumnIndex++;
		writeInternalLinks(device.getInternalLinks());
		writePins(device.getInternalPins());
		m_lastColumnIndex--;
		Set<IXDevice> devs = device.getFunctionalDevices();
		if (devs != null && !devs.isEmpty()) {
			String indexStr = getIndexString();
			m_writer.write(indexStr + "Functional Devices" + '\n');
			Set<IXDevice> sortedDevs = new TreeSet<IXDevice>(getComparator());
			sortedDevs.addAll(devs);
			for (IXDevice dev : sortedDevs) {
				writeDevice(dev);
			}
		}
	}

	protected void writeBlock(IXBlock block) throws IOException
	{
		writePinList(block);
		m_lastColumnIndex++;
		for (IXHighway hw : block.getHighways()) {
			logName("InterfacedHighway->", hw);
		}
		m_lastColumnIndex--;
	}

	protected void writeHole(IXHole hole) throws IOException
	{

		logObjectType(hole);
		m_lastColumnIndex++;
		//Write the attribute and properties of this slot
		writeAttributesAndProperties(hole);
		logName(hole.getSlot());
		logName(hole.getBundles());
		logName(hole.getNode());
		logName(hole.getWires());
		logName(hole.getHarnesses());
		m_lastColumnIndex--;
	}

	protected void writeSlot(IXSlot slot) throws IOException
	{

		logObjectType(slot);
		m_lastColumnIndex++;
		//Write the attribute and properties of this slot
		writeAttributesAndProperties(slot);

		//Get all the holes in this slot and output that
		Set<IXHole> slotHoles = slot.getHoles();
		if (slotHoles != null && !slotHoles.isEmpty()) {
			//Create a sorted set around these holes and output their name.
			Set<IXHole> sortedSlotHoles = new TreeSet<IXHole>(getComparator());
			sortedSlotHoles.addAll(slotHoles);
			for (IXHole hole : sortedSlotHoles) {
				writeHole(hole);
			}
		}

		//Output all devices this slot is associated with
		Set<IXDevice> slotDevices = slot.getDevices();
		if (slotDevices != null && !slotDevices.isEmpty()) {
			//Sort the set.
			Set<IXDevice> sortedSlotDevices = new TreeSet<IXDevice>(getComparator());
			sortedSlotDevices.addAll(slotDevices);
			for (IXDevice slotDevice : sortedSlotDevices) {
				writeDevice(slotDevice);
			}
		}
		//Create a sorted set before proceeding further.
		writeConnectors(slot.getMatedConnectors());
		//Get the slot type
		IXSlot.IXSlotTypeEnum slotType = slot.getSlotType();
		String indexStr = getIndexString();
		m_writer.write(indexStr + m_SlotType + ':' + slotType.toString() + '\n');
		m_lastColumnIndex--;
	}

	protected void writeSignal(IXSignal signal) throws IOException
	{

		logObjectType(signal);
		m_lastColumnIndex++;
		//Write the attribute and properties of this signal
		writeAttributesAndProperties(signal);

		//Get all the associated splices
		logName(signal.getSplices());
		//Get all the associated shields
		logName(signal.getShields());

		//Get all the associated wires
		logName(signal.getWires());
		//Get and write all functional pins associated
		Set<IXAbstractConductor> conds = signal.getFunctionalConductors();
		if (conds != null && !conds.isEmpty()) {
			String indexStr = getIndexString();
			m_writer.write(indexStr + "Functional Conductors" + '\n');
			Set<IXAbstractConductor> sortedConds = new TreeSet<IXAbstractConductor>(getComparator());
			sortedConds.addAll(conds);
			for (IXAbstractConductor cond : sortedConds) {
				writeConductor(cond);
			}
		}
		m_lastColumnIndex--;
	}

	protected void writeVehicleModel(IXVehicleModel model) throws IOException
	{
		logObjectType(model);
		m_lastColumnIndex++;
		// Write the attribute and properties of this model
		writeAttributesAndProperties(model);
		// Write the supported variants
		String indexStr = getIndexString();
		m_writer.write(indexStr + "Supported Variants" + '\n');
		logName(model.getSupportedVariants());
		// Write the supported options
		m_writer.write(indexStr + "Supported Options" + '\n');
		logName(model.getSupportedOptions());
		// Write the included options
		m_writer.write(indexStr + "Included Options" + '\n');
		logName(model.getIncludedOptions());
		m_lastColumnIndex--;
	}

	protected void writeVehicleConfiguration(IXVehicleConfiguration config) throws IOException
	{
		logObjectType(config);
		m_lastColumnIndex++;
		// Write the attribute and properties of this config
		writeAttributesAndProperties(config);
		// Write the vehicle model
		logName(config.getVehicleModel());
		// Write the harness levels
		logName(config.getHarnessLevels());
		m_lastColumnIndex--;
	}

	protected void writeHarnessLevel(IXHarnessLevel harnessLevel) throws IOException
	{
		logObjectType(harnessLevel);
		m_lastColumnIndex++;
		//Write the harnesslevel name
		m_writer.write(m_harnessLevelKey + ' ' + harnessLevel.getName() + '\n');
		logName(harnessLevel.getHarness());

		//Write the attribute and properties of this harness level
		writeAttributesAndProperties(harnessLevel);

		//Get the options in the harness level
		logName(harnessLevel.getOptions());
		m_lastColumnIndex--;
	}

	protected void writeOption(IXOption option) throws IOException
	{
		//Write the option name
		logObjectType(option);
		m_lastColumnIndex++;
		m_writer.write(m_optionKey + ' ' + option.getName() + '\n');
		writeAttributesAndProperties(option);
		m_lastColumnIndex--;
	}

	protected void writeOptionFolder(IXOptionFolder optFolder) throws IOException
	{
		//Write the option folder name
		logObjectType(optFolder);
		m_lastColumnIndex++;
		m_writer.write(m_OptionFolder + ' ' + optFolder.getBaseID() + '\n');
		writeAttributesAndProperties(optFolder);
		m_lastColumnIndex--;
	}

	protected void writeConfiguration(IXConfiguration config) throws IOException
	{
		//Write the configuration name
		logObjectType(config);
		m_lastColumnIndex++;
		m_writer.write(m_Configuration + ' ' + config.getBaseID() + '\n');
		// attributes and properties
		writeAttributesAndProperties(config);
		// write options
		logName(config.getOptions());
		// write child configurations
		writeConfigurations(config.getChildConfigurations());
		m_lastColumnIndex--;
	}

	protected void writeObjectTypeInfo(IXObjectTypeInfo info) throws IOException
	{
		//Write the object type info name
		String indexStr = getIndexString();
		m_writer.write(indexStr + m_ObjectTypeInfo + '\n');

		m_lastColumnIndex++;
		m_writer.write(m_ObjectTypeInfo + ' ' + info.getName() + '\n');
		// write Name Infos
		writeNameInfos(info.getNameInfos());
		// write Property Infos
		writePropertyInfos(info.getPropertyInfos());
		m_lastColumnIndex--;
	}

	protected void writeNameInfo(IXNameInfo info) throws IOException
	{
		//Write the name info name
		String indexStr = getIndexString();
		m_writer.write(indexStr + m_NameInfo + '\n');

		m_lastColumnIndex++;
		// write name
		m_writer.write(m_attributeKey + m_Name + '=' + info.getName() + '\n');
		// write description
		m_writer.write(m_attributeKey + m_Description + '=' + info.getDescription() + '\n');
		// write short description
		m_writer.write(m_attributeKey + m_ShortDescription + '=' + info.getShortDescription() + '\n');
		m_lastColumnIndex--;
	}

	protected void writePropertyInfo(IXPropertyInfo info) throws IOException
	{
		//Write the property info name
		String indexStr = getIndexString();
		m_writer.write(indexStr + m_PropertyInfo + '\n');

		m_lastColumnIndex++;
		// write name
		m_writer.write(m_attributeKey + m_Name + '=' + info.getName() + '\n');
		// write type
		m_writer.write(m_attributeKey + m_PropertyType + '=' + info.getType() + '\n');
		// write auto assign
		m_writer.write(m_attributeKey + m_AutoAssign + '=' + info.isAutoAssign() + '\n');
		// write default value
		m_writer.write(m_attributeKey + m_DefaultValue + '=' + info.getDefaultValue() + '\n');
		// write property ranges
		writePropertyInfoRanges(info.getRanges());
		m_lastColumnIndex--;
	}

	protected void writePropertyInfoRange(IXPropertyInfoRange range) throws IOException
	{
		//Write the property info range name
		String indexStr = getIndexString();
		m_writer.write(indexStr + m_PropertyInfoRange + '\n');

		m_lastColumnIndex++;
		// write start value
		m_writer.write(m_attributeKey + m_StartValue + '=' + range.getStartValue() + '\n');
		// write end value
		m_writer.write(m_attributeKey + m_EndValue + '=' + range.getEndValue() + '\n');
		m_lastColumnIndex--;
	}

	protected void writeAssembly(IXAssembly assmbly) throws IOException
	{
		logObjectType(assmbly);
		m_lastColumnIndex++;
		writeAttributesAndProperties(assmbly);
		logName(assmbly.getObjects());
		m_lastColumnIndex--;
	}

	protected void writeProject(IXProject proj) throws IOException
	{
		logObjectType(proj);
		m_lastColumnIndex++;
		writeAttributesAndProperties(proj);
		m_writer.write("Build Lists" + '\n');
		writeBuildLists(proj.getBuildLists());
		m_writer.write("Options" + '\n');
		writeOptions(proj.getOptions());
		m_writer.write("Options Folders" + '\n');
		writeOptionFolders(proj.getOptionsFolders());
		m_writer.write("Configurations" + '\n');
		writeConfigurations(proj.getConfigurations());
		m_writer.write("Object Type Infos" + '\n');
		writeObjectTypeInfos(proj.getObjectTypeInfos());
		m_writer.write("Logic Designs" + '\n');
		logNameAndProperties(proj.getLogicDesigns());
		m_writer.write("Integrator Designs" + '\n');
		logNameAndProperties(proj.getIntegratorDesigns());
		m_writer.write("Harness Designs" + '\n');
		logNameAndProperties(proj.getHarnessDesigns());
		m_writer.write("Project Folders" + '\n');
		logNameAndProperties(proj.getFolders());
		m_writer.write("Functional Module Codes" + '\n');
		logNameAndProperties(proj.getFunctionalModuleCodes());
		m_writer.write("Production Module Codes" + '\n');
		logNameAndProperties(proj.getProductionModuleCodes());
		for (Set<IXFunctionalModuleCode> fMCCobbination : proj.getFunctionalModuleCodeCombinations()) {
			m_writer.write("Functional Module Codes Combination" + '\n');
			logNameAndProperties(fMCCobbination);
		}
		for (Set<IXProductionModuleCode> pmCCobbination : proj.getProductionModuleCodeCombinations()) {
			m_writer.write("Production Module Codes Combination" + '\n');
			logNameAndProperties(pmCCobbination);
		}
		m_lastColumnIndex--;
	}

	protected void writeBuildList(IXBuildList bld) throws IOException
	{
		logObjectType(bld);
		m_lastColumnIndex++;
		writeAttributesAndProperties(bld);
		logName(bld.getDesigns());
		Set<? extends IXEngineeringChangeOrder> engineeringChangeOrders = bld.getAssociatedEngineeringChangeOrders();
		for (IXEngineeringChangeOrder xECO : engineeringChangeOrders) {
			logName(xECO);
		}
		m_lastColumnIndex--;
	}

	protected void writeDiagram(IXDiagram dgm) throws IOException
	{
		if (!shouldShowDiagrams()) {
			return;
		}
		if (dgm instanceof IXLogicDiagram) {
			writeLogicDiagram((IXLogicDiagram) dgm);
		}
		else if (dgm instanceof IXHarnessDiagram) {
			writeHarnessDiagram((IXHarnessDiagram) dgm);
		}
		else {
			logObjectType(dgm);
			m_lastColumnIndex++;
			writeAttributesAndProperties(dgm);
			m_lastColumnIndex--;
			m_lastColumnIndex++;
			writeAssociatedGraphics(dgm.getAssociatedObjects(IXGraphicObject.class));
			m_lastColumnIndex--;
		}
	}

	protected void writeDesign(IXDesign des) throws IOException
	{
		logObjectType(des);
		m_lastColumnIndex++;
		writeAttributesAndProperties(des);
		writeDiagrams(des.getDiagrams());
		logName(des.getOptions());
		writeConnectivity(des.getConnectivity());
		m_lastColumnIndex--;
	}

	protected void writeIntegratorDesign(IXIntegratorDesign des) throws IOException
	{
		writeDesign(des);
		m_lastColumnIndex++;
		writeBaseTopoDesignElements(des);
		m_writer.write("Vehicle Models:" + '\n' + '\n');
		writeVehicleModels(des.getVehicleModels());
		m_writer.write("Vehicle Configurations:" + '\n' + '\n');
		writeVehicleConfigurations(des.getVehicleConfigurations());
		m_writer.write("Active Vehicle Configuration:" + '\n' + '\n');
		logName(des.getActiveConfiguration());
		m_lastColumnIndex--;
	}

	protected void writeTopologyDesign(IXTopologyDesign des) throws IOException
	{
		writeDesign(des);
		m_lastColumnIndex++;
		writeBaseTopoDesignElements(des);
		m_lastColumnIndex--;
	}

	protected void writeBaseTopoDesignElements(IXAbstractTopologyDesign des)
			throws IOException
	{
		m_writer.write("Harnesses:" + '\n' + '\n');
		writeHarnesses(des.getHarnesses());
		m_writer.write("Slots:" + '\n' + '\n');
		writeSlots(des.getSlots());
		m_writer.write("Signals:" + '\n' + '\n');
		writeSignals(des.getSignals());
		m_writer.write("Associated Build List:" + '\n' + '\n');
		logName(des.getAssociatedBuildlist());
		m_writer.write("Associated Functional Designs:" + '\n' + '\n');
		logName(des.getAssociatedFunctionalDesigns());
	}

	protected void writeHarnessDesign(IXHarnessDesign des) throws IOException
	{
		writeDesign(des);
		m_lastColumnIndex++;
		m_writer.write("Harness:" + '\n' + '\n');
		writeHarness(des.getHarness());
		m_writer.write("Derivative Designs:" + '\n' + '\n');
		logName(des.getDerivativeDesigns());
		m_writer.write("Composite Parents:" + '\n' + '\n');
		logName(des.getCompositeParents());
		m_lastColumnIndex--;
	}

	protected void writeLibraryObject(IXLibraryObject libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Color:", libObj.getColorCode());
			logName("Material:", libObj.getMaterialCode());
			logName("Component Type Code:", libObj.getComponentTypeCode());

			String indexStr = getIndexString();
			Set<IXLibraryCustomerPartNumber> dets = libObj.getCustomerPartNumbers();
			if (dets != null && !dets.isEmpty()) {
				m_writer.write(indexStr + "Customer PartNumbers" + '\n');
				Set<IXLibraryCustomerPartNumber> sortedDets = new TreeSet<IXLibraryCustomerPartNumber>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryCustomerPartNumber det : sortedDets) {
					writeLibraryCustomerPartNumber(det);
				}
			}

			Set<IXLibrarySupplierPartNumber> sdets = libObj.getSupplierPartNumbers();
			if (sdets != null && !sdets.isEmpty()) {
				m_writer.write(indexStr + "Supplier PartNumbers" + '\n');
				Set<IXLibrarySupplierPartNumber> sortedSDets =
						new TreeSet<IXLibrarySupplierPartNumber>(getComparator());
				sortedSDets.addAll(sdets);
				for (IXLibrarySupplierPartNumber det : sortedSDets) {
					writeLibrarySupplierPartNumber(det);
				}
			}

			Set<IXLibraryHousingDefinition> hdets = libObj.getHousingDefinitions();
			if (hdets != null && !hdets.isEmpty()) {
				m_writer.write(indexStr + "Housing Definitions" + '\n');
				Set<IXLibraryHousingDefinition> sortedhDets = new TreeSet<IXLibraryHousingDefinition>(getComparator());
				sortedhDets.addAll(hdets);
				for (IXLibraryHousingDefinition det : sortedhDets) {
					writeLibraryHousingDefinition(det);
				}
			}

			Set<IXLibrarySymbol> sydets = libObj.getLibrarySymbols();
			if (sydets != null && !sydets.isEmpty()) {
				m_writer.write(indexStr + "Library Symbols" + '\n');
				Set<IXLibrarySymbol> sortedDets = new TreeSet<IXLibrarySymbol>(getComparator());
				sortedDets.addAll(sydets);
				for (IXLibrarySymbol det : sortedDets) {
					writeLibrarySymbol(det);
				}
			}
			writeLibraryRevisionGroup(libObj.getLibraryRevisionGroup());

			Set<? extends IXLibraryComponentScope> compScopes = libObj.getComponentScopeCodes();
			if (compScopes != null && !compScopes.isEmpty()) {
				m_writer.write(indexStr + "Component Scope Codes" + '\n');
				Set<IXLibraryComponentScope> sortedSDets =
						new TreeSet<IXLibraryComponentScope>(getComparator());
				sortedSDets.addAll(compScopes);
				for (IXLibraryComponentScope det : sortedSDets) {
					writeLibraryComponentScope(det);
					logName("Owner:", det.getOwner());
					logName("OwningPart:", det.getOwningPart());
				}
			}
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryColorCode(IXLibraryColorCode libCode) throws IOException
	{
		if (libCode != null) {
			logObjectType(libCode);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libCode);
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryComponentTypeCode(IXLibraryComponentTypeCode libCode) throws IOException
	{
		if (libCode != null) {
			logObjectType(libCode);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libCode);
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryMaterialCode(IXLibraryMaterialCode libCode) throws IOException
	{
		if (libCode != null) {
			logObjectType(libCode);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libCode);
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryScopeCode(IXLibraryScopeCode libCode) throws IOException
	{
		if (libCode != null) {
			logObjectType(libCode);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libCode);
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryComponentScope(IXLibraryComponentScope libCode) throws IOException
	{
		if (libCode != null) {
			logObjectType(libCode);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libCode);
			m_lastColumnIndex--;
			writeLibraryScopeCode(libCode.getScope());
		}
	}

	protected void writeLibraryAssembly(IXLibraryAssembly libObj) throws IOException
	{
		writeLibraryBaseAssembly(libObj);
	}

	protected void writeLibraryAssemblyDetails(IXLibraryAssemblyDetails libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			logName("SubComponent:", libObj.getSubComponent());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryBackshell(IXLibraryBackshell libObj) throws IOException
	{
		writeLibraryCavityContainer(libObj);
	}

	protected void writeLibraryBackshellPlug(IXLibraryBackshellPlug libObj) throws IOException
	{
		writeLibraryObject(libObj);
	}

	protected void writeLibraryBackshellSeal(IXLibraryBackshellSeal libObj) throws IOException
	{
		writeLibraryObject(libObj);
	}

	protected void writeLibraryTerminationsProvider(IXLibraryTerminationsProvider libObj) throws IOException
	{
		if (libObj != null) {
			Set<IXLibraryTermination> dets = libObj.getSingleTerminations();
			String indexStr = getIndexString();
			if (dets != null && !dets.isEmpty()) {
				m_writer.write(indexStr + "Single Terminations" + '\n');
				Set<IXLibraryTermination> sortedDets = new TreeSet<IXLibraryTermination>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryTermination det : sortedDets) {
					writeLibraryTermination(det);
				}
			}
			Set<IXLibraryMultipleTerminationsConfiguration> detConfs = libObj.getMultipleTerminationConfigurations();
			if (detConfs != null && !detConfs.isEmpty()) {
				m_writer.write(indexStr + "Multiple Terminations Configuration" + '\n');
				Set<IXLibraryMultipleTerminationsConfiguration> sortedDets =
						new TreeSet<IXLibraryMultipleTerminationsConfiguration>(getComparator());
				sortedDets.addAll(detConfs);
				for (IXLibraryMultipleTerminationsConfiguration det : sortedDets) {
					writeLibraryMultipleTerminationsConfiguration(det);
				}
			}
		}
	}

	protected void writeLibraryWireFitsCavityProvider(IXLibraryWireFitsCavityProvider libObj) throws IOException
	{
		if (libObj != null) {
			Set<IXLibraryWireFitsCavity> dets = libObj.getSingleWireFitsCavities();
			String indexStr = getIndexString();
			if (dets != null && !dets.isEmpty()) {
				m_writer.write(indexStr + "Single Wire Fits Cavity" + '\n');
				Set<IXLibraryWireFitsCavity> sortedDets = new TreeSet<IXLibraryWireFitsCavity>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryWireFitsCavity det : sortedDets) {
					writeLibraryWireFitsCavity(det);
				}
			}
			Set<IXLibraryMultipleWireFitsCavityConfiguration> detConfs = libObj.getWireFitsCavityConfigurations();
			if (detConfs != null && !detConfs.isEmpty()) {
				m_writer.write(indexStr + "Multiple Wire Fits Cavity Configuration" + '\n');
				Set<IXLibraryMultipleWireFitsCavityConfiguration> sortedDets =
						new TreeSet<IXLibraryMultipleWireFitsCavityConfiguration>(getComparator());
				sortedDets.addAll(detConfs);
				for (IXLibraryMultipleWireFitsCavityConfiguration det : sortedDets) {
					writeLibraryMultipleWireFitsCavityConfiguration(det);
				}
			}
		}
	}

	protected void writeLibraryBaseAssembly(IXLibraryBaseAssembly libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			m_lastColumnIndex++;
			Set<IXLibraryAssemblyDetails> dets = libObj.getAssemblyDetails();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Library Assembly Details" + '\n');
				Set<IXLibraryAssemblyDetails> sortedDets = new TreeSet<IXLibraryAssemblyDetails>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryAssemblyDetails det : sortedDets) {
					writeLibraryAssemblyDetails(det);
				}
			}
			writeLibraryWireFitsCavityProvider(libObj);
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryBaseConnector(IXLibraryBaseConnector libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryCavityContainer(libObj);
			Set<IXLibraryConnectorMating> dets = libObj.getConnectorMatings();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Connector Matings" + '\n');
				Set<IXLibraryConnectorMating> sortedDets = new TreeSet<IXLibraryConnectorMating>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryConnectorMating det : sortedDets) {
					writeLibraryConnectorMating(det, libObj);
				}
			}
			writeLibraryWireFitsCavityProvider(libObj);
		}
	}

	protected void writeLibraryBaseCavity(IXLibraryCavity libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryCavity(IXLibraryCavity libObj) throws IOException
	{
		if (libObj instanceof IXLibraryDevicePin) {
			writeLibraryDevicePin((IXLibraryDevicePin) libObj);
		}
		else {
			writeLibraryBaseCavity(libObj);
		}
	}

	protected void writeLibraryCavityContainer(IXLibraryCavityContainer libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			Set<IXLibraryCavity> dets = libObj.getCavities();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Library Cavities" + '\n');
				Set<IXLibraryCavity> sortedDets = new TreeSet<IXLibraryCavity>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryCavity det : sortedDets) {
					writeLibraryCavity(det);
				}
			}
		}
	}

	protected void writeLibraryCavityGroup(IXLibraryCavityGroup libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			Set<IXLibraryCavityGroupDetails> dets = libObj.getCavityGroupDetails();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Library Cavity Group Details" + '\n');
				Set<IXLibraryCavityGroupDetails> sortedDets = new TreeSet<IXLibraryCavityGroupDetails>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryCavityGroupDetails det : sortedDets) {
					writeLibraryCavityGroupDetails(det);
				}
			}
		}
	}

	protected void writeLibraryCavityGroupDetails(IXLibraryCavityGroupDetails libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			logName("SubComponent:", libObj.getSubComponent());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryRingTerminalGroup(IXLibraryRingTerminalGroup libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			Set<? extends IXLibraryRingTerminalGroupDetail> dets = libObj.getRingTerminalGroupDetails();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Library Ring Terminal Group Details" + '\n');
				Set<IXLibraryRingTerminalGroupDetail> sortedDets =
						new TreeSet<IXLibraryRingTerminalGroupDetail>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryRingTerminalGroupDetail det : sortedDets) {
					writeLibraryRingTerminalGroupDetails(det);
				}
			}
		}
	}

	protected void writeLibraryRingTerminalGroupDetails(IXLibraryRingTerminalGroupDetail libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("OwningPart:", libObj.getOwningPart());
			logName("SubComponent:", libObj.getSubComponent());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryCavityPlug(IXLibraryCavityPlug libObj) throws IOException
	{
		writeLibraryObject(libObj);
	}

	protected void writeLibraryCavitySeal(IXLibraryCavitySeal libObj) throws IOException
	{
		writeLibraryObject(libObj);
		writeLibraryTerminationsProvider(libObj);
	}

	protected void writeLibraryClip(IXLibraryClip libObj) throws IOException
	{
		writeLibraryObject(libObj);
	}

	protected void writeLibraryConnector(IXLibraryConnector libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryBaseConnector(libObj);
			Set<IXLibraryDressedRoute> dets = libObj.getDressedRoutes();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Library Dressed Routes" + '\n');
				Set<IXLibraryDressedRoute> sortedDets = new TreeSet<IXLibraryDressedRoute>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryDressedRoute det : sortedDets) {
					writeLibraryDressedRoute(det);
				}
			}
		}
	}

	protected void writeLibraryConnectorMating(IXLibraryConnectorMating libObj, IXLibraryBaseConnector con)
			throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Mated Connector:", libObj.getMatedConnector(con));
			Set<IXLibraryConnectorMatingMapping> dets = libObj.getMatingPinmappings();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Library Connector Mating Pinmappings" + '\n');
				Set<IXLibraryConnectorMatingMapping> sortedDets =
						new TreeSet<IXLibraryConnectorMatingMapping>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryConnectorMatingMapping det : sortedDets) {
					String genCavName = det.getCavity().getAttribute("Name");
					String refCavName = det.getMappedCavity().getAttribute("Name");
					m_writer.write(indexStr + genCavName + " --- " + refCavName + '\n');
				}
			}
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryConnectorSeal(IXLibraryConnectorSeal libObj) throws IOException
	{
		writeLibraryObject(libObj);
	}

	protected void writeLibraryCustomerOrganisation(IXLibraryCustomerOrganisation libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryCustomerPartNumber(IXLibraryCustomerPartNumber libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			logName("Customer:", libObj.getCustomer());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryDevice(IXLibraryDevice libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryCavityContainer(libObj);
			Set<IXLibraryDeviceFootprint> dets = libObj.getDeviceFootprints();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Device Fotprints" + '\n');
				Set<IXLibraryDeviceFootprint> sortedDets = new TreeSet<IXLibraryDeviceFootprint>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryDeviceFootprint det : sortedDets) {
					writeLibraryDeviceFootprint(det);
				}
			}
		}
	}

	protected void writeLibraryDeviceFootprint(IXLibraryDeviceFootprint libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			Set<IXLibraryDeviceFootprintPinMapping> dets = libObj.getFootprintPinMappings();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Library Device Footprint Pinmappings" + '\n');
				Set<IXLibraryDeviceFootprintPinMapping> sortedDets =
						new TreeSet<IXLibraryDeviceFootprintPinMapping>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryDeviceFootprintPinMapping det : sortedDets) {
					String genCavName = det.getCavity().getAttribute("Name");
					String refCavName = det.getPin().getAttribute("Name");
					String genConnName = det.getCavity().getOwner().getAttribute("PartNumber");
					m_writer.write(indexStr + genConnName + ":" + genCavName + " --- " + refCavName + '\n');
					logName("OwningPart:", det.getOwningPart());
					logName("Owner:", det.getOwner());
				}
			}
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryDevicePin(IXLibraryDevicePin libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryBaseCavity(libObj);
			logName("Contact Material:", libObj.getContactMaterial());
		}
	}

	protected void writeLibraryDressedRoute(IXLibraryDressedRoute libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryGrommet(IXLibraryGrommet libObj) throws IOException
	{
		writeLibraryObject(libObj);
	}

	protected void writeLibraryHeatshrinkSleeve(IXLibraryHeatshrinkSleeve libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			IXLibraryHeatshrinkSleeveSelection det = libObj.getHeatshrinkSelection();
			if (det != null) {
				writeLibraryHeatshrinkSleeveSelection(det);
			}
		}
	}

	protected void writeLibraryHeatshrinkSleeveSelection(IXLibraryHeatshrinkSleeveSelection libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryHousingDefinition(IXLibraryHousingDefinition libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			logName("Sub Component:", libObj.getSubComponent());
			logName("Associated Cavity Group:", libObj.getAssociatedCavityGroup());
			Set<IXLibraryScope> dets = libObj.getScopes();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Scopes" + '\n');
				Set<IXLibraryScope> sortedDets = new TreeSet<IXLibraryScope>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryScope det : sortedDets) {
					writeLibraryScope(det);
					logName("Owner:", det.getOwner());
					IXLibraryCustomerOrganisation customer = det.getCustomer();
					if (customer != null) {
						logName("Customer:", customer);
					}
					IXLibraryManufacturingOrganisation site = det.getManufacturingOrganization();
					if (site != null) {
						logName("Site:", site);
					}
				}
			}
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryIDCConnector(IXLibraryIDCConnector libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryBaseConnector(libObj);
			String indexStr = getIndexString();
			m_writer.write(indexStr + "IDC Cavity Names" + '\n');
			Set<String> sortNames = new TreeSet<String>();
			sortNames.addAll(libObj.getIDCCavityNames());
			for (String det : sortNames) {
				m_writer.write(indexStr + det + '\n');
			}
			writeLibraryTerminationsProvider(libObj);
		}
	}

	protected void writeLibraryInHouseAssembly(IXLibraryInHouseAssembly libObj) throws IOException
	{
		writeLibraryBaseAssembly(libObj);
	}

	protected void writeLibraryBaseInnerCore(IXLibraryInnerCore libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Material:", libObj.getMaterial());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryInnerCore(IXLibraryInnerCore libObj) throws IOException
	{
		if (libObj instanceof IXLibrarySingleWireCore) {
			writeLibrarySingleWireCore((IXLibrarySingleWireCore) libObj);
		}
		else if (libObj instanceof IXLibraryMultiWireCore) {
			writeLibraryMultiWireCore((IXLibraryMultiWireCore) libObj);
		}
	}

	protected void writeLibraryManufacturingOrganisation(IXLibraryManufacturingOrganisation libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryMulticore(IXLibraryMulticore libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			Set<IXLibraryInnerCore> dets = libObj.getInnerCores();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Inner cores" + '\n');
				Set<IXLibraryInnerCore> sortedDets = new TreeSet<IXLibraryInnerCore>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryInnerCore det : sortedDets) {
					writeLibraryInnerCore(det);
				}
			}
		}
	}

	protected void writeLibraryMultipleTerminationsConfiguration(IXLibraryMultipleTerminationsConfiguration libObj)
			throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			Set<IXLibraryTermination> dets = libObj.getMultipleTerminations();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Terminations" + '\n');
				Set<IXLibraryTermination> sortedDets = new TreeSet<IXLibraryTermination>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryTermination det : sortedDets) {
					writeLibraryTermination(det);
				}
			}
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryMultipleWireFitsCavityConfiguration(IXLibraryMultipleWireFitsCavityConfiguration libObj)
			throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			Set<IXLibraryWireFitsCavity> dets = libObj.getMultipleWireFitsCavities();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Wire Fits Cavities" + '\n');
				Set<IXLibraryWireFitsCavity> sortedDets = new TreeSet<IXLibraryWireFitsCavity>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryWireFitsCavity det : sortedDets) {
					writeLibraryWireFitsCavity(det);
				}
			}
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryMultiWireCore(IXLibraryMultiWireCore libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryBaseInnerCore(libObj);
			Set<IXLibraryInnerCore> dets = libObj.getInnerCores();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Inner cores" + '\n');
				Set<IXLibraryInnerCore> sortedDets = new TreeSet<IXLibraryInnerCore>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryInnerCore det : sortedDets) {
					writeLibraryInnerCore(det);
				}
			}
		}
	}

	protected void writeLibraryOther(IXLibraryOther libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			writeLibraryWireFitsCavityProvider(libObj);
		}
	}

	protected void writeLibraryRevisionGroup(IXLibraryRevisionGroup libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName(libObj.getLibraryRevisions());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryScope(IXLibraryScope libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
//			logName("Owner:",libObj.getOwner()); //commented so that UID fields are not shown in file
//			logName("Customer:", libObj.getCustomer());
//			logName("Manufacturing Organization:", libObj.getManufacturingOrganization());
			logName("Scope Code:", libObj.getScope());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibrarySingleWireCore(IXLibrarySingleWireCore libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryBaseInnerCore(libObj);
			logName("Wire Spec:", libObj.getWireSpec());
		}
	}

	protected void writeLibrarySolderSleeve(IXLibrarySolderSleeve libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			IXLibrarySolderSleeveSelection det = libObj.getSolderSleeveSelection();
			if (det != null) {
				writeLibrarySolderSleeveSelection(det);
			}
		}
	}

	protected void writeLibrarySolderSleeveSelection(IXLibrarySolderSleeveSelection libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibrarySplice(IXLibrarySplice libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryCavityContainer(libObj);
			IXLibrarySpliceSelection det = libObj.getSpliceSelection();
			if (det != null) {
				writeLibrarySpliceSelection(det);
			}
		}
	}

	protected void writeLibrarySpliceSelection(IXLibrarySpliceSelection libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibrarySupplierOrganisation(IXLibrarySupplierOrganisation libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			m_lastColumnIndex--;
		}
	}

	protected void writeLibrarySupplierPartNumber(IXLibrarySupplierPartNumber libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			logName("Supplier:", libObj.getSupplier());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibrarySymbol(IXLibrarySymbol libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			writeSymbol(libObj.getSymbol());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryTape(IXLibraryTape libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			Set<IXLibraryTapeSelection> dets = libObj.getTapeSelections();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Tape Selections" + '\n');
				Set<IXLibraryTapeSelection> sortedDets = new TreeSet<IXLibraryTapeSelection>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryTapeSelection det : sortedDets) {
					writeLibraryTapeSelection(det);
				}
			}
		}
	}

	protected void writeLibraryTapeSelection(IXLibraryTapeSelection libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryTerminal(IXLibraryTerminal libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			writeLibraryTerminationsProvider(libObj);
			writeLibraryWireFitsCavityProvider(libObj);
		}
	}

	protected void writeLibraryTermination(IXLibraryTermination libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Wire Spec:", libObj.getWireSpec());
			logName("Associated Wire Group:", libObj.getAssociatedWireGroup());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryTube(IXLibraryTube libObj) throws IOException
	{
		writeLibraryObject(libObj);
	}

	protected void writeLibraryUltrasonicWeld(IXLibraryUltrasonicWeld libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			Set<IXLibraryUltrasonicWeldSelection> dets = libObj.getUltrasonicWeldSelections();
			if (dets != null && !dets.isEmpty()) {
				String indexStr = getIndexString();
				m_writer.write(indexStr + "Ultrasonic Weld Selections" + '\n');
				Set<IXLibraryUltrasonicWeldSelection> sortedDets =
						new TreeSet<IXLibraryUltrasonicWeldSelection>(getComparator());
				sortedDets.addAll(dets);
				for (IXLibraryUltrasonicWeldSelection det : sortedDets) {
					writeLibraryUltrasonicWeldSelection(det);
				}
			}
		}
	}

	protected void writeLibraryUltrasonicWeldSelection(IXLibraryUltrasonicWeldSelection libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Owner:", libObj.getOwner());
			logName("OwningPart:", libObj.getOwningPart());
			logName("Wire Spec:", libObj.getWireSpec());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryUserProperty(IXLibraryUserProperty libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryWire(IXLibraryWire libObj) throws IOException
	{
		if (libObj != null) {
			writeLibraryObject(libObj);
			logName("Wire Spec:", libObj.getWireSpec());
		}
	}

	protected void writeLibraryWireFitsCavity(IXLibraryWireFitsCavity libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Wire Spec:", libObj.getWireSpec());
			logName("Associated Wire Group:", libObj.getAssociatedWireGroup());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryWireGroup(IXLibraryWireGroup libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName(libObj.getWireGroupSpecifications());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryWireInsulationThickness(IXLibraryWireInsulationThickness libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Material:", libObj.getMaterial());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryWirePitch(IXLibraryWirePitch libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Material:", libObj.getMaterial());
			logName("Wire Spec:", libObj.getWireSpec());
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryWireSpec(IXLibraryWireSpec libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			logName("Material:", libObj.getMaterial());
			m_lastColumnIndex--;
		}
	}

	protected void writeSymbol(IXSymbol libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryFixture(IXLibraryFixture libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			IXLibraryFixtureSelection det = libObj.getFixtureSelection();
			if (det != null) {
				writeLibraryFixtureSelection(det);
				logName("Owner:", det.getOwner());
				logName("OwningPart:", det.getOwningPart());
			}
			m_lastColumnIndex--;
		}
	}

	protected void writeLibraryFixtureSelection(IXLibraryFixtureSelection libObj) throws IOException
	{
		if (libObj != null) {
			logObjectType(libObj);
			m_lastColumnIndex++;
			writeAttributesAndProperties(libObj);
			m_lastColumnIndex--;
		}
	}

	public void writeLibrary(IXLibrary lib) throws IOException
	{
		if (lib != null) {
			String indexStr = getIndexString();
			m_writer.write(indexStr + "Color Codes" + '\n');
			Set<IXLibraryColorCode> sortedCols = new TreeSet<IXLibraryColorCode>(getComparator());
			sortedCols.addAll(lib.getColorCodes());
			for (IXLibraryColorCode colCode : sortedCols) {
				writeLibraryColorCode(colCode);
			}
			m_writer.write(indexStr + "Material Codes" + '\n');
			Set<IXLibraryMaterialCode> sortedMats = new TreeSet<IXLibraryMaterialCode>(getComparator());
			sortedMats.addAll(lib.getMaterialCodes());
			for (IXLibraryMaterialCode matCode : sortedMats) {
				writeLibraryMaterialCode(matCode);
			}
			m_writer.write(indexStr + "Component Type Codes" + '\n');
			Set<IXLibraryComponentTypeCode> sortedTypes = new TreeSet<IXLibraryComponentTypeCode>(getComparator());
			sortedTypes.addAll(lib.getComponentTypeCodes());
			for (IXLibraryComponentTypeCode cmpCode : sortedTypes) {
				writeLibraryComponentTypeCode(cmpCode);
			}
			m_writer.write(indexStr + "User Properties" + '\n');
			Set<IXLibraryUserProperty> sortedProps = new TreeSet<IXLibraryUserProperty>(getComparator());
			sortedProps.addAll(lib.getUserProperties());
			for (IXLibraryUserProperty prop : sortedProps) {
				writeLibraryUserProperty(prop);
			}

			m_writer.write(indexStr + "User Properties for type codes" + '\n');
			for (IXLibraryComponentTypeCode cmpCode : sortedTypes) {
				Set<IXLibraryUserProperty> sortedPropsForTypeCode = new TreeSet<IXLibraryUserProperty>(getComparator());
				sortedPropsForTypeCode.addAll(lib.getUserPropertiesForTypeCode(cmpCode));
				if (sortedPropsForTypeCode.size() > 0) {
					m_writer.write(indexStr + "User Properties for Type Code : " + cmpCode.toString() + '\n');
					logName(sortedPropsForTypeCode);
				}
			}
			m_writer.write(indexStr + "Customer Organisations" + '\n');
			Set<IXLibraryCustomerOrganisation> sortedCustOrgs =
					new TreeSet<IXLibraryCustomerOrganisation>(getComparator());
			sortedCustOrgs.addAll(lib.getCustomerOrganisations());
			for (IXLibraryCustomerOrganisation org : sortedCustOrgs) {
				writeLibraryCustomerOrganisation(org);
			}
			m_writer.write(indexStr + "Supplier Organisations" + '\n');
			Set<IXLibrarySupplierOrganisation> sortedSuppOrgs =
					new TreeSet<IXLibrarySupplierOrganisation>(getComparator());
			sortedSuppOrgs.addAll(lib.getSupplierOrganisations());
			for (IXLibrarySupplierOrganisation org : sortedSuppOrgs) {
				writeLibrarySupplierOrganisation(org);
			}
			m_writer.write(indexStr + "Manufacturing Organisations" + '\n');
			Set<IXLibraryManufacturingOrganisation> sortedManOrgs =
					new TreeSet<IXLibraryManufacturingOrganisation>(getComparator());
			sortedManOrgs.addAll(lib.getManufacturingOrganisations());
			for (IXLibraryManufacturingOrganisation org : sortedManOrgs) {
				writeLibraryManufacturingOrganisation(org);
			}
			m_writer.write(indexStr + "Wire Groups" + '\n');
			Set<IXLibraryWireGroup> sortedWGs = new TreeSet<IXLibraryWireGroup>(getComparator());
			sortedWGs.addAll(lib.getWireGroupCodes());
			for (IXLibraryWireGroup grp : sortedWGs) {
				writeLibraryWireGroup(grp);
			}
			m_writer.write(indexStr + "Wire Specs" + '\n');
			Set<IXLibraryWireSpec> sortedSpecs = new TreeSet<IXLibraryWireSpec>(getComparator());
			sortedSpecs.addAll(lib.getWireSpecs());
			for (IXLibraryWireSpec spec : sortedSpecs) {
				writeLibraryWireSpec(spec);
			}
			m_writer.write(indexStr + "Wire Insulation Thickness Objects" + '\n');
			Set<IXLibraryWireInsulationThickness> sortedThicks =
					new TreeSet<IXLibraryWireInsulationThickness>(getComparator());
			sortedThicks.addAll(lib.getWireThicknessDefinitions());
			for (IXLibraryWireInsulationThickness thick : sortedThicks) {
				writeLibraryWireInsulationThickness(thick);
			}
			m_writer.write(indexStr + "Pitch Values" + '\n');
			Set<Integer> sortedPitches = new TreeSet<Integer>();
			sortedPitches.addAll(lib.getPitchValues());
			for (Integer val : sortedPitches) {
				m_writer.write(indexStr + String.valueOf(val.intValue()) + '\n');
			}
			m_writer.write(indexStr + "Wire Pitches" + '\n');
			Set<IXLibraryWirePitch> sortedWPs = new TreeSet<IXLibraryWirePitch>(getComparator());
			sortedWPs.addAll(lib.getWirePitches());
			for (IXLibraryWirePitch pitch : sortedWPs) {
				writeLibraryWirePitch(pitch);
			}

			m_writer.write(indexStr + "Scope Codes" + '\n');
			Set<IXLibraryScopeCode> sortedScopes = new TreeSet<IXLibraryScopeCode>(getComparator());
			sortedScopes.addAll(lib.getScopeCodes());
			for (IXLibraryScopeCode scope : sortedScopes) {
				writeLibraryScopeCode(scope);
			}
		}
	}

	protected void LogInfo(String line) throws IOException
	{
		assert (m_writer != null) : "Null Writer?";
		String indexStr = getIndexString();
		m_writer.write(indexStr + line + "\n");
	}

	protected void writeObjectInfo(String prefix, IXObject xObj) throws IOException
	{
		assert (xObj != null) : "Null XObject?";
		logName(prefix + getObjectType(xObj) + ':', xObj);
	}

	protected void writeDiagramObjectInfo(String prefix, IXDiagramObject xObj) throws IOException
	{
		assert (xObj != null) : "Null XDiagramObject?";
		IXObject xConnObj = xObj.getConnectivity();
		if (xConnObj != null) {
			logName(prefix + getObjectType(xConnObj) + ':', xConnObj);
		}
		else {
			String indexStr = getIndexString();
			m_writer.write(indexStr + prefix + getObjectType(xConnObj) + ": <unknown>\n");
		}
	}

	protected void writeDiagramObjectInfo(IXDiagramObject xObj) throws IOException
	{
		assert (xObj != null) : "Null XDiagramObject?";
		writeDiagramObjectInfo("", xObj);
	}

	protected void writeDiagramObjectAttributes(IXDiagramObject xObj) throws IOException
	{
		assert (xObj != null) : "Null XDiagramObject?";
		writeAttributesAndProperties(xObj);
		writeDiagramObjectBaseAttributes(xObj);
	}

	protected void writeDiagramObjectBaseAttributes(IXDiagramObject xObj) throws IOException
	{
		IXZoneInfo info = xObj.getZoneInfo();
		String indexStr = getIndexString();

		String dispText;
		if (info != null) {
			dispText = indexStr + "Zone - " + info.getRowName() + "_" + info.getColumnName();
			m_writer.write(dispText + '\n');
		}
		Rectangle2D ext = xObj.getAbsoluteExtent();
		if (ext != null) {
			dispText =
					indexStr + "Absolute Extent - " + ext.getX() + "_" + ext.getY() + " -> " + ext.getHeight() + "_" +
							ext.getWidth();
			m_writer.write(dispText + '\n');
		}
		ext = xObj.getRelativeExtent();
		if (ext != null) {
			dispText =
					indexStr + "Relative Extent - " + ext.getX() + "_" + ext.getY() + " -> " + ext.getHeight() + "_" +
							ext.getWidth();
			m_writer.write(dispText + '\n');
		}
	}

	protected void writeGraphicObjectAttributes(IXGraphicObject xObj) throws IOException
	{
		IXZoneInfo info = xObj.getZoneInfo();
		String indexStr = getIndexString();

		String dispText;
		if (info != null) {
			dispText = indexStr + "Zone - " + info.getRowName() + "_" + info.getColumnName();
			m_writer.write(dispText + '\n');
		}
		Rectangle2D ext = xObj.getAbsoluteExtent();
		if (ext != null) {
			dispText =
					indexStr + "Absolute Extent - " + ext.getX() + "_" + ext.getY() + " -> " + ext.getHeight() + "_" +
							ext.getWidth();
			m_writer.write(dispText + '\n');
		}
		ext = xObj.getRelativeExtent();
		if (ext != null) {
			dispText =
					indexStr + "Relative Extent - " + ext.getX() + "_" + ext.getY() + " -> " + ext.getHeight() + "_" +
							ext.getWidth();
			m_writer.write(dispText + '\n');
		}
	}

	protected void writeDiagramInfo(IXDiagram xObj) throws IOException
	{
		assert (xObj != null) : "Null XDiagram?";
		logName(getObjectType(xObj) + ':', xObj);
		m_lastColumnIndex++;
		writeAttributesAndProperties(xObj);
		m_lastColumnIndex--;
	}

	protected void writeDiagramObject(IXDiagramObject xObj) throws IOException
	{
		assert (xObj != null) : "Null XDiagramObject?";
		writeDiagramObjectInfo(xObj);
		m_lastColumnIndex++;
		IXObject xParentObj = xObj.getDiagram();
		if (xParentObj != null) {
			if (xObj instanceof IXLogicDiagramPin) {
				xParentObj = ((IXLogicDiagramPin) xObj).getPinList();
				writeDiagramObjectInfo("Parent=", (IXDiagramObject) xParentObj);
			}
			else {
				writeObjectInfo("Parent=", xParentObj);
			}
		}
		else {
			String indexStr = getIndexString();
			m_writer.write(indexStr + "Parent=" + getObjectType(xParentObj) + ": <No Parent?>\n");
		}
		writeDiagramObjectAttributes(xObj);
		LogInfo("Associated Graphics");
		m_lastColumnIndex++;
		writeAssociatedGraphics(xObj.getAssociatedObjects(IXGraphicObject.class));
		m_lastColumnIndex--;
		m_lastColumnIndex--;
	}

	private void writeGraphic(IXGraphicObject graphObj) throws IOException
	{
		m_lastColumnIndex++;
		String indexStr = getIndexString();
//		logObjectType(graphObj);
		m_writer.write(indexStr + "Object - " + graphObj.toString() + '\n');
		IXObject xParentObj = graphObj.getOwner();
		if (xParentObj != null) {
			writeObjectInfo("Parent=", xParentObj);
		}
		else {
			m_writer.write(indexStr + "Parent=" + getObjectType(xParentObj) + ": <No Parent?>\n");
		}
		writeAttributesAndProperties(graphObj);
		writeGraphicObjectAttributes(graphObj);
		m_writer.write(indexStr + "IsGrouped - " + graphObj.isGrouped() + '\n');
		if (graphObj instanceof IXGraphicGroup) {
			LogInfo("Group.Members");
			m_lastColumnIndex++;
			writeAssociatedGraphics(((IXGraphicGroup) graphObj).getMembers());
			m_lastColumnIndex--;
		}
		m_lastColumnIndex--;
	}

	private void writeAssociatedGraphics(Set<IXGraphicObject> graphObjs) throws IOException
	{
		if (shouldDumpAssociatedGraphics()) {

			for (IXGraphicObject graphObj : getSortedSetOfObjects(graphObjs)) {
				writeGraphic(graphObj);
			}
		}
	}

	protected <T> Set<T> getSortedSetOfObjects(Set<T> inputSet)
	{
		List<T> sortedList = new ArrayList<T>(inputSet);
		Collections.sort(sortedList, m_fullSortComparator);
		Set<T> sortedSet = new LinkedHashSet<T>(sortedList);
		assert sortedSet.size() == inputSet.size();
		return sortedSet;
	}

	protected <T> Set<T> getSortedSetOfDimensionObjects(Set<T> inputSet)
	{
		List<T> sortedList = new ArrayList<T>(inputSet);
		Collections.sort(sortedList, m_dimensionComparator);
		Set<T> sortedSet = new LinkedHashSet<T>(sortedList);
		assert sortedSet.size() == inputSet.size();
		return sortedSet;
	}

	private void writeLogicDiagramPin(IXLogicDiagramPin xPin) throws IOException
	{
		assert (xPin != null) : "Null XLogicDiagramPin?";
		writeDiagramObject(xPin);
		m_lastColumnIndex++;

		LogInfo("Pin.Conductors");
		m_lastColumnIndex++;
		Set<IXLogicDiagramConductor> xAttachedConductors = getSortedSetOfObjects(xPin.getConductors());
		for (IXLogicDiagramConductor xAttachedConductor : xAttachedConductors) {
			writeDiagramObjectInfo(xAttachedConductor);
		}
		m_lastColumnIndex--;

		m_lastColumnIndex--;
	}

	private void writeLogicDiagramPinList(IXLogicDiagramPinList xPinList) throws IOException
	{
		assert (xPinList != null) : "Null XLogicDiagramPinList?";
		writeDiagramObject(xPinList);
		m_lastColumnIndex++;

		LogInfo("PinList.Pins");
		m_lastColumnIndex++;
		Set<IXLogicDiagramPin> xAttachedPins = getSortedSetOfObjects(xPinList.getPins());
		for (IXLogicDiagramPin xAttachedPin : xAttachedPins) {
			writeLogicDiagramPin(xAttachedPin);
		}
		m_lastColumnIndex--;

		LogInfo("PinList.PinLists");
		m_lastColumnIndex++;
		Set<IXLogicDiagramPinList> xAttachedPinLists = getSortedSetOfObjects(xPinList.getAttachedPinListObjects());
		for (IXLogicDiagramPinList xAttachedPinList : xAttachedPinLists) {
			writeDiagramObjectInfo(xAttachedPinList);
		}
		m_lastColumnIndex--;

		m_lastColumnIndex--;
	}

	private void writeLogicDiagramConductor(IXLogicDiagramConductor xConductor) throws IOException
	{
		assert (xConductor != null) : "Null XLogicDiagramConductor?";
		writeDiagramObject(xConductor);
		m_lastColumnIndex++;

		LogInfo("Conductor.Pins");
		m_lastColumnIndex++;
		Set<IXLogicDiagramPin> xAttachedPins = getSortedSetOfObjects(xConductor.getConnectedPins());
		for (IXLogicDiagramPin xAttachedPin : xAttachedPins) {
			writeDiagramObjectInfo(xAttachedPin);
		}
		m_lastColumnIndex--;

		m_lastColumnIndex--;
	}

	private void writeLogicDiagramMultiCore(IXLogicDiagramMultiCore xMultiCore) throws IOException
	{
		assert (xMultiCore != null) : "Null XLogicDiagramMultiCore?";
		writeDiagramObject(xMultiCore);
		m_lastColumnIndex++;

		LogInfo("MultiCore.AllConnectedDiagramPins");
		m_lastColumnIndex++;
		Set<IXLogicDiagramPin> xAttachedPins = getSortedSetOfObjects(xMultiCore.getAllConnectedDiagramPins());
		for (IXLogicDiagramPin xAttachedPin : xAttachedPins) {
			writeDiagramObjectInfo(xAttachedPin);
		}
		m_lastColumnIndex--;

		LogInfo("MultiCore.AllDirectlyConnectedShields");
		m_lastColumnIndex++;
		Set<IXLogicDiagramConductor> shields = getSortedSetOfObjects(xMultiCore.getAllDirectlyConnectedShields());
		for (IXLogicDiagramConductor shield : shields) {
			writeDiagramObjectInfo(shield);
		}
		m_lastColumnIndex--;

		LogInfo("MultiCore.AllConnectedDaisyChains");
		m_lastColumnIndex++;
		Set<IXLogicDiagramDaisyChain> chains = getSortedSetOfObjects(xMultiCore.getAllConnectedDaisyChains());
		for (IXLogicDiagramDaisyChain chain : chains) {
			writeDiagramObject(chain);
		}
		m_lastColumnIndex--;

		m_lastColumnIndex--;
	}

	public void writeLogicDiagram(IXLogicDiagram xLogDiagram) throws IOException
	{
		if (xLogDiagram == null) {
			return;
		}

		writeDiagramInfo(xLogDiagram);
		m_lastColumnIndex++;

		LogInfo("LogicDiagram.PinLists");
		m_lastColumnIndex++;
		Set<IXLogicDiagramPinList> xDiagramPinLists = getSortedSetOfObjects(xLogDiagram.getDiagramPinLists());
		for (IXLogicDiagramPinList xDiagramPinList : xDiagramPinLists) {
			writeLogicDiagramPinList(xDiagramPinList);
		}
		m_lastColumnIndex--;

		LogInfo("LogicDiagram.Conductors");
		m_lastColumnIndex++;
		Set<IXLogicDiagramConductor> xDiagramConductors = getSortedSetOfObjects(xLogDiagram.getDiagramConductors());
		for (IXLogicDiagramConductor xDiagramConductor : xDiagramConductors) {
			writeLogicDiagramConductor(xDiagramConductor);
		}
		m_lastColumnIndex--;

		LogInfo("LogicDiagram.MultiCores");
		m_lastColumnIndex++;
		Set<IXLogicDiagramMultiCore> xDiagramMultiCores = getSortedSetOfObjects(xLogDiagram.getDiagramMulticores());
		for (IXLogicDiagramMultiCore xDiagramMultiCore : xDiagramMultiCores) {
			writeLogicDiagramMultiCore(xDiagramMultiCore);
		}
		m_lastColumnIndex--;
		LogInfo("LogicDiagram.AssociatedGraphics");
		m_lastColumnIndex++;
		writeAssociatedGraphics(xLogDiagram.getAssociatedObjects(IXGraphicObject.class));
		m_lastColumnIndex--;
		m_lastColumnIndex--;
	}

	private void writeHarnessDiagramFixture(IXHarnessDiagramFixture xFixture) throws IOException
	{
		assert (xFixture != null) : "Null XHarnessDiagramFixture?";
		writeDiagramObject(xFixture);
		// also write the connectivity fixture here!
		if (xFixture.getConnectivity() != null) {
			writeFixture((IXFixture) xFixture.getConnectivity());
		}
//		for (IXDrillPoint dp : xFixture.getDrillPoints()) {
//			writeDrillPoint(dp);
//		}
	}

//	private void writeDrillPoint(IXDrillPoint dp) throws IOException
//	{
//		assert dp != null : "Null XDrillPoint?";
//		logName(dp);
//		writeGraphic(dp);
//	}

	private void writeFixture(IXFixture xFixture) throws IOException
	{
		logObjectType(xFixture);

		m_lastColumnIndex++;
		// Write the attributes and properties of fixture
		writeAttributesAndProperties(xFixture);
		m_lastColumnIndex--;
	}

	private void writeHarnessDiagramNodeDimension(IXHarnessDiagramNodeDimension xDimension) throws IOException
	{
		assert (xDimension != null) : "Null XHarnessDiagramNodeDimension?";
		writeDiagramObject(xDimension);
		// start node
		logName(xDimension.getStartNode());
		// end node
		logName(xDimension.getEndNode());
	}

	private void writeHarnessDiagramBundle(IXHarnessDiagramBundle xDiagramBundle) throws IOException
	{
		assert (xDiagramBundle != null) : "Null XHarnessDiagramBundle?";
		writeDiagramObject(xDiagramBundle);
	}

	private void writeHarnessDiagramAxisDimension(IXHarnessDiagramAxialDimension xAxialDimension) throws IOException
	{
		assert (xAxialDimension != null) : "Null XHarnessDiagramAxialDimension?";
		writeDiagramObject(xAxialDimension);
	}

	public void writeHarnessDiagram(IXHarnessDiagram xHarnessDiagram) throws IOException
	{
		if (xHarnessDiagram == null) {
			return;
		}

		writeDiagramInfo(xHarnessDiagram);
		m_lastColumnIndex++;

		LogInfo("HarnessDiagram.Fixtures");
		m_lastColumnIndex++;
		Set<IXHarnessDiagramFixture> xDiagramFixtures = getSortedSetOfObjects(xHarnessDiagram.getDiagramFixtures());
		for (IXHarnessDiagramFixture xDiagramFixture : xDiagramFixtures) {
			writeHarnessDiagramFixture(xDiagramFixture);
		}
		m_lastColumnIndex--;

		LogInfo("HarnessDiagram.Bundles");
		m_lastColumnIndex++;
		Set<IXHarnessDiagramBundle> xDiagramBundles = getSortedSetOfObjects(xHarnessDiagram.getDiagramBundles());
		for (IXHarnessDiagramBundle xDiagramBundle : xDiagramBundles) {
			writeHarnessDiagramBundle(xDiagramBundle);
		}
		m_lastColumnIndex--;

		LogInfo("HarnessDiagram.NodeDimensions");
		m_lastColumnIndex++;
		Set<IXHarnessDiagramNodeDimension> xDiagramNodeDimensions =
				getSortedSetOfDimensionObjects(xHarnessDiagram.getDiagramDimensions());
		for (IXHarnessDiagramNodeDimension xDiagramNodeDimension : xDiagramNodeDimensions) {
			writeHarnessDiagramNodeDimension(xDiagramNodeDimension);
		}
		m_lastColumnIndex--;

		LogInfo("HarnessDiagram.AxisDimensions");
		m_lastColumnIndex++;
		Set<IXHarnessDiagramAxialDimension> xDiagramAxialDimensions =
				getSortedSetOfDimensionObjects(xHarnessDiagram.getDiagramAxialDimensions());
		for (IXHarnessDiagramAxialDimension xDiagramAxialDimension : xDiagramAxialDimensions) {
			writeHarnessDiagramAxisDimension(xDiagramAxialDimension);
		}
		m_lastColumnIndex--;
		LogInfo("LogicDiagram.AssociatedGraphics");
		m_lastColumnIndex++;
		writeAssociatedGraphics(xHarnessDiagram.getAssociatedObjects(IXGraphicObject.class));
		m_lastColumnIndex--;

		m_lastColumnIndex--;
	}

	protected String getIndexString()
	{

		if (m_lastColumnIndex < 1) {
			return "";
		}
		if (m_lastColumnIndex == 1) {
			return "\t";
		}
		else if (m_lastColumnIndex == 2) {
			return "\t\t";
		}
		else if (m_lastColumnIndex == 3) {
			return "\t\t\t";
		}
		else if (m_lastColumnIndex == 4) {
			return "\t\t\t\t";
		}
		else {
			StringBuilder indexBldr = new StringBuilder();
			for (int i = 0; i < m_lastColumnIndex; i++) {
				indexBldr.append("\t");
			}
			return (indexBldr.toString());
		}
	}

	protected void writeAttributesAndProperties(IXObject object) throws IOException
	{
		Collection<IXValue> attributes = object.getAttributes();
		String indexStr = getIndexString();
		ITestIntermittancyResolver intermittancyResolver = getAttrInterMittancyResolver(object);
		for (IXValue attr : attributes) {
			String val = getAttributeValue(attr);
			val = setLibraryPartModifiedValueToEmpty(attr, val);
			if (shouldSkipAttribute(object, attr)) {
				continue;
			}
			if (intermittancyResolver != null) {
				if (!intermittancyResolver.preProcess(attr)) {
					m_writer.write(indexStr + m_attributeKey + attr.getName() + '=' + val + '\n');
				}
			}
			else {
				m_writer.write(indexStr + m_attributeKey + attr.getName() + '=' + val + '\n');
			}
		}
		if (intermittancyResolver != null) {
			intermittancyResolver.postProcess(m_writer, m_attributeKey, getIndexString());
		}

		if (m_isWriteBaseIds) {
			String baseID = object.getBaseID();
			m_writer.write(indexStr + m_baseIdKey + '=' + baseID + '\n');
		}

		Collection<IXValue> properties = object.getProperties();
		for (IXValue prop : properties) {
			m_writer.write(indexStr + m_propertyKey + prop.getName() + '=' + prop.getValue() + '\n');
		}

		// write Symbol
		writeSymbol(object.getSymbol());

		if (object instanceof IXLibrariedObject) {
			logName(((IXLibrariedObject) object).getLibraryObject());
		}
	}

	private String setLibraryPartModifiedValueToEmpty(IXValue attr, String val)
	{
		if (IXAttributes.PartModifiedTime.equalsIgnoreCase(attr.getName())) {
			return "";
		}
		return val;
	}

	protected boolean shouldSkipAttribute(IXObject object, IXValue attr)
	{
		return ExtensibilityAttributeSkipHelper.getInstance().isAttributeSkipped(object, attr);
	}

	protected boolean shouldDumpAssociatedGraphics()
	{
		return true;
	}

	protected String getAttributeValue(IXValue attr)
	{
		String val = attr.getValue();
		if (val == null || val.startsWith("UID")) {
			val = "";
		}
		if (!shouldShowTimezoneInDateValue() && !"".equals(val)) {
			String attrName = attr.getName();
			if (attrName.equalsIgnoreCase(m_CustomerDateAttribute) ||
					attrName.equalsIgnoreCase(m_InternalDateAttribute)) {
				val = convertDate(val);
			}
		}
		return val;
	}

	protected String convertDate(String date)
	{
		if (!shouldShowTimezoneInDateValue()) {
			return date.replaceFirst("(.* )([A-Za-z]{3})( [0-9]+)", "$1$3");
		}
		return date;
	}

	protected boolean shouldShowTimezoneInDateValue()
	{
		return true;
	}

	protected boolean shouldShowDiagrams()
	{
		return true;
	}

	public void writeNetlist(Set<? extends IXObject> xObjects) throws IOException
	{
		writeNetlist(xObjects, false);
	}

	public void writeNetlist(Set<? extends IXObject> xObjects, boolean reportIds) throws IOException
	{
		for (IXObject xObject : xObjects) {
			writeNetlist(xObject, reportIds);
		}
	}

	public void writeObjectIds(IXObject xObject) throws IOException
	{
		if (xObject == null) {
			return;
		}
		String indexStr = getIndexString();
		// write ID
		m_writer.write(indexStr + m_attributeKey + m_ID + '=' + xObject.getID() + '\n');
		// write Base ID
		m_writer.write(indexStr + m_attributeKey + m_BaseID + '=' + xObject.getBaseID() + '\n');
		// write Parent ID
		m_writer.write(indexStr + m_attributeKey + m_ParentID + '=' + xObject.getParentID() + '\n');
	}

	public void writeNetlist(IXObject xObject) throws IOException
	{
		writeNetlist(xObject, false);
	}

	public void writeNetlist(IXObject xObject, boolean reportIds) throws IOException
	{
		m_lastColumnIndex = 0;
		if (xObject == null) {
			return;
		}
		if (xObject instanceof IXDiagram) {
			writeDiagram((IXDiagram) xObject);
		}
		else if (xObject instanceof IXBuildList) {
			writeBuildList((IXBuildList) xObject);
		}
		else if (xObject instanceof IXIntegratorDesign) {
			writeIntegratorDesign((IXIntegratorDesign) xObject);
		}
		else if (xObject instanceof IXLogicDesign) {
			writeDesign((IXDesign) xObject);
		}
		else if (xObject instanceof IXHarnessDesign) {
			writeHarnessDesign((IXHarnessDesign) xObject);
		}
		else if (xObject instanceof IXProject) {
			writeProject((IXProject) xObject);
		}
		else if (xObject instanceof IXOption) {
			writeOption((IXOption) xObject);
		}
		else if (xObject instanceof IXAssembly) {
			writeAssembly((IXAssembly) xObject);
		}
		else if (xObject instanceof IXConnectivity) {
			writeConnectivity((IXConnectivity) xObject);
		}
		else if (xObject instanceof IXGround) {
			writeDevice((IXDevice) xObject);
		}
		else if (xObject instanceof IXDevice) {
			writeDevice((IXDevice) xObject);
		}
		else if (xObject instanceof IXInterconnectDevice) {
			writeAbstractDevice((IXAbstractDevice) xObject);
		}
		else if (xObject instanceof IXSlot) {
			writeSlot((IXSlot) xObject);
		}
		else if (xObject instanceof IXSplice) {
			writeSplice((IXSplice) xObject);
		}
		else if (xObject instanceof IXDeviceConnector) {
			writeAbstractConnector((IXAbstractConnector) xObject);
		}
		else if (xObject instanceof IXConnector) {
			writeConnector((IXConnector) xObject);
		}
		else if (xObject instanceof IXBackshell) {
			writeBackshell((IXBackshell) xObject);
		}
		else if (xObject instanceof IXAbstractConductor) {
			writeConductor((IXAbstractConductor) xObject);
		}
		else if (xObject instanceof IXOverbraid) {
			writeMulticore((IXMulticore) xObject);
		}
		else if (xObject instanceof IXMulticore) {
			writeMulticore((IXMulticore) xObject);
		}
		else if (xObject instanceof IXSignal) {
			writeSignal((IXSignal) xObject);
		}
		else if (xObject instanceof IXAbstractPin) {
			writePin((IXAbstractPin) xObject);
		}
		else if (xObject instanceof IXBundle) {
			writeBundle((IXBundle) xObject);
		}
		else if (xObject instanceof IXBundleRegion) {
			writeBundleRegion((IXBundleRegion) xObject);
		}
		else if (xObject instanceof IXHole) {
			writeHole((IXHole) xObject);
		}
		else if (xObject instanceof IXHarnessLevel) {
			writeHarnessLevel((IXHarnessLevel) xObject);
		}
		else if (xObject instanceof IXHarness) {
			writeHarness((IXHarness) xObject);
		}
		else if (xObject instanceof IXHarnessRegister) {
			writeHarnessRegister((IXHarnessRegister) xObject);
		}
		else if (xObject instanceof IXNode) {
			writeNode((IXNode) xObject);
		}
		else if (xObject instanceof IXNodeComponent) {
			writeNodeComponent((IXNodeComponent) xObject);
		}
		else if (xObject instanceof IXMultiLocationComponent) {
			writeMLC((IXMultiLocationComponent) xObject);
		}
		else if (xObject instanceof IXBreakoutTape) {
			writeBreakoutTape((IXBreakoutTape) xObject);
		}
		else if (xObject instanceof IXSpotTape) {
			writeSpotTape((IXSpotTape) xObject);
		}
		else if (xObject instanceof IXInsulation) {
			writeInsulation((IXInsulation) xObject);
		}
		else if (xObject instanceof IXInsulationRun) {
			writeInsulationRun((IXInsulationRun) xObject);
		}
		else if (xObject instanceof IXWireEnd) {
			writeWireEnd((IXWireEnd) xObject);
		}
		else if (xObject instanceof IXCavityDetail) {
			writeCavityDetail((IXCavityDetail) xObject);
		}
		else if (xObject instanceof IXTerminal) {
			writeTerminal((IXTerminal) xObject);
		}
		else if (xObject instanceof IXCavitySeal) {
			writeSeal((IXCavitySeal) xObject);
		}
		else if (xObject instanceof IXAdditionalComponent) {
			writeAdditionalComponent((IXAdditionalComponent) xObject);
		}
		else if (xObject instanceof IXVehicleModel) {
			writeVehicleModel((IXVehicleModel) xObject);
		}
		else if (xObject instanceof IXVehicleConfiguration) {
			writeVehicleConfiguration((IXVehicleConfiguration) xObject);
		}
		else if (xObject instanceof IXConfiguration) {
			writeConfiguration((IXConfiguration) xObject);
		}
		else if (xObject instanceof IXOptionFolder) {
			writeOptionFolder((IXOptionFolder) xObject);
		}
		else if (xObject instanceof IXFixture) {
			writeFixture((IXFixture) xObject);
		}
		else if (xObject instanceof IXLibraryColorCode) {
			writeLibraryColorCode((IXLibraryColorCode) xObject);
		}
		else if (xObject instanceof IXLibraryMaterialCode) {
			writeLibraryMaterialCode((IXLibraryMaterialCode) xObject);
		}
		else if (xObject instanceof IXLibraryComponentTypeCode) {
			writeLibraryComponentTypeCode((IXLibraryComponentTypeCode) xObject);
		}
		else if (xObject instanceof IXLibraryAssembly) {
			writeLibraryAssembly((IXLibraryAssembly) xObject);
		}
		else if (xObject instanceof IXLibraryAssemblyDetails) {
			writeLibraryAssemblyDetails((IXLibraryAssemblyDetails) xObject);
		}
		else if (xObject instanceof IXLibraryBackshell) {
			writeLibraryBackshell((IXLibraryBackshell) xObject);
		}
		else if (xObject instanceof IXLibraryBackshellPlug) {
			writeLibraryBackshellPlug((IXLibraryBackshellPlug) xObject);
		}
		else if (xObject instanceof IXLibraryBackshellSeal) {
			writeLibraryBackshellSeal((IXLibraryBackshellSeal) xObject);
		}
		else if (xObject instanceof IXLibraryDevicePin) {
			writeLibraryDevicePin((IXLibraryDevicePin) xObject);
		}
		else if (xObject instanceof IXLibraryCavity) {
			writeLibraryCavity((IXLibraryCavity) xObject);
		}
		else if (xObject instanceof IXLibraryCavityGroup) {
			writeLibraryCavityGroup((IXLibraryCavityGroup) xObject);
		}
		else if (xObject instanceof IXLibraryCavityGroupDetails) {
			writeLibraryCavityGroupDetails((IXLibraryCavityGroupDetails) xObject);
		}
		else if (xObject instanceof IXLibraryRingTerminalGroup) {
			writeLibraryRingTerminalGroup((IXLibraryRingTerminalGroup) xObject);
		}
		else if (xObject instanceof IXLibraryRingTerminalGroupDetail) {
			writeLibraryRingTerminalGroupDetails((IXLibraryRingTerminalGroupDetail) xObject);
		}
		else if (xObject instanceof IXLibraryCavityPlug) {
			writeLibraryCavityPlug((IXLibraryCavityPlug) xObject);
		}
		else if (xObject instanceof IXLibraryCavitySeal) {
			writeLibraryCavitySeal((IXLibraryCavitySeal) xObject);
		}
		else if (xObject instanceof IXLibraryClip) {
			writeLibraryClip((IXLibraryClip) xObject);
		}
		else if (xObject instanceof IXLibraryConnector) {
			writeLibraryConnector((IXLibraryConnector) xObject);
		}
		else if (xObject instanceof IXLibraryConnectorSeal) {
			writeLibraryConnectorSeal((IXLibraryConnectorSeal) xObject);
		}
		else if (xObject instanceof IXLibraryCustomerOrganisation) {
			writeLibraryCustomerOrganisation((IXLibraryCustomerOrganisation) xObject);
		}
		else if (xObject instanceof IXLibraryCustomerPartNumber) {
			writeLibraryCustomerPartNumber((IXLibraryCustomerPartNumber) xObject);
		}
		else if (xObject instanceof IXLibraryDevice) {
			writeLibraryDevice((IXLibraryDevice) xObject);
		}
		else if (xObject instanceof IXLibraryDeviceFootprint) {
			writeLibraryDeviceFootprint((IXLibraryDeviceFootprint) xObject);
		}
		else if (xObject instanceof IXLibraryDressedRoute) {
			writeLibraryDressedRoute((IXLibraryDressedRoute) xObject);
		}
		else if (xObject instanceof IXLibraryGrommet) {
			writeLibraryGrommet((IXLibraryGrommet) xObject);
		}
		else if (xObject instanceof IXLibraryHeatshrinkSleeve) {
			writeLibraryHeatshrinkSleeve((IXLibraryHeatshrinkSleeve) xObject);
		}
		else if (xObject instanceof IXLibraryHeatshrinkSleeveSelection) {
			writeLibraryHeatshrinkSleeveSelection((IXLibraryHeatshrinkSleeveSelection) xObject);
		}
		else if (xObject instanceof IXLibraryHousingDefinition) {
			writeLibraryHousingDefinition((IXLibraryHousingDefinition) xObject);
		}
		else if (xObject instanceof IXLibraryIDCConnector) {
			writeLibraryIDCConnector((IXLibraryIDCConnector) xObject);
		}
		else if (xObject instanceof IXLibraryInHouseAssembly) {
			writeLibraryInHouseAssembly((IXLibraryInHouseAssembly) xObject);
		}
		else if (xObject instanceof IXLibraryManufacturingOrganisation) {
			writeLibraryManufacturingOrganisation((IXLibraryManufacturingOrganisation) xObject);
		}
		else if (xObject instanceof IXLibraryMulticore) {
			writeLibraryMulticore((IXLibraryMulticore) xObject);
		}
		else if (xObject instanceof IXLibraryMultipleTerminationsConfiguration) {
			writeLibraryMultipleTerminationsConfiguration((IXLibraryMultipleTerminationsConfiguration) xObject);
		}
		else if (xObject instanceof IXLibraryMultipleWireFitsCavityConfiguration) {
			writeLibraryMultipleWireFitsCavityConfiguration((IXLibraryMultipleWireFitsCavityConfiguration) xObject);
		}
		else if (xObject instanceof IXLibraryMultiWireCore) {
			writeLibraryMultiWireCore((IXLibraryMultiWireCore) xObject);
		}
		else if (xObject instanceof IXLibraryOther) {
			writeLibraryOther((IXLibraryOther) xObject);
		}
		else if (xObject instanceof IXLibraryRevisionGroup) {
			writeLibraryRevisionGroup((IXLibraryRevisionGroup) xObject);
		}
		else if (xObject instanceof IXLibraryScope) {
			writeLibraryScope((IXLibraryScope) xObject);
		}
		else if (xObject instanceof IXLibraryScopeCode) {
			writeLibraryScopeCode((IXLibraryScopeCode) xObject);
		}
		else if (xObject instanceof IXLibraryComponentScope) {
			writeLibraryComponentScope((IXLibraryComponentScope) xObject);
		}
		else if (xObject instanceof IXLibrarySingleWireCore) {
			writeLibrarySingleWireCore((IXLibrarySingleWireCore) xObject);
		}
		else if (xObject instanceof IXLibrarySolderSleeve) {
			writeLibrarySolderSleeve((IXLibrarySolderSleeve) xObject);
		}
		else if (xObject instanceof IXLibrarySolderSleeveSelection) {
			writeLibrarySolderSleeveSelection((IXLibrarySolderSleeveSelection) xObject);
		}
		else if (xObject instanceof IXLibrarySplice) {
			writeLibrarySplice((IXLibrarySplice) xObject);
		}
		else if (xObject instanceof IXLibrarySpliceSelection) {
			writeLibrarySpliceSelection((IXLibrarySpliceSelection) xObject);
		}
		else if (xObject instanceof IXLibrarySupplierOrganisation) {
			writeLibrarySupplierOrganisation((IXLibrarySupplierOrganisation) xObject);
		}
		else if (xObject instanceof IXLibrarySupplierPartNumber) {
			writeLibrarySupplierPartNumber((IXLibrarySupplierPartNumber) xObject);
		}
		else if (xObject instanceof IXLibrarySymbol) {
			writeLibrarySymbol((IXLibrarySymbol) xObject);
		}
		else if (xObject instanceof IXLibraryTape) {
			writeLibraryTape((IXLibraryTape) xObject);
		}
		else if (xObject instanceof IXLibraryTapeSelection) {
			writeLibraryTapeSelection((IXLibraryTapeSelection) xObject);
		}
		else if (xObject instanceof IXLibraryTerminal) {
			writeLibraryTerminal((IXLibraryTerminal) xObject);
		}
		else if (xObject instanceof IXLibraryTermination) {
			writeLibraryTermination((IXLibraryTermination) xObject);
		}
		else if (xObject instanceof IXLibraryTube) {
			writeLibraryTube((IXLibraryTube) xObject);
		}
		else if (xObject instanceof IXLibraryUltrasonicWeld) {
			writeLibraryUltrasonicWeld((IXLibraryUltrasonicWeld) xObject);
		}
		else if (xObject instanceof IXLibraryUltrasonicWeldSelection) {
			writeLibraryUltrasonicWeldSelection((IXLibraryUltrasonicWeldSelection) xObject);
		}
		else if (xObject instanceof IXLibraryUserProperty) {
			writeLibraryUserProperty((IXLibraryUserProperty) xObject);
		}
		else if (xObject instanceof IXLibraryWire) {
			writeLibraryWire((IXLibraryWire) xObject);
		}
		else if (xObject instanceof IXLibraryWireFitsCavity) {
			writeLibraryWireFitsCavity((IXLibraryWireFitsCavity) xObject);
		}
		else if (xObject instanceof IXLibraryWireGroup) {
			writeLibraryWireGroup((IXLibraryWireGroup) xObject);
		}
		else if (xObject instanceof IXLibraryWireInsulationThickness) {
			writeLibraryWireInsulationThickness((IXLibraryWireInsulationThickness) xObject);
		}
		else if (xObject instanceof IXLibraryWirePitch) {
			writeLibraryWirePitch((IXLibraryWirePitch) xObject);
		}
		else if (xObject instanceof IXLibraryWireSpec) {
			writeLibraryWireSpec((IXLibraryWireSpec) xObject);
		}
		else if (xObject instanceof IXLibraryFixture) {
			writeLibraryFixture((IXLibraryFixture) xObject);
		}
		else if (xObject instanceof IXLibraryFixtureSelection) {
			writeLibraryFixtureSelection((IXLibraryFixtureSelection) xObject);
		}
		else if (xObject instanceof IXSymbol) {
			writeSymbol((IXSymbol) xObject);
		}
		else if (xObject instanceof IXGraphicObject) {
			writeGraphic((IXGraphicObject) xObject);
		}
		else if (xObject instanceof IXDiagramObject) {
			writeDiagramObject((IXDiagramObject) xObject);
		}
		else if (xObject instanceof IXEngineeringChangeOrder) {
			writeChangeOrder((IXEngineeringChangeOrder) xObject);
		}
		// write object ids at the end
		if (reportIds) {
			writeObjectIds(xObject);
		}
	}

	protected void writeInternalLinks(Collection<IXInternalLink> links) throws IOException
	{
		if (links != null && !links.isEmpty()) {
			m_lastColumnIndex++;
			Set<IXInternalLink> sortedLinks = new TreeSet<IXInternalLink>(m_comparator);
			sortedLinks.addAll(links);
			for (IXInternalLink link : sortedLinks) {
				writeInternalLink(link);
			}
			m_lastColumnIndex--;
		}
	}

	protected void writeInternalLink(IXInternalLink link) throws IOException
	{
		if (link != null) {
			logObjectType(link);
			m_lastColumnIndex++;
			writeAttributesAndProperties(link);
			logName(link.getPins());
			m_lastColumnIndex--;
		}
	}

	protected void writeVariantReferenceNodePositions(IXReferenceNode referenceNode) throws IOException
	{
		if (referenceNode == null) {
			return;
		}
		Set<? extends IXVariantReferenceNodePosition> variantPositions = referenceNode.getAllVariantNodePositions();
		if (variantPositions != null && !variantPositions.isEmpty()) {
			Set<IXVariantReferenceNodePosition> sortedVariantPositions =
					new TreeSet<IXVariantReferenceNodePosition>(getComparator());
			sortedVariantPositions.addAll(variantPositions);
			m_lastColumnIndex++;
			for (IXVariantReferenceNodePosition position : sortedVariantPositions) {
				writeVariantReferenceNodePosition(position);
			}
			m_lastColumnIndex--;
		}
	}

	protected void writeVariantReferenceNodePosition(IXVariantReferenceNodePosition position) throws IOException
	{
		logObjectType(position);
		//Write the attributes and properties of this pos.
		m_lastColumnIndex++;
		writeAttributesAndProperties(position);
		logName(position.getAnchorNode());
		m_lastColumnIndex--;
	}

	protected void writeChangeOrder(IXEngineeringChangeOrder eco) throws IOException
	{
		logObjectType(eco);
		m_lastColumnIndex++;
		writeAttributesAndProperties(eco);
		List<IXObject> objs = new ArrayList<IXObject>();
		for (IXEngineeringChangeOrderAssociatable assocObj : eco.getAssociatedObjects()) {
			objs.add((IXObject) assocObj);
		}
		logName(objs);
		m_lastColumnIndex--;
	}

	protected void writeChangeOrders(Set<IXEngineeringChangeOrder> ecos) throws IOException
	{
		//Create a sorted set before proceeding further.
		Set<IXEngineeringChangeOrder> sortedECOS = new TreeSet<IXEngineeringChangeOrder>(getComparator());
		sortedECOS.addAll(ecos);

		for (IXEngineeringChangeOrder eco : sortedECOS) {
			writeChangeOrder(eco);
		}
	}

	public static class XObjectComparator implements Comparator
	{

		private String m_compareAttr = "Name";
		private boolean m_sortWithObjType = false;
		private boolean m_sortWithDuplicates = false;

		public XObjectComparator(String attr)
		{
			m_compareAttr = attr;
		}

		public XObjectComparator(String attr, Boolean sortWithObjType, Boolean sortWithDuplicates)
		{
			m_compareAttr = attr;
			m_sortWithObjType = sortWithObjType;
			m_sortWithDuplicates = sortWithDuplicates;
		}

		public XObjectComparator()
		{
		}

		public int compare(Object po1, Object po2)
		{
			Object o1 = po1;
			Object o2 = po2;
			if (po1 instanceof IXDiagramObject) {
				o1 = ((IXDiagramObject) po1).getConnectivity();
			}
			if (po2 instanceof IXDiagramObject) {
				o2 = ((IXDiagramObject) po2).getConnectivity();
			}
			if (o1 == null) {
				o1 = po1;
			}
			if (o2 == null) {
				o2 = po2;
			}
			if (o1 != null && o2 != null) {
				if ((o1 instanceof IXObject) && (o2 instanceof IXObject)) {
					String firstObjName;
					String secondObjName;
					if (m_compareAttr != null) {
						firstObjName = ((IXObject) o1).getAttribute(m_compareAttr);
						secondObjName = ((IXObject) o2).getAttribute(m_compareAttr);
						if (firstObjName == null) {
							firstObjName = o1.toString();
						}
						if (secondObjName == null) {
							secondObjName = o2.toString();
						}
					}
					else {
						firstObjName = o1.toString();
						secondObjName = o2.toString();
					}
					if (firstObjName != null && secondObjName != null) {
						//TODO-cssingh we shouldnot ignore if the object names are same. in that case
						//TODO-cssingh we will return -1 so that they will be in the same order as being passed.
						if (m_sortWithObjType) {
							firstObjName = getObjectType((IXObject) o1) + ":" + firstObjName;
							secondObjName = getObjectType((IXObject) o2) + ":" + secondObjName;
						}
						int retVal = firstObjName.compareTo(secondObjName);
						if (m_sortWithDuplicates) {
							// To ensure we always produce a consistent result in unit tests we must fallback to UID
							// comapre when both name and type are the same (problem occurs multiple instances of
							// schematic objects e.g Multicore with Shield attached to only once instance).
							// Since we are falling back to UID comparison the *source* project *must* contain full UIDS
							// - i.e not be a copy
							if (retVal == 0) {
								String uid1 = ((IXObject) o1).getID();
								String uid2 = ((IXObject) o2).getID();
								retVal = compareUIDs(uid1, uid2);
							}
						}
						return retVal;
					}
				}
			}
			return -1;
		}

		//A dummy implementation. This comparator is only used in sorting of sets and it uses
		//only the compare method.
		public boolean equals(Object obj)
		{
			boolean isObjectsEqual = false;
			return isObjectsEqual;
		}
	}

	/**
	 * Intended to be used in a comparator so that newer UIDs compare greater than older ones
	 *
	 * @param uid1 the first UID
	 * @param uid2 the second UID
	 *
	 * @return <0, 0, or >0 per String.compareTo()
	 */
	private static int compareUIDs(String uid1, String uid2)
	{
		// compare based on 'time' UID was created
		String time1 = getTimeValue(uid1);
		String time2 = getTimeValue(uid2);
		if (time1 != null && time2 != null) {
			int diff = time1.compareTo(time2);
			if (diff != 0) {
				return diff;
			}
		}
		// fallback on comparing the whole string if we couldn't parse either, or if the times were identical
		return uid1.compareTo(uid2);
	}

	/**
	 * Extract the central part of the UID string, which is larger with more recent values
	 *
	 * @param uid the full UID string
	 *
	 * @return the central part of the UID string
	 */
	private static String getTimeValue(String uid)
	{
		int first_hyphen = uid.indexOf('-');
		int second_hyphen = uid.indexOf('-', first_hyphen + 1);
		if (first_hyphen == -1 || second_hyphen == -1) {
			return null;
		}
		return uid.substring(first_hyphen + 1, second_hyphen);
	}

	/**
	 * Returns the correct type of comparator based on the object sorting mode.
	 * <p/>
	 *
	 * @return XObjectComparator
	 */
	protected XObjectComparator getComparator()
	{
		return m_aggressiveSorting ? m_fullSortComparator : m_comparator;
	}

	public static class XDimensionObjectComparator implements Comparator
	{

		public XDimensionObjectComparator()
		{
		}

		public int compare(Object o1, Object o2)
		{
			IXHarnessDiagramNodeDimension dim1 = null;
			if (o1 instanceof IXHarnessDiagramNodeDimension) {
				dim1 = ((IXHarnessDiagramNodeDimension) o1);
			}
			IXHarnessDiagramNodeDimension dim2 = null;
			if (o2 instanceof IXHarnessDiagramNodeDimension) {
				dim2 = (IXHarnessDiagramNodeDimension) o2;
			}
			if (dim1 != null && dim2 != null) {
				String dimStr1 = dim1.getAttribute(DIMENSION_VALUE_ATTR);
				String dimStr2 = dim2.getAttribute(DIMENSION_VALUE_ATTR);
				if (dimStr1.equals(dimStr2)) {
					String o1Nodes =
							dim1.getStartNode().getAttribute("NAME").concat(dim1.getEndNode().getAttribute("NAME"));
					String o2Nodes =
							dim2.getStartNode().getAttribute("NAME").concat(dim2.getEndNode().getAttribute("NAME"));
					return o1Nodes.compareToIgnoreCase(o2Nodes);
				}
				return dimStr1.compareTo(dimStr2);
			}
			return -1;
		}
	}

	protected ITestIntermittancyResolver getAttrInterMittancyResolver(IXObject object)
	{
		return null;
	}

	public interface ITestIntermittancyResolver
	{

		boolean preProcess(IXValue attr);

		void postProcess(Writer writer, String attributeKey, String indexStr) throws IOException;
	}
}
