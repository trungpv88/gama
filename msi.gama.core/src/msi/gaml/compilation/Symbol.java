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
package msi.gaml.compilation;

import java.util.List;
import msi.gama.runtime.IScope;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;

/**
 * Written by drogoul Modified on 13 mai 2010 A simple class to serve as the root of all Gaml
 * Symbols
 * 
 * @todo Description
 * 
 */
public abstract class Symbol implements ISymbol {

	protected String name;
	protected final IDescription description;

	@Override
	public IDescription getDescription() {
		return description;
	}

	public Symbol(final IDescription description) {
		this.description = description;
		initFields();
	}

	protected void initFields() {}

	@Override
	public IExpression getFacet(final String key) {
		if ( description == null ) { return null; }
		if ( !description.getFacets().containsKey(key) ) { return null; }
		IExpression result = description.getFacets().getExpr(key);
		if ( result == null ) {
			error("Facet " + key + " could not be compiled.");
		}
		return result;
	}

	public IExpression getFacet(final String key, final IExpression ifAbsent) {
		return description == null ? ifAbsent : description.getFacets().getExpr(key, ifAbsent);
	}

	public Object getFacetValue(final IScope scope, final String key) throws GamaRuntimeException {
		return getFacetValue(scope, key, null);
	}

	public Object getFacetValue(final IScope scope, final String key, final Object defaultValue)
		throws GamaRuntimeException {
		IExpression exp = getFacet(key);
		return exp == null ? defaultValue : exp.value(scope);
	}

	public String getLiteral(final String key) {
		return getLiteral(key, null);
	}

	public String getLiteral(final String key, final String defaultValue) {
		IExpression exp = getFacet(key);
		return exp == null ? defaultValue : exp.literalValue();
	}

	protected void setFacet(final String key, final IExpression expr) {
		if ( description == null ) { return; }
		description.getFacets().put(key, expr);
	}

	@Override
	public boolean hasFacet(final String s) {
		return getFacet(s) != null;
	}

	@Override
	public abstract void setChildren(final List<? extends ISymbol> commands);

	@Override
	public void setName(final String n) {
		name = n;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void dispose() {}

	@Override
	public void error(final String s) /* throws GamlException */{
		error(s, null);
	}

	@Override
	public void error(final String s, final String facet) /* throws GamlException */{
		if ( description != null || description.getSourceInformation() != null ) {
			description.flagError(s, facet);
		} else {
			throw new GamaRuntimeException(s);
			// throw e;
		}
	}

	@Override
	public void warning(final String s, final String facet) /* throws GamlException */{
		if ( description != null || description.getSourceInformation() != null ) {
			description.flagWarning(s, facet);
		} else {
			// throw e;
			throw new GamaRuntimeException(s);
		}
	}

}
