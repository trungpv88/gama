/*
 * GAMA - V1.4 http://gama-platform.googlecode.com
 * 
 * (c) 2007-2011 UMI 209 UMMISCO IRD/UPMC & Partners (see below)
 * 
 * Developers :
 * 
 * - Alexis Drogoul, UMI 209 UMMISCO, IRD/UPMC (Kernel, Metamodel, GAML), 2007-2012
 * - Vo Duc An, UMI 209 UMMISCO, IRD/UPMC (SWT, multi-level architecture), 2008-2012
 * - Patrick Taillandier, UMR 6228 IDEES, CNRS/Univ. Rouen (Batch, GeoTools & JTS), 2009-2012
 * - Beno�t Gaudou, UMR 5505 IRIT, CNRS/Univ. Toulouse 1 (Documentation, Tests), 2010-2012
 * - Phan Huy Cuong, DREAM team, Univ. Can Tho (XText-based GAML), 2012
 * - Pierrick Koch, UMI 209 UMMISCO, IRD/UPMC (XText-based GAML), 2010-2011
 * - Romain Lavaud, UMI 209 UMMISCO, IRD/UPMC (RCP environment), 2010
 * - Francois Sempe, UMI 209 UMMISCO, IRD/UPMC (EMF model, Batch), 2007-2009
 * - Edouard Amouroux, UMI 209 UMMISCO, IRD/UPMC (C++ initial porting), 2007-2008
 * - Chu Thanh Quang, UMI 209 UMMISCO, IRD/UPMC (OpenMap integration), 2007-2008
 */
package msi.gama.metamodel.topology.continuous;

import java.awt.Graphics2D;
import java.util.Iterator;
import msi.gama.common.util.GisUtils;
import msi.gama.metamodel.agent.IAgent;
import msi.gama.metamodel.population.IPopulation;
import msi.gama.metamodel.shape.*;
import msi.gama.metamodel.topology.*;
import msi.gama.metamodel.topology.filter.IAgentFilter;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gama.util.path.*;
import msi.gaml.operators.*;
import msi.gaml.types.GamaGeometryType;
import com.google.common.collect.Iterators;
import com.vividsolutions.jts.geom.Geometry;

/**
 * The class ExpandableTopology.
 * 
 * @author drogoul
 * @since 2 d�c. 2011
 * 
 */
public class AmorphousTopology implements ITopology {

	IShape expandableEnvironment = GamaGeometryType.createPoint(new GamaPoint(0, 0));
	ISpatialIndex index = new GamaQuadTree(expandableEnvironment.getEnvelope());

	/**
	 * @see msi.gama.interfaces.IValue#type()
	 */
	// @Override
	// public IType type() {
	// return Types.get(IType.TOPOLOGY);
	// }

	/**
	 * @see msi.gama.interfaces.IValue#stringValue()
	 */
	@Override
	public String stringValue(final IScope scope) throws GamaRuntimeException {
		return "Expandable topology";
	}

	/**
	 * @see msi.gama.interfaces.IValue#toGaml()
	 */
	@Override
	public String toGaml() {
		return "topology({0,0})";
	}

	/**
	 * @see msi.gama.interfaces.IValue#copy()
	 */
	@Override
	public ITopology copy(final IScope scope) throws GamaRuntimeException {
		return new AmorphousTopology();
	}

	/**
	 * @see msi.gama.environment.ITopology#initialize(msi.gama.interfaces.IPopulation)
	 */
	@Override
	public void initialize(final IScope scope, final IPopulation pop) throws GamaRuntimeException {}

	/**
	 * @see msi.gama.environment.ITopology#updateAgent(msi.gama.interfaces.IAgent, boolean, msi.gama.util.GamaPoint,
	 *      com.vividsolutions.jts.geom.Envelope)
	 */
	// @Override
	// public void updateAgent(final IAgent agent, final boolean previousShapeIsPoint,
	// final ILocation previousLoc, final Envelope previousEnv) {
	// IShape ng =
	// Spatial.Operators.union(expandableEnvironment.getGeometry(), agent.getGeometry());
	// expandableEnvironment.setGeometry(new GamaShape(ng.getInnerGeometry().getEnvelope()));
	// }
	//
	@Override
	public void updateAgent(final IShape previous, final IShape agent) {
		final IShape ng = Spatial.Operators.union(expandableEnvironment.getGeometry(), agent.getGeometry());
		expandableEnvironment.setGeometry(new GamaShape(ng.getInnerGeometry().getEnvelope()));
	}

	/**
	 * @see msi.gama.environment.ITopology#removeAgent(msi.gama.interfaces.IAgent)
	 */
	@Override
	public void removeAgent(final IAgent agent) {}

	/**
	 * @see msi.gama.environment.ITopology#getAgentClosestTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.environment.IAgentFilter)
	 */
	@Override
	public IAgent getAgentClosestTo(final IScope scope, final IShape source, final IAgentFilter filter) {
		return null;
	}

	/**
	 * @see msi.gama.environment.ITopology#getNeighboursOf(msi.gama.interfaces.IGeometry, java.lang.Double,
	 *      msi.gama.environment.IAgentFilter)
	 */
	@Override
	public Iterator<IAgent> getNeighboursOf(final IScope scope, final IShape source, final Double distance,
		final IAgentFilter filter) throws GamaRuntimeException {
		return Iterators.emptyIterator();
	}

	/**
	 * @see msi.gama.environment.ITopology#getAgentsIn(msi.gama.interfaces.IGeometry, msi.gama.environment.IAgentFilter,
	 *      boolean)
	 */
	@Override
	public Iterator<IAgent> getAgentsIn(final IScope scope, final IShape source, final IAgentFilter f,
		final boolean covered) {
		return Iterators.emptyIterator();
	}

