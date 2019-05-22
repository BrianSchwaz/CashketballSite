/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.event.ItemSelectEvent;
import org.primefaces.model.chart.Axis;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.CategoryAxis;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;
import org.primefaces.model.chart.LineChartSeries;

/**
 *
 * @author bkschwar
 */
@ManagedBean
public class ChartView implements Serializable {

    private static LineChartModel animatedModel1;
    private static BarChartModel animatedModel2;
    private static List<LineChartModel> gamesModels;
    private static DBConnect dbConnect;
    private static Connection con;
    private static ArrayList<String> game_cols;
    private static HashMap<String, String> col_type;
    private static ArrayList<TableRow> games;
    private static ArrayList<TableRow> opponentGames;
    private static ArrayList<String> predictFields;

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
        try {
            dbConnect = DBConnect.getInstance();
            con = dbConnect.getConnection();
            gameColInfo();
            String[] avgFields = {"fg","fga","fg_pct","fg3","fg3a","fg3_pct","ft","fta","ft_pct","orb","drb","trb","ast","stl","blk","tov","pf","pts","plus_minus","game_result"};
            predictFields = new ArrayList<String>(Arrays.asList(avgFields));
        } catch (SQLException ex) {
            Logger.getLogger(ChartView.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void generateCharts(Integer pid) throws SQLException {
        createAnimatedModels(pid);
        //System.out.println("Gen Charts for: " + pid);
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
    
    public static BarChartModel initBarModel() {
        BarChartModel model = new BarChartModel();
 
        ChartSeries fgs = new ChartSeries();
        fgs.setLabel("FG");
        fgs.set("2018-19", -1);
 
        ChartSeries fgsa = new ChartSeries();
        fgsa.setLabel("FGA");
        fgsa.set("2018-19", -1);
 
        model.addSeries(fgs);
        model.addSeries(fgsa);
        
        model.getAxes().put(AxisType.X, new CategoryAxis("Date"));
 
        return model;
    }
 
    public static BarChartModel initBarModel(Integer pid,Integer limit) {
        BarChartModel model = new BarChartModel();
 
        ChartSeries fgs = new ChartSeries();
        fgs.setLabel("FG");
 
        ChartSeries fgsa = new ChartSeries();
        fgsa.setLabel("FGA");
        for(int i = games.size()-1;i >= 0 && i>=games.size()- limit;i--)
        {
            fgs.set((String)games.get(i).getField("date_game"),(Integer)games.get(i).getField("fg"));
            fgsa.set((String)games.get(i).getField("date_game"),(Integer)games.get(i).getField("fga"));
        }
 
        model.addSeries(fgs);
        model.addSeries(fgsa);
        
        CategoryAxis axis = new CategoryAxis("Dates");
        axis.setTickAngle(-50);
        model.getAxes().put(AxisType.X, axis);
 
        return model;
    }
    
    public static LineChartModel initLinearModel() {
        LineChartModel model = new LineChartModel();
 
        LineChartSeries homepts = new LineChartSeries();
        homepts.setLabel("Home Games");
        homepts.set("2018-19", -1);
 
        LineChartSeries awaypts = new LineChartSeries();
        awaypts.setLabel("Away Games");
        awaypts.set("2018-19", -1);
 
        model.addSeries(homepts);
        model.addSeries(awaypts);
        
        model.getAxes().put(AxisType.X, new CategoryAxis("Date"));
 
        return model;
    }

 
    public static LineChartModel initLinearModel(Integer pid) {
        LineChartModel model = new LineChartModel();
 
        LineChartSeries homepts = new LineChartSeries();
        homepts.setLabel("Home Games");
        LineChartSeries awaypts = new LineChartSeries();
        awaypts.setLabel("Away Games");
        
        for(int i = games.size()-1;i >= 0;i--)
        {
            if(games.get(i).getField("game_location").equals("@"))
            {
                awaypts.set((String)games.get(i).getField("date_game"),(Integer)games.get(i).getField("pts"));
            }
            else
            {
                homepts.set((String)games.get(i).getField("date_game"),(Integer)games.get(i).getField("pts"));
            }
            ////System.out.println((Integer)games.get(i).getField("pts"));
        }
        
//        homepts.set("2018-10-01", 2);
//        homepts.set("2018-10-03", 1);
//        homepts.set("2018-10-05", 3);
//        homepts.set("2018-10-07", 6);g
//        homepts.set("2018-10-09", 8);
 
        model.addSeries(homepts);
        model.addSeries(awaypts);
        
        DateAxis axis = new DateAxis("Dates");
        axis.setTickAngle(-50);
        axis.setTickFormat("%b %#d, %y");
 
        model.getAxes().put(AxisType.X, axis);
 
        return model;
    }
    
    public void gameColInfo() throws SQLException{
        game_cols = new ArrayList<String>();
        col_type = new HashMap<String,String>();
        
        PreparedStatement ps = con.prepareStatement(Queries.getCols("Game"));

        //get Player data from database
        ResultSet result = ps.executeQuery();

        while (result.next()) {
            String col_name = result.getString("column_name");
            String data_type = result.getString("data_type");
            game_cols.add(col_name);
            col_type.put(col_name,data_type);
        }
    }
    
        
    public static void predicted(String opponent, Integer pid, Integer pastGames) throws SQLException{
        System.out.println(Queries.pastGamesAvg(pid,opponent,pastGames));
        PreparedStatement ps = con.prepareStatement(Queries.pastGamesAvg(pid,opponent,pastGames));
        ResultSet result = ps.executeQuery();
        while (result.next()) {
            TableRow game = new TableRow();
            for(String col_name: game_cols)
            {                      
                if(col_type.get(col_name).equals("text"))
                {
                    game.setField(col_name, (Object)result.getString(col_name));
                }
                else if(col_type.get(col_name).equals("integer"))
                {
                    System.out.println((Object)result.getFloat(col_name));
                    game.setField(col_name, (Object)result.getFloat(col_name));
                }
                else if(col_type.get(col_name).equals("real"))
                {
                    System.out.println((Object)result.getFloat(col_name));
                    game.setField(col_name, (Object)result.getFloat(col_name));
                }
                
            }
            //store all data into a List
            opponentGames.add(game);
        }
        ps.close();
    }
    
    private static void getGames(Integer pid,String startDate,Integer limit) throws SQLException{
        PreparedStatement ps = con.prepareStatement(Queries.gamesQuery(pid, startDate,limit));
        ResultSet result = ps.executeQuery();
        
        while (result.next()) {
            TableRow game = new TableRow();
            for(String col_name: game_cols)
            {                      
                if(col_type.get(col_name).equals("text"))
                {
                    game.setField(col_name, (Object)result.getString(col_name));
                }
                else if(col_type.get(col_name).equals("integer"))
                {
                    game.setField(col_name, (Object)result.getInt(col_name));
                }
                else if(col_type.get(col_name).equals("real"))
                {
                    game.setField(col_name, (Object)result.getFloat(col_name));
                }
            }
            //store all data into a List
            games.add(game);
        }
        ps.close();
    }
    
    private static Object getAgg(Integer pid,String startDate,String agg,String col_name,Integer limit) throws SQLException{
        PreparedStatement ps = con.prepareStatement(Queries.gamesVal(pid, startDate,agg,col_name,limit));
        ResultSet result = ps.executeQuery();
        result.next();
        if(col_type.get(col_name).equals("text"))
        {
            return (Object)result.getString(col_name);
        }
        else if(col_type.get(col_name).equals("integer"))
        {
            return (Object)result.getInt(col_name);
        }
        else
        {
            return (Object)result.getFloat(col_name);
        }
    }
    
    private static void createAnimatedModels(Integer pid) throws SQLException {
        games = new ArrayList<TableRow>();
        int limit = 10;
        int barLimit = 5;
        String startDate = "2018-10-01";
        Integer maxScore = (Integer)getAgg(pid,startDate,"MAX","pts",limit);
        Integer maxFGA = (Integer)getAgg(pid,startDate,"MAX","fga",limit);
        getGames(pid,startDate,limit);
        
        animatedModel1 = initLinearModel(pid);
        animatedModel1.setTitle("Points Scored");
        animatedModel1.setAnimate(true);
        animatedModel1.setLegendPosition("se");
        Axis yAxis = animatedModel1.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(maxScore + maxScore/5 + 1);
 
        animatedModel2 = initBarModel(pid,barLimit);
        animatedModel2.setTitle("Field Goals");
        animatedModel2.setAnimate(true);
        animatedModel2.setLegendPosition("ne");
        yAxis = animatedModel2.getAxis(AxisType.Y);
        yAxis.setMin(0);
        yAxis.setMax(maxFGA + maxFGA/5 + 1);
    }
 
    public void createAnimatedModels() {
        int maxScore = 10, maxFGA = 10;
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

    public List<TableRow> getOpponentGames() {
        return opponentGames;
    }

    public void setOpponentGames(ArrayList<TableRow> opponentGames) {
        ChartView.opponentGames = opponentGames;
    }

    public ArrayList<String> getGame_cols() {
        return game_cols;
    }

    public void setGame_cols(ArrayList<String> game_cols) {
        ChartView.game_cols = game_cols;
    }

    public static ArrayList<String> getPredictFields() {
        return predictFields;
    }

    public static void setPredictFields(ArrayList<String> predictFields) {
        ChartView.predictFields = predictFields;
    }
    
    
}
