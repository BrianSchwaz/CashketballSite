/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package items;

import static items.Queries.getAvgFields;
import java.io.Serializable;
import static java.rmi.server.LogStream.log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.el.ELContext;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;

/**
 *
 * @author bkschwar
 */
@ManagedBean
@Named(value = "playerListBean")
@SessionScoped
public class PlayerListBean implements Serializable{
    
    @ManagedProperty(value="#{login}")
    private ELContext elContext = FacesContext.getCurrentInstance().getELContext();
    private Login login = (Login) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "login");
    private HashMap<Integer,TableRow> players = new HashMap<>();
    private HashMap<Integer,TableRow> allPlayers = new HashMap<>();
    private HashMap<Integer,TableRow> filteredPlayers = new HashMap<>();
    private HashMap<Integer,TableRow> roster = new HashMap<>();
    private HashMap<Integer,TableRow> opRoster = new HashMap<>();
    private HashMap<String,String> showingFields = new HashMap<>();
    private List<TableRow> displayedPlayers = new ArrayList<>();
    private List<TableRow> displayedRoster = new ArrayList<>();
    private List<TableRow> displayedOpposing = new ArrayList<>();
    private TableRow selected = null;
    private Integer offset = 0;
    private Integer rows = 10;
    private DBConnect dbConnect = DBConnect.getInstance();
    private Connection con;
    private String input = "";
    private String[] imp = {"name","team_id","mp_per_g","fg_per_g","fg_pct","fg3_per_g","fg3_pct","efg_pct","ft_per_g","ft_pct","orb_per_g","trb_per_g","ast_per_g","stl_per_g","blk_per_g","tov_per_g","pts_per_g"};
    private String[] compFields = {"fg_per_g","fg_pct","fg3_per_g","fg3_pct","efg_pct","ft_per_g","ft_pct","orb_per_g","trb_per_g","ast_per_g","stl_per_g","blk_per_g","tov_per_g","pts_per_g"};
    private ArrayList<String> important = new ArrayList<String>(Arrays.asList(imp));
    private ArrayList<String> selectedImp = new ArrayList<String>(Arrays.asList(imp));
    private String team = "Roster";
    private String currentSeason = "2018-19";
    private String previousSearch = "";
    private Integer previousOffset = 0;
    private HashMap<String,String> field_list;
    private ArrayList<String> col_names;
    private Boolean renderCharts;
    private ArrayList<Tuple> pidDist;
    
    public Login getLogin(){
        return login;
    }
    
    public void setLogin(Login login) {
        this.login = login;
    }
    
    public HashMap<String, String> getShowingFields() {
        return showingFields;
    }
    
    public TableRow getSelected() {
        return selected;
    }

    public void setSelected(TableRow selected) {
        this.selected = selected;
    }

    public void setShowingFields(HashMap<String, String> showingFields) {
        this.showingFields = showingFields;
    }
    
    public ArrayList<String> getSelectedImp() {
        selectedImp = new ArrayList<String>();
        for(int i = 0; i < important.size(); i++){
            String field = important.get(i);
            if(showingFields.containsKey(field)&&Boolean.parseBoolean(showingFields.get(field)))
            {
                selectedImp.add(field);
            }
        }
        return selectedImp;
    }

    public void setSelectedImp(ArrayList<String> selectedImp) {
        this.selectedImp = selectedImp;
    }
    
    public ArrayList<String> getImportant() {
        return important;
    }

    public void setImportant(ArrayList<String> important) {
        this.important = important;
    }
    
    public void setTeam(String team) throws SQLException {
        this.team = team;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }
    
    public String getTeam() {
        return team;
    }

    public List<TableRow> getRoster() {
        return new ArrayList<TableRow>(roster.values());
    }
   
    public void setRoster(HashMap<Integer, TableRow> roster) {
        this.roster = roster;
    }

    public List<TableRow> getOpRoster() {
        return new ArrayList<TableRow>(opRoster.values());
        
    }

    public void setOpRoster(HashMap<Integer, TableRow> opRoster) {
        this.opRoster = opRoster;
    }

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }
    
    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
    
    public HashMap<Integer, TableRow> getFilteredPlayers() {
        return filteredPlayers;
    }

    public void setFilteredPlayers(HashMap<Integer, TableRow> filteredPlayers) {
        this.filteredPlayers = filteredPlayers;
    }
    
    public Boolean getRenderCharts() {
        return renderCharts;
    }

    public void setRenderCharts(Boolean renderCharts) {
        this.renderCharts = renderCharts;
    }
    
    /**
     * Creates a new instance of PlayerListBean
     */
    
    public void fillMap(HashMap<Integer,TableRow> table,ResultSet result) throws SQLException
    {
        while (result.next()) {
            if(!table.containsKey(result.getInt("pid")))
            {
                TableRow recent = new TableRow();
                for(String col_name: col_names)
                {                      
                    if(field_list.get(col_name).equals("text"))
                    {
                        recent.setField(col_name, (Object)result.getString(col_name));
                    }
                    else if(field_list.get(col_name).equals("integer"))
                    {
                        recent.setField(col_name, (Object)result.getInt(col_name));
                    }
                    else if(field_list.get(col_name).equals("real"))
                    {
                        recent.setField(col_name, (Object)result.getFloat(col_name));
                    }
                }
                //store all data into a List
                table.put(result.getInt("pid"),recent);
            }
        }
    }
    
    public void updateDisplayedPlayers(){
        displayedPlayers = new ArrayList<TableRow>(players.values());
    }
    
    public void updateDisplayedRoster(){
        displayedRoster = new ArrayList<TableRow>(roster.values());
    }
    
    public void updateDisplayedOpposing(){
        displayedOpposing = new ArrayList<TableRow>(opRoster.values());
    }
    
    public List<TableRow> getDisplayedPlayers() {
        return displayedPlayers;
    }

    public void setDisplayedPlayers(List<TableRow> displayedPlayers) {
        this.displayedPlayers = displayedPlayers;
    }
    
    public List<TableRow> getDisplayedRoster() {
        return displayedRoster;
    }

    public void setDisplayedRoster(List<TableRow> displayedRoster) {
        this.displayedRoster = displayedRoster;
    }

    public List<TableRow> getDisplayedOpposing() {
        return displayedOpposing;
    }

    public void setDisplayedOpposing(List<TableRow> displayedOpposing) {
        this.displayedOpposing = displayedOpposing;
    }
    
    public String getDisplayable(String s){
        return s.replace("_per_g", "").replace("_pct", "%").replace("_id","").toUpperCase();
    }
    
    public void getColInfo() throws SQLException{
        col_names = new ArrayList<String>();
        field_list = new HashMap<String,String>();
        
        PreparedStatement ps = con.prepareStatement(Queries.getCols("PerGame"));

        //get Player data from database
        ResultSet result = ps.executeQuery();

        while (result.next()) {
            String col_name = result.getString("column_name");
            String data_type = result.getString("data_type");
            col_names.add(col_name);
            field_list.put(col_name,data_type);
        }
    }
    
    public void updatePlayers() throws SQLException{
        players = new HashMap<>();
        PreparedStatement ps = con.prepareStatement(
                        Queries.constructPlayerQuery(Queries.pgsString(col_names), currentSeason, rows, offset, previousSearch, team, true, curLogin()));
        
        ResultSet result = ps.executeQuery();
        
        fillMap(players,result);
                
        result.close();
        
        updateDisplayedPlayers();
    }
    
    public void updateRoster() throws SQLException{
        if(team.equals("Roster"))
        {
            roster = new HashMap<>();
        }
        else
        {
            opRoster = new HashMap<>();
        }
        PreparedStatement ps = con.prepareStatement(
                Queries.constructPlayerQuery(Queries.pgsString(col_names), currentSeason, rows, offset, previousSearch, team, false, curLogin()));
        
        ResultSet result = ps.executeQuery();
        
        fillMap(team.equals("Roster")?roster:opRoster,result);
        if(team.equals("Roster")){updateDisplayedRoster();}
        else{updateDisplayedOpposing();}
        result.close();
    }
    
    public String curLogin(){
        return getLogin().getLogin();
    }
    
    public PlayerListBean() throws SQLException {
        System.out.println("Cur Login: " + curLogin());
        input = "";
        renderCharts = false;
        String[] startingFields = {"name","team_id","mp_per_g","trb_per_g","ast_per_g","stl_per_g","blk_per_g","tov_per_g","pts_per_g"};
        
        for(int i=0;i<startingFields.length;i++){
            showingFields.put(startingFields[i],"true");
        }
        for(int i=0;i<important.size();i++){
            if(!showingFields.containsKey(important.get(i)))
            {
                showingFields.put(important.get(i),"false");
            }
        }
        con = dbConnect.getConnection();
        
        getColInfo();
        
        team = "Opposing_Roster";
        updateRoster();
        team = "Roster";
        updateRoster();
        updatePlayers();
        
        PreparedStatement ps = con.prepareStatement(
                Queries.allPlayers(Queries.pgsString(col_names), currentSeason));
        
        ResultSet result = ps.executeQuery();
        
        fillMap(allPlayers,result);
        normalizeAllPlayers();
    }
    
    public void normalizeAllPlayers(){
        for(String field : compFields){
            Double sumToAvg = 0.0;
            Double varSumToSD = 0.0;
            Double sd;
            for(TableRow player : allPlayers.values()){
                sumToAvg += (Float)player.getField(field);
            }
            sumToAvg = sumToAvg / allPlayers.size();
            for(TableRow player : allPlayers.values()){
                varSumToSD += Math.pow((Float)player.getField(field) - sumToAvg, 2);
            }
            varSumToSD = Math.sqrt(varSumToSD / allPlayers.size());
            for(TableRow player : allPlayers.values()){
                player.setField(field, ((Float)player.getField(field) - sumToAvg) / varSumToSD);
                
            }
        }
    }
    
    public void search() throws SQLException
    {
        System.out.print("updating");
        if(previousSearch.equals(input) && previousOffset == offset){
            return;
        }
        previousSearch = input;
        previousOffset = offset;
        players = new HashMap<Integer,TableRow>();
        PreparedStatement ps = con.prepareStatement(
                        Queries.constructPlayerQuery(Queries.pgsString(col_names), currentSeason, rows, offset, previousSearch, team, true, curLogin()));
        //get Player data from database
        ResultSet result = ps.executeQuery();
        fillMap(players,result);
        updateDisplayedPlayers();
        result.close();
    }
    
    public List<TableRow> getPlayers()
    {
        List<TableRow> r = new ArrayList<TableRow>(players.values());
        return r;
    }
    
    public void enactSearch() throws SQLException{
        offset = 0;
        search();
    }
    
    public String logout(){    
        System.out.print("logout");
        System.out.println("Logging out: " + curLogin());
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "back";
    }
    
    public void change(){
        System.out.print("change");
    }
    
    public void remove(String teamTable) throws SQLException
    {
        System.out.println(team);
        setTeam(teamTable);
        Integer pid = (Integer)selected.getField("pid");
        PreparedStatement ps
            = con.prepareStatement("DELETE FROM \"" + teamTable + "\" WHERE login = '" + curLogin() + "' AND pid = " + pid + ";");
        ps.executeUpdate();
        ps.close();
        (teamTable.equals("Roster")?roster:opRoster).remove(pid);
        if(teamTable.equals("Roster")){updateDisplayedRoster();}
        else{updateDisplayedOpposing();}
        updatePlayers();
    }
    
    public void next() throws SQLException
    {
        offset += rows;
        search();
    }
    
    public void prev() throws SQLException
    {
        offset -= rows;
        search();
    }
    
    public void add() throws SQLException
    {
        Integer pid;
        if(selected == null){return;}
        pid = (Integer)selected.getField("pid");
        System.out.println(selected.getField("name"));
        if(!(team.equals("Roster")?roster:opRoster).containsKey(pid))
        {
            PreparedStatement ps
                    = con.prepareStatement(
                        "INSERT INTO \"" + team + "\" VALUES(" + "'" + curLogin() + "'" + "," + pid + ")");
            //get Player data from database
            ps.executeUpdate();
            //con.commit();
            ps.close();
            (team.equals("Roster")?roster:opRoster).put(pid, selected);
            if(team.equals("Roster")){updateDisplayedRoster();}
            else{updateDisplayedOpposing();}
            updatePlayers();
        }
    }
    
    public void onRowSelect(SelectEvent event) throws SQLException {
        String opponent = "LAL";
        Integer pastGames = 5;
        //System.out.println(((TableRow) selected).getField("name"));
        Integer pid = (Integer)((TableRow) selected).getField("pid");
        renderCharts = true;
        ChartView.generateCharts(pid);
        nearest(5,pid);
        ChartView.predicted(opponent,pid,pastGames);
    }
 
    public void onRowUnselect(UnselectEvent event) {
        FacesMessage msg = new FacesMessage("Unselected", (String)(((TableRow) event.getObject()).getField("pid")));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        renderCharts=false;
    }
    
   public void nearest(int nearestNum,int compPid) throws SQLException{
       pidDist = new ArrayList<Tuple>();
       TableRow compPlayer = allPlayers.get(compPid);
       for(TableRow player : allPlayers.values()){
            if((Integer)player.getField("pid") != compPid){
                Tuple tuple = new Tuple((Integer)player.getField("pid"),(String)player.getField("name"),getDistance(player,compPlayer));
                pidDist.add(tuple);
            }
           
       }
       Collections.sort(pidDist);
       pidDist.forEach(tuple -> System.out.println(tuple.getName()));
       pidDist = new ArrayList<Tuple>(pidDist.subList(0, nearestNum));
   }
   
   public Double getDistance(TableRow p1, TableRow p2){
       Double dist = new Double(0);
       System.out.println(p1.getField("name"));
       for(String field : compFields){
           if(field_list.get(field).equals("int")){
               dist += Math.pow(Math.abs((Integer)p1.getField(field) - (Integer)p2.getField(field)),2);
           }
           else if(field_list.get(field).equals("real")){
               dist += Math.pow(Math.abs((Double)p1.getField(field) - (Double)p2.getField(field)),2);
           }
       }
       return Math.round(Math.sqrt(dist) * 10000.0) / 10000.0;
   }

    public ArrayList<Tuple> getPidDist() {
        return pidDist;
    }

    public void setPidDist(ArrayList<Tuple> pidDist) {
        this.pidDist = pidDist;
    }

    public List<TableRow> getAllPlayers() {
        List<TableRow> ap = new ArrayList<TableRow>(allPlayers.values());
        return ap;
    }

    public void setAllPlayers(HashMap<Integer, TableRow> allPlayers) {
        this.allPlayers = allPlayers;
    }
    
}
