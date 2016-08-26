package org.openxava.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.prefs.BackingStoreException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openxava.model.meta.MetaProperty;
import org.openxava.session.Chart;
import org.openxava.session.ChartColumn;
import org.openxava.tab.Tab;
import org.openxava.util.Is;
import org.openxava.util.Locales;
import org.openxava.util.XavaException;
import org.openxava.view.View;

/**
 * Charts helper.
 * 
 * @author Federico Alcantara
 * @author Javier Paniza
 */
public class Charts { 

	private static final Log log = LogFactory.getLog(Charts.class);
	public static final String CHART_DATA_SEPARATOR = ":"; 
	
	/**
	 * Update Actions
	 * @param request Originating request.
	 * @param view Current view
	 * @param tab current tab.
	 * @param chart Current chart.
	 * @throws XavaException
	 * @throws BackingStoreException
	 */
	public static void updateView(HttpServletRequest request, View view, Tab tab, Chart chart) throws XavaException, BackingStoreException {
		if (!chart.isRendered()) {
			chart.setRendered(true);
			MetaProperty labelMetaProperty = null;
			String name = view.getValueString("name");
			view.setModel(chart);
			try {
				labelMetaProperty = tab.getMetaTab().getMetaModel().getMetaProperty(chart.getxColumn());
			} catch (Exception e) {
				log.debug(e.getMessage());
			}
			view.setValue("xColumn", chart.getxColumn());
			view.setValue("chartType", chart.getChartType());
			view.setValueNotifying("chartData", chart.getChartType().jsType()
					 + CHART_DATA_SEPARATOR 
					 + chart.getChartType().grouped()
					 + CHART_DATA_SEPARATOR
					 + chart.getChartType().name()
					 + CHART_DATA_SEPARATOR
					 + (labelMetaProperty != null ? labelMetaProperty.isNumber() : "false")
					 + CHART_DATA_SEPARATOR
					 + (labelMetaProperty != null ? labelMetaProperty.getLabel() : "")
					 + CHART_DATA_SEPARATOR
					 + (new Date()).getTime()
					 );
			createTab(request, tab, chart);
			view.refreshCollections();
			chart.setRendered(false);
		}
	}
		
	/**
	 * Populates the tab for chart displaying purposes.
	 * @param request Originating request.
	 * @param tab Current tab.
	 * @param chart Chart.
	 */
	private static void createTab(HttpServletRequest request, Tab tab, Chart chart)  {		
		Tab chartTab = new Tab(true);
		chartTab.setRequest(request);
		chartTab.setModelName(tab.getModelName());
		chartTab.setTabName(tab.getTabName());
		chartTab.clearProperties();
		chartTab.clearCondition();
		Collection<String> comparators = new ArrayList<String>();
		Collection<String> values = new ArrayList<String>();
		StringBuffer order = new StringBuffer();
		for (ChartColumn column: chart.getColumns()) {
			addColumn(chartTab, comparators, values, order, column);
		}
		
		for (ChartColumn column : chart.getColumns()) { 
			addColumn(chartTab, comparators, values, order, column);
		}
		
		if (!Is.emptyString(chart.getxColumn())) {
			ChartColumn column = new ChartColumn();
			MetaProperty property = tab.getMetaTab().getMetaModel().getMetaProperty(chart.getxColumn());
			if (property != null) {
				column.setChart(chart);
				column.setName(chart.getxColumn());				
				addColumn(chartTab, comparators, values, order, column);
			}
		}

		if (order.length() > 0) {
			chartTab.setDefaultOrder(order.toString());			
		}		
		chartTab.setConditionComparators(comparators);
		chartTab.setConditionValues(values);
		request.getSession().setAttribute("xava_chartTab", chartTab); 
	}
	
	public static void release(HttpServletRequest request) throws XavaException, BackingStoreException {
		request.getSession().removeAttribute("xava_chartTab");
	}
	

	/**
	 * Adds a column to the given tab.
	 * @param tab Container tab.
	 * @param comparators List of comparators.
	 * @param values List of values.
	 * @param order String defining the order for the columns.
	 * @param column Column to be added.
	 */	
	private static void addColumn(Tab tab, Collection<String> comparators,
			Collection<String> values, StringBuffer order,
			ChartColumn column) 
	{
		if (!("," + tab.getPropertiesNamesAsString() + ",").contains("," + column.getName() + ",")) {
			tab.addProperty(column.getName());			
			tab.setLabel(column.getName(), column.getLabel());
		}
	}
	
}
