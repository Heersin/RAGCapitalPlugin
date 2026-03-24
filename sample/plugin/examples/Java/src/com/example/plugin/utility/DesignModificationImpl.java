package com.example.plugin.utility;

import com.mentor.chs.api.IXAbstractTopologyDesign;
import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXConnectivityObject;
import com.mentor.chs.api.IXDesign;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXHarness;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXWriteableObject;
import com.mentor.chs.api.XGraphicLineStyle;
import com.mentor.chs.plugin.IXAttributeSetter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA. User: kjuthi Date: Oct 3, 2012 Time: 6:26:45 PM
 */
public class DesignModificationImpl
{

	private Collection<IXDesign> designs;
	private Map<String, String> prefixMap;

	public DesignModificationImpl(Collection<IXDesign> designs)
	{
		this(designs, new HashMap<String, String>(1));
	}

	public DesignModificationImpl(Collection<IXDesign> designs, Map<String, String> prefixMap)
	{
		this.designs = designs;
		this.prefixMap = prefixMap;
	}

	public void modifyDesignObjects()
	{
		List<IXDiagramObject> diagObjs = new ArrayList<IXDiagramObject>();
		Set<IXConnectivityObject> allconnObjs = new HashSet<IXConnectivityObject>();

		collectDesignObjectsForModification(diagObjs, allconnObjs);

		String time = getFormattedTimeStamp();

		addPropertyToDiagrams(diagObjs, time);

		addPropertyToConnectivityObjects(allconnObjs, time);
	}

	private String getFormattedTimeStamp()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss:SSS, yyyy-MM-d");
		Calendar cal = Calendar.getInstance();
		String time = sdf.format(cal.getTime());
		return time;
	}

	private void addPropertyToConnectivityObjects(Set<IXConnectivityObject> allconnObjs, String time)
	{
		System.out.println("===================  Modifying shuffled connectivity objects");
		List<IXConnectivityObject> allconnObjList = new ArrayList<IXConnectivityObject>(allconnObjs);
		Collections.shuffle(allconnObjList);
		for (IXConnectivityObject connObj : allconnObjList) {
			if (connObj instanceof IXWriteableObject) {
				IXAttributeSetter attSett = ((IXWriteableObject) connObj).getAttributeSetter();
				if (attSett != null) {
					attSett.addProperty(getPropertyNameFor((IXWriteableObject) connObj), time);
				}
			}
		}
	}

	private String getPropertyNameFor(IXWriteableObject writeableObject)
	{
		String namePrefix = prefixMap.get(writeableObject.getClass().getName());
		String attributeName = "ModifiedTime";
		if (namePrefix != null) {
			return namePrefix + attributeName;
		}
		return attributeName;
	}

	private void addPropertyToDiagrams(List<IXDiagramObject> diagObjs, String time)
	{
		System.out.println("===================  Modifying shuffled diagram objects");
		Collections.shuffle(diagObjs);
		for (IXDiagramObject diagObj : diagObjs) {
			IXAttributeSetter attSett = diagObj.getAttributeSetter();
			if (attSett != null) {
				attSett.addAttribute(IXAttributes.GraphicLineStyle, XGraphicLineStyle.DOTTED.getName());
				attSett.addProperty(getPropertyNameFor(diagObj), time);
			}
		}
	}

	private void collectDesignObjectsForModification(List<IXDiagramObject> diagObjs,
			Set<IXConnectivityObject> allconnObjs)
	{
		for (IXDesign des : designs) {
			Set<IXConnectivityObject> connObjs = new HashSet<IXConnectivityObject>();
			connObjs.addAll(des.getConnectivity().getAssemblies());
			connObjs.addAll(des.getConnectivity().getConnectors());
			connObjs.addAll(des.getConnectivity().getBlocks());
			connObjs.addAll(des.getConnectivity().getDevices());
			connObjs.addAll(des.getConnectivity().getGrounds());
			connObjs.addAll(des.getConnectivity().getHighways());
			connObjs.addAll(des.getConnectivity().getInterconnectDevices());
			connObjs.addAll(des.getConnectivity().getInterconnects());
			connObjs.addAll(des.getConnectivity().getMulticores());
			connObjs.addAll(des.getConnectivity().getNets());
			connObjs.addAll(des.getConnectivity().getShields());
			connObjs.addAll(des.getConnectivity().getSplices());
			connObjs.addAll(des.getConnectivity().getWires());
			if (des instanceof IXHarnessDesign) {
				IXHarness harn = ((IXHarnessDesign) des).getHarness();
				addToConnCollection(harn, connObjs);
			}
			if (des instanceof IXAbstractTopologyDesign) {
				for (IXHarness harn : ((IXAbstractTopologyDesign) des).getHarnesses()) {
					addToConnCollection(harn, connObjs);
					connObjs.addAll(((IXAbstractTopologyDesign) des).getSignals());
					connObjs.addAll(((IXAbstractTopologyDesign) des).getSlots());
				}
			}
			for (IXDiagram diag : des.getDiagrams()) {
				for (IXConnectivityObject connObj : connObjs) {
					diagObjs.addAll(diag.getDiagramObjects(connObj));
				}
			}
			allconnObjs.addAll(connObjs);
		}
	}

	private void addToConnCollection(IXHarness harn, Set<IXConnectivityObject> connObjs)
	{
		connObjs.addAll(harn.getBreakoutTapes());
		connObjs.addAll(harn.getBundles());
		connObjs.addAll(harn.getClips());
		connObjs.addAll(harn.getConnectors());
		connObjs.addAll(harn.getGrommets());
		connObjs.addAll(harn.getInsulationRuns());
		connObjs.addAll(harn.getMultiLocationComponents());
		connObjs.addAll(harn.getNodes());
		connObjs.addAll(harn.getOtherComponents());
		connObjs.addAll(harn.getSpotTapes());
	}
}