	/**
	 * @see msi.gama.environment.ITopology#distanceBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Double distanceBetween(final IScope scope, final IShape source, final IShape target) {
		return source.euclidianDistanceTo(target);
	}

	@Override
	public Double distanceBetween(final IScope scope, final ILocation source, final ILocation target) {
		return source.euclidianDistanceTo(target);
	}

	/**
	 * @see msi.gama.environment.ITopology#pathBetween(msi.gama.interfaces.IGeometry, msi.gama.interfaces.IGeometry)
	 */
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final IShape source, final IShape target)
		throws GamaRuntimeException {
		// return new GamaPath(this, GamaList.with(source, target));
		return PathFactory.newInstance(this, GamaList.with(source, target));
	}

	/**
	 * @see msi.gama.environment.ITopology#getDestination(msi.gama.util.GamaPoint, int, double, boolean)
	 */
	@Override
	public ILocation getDestination(final ILocation source, final int direction, final double distance,
		final boolean nullIfOutside) {
		final double cos = distance * Maths.cos(direction);
		final double sin = distance * Maths.sin(direction);
		return new GamaPoint(source.getX() + cos, source.getY() + sin);

	}

	/**
	 * @see msi.gama.environment.ITopology#getRandomLocation()
	 */
	@Override
	public GamaPoint getRandomLocation() {
		return new GamaPoint(GAMA.getRandom().next(), GAMA.getRandom().next());
	}

	/**
	 * @see msi.gama.environment.ITopology#getPlaces()
	 */
	@Override
	public IContainer<?, IShape> getPlaces() {
		return GamaList.with(expandableEnvironment);
	}

	/**
	 * @see msi.gama.environment.ITopology#getEnvironment()
	 */
	@Override
	public IShape getEnvironment() {
		return expandableEnvironment;
	}

	/**
	 * @see msi.gama.environment.ITopology#normalizeLocation(msi.gama.util.GamaPoint, boolean)
	 */
	@Override
	public ILocation normalizeLocation(final ILocation p, final boolean nullIfOutside) {
		return p;
	}

	/**
	 * @see msi.gama.environment.ITopology#shapeChanged(msi.gama.interfaces.IPopulation)
	 */
	@Override
	public void shapeChanged(final IPopulation pop) {}

	/**
	 * @see msi.gama.environment.ITopology#getWidth()
	 */
	@Override
	public double getWidth() {
		return expandableEnvironment.getEnvelope().getWidth();
	}

	/**
	 * @see msi.gama.environment.ITopology#getHeight()
	 */
	@Override
	public double getHeight() {
		return expandableEnvironment.getEnvelope().getHeight();
	}

	/**
	 * @see msi.gama.environment.ITopology#dispose()
	 */
	@Override
	public void dispose() {}

	/**
	 * @see msi.gama.environment.ITopology#isValidLocation(msi.gama.util.GamaPoint)
	 */
	@Override
	public boolean isValidLocation(final ILocation p) {
		return true;
	}

	/**
	 * @see msi.gama.environment.ITopology#isValidGeometry(msi.gama.interfaces.IGeometry)
	 */
	@Override
	public boolean isValidGeometry(final IShape g) {
		return true;
	}

	/**
	 * @see msi.gama.environment.ITopology#directionInDegreesTo(msi.gama.interfaces.IGeometry,
	 *      msi.gama.interfaces.IGeometry)
	 */
	@Override
	public Integer directionInDegreesTo(final IScope scope, final IShape g1, final IShape g2) {
		final ILocation source = g1.getLocation();
		final ILocation target = g2.getLocation();
		final double x2 = /* translateX(source.x, target.x); */target.getX();
		final double y2 = /* translateY(source.y, target.y); */target.getY();
		final double dx = x2 - source.getX();
		final double dy = y2 - source.getY();
		final double result = Maths.atan2Opt(dy, dx);
		return Maths.checkHeading((int) result);
	}

	/**
	 * @see msi.gama.metamodel.topology.ITopology#getAgentClosestTo(msi.gama.metamodel.shape.ILocation,
	 *      msi.gama.metamodel.topology.filter.IAgentFilter)
	 */
	@Override
	public IAgent getAgentClosestTo(final IScope scope, final ILocation source, final IAgentFilter filter) {
		return null;
	}

	/**
	 * @see msi.gama.metamodel.topology.ITopology#getNeighboursOf(msi.gama.metamodel.shape.ILocation, java.lang.Double,
	 *      msi.gama.metamodel.topology.filter.IAgentFilter)
	 */
	// @Override
	// protected Iterator<IAgent> getNeighboursOf(final ILocation source, final Double distance, final IAgentFilter
	// filter)
	// throws GamaRuntimeException {
	// return Iterators.emptyIterator();
	// }

	/**
	 * @see msi.gama.metamodel.topology.ITopology#pathBetween(msi.gama.metamodel.shape.ILocation,
	 *      msi.gama.metamodel.shape.ILocation)
	 */
	@Override
	public GamaSpatialPath pathBetween(final IScope scope, final ILocation source, final ILocation target)
		throws GamaRuntimeException {
		// return new GamaPath(this, GamaList.with(source, target));
		return PathFactory.newInstance(this, GamaList.with(source, target));
	}

	@Override
	public Geometry returnToroidalGeom(final Geometry geom) {
		return null;
	}

	@Override
	public boolean isTorus() {
		return false;
	}

	@Override
	public GisUtils getGisUtils() {
		return new GisUtils();
	}

	@Override
	public ISpatialIndex getSpatialIndex() {
		return index;
	}

	@Override
	public void displaySpatialIndexOn(final Graphics2D g2, final int width, final int height) {}

}
