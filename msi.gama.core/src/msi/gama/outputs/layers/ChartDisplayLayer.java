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
package msi.gama.outputs.layers;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import msi.gama.common.interfaces.IKeyword;
import msi.gama.outputs.IDisplayOutput;
import msi.gama.precompiler.GamlAnnotations.facet;
import msi.gama.precompiler.GamlAnnotations.facets;
import msi.gama.precompiler.GamlAnnotations.inside;
import msi.gama.precompiler.GamlAnnotations.symbol;
import msi.gama.precompiler.GamlAnnotations.with_sequence;
import msi.gama.precompiler.*;
import msi.gama.runtime.*;
import msi.gama.runtime.exceptions.GamaRuntimeException;
import msi.gama.util.*;
import msi.gaml.compilation.ISymbol;
import msi.gaml.descriptions.IDescription;
import msi.gaml.expressions.IExpression;
import msi.gaml.factories.DescriptionFactory;
import msi.gaml.operators.*;
import msi.gaml.types.IType;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.*;
import org.jfree.data.xy.*;
import org.jfree.ui.RectangleInsets;

/**
 * Written by drogoul Modified on 9 nov. 2009
 * 
 * @todo Description
 * 
 */
@symbol(name = IKeyword.CHART, kind = ISymbolKind.LAYER)
@inside(symbols = IKeyword.DISPLAY)
@facets(value = {
	/* @facet(name = ISymbol.VALUE, type = TypeManager.STRING_STR, optional = true), */
	@facet(name = IKeyword.POSITION, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.SIZE, type = IType.POINT_STR, optional = true),
	@facet(name = IKeyword.BACKGROUND, type = IType.COLOR_STR, optional = true),
	@facet(name = IKeyword.AXES, type = IType.COLOR_STR, optional = true),
	@facet(name = IKeyword.TYPE, type = IType.ID, values = { IKeyword.XY, IKeyword.HISTOGRAM,
		IKeyword.SERIES, IKeyword.PIE }, optional = true),
	@facet(name = IKeyword.STYLE, type = IType.ID, values = { IKeyword.EXPLODED, IKeyword.THREE_D,
		IKeyword.STACK, IKeyword.BAR }, optional = true),
	@facet(name = IKeyword.TRANSPARENCY, type = IType.FLOAT_STR, optional = true),
	@facet(name = IKeyword.NAME, type = IType.LABEL, optional = false),
	@facet(name = IKeyword.FONT, type = IType.ID, optional = true),
	@facet(name = IKeyword.COLOR, type = IType.COLOR_STR, optional = true) }, omissible = IKeyword.NAME)
@with_sequence
public class ChartDisplayLayer extends AbstractDisplayLayer {

	private static final int SERIES_CHART = 0;
	private static final int HISTOGRAM_CHART = 1;
	private static final int PIE_CHART = 2;
	private static final int XY_CHART = 3;
	private static final String nl = java.lang.System.getProperty("line.separator");
	private int type = SERIES_CHART;
	private String style = IKeyword.DEFAULT;
	private JFreeChart chart = null;
	private StringBuilder history;
	private static String chartFolder = "charts";
	private GamaColor backgroundColor = null, axesColor = null;
	private final Map<String, Integer> expressions_index = new HashMap();
	private Dataset dataset;
	private boolean exploded;
	String xAxisName = "time";
	List<Data> datas;
	final Map<String, Double> lastValues;
	Long lastComputeCycle;
	Data timeSeriesXData = null;

	public JFreeChart getChart() {
		return chart;
	}

	public ChartDisplayLayer(/* final ISymbol context, */final IDescription desc)
		throws GamaRuntimeException {
		super(desc);
		axesColor = Cast.asColor(null, "black");
		lastValues = new HashMap();
		lastComputeCycle = 0l;
	}

	@Override
	public void setChildren(final List<? extends ISymbol> commands) {
		datas = new GamaList();
		for ( ISymbol s : commands ) {
			if ( s instanceof Data ) {
				datas.add((Data) s);
			}
		}
	}

	void createSeries(final IScope scope, final boolean isTimeSeries) throws GamaRuntimeException {
		final XYPlot plot = (XYPlot) chart.getPlot();
		final NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
		if ( isTimeSeries && timeSeriesXData == null ) {
			// set the range axis to display integers only...
			domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
			timeSeriesXData =
				(Data) DescriptionFactory.getModelFactory().compileDescription(
					DescriptionFactory.createDescription(IKeyword.DATA, description, IKeyword.NAME,
						IKeyword.TIME, IKeyword.VALUE, IKeyword.TIME));
			timeSeriesXData.prepare(scope);
			datas.add(0, timeSeriesXData);

		}
		domainAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
		domainAxis.setLabel(datas.get(0).getName());
		final LegendTitle ll = chart.getLegend();
		if ( ll != null ) {
			ll.setItemFont(new Font("SansSerif", Font.PLAIN, 10));
		}

		for ( int i = 0; i < datas.size(); i++ ) {
			Data e = datas.get(i);
			final String legend = e.getName();
			if ( i != 0 ) { // the first data is the domain
				dataset = new DefaultTableXYDataset();
				final XYSeries serie = new XYSeries(legend, false, false);
				((DefaultTableXYDataset) dataset).addSeries(serie);
				expressions_index.put(legend, i);
				plot.setRenderer(i, e.getRenderer(), false);
				final Color c = e.getColor();
				plot.getRenderer(i).setSeriesPaint(0, c);
				plot.setDataset(i, (DefaultTableXYDataset) dataset);
			}
			history.append(legend);
			history.append(',');

		}

		history.deleteCharAt(history.length() - 1);
		history.append(nl);

	}

