package com.example.plugin.drc;

import com.mentor.chs.api.IXAttributes;
import com.mentor.chs.api.IXBundle;
import com.mentor.chs.api.IXDiagram;
import com.mentor.chs.api.IXDiagramObject;
import com.mentor.chs.api.IXGraphicDatum;
import com.mentor.chs.api.IXGraphicSymbol;
import com.mentor.chs.api.IXHarnessDesign;
import com.mentor.chs.api.IXHarnessDiagramBundle;
import com.mentor.chs.api.IXNode;
import com.mentor.chs.api.IXObject;
import com.mentor.chs.api.IXOtherComponent;
import com.mentor.chs.api.IXStructureNode;
import com.mentor.chs.plugin.IXApplicationContext;
import com.mentor.chs.plugin.IXApplicationContextListener;
import com.mentor.chs.plugin.drc.IXDRCViolationReporter;
import com.mentor.chs.plugin.drc.IXDRCheckAdvancedConfiguration;
import com.mentor.chs.plugin.drc.IXHarnessDRCheck;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SymbolOrientationValidatorDRCheck extends BaseDRCheck
		implements IXHarnessDRCheck, IXApplicationContextListener, IXDRCheckAdvancedConfiguration
{

	private static final double FULL_ANGLE = 360.0;
	public static final String CENTER = "CENTER";
	public static final String DATUM_ENTRY_PORT_PREFIX1 = "EP :";
	public static final String DATUM_ENTRY_PORT_PREFIX2 = "EP:";
	public static final String BUNDLE_ENTRY_PORT_PREFIX = "EP_";
	public static final double DATUM_ORIENTATION_TOLERANCE = 10.0;
	public static final double ORIENTATION_COMPARE_TOLERANCE = 15.0;
	public static final double DEGREES_0 = 0.0;
	public static final double DEGREES_90 = 90.0;
	public static final double DEGREES_180 = 180.0;
	public static final double DEGREES_270 = 270.0;
	public static final double DEGREES_360 = 360.0;
	public static final String SYMBOL_INCORRECTLY_ORIENTED_MSG = "Symbol for {0} is incorrectly oriented.";
	protected IXApplicationContext applicationContext;

	private Map<IXGraphicDatum, Double> mapDatumOrientation = null;
	private Map<IXGraphicDatum, List<String>> mapDatumEntryPorts = null;
	private List<IXGraphicDatum> orderedSymbolDatums = null;

	private Map<IXHarnessDiagramBundle, Double> mapBundleOrientation = null;
	private Map<IXHarnessDiagramBundle, String> mapBundleEntryPorts = null;
	private List<IXHarnessDiagramBundle> orderedBundles = null;

	private static boolean DEBUG = false;

	public SymbolOrientationValidatorDRCheck()
	{
		super("Validate symbol orientation at constrained junctions", "1.0",
		   "Ensure that symbols are oriented correctly at the junctions where the bundles have flattening constraints.",
		   true, Severity.Error);
	}

	@Override public void setApplicationContext(IXApplicationContext applicationContext)
	{
		this.applicationContext = applicationContext;
	}

	@Override public void begin(IXDRCViolationReporter reporter)
	{
	}

	@Override public void check(IXDRCViolationReporter reporter, IXObject object)
	{
		if (!(object instanceof IXOtherComponent)) {
			return;
		}

		//Dont consider other components which are placed on reference nodes
		IXOtherComponent xOtherComponent = (IXOtherComponent) object;
		if (!(xOtherComponent.getNode() instanceof IXStructureNode)) {
			return;
		}

		checkSymbolOrientationAtComponent(reporter, xOtherComponent);
	}

	private void checkSymbolOrientationAtComponent(IXDRCViolationReporter reporter, IXOtherComponent xOtherComponent)
	{
		doCleanUp();

		IXHarnessDesign design = (IXHarnessDesign) xOtherComponent.getDesign();
		Set<IXDiagram> diagramList = design.getDiagrams();
		for (IXDiagram diagram : diagramList) {
			Set<IXDiagramObject> representationList = diagram.getDiagramObjects(xOtherComponent);
			IXDiagramObject representation = representationList.iterator().next();

			Set<IXGraphicSymbol> symbolsList = representation.getAssociatedObjects(IXGraphicSymbol.class);
			if (symbolsList.isEmpty()) {
				continue;
			}
			IXGraphicSymbol symbol = symbolsList.iterator().next();
			orderedSymbolDatums = getOrderedSymbolDatums(symbol);

			if (DEBUG) {
				StringBuilder symbolDatumOrder = new StringBuilder();
				for (IXGraphicDatum datum : orderedSymbolDatums) {
					for (String datumName : mapDatumEntryPorts.get(datum)) {
						symbolDatumOrder.append(datumName).append(", ");
					}
				}
				applicationContext.getOutputWindow().println("Symbol Datums Order: " + symbolDatumOrder);
			}

			if (orderedSymbolDatums != null && !orderedSymbolDatums.isEmpty()) {
				orderedBundles = getOrderedBundlesAtJunction(xOtherComponent, diagram);
				if (orderedBundles != null) {
					int nIndexToCompareWith = 0;
					List<String> validEntryPorts = mapDatumEntryPorts.get(orderedSymbolDatums.get(nIndexToCompareWith));
					boolean foundMatchingBundle = false;
					for (IXHarnessDiagramBundle bundle : orderedBundles) {
						String bundleEntryPort = mapBundleEntryPorts.get(bundle);
						if (!validEntryPorts.contains(bundleEntryPort)) {
							if (foundMatchingBundle) {
								nIndexToCompareWith++;
								validEntryPorts = mapDatumEntryPorts.get(orderedSymbolDatums.get(nIndexToCompareWith));
								foundMatchingBundle = validEntryPorts.contains(bundleEntryPort);
							}

							if (!foundMatchingBundle) {
								reporter.report(Severity.Error, SYMBOL_INCORRECTLY_ORIENTED_MSG, representation);
								applicationContext.getOutputWindow().activateTabPanel("Check");
								break;
							}
						}
						double angleDiff = Math.abs(mapBundleOrientation.get(bundle) -
								mapDatumOrientation.get(orderedSymbolDatums.get(nIndexToCompareWith)));
						if (angleDiff > getOrientationTolerance()) {
							reporter.report(Severity.Error, SYMBOL_INCORRECTLY_ORIENTED_MSG, representation);
							applicationContext.getOutputWindow().activateTabPanel("Check");
							break;
						}

						foundMatchingBundle = true;
					}
				}
			}
		}
	}

	@Override public void end(IXDRCViolationReporter reporter)
	{
		doCleanUp();
	}

	private void doCleanUp()
	{
		clear(mapDatumOrientation);
		clear(mapBundleEntryPorts);
		clear(orderedSymbolDatums);
		clear(mapBundleOrientation);
		clear(mapBundleEntryPorts);
		clear(orderedBundles);
	}

	@Override
	public boolean getAvailability(RunningMode runningMode, String designAbstraction)
	{
		return (runningMode == RunningMode.MANUAL || runningMode == RunningMode.BACKGROUND );
	}

	@Override public Severity getSeverity(RunningMode runningMode, String designAbstraction)
	{
		return Severity.Error;
	}

	protected double getOrientationTolerance()
	{
		return ORIENTATION_COMPARE_TOLERANCE;
	}

	private List<IXGraphicDatum> getOrderedSymbolDatums(IXGraphicSymbol symbol)
	{
		if (symbol != null) {
			Set<IXGraphicDatum> datumsList = symbol.getAssociatedObjects(IXGraphicDatum.class);
			IXGraphicDatum centralDatum = getDatumByName(datumsList, CENTER);
			if (centralDatum != null) {
				mapDatumOrientation = new HashMap<IXGraphicDatum, Double>();
				for (IXGraphicDatum datum : datumsList) {
					if (datum == centralDatum) {
						continue;
					}

					String datumName = getDatumName(datum);
					if (!datumName.startsWith(DATUM_ENTRY_PORT_PREFIX1) &&
							!datumName.startsWith(DATUM_ENTRY_PORT_PREFIX2)) {
						continue;
					}

					addDatumEntryPorts(datum);
					Rectangle2D centralDatumExtent = centralDatum.getAbsoluteExtent();
					Point2D centralDatumLoc =
							new Point2D.Double(centralDatumExtent.getCenterX(), centralDatumExtent.getCenterY());

					Rectangle2D currentDatumExtent = datum.getAbsoluteExtent();
					Point2D currentDatumLoc =
							new Point2D.Double(currentDatumExtent.getCenterX(), currentDatumExtent.getCenterY());
					double orientation = getOrientation(centralDatumLoc, currentDatumLoc);
					orientation = round(orientation);
					mapDatumOrientation.put(datum, orientation);
				}

				List<IXGraphicDatum> sortedSymbolDatums =
						new ArrayList<IXGraphicDatum>(mapDatumOrientation.keySet());
				Collections.sort(sortedSymbolDatums, new DatumOrientationComparator());
				return sortedSymbolDatums;
			}
		}
		return null;
	}

	private double round(double orientation)
	{
		if ((Double.compare(orientation, 10.0) <= 0) ||
				(Double.compare(orientation, 350.0) > 0)) {
			return 0.0;
		}
		if ((Double.compare(orientation, 80.0) >= 0) &&
				(Double.compare(orientation, 100.0) <= 0)) {
			return 90.0;
		}
		if ((Double.compare(orientation, 170.0) >= 0) &&
				(Double.compare(orientation, 190.0) <= 0)) {
			return 180.0;
		}
		if ((Double.compare(orientation, 260.0) >= 0) &&
				(Double.compare(orientation, 280.0) <= 0)) {
			return 270.0;
		}

		return orientation;
	}

	private List<IXHarnessDiagramBundle> getOrderedBundlesAtJunction(IXOtherComponent component, IXDiagram diagram)
	{
		IXNode componentNode = component.getNode();
		IXDiagramObject diagramNode = diagram.getDiagramObjects(componentNode).iterator().next();
		Set<IXBundle> bundlesAtNode = componentNode.getBundles();
		mapBundleOrientation = new HashMap<IXHarnessDiagramBundle, Double>();
		for (IXBundle bundle : bundlesAtNode) {
			IXHarnessDiagramBundle diagramBundle =
					(IXHarnessDiagramBundle) diagram.getDiagramObjects(bundle).iterator().next();
			addBundleEntryPorts(diagramBundle, component);
			for (IXDiagramObject diagramObject : diagramBundle.getAssociatedObjects(IXDiagramObject.class)) {
				Rectangle2D bundleExtent = diagramObject.getAbsoluteExtent();
				Rectangle2D nodeExtent = diagramNode.getAbsoluteExtent();
				double extentModificationDelta = nodeExtent.getWidth() / 2;
				double x1 = nodeExtent.getCenterX();
				double y1 = nodeExtent.getCenterY();
				Rectangle2D modifiedBundleExtent = modifyExtent(bundleExtent, extentModificationDelta);
				if (modifiedBundleExtent.contains(x1, y1)) {
					double x2 = bundleExtent.getCenterX();
					double y2 = bundleExtent.getCenterY();
					double bundleAngle = getOrientation(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
					mapBundleOrientation.put(diagramBundle, bundleAngle);
					break;
				}
			}
		}
		List<IXHarnessDiagramBundle> sortedBundles =
				new ArrayList<IXHarnessDiagramBundle>(mapBundleOrientation.keySet());
		Collections.sort(sortedBundles, new BundleOrientationComparator());
		return sortedBundles;
	}

	private IXGraphicDatum getDatumByName(Set<IXGraphicDatum> datumsList, String datumName)
	{
		for (IXGraphicDatum datum : datumsList) {
			if (datum.getAttribute(IXAttributes.Name).equals(datumName)) {
				return datum;
			}
		}
		return null;
	}

	private String getDatumName(IXGraphicDatum datum)
	{
		return (datum.getAttribute(IXAttributes.Name));
	}

	private void addDatumEntryPorts(IXGraphicDatum datum)
	{
		if (mapDatumEntryPorts == null) {
			mapDatumEntryPorts = new HashMap<IXGraphicDatum, List<String>>();
		}
		if (!mapDatumEntryPorts.containsKey(datum)) {
			String datumName = getDatumName(datum);
			if (datumName.startsWith(DATUM_ENTRY_PORT_PREFIX1)) {
				datumName = datumName.replaceFirst(DATUM_ENTRY_PORT_PREFIX1, "").trim();
			}
			else if (datumName.startsWith(DATUM_ENTRY_PORT_PREFIX2)) {
				datumName = datumName.replaceFirst(DATUM_ENTRY_PORT_PREFIX2, "").trim();
			}
			String[] entryPortsArray = datumName.split(",");
			List<String> entryPortsList = new ArrayList<String>();
			for (String entryPort : entryPortsArray) {
				entryPortsList.add(entryPort.trim());
			}
			mapDatumEntryPorts.put(datum, entryPortsList);
		}
	}

	private void addBundleEntryPorts(IXHarnessDiagramBundle bundle, IXOtherComponent component)
	{
		if (mapBundleEntryPorts == null) {
			mapBundleEntryPorts = new HashMap<IXHarnessDiagramBundle, String>();
		}
		if (!mapBundleEntryPorts.containsKey(bundle)) {
			IXBundle physicalBundle = (IXBundle) bundle.getConnectivity();
			String propertyName = BUNDLE_ENTRY_PORT_PREFIX + component.getAttribute(IXAttributes.Name);
			String entryPortName = physicalBundle.getProperty(propertyName);
			mapBundleEntryPorts.put(bundle, (entryPortName != null) ? entryPortName.trim() : entryPortName);
		}
	}


	private double getOrientation(Point2D location1, Point2D location2)
	{
		double x1 = location1.getX();
		double y1 = location1.getY();
		double x2 = location2.getX();
		double y2 = location2.getY();
		double bundleAngle = StrictMath.atan2((y2 - y1), (x2 - x1));
		bundleAngle = (FULL_ANGLE + StrictMath.toDegrees(bundleAngle)) % FULL_ANGLE;

		return StrictMath.round(bundleAngle);
	}

	private Rectangle2D modifyExtent(Rectangle2D extent, double factor)
	{
		double x1 = Math.min(extent.getMinX(), extent.getMinX() - factor);
		double x2 = Math.max(extent.getMaxX(), extent.getMaxX() + factor);
		double y1 = Math.min(extent.getMinY(), extent.getMinY() - factor);
		double y2 = Math.max(extent.getMaxY(), extent.getMaxY() + factor);
		Rectangle2D modifiedExtent = new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
		return modifiedExtent;
	}

	private <T> void clear(Collection<T> collection)
	{
		if (collection != null) {
			collection.clear();
		}
	}

	private <K, V> void clear(Map<K, V> map)
	{
		if (map != null) {
			map.clear();
		}
	}

	protected class DatumOrientationComparator implements Comparator<IXGraphicDatum>
	{
		DatumOrientationComparator()
		{
		}

		@Override public int compare(IXGraphicDatum o1, IXGraphicDatum o2)
		{
			return Double.compare(getOrientation(o1), getOrientation(o2)) ;
		}

		protected Double getOrientation(IXGraphicDatum datum)
		{
			Double orientation = 0.0;
			if (mapDatumOrientation != null) {
				orientation = mapDatumOrientation.get(datum);
			}

			return (orientation == null) ? 0.0 : orientation;
		}
	}

	protected class BundleOrientationComparator implements Comparator<IXHarnessDiagramBundle>
	{
		BundleOrientationComparator()
		{
		}

		@Override public int compare(IXHarnessDiagramBundle o1, IXHarnessDiagramBundle o2)
		{
			return Double.compare(getOrientation(o1), getOrientation(o2)) ;
		}

		protected Double getOrientation(IXHarnessDiagramBundle bundle)
		{
			Double orientation = 0.0;
			if (mapBundleOrientation!= null) {
				orientation = mapBundleOrientation.get(bundle);
			}

			return (orientation == null) ? 0.0 : orientation;
		}
	}
}
