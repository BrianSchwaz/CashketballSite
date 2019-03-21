/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import static com.sun.tools.xjc.reader.Ring.add;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.BarChartSeries;
import org.primefaces.model.chart.BubbleChartModel;
import org.primefaces.model.chart.BubbleChartSeries;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.DonutChartModel;
import org.primefaces.model.chart.HorizontalBarChartModel;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;
import org.primefaces.model.chart.LinearAxis;
import org.primefaces.model.chart.MeterGaugeChartModel;
import org.primefaces.model.chart.OhlcChartModel;
import org.primefaces.model.chart.OhlcChartSeries;
import org.primefaces.model.chart.PieChartModel;

/**
 *
 * @author bkschwar
 */
@ManagedBean
public class ChartView implements Serializable {
    private LineChartModel animatedModel1;
    private BarChartModel animatedModel2;
    private List<LineChartModel> gamesModels;

    public List<LineChartModel> getGamesModels() {
        return gamesModels;
    }

    public void setGamesModels(List<LineChartModel> gamesModels) {
        this.gamesModels = gamesModels;
    }

    public LineChartModel getDateModel() {
        return dateModel;
    }

    public void setDateModel(LineChartModel dateModel) {
        this.dateModel = dateModel;
    }
    private LineChartModel dateModel;
 
    @PostConstruct
    public void init() {
        createAnimatedModels();
    }
 
    public void itemSelect(ItemSelectEvent event) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Item selected",
                "Item Index: " + event.getItemIndex() + ", Series Index:" + event.getSeriesIndex());
 
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
 
    public LineChartModel getAnimatedModel1() {
        return animatedModel1;
    }
 
    public BarChartModel getAnimatedModel2() {
        return animatedModel2;
    }
 
    public BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        ChartSeries fgs = new ChartSeries();
        fgs.setLabel("FG");
        fgs.set("2004", 12);
        fgs.set("2005", 10);
        fgs.set("2006", 4);
        fgs.set("2007", 15);
        fgs.set("2008", 2.5);
 
        ChartSeries fgsa = new ChartSeries();
        fgsa.setLabel("FGA");
        fgsa.set("2004", 5);
        fgsa.set("2005", 6);
        fgsa.set("2006", 11);
        fgsa.set("2007", 13.5);
        fgsa.set("2008", 12);
 
        model.addSeries(fgs);
        model.addSeries(fgsa);
        
        model.getAxes().put(AxisType.X, new CategoryAxis("Date"));
 
        return model;
    }
 
    public LineChartModel initLinearModel() {
        LineChartModel model = new LineChartModel();
 
        LineChartSeries homepts = new LineChartSeries();
        homepts.setLabel("Home Games");
 
        homepts.set(1, 2);
        homepts.set(2, 1);
        homepts.set(3, 3);
        homepts.set(4, 6);
        homepts.set(5, 8);
 
        LineChartSeries awaypts = new LineChartSeries();
        awaypts.setLabel("Away Games");
 
        awaypts.set(1, 6);
        awaypts.set(2, 3);
        awaypts.set(3, 2);
        awaypts.set(4, 7);
        awaypts.set(5, 9);
 
        model.addSeries(homepts);
        model.addSeries(awaypts);
        
        model.getAxes().put(AxisType.X, new CategoryAxis("Date"));
 
        return model;
    }
 
    public void createAnimatedModels() {
        int maxScore = 10;
        int maxFGA = 15;
        animatedModel1 = initLinearModel();
        animatedModel1.setTitle("Points Scored");
        animatedModel1.setAnimate(true);
        animatedModel1.setLegendPosition("se");
        Axis yAxis = animatedModel1.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(maxScore + maxScore/5);
 
        animatedModel2 = initBarModel();
        animatedModel2.setTitle("Field Goals");
        animatedModel2.setAnimate(true);
        animatedModel2.setLegendPosition("ne");
        yAxis = animatedModel2.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(maxFGA + maxFGA/5);
    }
 
    public void createDateModel() {
        dateModel = new LineChartModel();
        LineChartSeries series1 = new LineChartSeries();
        series1.setLabel("Series");
 
        series1.set("2014-01-01", 51);
        series1.set("2014-01-06", 22);
        series1.set("2014-01-12", 65);
        series1.set("2014-01-18", 74);
        series1.set("2014-01-24", 24);
        series1.set("2014-01-30", 51);
 
        LineChartSeries series2 = new LineChartSeries();
        series2.setLabel("Series 2");
 
        series2.set("2014-01-01", 32);
        series2.set("2014-01-06", 73);
        series2.set("2014-01-12", 24);
        series2.set("2014-01-18", 12);
        series2.set("2014-01-24", 74);
        series2.set("2014-01-30", 62);
 
        dateModel.addSeries(series1);
        dateModel.addSeries(series2);
 
        dateModel.setTitle("Zoom for Details");
        dateModel.setZoom(true);
        dateModel.getAxis(AxisType.Y).setLabel("Values");
        DateAxis axis = new DateAxis("Dates");
        axis.setTickAngle(-50);
        axis.setMax("2014-02-01");
        axis.setTickFormat("%b %#d, %y");
 
        dateModel.getAxes().put(AxisType.X, axis);
    }
}