	private void createData(final IScope scope) throws GamaRuntimeException {
		for ( Data e : datas ) {
			e.prepare(scope);
		}
		switch (type) {
			case SERIES_CHART: {
				createSeries(scope, true);
				break;
			}
			case PIE_CHART: {
				createSlices(scope);
				break;
			}
			case HISTOGRAM_CHART: {
				createBars(scope);
				break;
			}
			case XY_CHART:
				createSeries(scope, false);
				break;
		}
	}

	private void createSlices(final IScope scope) throws GamaRuntimeException {
		int i = 0;
		dataset = new DefaultPieDataset();
		final PiePlot plot = (PiePlot) chart.getPlot();
		for ( final Data e : datas ) {
			final String legend = (String) e.getFacet(IKeyword.NAME).value(scope);
			((DefaultPieDataset) dataset).insertValue(i, legend, null);
			history.append(legend);
			history.append(',');
			i++;
		}
		history.deleteCharAt(history.length() - 1);
		history.append(nl);
		plot.setDataset((DefaultPieDataset) dataset);
		plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0} = {1} ({2})"));
		if ( exploded ) {
			for ( final Object c : ((DefaultPieDataset) dataset).getKeys() ) {
				plot.setExplodePercent((Comparable) c, 0.20);
			}
		}
		plot.setSectionOutlinesVisible(false);
		plot.setLabelFont(new Font("SansSerif", Font.PLAIN, 10));
		plot.setNoDataMessage("No data available yet");
		plot.setCircular(true);
		plot.setLabelGap(0.02);
		plot.setInteriorGap(0);
	}

	private void createBars(final IScope scope) {
		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		dataset = new DefaultCategoryDataset();
		for ( final Data e : datas ) {
			String legend = e.getName();
			((DefaultCategoryDataset) dataset).addValue(0, legend, legend);
			history.append(legend);
			history.append(',');
		}
		history.deleteCharAt(history.length() - 1);
		history.append(nl);
		plot.setDataset((DefaultCategoryDataset) dataset);
		chart.removeLegend();
		final CategoryAxis axis = plot.getDomainAxis();
		// ((BarRenderer3D) plot.getRenderer()).setItemMargin(0.1);
		axis.setCategoryMargin(0.1);
		axis.setUpperMargin(0.05);
		axis.setLowerMargin(0.05);
	}

	private void createChart(final IScope scope) {
		switch (type) {
			case SERIES_CHART: {
				chart =
					ChartFactory.createXYLineChart("", "time", "", null, PlotOrientation.VERTICAL,
						true, false, false);
				break;
			}
			case PIE_CHART: {
				if ( style.equals(IKeyword.THREE_D) ) {
					chart = ChartFactory.createPieChart3D("", null, false, true, false);
				} else if ( style.equals(IKeyword.RING) ) {
					chart = ChartFactory.createRingChart("", null, false, true, false);
				} else if ( style.equals(IKeyword.EXPLODED) ) {
					chart = ChartFactory.createPieChart("", null, false, true, false);
					exploded = true;
				} else {
					chart = ChartFactory.createPieChart("", null, false, true, false);
				}
				break;
			}
			case HISTOGRAM_CHART: {
				if ( style.equals(IKeyword.THREE_D) ) {
					chart =
						ChartFactory.createBarChart3D("", null, null, null,
							PlotOrientation.VERTICAL, true, true, false);
				} else if ( style.equals(IKeyword.STACK) ) {
					chart =
						ChartFactory.createStackedBarChart("", null, null, null,
							PlotOrientation.VERTICAL, true, true, false);
				} else {
					chart =
						ChartFactory.createBarChart("", null, null, null, PlotOrientation.VERTICAL,
							true, true, false);
				}
				break;
			}
			case XY_CHART:
				chart =
					ChartFactory.createXYLineChart("", "", "", null, PlotOrientation.VERTICAL,
						true, false, false);
				break;
		}
		Plot plot = chart.getPlot();
		if ( backgroundColor == null ) {
			plot.setBackgroundPaint(null);
			chart.setBackgroundPaint(null);
			chart.setBorderPaint(null);
			if ( chart.getLegend() != null ) {
				chart.getLegend().setBackgroundPaint(null);
			}
		} else {
			Color bg = backgroundColor;
			chart.setBackgroundPaint(bg);
			plot.setBackgroundPaint(bg);
			chart.setBorderPaint(bg);
			if ( chart.getLegend() != null ) {
				chart.getLegend().setBackgroundPaint(bg);
			}
		}
		// chart.getLegend().setItemPaint(axesColor);
		// chart.getLegend().setBackgroundPaint(null);

		if ( plot instanceof CategoryPlot ) {
			final CategoryPlot pp = (CategoryPlot) chart.getPlot();
			pp.setDomainGridlinePaint(axesColor);
			pp.setRangeGridlinePaint(axesColor);
			// plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
			// plot.setDomainCrosshairVisible(true);
			pp.setRangeCrosshairVisible(true);
		} else if ( plot instanceof XYPlot ) {
			final XYPlot pp = (XYPlot) chart.getPlot();
			pp.setDomainGridlinePaint(axesColor);
			pp.setRangeGridlinePaint(axesColor);
			pp.setDomainCrosshairPaint(axesColor);
			pp.setRangeCrosshairPaint(axesColor);
			pp.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
			pp.setDomainCrosshairVisible(true);
			pp.setRangeCrosshairVisible(true);
		}
	}

	@Override
	public void prepare(final IDisplayOutput out, final IScope scope) throws GamaRuntimeException {
		super.prepare(out, scope);
		history = new StringBuilder();
		IExpression string1 = getFacet(IKeyword.TYPE);
		if ( string1 != null ) {
			String t = Cast.asString(scope, string1.value(scope));
			type =
				IKeyword.SERIES.equals(t) ? SERIES_CHART : IKeyword.HISTOGRAM.equals(t)
					? HISTOGRAM_CHART : IKeyword.PIE.equals(t) ? PIE_CHART : XY_CHART;

		}
		IExpression color = getFacet(IKeyword.AXES);
		if ( color != null ) {
			axesColor = Cast.asColor(scope, color.value(scope));
		}
		IExpression color1 = getFacet(IKeyword.BACKGROUND);
		if ( color1 != null ) {
			backgroundColor = Cast.asColor(scope, color1.value(scope));
		}
		IExpression string = getFacet(IKeyword.STYLE);
		if ( string != null ) {
			style = Cast.asString(scope, string.value(scope));
			// TODO Verifier style;
		}
		createChart(scope);
		createData(scope);
		chart.setNotify(false);
	}

	@Override
	public void compute(final IScope scope, final long cycle) throws GamaRuntimeException {
		super.compute(scope, cycle);
		lastComputeCycle = cycle;
		switch (type) {
			case XY_CHART:
			case SERIES_CHART:
				computeSeries(scope, cycle);
				return;
		}

		for ( final Data d : datas ) {
			lastValues.put(d.getName(), d.getValue(scope));
		}
		for ( final Map.Entry<String, Double> d : lastValues.entrySet() ) {
			String s = d.getKey();
			final double n = d.getValue();
			switch (type) {
				case PIE_CHART: {
					((DefaultPieDataset) dataset).setValue(s, n);
					break;
				}
				case HISTOGRAM_CHART: {
					((DefaultCategoryDataset) dataset).setValue(n, s, s);
					break;
				}
			}
			history.append(n);
			history.append(',');
		}
		history.deleteCharAt(history.length() - 1);
		history.append(nl);
	}

	/**
	 * @throws GamaRuntimeException
	 * @param scope
	 * @param cycle
	 */
	private void computeSeries(final IScope scope, final long cycle) throws GamaRuntimeException {
		double x = datas.get(0).getValue(scope);
		history.append(x);
		history.append(',');
		for ( int i = 1; i < datas.size(); i++ ) {
			XYPlot plot = (XYPlot) chart.getPlot();
			DefaultTableXYDataset data = (DefaultTableXYDataset) plot.getDataset(i);
			XYSeries serie = data.getSeries(0);
			double n = datas.get(i).getValue(scope);
			serie.addOrUpdate(x, n);
			history.append(n);
			history.append(',');
		}
		history.deleteCharAt(history.length() - 1);
		history.append(nl);

	}

	@Override
	public short getType() {
		return IDisplayLayer.CHART;
	}

	@Override
	public void dispose() {
		chart = null;
		super.dispose();
	}

	public void saveHistory() {
		IScope scope = GAMA.obtainNewScope();
		if ( scope == null ) { return; }
		try {
			Files.newFolder(scope, chartFolder);
			String file = chartFolder + "/" + "chart_" + getName() + ".csv";
			BufferedWriter bw;
			file = scope.getSimulationScope().getModel().getRelativeFilePath(file, false);
			bw = new BufferedWriter(new FileWriter(file));
			bw.append(history);
			bw.close();
		} catch (final Exception e) {
			e.printStackTrace();
			return;
		} finally {
			GAMA.releaseScope(scope);
		}
	}

}
